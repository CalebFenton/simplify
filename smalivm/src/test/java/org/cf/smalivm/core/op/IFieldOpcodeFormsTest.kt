package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/** Executable routing matrix. Set membership alone cannot prove an opcode reached the correct builder branch. */
class IFieldOpcodeFormsTest {

    @TestFactory
    fun everyIGetFormDecodesAsAReadAndReturnsItsDeclaredTypeDefault(): List<DynamicTest> = cases.map { case ->
        DynamicTest.dynamicTest(case.iget + " " + case.field) {
            val graph = CoreTester.execute(CLASS_NAME, case.getter)
            assertEquals(CompletionStatus.Complete, graph.completion())
            val op = graph.getOp(2)
            assertEquals(IGetOp::class, op::class)
            assertEquals("${case.iget} r${case.resultRegister}, r0, $CLASS_NAME->${case.field}", op.toString())
            assertEquals(case.defaultValue, graph.trusted()!!.terminatingConsensus(case.resultRegister))
            assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(2))
        }
    }

    @TestFactory
    fun everyIPutFormDecodesAsAWriteStoresTheValueAndReportsWeak(): List<DynamicTest> = cases.map { case ->
        DynamicTest.dynamicTest(case.iput + " " + case.field) {
            val graph = CoreTester.execute(CLASS_NAME, case.putter, CoreState().apply {
                setRegister(2, case.writtenValue)
            })
            assertEquals(CompletionStatus.Complete, graph.completion())
            val op = graph.getOp(2)
            assertEquals(IPutOp::class, op::class)
            assertEquals("${case.iput} r2, r0, $CLASS_NAME->${case.field}", op.toString())
            assertEquals(case.writtenValue, graph.trusted()!!.terminatingConsensus(case.resultRegister))
            assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(2))
            assertEquals(setOf(0, 2), graph.registersRead(2))
            assertEquals(emptySet<Int>(), graph.registersAssigned(2))
        }
    }

    private data class FormCase(
        val getter: String,
        val putter: String,
        val iget: String,
        val iput: String,
        val field: String,
        val defaultValue: Value,
        val writtenValue: Value,
        val resultRegister: Int = 1,
    )

    private companion object {
        private const val CLASS_NAME = "Lifield_forms_test;"

        private fun known(raw: Any?, descriptor: String) = Value.Known(raw, VirtualType.of(descriptor))

        private val cases = listOf(
            FormCase("getBooleanDefault()Z", "putBoolean(Z)Z", "iget-boolean", "iput-boolean", "booleanValue:Z", known(false, "Z"), known(true, "Z")),
            FormCase("getByteDefault()B", "putByte(B)B", "iget-byte", "iput-byte", "byteValue:B", known(0.toByte(), "B"), known(7.toByte(), "B")),
            FormCase("getShortDefault()S", "putShort(S)S", "iget-short", "iput-short", "shortValue:S", known(0.toShort(), "S"), known(300.toShort(), "S")),
            FormCase("getCharDefault()C", "putChar(C)C", "iget-char", "iput-char", "charValue:C", known('\u0000', "C"), known('A', "C")),
            FormCase("getIntDefault()I", "putInt(I)I", "iget", "iput", "intValue:I", known(0, "I"), known(42, "I")),
            FormCase("getLongDefault()J", "putLong(J)J", "iget-wide", "iput-wide", "longValue:J", known(0L, "J"), known(42L, "J"), 0),
            FormCase("getFloatDefault()F", "putFloat(F)F", "iget", "iput", "floatValue:F", known(0f, "F"), known(1.5f, "F")),
            FormCase("getDoubleDefault()D", "putDouble(D)D", "iget-wide", "iput-wide", "doubleValue:D", known(0.0, "D"), known(2.5, "D"), 0),
            FormCase("getObjectDefault()Ljava/lang/String;", "putObject(Ljava/lang/String;)Ljava/lang/String;", "iget-object", "iput-object", "objectValue:Ljava/lang/String;", known(null, "Ljava/lang/String;"), known("value", "Ljava/lang/String;")),
        )
    }
}
