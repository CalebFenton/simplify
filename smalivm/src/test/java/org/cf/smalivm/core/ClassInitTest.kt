package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The class-initialization lifecycle (Plan 9 Task 16): `Uninitialized -> Initializing -> {Initialized |
 * Failed}` (design decision 2), with a guest `<clinit>` run as a **nested call** through the same
 * `Invoker`/`ApplicationBytecodeBackend` seam an ordinary call uses (design decision 1). Discharges
 * `CARRY-FORWARD.md` Plan 9 item 9, the `isClassInitialized` honest stub.
 *
 * **The bug classes this file exists for, all of them silent wrong answers:**
 *
 *  1. **A session-global lifecycle**, which reproduces `docs/architecture/state-model.md:160`'s existential
 *     merge leak exactly: the old engine's class map *"records presence, not a lifecycle"* and marks the
 *     caller's class initialized if it was initialized in *any* terminal callee state, so *"the resulting
 *     state can say 'initialized' while holding unknown fields because initialization occurred on only
 *     some paths; future access then skips initialization on paths where it never happened."* This is the
 *     fourth soundness item of the same class as Plan 9's colliding addresses, node piles and heap ids
 *     (resolved decision 9), and it is graded the same way.
 *  2. **A `<clinit>` that re-runs.** On the success path that applies its writes twice; from inside itself
 *     it is unbounded recursion, reported as `Partial(CALL_DEPTH)` in place of a correct answer.
 *  3. **A failed initializer that reads back as initialized** -- spec §7's `Failed` state, and
 *     `state-model.md:160` again: *"A failed initializer can therefore appear successfully initialized,
 *     potentially with partial or skipped field state."*
 *  4. **Subclass before superclass**, which is adjudication 4 and a *wrong constant* rather than an
 *     imprecision: the old engine recurses over ancestors first (`VirtualMachine.kt:457-491`, the intent
 *     plainly correct) and a LIFO deque then reverses it (`:145`), so a subclass `<clinit>` reads a static
 *     its superclass has not written yet.
 *  5. **The framework `<clinit>` dragged into virtual execution.** The framework smali is 21,088 ordinary
 *     loaded classes, so `new-instance Ljava/lang/Integer;` -- which is in Task 17's own corpus -- would
 *     run the real `Integer.<clinit>` (resolved decision 7).
 *
 * **Why the triggers arrive through the `opFactory` seam.** The ops that trigger initialization in real
 * bytecode are `new-instance` (Task 17), `sget`/`sput` (Task 18) and every `invoke` (Task 20), and none of
 * them decodes yet. So the fixtures hold REAL instructions -- so widths and addresses survive those tasks
 * unchanged -- and the tests substitute an op at the instruction's own address, exactly as `InvokerTest`
 * does for `Transition.Invoke` and for the same reason.
 */
class ClassInitTest {
    private val classIndex = CoreTester.classIndex
    private val int = VirtualType.of("I")

    @Test
    fun aClassWithNoDeclaredClinitSettlesWithNothingToRun() {
        val plan = defaultInvoker(classIndex).planClassInit(Frame.entryFrame(viewOf(TRIGGER, "triggersOnce()I")), NO_CLINIT)
        assertNull(plan.context, "there is no guest <clinit> to run (design decision 1's common case)")
        assertEquals(
            listOf(VirtualType.OBJECT.descriptor, NO_CLINIT.descriptor), plan.settled.map { it.descriptor },
            "the whole chain settles, most ancestral first",
        )
        assertFalse(
            plan.skippedAnInitializer,
            "Ljava/lang/Object; declares no <clinit>, so NOTHING was skipped -- and it is on every app " +
                "class's chain, so a STRONG here would make every class-init trigger in the system STRONG",
        )
    }

    @Test
    fun aStaticInitializerIsNeverInheritedFromAnAncestor() {
        // `ClassIndex.lookupMethod` routes through `VirtualClass.getMethod`, which falls back to every
        // ancestor (`type/VirtualClass.java:112-126`) -- so asking IT for `<clinit>()V` on a class that
        // declares none hands back the SUPERCLASS's initializer, and the scheduler would run the
        // superclass's `<clinit>` believing it had run the subclass's, marking the subclass initialized
        // while its own statics were never touched. That is the oracle defect `execution-engine.md:159`
        // records in as many words, and `lookupClassInitializer` exists to not have it.
        //
        // `Lclass_init/heir;` declares no `<clinit>` and extends one that does, which is the only shape in
        // which the two lookups can disagree at all.
        val inherited = classIndex.lookupMethod(HEIR, ClassIndex.CLASS_INITIALIZER_DESCRIPTOR)
        assertTrue(
            inherited is MethodLookup.Runnable &&
                inherited.view.signature == "${GRANDPARENT.descriptor}-><clinit>()V",
            "the ancestor-searching lookup hands back the SUPERCLASS's initializer: $inherited",
        )
        assertEquals(
            MethodLookup.NoSuchMethod, classIndex.lookupClassInitializer(HEIR),
            "...and the declared-only lookup correctly finds nothing",
        )
        assertFalse(classIndex.hasClassInitializer(HEIR))
        assertTrue(classIndex.hasClassInitializer(WITNESS), "a class that DOES declare one is still found")
        // ...and it matters end to end: initializing `heir` must run GRANDPARENT's initializer as the
        // ancestor's, then settle `heir` itself with nothing to run -- not run grandparent's twice, and not
        // mark `heir` initialized on the strength of an inherited method.
        val frame = Frame.entryFrame(viewOf(TRIGGER, "triggersOnce()I"))
        val first = defaultInvoker(classIndex).planClassInit(frame, HEIR)
        assertEquals(GRANDPARENT.descriptor, first.context?.activeInitializer?.type?.descriptor)
        frame.completeClassInit(GRANDPARENT)
        val second = defaultInvoker(classIndex).planClassInit(frame, HEIR)
        assertNull(second.context, "heir declares no initializer of its own")
        assertEquals(
            listOf(VirtualType.OBJECT.descriptor, HEIR.descriptor), second.settled.map { it.descriptor },
            "grandparent is already done, so what remains is Object and heir -- both with nothing to run",
        )
    }

    @Test
    fun aFrameworkClassIsSettledWithoutRunningItsInitializerAndDeclaresStrong() {
        // Integer declares <clinit> in the captured framework, but framework initialization is suppressed.
        // NewInstanceOpTest.theFrameworkConservativeInitMarksIntegerWithoutRunningItsClinit checks
        // the same policy through new-instance rather than directly through the planner.
        assertTrue(classIndex.isFrameworkClass(INTEGER), "Integer is framework, and Integer.smali IS loaded")
        assertFalse(classIndex.isFrameworkClass(WITNESS), "a fixture class is an application class")
        val plan = defaultInvoker(classIndex).planClassInit(Frame.entryFrame(viewOf(TRIGGER, "triggersOnce()I")), INTEGER)
        assertNull(plan.context, "a framework <clinit> is NEVER run")
        assertTrue(plan.settled.map { it.descriptor }.contains(INTEGER.descriptor), "but it IS marked initialized")
        assertTrue(
            plan.skippedAnInitializer,
            "an initializer that exists and did not run is unmodelled state, so the trigger is STRONG",
        )
    }

    @Test
    fun theTopmostUninitializedAncestorIsTheOneThatRunsFirst() {
        // Divergence 4 at the planner level: the plan names GRANDPARENT even though CHILD was asked for.
        // One initializer per plan is what makes the order unarrangeable -- see `ClassInitPlan`'s KDoc.
        val plan = defaultInvoker(classIndex).planClassInit(Frame.entryFrame(viewOf(TRIGGER, "triggersOnce()I")), CHILD)
        assertEquals(GRANDPARENT.descriptor, plan.context?.activeInitializer?.type?.descriptor)
        assertEquals(
            "${GRANDPARENT.descriptor}-><clinit>()V", plan.context?.activeInitializer?.view?.signature,
            "and the view is the ancestor's own declared initializer",
        )
    }

    // ------------------------------------------------------------------------------------------
    // Part 3: the lifecycle through a real run.
    // ------------------------------------------------------------------------------------------

    @Test
    fun aClassWithNoGuestClinitBecomesInitializedWithNoNestedRun() {
        val graph = runWithInitTrigger("triggersOnce()I", NO_CLINIT)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph), "nothing may be run for a class with no <clinit>")
        assertEquals(
            ClassInitState.INITIALIZED, initStateAtReturn(graph, NO_CLINIT),
            "it is still INITIALIZED: Uninitialized -> Initialized is a real step, not a shortcut",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.methodSideEffectLevelOverExploredPaths(),
            "nothing was skipped, so nothing may be declared -- a spurious STRONG stops every fold here",
        )
    }

    @Test
    fun aGuestClinitRunsAsANestedCallAndTheTriggerThenProceeds() {
        val graph = runWithInitTrigger("triggersOnce()I", WITNESS)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(WITNESS.descriptor), initializersRun(graph), "exactly one nested <clinit> ran")
        assertEquals(ClassInitState.INITIALIZED, initStateAtReturn(graph, WITNESS))
        // The trigger executes TWICE: initialization is a prelude to the instruction, so the prelude
        // resumes at the trigger's own address and the second pass does the op's real work.
        assertEquals(2, graph.nodePile(0).size, "the trigger re-executes once the prelude returns")
        // ...and the run still finishes normally, which is the half that says the resume actually landed.
        assertEquals(listOf(Outcome.Returned(Value.Known(7, int))), graph.outcomes)
    }

    @Test
    fun aClinitRunsOnTheCallersOwnHeapIdSpaceAndItsAllocationSurvivesTheReturn() {
        // "A nested call through the SAME seam" (design decision 1) is a claim with content: the `<clinit>`
        // gets its frame from `Frame.calleeEntryFrame`, so it shares the caller's heap ID SPACE, and the
        // caller resumes through `Frame.resumedWith`, so what the initializer allocated is still there.
        // A private mini-interpreter, or a fresh `Frame.entryFrame`, would pass every OTHER test in this
        // file and fail this one -- and would give the initializer's first object the same `HeapRef` as the
        // caller's, which heap consensus reads as ONE object.
        val graph = runWithInitTrigger("triggersOnce()I", WITNESS)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val allocation = nodesIn(graph, "${WITNESS.descriptor}-><clinit>()V")
            .single { it.address == NEW_ARRAY_IN_WITNESS_CLINIT }
            .frame.peekRegister(1)
        assertTrue(allocation is Value.Known && allocation.raw is HeapRef, "the <clinit> allocated: $allocation")
        val ref = (allocation as Value.Known).raw as HeapRef
        val resumed = nodesIn(graph, viewOf(TRIGGER, "triggersOnce()I").signature).last { it.outcome != null }
        val array = resumed.frame.heapObject(ref)
        assertTrue(array is HeapObject.TrackedArray, "the initializer's object is on the caller's heap: $array")
        assertEquals(3, (array as HeapObject.TrackedArray).length)
    }

    @Test
    fun aSecondTriggerOfAnInitializedClassDoesNotRerunItsClinit() {
        val graph = runWithInitTrigger("triggersTwice()I", WITNESS)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(WITNESS.descriptor), initializersRun(graph),
            "the second trigger finds the class INITIALIZED; re-running applies its writes twice",
        )
    }

    @Test
    fun aClinitThatRetriggersItsOwnClassSeesInitializingAndDoesNotRecurse() {
        // Design decision 2's re-entry guard, and the ONE invariant whose violation is loud rather than
        // silent -- it recurses until `Limits.maxCallDepth` truncates the run. Which is exactly why the
        // assertion is on `Complete` AND on the run count: `Partial(CALL_DEPTH)` is the mutation's
        // signature, and one `<clinit>` activation is the correct answer.
        val graph = runWithInitTrigger("triggersOnce()I", REENTRANT, alsoStubbing = REENTRANT)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "removing the Initializing guard makes this Partial(CALL_DEPTH)",
        )
        assertEquals(listOf(REENTRANT.descriptor), initializersRun(graph), "the initializer runs exactly once")
        // The guard is a STATE the nested call can see, not a stack walk: the `<clinit>`'s own frame must
        // read INITIALIZING, which is only true because `calleeEntryFrame` threads the store inward.
        assertEquals(
            listOf(ClassInitState.INITIALIZING),
            nodesIn(graph, "${REENTRANT.descriptor}-><clinit>()V").map { it.frame.initState(REENTRANT) }.distinct(),
            "every node inside the initializer sees its own class INITIALIZING",
        )
        assertFalse(
            initializerNode(graph, REENTRANT).frame.isClassInitialized(REENTRANT),
            "and INITIALIZING is not `isClassInitialized`: the statics are not written yet",
        )
    }

    @Test
    fun aSuperclassIsInitializedBeforeItsSubclassAndEachClassRunsOnce() {
        // DIVERGENCE 4 (adjudication 4). The oracle answers this in the opposite order and can run a
        // diamond ancestor's `<clinit>` twice; correct ordering is what makes a superclass static readable
        // by a subclass initializer, which is a foldable CONSTANT rather than a nicety.
        val graph = runWithInitTrigger("triggersOnce()I", CHILD)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(GRANDPARENT.descriptor, PARENT.descriptor, CHILD.descriptor), initializersRun(graph),
            "most ancestral first, each exactly once",
        )
        // EVERY class in the chain is initialized on the returning path, including the ones that had nothing
        // to run. `Ljava/lang/Object;` is the load-bearing entry: it is SETTLED, so its mark is applied to the
        // first prelude's own callee frame and comes back only because `Frame.resumedWith` takes the class
        // store from the CALLEE. A `resumedWith` that kept the caller's store instead passes every other
        // assertion in this file -- the ordering, the run count, the completion -- and loses this. Which is
        // the shape of what it will lose in Task 18 for real: every static a `<clinit>` wrote.
        assertEquals(
            listOf(ClassInitState.INITIALIZED),
            listOf(VirtualType.OBJECT, GRANDPARENT, PARENT, CHILD).map { initStateAtReturn(graph, it) }.distinct(),
            "the whole chain, settled classes included, is initialized when the trigger's method returns",
        )
        // FOUR passes over the trigger: one per initializer, plus the pass on which it finally has nothing
        // left to ask for and does its own work.
        assertEquals(4, graph.nodePile(0).size, "one trigger pass per initializer, plus the one that proceeds")
    }

    @Test
    fun oneBranchInitializingDoesNotExemptTheOther() {
        // RESOLVED DECISION 9, and the soundness item this whole file is scoped by. `state-model.md:160`:
        // the old engine marks the caller's class initialized if ANY terminal callee state had it, so a
        // path on which initialization never happened later reads the class as initialized and skips it.
        //
        // Two paths, one trigger reached on only one of them plus a shared trigger reached on both, so the
        // correct answer is TWO initializer runs -- one per path. A session-global store (or any
        // existential fold) gives ONE.
        val graph = runWithInitTrigger(
            "triggersOnOneBranchThenOnBoth(I)I", WITNESS,
            branchStubAddresses = intArrayOf(2, 3),
            seed = mapOf(1 to Value.Unknown(int)),
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(WITNESS.descriptor, WITNESS.descriptor), initializersRun(graph),
            "each PATH initializes for itself; one shared mark is state-model.md:160's leak",
        )
    }

    // ------------------------------------------------------------------------------------------
    // Part 4: the method-entry trigger (`execution-engine.md:151-153`).
    // ------------------------------------------------------------------------------------------

    @Test
    fun aMethodEntryTriggersItsOwnDefiningClassInitBeforeTheCalleeRuns() {
        // The broadest trigger, and the one an earlier draft of the plan under-stated as `invoke-static`
        // only: "Non-`<clinit>` method entry for the method's defining class"
        // (`VirtualMachine.kt:189-209`), so EVERY callee entry counts.
        val graph = runWithInvokes("callsWitnessTouch()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(WITNESS.descriptor), initializersRun(graph))
        // BEFORE the callee runs, and that ordering is the content of the claim: the callee's own entry
        // node must see its class already initialized.
        assertEquals(
            ClassInitState.INITIALIZED,
            nodesIn(graph, "${WITNESS.descriptor}->touch()V").first().frame.initState(WITNESS),
            "the callee's entry frame sees its defining class initialized",
        )
        assertEquals(listOf(Outcome.Returned(Value.Known(7, int))), graph.outcomes)
    }

    @Test
    fun aMethodEntryNeedingNoInitializerCostsNoExtraNode() {
        // The common case has to stay cheap: when a callee's class needs only marks, they are seeded onto
        // the CALLEE's own entry frame and the invoke is not re-executed at all.
        val graph = runWithInvokes("callsNoClinitTouch()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph))
        assertEquals(1, graph.nodePile(0).size, "the invoke runs ONCE when nothing had to be run")
        assertEquals(
            ClassInitState.INITIALIZED,
            nodesIn(graph, "${NO_CLINIT.descriptor}->touch()V").first().frame.initState(NO_CLINIT),
        )
    }

    @Test
    fun aClinitEntryDoesNotTriggerItsOwnClassInit() {
        // The carve-out in the trigger's own wording -- "NON-`<clinit>` method entry". Bytecode may call a
        // `<clinit>` directly and obfuscator output does; without the carve-out that call triggers the
        // class's initialization, which schedules the SAME `<clinit>` as a prelude, and the initializer
        // runs TWICE on one path with its writes applied twice. The `Initializing` guard cannot cover it:
        // at the call site nothing has marked the class yet, so it is still `Uninitialized`.
        val graph = runWithInvokes("callsWitnessClinitDirectly()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            emptyList<String>(), initializersRun(graph),
            "no class-init PRELUDE was scheduled: the call is an ordinary call that happens to be a <clinit>",
        )
        assertEquals(
            1, nodesIn(graph, "${WITNESS.descriptor}-><clinit>()V").count { it.isCallEntry },
            "and the <clinit> body ran exactly once -- as the call, not as a prelude to it",
        )
        assertEquals(
            ClassInitState.UNINITIALIZED, initStateAtReturn(graph, WITNESS),
            "calling a <clinit> by hand does not initialize the class; only a trigger does",
        )
    }

    @Test
    fun aMethodEntryInitializesTheWholeHierarchyTopDown() {
        // Divergence 4 again, reached through `planInvoke` rather than `planInitClass`: two scheduler paths,
        // one planner, and the ordering must not depend on which one asked.
        val graph = runWithInvokes("callsChildTouch()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(GRANDPARENT.descriptor, PARENT.descriptor, CHILD.descriptor), initializersRun(graph),
        )
    }

    // ------------------------------------------------------------------------------------------
    // Part 5: what must NOT initialize.
    // ------------------------------------------------------------------------------------------

    @Test
    fun constClassStillDoesNotInitialize() {
        // Plan 8's choice, and the one half of the trigger question that was `[DEVICE]`-measured before
        // this task: `tools/art-runner/fixtures/Fx_ConstClassDoesNotInitialize.smali` against its control
        // `Fx_StaticFieldDoesInitialize.smali`. Now that class init EXISTS, "does not initialize" is a
        // claim that can regress, so it gets an assertion rather than a comment.
        val graph = CoreTester.execute("Lclass_init/const_class_probe;", "resolvesWitnessClass()Ljava/lang/Class;")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph), "const-class resolves without initializing")
        assertEquals(ClassInitState.UNINITIALIZED, initStateAtReturn(graph, WITNESS))
        assertEquals(
            SideEffectLevel.NONE, graph.methodSideEffectLevelOverExploredPaths(),
            "which is what makes const-class's NONE truthful (oracle Correctness Defect 21 is the opposite)",
        )
    }

    // ------------------------------------------------------------------------------------------
    // Harness
    // ------------------------------------------------------------------------------------------

    private fun viewOf(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    /**
     * Stands in for Task 17's `new-instance` and Task 18's `sget`/`sput`: an op that must have [type]
     * initialized before it does anything, and that does nothing once it is.
     *
     * **It asks for a class-init action first**, which is the real op protocol rather than a shortcut for the
     * test: an op that returned `Transition.InitClass` unconditionally would re-execute forever, which is
     * why `planInitClass` treats a no-op plan as a loud invariant. The old engine has the same two-step,
     * split across `execute`/`resume` (`opcode/SGetOp.kt:23-35`).
     */
    private class InitClassStub(address: Int, private val type: VirtualType) : Op(address) {
        override val name = "init-class-stub"
        override fun execute(frame: Frame): OpExecution =
            frame.classInitTrigger(type) ?: listOf(Transition.Continue).asOpExecution()
    }

    /** Stands in for Task 20's `InvokeOp`; see `InvokerTest.InvokeStub`, of which this is the twin. */
    private class InvokeStub(address: Int, private val invoke: Transition.Invoke) : Op(address) {
        override val name = "invoke-static-stub"
        override val registersRead: Set<Int> get() = invoke.argumentRegisters.toSet()
        override fun execute(frame: Frame): OpExecution = listOf(invoke).asOpExecution()
    }

    /**
     * Runs [callerDescriptor] with an [InitClassStub] for [type] at each of [branchStubAddresses], and --
     * when [alsoStubbing] is given -- another one inside THAT class's own `<clinit>`, which is how a
     * transitive self-trigger is expressed without `sget` or `new-instance`.
     */
    private fun runWithInitTrigger(
        callerDescriptor: String,
        type: VirtualType,
        branchStubAddresses: IntArray = intArrayOf(0),
        alsoStubbing: VirtualType? = null,
        seed: Map<Int, Value> = emptyMap(),
    ): ExecutionGraph {
        val view = viewOf(TRIGGER, callerDescriptor)
        val frame = Frame.entryFrame(view)
        for ((register, value) in seed) frame.assign(register, value)
        val nestedInitializer = alsoStubbing?.let { "${it.descriptor}-><clinit>()V" }
        return ExecutionSession.of(view, classIndex, Limits(), frame) { address, v, index ->
            when {
                // Keyed on the METHOD as well as the address, exactly as `InvokerTest.run` is: a stub site
                // and an initializer body can hold the same address, and replacing both would move the
                // experiment.
                v.signature == view.signature && address in branchStubAddresses ->
                    InitClassStub(address, type)
                v.signature == nestedInitializer && address == 0 ->
                    InitClassStub(address, alsoStubbing!!)
                else -> OpBuilder.build(address, v, index)
            }
        }.run()
    }

    /**
     * Runs [callerDescriptor] with **every** `invoke-static` in **every** method decoded into the
     * [Transition.Invoke] Task 20's real op will return.
     *
     * The decode is a test-side stand-in and is deliberately the minimum: the register list and the
     * proto, off the instruction itself, so a fixture can be edited without a matching edit here. Task 20
     * deletes it along with the stub.
     */
    private fun runWithInvokes(callerDescriptor: String): ExecutionGraph {
        val view = viewOf(TRIGGER, callerDescriptor)
        return ExecutionSession.of(view, classIndex, Limits(), Frame.entryFrame(view)) { address, v, index ->
            if (v.opcodeAt(address) == Opcode.INVOKE_STATIC) {
                InvokeStub(address, invokeTransitionAt(v, address))
            } else {
                OpBuilder.build(address, v, index)
            }
        }.run()
    }

    private fun invokeTransitionAt(view: MethodView, address: Int): Transition.Invoke {
        val instruction = view.instructionAt(address)
        val reference = (instruction as ReferenceInstruction).reference as MethodReference
        val registers = when (instruction) {
            is RegisterRangeInstruction -> IntArray(instruction.registerCount) { instruction.startRegister + it }
            is FiveRegisterInstruction -> intArrayOf(
                instruction.registerC, instruction.registerD, instruction.registerE,
                instruction.registerF, instruction.registerG,
            ).copyOf(instruction.registerCount)
            else -> IntArray(0)
        }
        val proto = "(${reference.parameterTypes.joinToString("")})${reference.returnType}"
        return Transition.Invoke(
            MethodDescriptor(VirtualType.of(reference.definingClass), reference.name, proto),
            registers,
            InvokeKind.STATIC,
        )
    }

    /**
     * Every node the run created, entry activation and callees alike, in walk order.
     *
     * Walked down [ExecutionNode.children] rather than read off [ExecutionGraph], because a graph
     * describes ONE activation by design (`CARRY-FORWARD.md` Plan 9 item 2) and a `<clinit>`'s nodes are
     * therefore invisible to every one of its queries. The child edges are the only route to them, and
     * they are public precisely so an observer can walk the tree.
     */
    private fun allNodes(graph: ExecutionGraph): List<ExecutionNode> {
        val out = mutableListOf<ExecutionNode>()
        fun walk(node: ExecutionNode) {
            out.add(node)
            node.children.forEach(::walk)
        }
        walk(graph.nodePile(0).first())
        return out
    }

    private fun nodesIn(graph: ExecutionGraph, signature: String): List<ExecutionNode> =
        allNodes(graph).filter { it.view.signature == signature }

    /**
     * Every class whose `<clinit>` actually RAN as a class-init prelude, in the order the runs started.
     *
     * Counted by [Activation], deduplicated by IDENTITY (which is [Activation]'s whole contract -- two
     * calls of one method are two activations), so this is "how many times did the initializer run" and
     * not "how many nodes did it execute". An activation created by an ordinary `invoke` of a `<clinit>`
     * has a null [Activation.classInitContext] and is correctly not counted: that is a call, not a
     * prelude, and telling them apart is what the carve-out test asserts.
     */
    private fun initializersRun(graph: ExecutionGraph): List<String> {
        val seen = mutableListOf<Activation>()
        for (node in allNodes(graph)) if (seen.none { it === node.activation }) seen.add(node.activation)
        return seen.mapNotNull { it.classInitContext?.activeInitializer?.type?.descriptor }
    }

    private fun initializerNode(graph: ExecutionGraph, type: VirtualType): ExecutionNode =
        nodesIn(graph, "${type.descriptor}-><clinit>()V").first { it.isCallEntry }

    /**
     * [type]'s lifecycle state on the entry method's returning path -- i.e. what the run's own method
     * ended up believing.
     */
    private fun initStateAtReturn(graph: ExecutionGraph, type: VirtualType): ClassInitState {
        val returning = allNodes(graph).filter { it.outcome is Outcome.Returned && it.activation.caller == null }
        assertTrue(returning.isNotEmpty(), "no returning node in the entry activation")
        return returning.map { it.frame.initState(type) }.distinct().single()
    }

    private companion object {
        /**
         * DESCRIPTOR form, not the source form: `VirtualType.of` accepts only descriptors, and every other
         * test in this package spells types `Lfoo;`.
         */
        const val TRIGGER = "Lclass_init/trigger;"

        /** @1 in `witness.<clinit>()V`: the `new-array`, whose result lands in v1. */
        const val NEW_ARRAY_IN_WITNESS_CLINIT = 1

        val NO_CLINIT = VirtualType.of("Lclass_init/no_clinit;")
        val HEIR = VirtualType.of("Lclass_init/heir;")
        val WITNESS = VirtualType.of("Lclass_init/witness;")
        val REENTRANT = VirtualType.of("Lclass_init/reentrant;")
        val GRANDPARENT = VirtualType.of("Lclass_init/grandparent;")
        val PARENT = VirtualType.of("Lclass_init/parent;")
        val CHILD = VirtualType.of("Lclass_init/child;")

        val INTEGER = VirtualType.of("Ljava/lang/Integer;")
    }
}
