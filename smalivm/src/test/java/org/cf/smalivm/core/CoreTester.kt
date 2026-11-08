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
 * [ClassIndex] is shared because construction imports the complete fixture tree.
 */
internal object CoreTester {
    const val TEST_CLASS_PATH = "src/test/resources/smali"

    /** Parsed once; each import builds its own immutable program snapshot from it. */
    private val fixtureInput = ProgramInput.read(File(TEST_CLASS_PATH), SmaliParser.DEX_API_LEVEL)

    /** A fresh workspace over the fixture tree, for tests that need their own [ClassIndex]. */
    fun importFixtures(): ProgramWorkspace = InitialProgramImporter.import(fixtureInput)

    val classIndex: ClassIndex = ClassIndex(importFixtures())

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

    /** [testVisitation] with no seeded input -- the shape every Plan 3 test used. */
    fun assertVisited(className: String, methodDescriptor: String, expected: IntArray) =
        testVisitation(className, methodDescriptor, CoreState(), expected)

    // Register, static-field, and visitation assertions shared by opcode behavior tests.

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
     * Asserts exactly which addresses executed, with [initial] seeded so a visitation claim can depend
     * on an input. The single implementation; [assertVisited] is the unseeded alias.
     *
     * Checks completion FIRST, for the same reason [assertReturns] does, even though `wasReached` is
     * ungated and needs no [TrustedGraphView]: an *absent* address here is a dead-code claim, and on a
     * `Partial` run `wasReached` returns `false` for a genuinely unreachable address and for one the
     * run simply never got to, indistinguishably (see [ExecutionGraph.wasReached]). Without this
     * check the harness would quietly teach every later plan's tests to read a truncated exploration
     * as a coverage proof -- and a fixture that stopped early would show up as a *passing* narrower
     * expectation rather than as a failure.
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
     * Asserts some explored path threw [exceptionType] (and, when given, with [message]). The
     * counterpart of the oracle's `Tester.testSimpleException`.
     *
     * Reads `graph.outcomes` rather than a register, because a thrown value is an [Outcome.Threw] in
     * the new model rather than a value parked in a pseudo-register. Deliberately says nothing about
     * whether a NORMAL path was also explored: for an ambiguous operand both are feasible and both
     * must run, so a caller that needs "and the fall-through did not happen" asserts that itself
     * (`wasReached` on the successor address) rather than having it silently folded in here.
     *
     * **THIS CANNOT PIN AN EXCEPTION ORDER, and reaching for it to do so produces a green test with no
     * lethal mutation.** The assertion is `matching.isNotEmpty()` -- **existence, not exclusivity**. So
     * "an NPE is thrown before an ArrayStoreException" written as `testThrows(NPE)` still passes when the
     * op *also* forks the ASE, because the NPE is present either way. Found the hard way in Plan 7 Task 9:
     * two `APutOp` comments claimed "the ASE does not compose here", and appending the ASE fork to either
     * row killed **0 of 844** tests. A 19-mutation battery missed it, because every mutation it tried
     * changed *which* exception appeared rather than *how many*.
     *
     * To pin an order, assert the **exclusion**: that the losing exception's type is absent from
     * `graph.outcomes`, or that the node has exactly the expected number of children. See
     * `core/op/APutOpTest`'s `aNullArrayThrowsNullPointerExceptionBeforeArrayStore` and
     * `aVerifiedObjectStoreWithABadIndexThrowsBoundsBeforeArrayStore` for the shape that is lethal.
     * **Every inherited [JVMS] "Otherwise"-chained ordering claim needs this treatment** -- Plan 7's
     * remaining tasks and Plan 9 each have several.
     *
     * **Completion-gated like the other three entry points, even though its own claim would survive a
     * truncated run.** "Some path threw X" is existential, so it is technically sound on a `Partial`
     * run -- which is exactly why this was the one ungated entry point, and why gating it is a
     * deliberate decision rather than a consistency tidy-up:
     *  - it is never written alone. Callers pair it with NEGATIVE claims, which are unsound on a
     *    truncated run: `aKnownZeroIntDivisorThrowsAndDoesNotContinue` adds `assertFalse(wasReached(2))`
     *    and every zero-divisor test's real content is "throws AND does not do the other thing". A
     *    facade that passes the existential half while the run silently truncated is what makes the
     *    pair look verified.
     *  - the concrete accident is a later plan's, not this one's: an array fixture that throws on one
     *    path and exceeds `Limits.maxAddressVisits` on the other would pass here while
     *    `completion() == Partial(ADDRESS_LIMIT)`, and ~300 further migrations flow through this object.
     *    `ThrowRoutingTest` already exhibits a `Partial` run whose `outcomes` contain `Outcome.Threw`,
     *    so the shape is not hypothetical.
     *  - the escape hatch stays honest rather than being removed. A test that genuinely means "throws on
     *    one path, truncates on another" is still expressible -- through `execute` plus a POSITIVE
     *    `assertTrue(completion() is Partial)`, which
     *    `BinaryMathOpTest.aMalformedFallThroughDoesNotDiscardTheFeasibleExceptionalBranch` already
     *    does. That states the truncation and gets it checked; a second ungated facade overload would
     *    let it be assumed instead, and 300 migrations copy whatever the facade offers.
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
            // Two shapes reach here, and both are legitimate. A GUEST throwable the engine itself
            // synthesized (BinaryMathOp's divide-by-zero, and the array ops that follow it) is a tracked
            // heap object (spec §4.1), so its message has to be read through the heap of the node that
            // threw -- there is no such thing as "the" heap for a run. Deliberately NOT via
            // terminatingHeapObject: a throwing path does not return, so the terminating projection
            // cannot see it at all. A throwable that arrived as a SEEDED parameter (ThrowRoutingTest's
            // style, and CoreTesterTest.testThrowsMatchesTheThrownTypeAndItsMessage) is not synthesized
            // by any op -- there is nothing for `new-instance` (Plan 9) to have heap-allocated -- so it
            // still carries a live host Throwable directly, and its message is read straight off that.
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
     * A guest throw that the method's own handler CAUGHT, which is what the corpus's
     * `Tester.testSimpleException` asserted and what [testThrows] asserts the opposite of: a caught throw
     * produces NO `Outcome.Threw`, because it never escaped ([ExecutionSession]'s `planThrow` gives a
     * definitely-caught throw `successors=[handler], terminal=null`). Migrating a `…WithCatch()V` fixture
     * onto [testThrows] therefore fails on arrival, and twelve of Plan 7's tests are that shape -- six of
     * them in Task 9 alone.
     *
     * Asserts the whole of the original claim, so the migration stays byte-identical rather than losing
     * half of it:
     *  - the run is `Complete` (a caught throw is not a truncation);
     *  - nothing ESCAPED with [exceptionType] -- if it did, the handler did not cover the op and the
     *    fixture is not the one the test thinks it is;
     *  - `terminatingConsensus(caughtInRegister)` is a `Known` of [exceptionType], i.e. `move-exception`
     *    bound it where the fixture says it does -- derive the register from the smali, do not assume 0;
     *  - its [message] matches;
     *  - [unreachedAddress], when given, was not reached -- the "did not fall through" half.
     *
     * **Where the message is read from, and why it is NOT "exactly as [testThrows] does".** [testThrows]
     * finds the synthesized throwable by walking nodes whose own `outcome is Outcome.Threw`. For a CAUGHT
     * throw there is no such node at all -- that is the entire difference between the two helpers -- so
     * that mechanism cannot work here. The heap is reached through `terminatingHeapObject` instead, i.e.
     * through heap CONSENSUS over returning paths, which is also strictly better: it reads a value every
     * returning path agrees about rather than one path's. That query answering non-null is guaranteed
     * rather than hoped for, by the assertion immediately above it -- if every returning path agrees the
     * register holds `Known(ref, T)`, every returning path's heap holds `ref`. A throwable that arrived as
     * a SEEDED host `Throwable` (no op synthesized it, so there is nothing on the heap) has its message
     * read straight off the raw, exactly as [testThrows] does for that shape.
     *
     * Deliberately NOT an `onlyIf`-style flag on [testThrows]: the two assert different graph shapes, and a
     * boolean parameter that flips which shape is asserted is how a migration ends up asserting neither.
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
        // The half that tells "caught" apart from "escaped", and the half a `testThrows`-shaped migration
        // gets exactly backwards. Asserted on the TYPE rather than on `threw.isEmpty()`: a fixture may
        // legitimately fork a different escaping throw elsewhere, and this claim is about THIS one.
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
     * Seeds [initial]'s static fields onto [frame] **and marks each named class `INITIALIZED`**.
     *
     * **The mark is not a convenience -- without it the seed is destroyed by the very next instruction.** A
     * `sget`/`sput` of a class the path has not triggered triggers initialization, and JVMS §5.4.2 preparation then gives
     * every declared static its literal or type default (`ClassInitPlan.staticSeeds`) -- overwriting whatever
     * the test poked in. Marking the class initialized says what a corpus seed MEANS: "this field already holds
     * this value when the method is entered", which is only expressible for a class whose initialization has
     * already happened.
     *
     * It is also exactly what the oracle's own harness does, one line after poking the fields:
     * `Tester.buildInitialExecutionState` calls `state.setClassInitialized(virtualClass, SideEffectLevel.NONE)`
     * per class (`Tester.kt:99-107`). So a migrated seed keeps its meaning rather than acquiring a new one.
     *
     * `distinct()` because [ClassInitState.INITIALIZED] is TERMINAL: two seeded fields of one class would make
     * the second `completeClassInit` an illegal step and throw. Kept as a loud requirement of the store rather
     * than softened there.
     */
    private fun seedStaticFields(frame: Frame, initial: CoreState) {
        for ((descriptor, value) in initial.staticFields) frame.classes.setStaticField(descriptor, value)
        for (owner in initial.staticFields.keys.map(::declaringClassOf).distinct()) {
            frame.classes.complete(owner)
        }
    }

    /**
     * Every ESCAPING throw's type, in transition order -- the shared, TOTAL replacement for the four
     * hand-rolled copies this used to have (`core/ThrowRoutingTest`, `core/op/APutOpTest`,
     * `core/op/FillArrayDataOpTest`, `core/op/FilledNewArrayOpTest`), hoisted here by Plan 8 Task 11
     * (`CARRY-FORWARD.md` Plan 8 item 9).
     *
     * **Total over the lattice, which the copies were NOT.** Each copy read
     * `(it.exception as? Value.Known)?.type?.descriptor` through `mapNotNull`, so a genuinely thrown
     * [Value.Unknown] or [Value.Top] -- which `core/op/ThrowOp` escapes TODAY, demonstrated by
     * `ThrowRoutingTest.anUnprovableThrowReachesEveryFeasibleHandlerAndTheEscape` -- rendered as
     * NOTHING and the helper answered `[]` for a run that genuinely threw. The trap that made it worth
     * fixing was not a live wrong assertion (all 36 call sites throw a `Known`) but the next author writing
     * a vacuous `.isEmpty()` beside a `throw` of a parameter.
     *
     * **The representation, stated because it is a decision:** a `Known` renders its type descriptor
     * exactly as before (so every migrated assertion is byte-identical), a non-`Known` renders a MARKED
     * form -- `Unknown(<descriptor>)` for a [Value.Unknown], `Top` for a [Value.Top] -- so a non-`Known`
     * escape is both VISIBLE (an `.isEmpty()` beside one is now false) and DISTINCT from a definite type of
     * the same name. Reverting the `when` to the old `mapNotNull { (it.exception as? Value.Known)... }` drops
     * the non-`Known` rows again and fails
     * `ThrowRoutingTest.anUnprovableThrowReachesEveryFeasibleHandlerAndTheEscape`, which is the totality pin.
     */
    fun escapingTypes(graph: ExecutionGraph): List<String> =
        graph.outcomes.filterIsInstance<Outcome.Threw>().map { threw ->
            when (val exception = threw.exception) {
                is Value.Known -> exception.type.descriptor
                is Value.Unknown -> "Unknown(${exception.type.descriptor})"
                Value.Top -> "Top"
            }
        }
}
