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
import org.junit.jupiter.api.Test

/** Behavioral coverage added for Task 19. The retired oracle test asserted a no-op self-assignment. */
class IPutOpTest {

    @Test
    fun aWriteIsObservableThroughAFollowingRead() {
        CoreTester.assertReturns("Liget_test;", "putThenGetInt()I", Value.Known(0x42, type("I")), register = 2)
    }

    /**
     * The instance-field twin of `SPutOpTest.aNarrowStoreKeepsOnlyItsFieldsWidth`, read back through the
     * matching `iget-*`: ART's `GetFieldValue` casts the 32-bit register to the field's width before storing.
     */
    @Test
    fun aNarrowStoreKeepsOnlyItsFieldsWidth() {
        fun assertReadsBack(method: String, written: Int, stored: Value) = CoreTester.assertReturns(
            "Lifield_forms_test;", method, stored, register = 1,
            initialRegisters = mapOf(2 to Value.Known(written, type("I"))),
        )
        assertReadsBack("putByte(B)B", 0x1ff, Value.Known((-1).toByte(), type("B")))
        assertReadsBack("putShort(S)S", 0x18000, Value.Known((-32768).toShort(), type("S")))
        assertReadsBack("putChar(C)C", 0x10041, Value.Known('A', type("C")))
        assertReadsBack("putBoolean(Z)Z", 0x101, Value.Known(true, type("Z")))
        assertReadsBack("putBoolean(Z)Z", 2, Value.Unknown(type("Z")))
    }

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

    @Test
    fun definiteNullStoresHaveNoContinuationOrSiblingStore() {
        assertDefiniteNullStore("putIntoNullReceiver()V")
        assertDefiniteNullStore("putIntoPrimitiveTaggedReceiver()V")
    }

    @Test
    fun putResolutionFailuresPrecedeNullAndNeverStore() {
        val missingClass = CoreTester.execute(CLASS_NAME, "putMissingClassOnNullReceiver()V")
        assertEquals(CompletionStatus.Complete, missingClass.completion())
        assertEquals(listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(missingClass))
        assertFalse(missingClass.wasReached(5), "resolution fails before the null receiver can produce NPE")
        assertEquals(SideEffectLevel.NONE, missingClass.explored.sideEffectLevel(3))

        val missingMember = CoreTester.execute(CLASS_NAME, "putMissingMember()V")
        assertEquals(CompletionStatus.Complete, missingMember.completion())
        assertEquals(listOf("Ljava/lang/NoSuchFieldError;"), CoreTester.escapingTypes(missingMember))
        assertFalse(missingMember.wasReached(6), "a definite missing member has no write continuation")
        assertEquals(SideEffectLevel.NONE, missingMember.explored.sideEffectLevel(4))
    }

    @Test
    fun anInstanceWriteCannotResolveAStaticOnlyField() {
        val graph = CoreTester.execute(CLASS_NAME, "putStaticFieldWithInstanceOpcode()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchFieldError;"), CoreTester.escapingTypes(graph))
        assertFalse(graph.wasReached(6), "static-only declarations are absent from instance resolution")
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(4))
    }

    @Test
    fun anUnknownReceiverPreservesNormalAndNullOutcomesWithoutInventingAFieldTarget() {
        val graph = CoreTester.execute(CLASS_NAME, "putThroughUnknownReceiver(Liput_test;I)V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
        assertTrue(graph.outcomes.contains(Outcome.Returned(null)))
        // VERIFIER-tier malformed input. No target is provable, so spec 2.2 requires STRONG rather
        // than inventing a target or understating an unmodelled write.
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

    @Test
    fun aKnownNonHeapRefReceiverContinuesWithStrongUnmodelledEffect() {
        // VERIFIER-tier malformed input, not a runtime claim. Spec 2.2 requires STRONG because the known
        // non-null receiver identifies no modelled heap cell.
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

    @Test
    fun aTrackedObjectWithoutTheResolvedCellContinuesWithStrongUnmodelledEffect() {
        // VERIFIER-tier malformed input, not a runtime claim. The HeapRef is real, but writeField cannot
        // find the resolved declaring-class cell. Spec 2.2 requires STRONG and forbids a fabricated store.
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

    @Test
    fun iputObjectUsesReferenceReadCoercion() {
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

    private fun assertDefiniteNullStore(method: String) {
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

    private fun type(descriptor: String) = VirtualType.of(descriptor)

    private companion object {
        private const val CLASS_NAME = "Liput_test;"
    }
}
