package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Heap {

    private static final Logger log = LoggerFactory.getLogger(Heap.class.getSimpleName());

    private final Map<String, HeapItem> keyToHeapItem;

    private Heap parent;

    Heap() {
        keyToHeapItem = new HashMap<String, HeapItem>();
    }

    Heap(Heap other) {
        keyToHeapItem = new HashMap<String, HeapItem>(other.keyToHeapItem);
    }

    private static Set<String> getReassignedKeysBetweenChildAndAncestor(Heap child, Heap ancestor) {
        Heap current = child;
        Set<String> reassigned = new HashSet<String>();
        while (current != ancestor) {
            reassigned.addAll(current.keySet());
            current = current.getParent();
        }

        return reassigned;
    }

    private Set<String> keySet() {
        // Note: mutating this directly alters keytoHeapItem's keys
        return keyToHeapItem.keySet();
    }

    void setParent(Heap parent) {
        this.parent = parent;
    }

    protected Heap getParent() {
        return parent;
    }

    HeapItem get(String heapId, int register) {
        String key = buildKey(heapId, register);

        return get(key);
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
                log.trace("Undefined value for " + key + " Possibly a mistake!", new Exception());
            }

            return null;
        }

        /*
         * Store *clone* of target value to preserve history. Also, pull down any mappings to the target value,
         * excluding mappings which are no longer valid. E.g. peeking v0, and v0 and v1 both point to same object, pull
         * down both mappings, but only if v1 was not reassigned between now and then.
         */
        HeapItem targetItem = ancestor.get(key);
        HeapItem cloneItem = new HeapItem(targetItem);
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

    private String buildKey(String heapId, int register) {
        StringBuilder sb = new StringBuilder(heapId);
        sb.append(':').append(register);

        return sb.toString();
    }

    boolean hasRegister(String heapId, int register) {
        String key = buildKey(heapId, register);

        return hasKey(key);
    }

    boolean hasKey(String key) {
        return keyToHeapItem.containsKey(key);
    }

    void remove(String heapId, int register) {
        String key = buildKey(heapId, register);

        remove(key);
    }

    private void remove(String key) {
        keyToHeapItem.remove(key);
    }

    void set(String heapId, int register, Object value, String type) {
        set(heapId, register, new HeapItem(value, type));
    }

    void set(String heapId, int register, HeapItem item) {
        String key = buildKey(heapId, register);
        set(key, item);
    }

    private void set(String key, HeapItem item) {
        keyToHeapItem.put(key, item);
    }

    Map<String, HeapItem> getKeyToItem() {
        return keyToHeapItem;
    }

    void update(String heapId, int register, HeapItem item) {
        String key = buildKey(heapId, register);
        update(key, item);
    }

    void update(String key, HeapItem item) {
        /*
         * When replacing an uninitialized instance with a new instance (e.g. when executing new-instance), need to
         * update all registers that reference the uninitialized instance. This would be a lot easier if Dalvik's
         * "new-instance" or Java's "new" instruction were available at compile time.
         */

        HeapItem oldItem = get(key);
        for (String currentKey : keySet()) {
            HeapItem currentValue = get(currentKey);
            if (oldItem.getValue() == currentValue.getValue()) {
                set(currentKey, item);
            }
        }
    }

}
