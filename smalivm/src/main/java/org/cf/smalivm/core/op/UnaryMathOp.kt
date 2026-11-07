package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * `neg-*`, `not-*`, and the eighteen `*-to-*` conversions: one source register, one destination, no
 * way to throw.
 *
 * [resultType] is resolved at decode time from the mnemonic's last segment (`int-to-byte` -> `B`),
 * because that is the one thing the op's *name* states unambiguously while its operand's declared
 * type may be a lie (Dalvik reuses `const` across int/char/byte/short/float). The source is coerced
 * into the domain the mnemonic's FIRST segment names, then converted -- `int-to-byte` coerces to Int
 * and truncates, never reading the raw as whatever host box it happens to be.
 */
internal class UnaryMathOp(
    address: Int,
    override val name: String,
    val destRegister: Int,
    val srcRegister: Int,
    private val resultType: VirtualType,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(srcRegister)

    // Both halves for a wide result: the high half is not free scratch (see
    // ExecutionGraph.availableRegisters).
    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, resultType.isWide)

    override fun execute(frame: Frame): OpExecution {
        val source = frame.read(srcRegister)
        val result = computed(source) ?: Value.Unknown(resultType)
        return OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, result)))
    }

    /**
     * Null when the source is unknown or not readable at the width the mnemonic's first segment names (see
     * [Coerce]). The read is by bits and the conversion by value: `int-to-float` of a register holding a
     * computed `5.9f` converts the int 1086115021, its bit pattern.
     */
    private fun computed(source: Value): Value? {
        if (source !is Value.Known) return null
        val raw = source.raw
        val computed: Any = when (val domain = name.substringBefore('-')) {
            "neg" -> when (resultType.descriptor) {
                "I" -> -(Coerce.toInt(raw) ?: return null)
                "J" -> -(Coerce.toLong(raw) ?: return null)
                "F" -> -(Coerce.toFloat(raw) ?: return null)
                "D" -> -(Coerce.toDouble(raw) ?: return null)
                else -> error("neg with non-numeric result type ${resultType.descriptor} in $name")
            }
            "not" -> when (resultType.descriptor) {
                "I" -> (Coerce.toInt(raw) ?: return null).inv()
                "J" -> (Coerce.toLong(raw) ?: return null).inv()
                else -> error("not with non-integral result type ${resultType.descriptor} in $name")
            }
            "int" -> convertFromInt(Coerce.toInt(raw) ?: return null)
            "long" -> convertFromLong(Coerce.toLong(raw) ?: return null)
            "float" -> convertFromFloat(Coerce.toFloat(raw) ?: return null)
            "double" -> convertFromDouble(Coerce.toDouble(raw) ?: return null)
            else -> error("Unexpected unary math mnemonic domain '$domain' in $name")
        }
        return Value.Known(computed, resultType)
    }

    private fun convertFromInt(value: Int): Any = when (resultType.descriptor) {
        "B" -> value.toByte()
        "C" -> value.toChar()
        "S" -> value.toShort()
        "J" -> value.toLong()
        "F" -> value.toFloat()
        "D" -> value.toDouble()
        else -> error("Unexpected int-to-${resultType.descriptor} in $name")
    }

    private fun convertFromLong(value: Long): Any = when (resultType.descriptor) {
        "I" -> value.toInt()
        "F" -> value.toFloat()
        "D" -> value.toDouble()
        else -> error("Unexpected long-to-${resultType.descriptor} in $name")
    }

    private fun convertFromFloat(value: Float): Any = when (resultType.descriptor) {
        "I" -> value.toInt()
        "J" -> value.toLong()
        "D" -> value.toDouble()
        else -> error("Unexpected float-to-${resultType.descriptor} in $name")
    }

    private fun convertFromDouble(value: Double): Any = when (resultType.descriptor) {
        "I" -> value.toInt()
        "J" -> value.toLong()
        "F" -> value.toFloat()
        else -> error("Unexpected double-to-${resultType.descriptor} in $name")
    }

    override fun toString(): String = "$name r$destRegister, r$srcRegister"
}
