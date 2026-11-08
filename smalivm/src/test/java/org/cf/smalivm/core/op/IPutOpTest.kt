package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.agreed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `iput*`. Expected behaviour follows `docs/DALVIK-SEMANTICS.md#fields`; a write read back through `iget` is
 * covered by `IGetOpTest.aReadReturnsTheResolvedFieldsCell`.
 */
class IPutOpTest {

    /**
     * ART's `GetFieldValue` casts the 32-bit register to the field's width before storing
     * (`docs/DALVIK-SEMANTICS.md#stores-narrow-to-the-field-width`), so `0x1ff` stored to a byte reads back as -1.
     * A boolean byte other than 0 or 1 is real on a device but has no `Boolean` raw, so it is `Unknown(Z)`.
     */
    @TestFactory
    fun aNarrowStoreKeepsOnlyItsFieldsWidth(): List<DynamicTest> = listOf(
        narrowStore("putByte(B)B", 0x1ff, Value.Known((-1).toByte(), type("B"))),
        narrowStore("putShort(S)S", 0x18000, Value.Known((-32768).toShort(), type("S"))),
        narrowStore("putChar(C)C", 0x10041, Value.Known('A', type("C"))),
        narrowStore("putBoolean(Z)Z", 0x101, Value.Known(true, type("Z"))),
        narrowStore("putBoolean(Z)Z", 2, Value.Unknown(type("Z"))),
    )

    @Test
    fun aliasesShareOneFieldCell() {
        CoreTester.assertReturns(
            CLASS_NAME,
            "putThroughAliasReadThroughOriginal()I",
            Value.Known(0x42, type("I")),
            register = 3,
        )
    }

    @Test
    fun distinctObjectIdsKeepDistinctFieldCells() {
        val graph = CoreTester.execute(CLASS_NAME, "putIntoTwoObjectsKeepsCellsDistinct()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()!!
        assertEquals(Value.Known(1, type("I")), trusted.terminatingConsensus(3))
        assertEquals(Value.Known(2, type("I")), trusted.terminatingConsensus(4))
    }

    @Test
    fun forkedWritesAreIsolatedAndJoinToUnknown() {
        val graph = CoreTester.execute(CLASS_NAME, "putIntBranched(I)I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()!!
        assertEquals(Value.Unknown(type("I")), trusted.terminatingConsensus(2))
        val receiver = trusted.terminatingConsensus(0) as Value.Known
        val objectConsensus = trusted.terminatingHeapObject(receiver.raw as HeapRef).agreed()
        assertTrue(objectConsensus is HeapObject.TrackedObject)
        assertEquals(
            Value.Unknown(type("I")),
            (objectConsensus as HeapObject.TrackedObject).field("Liput_test;->myInt:I"),
            "the same object id survives both paths, but its per-path field values join",
        )
    }

    /** A literal zero and a primitive-tagged register both read as a null receiver. */
    @TestFactory
    fun aStoreThroughANullReceiverThrowsTheOnlyOutcome(): List<DynamicTest> =
        listOf("putIntoNullReceiver()V", "putIntoPrimitiveTaggedReceiver()V").map { method ->
            DynamicTest.dynamicTest(method) {
                // ART's `ThrowNullPointerExceptionForFieldAccess` text over the resolved field and the running method.
                CoreTester.testThrows(
                    CLASS_NAME, method,
                    exceptionType = "Ljava/lang/NullPointerException;",
                    message = "Attempt to write to field 'int iput_test.myInt' on a null object reference in method " +
                        "'void iput_test.${method.substringBefore('(')}()'",
                )
                val graph = CoreTester.execute(CLASS_NAME, method)
                assertEquals(CompletionStatus.Complete, graph.completion())
                assertEquals(
                    listOf("Ljava/lang/NullPointerException;"),
                    CoreTester.escapingTypes(graph),
                    "the null store has one feasible outcome; testThrows alone cannot exclude a sibling",
                )
                assertFalse(graph.wasReached(5), "a definite NPE must not continue or perform a store")
                assertEquals(0, graph.nodePile(3).single().children.size, "a definite escaping throw has no sibling")
            }
        }

    /**
     * Resolution precedes the receiver null check, and a static-only declaration is absent from instance
     * resolution (`docs/DALVIK-SEMANTICS.md#resolution`). A failed resolution writes nothing.
     */
    @TestFactory
    fun aResolutionFailureThrowsBeforeTheStore(): List<DynamicTest> = listOf(
        resolutionFailure("putMissingClassOnNullReceiver()V", "Ljava/lang/NoClassDefFoundError;", putAt = 3, nextAt = 5),
        resolutionFailure("putMissingMember()V", "Ljava/lang/NoSuchFieldError;", putAt = 4, nextAt = 6),
        resolutionFailure("putStaticFieldWithInstanceOpcode()V", "Ljava/lang/NoSuchFieldError;", putAt = 4, nextAt = 6),
    )

    @Test
    fun anUnknownReceiverPreservesNormalAndNullOutcomesWithoutInventingAFieldTarget() {
        val graph = CoreTester.execute(CLASS_NAME, "putThroughUnknownReceiver(Liput_test;I)V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
        assertTrue(graph.outcomes.contains(Outcome.Returned(null)))
        // No target is provable, so the continuation records STRONG rather than inventing a target.
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0))
        assertEquals(setOf(0, 1), graph.registersRead(0))
        assertEquals(emptySet<Int>(), graph.registersAssigned(0))

        val targetGuard = CoreTester.execute(
            CLASS_NAME,
            "putThroughUnknownReceiverDoesNotInventTarget(Liput_test;I)I",
        )
        assertEquals(CompletionStatus.Complete, targetGuard.completion())
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(targetGuard))
        assertTrue(
            targetGuard.outcomes.contains(Outcome.Returned(Value.Known(0, type("I")))),
            "the unrelated fresh object's cell must remain at its default; an unknown receiver names no target",
        )
    }

    @Test
    fun caughtUnknownReceiverNpeAndNormalPathKeepUnrelatedFieldUnchanged() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "unknownReceiverCaughtNpeKeepsUnrelatedField(Liput_test;I)I",
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph), "the local handler catches the NPE")
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }
        assertEquals(2, returned.size, "both normal and caught-NPE paths must return")
        assertEquals(
            mapOf(Value.Known(0, type("I")) to 2),
            returned.groupingBy { it }.eachCount(),
            "both paths observe the unrelated object's unchanged default field",
        )
        assertEquals(
            listOf(4, 7),
            graph.nodePile(2).single().children.map { it.address }.sorted(),
            "the unknown receiver must fork to fall-through and the local NPE handler",
        )
        assertTrue(graph.wasReached(7), "the handler path must execute; one normal return is not enough")
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(2))

        val trusted = graph.trusted()!!
        val unrelated = trusted.terminatingConsensus(0) as Value.Known
        val objectConsensus = trusted.terminatingHeapObject(unrelated.raw as HeapRef).agreed() as HeapObject.TrackedObject
        assertEquals(Value.Known(0, type("I")), objectConsensus.field("Liput_test;->myInt:I"))
    }

    /**
     * Verifier-excluded input: a known non-null receiver that identifies no modelled heap cell. The engine records
     * STRONG, writes nothing, and invents no exception
     * (`docs/DALVIK-SEMANTICS.md#engine-choices-for-verifier-excluded-input`).
     */
    @Test
    fun aKnownNonHeapRefReceiverContinuesWithStrongUnmodelledEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "putThroughKnownNonHeapRefReceiver()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph), "no guest exception is invented")
        val view = graph.methodView
        val putAddress = view.addresses.single { view.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.IPUT }
        assertTrue(graph.wasReached(view.nextAddress(putAddress)), "the conservative malformed-input path must continue")
        assertEquals(Value.Known(42, type("I")), graph.trusted()!!.terminatingConsensus(1))
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(putAddress))
        assertEquals(emptySet<Int>(), graph.registersAssigned(putAddress), "iput still assigns no register")

        val successful = CoreTester.execute(CLASS_NAME, "putInt()Liput_test;")
        assertEquals(CompletionStatus.Complete, successful.completion())
        assertEquals(
            SideEffectLevel.WEAK,
            successful.explored.sideEffectLevel(4),
            "the control identifies and writes a tracked cell, so it remains WEAK",
        )
    }

    /**
     * Verifier-excluded input: the receiver is a real tracked object of another class, so the resolved cell does
     * not exist on it. Same engine choice as the non-heap receiver: STRONG and no fabricated store.
     */
    @Test
    fun aTrackedObjectWithoutTheResolvedCellContinuesWithStrongUnmodelledEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "putThroughTrackedObjectWithoutResolvedCell()Liget_test;")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph), "no guest exception is invented")
        assertTrue(graph.wasReached(6), "the conservative malformed-input path must continue")
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(4))

        val trusted = graph.trusted()!!
        val receiver = trusted.terminatingConsensus(0) as Value.Known
        val tracked = trusted.terminatingHeapObject(receiver.raw as HeapRef).agreed() as HeapObject.TrackedObject
        assertEquals(
            Value.Known(0, type("I")),
            tracked.field("Liget_test;->myInt:I"),
            "the object's real field must remain at its default",
        )
        assertEquals(
            null,
            tracked.field("Liput_test;->myInt:I"),
            "the malformed access must not fabricate the resolved cell on the wrong object layout",
        )
    }

    /** `iput-object` stores a register that reads as null, here a primitive-tagged 5, as `null`. */
    @Test
    fun anObjectStoreOfAPrimitiveTaggedValueStoresNull() {
        CoreTester.assertReturns(
            CLASS_NAME,
            "putObjectWithPrimitiveTaggedValue()Ljava/lang/String;",
            Value.Known(null, type("Ljava/lang/String;")),
            register = 2,
        )

        val graph = CoreTester.execute(
            CLASS_NAME,
            "putObjectWithPrimitiveTaggedValueAndReturnReceiver()Liput_test;",
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()!!
        val receiver = trusted.terminatingConsensus(0) as Value.Known
        val stored = (trusted.terminatingHeapObject(receiver.raw as HeapRef).agreed() as HeapObject.TrackedObject)
            .field("Liput_test;->myString:Ljava/lang/String;")
        assertEquals(
            Value.Known(null, type("Ljava/lang/String;")),
            stored,
            "inspect the iput cell directly; a following iget-object would mask a bad stored primitive",
        )
    }

    @Test
    fun aResolvedWriteIsWeakReadsBothOperandsAndAssignsNoRegister() {
        val graph = CoreTester.execute(CLASS_NAME, "putInt()Liput_test;")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(4))
        assertEquals(SideEffectLevel.WEAK, graph.explored.methodSideEffectLevel())
        assertEquals(setOf(0, 1), graph.registersRead(4))
        assertEquals(emptySet<Int>(), graph.registersAssigned(4))
    }

    /** Writes [written] through `iput-*` and reads it back through the matching `iget-*`. */
    private fun narrowStore(method: String, written: Int, stored: Value): DynamicTest =
        DynamicTest.dynamicTest("$method stores 0x${written.toString(16)} as $stored") {
            CoreTester.assertReturns(
                "Lifield_forms_test;", method, stored, register = 1,
                initialRegisters = mapOf(2 to Value.Known(written, type("I"))),
            )
        }

    private fun resolutionFailure(method: String, error: String, putAt: Int, nextAt: Int): DynamicTest =
        DynamicTest.dynamicTest("$method -> $error") {
            val graph = CoreTester.execute(CLASS_NAME, method)
            assertEquals(CompletionStatus.Complete, graph.completion())
            assertEquals(listOf(error), CoreTester.escapingTypes(graph))
            assertFalse(graph.wasReached(nextAt), "a definite resolution failure has no write continuation")
            assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(putAt))
        }

    private fun type(descriptor: String) = VirtualType.of(descriptor)

    private companion object {
        private const val CLASS_NAME = "Liput_test;"
    }
}
