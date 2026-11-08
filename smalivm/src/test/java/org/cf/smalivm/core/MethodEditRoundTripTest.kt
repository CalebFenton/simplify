package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.BuilderInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * An edit that keeps a method's meaning must keep its executed result, both on the committed view and after the
 * edited program is written to dex and reloaded. Expected results come from the fixture's smali.
 */
class MethodEditRoundTripTest {
    @TempDir
    lateinit var root: Path
    private var fixtureSequence = 0

    @Test
    fun insertingANopBeforeABranchKeepsBothArmsResults() {
        val index = fixtureIndex()
        val services = VmServices(index)
        val original = requireNotNull(index.methodView(OPAQUE))
        assertFalse(original.instructionAt(0) is BuilderInstruction)
        assertEquals(1, executeInt(services, original, listOf(Value.Known(0, INT))))
        assertEquals(2, executeInt(services, original, listOf(Value.Known(7, INT))))

        val edit = services.methodEditor.beginEdit(original)
        val branchAddress = original.instructionAddresses.single { original.opcodeAt(it) == Opcode.IF_EQ }
        edit.insertInstructionsBefore(branchAddress, listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))))
        val edited = (edit.commit() as MethodEditCommitResult.Committed).editedMethodView
        assertEquals(1, executeInt(services, edited, listOf(Value.Known(0, INT))))
        assertEquals(2, executeInt(services, edited, listOf(Value.Known(7, INT))))

        val reloadedIndex = reload(index)
        val reloaded = requireNotNull(reloadedIndex.methodView(OPAQUE))
        assertTrue(reloaded.instructionAddresses.any { reloaded.opcodeAt(it) == Opcode.NOP })
        assertEquals(1, executeInt(VmServices(reloadedIndex), reloaded, listOf(Value.Known(0, INT))))
        assertEquals(2, executeInt(VmServices(reloadedIndex), reloaded, listOf(Value.Known(7, INT))))
    }

    @Test
    fun replacingAReflectiveLookupInsideATryKeepsTheResultAndTheTryBlock() {
        val index = fixtureIndex()
        val services = VmServices(index)
        val original = requireNotNull(index.methodView(REFLECTION))
        assertEquals(1, executeInt(services, original))

        val edit = services.methodEditor.beginEdit(original)
        val invokeAddress = original.instructionAddresses.single { original.opcodeAt(it) == Opcode.INVOKE_STATIC }
        val moveResultAddress = original.instructionAddresses.single {
            original.opcodeAt(it) == Opcode.MOVE_RESULT_OBJECT
        }
        edit.replaceInstruction(
            invokeAddress,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21c(Opcode.CONST_CLASS, 0, ImmutableTypeReference("Ljava/lang/String;")),
                ),
            ),
        )
        edit.removeInstruction(moveResultAddress)
        val edited = (edit.commit() as MethodEditCommitResult.Committed).editedMethodView
        assertEquals(1, executeInt(services, edited))
        assertEquals(1, edited.tryBlocks.size)

        val reloadedIndex = reload(index)
        val reloaded = requireNotNull(reloadedIndex.methodView(REFLECTION))
        assertTrue(reloaded.instructionAddresses.none { reloaded.opcodeAt(it) == Opcode.INVOKE_STATIC })
        assertEquals(1, executeInt(VmServices(reloadedIndex), reloaded))
    }

    @Test
    fun scratchRegistersKeepParametersInTheHighestRegisters() {
        val index = fixtureIndex()
        val services = VmServices(index)
        val original = requireNotNull(index.methodView(PARAMETERS))
        assertEquals(9L, executeParameterFixture(services, original))

        val edit = services.methodEditor.beginEdit(original)
        val scratch = edit.reserveScratchRegisters(3)
        edit.insertInstructionsBefore(
            0,
            listOf(InstructionTemplate.Encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 7), listOf(scratch[0]))),
        )
        val edited = (edit.commit() as MethodEditCommitResult.Committed).editedMethodView
        assertEquals(9L, executeParameterFixture(services, edited))
        // The wide parameter is the third slot; scratch registers sit below the parameter region.
        assertEquals(edited.registerCount - 3, edited.parameters[2].register)

        val reloadedIndex = reload(index)
        val reloaded = requireNotNull(reloadedIndex.methodView(PARAMETERS))
        assertEquals(edited.registerCount, reloaded.registerCount)
        assertEquals(9L, executeParameterFixture(VmServices(reloadedIndex), reloaded))
    }

    private fun executeInt(
        services: VmServices,
        view: MethodView,
        parameters: List<Value> = emptyList(),
    ): Int {
        val frame = Frame.entryFrame(view)
        parameters.forEachIndexed { index, value -> frame.assign(view.parameters[index].register, value) }
        val value = services.newSession(view, entryFrame = frame).run().trusted()!!.terminatingConsensus(0) as Value.Known
        return value.raw as Int
    }

    private fun executeParameterFixture(services: VmServices, view: MethodView): Long {
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters[0].register, Value.Known("receiver", view.definingClass))
        frame.assign(view.parameters[1].register, Value.Known(3, INT))
        frame.assign(view.parameters[2].register, Value.Known(9L, VirtualType.of("J")))
        frame.assign(view.parameters[3].register, Value.Known("reference", OBJECT))
        val outcome = services.newSession(view, entryFrame = frame).run().outcomes.single() as Outcome.Returned
        return (requireNotNull(outcome.value) as Value.Known).raw as Long
    }

    private fun fixtureIndex(): ClassIndex {
        val source = requireNotNull(javaClass.getResourceAsStream("/smali/task62_adversarial.smali"))
            .bufferedReader()
            .use { it.readText() }
        val directory = Files.createDirectory(root.resolve("fixture-${fixtureSequence++}"))
        return task53ClassIndex(directory, source)
    }

    private fun reload(index: ClassIndex): ClassIndex {
        val snapshot = index.currentSnapshot
        val dexFile = root.resolve("round-trip-${fixtureSequence++}.dex")
        Files.write(dexFile, ProgramDexWriter().writeToBytes(snapshot))
        return ClassIndex.open(dexFile.toFile(), snapshot.outputApiPolicy.apiLevel)
    }

    private companion object {
        val INT = VirtualType.of("I")
        val OBJECT = VirtualType.OBJECT
        const val OPAQUE = "Ltask62/Adversarial;->opaque(I)I"
        const val REFLECTION = "Ltask62/Adversarial;->reflectionInTry()I"
        const val PARAMETERS = "Ltask62/Adversarial;->parameters(IJLjava/lang/Object;)J"
    }
}
