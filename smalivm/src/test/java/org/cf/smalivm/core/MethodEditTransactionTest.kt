package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.debug.LineNumber
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction12x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction23x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction30t
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
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
    fun insertionPublishesViewBWithoutChangingViewA() {
        val index = index(SHAPE_SOURCE)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(INSERTION))
        val shapeA = instructionShape(viewA)
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.insertInstructionsBefore(
            0,
            listOf(encoded(ImmutableInstruction10x(Opcode.NOP))),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView

        assertEquals(
            listOf(0 to Opcode.NOP, 1 to Opcode.CONST_4, 2 to Opcode.RETURN),
            instructionShape(viewB),
        )
        assertPublishedViewB(index, snapshotA, viewA, shapeA, committed)
    }

    @Test
    fun entryAndMiddleRemovalUseBaselineAddressesWithoutChangingViewAOrTheCallerCollection() {
        val index = index(SHAPE_SOURCE)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(REMOVAL))
        val shapeA = instructionShape(viewA)
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        val batch = mutableListOf(1)
        edit.removeInstruction(0)
        edit.removeInstructions(batch)
        batch.clear()

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView

        assertEquals(
            listOf(0 to Opcode.CONST_4, 1 to Opcode.CONST_4, 2 to Opcode.RETURN),
            instructionShape(viewB),
        )
        assertEquals(setOf(2), VmServices(index).newSession(viewB).run().registersAssigned(0))
        assertEquals(setOf(3), VmServices(index).newSession(viewB).run().registersAssigned(1))
        assertPublishedViewB(index, snapshotA, viewA, shapeA, committed)
    }

    @Test
    fun insertionAcrossTheGotoLimitWidensTheBranchWithoutChangingViewA() {
        val index = index(SHAPE_SOURCE)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(BRANCH_WIDENING))
        val shapeA = instructionShape(viewA)
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.insertInstructionsBefore(
            1,
            List(127) { encoded(ImmutableInstruction10x(Opcode.NOP)) },
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView

        assertEquals(Opcode.GOTO, viewA.opcodeAt(0))
        assertEquals(2, branchTarget(viewA, 0))
        assertEquals(Opcode.GOTO_16, viewB.opcodeAt(0))
        assertEquals(Opcode.RETURN_VOID, viewB.opcodeAt(branchTarget(viewB, 0)))
        assertPublishedViewB(index, snapshotA, viewA, shapeA, committed)
    }

    @Test
    fun branchReplacementUsesATransactionBoundTargetWithoutChangingViewA() {
        val index = index(SHAPE_SOURCE)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(BRANCH_REPLACEMENT))
        val shapeA = instructionShape(viewA)
        val branchAddress = 1
        val target = branchTarget(viewA, branchAddress)
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        val targetAnchor = edit.pendingAnchorForCodeAddress(target)
        edit.replaceInstruction(
            branchAddress,
            listOf(InstructionTemplate.Branch(ImmutableInstruction30t(Opcode.GOTO_32, 0), targetAnchor)),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView

        assertEquals(Opcode.IF_EQ, viewA.opcodeAt(branchAddress))
        assertEquals(Opcode.GOTO_32, viewB.opcodeAt(branchAddress))
        assertEquals(Opcode.RETURN, viewB.opcodeAt(branchTarget(viewB, branchAddress)))
        assertEquals(Value.Known(0, INT), VmServices(index).newSession(viewB).run().trusted()!!.terminatingConsensus(0))
        assertPublishedViewB(index, snapshotA, viewA, shapeA, committed)
    }

    @Test
    fun payloadPaddingTracksAlignmentAfterInsertionAndRemoval() {
        val insertionIndex = index(SHAPE_SOURCE)
        val insertionSnapshotA = insertionIndex.currentSnapshot
        val insertionViewA = requireNotNull(insertionIndex.methodView(PAYLOAD_WITHOUT_PADDING))
        val insertionShapeA = instructionShape(insertionViewA)
        val insert = VmServices(insertionIndex).methodEditor.beginEdit(insertionViewA)
        insert.insertInstructionsBefore(
            4,
            listOf(encoded(ImmutableInstruction10x(Opcode.NOP))),
        )

        val inserted = insert.commit() as MethodEditCommitResult.Committed
        val insertedViewB = inserted.editedMethodView

        assertArrayEquals(intArrayOf(8), insertionViewA.payloadAddresses)
        assertArrayEquals(intArrayOf(10), insertedViewB.payloadAddresses)
        assertEquals(
            listOf(4, 9),
            insertedViewB.instructionAddresses.filter { insertedViewB.opcodeAt(it) == Opcode.NOP },
        )
        assertPublishedViewB(insertionIndex, insertionSnapshotA, insertionViewA, insertionShapeA, inserted)

        val removalIndex = index(SHAPE_SOURCE)
        val removalSnapshotA = removalIndex.currentSnapshot
        val removalViewA = requireNotNull(removalIndex.methodView(PAYLOAD_WITH_PADDING))
        val removalShapeA = instructionShape(removalViewA)
        val remove = VmServices(removalIndex).methodEditor.beginEdit(removalViewA)
        remove.removeInstruction(7)

        val removed = remove.commit() as MethodEditCommitResult.Committed
        val removedViewB = removed.editedMethodView

        assertArrayEquals(intArrayOf(12), removalViewA.payloadAddresses)
        assertArrayEquals(intArrayOf(8), removedViewB.payloadAddresses)
        assertTrue(removedViewB.instructionAddresses.none { removedViewB.opcodeAt(it) == Opcode.NOP })
        assertPublishedViewB(removalIndex, removalSnapshotA, removalViewA, removalShapeA, removed)
    }

    @Test
    fun widerReplacementRecomputesLaterAddressesWithoutChangingViewA() {
        val index = index(SHAPE_SOURCE)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(WIDTH_REPLACEMENT))
        val shapeA = instructionShape(viewA)
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.replaceInstruction(
            0,
            listOf(encoded(ImmutableInstruction21s(Opcode.CONST_16, 0, 0))),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView
        val replacement = viewB.instructionAt(0)

        assertEquals(
            listOf(0 to Opcode.CONST_16, 2 to Opcode.CONST_4, 3 to Opcode.RETURN),
            instructionShape(viewB),
        )
        assertEquals(0, (replacement as OneRegisterInstruction).registerA)
        assertEquals(0, (replacement as NarrowLiteralInstruction).narrowLiteral)
        assertPublishedViewB(index, snapshotA, viewA, shapeA, committed)
    }

    @Test
    fun oneToManyReplacementKeepsSubmittedOrderWithoutChangingViewAOrTheCallerCollection() {
        val index = index(SHAPE_SOURCE)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(MULTI_REPLACEMENT))
        val shapeA = instructionShape(viewA)
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        val submitted = mutableListOf(
            encoded(ImmutableInstruction21s(Opcode.CONST_16, 1, 2)),
            encoded(ImmutableInstruction11n(Opcode.CONST_4, 2, 3)),
        )
        edit.replaceInstruction(1, submitted)
        submitted.clear()

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView

        assertEquals(
            listOf(0 to Opcode.CONST_4, 1 to Opcode.CONST_16, 3 to Opcode.CONST_4, 4 to Opcode.RETURN),
            instructionShape(viewB),
        )
        assertEquals(1, (viewB.instructionAt(1) as OneRegisterInstruction).registerA)
        assertEquals(2, (viewB.instructionAt(1) as NarrowLiteralInstruction).narrowLiteral)
        assertEquals(2, (viewB.instructionAt(3) as OneRegisterInstruction).registerA)
        assertEquals(3, (viewB.instructionAt(3) as NarrowLiteralInstruction).narrowLiteral)
        val graphB = VmServices(index).newSession(viewB).run()
        assertEquals(setOf(1), graphB.registersAssigned(1))
        assertEquals(setOf(2), graphB.registersAssigned(3))
        assertEquals(Value.Known(3, INT), graphB.trusted()!!.terminatingConsensus(2))
        assertPublishedViewB(index, snapshotA, viewA, shapeA, committed)
    }

    @Test
    fun removingAValidProtectedRangeAndItsHandlerEntryCleansUpTheTryRegion() {
        val index = index(SHAPE_SOURCE)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(TRY_CLEANUP))
        val shapeA = instructionShape(viewA)
        val moveExceptionAddress = viewA.instructionAddresses.single { viewA.opcodeAt(it) == Opcode.MOVE_EXCEPTION }
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.removeInstructions(listOf(0, moveExceptionAddress))

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val viewB = committed.editedMethodView

        assertEquals(2, viewA.tryBlocks.sumOf { it.catches.size })
        assertTrue(viewB.tryBlocks.isEmpty())
        assertTrue(instructionShape(viewB).all { it.second == Opcode.RETURN_VOID })
        assertTrue(requireNotNull(reload(index).methodView(TRY_CLEANUP)).tryBlocks.isEmpty())
        assertPublishedViewB(index, snapshotA, viewA, shapeA, committed)
    }

    @Test
    fun labelsAndDebugItemsMoveToTheFirstReplacementWithoutChangingViewA() {
        val index = index(SHAPE_SOURCE)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(LABEL_AND_DEBUG))
        val shapeA = instructionShape(viewA)
        val targetA = branchTarget(viewA, 0)
        val lineA = lineNumbers(snapshotA, LABEL_AND_DEBUG).single()
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.replaceInstruction(
            targetA,
            listOf(
                encoded(ImmutableInstruction10x(Opcode.NOP)),
                encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 3)),
            ),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val snapshotB = index.currentSnapshot
        val viewB = committed.editedMethodView
        val targetB = branchTarget(viewB, 0)
        val lineB = lineNumbers(snapshotB, LABEL_AND_DEBUG).single()

        assertEquals(targetA, lineA.codeAddress)
        assertEquals(targetB, lineB.codeAddress)
        assertEquals(Opcode.CONST_4, viewA.opcodeAt(targetA))
        assertEquals(Opcode.NOP, viewB.opcodeAt(targetB))
        assertEquals(42, lineB.lineNumber)
        assertPublishedViewB(index, snapshotA, viewA, shapeA, committed)
    }

    @Test
    fun aDeadNopCarryingALineBeforeAPayloadDoesNotFallIntoIt() {
        // dx puts a `.line` on the alignment nop after the last return. ART's verifier checks flow only from reachable
        // instructions, so the dead nop does not fall into the payload and the method is valid.
        val index = index(SHAPE_SOURCE)
        val viewA = requireNotNull(index.methodView(LINE_ON_PADDING))
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 1, 3))))

        val result = edit.commit()

        assertTrue(result is MethodEditCommitResult.Committed, result.toString())
        val viewB = (result as MethodEditCommitResult.Committed).editedMethodView
        assertEquals(Opcode.NOP, viewB.opcodeAt(7))
        assertArrayEquals(intArrayOf(8), viewB.payloadAddresses)
        assertEquals(7, lineNumbers(index.currentSnapshot, LINE_ON_PADDING).single().codeAddress)
    }

    @Test
    fun aLineAtAPayloadAddressSurvivesAnEdit() {
        // Debug info is an address-to-line table that ART does not check against instruction kinds, so a `.line` at
        // a payload address is valid metadata, not a reason to refuse every edit of the method.
        val index = index(SHAPE_SOURCE)
        val viewA = requireNotNull(index.methodView(LINE_ON_PAYLOAD))
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 1, 3))))

        val result = edit.commit()

        assertTrue(result is MethodEditCommitResult.Committed, result.toString())
        val viewB = (result as MethodEditCommitResult.Committed).editedMethodView
        assertArrayEquals(intArrayOf(8), viewB.payloadAddresses)
        assertEquals(8, lineNumbers(index.currentSnapshot, LINE_ON_PAYLOAD).single().codeAddress)
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
        assertEquals(MethodEditRejectionReason.StaleMethodRevision, staleResult.reason)
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
        assertEquals(MethodEditRejectionReason.StaleProgramRevision, trustedResult.reason)
        val manualResult = manualEdit.commit() as MethodEditCommitResult.Committed
        val graph = services.newSession(manualResult.editedMethodView).run()
        assertEquals(Value.Known(3, INT), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun closedTransactionsRejectLaterOperations() {
        val index = index(BASE_SOURCE)
        val editor = VmServices(index).methodEditor
        val unchanged = editor.beginEdit(requireNotNull(index.methodView(VALUE)))

        assertSame(MethodEditCommitResult.Unchanged, unchanged.commit())
        assertFalse(unchanged.isOpen)
        assertThrows(IllegalStateException::class.java) { unchanged.removeInstruction(0) }

        val discarded = editor.beginEdit(requireNotNull(index.methodView(VALUE)))
        discarded.insertInstructionsBefore(1, listOf(encoded(ImmutableInstruction10x(Opcode.NOP))))
        discarded.discard()
        assertFalse(discarded.isOpen)
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

        // Without the const-string, invoke-static moves to address 0 and reads v0 before any write.
        assertEquals(MethodEditRejectionReason.ReadBeforeAssignment(codeAddress = 0, register = 0), invokeResult.reason)
        assertSame(snapshot, index.currentSnapshot)

        val objectReturn = editor.beginEdit(requireNotNull(index.methodView(OBJECT_RETURN)))
        objectReturn.replaceInstruction(0, listOf(encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 1))))

        val returnResult = objectReturn.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.ReturnCategoryMismatch(codeAddress = 1, register = 0), returnResult.reason)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun twoAddressArithmeticRejectsAnUndefinedDestinationRead() {
        val index = index(
            """
            .class public Ledit/Edit;
            .super Ljava/lang/Object;
            .method public static value()I
                .registers 2
                const/4 v0, 0x1
                return v0
            .end method
            """,
        )
        val snapshot = index.currentSnapshot
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(VALUE)))
        edit.replaceInstruction(
            0,
            listOf(
                encoded(ImmutableInstruction11n(Opcode.CONST_4, 1, 1)),
                encoded(ImmutableInstruction12x(Opcode.ADD_INT_2ADDR, 0, 1)),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        // add-int/2addr v0, v1 at address 1 reads its destination v0, which nothing assigned.
        assertEquals(MethodEditRejectionReason.ReadBeforeAssignment(codeAddress = 1, register = 0), result.reason)
        assertSame(snapshot, index.currentSnapshot)
        val graph = services.newSession(requireNotNull(index.methodView(VALUE))).run()
        assertEquals(Value.Known(1, INT), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun registerFlowRejectsReadsAndReturnsTheVerifierRejects() {
        // ART's verifier: check-cast reads vA; a wide read needs both registers of the pair; return-object of an
        // int arithmetic result is a category mismatch.
        val index = index(
            """
            .class public Ledit/Flow;
            .super Ljava/lang/Object;
            .method public static reference()Ljava/lang/Object;
                .registers 2
                const/4 v0, 0x0
                return-object v0
            .end method
            .method public static wide()J
                .registers 2
                const-wide/16 v0, 0x1
                return-wide v0
            .end method
            """,
        )
        val editor = VmServices(index).methodEditor
        fun rejection(signature: String, replacement: List<Instruction>): MethodEditRejectionReason? {
            val edit = editor.beginEdit(requireNotNull(index.methodView(signature)))
            edit.replaceInstruction(0, replacement.map(::encoded))
            return (edit.commit() as? MethodEditCommitResult.Rejected)?.reason
        }
        val reference = "Ledit/Flow;->reference()Ljava/lang/Object;"

        assertEquals(
            listOf(
                MethodEditRejectionReason.ReadBeforeAssignment(codeAddress = 0, register = 0),
                MethodEditRejectionReason.ReadBeforeAssignment(codeAddress = 1, register = 1),
                MethodEditRejectionReason.ReturnCategoryMismatch(codeAddress = 4, register = 0),
            ),
            listOf(
                rejection(
                    reference,
                    listOf(ImmutableInstruction21c(Opcode.CHECK_CAST, 0, ImmutableTypeReference("Ljava/lang/Object;"))),
                ),
                rejection("Ledit/Flow;->wide()J", listOf(ImmutableInstruction11n(Opcode.CONST_4, 0, 0))),
                rejection(
                    reference,
                    listOf(
                        ImmutableInstruction11n(Opcode.CONST_4, 0, 1),
                        ImmutableInstruction11n(Opcode.CONST_4, 1, 2),
                        ImmutableInstruction23x(Opcode.ADD_INT, 0, 0, 1),
                    ),
                ),
            ),
        )
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
                        ImmutableFieldReference("Ledit/Edit;", "staticWide", "J"),
                    ),
                ),
            ),
        )

        val result = overflow.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.RegisterOverflow(1), result.reason)
    }

    @Test
    fun invalidAddressesPayloadsAndOffsetTemplatesReturnExpectedRejections() {
        val index = index(TEMPLATE_SOURCE)
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
        assertEquals(MethodEditRejectionReason.TemplateOpcodeMismatch(Opcode.GOTO), result.reason)
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

        // value()I must return with `return`; return-void at address 1 breaks the return-type rule.
        assertEquals(MethodEditRejectionReason.ReturnOpcodeMismatch(codeAddress = 1, expected = Opcode.RETURN), result.reason)
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

    private fun reload(index: ClassIndex): ClassIndex {
        val snapshot = index.currentSnapshot
        val dexFile = root.resolve("round-trip-${fixtureSequence++}.dex")
        Files.write(dexFile, ProgramDexWriter().writeToBytes(snapshot))
        return ClassIndex(ClassManagerFactory().build(dexFile.toFile(), snapshot.outputApiPolicy.apiLevel))
    }

    private fun lineNumbers(snapshot: ProgramSnapshot, methodSignature: String): List<LineNumber> {
        val dexFile = root.resolve("debug-${fixtureSequence++}.dex")
        Files.write(dexFile, ProgramDexWriter().writeToBytes(snapshot))
        val method = DexFileFactory.loadDexFile(
            dexFile.toFile(),
            Opcodes.forApi(snapshot.outputApiPolicy.apiLevel),
        ).classes.flatMap { it.methods }.single { method ->
            buildString {
                append(method.definingClass)
                append("->")
                append(method.name)
                method.parameterTypes.joinTo(this, separator = "", prefix = "(", postfix = ")")
                append(method.returnType)
            } == methodSignature
        }
        return requireNotNull(method.implementation).debugItems.filterIsInstance<LineNumber>().toList()
    }

    private fun assertPublishedViewB(
        index: ClassIndex,
        snapshotA: ProgramSnapshot,
        viewA: MethodView,
        shapeA: List<Pair<Int, Opcode>>,
        committed: MethodEditCommitResult.Committed,
    ) {
        val snapshotB = index.currentSnapshot
        assertSame(snapshotA.programRevision, viewA.programRevision)
        assertSame(snapshotB.programRevision, committed.programRevision)
        assertSame(snapshotB.programRevision, committed.editedMethodView.programRevision)
        assertNotSame(snapshotA.programRevision, snapshotB.programRevision)
        assertNotSame(viewA.methodRevision, committed.editedMethodView.methodRevision)
        assertEquals(shapeA, instructionShape(viewA))
        val published = requireNotNull(index.methodView(viewA.signature))
        assertSame(committed.editedMethodView.programRevision, published.programRevision)
        assertSame(committed.editedMethodView.methodRevision, published.methodRevision)
        assertEquals(instructionShape(committed.editedMethodView), instructionShape(published))
    }

    private fun instructionShape(view: MethodView): List<Pair<Int, Opcode>> =
        view.instructionAddresses.map { it to view.opcodeAt(it) }

    private fun branchTarget(view: MethodView, codeAddress: Int): Int =
        codeAddress + (view.instructionAt(codeAddress) as OffsetInstruction).codeOffset

    private fun encoded(instruction: Instruction): InstructionTemplate = InstructionTemplate.Encoded(instruction)

    private companion object {
        val INT = VirtualType.of("I")

        const val INSERTION = "Ledit/Shape;->insertion()I"
        const val REMOVAL = "Ledit/Shape;->removal()I"
        const val BRANCH_WIDENING = "Ledit/Shape;->branchWidening()V"
        const val BRANCH_REPLACEMENT = "Ledit/Shape;->branchReplacement()I"
        const val PAYLOAD_WITHOUT_PADDING = "Ledit/Shape;->payloadWithoutPadding()V"
        const val PAYLOAD_WITH_PADDING = "Ledit/Shape;->payloadWithPadding()V"
        const val WIDTH_REPLACEMENT = "Ledit/Shape;->widthReplacement()I"
        const val MULTI_REPLACEMENT = "Ledit/Shape;->multiReplacement()I"
        const val TRY_CLEANUP = "Ledit/Shape;->tryCleanup()V"
        const val LABEL_AND_DEBUG = "Ledit/Shape;->labelAndDebug()I"
        const val LINE_ON_PADDING = "Ledit/Shape;->lineOnPadding()V"
        const val LINE_ON_PAYLOAD = "Ledit/Shape;->lineOnPayload()V"

        const val VALUE = "Ledit/Edit;->value()I"
        const val LINEAR = "Ledit/Edit;->linear()I"
        const val OTHER = "Ledit/Edit;->other()I"
        const val DYNAMIC = "Ledit/Edit;->dynamic()V"
        const val OBJECT_RETURN = "Ledit/Edit;->objectReturn()Ljava/lang/Object;"
        const val APUT_WIDE = "Ledit/Edit;->aputWide([JJI)V"
        const val IPUT_WIDE = "Ledit/Edit;->iputWide(Ledit/Edit;J)V"
        const val WIDE_OVERFLOW = "Ledit/Edit;->wideOverflow()V"

        const val SIMPLE_VOID = "Ledit/Templates;->simple()V"
        const val PAYLOAD = "Ledit/Templates;->payload()V"

        const val SHAPE_SOURCE = """
            .class public Ledit/Shape;
            .super Ljava/lang/Object;
            .source "Shape.smali"

            .method public static insertion()I
                .registers 1
                const/4 v0, 0x1
                return v0
            .end method

            .method public static removal()I
                .registers 4
                nop
                const/16 v0, 0x2
                const/4 v2, 0x4
                const/4 v3, 0x5
                return v3
            .end method

            .method public static branchWidening()V
                .registers 0
                goto :target
                nop
                :target
                return-void
            .end method

            .method public static branchReplacement()I
                .registers 1
                const/4 v0, 0x0
                if-eq v0, v0, :target
                const/4 v0, 0x1
                :target
                return v0
            .end method

            .method public static payloadWithoutPadding()V
                .registers 1
                const/16 v0, 0x1
                new-array v0, v0, [B
                fill-array-data v0, :data
                return-void
                :data
                .array-data 1
                    0x1t
                .end array-data
            .end method

            .method public static payloadWithPadding()V
                .registers 1
                const/16 v0, 0x1
                new-array v0, v0, [B
                fill-array-data v0, :data
                fill-array-data v0, :data
                return-void
                :data
                .array-data 1
                    0x1t
                .end array-data
            .end method

            .method public static widthReplacement()I
                .registers 1
                const/4 v0, 0x0
                const/4 v0, 0x1
                return v0
            .end method

            .method public static multiReplacement()I
                .registers 3
                const/4 v0, 0x0
                const/4 v1, 0x1
                return v2
            .end method

            .method public static tryCleanup()V
                .registers 1
                :try_start
                nop
                :try_end
                .catch Ljava/io/FileNotFoundException; {:try_start .. :try_end} :handler
                .catch Ljava/io/IOException; {:try_start .. :try_end} :handler
                return-void
                :handler
                move-exception v0
                return-void
            .end method

            .method public static labelAndDebug()I
                .registers 1
                goto :target
                const/4 v0, 0x1
                :target
                .line 42
                const/4 v0, 0x2
                return v0
            .end method

            .method public static lineOnPadding()V
                .registers 2
                const/4 v1, 0x2
                new-array v1, v1, [B
                fill-array-data v1, :data
                return-void
                .line 7
                nop
                :data
                .array-data 1
                    0x1t
                    0x2t
                .end array-data
            .end method

            .method public static lineOnPayload()V
                .registers 2
                const/4 v1, 0x2
                new-array v1, v1, [B
                fill-array-data v1, :data
                return-void
                nop
                .line 7
                :data
                .array-data 1
                    0x1t
                    0x2t
                .end array-data
            .end method
        """

        const val BASE_SOURCE = """
            .class public Ledit/Edit;
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

            .method public static iputWide(Ledit/Edit;J)V
                .registers 3
                iput-wide v1, v0, Ledit/Edit;->wide:J
                return-void
            .end method

            .method public static wideOverflow()V
                .registers 1
                return-void
            .end method
        """

        const val TEMPLATE_SOURCE = """
            .class public Ledit/Templates;
            .super Ljava/lang/Object;

            .method public static simple()V
                .registers 0
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
