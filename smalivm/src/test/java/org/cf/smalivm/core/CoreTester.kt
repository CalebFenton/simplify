package org.cf.smalivm.core

import java.io.File
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.dex.SmaliParser
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Executes immutable fixture snapshots and asserts register, heap, static-field, and visitation results.
 * [classIndex] is one shared load-time cache: no test edits or publishes through it (method-edit tests open
 * their own index), and every session copies its state, so no test can observe another's run.
 */
internal object CoreTester {
    const val TEST_CLASS_PATH = "src/test/resources/smali"

    val classIndex: ClassIndex =
        ClassIndex(InitialProgramImporter.import(ProgramInput.read(File(TEST_CLASS_PATH), SmaliParser.DEX_API_LEVEL)))

    val vmServices = VmServices(classIndex)

    /**
     * The engine's own host-class loader ([ClassIndex.classLoader]), so a test seeds or expects a `Class` object
     * through the loader the core resolves through; a distinct loader would define a different `Class`.
     */
    val classLoader: SmaliClassLoader get() = classIndex.classLoader

    /**
     * Runs a method to completion. [initialRegisters] seeds the entry frame to explore one input. A test of
     * class-initialization or static-field semantics that starts at process start passes
     * [EntryClassState.FRESH_PROCESS]; every entry point below takes the same parameter.
     */
    fun execute(
        className: String,
        methodDescriptor: String,
        initialRegisters: Map<Int, Value> = emptyMap(),
        limits: Limits = Limits(),
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ): ExecutionGraph {
        val view = classIndex.requireMethodView(className, methodDescriptor)
        val frame = Frame.entryFrame(view)
        val entries = initialRegisters.entries.toList()
        val admitted = frame.heap.admitHostSeeds(entries.map { HostSeed(it.value) })
        entries.zip(admitted).forEach { (entry, value) -> frame.assign(entry.key, value) }
        return vmServices.newSession(view, limits, frame, entryClassState).run()
    }

    /**
     * Asserts the value every returning path agrees on for [register].
     *
     * Goes through [ExecutionGraph.trusted] rather than the ungated accessor on purpose: a behavioral
     * assertion is exactly the kind of "act on this value" a consumer does, so the harness models the
     * consumer's own path. That also makes the completion check unskippable here -- the assertion
     * below cannot even be written without it.
     */
    fun assertReturns(
        className: String,
        methodDescriptor: String,
        expected: Value,
        register: Int = 0,
        initialRegisters: Map<Int, Value> = emptyMap(),
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ) {
        val graph = execute(className, methodDescriptor, initialRegisters, entryClassState = entryClassState)
        assertEquals(CompletionStatus.Complete, graph.completion(), "exploration must complete to be trusted")
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(expected, trusted!!.terminatingConsensus(register))
    }

    /** [testVisitation] with no seeded input. */
    fun assertVisited(className: String, methodDescriptor: String, expected: IntArray) =
        testVisitation(className, methodDescriptor, CoreState(), expected)

    /**
     * Runs [methodDescriptor] with [initial] seeded and asserts every register [expected] names.
     *
     * Goes through [ExecutionGraph.trusted] for the same reason [assertReturns] does: a behavioral
     * assertion is exactly the "act on this value" a consumer does, so the harness walks the
     * consumer's own path and the completion check becomes unskippable.
     */
    fun test(
        className: String,
        methodDescriptor: String,
        initial: CoreState,
        expected: CoreState,
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ) {
        // An expectation with no register, array, or static field asserts only that the run completed.
        require(expected.registers.isNotEmpty() || expected.arrays.isNotEmpty() || expected.staticFields.isNotEmpty()) {
            "CoreTester.test needs at least one expected register, array or static field, or it asserts " +
                "nothing about $className->$methodDescriptor beyond the run completing"
        }
        // The seed passed as the expectation degenerates every assertion to "the seed survived". Identity,
        // not equality: two independent equal states are a legitimate "this method changes nothing" claim.
        require(initial !== expected) {
            "CoreTester.test was given ONE CoreState as both seed and expectation, so every assertion " +
                "it makes is `the seed survived` -- vacuous for any register " +
                "$className->$methodDescriptor does not write. Build a separate expected state: $expected"
        }
        // `valueIn` answers `Top` for an out-of-range register, so an expected `Top` there would pass with the
        // wrong register number.
        val view = classIndex.requireMethodView(className, methodDescriptor)
        val expectedRegistersAndArrays = expected.registers.keys + expected.arrays.keys
        require(expectedRegistersAndArrays.all { it in 0 until view.registerCount }) {
            "CoreTester.test was given expected register(s) " +
                "${expectedRegistersAndArrays.filter { it !in 0 until view.registerCount }} outside " +
                "$className->$methodDescriptor's [0, ${view.registerCount}) -- which would compare " +
                "Value.Top against Value.Top and pass while asserting nothing"
        }
        val graph = execute(className, methodDescriptor, initial, entryClassState = entryClassState)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "exploration must complete before any consensus may be trusted",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        for ((register, expectedValue) in expected.registers) {
            assertEquals(
                expectedValue, trusted!!.terminatingConsensus(register),
                "register $register after $className->$methodDescriptor",
            )
        }
        // Static fields through returning-path consensus, never one node's frame.
        for ((descriptor, expectedValue) in expected.staticFields) {
            assertEquals(
                expectedValue,
                trusted!!.terminatingStaticFieldConsensus(declaringClassOf(descriptor), descriptor),
                "static field $descriptor after $className->$methodDescriptor",
            )
        }
        // Element-wise through heap consensus, never one path's heap: two returning paths can agree about the
        // ref and disagree about the contents.
        for ((register, spec) in expected.arrays) {
            val consensus = trusted!!.terminatingConsensus(register)
            assertTrue(
                consensus is Value.Known && consensus.raw is HeapRef,
                "register $register after $className->$methodDescriptor should hold a tracked array, but was $consensus",
            )
            val ref = (consensus as Value.Known).raw as HeapRef
            val actual = trusted.terminatingHeapObject(ref).agreed()
            assertTrue(actual is HeapObject.TrackedArray, "no array at $ref for register $register; got $actual")
            val array = actual as HeapObject.TrackedArray
            val expectedElements = CoreState.elementsOf(spec)
            assertEquals(expectedElements.size, array.length, "length of register $register after $className->$methodDescriptor")
            // Assignability, not equality: the verifier proves the declared and runtime array types
            // compatible, not identical (`String[]` may be declared as `Object[]`).
            assertTrue(
                classIndex.isSubtypeOf(array.type, spec.type),
                "register $register after $className->$methodDescriptor: runtime array type ${array.type} " +
                    "is not assignable to declared type ${spec.type}",
            )
            for (i in expectedElements.indices) {
                assertEquals(expectedElements[i], array.element(i), "register $register element $i after $className->$methodDescriptor")
            }
        }
    }

    fun test(
        className: String,
        methodDescriptor: String,
        expected: CoreState,
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ) = test(className, methodDescriptor, CoreState(), expected, entryClassState)

    /**
     * Asserts exactly which addresses executed, with [initial] seeded so a visitation claim can depend on an
     * input. Checks completion first: an absent address is a dead-code claim, and on a `Partial` run
     * [ExecutionGraph.wasReached] answers false both for an unreachable address and for one the run never got to.
     */
    fun testVisitation(
        className: String,
        methodDescriptor: String,
        initial: CoreState,
        expectedAddresses: IntArray,
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ) {
        val graph = execute(className, methodDescriptor, initial, entryClassState = entryClassState)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "an unreached address is only a dead-code claim on a run that explored everything",
        )
        assertArrayEquals(
            expectedAddresses.sortedArray(),
            graph.addresses().filter { graph.wasReached(it) }.toIntArray(),
        )
    }

    /**
     * Asserts some explored path threw [exceptionType], with [message] when given. It says nothing about
     * whether a normal path was also explored; a caller that means "and did not fall through" asserts
     * `wasReached` on the successor itself.
     *
     * This is existence, not exclusivity, so it cannot pin an exception order: "NPE before ArrayStoreException"
     * written as `testThrows(NPE)` still passes when the op also forks the ASE. To pin an order, assert the
     * losing type is absent from [escapingTypes], or that the node has exactly the expected children.
     *
     * Completion-gated although the claim is existential, because callers pair it with negative claims that
     * are unsound on a truncated run. A test that means "throws on one path, truncates on another" uses
     * [execute] and asserts `completion() is Partial` itself.
     */
    fun testThrows(
        className: String,
        methodDescriptor: String,
        initial: CoreState = CoreState(),
        exceptionType: String,
        message: String? = null,
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ) {
        val graph = execute(className, methodDescriptor, initial, entryClassState = entryClassState)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "a throw claim is only half of what its callers mean, and the other half needs a finished run",
        )
        val threw = graph.outcomes.filterIsInstance<Outcome.Threw>()
        assertTrue(threw.isNotEmpty(), "expected a thrown outcome from $className->$methodDescriptor")
        val expectedType = VirtualType.of(exceptionType)
        val matching = threw.filter { (it.exception as? Value.Known)?.type == expectedType }
        assertTrue(
            matching.isNotEmpty(),
            "expected a $exceptionType, got ${threw.map { it.exception }}",
        )
        if (message != null) {
            // A throwable an op synthesized is a tracked heap object, so its message is read through the heap of
            // a node that threw; the terminating projection sees only returning paths. A seeded host
            // `Throwable` was allocated by no op, so its message is read straight off the raw.
            val thrown = matching.first().exception as Value.Known
            val actualMessage = when (val raw = thrown.raw) {
                is HeapRef -> {
                    val host = graph.addresses().asSequence()
                        .flatMap { graph.nodePile(it).asSequence() }
                        .filter { it.outcome is Outcome.Threw }
                        .mapNotNull { it.terminalFrame.heap.hostObjectAt(raw) as? Throwable }
                        .firstOrNull()
                    host?.message ?: throw AssertionError("no tracked throwable at $raw")
                }
                is Throwable -> raw.message
                else -> throw AssertionError(
                    "a message can only be read off a host throwable or a tracked guest throwable, but the raw was $raw",
                )
            }
            assertEquals(message, actualMessage)
        }
    }

    /**
     * Asserts a guest throw that the method's own handler caught: a caught throw produces no `Outcome.Threw`,
     * so [testThrows] asserts the opposite shape. Checks that the run completed, that nothing escaped with
     * [exceptionType], that `move-exception` bound a `Known` of [exceptionType] in [caughtInRegister] (derive
     * the register from the smali), its [message], and that [unreachedAddress], when given, was not reached.
     *
     * The message of a synthesized throwable is read through heap consensus over returning paths, which the
     * register assertion above guarantees exists; a seeded host `Throwable` has it read off the raw.
     */
    fun testCaught(
        className: String,
        methodDescriptor: String,
        initial: CoreState = CoreState(),
        exceptionType: String,
        message: String? = null,
        caughtInRegister: Int = 0,
        unreachedAddress: Int? = null,
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ) {
        val graph = execute(className, methodDescriptor, initial, entryClassState = entryClassState)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "a caught throw is not a truncation, so $className->$methodDescriptor must complete",
        )
        val expectedType = VirtualType.of(exceptionType)
        // Asserted on the type rather than on `threw.isEmpty()`: a fixture may legitimately fork a different
        // escaping throw elsewhere, and this claim is about this one.
        val escaped = graph.outcomes.filterIsInstance<Outcome.Threw>()
            .filter { (it.exception as? Value.Known)?.type == expectedType }
        assertTrue(
            escaped.isEmpty(),
            "$exceptionType ESCAPED $className->$methodDescriptor, so the handler did not cover the op: $escaped",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val caught = trusted!!.terminatingConsensus(caughtInRegister)
        assertTrue(
            caught is Value.Known && caught.type == expectedType,
            "register $caughtInRegister after $className->$methodDescriptor should hold the caught " +
                "$exceptionType that `move-exception` bound there, but was $caught",
        )
        if (message != null) {
            val known = caught as Value.Known
            val actualMessage = when (val raw = known.raw) {
                is HeapRef -> {
                    val host = terminatingHostObject(graph, raw) as? Throwable
                    host?.message ?: throw AssertionError("no tracked throwable at $raw")
                }
                is Throwable -> raw.message
                else -> throw AssertionError(
                    "a message can only be read off a host throwable or a tracked guest throwable, but the raw was $raw",
                )
            }
            assertEquals(message, actualMessage)
        }
        if (unreachedAddress != null) {
            // Sound only because of the completion gate at the top: `wasReached` answers false
            // identically for a genuinely unexplored address and for one a truncated run never got to
            // (see [testVisitation]'s KDoc).
            assertFalse(
                graph.wasReached(unreachedAddress),
                "a definite throw must not also explore the fall-through at $unreachedAddress",
            )
        }
    }

    /**
     * The class a fully-qualified field descriptor names. [CoreState] stores one flat key while
     * [TrustedGraphView.terminatingStaticFieldConsensus] and [ClassState.getStaticField] take the pair.
     */
    private fun declaringClassOf(fieldDescriptor: String): VirtualType = FieldSignature.parse(fieldDescriptor).definingClass

    /** [initialRegisters]-shaped overload of [execute] taking the harness's own state type. */
    fun execute(
        className: String,
        methodDescriptor: String,
        initial: CoreState,
        limits: Limits = Limits(),
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ): ExecutionGraph {
        val view = classIndex.requireMethodView(className, methodDescriptor)
        val frame = Frame.entryFrame(view)
        val seeds = buildList {
            initial.registers.forEach { (register, value) -> add(register to HostSeed(value)) }
            initial.arrays.forEach { (register, spec) ->
                add(
                    register to HostSeed(
                        Value.Known(spec.hostArray, spec.type),
                        CoreState.trueTypeOf(spec),
                        CoreState.elementsOf(spec),
                    ),
                )
            }
        }
        val admitted = frame.heap.admitHostSeeds(seeds.map { it.second })
        seeds.zip(admitted).forEach { (seed, value) -> frame.assign(seed.first, value) }
        seedStaticFields(frame, initial)
        return vmServices.newSession(view, limits, frame, entryClassState).run()
    }

    /**
     * The host object every returning path of [graph] holds at [ref], or null when they disagree or none
     * returned. Host objects are mutable, so no public graph query exposes them.
     */
    fun terminatingHostObject(graph: ExecutionGraph, ref: HeapRef): Any? {
        val returned = graph.addresses().asSequence()
            .flatMap { graph.nodePile(it).asSequence() }
            .filter { it.outcome is Outcome.Returned && it.activation.returnSite == null }
            .toList()
        if (returned.isEmpty()) return null
        val first = returned.first().terminalFrame.heap.hostObjectAt(ref) ?: return null
        return if (returned.all { it.terminalFrame.heap.hostObjectAt(ref) === first }) first else null
    }

    /**
     * Seeds [initial]'s static fields onto [frame] and marks each declaring class `INITIALIZED`. Without the
     * mark, the first `sget`/`sput` of an untriggered class prepares its statics (JVMS §5.4.2) and overwrites
     * the seed; a seed means "this field holds this value at entry", which holds only for an initialized class.
     * `distinct()` because completing an initialized class again is an illegal transition.
     */
    private fun seedStaticFields(frame: Frame, initial: CoreState) {
        for ((descriptor, value) in initial.staticFields) frame.classes.setStaticField(descriptor, value)
        for (owner in initial.staticFields.keys.map(::declaringClassOf).distinct()) {
            frame.classes.complete(owner)
        }
    }

    /**
     * Every escaping throw's type, in transition order. Total over the lattice: a [Value.Unknown] renders as
     * `Unknown(<descriptor>)` and [Value.Top] as `Top`, so an escape of an unprovable value is visible and never
     * equal to a definite type of the same name.
     */
    fun escapingTypes(graph: ExecutionGraph): List<String> =
        graph.outcomes.filterIsInstance<Outcome.Threw>().map { threw ->
            when (val exception = threw.exception) {
                is Value.Known -> exception.type.descriptor
                is Value.Unknown -> "Unknown(${exception.type.descriptor})"
                Value.Top -> "Top"
            }
        }

    /**
     * Every node the run created, entry activation and callees alike, in walk order. A callee's nodes are not
     * in the graph's piles, which describe the entry method, so the walk follows the child edges.
     */
    fun allNodes(graph: ExecutionGraph): List<ExecutionNode> {
        val out = mutableListOf<ExecutionNode>()
        fun walk(node: ExecutionNode) {
            out.add(node)
            node.children.forEach(::walk)
        }
        walk(graph.nodePile(0).first())
        return out
    }

    /**
     * Every class whose `<clinit>` ran as a class-init prelude, in the order the runs started. Activations are
     * deduplicated by identity, so this counts initializer runs, not nodes; an ordinary `invoke` of a
     * `<clinit>` has no [Activation.classInitContext] and is not counted.
     */
    fun initializersRun(graph: ExecutionGraph): List<String> {
        val seen = mutableListOf<Activation>()
        for (node in allNodes(graph)) if (seen.none { it === node.activation }) seen.add(node.activation)
        return seen.mapNotNull { it.classInitContext?.activeInitializer?.type?.descriptor }
    }

    /** [type]'s lifecycle state on the entry method's returning paths, which must agree. */
    fun initStateAtReturn(graph: ExecutionGraph, type: VirtualType): ClassInitState {
        val returning = allNodes(graph).filter { it.outcome is Outcome.Returned && it.activation.caller == null }
        assertTrue(returning.isNotEmpty(), "no returning node in the entry activation")
        return returning.map { it.frame.initState(type) }.distinct().single()
    }
}
