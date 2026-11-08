package org.cf.simplify.strategy

import java.nio.file.Files
import java.nio.file.Path
import java.io.File
import org.cf.simplify.Optimizer
import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.SwitchPayload
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.cf.simplify.methodView
import org.cf.simplify.optimize

class DeadRemovalStrategyTest {
    @TempDir lateinit var temporaryDirectory: Path

    @Test fun overwrittenNarrowAndWideAssignmentsAreRemovedButLiveBranchValuesRemain() {
        val services = services()
        for ((methodName, opcode, expected) in listOf(
            Triple("overwritten()I", Opcode.CONST_4, 2),
            Triple("overwrittenWide()J", Opcode.CONST_WIDE_16, 2L),
        )) {
            val method = OWNER + methodName
            assertEquals(expected, returned(services, method, 0).raw)
            optimize(services, method)
            assertEquals(1, count(view(services, method), opcode))
            assertEquals(expected, returned(services, method, 0).raw)
        }
        val branch = OWNER + "liveOnBranch(I)I"
        optimize(services, branch)
        assertEquals(2, count(view(services, branch), Opcode.CONST_4))
    }

    @Test fun aConversionFromANarrowSourceDoesNotKeepTheRegisterAboveItLive() {
        // int-to-long reads only v2; v3 is never read, so its assignment is dead.
        val services = services()
        val method = OWNER + "narrowSourceOfWideConversion()J"
        optimize(services, method)
        val edited = view(services, method)
        assertEquals(1, count(edited, Opcode.CONST_4))
        assertEquals(1L, returned(services, method, 0).raw)
    }

    @Test fun unusedMoveResultsThenPureCallsDisappearOnSuccessivePasses() {
        val services = services()
        for ((name, call, result, expected) in listOf(
            UnusedResult("unusedMoveResult()I", Opcode.INVOKE_STATIC, Opcode.MOVE_RESULT, 4),
            UnusedResult("unusedMoveResultRange()I", Opcode.INVOKE_STATIC_RANGE, Opcode.MOVE_RESULT, 4),
            UnusedResult("unusedObjectResults()Ljava/lang/String;", Opcode.INVOKE_STATIC, Opcode.MOVE_RESULT_OBJECT, "other stringy"),
        )) {
            val method = OWNER + name
            assertEquals(expected, returned(services, method, 0).raw, name)
            optimize(services, method)
            assertFalse(has(view(services, method), result), name)
            assertFalse(has(view(services, method), call), name)
            assertEquals(expected, returned(services, method, 0).raw, name)
        }
    }

    @Test fun unreachableInstructionsAndAGotoToTheNextInstructionAreRemoved() {
        val services = services()
        val unreachable = OWNER + "unreachableArithmeticAndNops()V"
        val goto = OWNER + "gotoNextInstruction()V"
        optimize(services, unreachable)
        optimize(services, goto)
        assertFalse(has(view(services, unreachable), Opcode.ADD_INT_2ADDR))
        assertFalse(has(view(services, unreachable), Opcode.NOP))
        assertFalse(has(view(services, goto), Opcode.GOTO))
    }

    @Test fun payloadAlignmentNopAndAMoveFeedingAnEffectfulCallRemain() {
        val services = services()
        val padded = OWNER + "payloadAlignmentNop()V"
        val moves = OWNER + "liveMoveFeedsCallDeadMoveDoesNot(I)V"
        optimize(services, padded)
        optimize(services, moves)
        // The nop after return-void puts the fill-array-data payload on the four-byte boundary it requires.
        assertEquals(1, count(view(services, padded), Opcode.NOP))
        // p0 is v30, which invoke-static's four-bit operands cannot name: the move into v0 feeds the call,
        // and the move back into p0 writes a register nothing reads.
        val edited = view(services, moves)
        val kept = edited.instructionAddresses.filter { edited.opcodeAt(it) == Opcode.MOVE_FROM16 }
        assertEquals(listOf(0), kept.map { (edited.instructionAt(it) as OneRegisterInstruction).registerA })
        assertTrue(has(edited, Opcode.INVOKE_STATIC))
    }

    @Test fun callsThatCanThrowRemainEvenWithNoExternalSideEffect() {
        val services = services()
        for (name in listOf("catchesThrowingNone()I", "escapingThrowingNone()I", "mixedCallMustRemain(I)I")) {
            val method = OWNER + name
            optimize(services, method)
            assertTrue(has(view(services, method), Opcode.INVOKE_STATIC), name)
        }
        assertEquals(5, returned(services, OWNER + "catchesThrowingNone()I", 0).raw)
        assertEquals(3, returned(services, OWNER + "mixedCallMustRemain(I)I", 0).raw)
    }

    @Test fun exceptionEdgesKeepTheOldWideValueReadByTheHandler() {
        val services = services()
        val method = OWNER + "wideValueSurvivesThrow()J"
        assertEquals(0x42L, returned(services, method, 0).raw)
        optimize(services, method)
        val edited = view(services, method)
        assertTrue(has(edited, Opcode.AGET_WIDE))
        assertTrue(has(edited, Opcode.CONST_WIDE_16))
        assertTrue(has(edited, Opcode.MOVE_EXCEPTION))
        assertFalse(edited.tryBlocks.isEmpty())
        assertEquals(0x42L, returned(services, method, 0).raw)
    }

    @Test fun weakStrongSynchronizedVolatileAndInitializationEffectsAreRetained() {
        val services = services()
        for ((name, callee) in listOf(
            "mutableArgumentCall()[I" to "mutateArgument",
            "staticRootMutation()I" to "mutateRoot",
            "classInitializingCall()I" to "pure",
            "reachedStrongCall()V" to "strongNoArgs",
            "sideEffectingCall()I" to "visibleWrite",
            "synchronizedCallMustRemain()I" to "synchronizedPure",
            "volatileReadCallMustRemain()I" to "readVolatile",
        )) {
            val method = OWNER + name
            optimize(services, method)
            assertTrue(hasCall(view(services, method), callee), name)
        }
        assertEquals(listOf(9), returnedArray(services, OWNER + "mutableArgumentCall()[I", 1))
        assertEquals(8, returned(services, OWNER + "staticRootMutation()I", 0).raw)
        assertEquals(4, returned(services, OWNER + "classInitializingCall()I", 0).raw)
        assertEquals(2, returned(services, OWNER + "synchronizedCallMustRemain()I", 0).raw)
        assertEquals(3, returned(services, OWNER + "volatileReadCallMustRemain()I", 0).raw)
    }

    @Test fun constructorsAndSelfInitializationRemain() {
        val services = services()
        for (name in listOf("constructorCall()Ljava/lang/Object;", "unusedConstructor()V", "<init>()V")) {
            val method = OWNER + name
            optimize(services, method)
            assertTrue(has(view(services, method), Opcode.INVOKE_DIRECT), name)
            if (name != "<init>()V") assertTrue(has(view(services, method), Opcode.NEW_INSTANCE), name)
        }
    }

    @Test fun structuralDeadCodeIsRemovedButAStaticValueDoesNotProveAnotherBranchDead() {
        val services = services()
        val structural = OWNER + "unreachableStrong()V"
        val mutable = OWNER + "mutableStaticMakesOneBranchLookDead()V"
        optimize(services, structural)
        optimize(services, mutable)
        assertFalse(has(view(services, structural), Opcode.INVOKE_STATIC))
        assertTrue(has(view(services, mutable), Opcode.INVOKE_STATIC))
        // The branch that returns p0 never ran, so liveness has no read evidence for it and keeps the definition.
        val definition = GUARDS + "reachedDefinitionUsedOnlyByMutableBranch(I)I"
        optimize(services, definition)
        val edited = view(services, definition)
        assertTrue(edited.instructionAddresses.any {
            (edited.instructionAt(it) as? NarrowLiteralInstruction)?.narrowLiteral == 7
        })
    }

    @Test fun unknownArrayAccessMixedCallAndClassForNameAreNotDeleted() {
        val services = services()
        for ((name, opcode) in listOf(
            "deadArrayReadMustRemain([II)I" to Opcode.AGET,
            "mixedCallMustRemain(I)I" to Opcode.INVOKE_STATIC,
            "unusedClassForNameResultMustRemain()I" to Opcode.INVOKE_STATIC,
        )) {
            val method = GUARDS + name
            optimize(services, method)
            assertTrue(has(view(services, method), opcode), name)
        }
        assertEquals(3, returned(services, GUARDS + "mixedCallMustRemain(I)I", 0).raw)
    }

    @Test fun guardedCalleesKeepEveryCallersNormalAndThrowingOutcome() {
        val services = services()
        val run = GUARDS + "run()Ljava/lang/Object;"
        // Hand-traced from the fixture: every check passes, so run() boxes 3342.
        assertEquals(3342, returned(services, run, 0).raw)
        for (name in listOf("mixedCallMustRemain(I)I", "deadArrayReadMustRemain([II)I",
            "reachedDefinitionUsedOnlyByMutableBranch(I)I", "mixedCallReturns()I", "mixedCallThrowCaught()I",
            "arrayReadReturns()I", "arrayReadThrowCaught()I", "branchOnZero()I", "branchOnOne()I")) {
            optimize(services, GUARDS + name)
        }
        assertEquals(3342, returned(services, run, 0).raw)
        assertEquals(3342, returned(reload(services), run, 0).raw)
    }

    @Test fun payloadTargetsCatchHandlersAndTerminalsSurviveCommitAndDexReload() {
        val services = services()
        val switch = OWNER + "switchTarget()I"
        val array = OWNER + "arrayPayload()[B"
        val handler = OWNER + "catchesThrowingNone()I"
        val unreachable = OWNER + "unreachablePayloadOwner()V"
        val unreachableShapes = listOf(OWNER + "unreachableTargetAndHandler()V", OWNER + "unreachableLoopGraph()V")
        for (method in listOf(switch, array, handler, unreachable) + unreachableShapes) optimize(services, method)
        val reloaded = reload(services)
        for (method in unreachableShapes) {
            assertTrue(execute(reloaded, method).outcomes.single() is Outcome.Returned, method)
        }
        assertTrue(has(view(reloaded, switch), Opcode.SPARSE_SWITCH))
        assertTrue(view(reloaded, switch).payloadAddresses.isNotEmpty())
        assertTrue(has(view(reloaded, array), Opcode.FILL_ARRAY_DATA))
        assertTrue(view(reloaded, array).payloadAddresses.isNotEmpty())
        assertTrue(has(view(reloaded, handler), Opcode.MOVE_EXCEPTION))
        assertTrue(view(reloaded, handler).tryBlocks.isNotEmpty())
        assertEquals(7, returned(reloaded, switch, 0).raw)
        assertEquals(5, returned(reloaded, handler, 0).raw)
        assertEquals(listOf(4.toByte(), 5.toByte()), returnedArray(reloaded, array, 1))
        val unreachableView = view(reloaded, unreachable)
        val address = unreachableView.instructionAddresses.first { unreachableView.opcodeAt(it) == Opcode.SPARSE_SWITCH }
        val offset = (unreachableView.instructionAt(address) as OffsetInstruction).codeOffset
        val payload = unreachableView.payloadAt(address + offset) as SwitchPayload
        assertEquals(Opcode.NOP, unreachableView.opcodeAt(address + payload.switchElements.single().offset))
        assertTrue(has(unreachableView, Opcode.RETURN_VOID))
    }

    @Test fun partialGraphCannotPublishStructuralCleanup() {
        val services = services()
        val snapshot = services.currentSnapshot
        val method = OWNER + "partialWithUnreachableStrong()V"
        val result = Optimizer(services, Limits(1, 100, 0, 20), 5, listOf(DeadRemovalStrategy())).optimize(method)
        assertTrue(result.completionStatus is CompletionStatus.Partial)
        assertEquals(0, result.committedEditCount)
        assertSame(snapshot, services.currentSnapshot)
        assertTrue(has(view(services, method), Opcode.INVOKE_STATIC))
    }

    @Test fun highRegisterWorklistPreservesTheReturnValue() {
        val root = temporaryDirectory.resolve("scale")
        Files.createDirectories(root)
        val smali = buildString {
            append(".class public LScale;\n.super Ljava/lang/Object;\n.method public static run()I\n.registers 60001\nconst/4 v0, 0x7\n")
            repeat(9999) { append("move/16 v60000, v0\n") }
            append("return v0\n.end method\n")
        }
        Files.writeString(root.resolve("Scale.smali"), smali)
        val services = services(root.toString())
        Optimizer(services, LIMITS, 1, listOf(DeadRemovalStrategy())).optimize("LScale;->run()I")
        assertFalse(has(view(services, "LScale;->run()I"), Opcode.MOVE_16))
        assertEquals(7, returned(services, "LScale;->run()I", 0).raw)
    }

    @Test fun livenessOverAMaximumSizeLoopWithThousandsOfLiveRegistersFinishesPromptly() {
        // Every loop move reads a distinct register, so each loop position carries all of them live, and the
        // initializing prologue produces one distinct shrinking set per instruction: quadratic in method size.
        val registers = 10_900
        val root = temporaryDirectory.resolve("worst")
        Files.createDirectories(root)
        val smali = buildString {
            append(".class public LWorst;\n.super Ljava/lang/Object;\n.method public static run()I\n.registers ${registers + 2}\n")
            append("const/4 v0, 0x0\nconst/4 v1, 0x1\n")
            for (register in 2..registers + 1) append("move/16 v$register, v0\n")
            append(":top\n")
            for (register in 2..registers + 1) append("move/16 v0, v$register\n")
            append("if-eqz v1, :exit\nconst/4 v1, 0x0\ngoto/32 :top\n:exit\nreturn v0\n.end method\n")
        }
        Files.writeString(root.resolve("Worst.smali"), smali)
        val services = services(root.toString())
        val strategy = DeadRemovalStrategy()
        var performNanos = 0L
        val timed = OptimizationStrategy { input ->
            val start = System.nanoTime()
            strategy.perform(input)
            performNanos += System.nanoTime() - start
        }
        Optimizer(services, LIMITS, 1, listOf(timed)).optimize("LWorst;->run()I")
        val edited = view(services, "LWorst;->run()I")
        assertEquals(registers + 1, count(edited, Opcode.MOVE_16))
        assertEquals(0, returned(services, "LWorst;->run()I", 0).raw)
        assertTrue(performNanos < 30_000_000_000L, "liveness took ${performNanos / 1_000_000} ms")
    }

    private data class UnusedResult(val name: String, val call: Opcode, val result: Opcode, val expected: Any)
    private fun optimize(services: VmServices, method: String) =
        Optimizer(services, LIMITS, 10, listOf(DeadRemovalStrategy())).optimize(method)
    private fun services(root: String = "src/test/resources/dead-code") = VmServices(ClassIndex.open(File(root), 20))
    private fun view(services: VmServices, method: String) = requireNotNull(services.classIndex.methodView(method))
    private fun execute(services: VmServices, method: String) = services.newSession(view(services, method), LIMITS).run()
    private fun returned(services: VmServices, method: String, register: Int): Value.Known {
        val graph = execute(services, method)
        val trusted = requireNotNull(graph.trusted()) { "$method: ${graph.completion()}" }
        return trusted.terminatingConsensus(register) as Value.Known
    }
    private fun returnedArray(services: VmServices, method: String, register: Int): List<Any?> {
        val trusted = requireNotNull(execute(services, method).trusted())
        val value = trusted.terminatingConsensus(register) as Value.Known
        val array = (trusted.terminatingHeapObject(value.raw as HeapRef) as Agreement.Agreed).value as HeapObject.TrackedArray
        return array.elements().map { (it as Value.Known).raw }
    }
    private fun has(view: MethodView, opcode: Opcode) = count(view, opcode) != 0
    private fun count(view: MethodView, opcode: Opcode) = view.instructionAddresses.count { view.opcodeAt(it) == opcode }
    private fun hasCall(view: MethodView, name: String) = view.instructionAddresses.any {
        ((view.instructionAt(it) as? ReferenceInstruction)?.reference as? MethodReference)?.name == name
    }
    private fun reload(services: VmServices): VmServices {
        val dex = temporaryDirectory.resolve("dead-removal.dex").toFile()
        ProgramDexWriter().write(services.currentSnapshot, dex)
        return VmServices(ClassIndex.open(dex, 20))
    }
    companion object {
        private const val OWNER = "Ldeadcode/DeadCode;->"
        private const val GUARDS = "LDeadRemovalGuards;->"
        private val LIMITS = Limits(500, 1_000_000, 0, 20)
    }
}
