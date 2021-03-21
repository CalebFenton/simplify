package org.cf.smalivm2

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.type.VirtualType
import org.cf.util.ClassNameUtils
import org.cf.util.Utils
import java.util.*

// TODO: strip out id, I don't think it's a good idea
data class Value(val value: Any?, val type: String, val id: ByteArray) {
    companion object {
        fun wrap(value: Any): Value {
            return wrap(value, ClassNameUtils.toInternal(value.javaClass))
        }

        fun wrap(value: Any?, type: String): Value {
            val id = ByteArray(4)
            Random().nextBytes(id)
            return Value(value, type, id)
        }

        fun uninitializedInstance(type: VirtualType): Value {
            val instance = UninitializedInstance(type)
            return wrap(instance, type.toString())
        }

        fun unknown(type: String): Value {
            return wrap(UnknownValue(), type)
        }

        private fun wrap(other: Value): Value {
            return Value(other.value, other.type, other.id)
        }
    }

//    fun hasSameID(other: Value): Boolean {
//        return id.contentEquals(other.id)
//    }

    val isImmutable: Boolean
        get() = Configuration.instance().isImmutable(type)

    val isMutable: Boolean
        get() = !isImmutable

    val isNull: Boolean
        get() = value == null

    val isObject: Boolean
        get() = ClassNameUtils.isObject(type)

    val isPrimitive: Boolean
        get() = ClassNameUtils.isPrimitive(type)

    val isPrimitiveOrWrapper: Boolean
        get() = ClassNameUtils.isPrimitiveOrWrapper(type)

    val isPrimitiveWrapper: Boolean
        get() = ClassNameUtils.isWrapper(type)

    val isUnknown: Boolean
        get() = value is UnknownValue

    val isKnown: Boolean
        get() = !isUnknown

    val componentBase: String?
        get() = ClassNameUtils.getComponentBase(type)

    val unboxedType: String
        get() = ClassNameUtils.getPrimitive(type) ?: type

    val unboxedValueType: String
        get() = ClassNameUtils.getPrimitive(valueType) ?: valueType

    val valueType: String
        get() = if (isNull) type else ClassNameUtils.toInternal(value!!.javaClass)

    val declaredAndValueTypeNames: Set<String>
        get() = if (!isNull && !isUnknown) {
            setOf(type, ClassNameUtils.toInternal(value!!.javaClass))
        } else {
            setOf(type)
        }

    fun toDouble(): Double {
        return Utils.getDoubleValue(value)
    }

    fun toFloat(): Float {
        return Utils.getFloatValue(value)
    }

    fun toInteger(): Int {
        return Utils.getIntegerValue(value)
    }

    fun toLong(): Long {
        return Utils.getLongValue(value)
    }

    override fun toString(): String {
        val sb = StringBuilder("type=")
        sb.append(type).append(", value=")
        if (value == null) {
            sb.append("null")
        } else {
            if (value.javaClass.isArray) {
                val objArray: Array<Any?>
                if (value.javaClass.componentType.isPrimitive) {
                    val arrayLen = java.lang.reflect.Array.getLength(value)
                    objArray = arrayOfNulls(arrayLen)
                    for (i in 0 until arrayLen) {
                        objArray[i] = java.lang.reflect.Array.get(value, i)
                    }
                } else {
                    objArray = value as Array<Any?>
                }
                val arrayString = objArray.contentDeepToString()
                sb.append(arrayString)
            } else {
                sb.append(value)
            }
        }
        return sb.toString()
    }

    fun valueEquals(other: Value): Boolean {
        return this.value === other.value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (value != other.value) return false
        if (type != other.type) return false
        if (!id.contentEquals(other.id)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + id.contentHashCode()
        return result
    }
}
