package org.cf.smalivm.core

/**
 * Reads a register's raw host value at the width an instruction reads it, answering the bits a device reads.
 *
 * Dalvik's type tags are ambiguous: one `const` serves int/char/byte/short/float and one `const-wide` serves
 * long/double, so any register may hold any of the eight boxes the engine writes -- `Byte`, `Short`, `Int`,
 * `Char`, `Boolean` (Dalvik's 1/0) and `Float` for a 32-bit register, `Long` and `Double` for a pair. Each
 * reader reinterprets the box's bits at its own width. A read at 32 bits always has them: a pair's raw
 * supplies its low word, which is the register a 32-bit read names. A read at 64 bits needs both registers,
 * and a 32-bit raw supplies only one; the other is `register + 1`, which the engine's one value per pair does
 * not hold.
 *
 * Null means "not readable at this width": a 32-bit raw read at 64 bits, or a raw that is none of the eight
 * boxes (`BigDecimal("7.9").toInt()` is 7, a number no register holds). Callers turn null into
 * [Value.Unknown] of their result type, which keeps the path exploring and never invents a constant.
 */
internal object Coerce {
    /** The register's 32 bits as an `Int`. */
    fun toInt(raw: Any?): Int? = when (raw) {
        is Int -> raw
        is Byte -> raw.toInt()
        is Short -> raw.toInt()
        is Char -> raw.code
        is Boolean -> if (raw) 1 else 0
        is Float -> java.lang.Float.floatToRawIntBits(raw)
        is Long -> raw.toInt()
        is Double -> java.lang.Double.doubleToRawLongBits(raw).toInt()
        else -> null
    }

    /** [toInt] under the name the array ops use for a stored value, length or element. */
    fun toProvableInt(raw: Any?): Int? = toInt(raw)

    /** [toInt] under the name `AGetOp` and `APutOp` use for an index. */
    fun toArrayIndex(raw: Any?): Int? = toInt(raw)

    /** The register pair's 64 bits as a `Long`, or null for a 32-bit raw. */
    fun toLong(raw: Any?): Long? = when (raw) {
        is Long -> raw
        is Double -> java.lang.Double.doubleToRawLongBits(raw)
        else -> null
    }

    /**
     * The register's 32 bits as a `float`. `float-to-int` and its siblings read here and then convert by
     * value in `UnaryMathOp`: the read is by bits, the conversion is [JLS] §5.1.3's.
     */
    fun toFloat(raw: Any?): Float? = if (raw is Float) raw else toInt(raw)?.let(java.lang.Float::intBitsToFloat)

    /** The register pair's 64 bits as a `double`, or null for a 32-bit raw. */
    fun toDouble(raw: Any?): Double? = if (raw is Double) raw else toLong(raw)?.let(java.lang.Double::longBitsToDouble)

    /**
     * The value a store of [value] into a slot of [type] holds. A `Z`/`B`/`C`/`S` slot keeps the low 8 or 16
     * bits of the 32-bit register, as ART's field stores do (`GetFieldValue` casts the vreg with
     * `static_cast<uint8_t>`, `<int8_t>`, `<uint16_t>` and `<int16_t>`); any other slot stores [value]
     * unchanged. A narrow store answers `Unknown(type)` when the register has no 32-bit reading, and for a
     * boolean whose low byte is neither 0 nor 1, which a device keeps but a `Boolean` raw cannot hold.
     */
    fun narrowStore(value: Value, type: VirtualType): Value {
        val narrow = type.descriptor.singleOrNull()?.takeIf { it in "ZBCS" } ?: return value
        val bits = (value as? Value.Known)?.let { toInt(it.raw) } ?: return Value.Unknown(type)
        val raw: Any = when (narrow) {
            'Z' -> when (bits and 0xff) {
                0 -> false
                1 -> true
                else -> return Value.Unknown(type)
            }
            'B' -> bits.toByte()
            'C' -> bits.toChar()
            else -> bits.toShort()
        }
        return Value.Known(raw, type)
    }
}
