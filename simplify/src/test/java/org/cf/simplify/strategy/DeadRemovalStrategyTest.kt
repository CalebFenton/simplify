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
import org.cf.simplify.assertCostRatio
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
        val padded = OWNER + "payloadAlignmentNop()[B"
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
        assertEquals(2, returned(services, OWNER + "synchronizedCallMustRemain()I", 0).raw)
        assertEquals(3, returned(services, OWNER + "volatileReadCallMustRemain()I", 0).raw)
    }

    @Test fun aCallWhoseOnlyEffectIsASelfContainedInitializationIsRemoved() {
        // Initializer.<clinit> writes only its own private statics and an array it allocates, so removing the
        // call only delays that initialization to the class's next trigger.
        val services = services()
        val method = OWNER + "classInitializingCall()I"
        optimize(services, method)
        assertFalse(hasCall(view(services, method), "pure"))
        assertEquals(4, returned(services, method, 0).raw)
    }

    @Test fun aConstructorsSelfInitializationRemains() {
        val services = services()
        val method = OWNER + "<init>()V"
        optimize(services, method)
        assertTrue(has(view(services, method), Opcode.INVOKE_DIRECT))
    }

    @Test fun aVoidCallWithNoEffectIsRemoved() {
        val services = services()
        val method = ALLOCATIONS + "voidCallWithNoEffect()V"
        optimize(services, method)
        assertEquals(listOf(Opcode.RETURN_VOID), opcodes(view(services, method)))
    }

    @Test fun anUnobservedAllocationGoesWithItsInitializationAndTheCallsConfinedToIt() {
        val services = services()
        val returning = listOf(Opcode.INVOKE_STATIC, Opcode.MOVE_RESULT_OBJECT, Opcode.RETURN_OBJECT)
        val cases = listOf(
            Dropped(OWNER + "unusedConstructor()V", listOf(Opcode.RETURN_VOID), null),
            Dropped(ALLOCATIONS + "unusedInteger()Ljava/lang/Integer;", listOf(Opcode.CONST_4) + returning, 7),
            Dropped(ALLOCATIONS + "unusedStrings()Ljava/lang/String;", listOf(Opcode.CONST_16) + returning, "42"),
            Dropped(
                ALLOCATIONS + "droppedBuilderChain()Ljava/lang/String;",
                listOf(Opcode.NEW_INSTANCE, Opcode.CONST_STRING, Opcode.INVOKE_DIRECT, Opcode.INVOKE_VIRTUAL,
                    Opcode.MOVE_RESULT_OBJECT, Opcode.RETURN_OBJECT),
                "k",
            ),
        )
        for ((method, kept, value) in cases) {
            if (value != null) assertEquals(value, returned(services, method, 0).raw, method)
            optimize(services, method)
            assertEquals(kept, opcodes(view(services, method)), method)
            assertTrue(view(services, method).payloadAddresses.isEmpty(), method)
        }
        val reloaded = reload(services)
        for ((method, _, value) in cases) {
            if (value != null) assertEquals(value, returned(reloaded, method, 0).raw, method)
        }
    }

    @Test fun anAllocationThatEscapesOrWhoseCallsReachOtherStateRemains() {
        val services = services()
        assertEquals(3, returned(services, ALLOCATIONS + "constructorWritesStatic()I", 0).raw)
        assertEquals("d", returned(services, ALLOCATIONS + "appendedToAnotherBuilder()Ljava/lang/String;", 0).raw)
        for ((method, keptCall) in listOf(
            OWNER + "constructorCall()Ljava/lang/Object;" to "<init>",
            ALLOCATIONS + "storedToField()I" to "<init>",
            ALLOCATIONS + "storedToArray([Ljava/lang/Object;)V" to "<init>",
            ALLOCATIONS + "passedToOpaqueCall()I" to "<init>",
            ALLOCATIONS + "comparedByReference(Ljava/lang/Object;)I" to "<init>",
            ALLOCATIONS + "constructorWritesStatic()I" to "<init>",
            ALLOCATIONS + "allocationTriggersInitializer()V" to "<init>",
            ALLOCATIONS + "appendedToAnotherBuilder()Ljava/lang/String;" to "append",
        )) {
            optimize(services, method)
            assertTrue(has(view(services, method), Opcode.NEW_INSTANCE), method)
            assertTrue(hasCall(view(services, method), keptCall), method)
        }
        assertEquals(3, returned(services, ALLOCATIONS + "constructorWritesStatic()I", 0).raw)
        assertEquals("d", returned(services, ALLOCATIONS + "appendedToAnotherBuilder()Ljava/lang/String;", 0).raw)
    }

    @Test fun structurallyUnreachableStrongCallIsRemoved() {
        val services = services()
        val structural = OWNER + "unreachableStrong()V"
        optimize(services, structural)
        assertFalse(has(view(services, structural), Opcode.INVOKE_STATIC))
    }

    @Test fun aMutableStaticDoesNotProveEitherBranchDeadThroughTheFullPipeline() {
        // The analysed method can run after any write to the static, so both branches are reachable on a device.
        val services = services()
        val gc = OWNER + "mutableStaticMakesOneBranchLookDead()V"
        val definition = GUARDS + "reachedDefinitionUsedOnlyByMutableBranch(I)I"
        pipeline(services, gc)
        pipeline(services, definition)
        assertTrue(hasCall(view(services, gc), "gc"))
        // Returns 7 when mutableBranch is non-zero and 0 when it is zero.
        assertEquals(
            setOf<Any?>(0, 7),
            execute(services, definition).outcomes.map { ((it as Outcome.Returned).value as Value.Known).raw }.toSet(),
        )
        assertEquals(3342, returned(services, GUARDS + "run()Ljava/lang/Object;", 0).raw)
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

    @Test fun livenessOverALoopWithThousandsOfLiveRegistersIsQuadraticInMethodSize() {
        // Every loop move reads a distinct register, so each loop position carries all of them live, and the
        // initializing prologue produces one distinct shrinking set per instruction: quadratic in method size.
        // Four times the registers costs liveness about 16 times as much. A worklist that moves liveness back one
        // instruction per pass is cubic and costs about 64 times as much.
        val small = WorstCaseLoop(LIVENESS_REGISTERS)
        val large = WorstCaseLoop(4 * LIVENESS_REGISTERS)
        val strategy = DeadRemovalStrategy()

        assertCostRatio(32.0, { small.input() }, { large.input() }) { input ->
            try {
                strategy.perform(input)
            } finally {
                input.transaction.discard()
            }
        }

        // The largest such loop a method holds still optimizes and keeps its value.
        val registers = 10_900
        val worst = WorstCaseLoop(registers)
        Optimizer(worst.services, LIMITS, 1, listOf(strategy)).optimize(WORST)
        assertEquals(registers + 1, count(view(worst.services, WORST), Opcode.MOVE_16))
        assertEquals(0, returned(worst.services, WORST, 0).raw)
    }

    /** A method whose loop reads each of [registers] registers once per lap, after a prologue that sets them all. */
    private inner class WorstCaseLoop(registers: Int) {
        val services: VmServices
        private val trusted: TrustedGraphView by lazy { requireNotNull(execute(services, WORST).trusted()) }

        init {
            val root = temporaryDirectory.resolve("worst$registers")
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
            services = services(root.toString())
        }

        /** A fresh edit of the analysed method; the caller discards it. */
        fun input() = StrategyInput(trusted, services.methodEditor.beginEdit(trusted))
    }

    private data class UnusedResult(val name: String, val call: Opcode, val result: Opcode, val expected: Any)
    private data class Dropped(val method: String, val kept: List<Opcode>, val returned: Any?)
    private fun optimize(services: VmServices, method: String) =
        Optimizer(services, LIMITS, 10, listOf(DeadRemovalStrategy())).optimize(method)
    /** The strategies `Launcher` runs by default, in its order. */
    private fun pipeline(services: VmServices, method: String) = Optimizer(
        services, LIMITS, 10,
        listOf(ConstantPropagationStrategy(), DeadRemovalStrategy(), PeepholeStrategy(), UnreflectionStrategy()),
    ).optimize(method)
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
    private fun opcodes(view: MethodView) = view.instructionAddresses.map(view::opcodeAt)
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
        private const val ALLOCATIONS = "Ldeadcode/Allocations;->"
        private const val GUARDS = "LDeadRemovalGuards;->"
        private val LIMITS = Limits(500, 1_000_000, 0, 20)
        private const val WORST = "LWorst;->run()I"
        private const val LIVENESS_REGISTERS = 300
    }
}
