package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import com.android.tools.smali.dexlib2.AnnotationVisibility
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.ImmutableExceptionHandler
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.ImmutableTryBlock
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class MethodTryRegionEditTest {
    @TempDir
    lateinit var root: Path
    private var fixtureSequence = 0

    @Test
    fun equalRangeHandlersKeepSubmittedOrderAndPublishFreshRouting() {
        val index = index()
        val services = VmServices(index)
        val snapshotA = index.currentSnapshot
        val viewA = requireNotNull(index.methodView(METHOD))
        val throwAddress = opcodeAddress(viewA, Opcode.DIV_INT)
        val oldTable = (viewA.handlerTableAt(throwAddress) as HandlerTableLookup.Found).table
        val graphA = services.newSession(viewA).run()
        val edit = services.methodEditor.beginEdit(viewA)
        val anchors = anchors(edit, viewA)
        val exception = edit.stageType("Ljava/lang/Exception;")
        edit.stageType("Ljava/lang/Exception;")
        val arithmetic = edit.stageType("Ljava/lang/ArithmeticException;")
        val firstHandlers = mutableListOf<CatchHandlerSpec>(
            CatchHandlerSpec.Typed(exception, anchors.exceptionHandler),
        )
        val submitted = mutableListOf(
            TryRegionSpec(anchors.start, anchors.end, firstHandlers),
            TryRegionSpec(
                anchors.start,
                anchors.end,
                listOf(CatchHandlerSpec.Typed(arithmetic, anchors.arithmeticHandler)),
            ),
        )
        edit.setTryRegions(submitted)
        firstHandlers.clear()
        submitted.clear()

        val result = edit.commit()
        assertTrue(
            result is MethodEditCommitResult.Committed,
            (result as? MethodEditCommitResult.Rejected)?.reason.toString(),
        )
        val committed = result as MethodEditCommitResult.Committed
        val graphB = services.newSession(committed.editedMethodView).run()
        val newTable = (committed.editedMethodView.handlerTableAt(throwAddress) as HandlerTableLookup.Found).table

        assertEquals(Value.Known(2, INT), graphA.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Known(1, INT), graphB.trusted()!!.terminatingConsensus(0))
        assertSame(snapshotA.programRevision, graphA.programRevision)
        assertNotSame(oldTable, newTable)
        assertEquals(
            listOf("Ljava/lang/Exception;", "Ljava/lang/ArithmeticException;"),
            newTable.clauses.map { (it as HandlerClause.Typed).exceptionType.descriptor },
        )
        assertEquals(setOf(METHOD), committed.createdMethodRevisionsBySignature.keys)
        assertTrue(index.currentSnapshot.dexReferenceClosure.types.contains("Ljava/lang/ArithmeticException;"))

        val reloaded = reload(index)
        val reloadedView = requireNotNull(reloaded.methodView(METHOD))
        val reloadedConsensus = VmServices(reloaded).newSession(reloadedView).run().trusted()!!.terminatingConsensus(0)
        assertEquals(Value.Known(1, INT), reloadedConsensus)
        assertEquals(
            listOf("Ljava/lang/Exception;", "Ljava/lang/ArithmeticException;"),
            reloadedView.tryBlocks.single().catches.map { it.exceptionType.descriptor },
        )
    }

    @Test
    fun emptyRangesDisappearWhileLiveTypedAndCatchAllHandlersSurviveReload() {
        val index = index()
        val view = requireNotNull(index.methodView(METHOD))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val anchors = anchors(edit, view)
        val arithmetic = edit.stageType("Ljava/lang/ArithmeticException;")
        val removedType = edit.stageType("Ltask57/RemovedCatch;")
        edit.setTryRegions(
            listOf(
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.Typed(arithmetic, anchors.arithmeticHandler)),
                ),
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.CatchAll(anchors.exceptionHandler)),
                ),
                TryRegionSpec(
                    anchors.end,
                    anchors.end,
                    listOf(CatchHandlerSpec.Typed(removedType, anchors.arithmeticHandler)),
                ),
            ),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed

        assertEquals(1, committed.editedMethodView.tryBlocks.size)
        assertEquals(1, committed.editedMethodView.tryBlocks.single().catches.size)
        assertEquals(
            anchorsAddress(committed, anchors.exceptionHandler),
            committed.editedMethodView.tryBlocks.single().catchAllAddress,
        )
        assertFalse(index.currentSnapshot.dexReferenceClosure.types.contains("Ltask57/RemovedCatch;"))

        val reloaded = reload(index)
        val reloadedTry = requireNotNull(reloaded.methodView(METHOD)).tryBlocks.single()
        assertEquals("Ljava/lang/ArithmeticException;", reloadedTry.catches.single().exceptionType.descriptor)
        assertTrue(reloadedTry.catchAllAddress != null)
    }

    @Test
    fun malformedCatchAllOrderLeavesTheSnapshotAndOutputUnchanged() {
        val index = index()
        val snapshot = index.currentSnapshot
        val output = ProgramDexWriter().writeToBytes(snapshot)
        val view = requireNotNull(index.methodView(METHOD))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val anchors = anchors(edit, view)
        val arithmetic = edit.stageType("Ljava/lang/ArithmeticException;")
        edit.setTryRegions(
            listOf(
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.CatchAll(anchors.exceptionHandler)),
                ),
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.Typed(arithmetic, anchors.arithmeticHandler)),
                ),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.CatchAllNotLast, result.reason)
        assertSame(snapshot, index.currentSnapshot)
        assertArrayEquals(output, ProgramDexWriter().writeToBytes(index.currentSnapshot))
    }

    @Test
    fun distinctOverlappingRangesRejectAtomically() {
        val index = index()
        val snapshot = index.currentSnapshot
        val view = requireNotNull(index.methodView(METHOD))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val anchors = anchors(edit, view)
        val middle = edit.pendingAnchorForCodeAddress(view.instructionAddresses[1])
        edit.setTryRegions(
            listOf(
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.CatchAll(anchors.arithmeticHandler)),
                ),
                TryRegionSpec(
                    middle,
                    anchors.end,
                    listOf(CatchHandlerSpec.CatchAll(anchors.exceptionHandler)),
                ),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.OverlappingTryRanges, result.reason)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun moveExceptionThatNoHandlerTargetsRejects() {
        val index = index()
        val snapshot = index.currentSnapshot
        val view = requireNotNull(index.methodView(METHOD))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val anchors = anchors(edit, view)
        edit.setTryRegions(
            listOf(
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.CatchAll(anchors.end)),
                ),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        // The only handler now targets the normal return; the first move-exception, at 5, starts no handler.
        assertEquals(MethodEditRejectionReason.MoveExceptionOutsideHandler(5), result.reason)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun foreignTryHandlesAndMalformedStagedTypesRejectBeforeDraftConstruction() {
        val index = index()
        val editor = VmServices(index).methodEditor
        val view = requireNotNull(index.methodView(METHOD))
        val foreign = editor.beginEdit(view)
        val foreignType = foreign.stageType("Ljava/lang/Exception;")
        val foreignStart = foreign.pendingAnchorForCodeAddress(0)
        val local = editor.beginEdit(view)
        val localAnchors = anchors(local, view)
        local.setTryRegions(
            listOf(
                TryRegionSpec(
                    foreignStart,
                    localAnchors.end,
                    listOf(CatchHandlerSpec.Typed(foreignType, localAnchors.exceptionHandler)),
                ),
            ),
        )

        val foreignResult = local.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.ForeignAnchor, foreignResult.reason)
        foreign.discard()

        val malformed = editor.beginEdit(view)
        malformed.stageType("[V")
        val malformedResult = malformed.commit() as MethodEditCommitResult.Rejected
        assertEquals(MethodEditRejectionReason.MalformedDescriptor("[V"), malformedResult.reason)

        val arrayCatch = editor.beginEdit(view)
        val arrayCatchAnchors = anchors(arrayCatch, view)
        val arrayType = arrayCatch.stageType("[Ljava/lang/Exception;")
        arrayCatch.setTryRegions(
            listOf(
                TryRegionSpec(
                    arrayCatchAnchors.start,
                    arrayCatchAnchors.end,
                    listOf(CatchHandlerSpec.Typed(arrayType, arrayCatchAnchors.arithmeticHandler)),
                ),
            ),
        )
        val arrayCatchResult = arrayCatch.commit() as MethodEditCommitResult.Rejected
        assertEquals(MethodEditRejectionReason.CatchTypeNotClass("[Ljava/lang/Exception;"), arrayCatchResult.reason)

        val nonThrowable = editor.beginEdit(view)
        val nonThrowableAnchors = anchors(nonThrowable, view)
        val stringType = nonThrowable.stageType("Ljava/lang/String;")
        nonThrowable.setTryRegions(
            listOf(
                TryRegionSpec(
                    nonThrowableAnchors.start,
                    nonThrowableAnchors.end,
                    listOf(CatchHandlerSpec.Typed(stringType, nonThrowableAnchors.arithmeticHandler)),
                ),
            ),
        )
        val nonThrowableResult = nonThrowable.commit() as MethodEditCommitResult.Rejected
        assertEquals(MethodEditRejectionReason.CatchTypeNotThrowable("Ljava/lang/String;"), nonThrowableResult.reason)
    }

    @Test
    fun crossingTheStringIndexBoundaryRelayoutsAllAffectedMethodsInOneSnapshot() {
        val index = stringBoundaryIndex()
        val snapshotA = index.currentSnapshot
        val targetA = requireNotNull(snapshotA.applicationMethod(METHOD))
        val collateralA = requireNotNull(snapshotA.applicationMethod(COLLATERAL))
        val unaffectedA = requireNotNull(snapshotA.applicationMethod(UNAFFECTED))
        assertEquals(65_536, snapshotA.dexReferenceClosure.strings.size)
        assertEquals(StringInstructionEncoding.CONST_STRING, snapshotA.stringInstructionEncoding)

        val view = requireNotNull(index.methodView(METHOD))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val anchors = anchors(edit, view)
        val newCatch = edit.stageType(NEW_CATCH_TYPE)
        val arithmetic = edit.stageType("Ljava/lang/ArithmeticException;")
        val exception = edit.stageType("Ljava/lang/Exception;")
        edit.setTryRegions(
            listOf(
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.Typed(newCatch, anchors.arithmeticHandler)),
                ),
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.Typed(arithmetic, anchors.arithmeticHandler)),
                ),
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.Typed(exception, anchors.exceptionHandler)),
                ),
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(CatchHandlerSpec.CatchAll(anchors.exceptionHandler)),
                ),
            ),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val snapshotB = index.currentSnapshot

        assertEquals(StringInstructionEncoding.CONST_STRING_JUMBO, snapshotB.stringInstructionEncoding)
        assertEquals(65_537, snapshotB.dexReferenceClosure.strings.size)
        assertTrue(NEW_CATCH_TYPE in snapshotB.dexReferenceClosure.strings)
        assertEquals(setOf(METHOD, COLLATERAL), committed.createdMethodRevisionsBySignature.keys)
        assertNotSame(targetA.methodRevision, snapshotB.applicationMethod(METHOD)?.methodRevision)
        assertNotSame(collateralA.methodRevision, snapshotB.applicationMethod(COLLATERAL)?.methodRevision)
        assertSame(unaffectedA, snapshotB.applicationMethod(UNAFFECTED))
        assertSame(snapshotA.frameworkCatalog, snapshotB.frameworkCatalog)
        val collateralView = requireNotNull(index.methodView(COLLATERAL))
        assertEquals(Opcode.CONST_STRING_JUMBO, collateralView.opcodeAt(0))

        val reloaded = reload(index)
        assertEquals(
            Opcode.CONST_STRING_JUMBO,
            requireNotNull(reloaded.methodView(COLLATERAL)).opcodeAt(0),
        )
        assertEquals(
            NEW_CATCH_TYPE,
            requireNotNull(reloaded.methodView(METHOD)).tryBlocks.single().catches.first().exceptionType.descriptor,
        )
    }

    @Test
    fun handlerCanIgnoreTheExceptionWithoutMoveException() {
        val index = index()
        val view = requireNotNull(index.methodView(METHOD))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val anchors = anchors(edit, view)
        val moveException = view.instructionAddresses.first { view.opcodeAt(it) == Opcode.MOVE_EXCEPTION }
        val ignoredHandlerAddress = view.instructionAddresses.first { it > moveException }
        val ignoredHandler = edit.pendingAnchorForCodeAddress(ignoredHandlerAddress)
        val arithmetic = edit.stageType("Ljava/lang/ArithmeticException;")
        val runtime = edit.stageType("Ljava/lang/RuntimeException;")
        val exception = edit.stageType("Ljava/lang/Exception;")
        edit.setTryRegions(
            listOf(
                TryRegionSpec(
                    anchors.start,
                    anchors.end,
                    listOf(
                        CatchHandlerSpec.Typed(arithmetic, ignoredHandler),
                        CatchHandlerSpec.Typed(runtime, anchors.arithmeticHandler),
                        CatchHandlerSpec.Typed(exception, anchors.exceptionHandler),
                    ),
                ),
            ),
        )

        val result = edit.commit()
        assertTrue(
            result is MethodEditCommitResult.Committed,
            (result as? MethodEditCommitResult.Rejected)?.reason.toString(),
        )
        val committed = result as MethodEditCommitResult.Committed

        assertEquals(Value.Known(2, INT), VmServices(index).newSession(committed.editedMethodView).run()
            .trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun ordinaryBranchIntoMoveExceptionRejects() {
        val index = index()
        val snapshot = index.currentSnapshot
        val view = requireNotNull(index.methodView(METHOD))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val anchors = anchors(edit, view)
        edit.replaceInstruction(
            0,
            listOf(
                InstructionTemplate.Branch(
                    ImmutableInstruction10t(Opcode.GOTO, 0),
                    anchors.arithmeticHandler,
                ),
            ),
        )
        edit.setTryRegions(
            listOf(TryRegionSpec(anchors.start, anchors.end, listOf(CatchHandlerSpec.CatchAll(anchors.arithmeticHandler)))),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        // The arithmetic handler's move-exception is at 5: const, const, div-int (2 units), return.
        assertEquals(MethodEditRejectionReason.BranchIntoMoveException(5), result.reason)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun exceptionEdgesParticipateInRegisterValidation() {
        val index = index(EXCEPTION_FLOW_SOURCE)
        val snapshot = index.currentSnapshot
        val view = requireNotNull(index.methodView(EXCEPTION_FLOW_METHOD))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val div = view.instructionAddresses.single { view.opcodeAt(it) == Opcode.DIV_INT_LIT8 }
        val normalReturn = view.instructionAddresses.first { it > div && view.opcodeAt(it) == Opcode.RETURN }
        val handler = view.instructionAddresses.single { view.opcodeAt(it) == Opcode.MOVE_EXCEPTION }
        val arithmetic = edit.stageType("Ljava/lang/ArithmeticException;")
        edit.setTryRegions(
            listOf(
                TryRegionSpec(
                    edit.pendingAnchorForCodeAddress(div),
                    edit.pendingAnchorForCodeAddress(normalReturn),
                    listOf(CatchHandlerSpec.Typed(arithmetic, edit.pendingAnchorForCodeAddress(handler))),
                ),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        // const/4 @0, div-int/lit8 @1 (2 units), return @3, move-exception @4, then `return v1` @5 reads v1.
        assertEquals(MethodEditRejectionReason.ReadBeforeAssignment(codeAddress = 5, register = 1), result.reason)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun dexWriterPreservesAdjacentSplitRangesWithEqualHandlers() {
        val index = index()
        val implementation = ImmutableMethodImplementation(
            0,
            buildList {
                repeat(65_535) { add(ImmutableInstruction10x(Opcode.NOP)) }
                add(ImmutableInstruction10x(Opcode.RETURN_VOID))
            },
            listOf(
                ImmutableTryBlock(0, 65_535, listOf(ImmutableExceptionHandler("Ljava/lang/Exception;", 65_535))),
                ImmutableTryBlock(65_535, 1, listOf(ImmutableExceptionHandler("Ljava/lang/Exception;", 65_535))),
            ),
            emptyList(),
        )
        installBody(index, TRY_BOUNDARY, MethodBody.import(implementation))

        val reloaded = reload(index)
        val tryBlocks = requireNotNull(reloaded.methodView(TRY_BOUNDARY)).tryBlocks

        assertEquals(listOf(65_535, 1), tryBlocks.map { it.endAddress - it.startAddress })
    }

    @Test
    fun handlerListsPastTheSixteenBitOffsetRejectBeforePublication() {
        // The writer emits one handler list per try item (dexlib2 would otherwise merge adjacent equal-handler
        // ranges). 17,000 one-instruction ranges with a catch-all at address 17,001 need four bytes per list, so
        // later try_item.handler_off values pass the 16-bit limit and the edit cannot be written.
        val rangeCount = 17_000
        val index = index(
            buildString {
                appendLine(".class public Ltask57/ManyRanges;")
                appendLine(".super Ljava/lang/Object;")
                appendLine(".method public static run()V")
                appendLine(".registers 1")
                repeat(rangeCount) { appendLine("nop") }
                appendLine("return-void")
                appendLine("move-exception v0")
                appendLine("return-void")
                appendLine(".end method")
            },
        )
        val snapshot = index.currentSnapshot
        val view = requireNotNull(index.methodView("Ltask57/ManyRanges;->run()V"))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val boundaries = (0..rangeCount).map(edit::pendingAnchorForCodeAddress)
        val handler = edit.pendingAnchorForCodeAddress(rangeCount + 1)
        edit.setTryRegions(
            (0 until rangeCount).map { range ->
                TryRegionSpec(boundaries[range], boundaries[range + 1], listOf(CatchHandlerSpec.CatchAll(handler)))
            },
        )

        val result = edit.commit()

        assertTrue((result as? MethodEditCommitResult.Rejected)?.reason is MethodEditRejectionReason.HandlerListOffsetOverflow)
        assertSame(snapshot, index.currentSnapshot)
        assertDoesNotThrow { ProgramDexWriter().writeToBytes(index.currentSnapshot) }
    }

    private data class Anchors(
        val start: PendingCodeAnchor,
        val end: PendingCodeAnchor,
        val arithmeticHandler: PendingCodeAnchor,
        val exceptionHandler: PendingCodeAnchor,
    )

    private fun anchors(edit: MethodEditTransaction, view: MethodView): Anchors {
        val moveExceptions = view.instructionAddresses.filter { view.opcodeAt(it) == Opcode.MOVE_EXCEPTION }
        val normalReturn = view.instructionAddresses.first { address ->
            address > opcodeAddress(view, Opcode.DIV_INT) && view.opcodeAt(address) == Opcode.RETURN
        }
        return Anchors(
            edit.pendingAnchorForCodeAddress(0),
            edit.pendingAnchorForCodeAddress(normalReturn),
            edit.pendingAnchorForCodeAddress(moveExceptions[0]),
            edit.pendingAnchorForCodeAddress(moveExceptions[1]),
        )
    }

    private fun anchorsAddress(
        committed: MethodEditCommitResult.Committed,
        anchor: PendingCodeAnchor,
    ): Int = committed.codeAddressesByPendingAnchor.getValue(anchor)

    private fun index(source: String = SOURCE): ClassIndex {
        val directory = Files.createDirectory(root.resolve("fixture-${fixtureSequence++}"))
        return task53ClassIndex(directory, source)
    }

    private fun installBody(index: ClassIndex, methodSignature: String, body: MethodBody) {
        val snapshot = index.currentSnapshot
        val method = requireNotNull(snapshot.applicationMethod(methodSignature))
        val editedMethod = ApplicationMethodDefinition.withBody(
            method,
            body,
            index.programWorkspace.newApplicationMethodRevision(methodSignature),
        )
        val definingClass = methodSignature.substringBefore("->")
        val editedClass = requireNotNull(snapshot.applicationClass(definingClass)).withMethod(editedMethod)
        val candidate = ProgramSnapshot.candidate(
            snapshot,
            index.programWorkspace.newProgramRevision(),
            snapshot.applicationClasses.putting(definingClass, editedClass),
        )
        index.programWorkspace.publish(candidate)
    }

    private fun reload(index: ClassIndex): ClassIndex {
        val snapshot = index.currentSnapshot
        val dexFile = root.resolve("round-trip-${fixtureSequence++}.dex")
        Files.write(dexFile, ProgramDexWriter().writeToBytes(snapshot))
        return ClassIndex.open(dexFile.toFile(), snapshot.outputApiPolicy.apiLevel)
    }

    /** A fresh import of [SOURCE] whose string section holds exactly 65,536 entries. */
    private fun stringBoundaryIndex(): ClassIndex {
        val annotationType = "Ltask57/StringBoundary;"
        val existingStrings = index().currentSnapshot.dexReferenceClosure.strings
        var stringCount = existingStrings.size + if (annotationType in existingStrings) 0 else 1
        val annotation = StringBuilder("\n.annotation runtime $annotationType\n")
        var sequence = 0
        while (stringCount < 65_536) {
            val name = "boundary_$sequence"
            sequence++
            if (name in existingStrings) continue
            annotation.append(name).append(" = 0x0\n")
            stringCount++
        }
        annotation.append(".end annotation\n")
        val index = index(SOURCE.trimIndent() + annotation)
        assertEquals(65_536, index.currentSnapshot.dexReferenceClosure.strings.size)
        return index
    }

    private fun opcodeAddress(view: MethodView, opcode: Opcode): Int =
        view.instructionAddresses.single { view.opcodeAt(it) == opcode }

    private companion object {
        val INT = VirtualType.of("I")
        const val CLASS_TYPE = "Ltask57/TryEdit;"
        const val METHOD = "Ltask57/TryEdit;->run()I"
        const val COLLATERAL = "Ltask57/TryEdit;->collateral()V"
        const val UNAFFECTED = "Ltask57/TryEdit;->unaffected()V"
        const val TRY_BOUNDARY = "Ltask57/TryEdit;->tryBoundary()V"
        const val NEW_CATCH_TYPE = "Ltask57/NewCatch;"
        const val SOURCE = """
            .class public Ltask57/TryEdit;
            .super Ljava/lang/Object;

            .method public static run()I
                .registers 2
                :try_start
                const/4 v0, 0x1
                const/4 v1, 0x0
                div-int v0, v0, v1
                :try_end
                return v0
                :arithmetic_handler
                move-exception v0
                const/4 v0, 0x2
                return v0
                :exception_handler
                move-exception v0
                const/4 v0, 0x1
                return v0
                .catch Ljava/lang/ArithmeticException; {:try_start .. :try_end} :arithmetic_handler
                .catch Ljava/lang/Exception; {:try_start .. :try_end} :exception_handler
            .end method

            .method public static collateral()V
                .registers 1
                const-string v0, "collateral"
                return-void
            .end method

            .method public static unaffected()V
                .registers 0
                return-void
            .end method

            .method public static tryBoundary()V
                .registers 1
                return-void
            .end method
        """

        const val EXCEPTION_FLOW_METHOD = "Ltask57/ExceptionFlow;->run()I"
        const val EXCEPTION_FLOW_SOURCE = """
            .class public Ltask57/ExceptionFlow;
            .super Ljava/lang/Object;

            .method public static run()I
                .registers 2
                const/4 v0, 0x1
                div-int/lit8 v0, v0, 0x0
                return v0
                move-exception v0
                return v1
            .end method
        """
    }
}
