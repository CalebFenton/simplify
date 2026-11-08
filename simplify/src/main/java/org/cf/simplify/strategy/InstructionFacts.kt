package org.cf.simplify.strategy

import org.cf.smalivm.core.MethodView
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference

private val CLASS_FOR_NAME = ImmutableMethodReference(
    "Ljava/lang/Class;", "forName", listOf("Ljava/lang/String;"), "Ljava/lang/Class;",
)
internal val MOVE_RESULTS: Set<Opcode> = setOf(Opcode.MOVE_RESULT, Opcode.MOVE_RESULT_WIDE, Opcode.MOVE_RESULT_OBJECT)

/** The method an invoke names, or null for any other instruction. */
internal fun invokedMethod(instruction: Instruction): MethodReference? =
    (instruction as? ReferenceInstruction)?.reference as? MethodReference

/** `Class.forName(String)` initializes the class it names; neither its call nor its result may be elided. */
internal fun isClassForName(instruction: Instruction): Boolean = invokedMethod(instruction) == CLASS_FOR_NAME

/** Argument registers of an invoke, one per argument word, in order. Empty for any other instruction. */
internal fun invokeRegisters(instruction: Instruction): IntArray = when (instruction) {
    is RegisterRangeInstruction -> IntArray(instruction.registerCount) { instruction.startRegister + it }
    is FiveRegisterInstruction ->
        intArrayOf(instruction.registerC, instruction.registerD, instruction.registerE, instruction.registerF,
            instruction.registerG).copyOf(minOf(instruction.registerCount, 5))
    else -> IntArray(0)
}

/** The `move-result*` that receives the result of the invoke at [address], if one follows it. */
internal fun moveResultAfter(method: MethodView, address: Int): MoveResult? {
    val next = method.nextAddress(address)
    if (!method.hasAddress(next) || method.isPayload(next)) return null
    val instruction = method.instructionAt(next)
    if (instruction.opcode !in MOVE_RESULTS) return null
    return MoveResult(next, instruction.opcode, (instruction as OneRegisterInstruction).registerA)
}

internal data class MoveResult(val address: Int, val opcode: Opcode, val destination: Int)
