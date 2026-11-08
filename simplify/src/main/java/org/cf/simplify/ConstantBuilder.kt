package org.cf.simplify

import org.cf.smalivm.core.DexReferenceOperand
import org.cf.smalivm.core.InstructionTemplate
import org.cf.smalivm.core.MethodEditTransaction
import org.cf.smalivm.core.Value
import org.cf.util.ClassNameUtils
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.immutable.instruction.*
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference

/** Detached Dalvik literals. Unsupported carriers are declined rather than numerically coerced. */
object ConstantBuilder {
    /**
     * Emits a string only after the caller proves canonical literal identity. Known string contents
     * alone are insufficient: host methods can return fresh, non-interned immutable strings.
     */
    fun buildStringLiteral(value: String, register: Int, transaction: MethodEditTransaction): InstructionTemplate? {
        if (register !in 0..255) return null
        return InstructionTemplate.Encoded(
            ImmutableInstruction21c(Opcode.CONST_STRING, register, ImmutableStringReference(value)),
            DexReferenceOperand.Staged(transaction.stageString(value)),
        )
    }

    fun build(value: Value.Known, register: Int, transaction: MethodEditTransaction): InstructionTemplate? {
        if (register !in 0..255) return null
        val raw = value.raw
        val type = value.type
        if (raw == null) return if (type.isReference) narrow(0, register) else null
        if (type.isReference) {
            return when (raw) {
                is Class<*> -> {
                    val descriptor = ClassNameUtils.toInternal(raw)
                    InstructionTemplate.Encoded(
                        ImmutableInstruction21c(Opcode.CONST_CLASS, register, ImmutableTypeReference(descriptor)),
                        DexReferenceOperand.Staged(transaction.stageType(descriptor)),
                    )
                }
                else -> null
            }
        }
        return when (type.descriptor) {
            "Z", "B", "S", "C", "I" -> integerBits(raw)?.let { narrow(it, register) }
            "J" -> when (raw) {
                is Float, is Double -> null
                is Number -> wide(raw.toLong(), register)
                else -> integerBits(raw)?.let { wide(it.toLong(), register) }
            }
            "F" -> {
                val bits = when (raw) {
                    is Float -> raw.toRawBits()
                    is Double -> raw.toRawBits().toInt()
                    else -> integerBits(raw) ?: return null
                }
                if (bits and 0xffff == 0) encoded(ImmutableInstruction21ih(Opcode.CONST_HIGH16, register, bits))
                else encoded(ImmutableInstruction31i(Opcode.CONST, register, bits))
            }
            "D" -> {
                val bits = when (raw) {
                    is Double -> raw.toRawBits()
                    is Long -> raw
                    else -> return null // A narrow carrier does not establish the high word.
                }
                if (bits and 0x0000ffffffffffffL == 0L)
                    encoded(ImmutableInstruction21lh(Opcode.CONST_WIDE_HIGH16, register, bits))
                else encoded(ImmutableInstruction51l(Opcode.CONST_WIDE, register, bits))
            }
            else -> null
        }
    }

    // A floating carrier is not a proven integer read.
    private fun integerBits(raw: Any): Int? = when (raw) {
        is Float, is Double -> null
        is Number -> raw.toInt()
        is Boolean -> if (raw) 1 else 0
        is Char -> raw.code
        else -> null
    }

    private fun narrow(bits: Int, register: Int): InstructionTemplate = encoded(when {
        register <= 15 && bits in -8..7 -> ImmutableInstruction11n(Opcode.CONST_4, register, bits)
        bits in Short.MIN_VALUE..Short.MAX_VALUE -> ImmutableInstruction21s(Opcode.CONST_16, register, bits)
        else -> ImmutableInstruction31i(Opcode.CONST, register, bits)
    })

    private fun wide(bits: Long, register: Int): InstructionTemplate = encoded(when {
        bits in Short.MIN_VALUE.toLong()..Short.MAX_VALUE.toLong() ->
            ImmutableInstruction21s(Opcode.CONST_WIDE_16, register, bits.toInt())
        bits in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() ->
            ImmutableInstruction31i(Opcode.CONST_WIDE_32, register, bits.toInt())
        else -> ImmutableInstruction51l(Opcode.CONST_WIDE, register, bits)
    })

    private fun encoded(instruction: Instruction): InstructionTemplate = InstructionTemplate.Encoded(instruction)
}
