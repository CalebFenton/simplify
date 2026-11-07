package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.debug.StartLocal
import com.android.tools.smali.dexlib2.iface.debug.LineNumber
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class ScratchRegisterAllocatorTest {
    @TempDir
    lateinit var root: Path
    private var fixtureSequence = 0

    @Test
    fun reservationsReturnExactRangesAndCommitAsOneRegisterGrowth() {
        val index = index(SOURCE)
        val view = requireNotNull(index.methodView(VALUE))
        val edit = VmServices(index).methodEditor.beginEdit(view)

        val first = edit.reserveScratchRegisters(1)
        val second = edit.reserveScratchRegisters(2)
        edit.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction11n(Opcode.CONST_4, 0, 7),
                    listOf(first[0]),
                ),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Committed
        val inserted = result.editedMethodView.instructionAt(0) as OneRegisterInstruction

        assertEquals(1, first.wordCount)
        assertEquals(2, second.wordCount)
        assertNotSame(first[0], second[0])
        assertEquals(4, result.editedMethodView.registerCount)
        assertEquals(0, inserted.registerA)
    }

    @Test
    fun foreignScratchRegisterRejectsWithoutPublication() {
        val index = index(SOURCE)
        val snapshot = index.currentSnapshot
        val editor = VmServices(index).methodEditor
        val owner = editor.beginEdit(requireNotNull(index.methodView(VALUE)))
        val foreign = owner.reserveScratchRegisters(1)
        val edit = editor.beginEdit(requireNotNull(index.methodView(VALUE)))
        edit.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction11n(Opcode.CONST_4, 0, 1),
                    listOf(foreign[0]),
                ),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected
        owner.discard()

        assertTrue(result.reason is MethodEditRejectionReason.InvalidInstructionTemplate)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun wideOperandCannotStartAtTheFinalScratchWord() {
        val index = index(SOURCE)
        val snapshot = index.currentSnapshot
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(VALUE)))
        val scratch = edit.reserveScratchRegisters(2)
        edit.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21s(Opcode.CONST_WIDE_16, 0, 7),
                    listOf(scratch[1]),
                ),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        assertTrue(result.reason is MethodEditRejectionReason.InvalidInstructionTemplate)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun staticRemapRepairsHighNarrowAndReferenceBranchesWithoutChangingLabels() {
        val index = index(SOURCE)
        val services = VmServices(index)
        val intEdit = services.methodEditor.beginEdit(requireNotNull(index.methodView(HIGH_INT)))
        intEdit.reserveScratchRegisters(1)
        val intResult = intEdit.commit() as MethodEditCommitResult.Committed
        val objectEdit = services.methodEditor.beginEdit(requireNotNull(index.methodView(HIGH_OBJECT)))
        objectEdit.reserveScratchRegisters(1)
        val objectResult = objectEdit.commit() as MethodEditCommitResult.Committed

        val currentIntView = requireNotNull(index.methodView(HIGH_INT))
        assertEquals(7, executeInt(services, currentIntView, Value.Known(0, INT)))
        assertEquals(4, executeInt(services, currentIntView, Value.Known(4, INT)))
        assertEquals(
            Value.Known(0, INT),
            executeSingleParameter(services, objectResult.editedMethodView, Value.Known(null, OBJECT)),
        )
        val marker = Value.Known("marker", OBJECT)
        assertEquals(marker, executeSingleParameter(services, objectResult.editedMethodView, marker))
        assertTrue(intResult.editedMethodView.addresses.any { address ->
            !intResult.editedMethodView.isPayload(address) && intResult.editedMethodView.opcodeAt(address) == Opcode.MOVE_FROM16
        })
        assertTrue(objectResult.editedMethodView.addresses.any { address ->
            !objectResult.editedMethodView.isPayload(address) &&
                objectResult.editedMethodView.opcodeAt(address) == Opcode.MOVE_OBJECT_FROM16
        })

        val intBody = requireNotNull(index.currentSnapshot.applicationMethod(HIGH_INT)?.body)
        val line = intBody.implementation.debugItems.filterIsInstance<LineNumber>().single()
        val local = intBody.implementation.debugItems.filterIsInstance<StartLocal>().single()
        val instructionBeforeLine = intResult.editedMethodView.instructionAddresses.last { it < line.codeAddress }
        assertEquals(Opcode.IF_EQZ, intResult.editedMethodView.opcodeAt(line.codeAddress))
        assertEquals(Opcode.MOVE_FROM16, intResult.editedMethodView.opcodeAt(instructionBeforeLine))
        assertEquals(257, local.register)
        val branchTarget = requireNotNull(intResult.editedMethodView.controlTargetAt(line.codeAddress))
        assertEquals(Opcode.MOVE_FROM16, intResult.editedMethodView.opcodeAt(branchTarget))
    }

    @Test
    fun staticRemapKeepsInvokeRangeArgumentsContiguous() {
        val index = index(SOURCE)
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(RANGE)))
        edit.reserveScratchRegisters(1)

        val result = edit.commit() as MethodEditCommitResult.Committed
        val rangeInstruction = result.editedMethodView.addresses.asSequence()
            .mapNotNull { address ->
                if (result.editedMethodView.isPayload(address)) null
                else result.editedMethodView.instructionAt(address) as? RegisterRangeInstruction
            }
            .single()
        val frame = Frame.entryFrame(result.editedMethodView)
        result.editedMethodView.parameters.forEachIndexed { index, parameter ->
            frame.assign(parameter.register, Value.Known(index + 1, INT))
        }
        val graph = services.newSession(result.editedMethodView, entryFrame = frame).run()

        assertEquals(251, rangeInstruction.startRegister)
        assertEquals(6, rangeInstruction.registerCount)
        assertEquals(Value.Known(1, INT), graph.trusted()!!.terminatingConsensus(1))
    }

    @Test
    fun staticRemapLegalizesNarrowThreeRegisterAndWideFormats() {
        val index = index(SOURCE)
        val services = VmServices(index)
        val newArrayEdit = services.methodEditor.beginEdit(requireNotNull(index.methodView(NEW_ARRAY)))
        newArrayEdit.reserveScratchRegisters(1)
        val newArrayResult = newArrayEdit.commit() as MethodEditCommitResult.Committed
        val newArrayFrame = Frame.entryFrame(newArrayResult.editedMethodView)
        newArrayFrame.assign(newArrayResult.editedMethodView.parameters.single().register, Value.Known(5, INT))
        val newArrayGraph = services.newSession(newArrayResult.editedMethodView, entryFrame = newArrayFrame).run()

        val wideEdit = services.methodEditor.beginEdit(requireNotNull(index.methodView(WIDE_MATH)))
        wideEdit.reserveScratchRegisters(2)
        val wideResult = wideEdit.commit() as MethodEditCommitResult.Committed
        val wideFrame = Frame.entryFrame(wideResult.editedMethodView)
        wideFrame.assign(wideResult.editedMethodView.parameters[0].register, Value.Known(5L, LONG))
        wideFrame.assign(wideResult.editedMethodView.parameters[1].register, Value.Known(7L, LONG))
        val wideGraph = services.newSession(wideResult.editedMethodView, entryFrame = wideFrame).run()

        assertEquals(Value.Known(5, INT), returnedValue(newArrayGraph))
        assertEquals(Value.Known(12L, LONG), returnedValue(wideGraph))
    }

    @Test
    fun staticRemapPreservesNonRangeInvokeArgumentKinds() {
        val index = index(SOURCE)
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(NON_RANGE)))
        edit.reserveScratchRegisters(1)

        val result = edit.commit() as MethodEditCommitResult.Committed
        val graph = services.newSession(result.editedMethodView).run()
        val opcodes = result.editedMethodView.addresses.asSequence()
            .filterNot(result.editedMethodView::isPayload)
            .map(result.editedMethodView::opcodeAt)
            .toList()

        assertEquals(Value.Known(7L, LONG), returnedValue(graph))
        assertTrue(Opcode.INVOKE_STATIC in opcodes)
        assertTrue(Opcode.MOVE_FROM16 in opcodes)
        assertTrue(Opcode.MOVE_OBJECT_FROM16 in opcodes)
        assertTrue(Opcode.MOVE_WIDE in opcodes || Opcode.MOVE_WIDE_FROM16 in opcodes)
    }

    @Test
    fun staticRemapUsesVerifierCategoryAtAnAmbiguousBranchMerge() {
        val index = index(SOURCE)
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(MERGED_BRANCH)))
        edit.reserveScratchRegisters(1)

        val result = edit.commit() as MethodEditCommitResult.Committed
        val view = result.editedMethodView
        fun execute(selector: Int): Value {
            val frame = Frame.entryFrame(view)
            frame.assign(view.parameters[0].register, Value.Known("reference", OBJECT))
            frame.assign(view.parameters[1].register, Value.Known(selector, INT))
            return returnedValue(services.newSession(view, entryFrame = frame).run())
        }
        val branchPreludes = view.instructionAddresses.filter { view.opcodeAt(it) == Opcode.IF_EQZ }.map { branch ->
            view.opcodeAt(view.instructionAddresses.last { it < branch })
        }

        assertEquals(Value.Known(1, INT), execute(1))
        assertEquals(Value.Known(0, INT), execute(0))
        assertTrue(Opcode.MOVE_FROM16 in branchPreludes)
        assertTrue(Opcode.MOVE_OBJECT_FROM16 in branchPreludes)
    }

    @Test
    fun staticRemapPreservesNarrowReferenceAndWideParameters() {
        val index = index(CANDIDATE_SOURCE)
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(CANDIDATE)))
        edit.reserveScratchRegisters(2)
        assertTrue(edit.commit() is MethodEditCommitResult.Committed)
        assertEquals(7L, executeLong(services, requireNotNull(index.methodView(RUN_CANDIDATE))))
    }

    @Test
    fun staticRemapPreservesThisAndKeepsTheWideParameterPairTogether() {
        val index = index(CANDIDATE_SOURCE)
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(INSTANCE_CANDIDATE)))
        edit.reserveScratchRegisters(3)

        val result = edit.commit() as MethodEditCommitResult.Committed
        val view = result.editedMethodView
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters[0].register, Value.Known("receiver", view.definingClass))
        frame.assign(view.parameters[1].register, Value.Known(3, INT))
        frame.assign(view.parameters[2].register, Value.Known(11L, LONG))
        val graph = services.newSession(view, entryFrame = frame).run()

        assertEquals(Value.Known(11L, LONG), graph.trusted()!!.terminatingConsensus(view.parameters[2].register))
        assertEquals(view.registerCount - 2, view.parameters[2].register)
        assertTrue(view.parameters[2].register + 1 < view.registerCount)
    }

    @Test
    fun staticRemapMovesDebugRegistersAndKeepsTheirOriginalPosition() {
        val index = index(SOURCE)
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(DEBUG)))
        edit.reserveScratchRegisters(1)

        val result = edit.commit() as MethodEditCommitResult.Committed
        val body = requireNotNull(index.currentSnapshot.applicationMethod(DEBUG)?.body)
        val start = body.implementation.debugItems.filterIsInstance<StartLocal>().single()

        assertEquals(1, start.register)
        assertEquals(0, start.codeAddress)
        assertEquals(2, result.editedMethodView.registerCount)
    }

    @Test
    fun registerOverflowRejectsBeforePublication() {
        val index = index(OVERFLOW_SOURCE)
        val snapshot = index.currentSnapshot
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(OVERFLOW)))
        edit.reserveScratchRegisters(1)

        val result = edit.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.RegisterOverflow(65_535), result.reason)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun selectedStaticEncodingSurvivesDexWriteAndReload() {
        val index = index(SOURCE)
        val services = VmServices(index)
        val highEdit = services.methodEditor.beginEdit(requireNotNull(index.methodView(HIGH_OBJECT)))
        highEdit.reserveScratchRegisters(1)
        assertTrue(highEdit.commit() is MethodEditCommitResult.Committed)
        val debugEdit = services.methodEditor.beginEdit(requireNotNull(index.methodView(DEBUG)))
        debugEdit.reserveScratchRegisters(1)
        assertTrue(debugEdit.commit() is MethodEditCommitResult.Committed)

        val snapshot = index.currentSnapshot
        val reloaded = DexBackedDexFile(snapshot.outputApiPolicy.opcodes(), ProgramDexWriter().writeToBytes(snapshot))
        val methods = reloaded.classes.single().methods.associateBy { it.name }
        val high = requireNotNull(methods["highObject"]?.implementation)
        val debug = requireNotNull(methods["debug"]?.implementation)

        assertEquals(258, high.registerCount)
        assertTrue(high.instructions.any { it.opcode == Opcode.MOVE_OBJECT_FROM16 })
        assertEquals(2, debug.registerCount)
        assertEquals(1, debug.debugItems.filterIsInstance<StartLocal>().single().register)
    }

    private fun executeInt(services: VmServices, view: MethodView, parameter: Value): Int =
        (executeSingleParameter(services, view, parameter) as Value.Known).raw as Int

    private fun executeSingleParameter(services: VmServices, view: MethodView, parameter: Value): Value {
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters.single().register, parameter)
        return services.newSession(view, entryFrame = frame).run().trusted()!!.terminatingConsensus(0)
    }

    private fun executeLong(services: VmServices, view: MethodView): Long {
        val graph = services.newSession(view).run()
        return (graph.trusted()!!.terminatingConsensus(0) as Value.Known).raw as Long
    }

    private fun returnedValue(graph: ExecutionGraph): Value =
        requireNotNull((graph.outcomes.single() as Outcome.Returned).value)

    private fun index(source: String): ClassIndex {
        val directory = Files.createDirectory(root.resolve("fixture-${fixtureSequence++}"))
        return task53ClassIndex(directory, source)
    }

    private companion object {
        val INT = VirtualType.of("I")
        val OBJECT = VirtualType.OBJECT
        val LONG = VirtualType.of("J")
        const val VALUE = "Ltask58/Scratch;->value()I"
        const val HIGH_INT = "Ltask58/Scratch;->highInt(I)I"
        const val HIGH_OBJECT = "Ltask58/Scratch;->highObject(Ljava/lang/Object;)Ljava/lang/Object;"
        const val RANGE = "Ltask58/Scratch;->range(IIIIII)I"
        const val DEBUG = "Ltask58/Scratch;->debug(I)I"
        const val NEW_ARRAY = "Ltask58/Scratch;->newArray(I)I"
        const val WIDE_MATH = "Ltask58/Scratch;->wideMath(JJ)J"
        const val NON_RANGE = "Ltask58/Scratch;->nonRange()J"
        const val MERGED_BRANCH = "Ltask58/Scratch;->mergedBranch(Ljava/lang/Object;I)I"
        const val CANDIDATE = "Ltask58/Candidate;->candidate(ILjava/lang/Object;J)J"
        const val RUN_CANDIDATE = "Ltask58/Candidate;->runCandidate()J"
        const val INSTANCE_CANDIDATE = "Ltask58/Candidate;->instanceCandidate(IJ)J"
        const val OVERFLOW = "Ltask58/Overflow;->run()V"

        const val SOURCE = """
            .class public Ltask58/Scratch;
            .super Ljava/lang/Object;

            .method public static value()I
                .registers 1
                const/4 v0, 0x1
                return v0
            .end method

            .method public static highInt(I)I
                .registers 256
                .local p0, "value":I
                const/16 v254, 0x7
                .line 58
                if-eqz v255, :zero
                return v255
                :zero
                return v254
                .end local p0
            .end method

            .method public static highObject(Ljava/lang/Object;)Ljava/lang/Object;
                .registers 256
                if-eqz v255, :zero
                return-object v255
                :zero
                const/16 v254, 0x0
                return-object v254
            .end method

            .method private static first(IIIIII)I
                .registers 6
                return v0
            .end method

            .method public static range(IIIIII)I
                .registers 256
                invoke-static/range {v250 .. v255}, Ltask58/Scratch;->first(IIIIII)I
                move-result v0
                return v0
            .end method

            .method public static debug(I)I
                .registers 1
                .local p0, "value":I
                .line 58
                return p0
                .end local p0
            .end method

            .method public static newArray(I)I
                .registers 16
                new-array v14, v15, [I
                array-length v13, v14
                return v13
            .end method

            .method public static wideMath(JJ)J
                .registers 256
                add-long v250, v252, v254
                return-wide v250
            .end method


            .method private static passMixed(ILjava/lang/Object;J)J
                .registers 4
                return-wide p2
            .end method

            .method public static nonRange()J
                .registers 16
                const/4 v15, 0x3
                const/4 v13, 0x0
                const-wide/16 v10, 0x7
                invoke-static {v15, v13, v10, v11}, Ltask58/Scratch;->passMixed(ILjava/lang/Object;J)J
                move-result-wide v0
                return-wide v0
            .end method


            .method public static mergedBranch(Ljava/lang/Object;I)I
                .registers 256
                if-eqz v255, :zero_value
                move-object/from16 v255, v254
                goto :joined
                :zero_value
                const/16 v255, 0x0
                :joined
                if-eqz v255, :is_zero
                const/4 v0, 0x1
                return v0
                :is_zero
                const/4 v0, 0x0
                return v0
            .end method
        """

        const val CANDIDATE_SOURCE = """
            .class public Ltask58/Candidate;
            .super Ljava/lang/Object;

            .method public static candidate(ILjava/lang/Object;J)J
                .registers 4
                return-wide p2
            .end method

            .method public instanceCandidate(IJ)J
                .registers 4
                return-wide p2
            .end method

            .method public static runCandidate()J
                .registers 4
                const/4 v0, 0x3
                const/4 v1, 0x0
                const-wide/16 v2, 0x7
                invoke-static/range {v0 .. v3}, Ltask58/Candidate;->candidate(ILjava/lang/Object;J)J
                move-result-wide v0
                return-wide v0
            .end method
        """

        const val OVERFLOW_SOURCE = """
            .class public Ltask58/Overflow;
            .super Ljava/lang/Object;

            .method public static run()V
                .registers 65535
                return-void
            .end method
        """
    }
}
