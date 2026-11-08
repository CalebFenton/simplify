package org.cf.smalivm.differential

import com.android.tools.smali.dexlib2.Opcode
import org.cf.smalivm.core.AbortReason
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.agreed
import org.cf.smalivm.core.requireMethodView
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class CoreProjectionTest {

    @TestFactory
    fun curatedFixturesProjectTheirSpecifiedRegisters(): List<DynamicTest> {
        // Constants come from the smali bodies and arithmetic from [BC] and JLS 15; an unknown branch input
        // joins its arms rather than taking one.
        fun known(type: String, repr: String) = NormalizedValue(ValueKind.KNOWN, type, repr)
        fun int(repr: String) = known("I", repr)
        fun unknown(type: String) = NormalizedValue(ValueKind.UNKNOWN, type, null)
        val absent = NormalizedValue(ValueKind.ABSENT, null, null)
        val expected = mapOf(
            "spine-return-five" to mapOf(0 to int("5")),
            "spine-return-nothing" to mapOf(0 to int("7")),
            "spine-nop-then-const" to mapOf(0 to int("42")),
            "spine-goto-skip" to mapOf(0 to int("1")),
            "spine-move-chain" to mapOf(0 to int("9"), 1 to int("9"), 2 to int("9")),
            "spine-return-wide" to mapOf(0 to known("J", "4886718345")),
            "spine-return-string" to mapOf(0 to known("Ljava/lang/String;", "spine")),
            "spine-instance-wide-params" to mapOf(
                0 to int("7"), 3 to unknown("Lspine/spine;"), 4 to unknown("J"), 5 to absent,
                6 to unknown("Ljava/lang/String;"), 7 to unknown("I"),
            ),
            "spine-add-int" to mapOf(0 to int("7"), 1 to int("4")),
            "branch-disagreeing-arms" to mapOf(
                0 to NormalizedValue(ValueKind.CONFLICTED, null, null), 1 to absent, 2 to unknown("I"),
            ),
            "branch-returns-different-constants" to mapOf(0 to unknown("I")),
            "branch-known-falls-through" to mapOf(0 to int("42")),
            "smoke-return-five" to mapOf(0 to int("5")),
            "smoke-nested-array" to mapOf(0 to known("[[I", "[[1, 2]]")),
            "smoke-self-referential-array" to mapOf(0 to known("[Ljava/lang/Object;", "[[...]]")),
            "smoke-shared-inner-array" to mapOf(0 to known("[[I", "[[7], [7]]")),
            "smoke-null-element-from-fresh-array" to mapOf(2 to NormalizedValue(ValueKind.NULL, "Ljava/lang/Object;", null)),
            "smoke-fill-array-data-round-trip" to mapOf(0 to known("[I", "[1, 2, 3]")),
            "smoke-new-object" to mapOf(0 to known("Ljava/lang/Object;", "<instance java.lang.Object>")),
            "ratchet-compare-wide" to mapOf(4 to int("-1")),
            "ratchet-array-round-trip" to mapOf(2 to int("7")),
            "ratchet-branch-on-zero" to mapOf(0 to int("0")),
            "ratchet-invoke-static" to mapOf(0 to int("0")),
            "ratchet-new-instance" to mapOf(0 to known("Ljava/lang/Object;", "<instance java.lang.Object>")),
            "ratchet-alias-unrelated-allocation" to mapOf(
                0 to known("Ldifferential/ratchet;", "Uninitialized Ldifferential/ratchet;"),
                1 to known("Ldifferential/ratchet;", "<instance differential.ratchet>"),
            ),
            "ratchet-alias-object-allocation" to mapOf(
                0 to known("Ljava/lang/Object;", "Uninitialized Ljava/lang/Object;"),
                1 to known("Ljava/lang/Object;", "<instance java.lang.Object>"),
            ),
            "ratchet-static-field-round-trip" to mapOf(0 to int("5")),
            "switch-packed-known-match" to mapOf(0 to int("22")),
            "switch-packed-known-no-match" to mapOf(0 to int("99")),
            "switch-sparse-known-match" to mapOf(0 to int("22")),
            "switch-unknown-selector-forks" to mapOf(0 to int("7")),
            "types-array-arms-join-covariantly" to mapOf(0 to unknown("[Ljava/lang/Object;")),
            "types-cast-string-to-object" to mapOf(0 to known("Ljava/lang/Object;", "great maker")),
            "types-const-class-object" to mapOf(0 to known("Ljava/lang/Class;", "class java.lang.Object")),
            "types-known-instance-of-object" to mapOf(0 to known("Z", "true")),
            "unary-int-to-long" to mapOf(0 to known("J", "11")),
            "unary-neg-double" to mapOf(0 to known("D", "-15.1")),
            "binary-add-int" to mapOf(0 to int("4"), 1 to int("7")),
            "binary-add-int-2addr" to mapOf(0 to int("4")),
            "binary-add-int-lit8" to mapOf(0 to int("12")),
            "binary-rsub-int" to mapOf(0 to int("245")),
            "binary-shl-long" to mapOf(0 to known("J", "8589934592"), 2 to int("33")),
            "binary-div-double" to mapOf(0 to known("D", "2.5")),
            "binary-div-double-by-zero" to mapOf(0 to known("D", "Infinity")),
            "binary-div-int-by-zero-caught" to mapOf(
                0 to known("Ljava/lang/ArithmeticException;", "java.lang.ArithmeticException: / by zero"),
            ),
            "cmp-long-greater" to mapOf(0 to int("1")),
            "cmp-float-nan-cmpg" to mapOf(0 to int("1"), 1 to known("F", "1.0")),
            "cmp-float-nan-cmpl" to mapOf(0 to int("-1")),
            "new-instance-local" to mapOf(0 to known("Lnew_instance_test;", "Uninitialized Lnew_instance_test;")),
            "new-instance-framework-class" to mapOf(0 to known("Ljava/lang/Integer;", "Uninitialized Ljava/lang/Integer;")),
            "new-instance-two-allocations" to mapOf(
                0 to known("Lnew_instance_test;", "Uninitialized Lnew_instance_test;"),
                1 to known("Lnew_instance_test;", "Uninitialized Lnew_instance_test;"),
            ),
            "new-instance-triggers-class-init" to mapOf(0 to known("Lclass_init/witness;", "Uninitialized Lclass_init/witness;")),
            "sget-literal-no-clinit" to mapOf(0 to int("66")),
            "statics-clinit-assigned-field" to mapOf(0 to int("7")),
            "invoke-static-resolved" to mapOf(0 to int("11")),
            "invoke-direct-resolved" to mapOf(1 to int("12")),
            "invoke-virtual-resolved" to mapOf(1 to int("13")),
            "invoke-interface-resolved" to mapOf(1 to int("14")),
            "invoke-handled-throw" to mapOf(0 to int("22")),
            "invoke-handled-partition-agreement" to mapOf(0 to int("2")),
            "class-init-parent-only" to mapOf(0 to int("1")),
        )
        assertEquals(Fixtures.curated.map { it.id }.toSet(), expected.keys, "every curated fixture has an expectation")
        return Fixtures.curated.map { fixture ->
            DynamicTest.dynamicTest(fixture.id) {
                val result = CORE.run(fixture)
                assertEquals(expected.getValue(fixture.id), result.registers)
                assertFalse(result.aborted)
                assertEquals(emptyList<String>(), result.unhandledExceptions)
            }
        }
    }

    @TestFactory
    fun regressionsKeepTheirSpecifiedOutcomes(): List<DynamicTest> = Fixtures.regressions.map { entry ->
        DynamicTest.dynamicTest(entry.fixture.id) {
            val actual = CORE.run(entry.fixture)
            val message = entry.basis
            assertEquals(entry.aborted, actual.aborted, message)
            assertTrue(actual.reachedAddresses.isNotEmpty(), message)
            entry.registers.forEach { (register, expected) ->
                assertEquals(expected, actual.registers[register], "$message register $register")
            }
            entry.caughtTypes.forEach { (register, expected) ->
                assertEquals(ValueKind.KNOWN, actual.registers[register]?.kind, message)
                assertEquals(expected, actual.registers[register]?.type, message)
                assertEquals(emptyList<String>(), actual.unhandledExceptions, message)
            }
            assertTrue(actual.reachedAddresses.containsAll(entry.reached), message)
            assertTrue(entry.notReached.none { it in actual.reachedAddresses }, message)
            entry.exactReached?.let { assertEquals(it, actual.reachedAddresses, message) }
            entry.escapes?.let { assertEquals(it, actual.unhandledExceptions.toSet(), message) }
        }
    }

    @Test
    fun aCalleeThrowWithNoHandlerEscapesTheCaller() {
        val result = CORE.run(Fixture("invoke-escaping-throw", "Ldifferential/invocation;", "callEscapingThrow()V", emptySet()))
        assertEquals(
            setOf("java.lang.ArithmeticException"),
            result.unhandledExceptions.map { it.substringBefore(':') }.toSet(),
        )
        assertFalse(result.aborted)
    }

    @Test
    fun abortedCalleeFallbackCollapsesTheHeldArrayContentsOnTheCore() {
        val graph = CoreTester.execute("Ldifferential/invocation;", "callAbortedArrayCallee()[I")
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        val argument = graph.explored.terminatingConsensus(1) as Value.Known
        assertTrue(argument.raw is HeapRef, "opaque fallback retains the argument reference")
        val array = graph.explored.terminatingHeapObject(argument.raw as HeapRef).agreed() as HeapObject.TrackedArray
        assertEquals(Value.Unknown(VirtualType.of("I")), array.element(0))
        assertEquals(Value.Unknown(VirtualType.of("[I")), graph.explored.terminatingConsensus(3))
    }

    @Test
    fun opaqueCollapseAssertsTheHeldRefAndItsContentsWithoutAssumingDenseHeapIds() {
        val graph = CoreTester.execute(
            "Ldifferential/invocation;",
            "opaqueCollapseKeepsReference()Ldifferential/invocation_target;",
        )
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        val held = graph.explored.terminatingConsensus(0) as Value.Known
        assertTrue(held.raw is HeapRef, "the caller must retain its exact reference: $held")
        val obj = graph.explored.terminatingHeapObject(held.raw as HeapRef).agreed() as HeapObject.TrackedObject
        assertEquals(
            Value.Unknown(VirtualType.of("I")),
            obj.field("Ldifferential/invocation_target;->value:I"),
            "opaque collapse changes reachable contents, not the held ref",
        )
    }

    @Test
    fun entryPointTruncationKeepsExploredEvidenceButDeniesTrustedOutput() {
        val graph = CoreTester.execute("Ldifferential/invocation;", "entryPointTruncatesAfterCall(I)I")
        assertTrue(graph.completion() is CompletionStatus.Partial)
        assertNull(graph.trusted(), "a truncated core run cannot expose trusted output")
        assertEquals(Value.Known(11, VirtualType.of("I")), graph.explored.terminatingConsensus(0))
        assertEquals(setOf(0, 3, 4, 6, 7), graph.addresses().toSet())
    }

    @Test
    fun anUnhandledSynthesizedGuestThrowableProjectsItsMessageToo() {
        val divByZero = Fixture(
            id = "div-int-by-zero-unhandled",
            className = "Lbinary_math_test;",
            methodDescriptor = "divInt()V",
            seededRegisters = mapOf(0 to (12 to "I"), 1 to (0 to "I")),
        )
        assertEquals(listOf("java.lang.ArithmeticException:/ by zero"), CORE.run(divByZero).unhandledExceptions)
    }

    @Test
    fun anAmbiguousLocalDivideProjectsTheThrowingArmsMessage() {
        val fixture = Fixture(
            id = "ambiguous-local-divide",
            className = "Ldifferential/exceptions;",
            methodDescriptor = "ambiguousLocalDivide(I)V",
            observeRegisters = emptySet(),
        )
        assertEquals(listOf("java.lang.ArithmeticException:/ by zero"), CORE.run(fixture).unhandledExceptions)
    }

    @Test
    fun typeTagsAreProjectedVerbatimFromTheDescriptor() {
        val expected = mapOf(
            "returnWideSixteen()J" to NormalizedValue(ValueKind.KNOWN, "J", "42"),
            "returnWideThirtyTwo()J" to NormalizedValue(ValueKind.KNOWN, "J", "1800000"),
            "returnWideHighSixteen()J" to NormalizedValue(ValueKind.KNOWN, "J", "4774378554966147072"),
            "constThirtyTwo()I" to NormalizedValue(ValueKind.KNOWN, "I", "1111638594"),
            "constHighSixteen()I" to NormalizedValue(ValueKind.KNOWN, "I", "1111621632"),
        )
        for ((descriptor, value) in expected) {
            val fixture = Fixture(id = "tag-$descriptor", className = "Lspine/spine;", methodDescriptor = descriptor)
            assertEquals(value, CORE.run(fixture).registers[0], descriptor)
        }
    }

    @Test
    fun aBackwardBranchLoopTruncatesOnlyItsOwnPathAndItsSiblingSurvives() {
        val view = CoreTester.classIndex.requireMethodView("Ldifferential/branch;", "loopWithNonLoopingSibling(I)I")
        val graph = CoreTester.vmServices.newSession(view).run()

        val completion = graph.completion()
        assertTrue(completion is CompletionStatus.Partial, "an unbounded loop cannot be fully explored: $completion")
        assertTrue(
            (completion as CompletionStatus.Partial).reasons.contains(AbortReason.ADDRESS_LIMIT),
            "the truncation is the per-address visit budget, not a step/time limit: ${completion.reasons}",
        )
        assertNull(graph.trusted(), "a truncated run must not be trustable")
        assertTrue(
            graph.outcomes.any { it == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) },
            "the looping path itself must abort on the visit limit: ${graph.outcomes}",
        )
        assertTrue(
            graph.outcomes.any { it is Outcome.Returned && (it.value as? Value.Known)?.raw == 0x2a },
            "the non-looping sibling (return 42) must survive the loop's truncation: ${graph.outcomes}",
        )
    }

    @Test
    fun theSynthesizedNpesHandlerReachabilityIsIndependentOfThePrimitiveRegistersValue() {
        fun run(descriptor: String) = CORE.run(
            Fixture(id = descriptor, className = "Ldifferential/smoke;", methodDescriptor = descriptor),
        )
        val nullIdiom = run("throwNullIdiomFourClauses()Ljava/lang/Object;")
        val nonZero = run("throwNonZeroConstFourClauses()Ljava/lang/Object;")

        val view = CoreTester.classIndex.requireMethodView(
            "Ldifferential/smoke;", "throwNullIdiomFourClauses()Ljava/lang/Object;",
        )
        val clauses = view.tryBlocks.flatMap { it.catches }
        val npeHandler = clauses.single {
            it.exceptionType == VirtualType.of("Ljava/lang/Exception;")
        }.handlerAddress
        val throwAddress = view.addresses.single { view.opcodeAt(it) == Opcode.THROW }
        assertEquals(
            setOf(view.codeAddressAtInstructionIndex(0), throwAddress, npeHandler, view.nextAddress(npeHandler)),
            nullIdiom.reachedAddresses,
            "only the clause that catches an NPE runs",
        )
        val deadHandlerAddresses = clauses.filter { it.handlerAddress != npeHandler }.flatMap {
            listOf(it.handlerAddress, view.nextAddress(it.handlerAddress))
        }
        assertTrue(
            deadHandlerAddresses.none { it in nullIdiom.reachedAddresses },
            "the IOException, Error and IllegalStateException blocks are dead for an NPE: ${nullIdiom.reachedAddresses}",
        )
        assertEquals(emptyList<String>(), nullIdiom.unhandledExceptions, "nothing escapes a definite catch")
        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "Ljava/lang/NullPointerException;", "java.lang.NullPointerException"),
            nullIdiom.registers[0],
            "move-exception binds a fresh, message-less NullPointerException",
        )
        assertEquals(nullIdiom, nonZero, "0x5 and 0x0 project identically; the value is irrelevant")
    }

    @Test
    fun payloadPseudoAddressesAreExcludedFromReachedSets() {
        val fixtures = listOf(
            Fixture(
                "fill-array-data-payload-projection", "Lfill_array_data_test;", "fillArrayDataInt()V",
                emptySet(), mapOf(0 to (IntArray(5) to "[I")),
            ),
            Fixture("switch-payload-projection", "Ldifferential/switch;", "packedKnownMatch()I"),
        )
        for (fixture in fixtures) {
            val view = CoreTester.classIndex.requireMethodView(fixture.className, fixture.methodDescriptor)
            val payloads = view.payloadAddresses.toSet()
            assertTrue(payloads.isNotEmpty(), "${fixture.id} must exercise a payload")
            val result = CORE.run(fixture)
            assertFalse(result.aborted)
            assertTrue(payloads.none { it in result.reachedAddresses })
        }
    }

    @Test
    fun returnedOutcomesPreserveValuesNullsAndRuntimeReferenceTypes() {
        fun curated(id: String) = CORE.run(Fixtures.curated.single { it.id == id })
        // compareWide returns v4, so reading v0 would report the first long operand.
        assertEquals(NormalizedValue(ValueKind.KNOWN, "I", "-1"), curated("ratchet-compare-wide").returnValue)
        assertNull(curated("spine-return-nothing").returnValue, "return-void has no value despite v0 holding 7")
        assertEquals(
            NormalizedValue(ValueKind.NULL, "Ljava/lang/Object;", null),
            curated("smoke-null-element-from-fresh-array").returnValue,
        )
        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "java.lang.String", "great maker"),
            curated("types-cast-string-to-object").returnValue,
            "the device records the returned object's runtime class, not its widened Object bound",
        )
        val escaping = CORE.run(Fixtures.regressions.single { it.fixture.id == "spine-throw-uncaught" }.fixture)
        assertNull(escaping.returnValue, "an escaping throw must not fabricate a return")
    }

    private companion object {
        private val CORE = CoreProjection()
    }
}
