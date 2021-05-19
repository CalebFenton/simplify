package org.cf.smalivm

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.type.VirtualType
import org.cf.util.ClassNameUtils
import org.cf.util.Utils

data class Value(val raw: Any?, val type: String) {
    companion object {
        fun wrap(value: Any): Value {
            return wrap(value, ClassNameUtils.toInternal(value.javaClass))
        }

        fun wrap(value: Any, klazz: Class<*>): Value {
            return wrap(value, ClassNameUtils.toInternal(klazz))
        }

        fun wrap(value: Any?, type: String): Value {
            return Value(value, type)
        }

        fun uninitializedInstance(type: VirtualType): Value {
            val instance = UninitializedInstance(type)
            return wrap(instance, type.toString())
        }

        fun unknown(type: String): Value {
            return wrap(UnknownValue(), type)
        }

        private fun wrap(other: Value): Value {
            return Value(other.raw, other.type)
        }
    }

    val isImmutable: Boolean
        get() = Configuration.instance().isImmutable(type)

    val isMutable: Boolean
        get() = !isImmutable

    val isNull: Boolean
        get() = raw == null

    val isNotNull: Boolean
        get() = !isNull

    val isObject: Boolean
        get() = ClassNameUtils.isObject(type)

    val isPrimitive: Boolean
        get() = ClassNameUtils.isPrimitive(type)

    val isPrimitiveOrWrapper: Boolean
        get() = ClassNameUtils.isPrimitiveOrWrapper(type)

    val isPrimitiveWrapper: Boolean
        get() = ClassNameUtils.isWrapper(type)

    val isArray: Boolean
        get() = type.startsWith("[")
    
    val isUnknown: Boolean
        get() = raw is UnknownValue

    val isKnown: Boolean
        get() = !isUnknown

    val componentBase: String
        get() = ClassNameUtils.getComponentBase(type)

    val unboxedType: String
        get() = ClassNameUtils.getPrimitive(type) ?: type

    val unboxedValueType: String
        get() = ClassNameUtils.getPrimitive(rawType) ?: rawType

    val rawType: String
        get() = if (isNull) type else ClassNameUtils.toInternal(raw!!.javaClass)

    val declaredAndValueTypeNames: Set<String>
        get() = if (!isNull && !isUnknown) {
            setOf(type, ClassNameUtils.toInternal(raw!!.javaClass))
        } else {
            setOf(type)
        }

    val registerSize: Int
        get() = if (CommonTypes.LONG == type || CommonTypes.DOUBLE == type) 2 else 1

    fun toDouble(): Double {
        return Utils.getDoubleValue(raw)
    }

    fun toFloat(): Float {
        return Utils.getFloatValue(raw)
    }

    fun toInteger(): Int {
        return Utils.getIntegerValue(raw)
    }

    fun toLong(): Long {
        return Utils.getLongValue(raw)
    }

    override fun toString(): String {
        val sb = StringBuilder("type=")
        sb.append(type).append(", value=")
        if (raw == null) {
            sb.append("null")
        } else {
            if (raw.javaClass.isArray) {
                val objArray: Array<Any?>
                if (raw.javaClass.componentType.isPrimitive) {
                    val arrayLen = java.lang.reflect.Array.getLength(raw)
                    objArray = arrayOfNulls(arrayLen)
                    for (i in 0 until arrayLen) {
                        objArray[i] = java.lang.reflect.Array.get(raw, i)
                    }
                } else {
                    objArray = raw as Array<Any?>
                }
                val arrayString = objArray.contentDeepToString()
                sb.append(arrayString)
            } else {
                sb.append(raw)
            }
        }
        return sb.toString()
    }
}
