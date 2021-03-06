package org.cf.smalivm2

import com.rits.cloning.Cloner
import org.slf4j.LoggerFactory
import java.util.*

internal class ValueHeap(cloner: Cloner) {
    private val keyToValues: MutableMap<String, Value>
    private val cloner: Cloner
    var parent: ValueHeap? = null

    init {
        keyToValues = HashMap()
        this.cloner = cloner
    }


    /**
     * Retrieves the value indexed by `key` from the Heap. If the value is not stored locally
     * in the current heap, search heap ancestors for the value. If found in an ancestor, stores a
     * clone of the target value to preserve history. Additionally, this locally sets any other keys
     * which point to the target value.
     *
     * @param key
     * @return
     */
    operator fun get(key: String): Value? {
        if (hasKey(key)) {
            return keyToValues[key]
        }

        /*
         * To save space, only registers that are accessed are contained in any given heap.
         */
        val ancestor = getAncestorWithKey(key)
        if (ancestor == null) {
            log.trace("Undefined value for {}; possibly a mistake!", key, Exception())
            return null
        }

        /*
         * Store *clone* of target value to preserve history. Also, pull down any mappings to the target value,
         * excluding mappings which are no longer valid. E.g. peeking v0 when v0 and v1 both reference the same object,
         * pull down both mappings, but only if v1 was not reassigned between now and then.
         */
        val targetItem = ancestor[key]!!
        val cloneItem = cloneValue(targetItem)
        val reassigned = getReassignedKeysBetweenChildAndAncestor(this, ancestor)
        val potential = ancestor.keys()
        for (currentKey in potential) {
            if (reassigned.contains(currentKey)) {
                continue
            }
            val currentItem = ancestor[currentKey]!!
            if (targetItem.value === currentItem.value) {
                set(currentKey, cloneItem)
            }
        }
        return cloneItem
    }

    fun hasKey(key: String): Boolean {
        return keyToValues.containsKey(key)
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
     * @param updatedValue
     */
    fun update(key: String, updatedValue: Value) {
        val oldValue = get(key)!!
        if (oldValue.value == null) {
            set(key, updatedValue)
        } else {
            for (currentKey in keys()) {
                val currentValue = get(currentKey)!!
                if (oldValue.value == currentValue.value) {
                    set(currentKey, updatedValue)
                }
            }
        }
    }

    private fun cloneValue(original: Value): Value {
        val cloneValue = cloner.deepClone(original.value)
        return Value.wrap(cloneValue)
    }

    private fun getAncestorWithKey(key: String): ValueHeap? {
        var ancestor: ValueHeap? = this
        do {
            if (ancestor!!.hasKey(key)) {
                break
            }
            ancestor = ancestor.parent
        } while (ancestor != null)
        return ancestor
    }

    private fun keys(): Set<String> {
        // Note: mutating this directly alters keyToHeapItem's keys
        return keyToValues.keys
    }

    private fun remove(key: String) {
        keyToValues.remove(key)
    }

    private operator fun set(key: String, value: Value) {
        keyToValues[key] = value
    }

    companion object {
        private val log = LoggerFactory.getLogger(ValueHeap::class.java.simpleName)

        private fun getReassignedKeysBetweenChildAndAncestor(child: ValueHeap, ancestor: ValueHeap): Set<String?> {
            val reassigned: MutableSet<String> = HashSet()
            var current: ValueHeap = child
            while (current !== ancestor) {
                reassigned.addAll(current.keys())
                if (current.parent == null) {
                    break
                }
                current = current.parent!!
            }
            return reassigned
        }
    }
}
