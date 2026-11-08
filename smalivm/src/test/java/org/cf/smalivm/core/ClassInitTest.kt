package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The class-initialization lifecycle `Uninitialized -> Initializing -> {Initialized | Failed}`, with a guest
 * `<clinit>` run as a nested call through the same `Invoker` an ordinary call uses.
 *
 * The bug classes, all of them silent wrong answers:
 *
 *  1. **A session-global lifecycle.** A class initialized on some paths would read as initialized on every
 *     path, so a path on which initialization never happened skips it.
 *  2. **A `<clinit>` that re-runs.** On the success path it applies its writes twice; from inside itself it
 *     recurses until the call-depth limit truncates the run.
 *  3. **A failed initializer that reads back as initialized.**
 *  4. **Subclass before superclass**, so a subclass `<clinit>` reads a static its superclass has not written.
 *  5. **A framework `<clinit>` dragged into virtual execution.**
 */
class ClassInitTest {
    private val classIndex = CoreTester.classIndex
    private val int = VirtualType.of("I")

    @Test
    fun aClassWithNoDeclaredClinitSettlesWithNothingToRun() {
        val plan = defaultInvoker(classIndex).planClassInit(plannerFrame(), NO_CLINIT)
        assertNull(plan.context, "there is no guest <clinit> to run")
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
        // `ClassIndex.lookupMethod` falls back to every ancestor, so asking it for `<clinit>()V` on a class that
        // declares none hands back the superclass's initializer. Running that would mark the subclass initialized
        // while its own statics were never touched; `lookupClassInitializer` finds declared initializers only.
        //
        // `Lclass_init/heir;` declares no `<clinit>` and extends one that does, the only shape in which the two
        // lookups can disagree.
        val inherited = classIndex.lookupMethod(HEIR, ClassIndex.CLASS_INITIALIZER_DESCRIPTOR)
        assertTrue(
            inherited is MethodLookup.Runnable &&
                inherited.view.signature == "${GRANDPARENT.descriptor}-><clinit>()V",
            "the ancestor-searching lookup hands back the SUPERCLASS's initializer: $inherited",
        )
        assertEquals(
            MethodLookup.NoSuchMethod, classIndex.currentResolver().lookupClassInitializer(HEIR),
            "...and the declared-only lookup correctly finds nothing",
        )
        assertFalse(classIndex.currentResolver().hasClassInitializer(HEIR))
        assertTrue(classIndex.currentResolver().hasClassInitializer(WITNESS), "a class that DOES declare one is still found")
        // Initializing `heir` runs grandparent's initializer as the ancestor's, then settles `heir` itself with
        // nothing to run.
        val frame = plannerFrame()
        val first = defaultInvoker(classIndex).planClassInit(frame, HEIR)
        assertEquals(GRANDPARENT.descriptor, first.context?.activeInitializer?.type?.descriptor)
        frame.classes.complete(GRANDPARENT)
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
        assertTrue(classIndex.currentSnapshot.hierarchy.isFrameworkClass(INTEGER), "Integer is framework, and Integer.smali IS loaded")
        assertFalse(classIndex.currentSnapshot.hierarchy.isFrameworkClass(WITNESS), "a fixture class is an application class")
        val plan = defaultInvoker(classIndex).planClassInit(plannerFrame(), INTEGER)
        assertNull(plan.context, "a framework <clinit> is NEVER run")
        assertTrue(plan.settled.map { it.descriptor }.contains(INTEGER.descriptor), "but it IS marked initialized")
        assertTrue(
            plan.skippedAnInitializer,
            "an initializer that exists and did not run is unmodelled state, so the trigger is STRONG",
        )
    }

    @Test
    fun theTopmostUninitializedAncestorIsTheOneThatRunsFirst() {
        // The plan names GRANDPARENT even though CHILD was asked for. One initializer per plan is what makes
        // the order unarrangeable -- see `ClassInitPlan`'s KDoc.
        val plan = defaultInvoker(classIndex).planClassInit(plannerFrame(), CHILD)
        assertEquals(GRANDPARENT.descriptor, plan.context?.activeInitializer?.type?.descriptor)
        assertEquals(
            "${GRANDPARENT.descriptor}-><clinit>()V", plan.context?.activeInitializer?.view?.signature,
            "and the view is the ancestor's own declared initializer",
        )
    }

    // ------------------------------------------------------------------------------------------
    // The lifecycle through a real run, triggered by `new-instance`.
    // ------------------------------------------------------------------------------------------

    @Test
    fun aClassWithNoGuestClinitBecomesInitializedWithNoNestedRun() {
        val graph = run(TRIGGER, "newNoClinit()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph), "nothing may be run for a class with no <clinit>")
        assertEquals(
            ClassInitState.INITIALIZED, initStateAtReturn(graph, NO_CLINIT),
            "it is still INITIALIZED: Uninitialized -> Initialized is a real step, not a shortcut",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.explored.methodSideEffectLevel(),
            "nothing was skipped, so nothing may be declared -- a spurious STRONG stops every fold here",
        )
    }

    @Test
    fun aGuestClinitRunsAsANestedCallAndTheTriggerThenProceeds() {
        val graph = run(TRIGGER, "newWitness()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(WITNESS.descriptor), initializersRun(graph), "exactly one nested <clinit> ran")
        assertEquals(ClassInitState.INITIALIZED, initStateAtReturn(graph, WITNESS))
        // Initialization is a prelude to the instruction, so the prelude resumes at the trigger's own address
        // and the second pass does the op's real work.
        assertEquals(2, graph.nodePile(0).size, "the trigger re-executes once the prelude returns")
        assertEquals(listOf(Outcome.Returned(Value.Known(7, int))), graph.outcomes)
    }

    @Test
    fun aClinitRunsOnTheCallersOwnHeapIdSpaceAndItsAllocationSurvivesTheReturn() {
        // The `<clinit>` frame shares the caller's heap id space and the caller resumes with what the initializer
        // allocated. A fresh id space would give the initializer's first object the same `HeapRef` as the
        // caller's, which heap consensus reads as one object.
        val graph = run(TRIGGER, "newWitness()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        val allocation = nodesIn(graph, "${WITNESS.descriptor}-><clinit>()V")
            .single { it.address == NEW_ARRAY_IN_WITNESS_CLINIT }
            .frame.peekRegister(1)
        assertTrue(allocation is Value.Known && allocation.raw is HeapRef, "the <clinit> allocated: $allocation")
        val ref = (allocation as Value.Known).raw as HeapRef
        val resumed = nodesIn(graph, viewOf(TRIGGER, "newWitness()I").signature).last { it.outcome != null }
        val array = resumed.frame.heapObject(ref)
        assertTrue(array is HeapObject.TrackedArray, "the initializer's object is on the caller's heap: $array")
        assertEquals(3, (array as HeapObject.TrackedArray).length)
    }

    @Test
    fun aSecondTriggerOfAnInitializedClassDoesNotRerunItsClinit() {
        val graph = run(TRIGGER, "newWitnessTwice()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(WITNESS.descriptor), initializersRun(graph),
            "the second trigger finds the class INITIALIZED; re-running applies its writes twice",
        )
    }

    @Test
    fun aClinitThatRetriggersItsOwnClassSeesInitializingAndDoesNotRecurse() {
        // Without the re-entry guard the run recurses until `Limits.maxCallDepth` truncates it, so the
        // assertions are on `Complete` and on the run count.
        val graph = run(TRIGGER, "newReentrant()I")
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "removing the Initializing guard makes this Partial(CALL_DEPTH)",
        )
        assertEquals(listOf(REENTRANT.descriptor), initializersRun(graph), "the initializer runs exactly once")
        // The guard is state the nested call can see: the `<clinit>`'s own frame reads INITIALIZING.
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
        // Superclass-first ordering is what makes a superclass static readable by a subclass initializer.
        val graph = run(TRIGGER, "newChild()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(GRANDPARENT.descriptor, PARENT.descriptor, CHILD.descriptor), initializersRun(graph),
            "most ancestral first, each exactly once",
        )
        // Every class in the chain is initialized on the returning path, including `Ljava/lang/Object;`, which
        // had nothing to run: its mark is applied to the first prelude's callee frame and comes back only
        // because the resumed caller takes the class store from the callee.
        assertEquals(
            listOf(ClassInitState.INITIALIZED),
            listOf(VirtualType.OBJECT, GRANDPARENT, PARENT, CHILD).map { initStateAtReturn(graph, it) }.distinct(),
            "the whole chain, settled classes included, is initialized when the trigger's method returns",
        )
        // One pass over the trigger per initializer, plus the pass that does its own work.
        assertEquals(4, graph.nodePile(0).size, "one trigger pass per initializer, plus the one that proceeds")
    }

    @Test
    fun oneBranchInitializingDoesNotExemptTheOther() {
        // Two paths: one trigger reached on only one of them plus a shared trigger reached on both, so the
        // correct answer is two initializer runs, one per path. A session-global store gives one.
        val graph = run(TRIGGER, "newWitnessOnOneBranchThenOnBoth(I)I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(WITNESS.descriptor, WITNESS.descriptor), initializersRun(graph),
            "each PATH initializes for itself",
        )
    }

    // ------------------------------------------------------------------------------------------
    // The method-entry trigger.
    // ------------------------------------------------------------------------------------------

    @Test
    fun aMethodEntryTriggersItsOwnDefiningClassInitBeforeTheCalleeRuns() {
        // Every non-`<clinit>` method entry triggers initialization of the method's defining class.
        val graph = run(METHOD_ENTRY_TRIGGER, "callsWitnessTouch()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(WITNESS.descriptor), initializersRun(graph))
        // The callee's own entry node must see its class already initialized.
        assertEquals(
            ClassInitState.INITIALIZED,
            nodesIn(graph, "${WITNESS.descriptor}->touch()V").first().frame.initState(WITNESS),
            "the callee's entry frame sees its defining class initialized",
        )
        assertEquals(listOf(Outcome.Returned(Value.Known(7, int))), graph.outcomes)
    }

    @Test
    fun aMethodEntryNeedingNoInitializerCostsNoExtraNode() {
        // When a callee's class needs only marks, they are seeded onto the callee's own entry frame and the
        // invoke is not re-executed.
        val graph = run(METHOD_ENTRY_TRIGGER, "callsNoClinitTouch()I")
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
        // Bytecode may call a `<clinit>` directly. If that entry triggered the class's initialization, the same
        // `<clinit>` would be scheduled as a prelude and run twice on one path. The `Initializing` guard cannot
        // cover it: at the call site the class is still `Uninitialized`.
        val graph = run(METHOD_ENTRY_TRIGGER, "callsWitnessClinitDirectly()I")
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
        // The method-entry trigger and the instruction trigger share one planner, so the ordering does not
        // depend on which one asked.
        val graph = run(METHOD_ENTRY_TRIGGER, "callsChildTouch()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(GRANDPARENT.descriptor, PARENT.descriptor, CHILD.descriptor), initializersRun(graph),
        )
    }

    // ------------------------------------------------------------------------------------------
    // What must NOT initialize.
    // ------------------------------------------------------------------------------------------

    @Test
    fun constClassStillDoesNotInitialize() {
        // Measured on device: `tools/art-runner/fixtures/Fx_ConstClassDoesNotInitialize.smali` against its control
        // `Fx_StaticFieldDoesInitialize.smali`.
        val graph = CoreTester.execute("Lclass_init/const_class_probe;", "resolvesWitnessClass()Ljava/lang/Class;")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph), "const-class resolves without initializing")
        assertEquals(ClassInitState.UNINITIALIZED, initStateAtReturn(graph, WITNESS))
        assertEquals(
            SideEffectLevel.NONE, graph.explored.methodSideEffectLevel(),
            "which is what makes const-class's NONE truthful (oracle Correctness Defect 21 is the opposite)",
        )
    }

    // ------------------------------------------------------------------------------------------
    // Harness
    // ------------------------------------------------------------------------------------------

    private fun viewOf(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    /** A fresh path state on which nothing is initialized yet, for planner-only questions. */
    private fun plannerFrame(): Frame = Frame.entryFrame(viewOf(TRIGGER, "newWitness()I"))

    private fun run(className: String, descriptor: String): ExecutionGraph =
        CoreTester.vmServices.newSession(viewOf(className, descriptor)).run()

    /**
     * Every node the run created, entry activation and callees alike, in walk order. A `<clinit>`'s nodes
     * are not in the graph's piles, which describe the entry method, so the walk follows the child edges.
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
        /** Instruction triggers: each method's `new-instance` names the class it initializes. */
        const val TRIGGER = "Lclass_init/instruction_trigger;"

        /** Method-entry triggers: each method's `invoke-static` names a method of the class it initializes. */
        const val METHOD_ENTRY_TRIGGER = "Lclass_init/trigger;"

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
