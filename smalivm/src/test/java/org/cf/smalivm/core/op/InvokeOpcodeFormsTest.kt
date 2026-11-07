package org.cf.smalivm.core.op

import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.InvokeKind
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.OpBuilder
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.Transition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

class InvokeOpcodeFormsTest {
    @Test
    fun everyStandardInvokeKindAndRangeFormDecodesToItsExactKind() {
        val rows = listOf(
            Row(TASK, "staticReturn()I", "invoke-static", InvokeKind.STATIC, target(TASK, "seven", "()I"), intArrayOf()),
            Row(TASK, "wideRange()J", "invoke-static/range", InvokeKind.STATIC, target(TASK, "lastWide", "(JJJI)J"), IntArray(7) { it }),
            Row(TASK, "initOneOnly()Linvoke_ctor_target;", "invoke-direct", InvokeKind.DIRECT, target("Linvoke_ctor_target;", "<init>", "()V"), intArrayOf(0)),
            Row(TASK, "directRange()V", "invoke-direct/range", InvokeKind.DIRECT, target("Linvoke_ctor_target;", "<init>", "()V"), intArrayOf(0)),
            Row("Lchild_class;", "callsSuperParentMethod()Ljava/lang/String;", "invoke-super", InvokeKind.SUPER, target("Lparent_class;", "parentMethod", "()Ljava/lang/String;"), intArrayOf(1)),
            Row("Lchild_class;", "callsSuperParentMethodRange()Ljava/lang/String;", "invoke-super/range", InvokeKind.SUPER, target("Lparent_class;", "parentMethod", "()Ljava/lang/String;"), intArrayOf(1)),
            Row(TASK, "knownVirtual()I", "invoke-virtual", InvokeKind.VIRTUAL, target("Linvoke_dispatch_base;", "value", "()I"), intArrayOf(0)),
            Row(TASK, "virtualRange()I", "invoke-virtual/range", InvokeKind.VIRTUAL, target("Linvoke_dispatch_base;", "value", "()I"), intArrayOf(0)),
            Row(TASK, "interfaceUnknown(Linvoke_single_interface;)I", "invoke-interface", InvokeKind.INTERFACE, target("Linvoke_single_interface;", "value", "()I"), intArrayOf(0)),
            Row(TASK, "interfaceRange(Linvoke_single_interface;)I", "invoke-interface/range", InvokeKind.INTERFACE, target("Linvoke_single_interface;", "value", "()I"), intArrayOf(0)),
        )
        for (row in rows) {
            val view = CoreTester.classIndex.requireMethodView(row.owner, row.method)
            val address = view.addresses.single { view.instructionAt(it).opcode.name == row.opcode }
            val op = OpBuilder.build(address, view, CoreTester.classIndex)
            val transition = op.execute(Frame.entryFrame(view)).arms.single() as OpArm.Invoke
            assertEquals(row.kind, transition.kind, "${row.owner}->${row.method} ${row.opcode}")
            assertEquals(row.target, transition.target, row.opcode)
            assertArrayEquals(row.registers, transition.argumentRegisters, row.opcode)
            assertEquals(row.registers.toSet(), op.registersRead, row.opcode)
        }
    }

    private data class Row(
        val owner: String,
        val method: String,
        val opcode: String,
        val kind: InvokeKind,
        val target: org.cf.smalivm.core.MethodDescriptor,
        val registers: IntArray,
    )

    private fun target(owner: String, name: String, proto: String) =
        org.cf.smalivm.core.MethodDescriptor(org.cf.smalivm.core.VirtualType.of(owner), name, proto)

    private companion object {
        const val TASK = "Linvoke_task20;"
    }
}
