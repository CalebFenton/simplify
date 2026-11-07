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
 * One `neg-*`, `not-*` or `*-to-*` instruction. The source is read by its bits at the width the instruction
 * names and converted by value, so `int-to-float` of a register holding a computed `5.9f` converts the int
 * 1086115021. [convert] answers null when the source has no reading at that width.
 */
internal enum class UnaryOperation(resultDescriptor: String, private val convert: (Any?) -> Any?) {
    NEG_INT("I", { raw -> Coerce.toInt(raw)?.let { -it } }),
    NOT_INT("I", { raw -> Coerce.toInt(raw)?.inv() }),
    NEG_LONG("J", { raw -> Coerce.toLong(raw)?.let { -it } }),
    NOT_LONG("J", { raw -> Coerce.toLong(raw)?.inv() }),
    NEG_FLOAT("F", { raw -> Coerce.toFloat(raw)?.let { -it } }),
    NEG_DOUBLE("D", { raw -> Coerce.toDouble(raw)?.let { -it } }),
    INT_TO_LONG("J", { raw -> Coerce.toInt(raw)?.toLong() }),
    INT_TO_FLOAT("F", { raw -> Coerce.toInt(raw)?.toFloat() }),
    INT_TO_DOUBLE("D", { raw -> Coerce.toInt(raw)?.toDouble() }),
    LONG_TO_INT("I", { raw -> Coerce.toLong(raw)?.toInt() }),
    LONG_TO_FLOAT("F", { raw -> Coerce.toLong(raw)?.toFloat() }),
    LONG_TO_DOUBLE("D", { raw -> Coerce.toLong(raw)?.toDouble() }),
    FLOAT_TO_INT("I", { raw -> Coerce.toFloat(raw)?.toInt() }),
    FLOAT_TO_LONG("J", { raw -> Coerce.toFloat(raw)?.toLong() }),
    FLOAT_TO_DOUBLE("D", { raw -> Coerce.toFloat(raw)?.toDouble() }),
    DOUBLE_TO_INT("I", { raw -> Coerce.toDouble(raw)?.toInt() }),
    DOUBLE_TO_LONG("J", { raw -> Coerce.toDouble(raw)?.toLong() }),
    DOUBLE_TO_FLOAT("F", { raw -> Coerce.toDouble(raw)?.toFloat() }),
    INT_TO_BYTE("B", { raw -> Coerce.toInt(raw)?.toByte() }),
    INT_TO_CHAR("C", { raw -> Coerce.toInt(raw)?.toChar() }),
    INT_TO_SHORT("S", { raw -> Coerce.toInt(raw)?.toShort() }),
    ;

    val resultType: VirtualType = VirtualType.of(resultDescriptor)

    fun applyTo(source: Value): Value =
        (source as? Value.Known)?.let { convert(it.raw) }?.let { Value.Known(it, resultType) } ?: Value.Unknown(resultType)
}

/** `neg-*`, `not-*` and the `*-to-*` conversions: one source register, one destination, no way to throw. */
internal class UnaryMathOp(
    address: Int,
    override val name: String,
    val destRegister: Int,
    val srcRegister: Int,
    private val operation: UnaryOperation,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(srcRegister)

    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, operation.resultType.isWide)

    override fun execute(frame: Frame): OpExecution =
        OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, operation.applyTo(frame.read(srcRegister)))))

    override fun toString(): String = "$name r$destRegister, r$srcRegister"
}
