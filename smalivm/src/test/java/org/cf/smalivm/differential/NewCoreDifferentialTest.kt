package org.cf.smalivm.differential

import org.cf.smalivm.core.AbortReason
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.agreed
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NewCoreDifferentialTest {

    @Test
    fun curatedFixturesHaveIndependentlySpecifiedRegisterResults() {
        // Constants come from the smali bodies, arithmetic from Dalvik/JLS rules. In particular,
        // unknown branch inputs join their arms; they are not assigned a sampled implementation result.
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
            "branch-disagreeing-arms" to mapOf(1 to absent, 2 to unknown("I")),
            "smoke-return-five" to mapOf(0 to int("5")),
            "ratchet-compare-wide" to mapOf(4 to int("-1")),
            "ratchet-array-round-trip" to mapOf(2 to int("7")),
            "ratchet-branch-on-zero" to mapOf(0 to int("0")),
            "branch-returns-different-constants" to mapOf(0 to unknown("I")),
            "branch-known-falls-through" to mapOf(0 to int("42")),
            "switch-packed-known-match" to mapOf(0 to int("22")),
            "switch-packed-known-no-match" to mapOf(0 to int("99")),
            "switch-sparse-known-match" to mapOf(0 to int("22")),
            "switch-unknown-selector-forks" to mapOf(0 to int("7")),
            "types-array-arms-join-covariantly" to mapOf(0 to unknown("[Ljava/lang/Object;")),
            "types-cast-string-to-object" to mapOf(0 to known("Ljava/lang/Object;", "great maker")),
            "types-const-class-object" to mapOf(0 to known("Ljava/lang/Class;", "class java.lang.Object")),
            "types-known-instance-of-object" to mapOf(0 to known("Z", "true")),
            "smoke-nested-array" to mapOf(0 to known("[[I", "[[1, 2]]")),
            "smoke-self-referential-array" to mapOf(0 to known("[Ljava/lang/Object;", "[[...]]")),
            "smoke-shared-inner-array" to mapOf(0 to known("[[I", "[[7], [7]]")),
            "smoke-null-element-from-fresh-array" to mapOf(2 to NormalizedValue(ValueKind.NULL, "Ljava/lang/Object;", null)),
            "smoke-fill-array-data-round-trip" to mapOf(0 to known("[I", "[1, 2, 3]")),
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
            "spine-add-int" to mapOf(0 to int("7"), 1 to int("4")),
            "new-instance-local" to mapOf(0 to known("Lnew_instance_test;", "Uninitialized Lnew_instance_test;")),
            "new-instance-framework-class" to mapOf(0 to known("Ljava/lang/Integer;", "Uninitialized Ljava/lang/Integer;")),
            "new-instance-two-allocations" to mapOf(
                0 to known("Lnew_instance_test;", "Uninitialized Lnew_instance_test;"),
                1 to known("Lnew_instance_test;", "Uninitialized Lnew_instance_test;"),
            ),
            "new-instance-triggers-class-init" to mapOf(0 to known("Lclass_init/witness;", "Uninitialized Lclass_init/witness;")),
            "ratchet-invoke-static" to mapOf(0 to int("0")),
            "ratchet-new-instance" to mapOf(0 to known("Ljava/lang/Object;", "<instance java.lang.Object>")),
            "smoke-new-object" to mapOf(0 to known("Ljava/lang/Object;", "<instance java.lang.Object>")),
            "ratchet-alias-unrelated-allocation" to mapOf(
                0 to known("Ldifferential/ratchet;", "Uninitialized Ldifferential/ratchet;"),
                1 to known("Ldifferential/ratchet;", "<instance differential.ratchet>"),
            ),
            "ratchet-static-field-round-trip" to mapOf(0 to int("5")),
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
        for (fixture in Fixtures.newCoreSupported) {
            val result = NEW_CORE.run(fixture)
            assertEquals(expected.getValue(fixture.id), result.registers, fixture.id)
            assertFalse(result.aborted, fixture.id)
            assertTrue(result.unhandledExceptions.isEmpty(), fixture.id)
        }
    }

    @Test
    fun callOutcomeFixturesKeepReturnedHandledEscapingAndAbortedPartitionsSeparate() {
        val returned = Fixture("probe-returned", "Ldifferential/invocation;", "callStatic()I")
        val handled = Fixture("probe-handled", "Ldifferential/invocation;", "callHandledThrow()I")
        val escaping = Fixture("probe-escaping", "Ldifferential/invocation;", "callEscapingThrow()V", emptySet())
        val aborted = Fixtures.regressions.single { it.fixture.id == "invoke-aborted-callee-fallback" }.fixture

        val expectedReturned = NormalizedValue(ValueKind.KNOWN, "I", "11")
        val expectedHandled = NormalizedValue(ValueKind.KNOWN, "I", "22")
        val expectedFallback = NormalizedValue(ValueKind.UNKNOWN, "[I", null)
        run {
            val vm = NEW_CORE
            val returnedResult = vm.run(returned)
            assertEquals(expectedReturned, returnedResult.registers[0], "${vm.name}: returned partition")
            assertEquals(emptyList<String>(), returnedResult.unhandledExceptions)
            assertFalse(returnedResult.aborted)

            val handledResult = vm.run(handled)
            assertEquals(expectedHandled, handledResult.registers[0], "${vm.name}: handled-throw partition")
            assertEquals(emptyList<String>(), handledResult.unhandledExceptions)
            assertFalse(handledResult.aborted)

            val escapingResult = vm.run(escaping)
            assertEquals(
                setOf("java.lang.ArithmeticException"),
                escapingResult.unhandledExceptions.map { it.substringBefore(':') }.toSet(),
                "${vm.name}: escaping-throw partition",
            )
            assertFalse(escapingResult.aborted)

            val abortedResult = vm.run(aborted)
            assertEquals(expectedFallback, abortedResult.registers[3], "${vm.name}: aborted-callee fallback")
            assertEquals(emptyList<String>(), abortedResult.unhandledExceptions)
            assertTrue(abortedResult.aborted, "${vm.name}: fallback must not hide truncation")
        }
    }

    @Test
    fun abortedCalleeFallbackCollapsesTheHeldArrayContentsOnTheCore() {
        val graph = CoreTester.execute("Ldifferential/invocation;", "callAbortedArrayCallee()[I")
        assertEquals(CompletionStatus.Partial(setOf(org.cf.smalivm.core.AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        val argument = graph.explored.terminatingConsensus(1) as Value.Known
        assertTrue(argument.raw is HeapRef, "opaque fallback retains the argument reference")
        val array = graph.explored.terminatingHeapObject(argument.raw as HeapRef).agreed() as org.cf.smalivm.core.HeapObject.TrackedArray
        assertEquals(Value.Unknown(VirtualType.of("I")), array.element(0))
        assertEquals(Value.Unknown(VirtualType.of("[I")), graph.explored.terminatingConsensus(3))
    }

    @Test
    fun resolutionFailureRowsPinExactCoreGuestErrorsAndDeadContinuations() {
        data class Row(
            val id: String,
            val descriptor: String,
            val escapedType: String,
            val normalizedEscape: String,
        )

        val rows = listOf(

            Row(
                "invoke-missing-class",
                "missingClass()V",
                "Ljava/lang/NoClassDefFoundError;",
                "java.lang.NoClassDefFoundError:differential.absent",
            ),

            Row(
                "invoke-missing-method",
                "missingMethod()V",
                "Ljava/lang/NoSuchMethodError;",
                "java.lang.NoSuchMethodError:missing()V",
            ),
        )

        for (row in rows) {
            val graph = CoreTester.execute("Ldifferential/invocation;", row.descriptor)
            assertEquals(CompletionStatus.Complete, graph.completion(), "${row.id}: guest error completes")
            assertEquals(listOf(row.escapedType), CoreTester.escapingTypes(graph), "${row.id}: exact guest Error")
            val reached = graph.addresses().filter(graph::wasReached).toSet()
            assertEquals(setOf(0), reached, "${row.id}: only the invoke executes")
            assertFalse(graph.wasReached(3), "${row.id}: the infeasible return continuation stays dead")
            assertEquals(Value.Top, graph.explored.terminatingConsensus(0), "${row.id}: no return state")

            val fixture = Fixtures.invocationCases.single { it.id == row.id }
            val projected = NEW_CORE.run(fixture)
            assertFalse(projected.aborted, "${row.id}: guest linking errors are outcomes, not truncation")
            assertEquals(setOf(0), projected.reachedAddresses)
            assertEquals(listOf(row.normalizedEscape), projected.unhandledExceptions)
            assertEquals(
                mapOf(0 to NormalizedValue(ValueKind.ABSENT, null, null)),
                projected.registers,
                "${row.id}: no fabricated register result",
            )
        }
    }

    @Test
    fun opaqueCollapseAssertsTheHeldRefAndItsContentsWithoutAssumingDenseHeapIds() {
        val graph = CoreTester.execute(
            "Ldifferential/invocation;",
            "opaqueCollapseKeepsReference()Ldifferential/invocation_target;",
        )
        assertEquals(CompletionStatus.Partial(setOf(org.cf.smalivm.core.AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        val held = graph.explored.terminatingConsensus(0) as Value.Known
        assertTrue(held.raw is HeapRef, "the caller must retain its exact reference: $held")
        val obj = graph.explored.terminatingHeapObject(held.raw as HeapRef).agreed() as org.cf.smalivm.core.HeapObject.TrackedObject
        assertEquals(
            Value.Unknown(VirtualType.of("I")),
            obj.field("Ldifferential/invocation_target;->value:I"),
            "opaque collapse changes reachable contents, not the held ref",
        )

    }

    @Test
    fun entryPointTruncationKeepsExploredEvidenceButDeniesTrustedOutput() {
        val graph = CoreTester.execute(
            "Ldifferential/invocation;",
            "entryPointTruncatesAfterCall(I)I",
        )
        assertTrue(graph.completion() is CompletionStatus.Partial)
        assertNull(graph.trusted(), "a truncated core run cannot expose trusted output")
        assertEquals(Value.Known(11, VirtualType.of("I")), graph.explored.terminatingConsensus(0))
        assertEquals(setOf(0, 3, 4, 6, 7), graph.addresses().toSet())

        val fixture = Fixtures.invocationCases.single { it.id == "invoke-entrypoint-truncation" }
        val result = NEW_CORE.run(fixture)
        assertTrue(result.aborted)
        assertEquals(NormalizedValue(ValueKind.KNOWN, "I", "11"), result.registers[0])
        assertEquals(setOf(0, 3, 4, 6, 7), result.reachedAddresses)
    }

    @Test
    fun unhandledExceptionsAreProjectedInTheDocumentedFormat() {

        val uncaught = Fixtures.regressions.first { it.fixture.id == "spine-throw-uncaught" }.fixture
        assertEquals(listOf("java.lang.RuntimeException:null"), NEW_CORE.run(uncaught).unhandledExceptions)

        val twoHandlers = Fixtures.regressions.first { it.fixture.id == "spine-throw-two-handlers" }.fixture
        val entries = NEW_CORE.run(twoHandlers).unhandledExceptions
        assertEquals(listOf("java.lang.Object:null"), entries)
        assertTrue(entries.none { it.contains('/') || it.startsWith("L") }, "binary names, not descriptors: $entries")
    }

    @Test
    fun anUnhandledSynthesizedGuestThrowableProjectsItsMessageToo() {

        val divByZero = Fixture(
            id = "unit-only-div-int-by-zero-unhandled",
            className = "Lbinary_math_test;",
            methodDescriptor = "divInt()V",
            seededRegisters = mapOf(0 to (12 to "I"), 1 to (0 to "I")),
        )
        assertEquals(listOf("java.lang.ArithmeticException:/ by zero"), NEW_CORE.run(divByZero).unhandledExceptions)
    }

    @Test
    fun anAmbiguousLocalDivideProjectsTheThrowingArmsMessage() {

        val fixture = Fixture(
            id = "unit-only-ambiguous-local-divide",
            className = "Ldifferential/exceptions;",
            methodDescriptor = "ambiguousLocalDivide(I)V",
            observeRegisters = emptySet(),
        )
        assertEquals(
            listOf("java.lang.ArithmeticException:/ by zero"),
            NEW_CORE.run(fixture).unhandledExceptions,
        )
    }

    @Test
    fun typeTagsAreProjectedVerbatimFromTheDescriptor() {

        val expected = mapOf(
            "returnWide()J" to NormalizedValue(ValueKind.KNOWN, "J", "4886718345"),
            "returnWideSixteen()J" to NormalizedValue(ValueKind.KNOWN, "J", "42"),
            "returnWideThirtyTwo()J" to NormalizedValue(ValueKind.KNOWN, "J", "1800000"),
            "returnWideHighSixteen()J" to NormalizedValue(ValueKind.KNOWN, "J", "4774378554966147072"),
            "constThirtyTwo()I" to NormalizedValue(ValueKind.KNOWN, "I", "1111638594"),
            "constHighSixteen()I" to NormalizedValue(ValueKind.KNOWN, "I", "1111621632"),
            "returnString()Ljava/lang/String;" to
                NormalizedValue(ValueKind.KNOWN, "Ljava/lang/String;", "spine"),
        )
        for ((descriptor, value) in expected) {
            val fixture = Fixture(id = "tag-$descriptor", className = "Lspine/spine;", methodDescriptor = descriptor)
            assertEquals(value, NEW_CORE.run(fixture).registers[0], "new-core tag for $descriptor")

        }
    }

    @Test
    fun forkedReturnPathsProjectTheirJoinedConsensus() {

        val forking = Fixtures.newCoreSupported.first { it.id == "branch-returns-different-constants" }
        assertEquals(
            NormalizedValue(ValueKind.UNKNOWN, "I", null),
            NEW_CORE.run(forking).registers[0],
            "two disagreeing returning paths must project their joined consensus",
        )
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

        assertNull(graph.trusted(), "a truncated run must not be trustable -- canTrustConsensus is false on Partial")

        assertTrue(
            graph.outcomes.any { it == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) },
            "the looping path itself must abort on the visit limit: ${graph.outcomes}",
        )
        assertTrue(
            graph.outcomes.any { it is Outcome.Returned && (it.value as? Value.Known)?.raw == 0x2a },
            "the NON-looping sibling (return 42) must survive the loop's truncation: ${graph.outcomes}",
        )
    }

    @Test
    fun aJoinedTopAndAnUnassignedRegisterNoLongerProjectAsTheSameObservable() {

        val fixture = Fixture(
            id = "unit-only-branch-disagreeing-arms",
            className = "Ldifferential/branch;",
            methodDescriptor = "disagreeingArms(I)V",
            observeRegisters = setOf(0, 1, 2),
        )
        val core = NEW_CORE.run(fixture)

        assertEquals(NormalizedValue(ValueKind.CONFLICTED, null, null), core.registers[0])
        assertEquals(NormalizedValue(ValueKind.ABSENT, null, null), core.registers[1])
        assertNotEquals(
            core.registers[0], core.registers[1],
            "the two meanings of Top must not project identically -- that is the false-green this closes",
        )

        assertEquals(NormalizedValue(ValueKind.UNKNOWN, "I", null), core.registers[2])

        val held = NEW_CORE.run(
            Fixtures.regressions.first { it.fixture.id == "branch-move-from-untouched-register" }.fixture,
        )
        assertEquals(NormalizedValue(ValueKind.CONFLICTED, null, null), held.registers[0])
        assertEquals(NormalizedValue(ValueKind.ABSENT, null, null), held.registers[1])

    }

    @Test
    fun anUninitializedInstanceRetainsItsDescriptor() {

        val expected = NormalizedValue(ValueKind.KNOWN, "Lnew_instance_test;", "Uninitialized Lnew_instance_test;")
        val local = Fixtures.newCoreSupported.first { it.id == "new-instance-local" }
        assertEquals(expected, NEW_CORE.run(local).registers[0], "new-core repr for a fresh allocation")

        assertTrue(
            NEW_CORE.run(local).registers[0]!!.repr!!.startsWith("Uninitialized L"),
            "the uninitialized form carries the DESCRIPTOR, rather than an identity hash",
        )

        val framework = Fixtures.newCoreSupported.first { it.id == "new-instance-framework-class" }
        val frameworkExpected =
            NormalizedValue(ValueKind.KNOWN, "Ljava/lang/Integer;", "Uninitialized Ljava/lang/Integer;")
        assertEquals(frameworkExpected, NEW_CORE.run(framework).registers[0])

    }

    @Test
    fun initializingOneAllocationLeavesTheOtherUninitialized() {

        val uninitializedRatchet = "Uninitialized Ldifferential/ratchet;"
        val local = NEW_CORE.run(
            Fixtures.newCoreSupported.first { it.id == "ratchet-alias-unrelated-allocation" },
        )
        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "Ldifferential/ratchet;", uninitializedRatchet),
            local.registers[0],
            "the UNRELATED allocation is untouched: the type-equality rewrite did not reach an ancestor state",
        )
        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "Ldifferential/ratchet;", "<instance differential.ratchet>"),
            local.registers[1],
            "...while the one that WAS initialized became a real instance, so the fixture does drive the transition",
        )
        val throughEmulatedObjectInit = NEW_CORE.run(
            Fixture(
                id = "unit-only-alias-through-emulated-object-init",
                className = "Ldifferential/ratchet;",
                methodDescriptor = "initializeOneOfTwoObjectAllocations()V",
                observeRegisters = setOf(0, 1),
            ),
        )
        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "Ljava/lang/Object;", "Uninitialized Ljava/lang/Object;"),
            throughEmulatedObjectInit.registers[0],
            "nor through Object construction",
        )
        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "Ljava/lang/Object;", "<instance java.lang.Object>"),
            throughEmulatedObjectInit.registers[1],
        )
    }

    @Test
    fun adjudicatedRegressionsKeepTheirSemanticExpectations() {
        for (entry in Fixtures.regressions) {
            val actual = NEW_CORE.run(entry.fixture)
            val message = "${entry.fixture.id}: ${entry.basis}"
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
    fun aHeapBackedGuestThrowableProjectsItsTypeAndMessage() {

        val fixture = Fixtures.newCoreSupported.first { it.id == "binary-div-int-by-zero-caught" }
        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "Ljava/lang/ArithmeticException;", "java.lang.ArithmeticException: / by zero"),
            NEW_CORE.run(fixture).registers[0],
        )

    }

    @Test
    fun aSelfReferentialArrayRendersItsCycleGuard() {

        val fixture = Fixtures.newCoreSupported.first { it.id == "smoke-self-referential-array" }
        val expected = NormalizedValue(ValueKind.KNOWN, "[Ljava/lang/Object;", "[[...]]")
        assertEquals(expected, NEW_CORE.run(fixture).registers[0])

    }

    @Test
    fun anArrayReachableFromTwoSiblingSlotsRendersFullyBothTimes() {

        val fixture = Fixtures.newCoreSupported.first { it.id == "smoke-shared-inner-array" }
        val expected = NormalizedValue(ValueKind.KNOWN, "[[I", "[[7], [7]]")
        assertEquals(expected, NEW_CORE.run(fixture).registers[0], "the core must render the inner array twice")

        val inner = intArrayOf(7)
        assertEquals(
            "[[7], [7]]", ResultProjection.reprOf(arrayOf(inner, inner)),
            "one host array in two sibling slots renders fully both times -- `visiting.remove` in the finally",
        )

        val selfReferential = arrayOfNulls<Any>(1)
        selfReferential[0] = selfReferential
        assertEquals("[[...]]", ResultProjection.reprOf(selfReferential), "and a real cycle still collapses")
    }

    @Test
    fun arrayWritesEscapeOnlyFeasibleExceptions() {

        val aioobe = "java.lang.ArrayIndexOutOfBoundsException:null"

        val unknownValue = Fixtures.regressions.first { it.fixture.id == "smoke-aput-unknown-value" }.fixture

        assertEquals(
            emptyList<String>(), NEW_CORE.run(unknownValue).unhandledExceptions,
            "a provably non-null array at a provable index can throw NEITHER, so the core forks nothing",
        )

        val unknownIndex = Fixtures.regressions.first { it.fixture.id == "smoke-aput-unknown-index" }.fixture

        assertEquals(
            listOf(aioobe), NEW_CORE.run(unknownIndex).unhandledExceptions,
            "an unprovable index makes the bounds exception feasible -- but the array is still provably non-null",
        )

        val fna = Fixtures.regressions
            .first { it.fixture.id == "smoke-filled-new-array-unknown-element" }.fixture

        assertEquals(emptyList<String>(), NEW_CORE.run(fna).unhandledExceptions)
    }

    @Test
    fun aReferenceComponentFilledNewArraySpellsItsNullElementAsDalvikReadsIt() {
        val fixture = Fixtures.regressions
            .first { it.fixture.id == "smoke-filled-new-array-reference-component" }.fixture
        val core = NEW_CORE.run(fixture).registers[2]
        assertEquals(
            "[Ljava/lang/String;", core?.type,
            "the result type is the instruction's reference-array descriptor",
        )

        assertEquals(
            "[x, null]", core?.repr,
            "a primitive-written element register contributes Dalvik's null, not its raw",
        )

    }

    @Test
    fun theSynthesizedNpesHandlerReachabilityIsIndependentOfThePrimitiveRegistersValue() {

        fun run(descriptor: String) = NEW_CORE.run(
            Fixture(id = "unit-only-$descriptor", className = "Ldifferential/smoke;", methodDescriptor = descriptor)
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
        val throwAddress = view.addresses.single { view.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.THROW }
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
            "the IOException, Error and IllegalStateException blocks are DEAD for an NPE: ${nullIdiom.reachedAddresses}",
        )

        assertEquals(emptyList<String>(), nullIdiom.unhandledExceptions, "and nothing escapes a definite catch")

        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "Ljava/lang/NullPointerException;", "java.lang.NullPointerException"),
            nullIdiom.registers[0],
            "move-exception must bind a fresh, message-less NullPointerException",
        )

        assertEquals(nullIdiom, nonZero, "0x5 and 0x0 must project identically -- the value is irrelevant")
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
            val result = NEW_CORE.run(fixture)
            assertFalse(result.aborted)
            assertTrue(payloads.none { it in result.reachedAddresses })
        }
    }

    @Test
    fun returnedOutcomesPreserveValuesNullsAndRuntimeReferenceTypes() {
        // compareWide returns v4, so reading v0 would incorrectly report the first long operand.
        val compare = NEW_CORE.run(Fixtures.newCoreSupported.single { it.id == "ratchet-compare-wide" })
        assertEquals(NormalizedValue(ValueKind.KNOWN, "I", "-1"), compare.returnValue)
        val noValue = NEW_CORE.run(Fixtures.newCoreSupported.single { it.id == "spine-return-nothing" })
        assertNull(noValue.returnValue, "return-void has no value despite v0 holding 7")
        val nullValue = NEW_CORE.run(Fixtures.newCoreSupported.single { it.id == "smoke-null-element-from-fresh-array" })
        assertEquals(NormalizedValue(ValueKind.NULL, "Ljava/lang/Object;", null), nullValue.returnValue)
        val widened = NEW_CORE.run(Fixtures.newCoreSupported.single { it.id == "types-cast-string-to-object" })
        assertEquals(
            NormalizedValue(ValueKind.KNOWN, "java.lang.String", "great maker"), widened.returnValue,
            "the device records the returned object's runtime class, not its widened Object bound",
        )
        val escaping = NEW_CORE.run(Fixtures.regressions.single { it.fixture.id == "spine-throw-uncaught" }.fixture)
        assertNull(escaping.returnValue, "an escaping throw must not fabricate a return")
        assertEquals(listOf("java.lang.RuntimeException:null"), escaping.unhandledExceptions)
    }

    private companion object {

        private val NEW_CORE = NewCoreVm()
    }
}
