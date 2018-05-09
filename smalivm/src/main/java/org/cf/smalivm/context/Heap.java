package org.cf.smalivm.context;

import com.rits.cloning.Cloner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Heap {

    private static final Logger log = LoggerFactory.getLogger(Heap.class.getSimpleName());

    private final Map<String, HeapItem> keyToHeapItem;
    private final Cloner cloner;
    private Heap parent;

    Heap(Cloner cloner) {
        keyToHeapItem = new HashMap<>();
        this.cloner = cloner;
    }

    private static Set<String> getReassignedKeysBetweenChildAndAncestor(Heap child, Heap ancestor) {
        Heap current = child;
        Set<String> reassigned = new HashSet<>();
        while (current != ancestor) {
            reassigned.addAll(current.keySet());
            current = current.getParent();
        }

        return reassigned;
    }

    /**
     * Retrieves the value indexed by {@code key} from the Heap. If the value is not stored locally
     * in the current heap, search heap ancestors for the value. If found in an ancestor, stores a
     * clone of the target value to preserve history. Additionally, this locally sets any other keys
     * which point to the target value.
     *
     * @param key
     * @return
     */
    HeapItem get(String key) {
        if (hasKey(key)) {
            return keyToHeapItem.get(key);
        }

        /*
         * To save space, only registers that are accessed are contained in any given heap.
         */
        Heap ancestor = getAncestorWithKey(key);
        if (ancestor == null) {
            if (log.isTraceEnabled()) {
                log.trace("Undefined value for {}; possibly a mistake!", key, new Exception());
            }

            return null;
        }

        /*
         * Store *clone* of target value to preserve history. Also, pull down any mappings to the target value,
         * excluding mappings which are no longer valid. E.g. peeking v0 when v0 and v1 both reference the same object,
         * pull down both mappings, but only if v1 was not reassigned between now and then.
         */
        HeapItem targetItem = ancestor.get(key);
        HeapItem cloneItem = cloneItem(targetItem);
        Set<String> reassigned = getReassignedKeysBetweenChildAndAncestor(this, ancestor);
        Set<String> potential = ancestor.keySet();
        for (String currentKey : potential) {
            if (reassigned.contains(currentKey)) {
                continue;
            }

            HeapItem currentItem = ancestor.get(currentKey);
            if (targetItem.getValue() == currentItem.getValue()) {
                set(currentKey, cloneItem);
            }
        }

        return cloneItem;
    }

    HeapItem get(String heapId, int register) {
        String key = buildKey(heapId, register);

        return get(key);
    }

    Map<String, HeapItem> getKeyToItem() {
        return keyToHeapItem;
    }

    boolean hasKey(String key) {
        return keyToHeapItem.containsKey(key);
    }

    boolean hasRegister(String heapId, int register) {
        String key = buildKey(heapId, register);

        return hasKey(key);
    }

    void remove(String heapId, int register) {
        String key = buildKey(heapId, register);

        remove(key);
    }

    /**
     * Sets the heap value indexed by the {@code heapId} and {@code register} to {@code item}.
     *
     * @param heapId
     * @param register
     * @param item
     */
    void set(String heapId, int register, HeapItem item) {
        String key = buildKey(heapId, register);
        set(key, item);
    }

    /***
     * {@see #set(String, int, HeapItem)}
     *
     * @param heapId
     * @param register
     * @param value
     * @param type
     */
    void set(String heapId, int register, Object value, String type) {
        set(heapId, register, new HeapItem(value, type));
    }

    /**
     * This behaves like {@link #set(String, HeapItem) set} and also updates values for all keys
     * which point to values identical to what was stored in {@code key} originally. This is
     * necessary because the same item may exist under multiple mappings that need to be updated.
     * For example, when an uninitialized instance is stored under multiple mappings and is
     * initialized, rather than simply setting a single key with the updated instance, it's
     * necessary to look for any other identical copies of the uninitialized instance and update
     * them with the new initialized instance value.
     *
     * @param key
     * @param updatedItem
     */
    void update(String key, HeapItem updatedItem) {
        HeapItem oldItem = get(key);
        if (oldItem == null || oldItem.getValue() == null) {
            set(key, updatedItem);
        } else {
            for (String currentKey : keySet()) {
                HeapItem currentItem = get(currentKey);
                if (oldItem.valueIdentity(currentItem)) {
                    set(currentKey, updatedItem);
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
    void update(String heapId, int register, HeapItem item) {
        String key = buildKey(heapId, register);
        update(key, item);
    }

    protected Heap getParent() {
        return parent;
    }

    void setParent(Heap parent) {
        this.parent = parent;
    }

    private String buildKey(String heapId, int register) {
        return heapId + ':' + register;
    }

    private HeapItem cloneItem(HeapItem original) {
        Object cloneValue = cloner.deepClone(original.getValue());

        return new HeapItem(cloneValue, original.getType());
    }

    private Heap getAncestorWithKey(String key) {
        Heap ancestor = this;
        do {
            if (ancestor.hasKey(key)) {
                break;
            }

            ancestor = ancestor.getParent();
        } while (ancestor != null);

        return ancestor;
    }

    private Set<String> keySet() {
        // Note: mutating this directly alters keyToHeapItem's keys
        return keyToHeapItem.keySet();
    }

    private void remove(String key) {
        keyToHeapItem.remove(key);
    }

    private void set(String key, HeapItem item) {
        keyToHeapItem.put(key, item);
    }
}
