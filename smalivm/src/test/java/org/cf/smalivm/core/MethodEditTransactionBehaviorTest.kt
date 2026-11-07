package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.debug.LineNumber
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction30t
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class MethodEditTransactionBehaviorTest {
    @TempDir
    lateinit var root: Path

    private var fixtureSequence = 0

    @Test
    fun insertionPublishesViewBWithoutChangingViewA() {
        val index = index()
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
    fun entryAndMiddleRemovalUseBaselineAddressesWithoutChangingViewA() {
        val index = index()
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(REMOVAL))
        val shapeA = instructionShape(viewA)
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.removeInstructions(listOf(0, 1))

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
        val index = index()
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
        val index = index()
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
        val insertionIndex = index()
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

        val removalIndex = index()
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
        val index = index()
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
    fun oneToManyReplacementKeepsSubmittedOrderWithoutChangingViewA() {
        val index = index()
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(MULTI_REPLACEMENT))
        val shapeA = instructionShape(viewA)
        val edit = VmServices(index).methodEditor.beginEdit(viewA)
        edit.replaceInstruction(
            1,
            listOf(
                encoded(ImmutableInstruction21s(Opcode.CONST_16, 1, 2)),
                encoded(ImmutableInstruction11n(Opcode.CONST_4, 2, 3)),
            ),
        )

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
        val index = index()
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
        val index = index()
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

    private fun index(): ClassIndex {
        val directory = Files.createDirectory(root.resolve("fixture-${fixtureSequence++}"))
        return task53ClassIndex(directory, SOURCE)
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

    private fun encoded(instruction: com.android.tools.smali.dexlib2.iface.instruction.Instruction): InstructionTemplate =
        InstructionTemplate.Encoded(instruction)

    private companion object {
        val INT = VirtualType.of("I")
        const val INSERTION = "Ltask61/EditBehavior;->insertion()I"
        const val REMOVAL = "Ltask61/EditBehavior;->removal()I"
        const val BRANCH_WIDENING = "Ltask61/EditBehavior;->branchWidening()V"
        const val BRANCH_REPLACEMENT = "Ltask61/EditBehavior;->branchReplacement()I"
        const val PAYLOAD_WITHOUT_PADDING = "Ltask61/EditBehavior;->payloadWithoutPadding()V"
        const val PAYLOAD_WITH_PADDING = "Ltask61/EditBehavior;->payloadWithPadding()V"
        const val WIDTH_REPLACEMENT = "Ltask61/EditBehavior;->widthReplacement()I"
        const val MULTI_REPLACEMENT = "Ltask61/EditBehavior;->multiReplacement()I"
        const val TRY_CLEANUP = "Ltask61/EditBehavior;->tryCleanup()V"
        const val LABEL_AND_DEBUG = "Ltask61/EditBehavior;->labelAndDebug()I"

        const val SOURCE = """
            .class public Ltask61/EditBehavior;
            .super Ljava/lang/Object;
            .source "task61.smali"

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
        """
    }
}
