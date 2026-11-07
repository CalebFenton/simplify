package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction12x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class MethodEditTransactionTest {
    @TempDir
    lateinit var root: Path
    private var fixtureSequence = 0

    @Test
    fun insertBeforePublishesOneRevisionAndKeepsTheOldGraphStable() {
        val index = index(BASE_SOURCE)
        val services = VmServices(index)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(VALUE))
        val graphA = services.newSession(viewA).run()
        val edit = services.methodEditor.beginEdit(viewA)
        val baselineAnchor = edit.pendingAnchorForCodeAddress(1)
        val inserted = edit.insertInstructionsBefore(
            1,
            listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 2))),
        )

        val result = edit.commit() as MethodEditCommitResult.Committed
        val graphB = services.newSession(result.editedMethodView).run()

        assertEquals(Value.Known(1, INT), graphA.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Known(2, INT), graphB.trusted()!!.terminatingConsensus(0))
        assertSame(snapshotA.programRevision, graphA.programRevision)
        assertNotSame(snapshotA.programRevision, result.programRevision)
        assertNotSame(viewA.methodRevision, result.editedMethodView.methodRevision)
        assertSame(result.programRevision, index.currentSnapshot.programRevision)
        assertEquals(1, result.codeAddressesByPendingAnchor.getValue(baselineAnchor))
        assertEquals(1, result.codeAddressesByPendingAnchor.getValue(inserted.anchors.single()))
        assertEquals(MethodEditTransactionState.COMMITTED, edit.state)
    }

    @Test
    fun oneToManyReplacementCopiesInputAndRecomputesCodeAddresses() {
        val index = index(BASE_SOURCE)
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(VALUE)))
        val anchor = edit.pendingAnchorForCodeAddress(0)
        val submitted = mutableListOf<InstructionTemplate>(
            encoded(ImmutableInstruction21s(Opcode.CONST_16, 0, 2)),
            encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 3)),
        )
        edit.replaceInstruction(0, submitted)
        submitted.clear()

        val result = edit.commit() as MethodEditCommitResult.Committed
        val graph = VmServices(index).newSession(result.editedMethodView).run()

        assertArrayEquals(intArrayOf(0, 2, 3), result.editedMethodView.instructionAddresses)
        assertEquals(Opcode.CONST_16, result.editedMethodView.opcodeAt(0))
        assertEquals(Value.Known(3, INT), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(0, result.codeAddressesByPendingAnchor.getValue(anchor))
    }

    @Test
    fun singleAndBatchRemovalUseBaselineAddressesWithoutMutatingTheCallerCollection() {
        val index = index(BASE_SOURCE)
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(LINEAR)))
        val removedAnchor = edit.pendingAnchorForCodeAddress(1)
        val batch = mutableListOf(3, 1)
        edit.removeInstruction(0)
        edit.removeInstructions(batch)
        batch.clear()

        val result = edit.commit() as MethodEditCommitResult.Committed
        val graph = VmServices(index).newSession(result.editedMethodView).run()

        assertArrayEquals(intArrayOf(0, 1), result.editedMethodView.instructionAddresses)
        assertEquals(Opcode.CONST_4, result.editedMethodView.opcodeAt(0))
        assertEquals(0, result.codeAddressesByPendingAnchor.getValue(removedAnchor))
        assertEquals(Value.Known(4, INT), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun staleAndConflictingEditsRejectWithoutPublication() {
        val index = index(BASE_SOURCE)
        val editor = VmServices(index).methodEditor
        val view = requireNotNull(index.methodView(LINEAR))
        val winner = editor.beginEdit(view)
        val stale = editor.beginEdit(view)
        winner.removeInstruction(0)
        stale.removeInstruction(0)
        winner.commit()
        val snapshotAfterWinner = index.currentSnapshot

        val staleResult = stale.commit() as MethodEditCommitResult.Rejected
        assertTrue(staleResult.reason is MethodEditRejectionReason.StaleMethodRevision)
        assertSame(snapshotAfterWinner, index.currentSnapshot)

        val conflicting = editor.beginEdit(requireNotNull(index.methodView(LINEAR)))
        conflicting.removeInstruction(0)
        conflicting.removeInstruction(0)
        val conflictResult = conflicting.commit() as MethodEditCommitResult.Rejected
        assertEquals(MethodEditRejectionReason.ConflictingCommands(0), conflictResult.reason)
    }

    @Test
    fun trustedEditsGuardTheProgramRevisionWhileManualEditsGuardOnlyTheMethod() {
        val index = index(BASE_SOURCE)
        val services = VmServices(index)
        val valueView = requireNotNull(index.methodView(VALUE))
        val trusted = services.newSession(valueView).run().trusted()!!
        val trustedEdit = services.methodEditor.beginEdit(trusted)
        val manualEdit = services.methodEditor.beginEdit(valueView)
        trustedEdit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 2))))
        manualEdit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 3))))

        val unrelated = services.methodEditor.beginEdit(requireNotNull(index.methodView(OTHER)))
        unrelated.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 4))))
        unrelated.commit()

        val trustedResult = trustedEdit.commit() as MethodEditCommitResult.Rejected
        assertTrue(trustedResult.reason is MethodEditRejectionReason.StaleProgramRevision)
        val manualResult = manualEdit.commit() as MethodEditCommitResult.Committed
        val graph = services.newSession(manualResult.editedMethodView).run()
        assertEquals(Value.Known(3, INT), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun terminalStatesRejectLaterOperations() {
        val index = index(BASE_SOURCE)
        val editor = VmServices(index).methodEditor
        val unchanged = editor.beginEdit(requireNotNull(index.methodView(VALUE)))

        assertSame(MethodEditCommitResult.Unchanged, unchanged.commit())
        assertEquals(MethodEditTransactionState.UNCHANGED, unchanged.state)
        assertThrows(IllegalStateException::class.java) { unchanged.removeInstruction(0) }

        val discarded = editor.beginEdit(requireNotNull(index.methodView(VALUE)))
        discarded.insertInstructionsBefore(1, listOf(encoded(ImmutableInstruction10x(Opcode.NOP))))
        discarded.discard()
        assertEquals(MethodEditTransactionState.DISCARDED, discarded.state)
        assertThrows(IllegalStateException::class.java) { discarded.commit() }
    }

    @Test
    fun byteIdenticalReplacementIsUnchangedWithoutPublication() {
        val index = index(BASE_SOURCE)
        val snapshot = index.currentSnapshot
        val view = requireNotNull(index.methodView(VALUE))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        edit.replaceInstruction(0, listOf(encoded(view.instructionAt(0))))

        val result = edit.commit()

        assertSame(MethodEditCommitResult.Unchanged, result)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun undefinedInvokeArgumentAndDefiniteNonReferenceReturnRejectWithoutPublication() {
        val index = index(BASE_SOURCE)
        val snapshot = index.currentSnapshot
        val editor = VmServices(index).methodEditor
        val invoke = editor.beginEdit(requireNotNull(index.methodView(DYNAMIC)))
        invoke.removeInstruction(0)

        val invokeResult = invoke.commit() as MethodEditCommitResult.Rejected

        assertTrue(invokeResult.reason is MethodEditRejectionReason.StructuralValidationFailure)
        assertSame(snapshot, index.currentSnapshot)

        val objectReturn = editor.beginEdit(requireNotNull(index.methodView(OBJECT_RETURN)))
        objectReturn.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 1))))

        val returnResult = objectReturn.commit() as MethodEditCommitResult.Rejected

        assertTrue(returnResult.reason is MethodEditRejectionReason.StructuralValidationFailure)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun twoAddressArithmeticRejectsAnUndefinedDestinationRead() {
        val index = index("""
            .class public Ltask55/Edit;
            .super Ljava/lang/Object;
            .method public static value()I
                .registers 2
                const/4 v0, 0x1
                return v0
            .end method
        """)
        val snapshot = index.currentSnapshot
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(VALUE)))
        edit.replaceInstruction(0, listOf(
            encoded(ImmutableInstruction11n(Opcode.CONST_4, 1, 1)),
            encoded(ImmutableInstruction12x(Opcode.ADD_INT_2ADDR, 0, 1)),
        ))
        val result = edit.commit()
        assertTrue(result is MethodEditCommitResult.Rejected)
        assertSame(snapshot, index.currentSnapshot)
        val graph = services.newSession(requireNotNull(index.methodView(VALUE))).run()
        assertEquals(Value.Known(1, INT), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun validWideStoresCommitAndVolatileWideOverflowRejects() {
        val index = index(BASE_SOURCE)
        val editor = VmServices(index).methodEditor
        for (signature in listOf(APUT_WIDE, IPUT_WIDE)) {
            val view = requireNotNull(index.methodView(signature))
            val edit = editor.beginEdit(view)
            val returnAddress = view.terminalInstructionAddresses.single()
            edit.insertInstructionsBefore(returnAddress, listOf(encoded(ImmutableInstruction10x(Opcode.NOP))))
            assertTrue(edit.commit() is MethodEditCommitResult.Committed)
        }

        val overflow = editor.beginEdit(requireNotNull(index.methodView(WIDE_OVERFLOW)))
        overflow.insertInstructionsBefore(
            0,
            listOf(
                encoded(
                    ImmutableInstruction21c(
                        Opcode.SPUT_WIDE_VOLATILE,
                        0,
                        ImmutableFieldReference("Ltask55/Edit;", "staticWide", "J"),
                    ),
                ),
            ),
        )

        val result = overflow.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.RegisterOverflow(1), result.reason)
    }

    @Test
    fun invalidAddressesPayloadsAndOffsetTemplatesReturnExpectedRejections() {
        val index = index(UNSUPPORTED_SOURCE)
        val editor = VmServices(index).methodEditor

        val invalidAddress = editor.beginEdit(requireNotNull(index.methodView(SIMPLE_VOID)))
        invalidAddress.removeInstruction(99)
        assertEquals(
            MethodEditRejectionReason.InvalidCodeAddress(99),
            (invalidAddress.commit() as MethodEditCommitResult.Rejected).reason,
        )

        val payloadView = requireNotNull(index.methodView(PAYLOAD))
        val payloadAddress = payloadView.payloadAddresses.single()
        val payloadEdit = editor.beginEdit(payloadView)
        payloadEdit.removeInstruction(payloadAddress)
        assertEquals(
            MethodEditRejectionReason.PayloadCodeAddress(payloadAddress),
            (payloadEdit.commit() as MethodEditCommitResult.Rejected).reason,
        )

        val offsetTemplate = editor.beginEdit(requireNotNull(index.methodView(SIMPLE_VOID)))
        offsetTemplate.insertInstructionsBefore(
            0,
            listOf(encoded(ImmutableInstruction10t(Opcode.GOTO, 1))),
        )
        val result = offsetTemplate.commit() as MethodEditCommitResult.Rejected
        assertTrue(result.reason is MethodEditRejectionReason.InvalidInstructionTemplate)
    }

    @Test
    fun task56PreservesControlTryAndDebugMetadata() {
        val index = index(UNSUPPORTED_SOURCE)
        val editor = VmServices(index).methodEditor

        for (signature in listOf(BRANCH, WITH_TRY, WITH_DEBUG)) {
            val edit = editor.beginEdit(requireNotNull(index.methodView(signature)))
            edit.insertInstructionsBefore(0, listOf(encoded(ImmutableInstruction10x(Opcode.NOP))))
            assertTrue(edit.commit() is MethodEditCommitResult.Committed)
        }
    }

    @Test
    fun wideTemplateRejectsWhenItsHighRegisterExceedsTheMethodRegisterFile() {
        val index = index(BASE_SOURCE)
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(VALUE)))
        edit.replaceInstruction(
            0,
            listOf(encoded(ImmutableInstruction21s(Opcode.CONST_WIDE_16, 0, 1))),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.RegisterOverflow(1), result.reason)
    }

    @Test
    fun finalStructuralFailureDoesNotPublish() {
        val index = index(BASE_SOURCE)
        val snapshot = index.currentSnapshot
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(VALUE)))
        edit.replaceInstruction(1, listOf(encoded(ImmutableInstruction10x(Opcode.RETURN_VOID))))

        val result = edit.commit() as MethodEditCommitResult.Rejected

        assertTrue(result.reason is MethodEditRejectionReason.StructuralValidationFailure)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun editPreservesValidInvokeAndMoveResultAdjacency() {
        val index = index(BASE_SOURCE)
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(DYNAMIC)))
        edit.insertInstructionsBefore(6, listOf(encoded(ImmutableInstruction10x(Opcode.NOP))))

        val result = edit.commit()

        assertTrue(result is MethodEditCommitResult.Committed)
        assertTrue(requireNotNull(index.currentSnapshot.applicationMethod(DYNAMIC)).dynamicLoadingEvidence.isPresent)
    }

    @Test
    fun commitReplacesOnlyTheEditedMethodsEvidenceAndReferenceClosure() {
        val index = index(BASE_SOURCE)
        val snapshotA = index.currentSnapshot
        val dynamicA = requireNotNull(snapshotA.applicationMethod(DYNAMIC))
        val otherA = requireNotNull(snapshotA.applicationMethod(OTHER))
        assertTrue(dynamicA.dynamicLoadingEvidence.isPresent)
        assertTrue(dynamicA.dexReferenceClosure.methods.any { it.name == "forName" })

        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(DYNAMIC)))
        edit.removeInstructions(listOf(2, 5))
        edit.commit()

        val snapshotB = index.currentSnapshot
        val dynamicB = requireNotNull(snapshotB.applicationMethod(DYNAMIC))
        assertFalse(dynamicB.dynamicLoadingEvidence.isPresent)
        assertFalse(dynamicB.dexReferenceClosure.methods.any { it.name == "forName" })
        assertFalse(snapshotB.dynamicLoadingEvidence.isPresent)
        assertSame(otherA, snapshotB.applicationMethod(OTHER))
        assertTrue(dynamicA.dynamicLoadingEvidence.isPresent)
    }

    private fun index(source: String): ClassIndex {
        val directory = Files.createDirectory(root.resolve("fixture-${fixtureSequence++}"))
        return task53ClassIndex(directory, source)
    }

    private fun encoded(instruction: com.android.tools.smali.dexlib2.iface.instruction.Instruction): InstructionTemplate =
        InstructionTemplate.Encoded(instruction)

    private companion object {
        val INT = VirtualType.of("I")
        const val VALUE = "Ltask55/Edit;->value()I"
        const val LINEAR = "Ltask55/Edit;->linear()I"
        const val OTHER = "Ltask55/Edit;->other()I"
        const val DYNAMIC = "Ltask55/Edit;->dynamic()V"
        const val OBJECT_RETURN = "Ltask55/Edit;->objectReturn()Ljava/lang/Object;"
        const val APUT_WIDE = "Ltask55/Edit;->aputWide([JJI)V"
        const val IPUT_WIDE = "Ltask55/Edit;->iputWide(Ltask55/Edit;J)V"
        const val WIDE_OVERFLOW = "Ltask55/Edit;->wideOverflow()V"
        const val SIMPLE_VOID = "Ltask55/Unsupported;->simple()V"
        const val BRANCH = "Ltask55/Unsupported;->branch()V"
        const val WITH_TRY = "Ltask55/Unsupported;->withTry()V"
        const val WITH_DEBUG = "Ltask55/Unsupported;->withDebug()V"
        const val PAYLOAD = "Ltask55/Unsupported;->payload()V"

        const val BASE_SOURCE = """
            .class public Ltask55/Edit;
            .super Ljava/lang/Object;

            .field public wide:J
            .field public static volatile staticWide:J

            .method public static value()I
                .registers 1
                const/4 v0, 0x1
                return v0
            .end method

            .method public static linear()I
                .registers 1
                nop
                const/16 v0, 0x2
                nop
                const/4 v0, 0x4
                return v0
            .end method

            .method public static other()I
                .registers 1
                const/4 v0, 0x1
                return v0
            .end method

            .method public static dynamic()V
                .registers 1
                const-string v0, "java.lang.String"
                invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
                move-result-object v0
                return-void
            .end method

            .method public static objectReturn()Ljava/lang/Object;
                .registers 1
                const/4 v0, 0x0
                return-object v0
            .end method

            .method public static aputWide([JJI)V
                .registers 4
                aput-wide v1, v0, v3
                return-void
            .end method

            .method public static iputWide(Ltask55/Edit;J)V
                .registers 3
                iput-wide v1, v0, Ltask55/Edit;->wide:J
                return-void
            .end method

            .method public static wideOverflow()V
                .registers 1
                return-void
            .end method
        """

        const val UNSUPPORTED_SOURCE = """
            .class public Ltask55/Unsupported;
            .super Ljava/lang/Object;

            .method public static simple()V
                .registers 0
                return-void
            .end method

            .method public static branch()V
                .registers 0
                goto :done
                nop
                :done
                return-void
            .end method

            .method public static withTry()V
                .registers 1
                :try_start
                const/4 v0, 0x0
                :try_end
                .catchall {:try_start .. :try_end} :handler
                return-void
                :handler
                move-exception v0
                return-void
            .end method

            .method public static withDebug()V
                .registers 0
                .line 7
                return-void
            .end method

            .method public static payload()V
                .registers 2
                const/4 v0, 0x1
                new-array v0, v0, [I
                fill-array-data v0, :data
                return-void
                :data
                .array-data 4
                    0x1
                .end array-data
            .end method
        """
    }
}
