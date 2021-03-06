package org.cf.smalivm.context

import com.rits.cloning.Cloner
import org.cf.smalivm.context.HeapItem
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.util.HashSet
import java.util.HashMap

internal class Heap(cloner: Cloner) {
    private val keyToHeapItem: MutableMap<String?, HeapItem>
    private val cloner: Cloner
    protected var parent: Heap? = null
        set

    /**
     * Retrieves the value indexed by `key` from the Heap. If the value is not stored locally
     * in the current heap, search heap ancestors for the value. If found in an ancestor, stores a
     * clone of the target value to preserve history. Additionally, this locally sets any other keys
     * which point to the target value.
     *
     * @param key
     * @return
     */
    operator fun get(key: String?): HeapItem? {
        if (hasKey(key)) {
            return keyToHeapItem[key]
        }

        /*
         * To save space, only registers that are accessed are contained in any given heap.
         */
        val ancestor = getAncestorWithKey(key)
        if (ancestor == null) {
            if (log.isTraceEnabled) {
                log.trace("Undefined value for {}; possibly a mistake!", key, Exception())
            }
            return null
        }

        /*
         * Store *clone* of target value to preserve history. Also, pull down any mappings to the target value,
         * excluding mappings which are no longer valid. E.g. peeking v0 when v0 and v1 both reference the same object,
         * pull down both mappings, but only if v1 was not reassigned between now and then.
         */
        val targetItem = ancestor[key]
        val cloneItem = cloneItem(targetItem)
        val reassigned = getReassignedKeysBetweenChildAndAncestor(this, ancestor)
        val potential = ancestor.keySet()
        for (currentKey in potential) {
            if (reassigned.contains(currentKey)) {
                continue
            }
            val currentItem = ancestor[currentKey]
            if (targetItem!!.value === currentItem!!.value) {
                set(currentKey, cloneItem)
            }
        }
        return cloneItem
    }

    operator fun get(heapId: String, register: Int): HeapItem? {
        val key = buildKey(heapId, register)
        return get(key)
    }

    val keyToItem: Map<String?, HeapItem>
        get() = keyToHeapItem

    fun hasKey(key: String?): Boolean {
        return keyToHeapItem.containsKey(key)
    }

    fun hasRegister(heapId: String, register: Int): Boolean {
        val key = buildKey(heapId, register)
        return hasKey(key)
    }

    fun remove(heapId: String, register: Int) {
        val key = buildKey(heapId, register)
        remove(key)
    }

    /**
     * Sets the heap value indexed by the `heapId` and `register` to `item`.
     *
     * @param heapId
     * @param register
     * @param item
     */
    operator fun set(heapId: String, register: Int, item: HeapItem) {
        val key = buildKey(heapId, register)
        set(key, item)
    }

    /***
     * {@see #set(String, int, HeapItem)}
     *
     * @param heapId
     * @param register
     * @param value
     * @param type
     */
    operator fun set(heapId: String, register: Int, value: Any?, type: String?) {
        set(heapId, register, HeapItem(value, type))
    }

    /**
     * This behaves like [set][.set] and also updates values for all keys
     * which point to values identical to what was stored in `key` originally. This is
     * necessary because the same item may exist under multiple mappings that need to be updated.
     * For example, when an uninitialized instance is stored under multiple mappings and is
     * initialized, rather than simply setting a single key with the updated instance, it's
     * necessary to look for any other identical copies of the uninitialized instance and update
     * them with the new initialized instance value.
     *
     * @param key
     * @param updatedItem
     */
    fun update(key: String?, updatedItem: HeapItem) {
        val oldItem = get(key)
        if (oldItem == null || oldItem.value == null) {
            set(key, updatedItem)
        } else {
            for (currentKey in keySet()) {
                val currentItem = get(currentKey)
                if (oldItem.valueIdentity(currentItem)) {
                    set(currentKey, updatedItem)
                }
            }
        }
    }

    /**
     * {@see #update(String, HeapItem)}
     *
     * @param heapId
     * @param register
     * @param item
     */
    fun update(heapId: String, register: Int, item: HeapItem) {
        val key = buildKey(heapId, register)
        update(key, item)
    }

    private fun buildKey(heapId: String, register: Int): String {
        return "$heapId:$register"
    }

    private fun cloneItem(original: HeapItem?): HeapItem {
        val cloneValue = cloner.deepClone(original!!.value)
        return HeapItem(cloneValue, original.type)
    }

    private fun getAncestorWithKey(key: String?): Heap? {
        var ancestor: Heap? = this
        do {
            if (ancestor!!.hasKey(key)) {
                break
            }
            ancestor = ancestor.parent
        } while (ancestor != null)
        return ancestor
    }

    private fun keySet(): Set<String?> {
        // Note: mutating this directly alters keyToHeapItem's keys
        return keyToHeapItem.keys
    }

    private fun remove(key: String) {
        keyToHeapItem.remove(key)
    }

    private operator fun set(key: String?, item: HeapItem) {
        keyToHeapItem[key] = item
    }

    companion object {
        private val log = LoggerFactory.getLogger(Heap::class.java.simpleName)
        private fun getReassignedKeysBetweenChildAndAncestor(child: Heap, ancestor: Heap): Set<String?> {
            var current: Heap? = child
            val reassigned: MutableSet<String?> = HashSet()
            while (current !== ancestor) {
                reassigned.addAll(current!!.keySet())
                current = current.parent
            }
            return reassigned
        }
    }

    init {
        keyToHeapItem = HashMap()
        this.cloner = cloner
    }
}
