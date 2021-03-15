package org.cf.smalivm2

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.type.VirtualType
import org.cf.util.ClassNameUtils
import org.cf.util.Utils
import java.lang.StringBuilder
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

    fun asDouble(): Double {
        return Utils.getDoubleValue(value)
    }

    fun asFloat(): Float {
        return Utils.getFloatValue(value)
    }

    fun asInteger(): Int {
        return Utils.getIntegerValue(value)
    }

    fun asLong(): Long {
        return Utils.getLongValue(value)
    }

    fun getComponentBase(): String? {
        return ClassNameUtils.getComponentBase(type)
    }

    fun getUnboxedType(): String {
        var unboxedType = ClassNameUtils.getPrimitive(type)
        if (unboxedType == null) {
            unboxedType = type
        }
        return unboxedType
    }

    fun getUnboxedValueType(): String {
        val valueType = getValueType()
        var unboxedType = ClassNameUtils.getPrimitive(valueType)
        if (unboxedType == null) {
            unboxedType = valueType
        }
        return unboxedType
    }

    fun getValueType(): String {
        return if (isNull()) type else ClassNameUtils.toInternal(value!!.javaClass)
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

    fun isImmutable(): Boolean {
        return Configuration.instance().isImmutable(type)
    }

    fun isMutable(): Boolean {
        return !isImmutable()
    }

    fun isNull(): Boolean {
        return value == null
    }

    fun isObject(): Boolean {
        return ClassNameUtils.isObject(type)
    }

    fun isPrimitive(): Boolean {
        return ClassNameUtils.isPrimitive(type)
    }

    fun isPrimitiveOrWrapper(): Boolean {
        return ClassNameUtils.isPrimitiveOrWrapper(type)
    }

    fun isPrimitiveWrapper(): Boolean {
        return ClassNameUtils.isWrapper(type)
    }

    fun isUnknown(): Boolean {
        return value is UnknownValue
    }

    fun isKnown(): Boolean {
        return !isUnknown()
    }

    fun valueIdentity(other: Value): Boolean {
        return this.value === other.value
    }

}
