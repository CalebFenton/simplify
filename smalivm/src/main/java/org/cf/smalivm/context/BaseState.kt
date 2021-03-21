package org.cf.smalivm.context

import gnu.trove.set.TIntSet
import gnu.trove.set.hash.TIntHashSet
import org.cf.smalivm.context.BaseState
import org.slf4j.LoggerFactory

open class BaseState {
    val registerCount: Int
    private val registersAssigned: TIntSet
    private val registersRead: TIntSet
    val context: ExecutionContext

    constructor(parent: BaseState, context: ExecutionContext) {
        registerCount = parent.registerCount
        registersAssigned = TIntHashSet()
        registersRead = TIntHashSet()
        this.context = context
    }

    @JvmOverloads
    constructor(context: ExecutionContext, registerCount: Int = 0) {
        // The number of instances of contexts in memory could be very high. Allocate minimally.
        registersAssigned = TIntHashSet()
        registersRead = TIntHashSet()

        // This is locals + parameters
        this.registerCount = registerCount
        this.context = context
    }

    fun getRegistersAssigned(): IntArray {
        return registersAssigned.toArray()
    }

    fun getRegistersRead(): IntArray {
        return registersRead.toArray()
    }

    fun wasRegisterAssigned(register: Int): Boolean {
        return registersAssigned.contains(register)
    }

    fun assignRegister(register: Int, item: HeapItem, heapId: String) {
        registersAssigned.add(register)
        pokeRegister(register, item, heapId)
    }

    fun assignRegisterAndUpdateIdentities(register: Int, item: HeapItem, heapId: String) {
        registersAssigned.add(register)
        context.heap.update(heapId, register, item)
    }

    fun updateKey(key: String?, item: HeapItem) {
        context.heap.update(key, item)
    }

    open val parent: BaseState?
        get() {
            val parentContext = context.parent
            var parent: MethodState? = null
            if (parentContext != null) {
                parent = parentContext.methodState
            }
            return parent
        }

    fun hasRegister(register: Int, heapId: String): Boolean {
        return context.heap.hasRegister(heapId, register)
    }

    fun peekRegister(register: Int, heapId: String): HeapItem? {
        return context.heap[heapId, register]
    }

    fun pokeRegister(register: Int, item: HeapItem, heapId: String) {
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

    fun readRegister(register: Int, heapId: String): HeapItem {
        registersRead.add(register)
        return peekRegister(register, heapId)
    }

    fun removeRegister(register: Int, heapId: String) {
        context.heap.remove(heapId, register)
    }

    fun wasRegisterRead(register: Int, heapId: String): Boolean {
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

    companion object {
        private val log = LoggerFactory.getLogger(BaseState::class.java.simpleName)
    }
}
