package org.cf.smalivm2

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.cf.smalivm.context.ClassState
import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.type.VirtualField
import org.cf.util.ClassNameUtils
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

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

class ExecutionState(val registerCount: Int, val parameterCount: Int = 0, val parameterSize: Int = 0, val fieldCount: Int = 0, initializedClassesSize: Int = 0, val multiverseId: ByteArray = ByteArray(4)) {
    val values = HashMap<String, Value>(registerCount + fieldCount)
    val initializedClasses = HashSet<String>(initializedClassesSize)
    private val registersAssigned: MutableSet<Int> = HashSet<Int>(0)
    private val registersRead: MutableSet<Int> = HashSet<Int>(0)
    var node: ExecutionNode? = null

    init {
        if (multiverseId.contentEquals(byteArrayOf(0x00, 0x00, 0x00, 0x00))) {
            getNewId(multiverseId)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExecutionState::class.java.simpleName)
        private val rnd = Random()

        fun getNewId(multiverseId: ByteArray) {
            rnd.nextBytes(multiverseId)
        }
    }


    fun wasRegisterAssigned(register: Int): Boolean {
        return registersAssigned.contains(register)
    }

    fun assignRegister(register: Int, value: Value, updateIdentities: Boolean = false) {
        registersAssigned.add(register)
        if (updateIdentities) {
            node.value
        } else {
            pokeRegister(register, value)
        }
    }

    fun assignRegisterAndUpdateIdentities(register: Int, value: HeapItem?, heapId: String?) {
        registersAssigned.add(register)
        context.heap.update(heapId, register, item)
    }

//    open val parent: BaseState?
//        get() {
//            val parentContext = context.parent
//            var parent: MethodState? = null
//            if (parentContext != null) {
//                parent = parentContext.methodState
//            }
//            return parent
//        }

    fun hasRegister(register: Int, heapId: String?): Boolean {
        return context.heap.hasRegister(heapId, register)
    }

    fun peekRegister(register: Int, heapId: String?): HeapItem {
        return context.heap[heapId, register]
    }

    fun pokeRegister(register: Int, item: HeapItem?, heapId: String?) {
        if (log.isTraceEnabled) {
            val sb = StringBuilder()
            sb.append("Setting ").append(heapId).append(':').append(register).append(" = ").append(item)
            // VERY noisy
            // StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            // for (int i = 2; i < ste.length; i++) {
            // sb.append("\n\t").append(ste[i]);
            // }
            log.trace(sb.toString())
        }
        context.heap[heapId, register] = item
    }

    fun readRegister(register: Int, heapId: String?): HeapItem {
        registersRead.add(register)
        return peekRegister(register, heapId)
    }

    fun removeRegister(register: Int, heapId: String?) {
        context.heap.remove(heapId, register)
    }

    fun wasRegisterRead(register: Int, heapId: String?): Boolean {
        if (registersRead.contains(register)) {
            return true
        }
        val item = peekRegister(register, heapId) ?: return false

        /*
         * Since multiple registers may hold the same object class, need to examine other registers for identity.
         * However, result register must be excluded because move-result will always read and assign an identical object
         * every time it's executed.
         */for (currentRegister in getRegistersRead()) {
            if (currentRegister == MethodState.ResultRegister) {
                continue
            }
            val currentItem = peekRegister(currentRegister, heapId)
            if (item.value === currentItem.value) {
                return true
            }
        }
        return false
    }

    fun assignField(field: VirtualField, value: Any?) {
        val register = 0
        val heapId = getHeapId(field)
        val type = field.type
        assignRegister(register, HeapItem(value, type), heapId)
    }

    fun assignField(field: VirtualField, item: HeapItem?) {
        val register = 0
        val heapId = getHeapId(field)
        assignRegister(register, item, heapId)
    }

    fun getChild(childContext: ExecutionContext): ClassState {
        return ClassState(this, childContext)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(1337, 13).append(virtualClass).hashCode()
    }

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

    fun pokeField(field: VirtualField, item: HeapItem?) {
        val register = 0
        val heapId = getHeapId(field)
        pokeRegister(register, item, heapId)
    }

    fun updateIdentities(field: VirtualField, item: HeapItem?) {
        val register = 0
        val heapId = getHeapId(field)
        val heapKey = "$heapId:$register"
        updateKey(heapKey, item)
    }

}
