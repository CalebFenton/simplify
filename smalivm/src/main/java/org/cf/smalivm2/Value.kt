package org.cf.smalivm2

import org.cf.util.ClassNameUtils
import java.util.*

data class Value(val value: Any?, val type: String, val id: ByteArray) {
    companion object {
        fun wrap(value: Any?): Value {
            return wrap(value, ClassNameUtils.toInternal(value.javaClass))
        }

        fun wrap(value: Any?, type: String): Value {
            val id = ByteArray(4)
            Random().nextBytes(id)
            return Value(value, type, id)
        }

        private fun wrap(other: Value): Value {
            return Value(other.value, other.type, other.id)
        }
    }

    fun hasSameID(other: Value): Boolean {
        return id.contentEquals(other.id)
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
