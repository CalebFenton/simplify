package org.cf.smalivm.core.op

import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.InvokeKind
import org.cf.smalivm.core.OpBuilder
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.currentResolver
import org.cf.smalivm.core.requireMethodView
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class InvokeOpcodeFormsTest {
    @TestFactory
    fun everyStandardInvokeKindAndRangeFormDecodesToItsExactKind(): List<DynamicTest> {
        val rows = listOf(
            Row(FIXTURE, "staticReturn()I", "invoke-static", InvokeKind.STATIC, target(FIXTURE, "seven", "()I"), intArrayOf()),
            Row(FIXTURE, "wideRange()J", "invoke-static/range", InvokeKind.STATIC, target(FIXTURE, "lastWide", "(JJJI)J"), IntArray(7) { it }),
            Row(FIXTURE, "initOneOnly()Linvoke_ctor_target;", "invoke-direct", InvokeKind.DIRECT, target("Linvoke_ctor_target;", "<init>", "()V"), intArrayOf(0)),
            Row(FIXTURE, "directRange()V", "invoke-direct/range", InvokeKind.DIRECT, target("Linvoke_ctor_target;", "<init>", "()V"), intArrayOf(0)),
            Row("Lchild_class;", "callsSuperParentMethod()Ljava/lang/String;", "invoke-super", InvokeKind.SUPER, target("Lparent_class;", "parentMethod", "()Ljava/lang/String;"), intArrayOf(1)),
            Row("Lchild_class;", "callsSuperParentMethodRange()Ljava/lang/String;", "invoke-super/range", InvokeKind.SUPER, target("Lparent_class;", "parentMethod", "()Ljava/lang/String;"), intArrayOf(1)),
            Row(FIXTURE, "knownVirtual()I", "invoke-virtual", InvokeKind.VIRTUAL, target("Linvoke_dispatch_base;", "value", "()I"), intArrayOf(0)),
            Row(FIXTURE, "virtualRange()I", "invoke-virtual/range", InvokeKind.VIRTUAL, target("Linvoke_dispatch_base;", "value", "()I"), intArrayOf(0)),
            Row(FIXTURE, "interfaceUnknown(Linvoke_single_interface;)I", "invoke-interface", InvokeKind.INTERFACE, target("Linvoke_single_interface;", "value", "()I"), intArrayOf(0)),
            Row(FIXTURE, "interfaceRange(Linvoke_single_interface;)I", "invoke-interface/range", InvokeKind.INTERFACE, target("Linvoke_single_interface;", "value", "()I"), intArrayOf(0)),
        )
        return rows.map { row ->
            DynamicTest.dynamicTest("${row.opcode} in ${row.owner}->${row.method}") {
                val view = CoreTester.classIndex.requireMethodView(row.owner, row.method)
                val address = view.addresses.single { view.instructionAt(it).opcode.name == row.opcode }
                val op = OpBuilder.build(address, view, CoreTester.classIndex.currentResolver())
                val arm = op.execute(Frame.entryFrame(view)).arms.single() as OpArm.Invoke
                assertEquals(row.kind, arm.kind)
                assertEquals(row.target, arm.target)
                assertArrayEquals(row.registers, arm.argumentRegisters)
                assertEquals(row.registers.toSet(), op.registersRead)
            }
        }
    }

    private data class Row(
        val owner: String,
        val method: String,
        val opcode: String,
        val kind: InvokeKind,
        val target: org.cf.smalivm.core.MethodSignature,
        val registers: IntArray,
    )

    private fun target(owner: String, name: String, proto: String) =
        org.cf.smalivm.core.MethodSignature(org.cf.smalivm.core.VirtualType.of(owner), name, proto)

    private companion object {
        const val FIXTURE = "Linvoke_resolution;"
    }
}
