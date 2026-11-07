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
     * Null when the answer is not provable, which is three cases collapsed onto one `return`: an unknown
     * source, a raw that is not a number, and -- since Plan 8 Task 3 -- a raw whose bits this engine cannot
     * read at the width the mnemonic names.
     *
     * That third case is the `Float`/`Double` raw an `int-*` or `long-*` read lands on. `Coerce.toInt`/
     * `Coerce.toLong` answer null for it rather than performing a [JLS] §5.1.3 narrowing conversion, because
     * a device reads the register's BITS: `int-to-float` on a register holding a computed `5.9f` had answered
     * a confident `Known(5.0f)` where a device converts 1086115021. The rule and its whole argument live in
     * `Coerce.toInt`'s KDoc; the six reads below inherit it with no local copy, deliberately.
     *
     * Every op in this file has exactly one successor, so the refusal explores neither more nor fewer paths
     * -- it declines a fold the engine was not entitled to make. Pinned by
     * `core/op/UnaryMathOpTest.anIntToFloatReadingAComputedFloatRawIsNotProvableRatherThanTruncatedByValue`
     * and, for the separate `Coerce.toLong` route, by
     * `core/op/UnaryMathOpTest.aLongToIntReadingAComputedDoubleRawIsNotProvableRatherThanTruncatedByValue`.
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
