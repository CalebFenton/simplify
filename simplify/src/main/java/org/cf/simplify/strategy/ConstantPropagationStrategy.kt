package org.cf.simplify.strategy

import org.cf.simplify.ConstantBuilder
import org.cf.smalivm.core.MethodControlFlow
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference

/** Replaces only normally completed, effect-free assignments with proven literals. */
class ConstantPropagationStrategy : OptimizationStrategy {
    override val optimizationCategories: Set<String> = setOf(CONSTANTIZED_OPS)

    override fun perform(input: StrategyInput) {
        val method = input.method
        var previous: Instruction? = null
        for (address in method.instructionAddresses) {
            val instruction = method.instructionAt(address)
            val classForNameResult = instruction.opcode == Opcode.MOVE_RESULT_OBJECT &&
                previous?.let(::isClassForName) == true
            previous = instruction
            if (input.graph.isDead(address) || instruction.opcode !in ELIGIBLE_OPCODES ||
                instruction !is OneRegisterInstruction || classForNameResult ||
                !input.graph.didEveryExecutionContinueNormally(address) ||
                input.graph.sideEffectLevel(address) != SideEffectLevel.NONE) continue
            val destination = instruction.registerA
            val value = input.graph.consensus(address, destination) as? Value.Known ?: continue
            val raw = value.raw
            val replacement = if (raw is String && value.type.isReference) {
                // String consensus joins equal contents, not canonical identity. Prove the
                // register's definition instead of inspecting the representative raw object.
                if (instruction.opcode !in OBJECT_MOVES || instruction !is TwoRegisterInstruction) continue
                val flow = input.controlFlow ?: continue
                val index = flow.indexOf(address) ?: continue
                if (!hasLiteralOrigin(flow, index, instruction.registerB, raw)) continue
                ConstantBuilder.buildStringLiteral(raw, destination, input.transaction)
            } else ConstantBuilder.build(value, destination, input.transaction)
            if (replacement == null) continue
            input.transaction.replaceInstruction(address, listOf(replacement))
            input.recordOptimization(CONSTANTIZED_OPS)
        }
    }

    /**
     * Whether [sourceRegister], read at [useIndex], holds the `const-string` [value] on every path. The walk only
     * crosses instructions whose fall-through is the sole way into the next one, so no other definition can merge in.
     */
    private fun hasLiteralOrigin(flow: MethodControlFlow, useIndex: Int, sourceRegister: Int, value: String): Boolean {
        var index = useIndex
        var register = sourceRegister
        while (true) {
            index = flow.fallthroughPredecessor(index) ?: return false
            if (register !in flow.registersWritten(index)) continue
            val instruction = flow.instructionAt(index)
            if (instruction.opcode == Opcode.CONST_STRING || instruction.opcode == Opcode.CONST_STRING_JUMBO) {
                return ((instruction as ReferenceInstruction).reference as StringReference).string == value
            }
            if (instruction.opcode !in OBJECT_MOVES || instruction !is TwoRegisterInstruction) return false
            register = instruction.registerB
        }
    }

    companion object {
        const val CONSTANTIZED_OPS = "constantized ops"
        private val OBJECT_MOVES = setOf(Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16)
        private val ELIGIBLE_OPCODES: Set<Opcode> = java.util.EnumSet.of(
            Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16,
            Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16,
            Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16,
            Opcode.MOVE_EXCEPTION,
            Opcode.MOVE_RESULT, Opcode.MOVE_RESULT_WIDE, Opcode.MOVE_RESULT_OBJECT,
            Opcode.NEG_INT, Opcode.NOT_INT, Opcode.NEG_LONG, Opcode.NOT_LONG,
            Opcode.NEG_FLOAT, Opcode.NEG_DOUBLE,
            Opcode.INT_TO_LONG, Opcode.INT_TO_FLOAT, Opcode.INT_TO_DOUBLE,
            Opcode.LONG_TO_INT, Opcode.LONG_TO_FLOAT, Opcode.LONG_TO_DOUBLE,
            Opcode.FLOAT_TO_INT, Opcode.FLOAT_TO_LONG, Opcode.FLOAT_TO_DOUBLE,
            Opcode.DOUBLE_TO_INT, Opcode.DOUBLE_TO_LONG, Opcode.DOUBLE_TO_FLOAT,
            Opcode.INT_TO_BYTE, Opcode.INT_TO_CHAR, Opcode.INT_TO_SHORT,
            Opcode.ADD_INT, Opcode.ADD_INT_2ADDR, Opcode.ADD_INT_LIT8, Opcode.ADD_INT_LIT16,
            Opcode.SUB_INT, Opcode.SUB_INT_2ADDR, Opcode.RSUB_INT, Opcode.RSUB_INT_LIT8,
            Opcode.MUL_INT, Opcode.MUL_INT_2ADDR, Opcode.MUL_INT_LIT8, Opcode.MUL_INT_LIT16,
            Opcode.DIV_INT, Opcode.DIV_INT_2ADDR, Opcode.DIV_INT_LIT8, Opcode.DIV_INT_LIT16,
            Opcode.REM_INT, Opcode.REM_INT_2ADDR, Opcode.REM_INT_LIT8, Opcode.REM_INT_LIT16,
            Opcode.AND_INT, Opcode.AND_INT_2ADDR, Opcode.AND_INT_LIT8, Opcode.AND_INT_LIT16,
            Opcode.OR_INT, Opcode.OR_INT_2ADDR, Opcode.OR_INT_LIT8, Opcode.OR_INT_LIT16,
            Opcode.XOR_INT, Opcode.XOR_INT_2ADDR, Opcode.XOR_INT_LIT8, Opcode.XOR_INT_LIT16,
            Opcode.SHL_INT, Opcode.SHL_INT_2ADDR, Opcode.SHL_INT_LIT8,
            Opcode.SHR_INT, Opcode.SHR_INT_2ADDR, Opcode.SHR_INT_LIT8,
            Opcode.USHR_INT, Opcode.USHR_INT_2ADDR, Opcode.USHR_INT_LIT8,
            Opcode.ADD_LONG, Opcode.ADD_LONG_2ADDR, Opcode.SUB_LONG, Opcode.SUB_LONG_2ADDR,
            Opcode.MUL_LONG, Opcode.MUL_LONG_2ADDR, Opcode.DIV_LONG, Opcode.DIV_LONG_2ADDR,
            Opcode.REM_LONG, Opcode.REM_LONG_2ADDR, Opcode.AND_LONG, Opcode.AND_LONG_2ADDR,
            Opcode.OR_LONG, Opcode.OR_LONG_2ADDR, Opcode.XOR_LONG, Opcode.XOR_LONG_2ADDR,
            Opcode.SHL_LONG, Opcode.SHL_LONG_2ADDR, Opcode.SHR_LONG, Opcode.SHR_LONG_2ADDR,
            Opcode.USHR_LONG, Opcode.USHR_LONG_2ADDR,
            Opcode.ADD_FLOAT, Opcode.ADD_FLOAT_2ADDR, Opcode.SUB_FLOAT, Opcode.SUB_FLOAT_2ADDR,
            Opcode.MUL_FLOAT, Opcode.MUL_FLOAT_2ADDR, Opcode.DIV_FLOAT, Opcode.DIV_FLOAT_2ADDR,
            Opcode.REM_FLOAT, Opcode.REM_FLOAT_2ADDR,
            Opcode.ADD_DOUBLE, Opcode.ADD_DOUBLE_2ADDR, Opcode.SUB_DOUBLE, Opcode.SUB_DOUBLE_2ADDR,
            Opcode.MUL_DOUBLE, Opcode.MUL_DOUBLE_2ADDR, Opcode.DIV_DOUBLE, Opcode.DIV_DOUBLE_2ADDR,
            Opcode.REM_DOUBLE, Opcode.REM_DOUBLE_2ADDR,
            Opcode.AGET, Opcode.AGET_WIDE, Opcode.AGET_OBJECT,
            Opcode.AGET_BOOLEAN, Opcode.AGET_BYTE, Opcode.AGET_CHAR, Opcode.AGET_SHORT,
            Opcode.SGET, Opcode.SGET_WIDE, Opcode.SGET_OBJECT,
            Opcode.SGET_BOOLEAN, Opcode.SGET_BYTE, Opcode.SGET_CHAR, Opcode.SGET_SHORT
        )
    }
}
