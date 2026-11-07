package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.BuilderInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class Task62MethodEditingTest {
    @TempDir
    lateinit var root: Path
    private var fixtureSequence = 0

    @Test
    fun hostileApplicationFixturesPreserveBehaviorAcrossEditingAndReload() {
        val index = adversarialIndex()
        val services = VmServices(index)
        val opaqueA = requireNotNull(index.methodView(OPAQUE))
        assertFalse(opaqueA.instructionAt(0) is BuilderInstruction)
        assertEquals(1, executeInt(services, opaqueA, listOf(Value.Known(0, INT))))
        assertEquals(2, executeInt(services, opaqueA, listOf(Value.Known(7, INT))))

        val opaqueEdit = services.methodEditor.beginEdit(opaqueA)
        val branchAddress = opaqueA.instructionAddresses.single { opaqueA.opcodeAt(it) == Opcode.IF_EQ }
        opaqueEdit.insertInstructionsBefore(
            branchAddress,
            listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
        )
        val opaqueB = (opaqueEdit.commit() as MethodEditCommitResult.Committed).editedMethodView
        assertEquals(1, executeInt(services, opaqueB, listOf(Value.Known(0, INT))))
        assertEquals(2, executeInt(services, opaqueB, listOf(Value.Known(7, INT))))

        val reflectionA = requireNotNull(index.methodView(REFLECTION))
        assertEquals(1, executeInt(services, reflectionA))
        val reflectionEdit = services.methodEditor.beginEdit(reflectionA)
        val invokeAddress = reflectionA.instructionAddresses.single { reflectionA.opcodeAt(it) == Opcode.INVOKE_STATIC }
        val moveResultAddress = reflectionA.instructionAddresses.single {
            reflectionA.opcodeAt(it) == Opcode.MOVE_RESULT_OBJECT
        }
        reflectionEdit.replaceInstruction(
            invokeAddress,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21c(
                        Opcode.CONST_CLASS,
                        0,
                        ImmutableTypeReference("Ljava/lang/String;"),
                    ),
                ),
            ),
        )
        reflectionEdit.removeInstruction(moveResultAddress)
        val reflectionB = (reflectionEdit.commit() as MethodEditCommitResult.Committed).editedMethodView
        assertEquals(1, executeInt(services, reflectionB))
        assertEquals(1, reflectionB.tryBlocks.size)

        val parametersA = requireNotNull(index.methodView(PARAMETERS))
        assertEquals(9L, executeParameterFixture(services, parametersA))
        val parameterEdit = services.methodEditor.beginEdit(parametersA)
        val scratch = parameterEdit.reserveScratchRegisters(3)
        parameterEdit.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction11n(Opcode.CONST_4, 0, 7),
                    listOf(scratch[0]),
                ),
            ),
        )
        val parametersB = (parameterEdit.commit() as MethodEditCommitResult.Committed).editedMethodView
        assertEquals(9L, executeParameterFixture(services, parametersB))
        assertEquals(parametersB.registerCount - 3, parametersB.parameters[2].register)

        val reloaded = reload(index)
        assertEquals(1, executeInt(VmServices(reloaded), requireNotNull(reloaded.methodView(REFLECTION))))
        assertEquals(9L, executeParameterFixture(VmServices(reloaded), requireNotNull(reloaded.methodView(PARAMETERS))))
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

    private fun adversarialIndex(): ClassIndex {
        val source = requireNotNull(javaClass.getResourceAsStream("/smali/task62_adversarial.smali"))
            .bufferedReader()
            .use { it.readText() }
        return index(source)
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

    private companion object {
        val INT = VirtualType.of("I")
        val OBJECT = VirtualType.OBJECT
        const val OPAQUE = "Ltask62/Adversarial;->opaque(I)I"
        const val REFLECTION = "Ltask62/Adversarial;->reflectionInTry()I"
        const val PARAMETERS = "Ltask62/Adversarial;->parameters(IJLjava/lang/Object;)J"
    }
}
