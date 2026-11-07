package org.cf.simplify.strategy

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.io.File
import org.cf.simplify.Optimizer
import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.SwitchPayload
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

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

    @Test fun unusedMoveResultsThenPureCallsDisappearOnSuccessivePasses() {
        val services = services()
        for ((name, opcode) in listOf("unusedMoveResult()I" to Opcode.INVOKE_STATIC,
            "unusedMoveResultRange()I" to Opcode.INVOKE_STATIC_RANGE)) {
            val method = OWNER + name
            assertEquals(4, returned(services, method, 0).raw)
            optimize(services, method)
            assertFalse(has(view(services, method), Opcode.MOVE_RESULT))
            assertFalse(has(view(services, method), opcode))
            assertEquals(4, returned(services, method, 0).raw)
        }
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
        // The unseen branch's register uses are unavailable in this engine. Liveness must refuse,
        // rather than interpreting an empty graph read-set as static proof that p0 is dead.
        val definition = "LFx_DeadRemovalRemediation;->reachedDefinitionUsedOnlyByMutableBranch(I)I"
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
            val method = "LFx_DeadRemovalRemediation;->$name"
            optimize(services, method)
            assertTrue(has(view(services, method), opcode), name)
        }
        assertEquals(3, returned(services, "LFx_DeadRemovalRemediation;->mixedCallMustRemain(I)I", 0).raw)
    }

    @Test fun payloadTargetsCatchHandlersAndTerminalsSurviveCommitAndDexReload() {
        val services = services()
        val switch = OWNER + "switchTarget()I"
        val array = OWNER + "arrayPayload()[B"
        val handler = OWNER + "catchesThrowingNone()I"
        val unreachable = OWNER + "unreachablePayloadOwner()V"
        for (method in listOf(switch, array, handler, unreachable)) optimize(services, method)
        val reloaded = reload(services)
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

    @Test fun originalLegacyScenariosUseCommittedSnapshotsInsteadOfManipulatorInternals() {
        val root = temporaryDirectory.resolve("legacy")
        Files.createDirectories(root)
        Files.copy(Path.of("src/test/resources/smali/dead_removal_strategy_test.smali"),
            root.resolve("dead_removal_strategy_test.smali"), StandardCopyOption.REPLACE_EXISTING)
        val services = services(root.toString())
        val owner = "Ldead_removal_strategy_test;->"
        for (name in listOf("unusedAssignment()I", "unusedResultNoSideEffects()I", "moveResult()Ljava/lang/String;",
            "deadCode()V", "deadCodeWithStrongSideEffect()V", "uselessGoto()V", "hasNopPadding()V",
            "reassignedInOnlyOneMultiverse(I)I", "moveP0IntoV0With30Locals(I)V", "unusedResultWithSideEffects()I")) {
            optimize(services, owner + name)
        }
        assertEquals(2, returned(services, owner + "unusedAssignment()I", 0).raw)
        assertEquals(2, returned(services, owner + "unusedResultNoSideEffects()I", 0).raw)
        assertEquals("other stringy", returned(services, owner + "moveResult()Ljava/lang/String;", 0).raw)
        assertFalse(has(view(services, owner + "unusedResultNoSideEffects()I"), Opcode.INVOKE_STATIC))
        assertFalse(has(view(services, owner + "moveResult()Ljava/lang/String;"), Opcode.MOVE_RESULT))
        assertFalse(has(view(services, owner + "deadCode()V"), Opcode.ADD_INT_2ADDR))
        assertFalse(has(view(services, owner + "deadCode()V"), Opcode.NOP))
        assertFalse(has(view(services, owner + "deadCodeWithStrongSideEffect()V"), Opcode.INVOKE_STATIC))
        assertFalse(has(view(services, owner + "uselessGoto()V"), Opcode.GOTO))
        assertEquals(1, count(view(services, owner + "hasNopPadding()V"), Opcode.NOP))
        assertEquals(3, count(view(services, owner + "reassignedInOnlyOneMultiverse(I)I"), Opcode.CONST_4))
        assertTrue(has(view(services, owner + "moveP0IntoV0With30Locals(I)V"), Opcode.MOVE_OBJECT_FROM16))
        assertTrue(has(view(services, owner + "unusedResultWithSideEffects()I"), Opcode.INVOKE_STATIC))
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

    private fun optimize(services: VmServices, method: String) =
        Optimizer(services, LIMITS, 10, listOf(DeadRemovalStrategy())).optimize(method)
    private fun services(root: String = "src/test/resources/task66") = VmServices(ClassIndex.open(File(root), 20))
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
        val array = trusted.terminatingHeapObject(value.raw as HeapRef) as HeapObject.TrackedArray
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
        private const val OWNER = "Ltask66/DeadCode;->"
        private val LIMITS = Limits(500, 1_000_000, 0, 20)
    }
}
