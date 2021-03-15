package org.cf.smalivm.context

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.cf.smalivm.context.ClassState
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.type.VirtualField
import org.cf.smalivm.type.VirtualType
import org.slf4j.LoggerFactory

class ClassState(val virtualClass: VirtualType, context: ExecutionContext?) : BaseState(context!!, virtualClass.fields.size) {

    private constructor(parent: ClassState, context: ExecutionContext) : this(parent.virtualClass, context) {}

    fun assignField(field: VirtualField, value: Any?) {
        val register = 0
        val heapId = getHeapId(field)
        val type = field.type
        assignRegister(register, HeapItem(value, type), heapId)
    }

    fun assignField(field: VirtualField, item: HeapItem) {
        val register = 0
        val heapId = getHeapId(field)
        assignRegister(register, item, heapId)
    }

//    fun getChild(childContext: ExecutionContext): ClassState {
//        return ClassState(this, childContext)
//    }

//    override fun hashCode(): Int {
//        return HashCodeBuilder(1337, 13).append(virtualClass).hashCode()
//    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        } else if (obj === this) {
            return true
        } else if (obj.javaClass != javaClass) {
            return false
        }
        val other = obj as ClassState
        return EqualsBuilder().append(virtualClass, other.virtualClass).isEquals
    }

    override fun toString(): String {
        val sb = StringBuilder("Fields:\n")
        for (field in virtualClass.fields) {
            sb.append(field).append(" = ").append(peekField(field)).append('\n')
        }
        sb.setLength(sb.length - 1)
        sb.append('\n')
        return sb.toString()
    }

    fun peekField(field: VirtualField): HeapItem? {
        val register = 0
        val heapId = getHeapId(field)
        var fieldItem: HeapItem? = peekRegister(register, heapId)
        if (fieldItem == null) {
            log.error("Undefined field: {}; returning unknown", field)
            fieldItem = HeapItem.newUnknown(field.type)
        }
        return fieldItem
    }

    fun pokeField(field: VirtualField, value: Any?) {
        val register = 0
        val heapId = getHeapId(field)
        val type = field.type
        pokeRegister(register, HeapItem(value, type), heapId)
    }

    fun pokeField(field: VirtualField, item: HeapItem) {
        val register = 0
        val heapId = getHeapId(field)
        pokeRegister(register, item, heapId)
    }

    fun updateIdentities(field: VirtualField, item: HeapItem) {
        val register = 0
        val heapId = getHeapId(field)
        val heapKey = "$heapId:$register"
        updateKey(heapKey, item)
    }

    private fun getHeapId(field: VirtualField): String {
        return field.toString()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ClassState::class.java.simpleName)
    }
}
