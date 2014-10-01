package org.cf.smalivm.context;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rits.cloning.Cloner;

class Heap {

    // TODO: add immutable classes to cloner so it avoids cloning them
    private static final Cloner cloner = new Cloner();

    private static final Logger log = LoggerFactory.getLogger(Heap.class.getSimpleName());

    private final TMap<HeapKey, Object> heapKeyToValue;
    private Heap parent;

    Heap() {
        heapKeyToValue = new THashMap<HeapKey, Object>();
    }

    Heap(Heap other) {
        heapKeyToValue = new THashMap<HeapKey, Object>(other.heapKeyToValue);
    }

    private static Object cloneRegisterValue(Object value) {
        Object result = cloner.deepClone(value);

        return result;
    }

    private static Set<HeapKey> getReassignedKeysBetweenChildAndAncestor(Heap child, Heap ancestor) {
        Heap current = child;
        Set<HeapKey> result = new HashSet<HeapKey>();
        while (current != ancestor) {
            result.addAll(current.getHeapKeySet());
            current = current.getParent();
        }

        return result;
    }

    private Set<HeapKey> getHeapKeySet() {
        return heapKeyToValue.keySet();
    }

    void setParent(Heap parent) {
        this.parent = parent;
    }

    private Heap getParent() {
        return parent;
    }

    Object get(String heapId, int register) {
        HeapKey key = new HeapKey(heapId, register);

        return get(key);
    }

    private Heap getAncestorWithKey(HeapKey key) {
        Heap ancestor = this;
        do {
            if (ancestor.hasKey(key)) {
                break;
            }

            ancestor = ancestor.getParent();
        } while (ancestor != null);

        return ancestor;
    }

    Object get(HeapKey key) {
        if (hasKey(key)) {
            return heapKeyToValue.get(key);
        }

        /*
         * To save space, only registers that are accessed are contained in any given heap.
         */
        Heap ancestor = getAncestorWithKey(key);
        if (ancestor == null) {
            Exception e = new Exception();
            log.warn(key + " is being read but is null. Possibly a mistake!", e);

            return null;
        }

        /*
         * Store *clone* of target value to preserve history. Also, pull down any mappings to the target value,
         * excluding mappings which are no longer valid. E.g. peeking v0, and v0 and v1 both point to same object, pull
         * down both mappings, but only if v1 was not reassigned between now and then.
         */
        Object targetValue = ancestor.get(key);
        Object cloneValue = cloneRegisterValue(targetValue);
        Set<HeapKey> reassigned = getReassignedKeysBetweenChildAndAncestor(this, ancestor);
        for (HeapKey currentKey : ancestor.getHeapKeySet()) {
            if (!reassigned.contains(currentKey)) {
                Object currentValue = ancestor.get(currentKey);
                if (targetValue == currentValue) {
                    set(currentKey, cloneValue);
                }
            }

        }

        return cloneValue;
    }

    boolean hasRegister(String heapId, int register) {
        HeapKey key = new HeapKey(heapId, register);

        return hasKey(key);
    }

    boolean hasKey(HeapKey key) {
        return heapKeyToValue.containsKey(key);
    }

    void remove(String heapId, int register) {
        HeapKey key = new HeapKey(heapId, register);

        remove(key);
    }

    void remove(HeapKey key) {
        heapKeyToValue.remove(key);
    }

    void set(String heapId, int register, Object value) {
        HeapKey key = new HeapKey(heapId, register);
        set(key, value);
    }

    void set(HeapKey key, Object value) {
        heapKeyToValue.put(key, value);
    }

    Map<HeapKey, Object> getHeapKeyToValue() {
        return heapKeyToValue;
    }

    void update(String heapId, int register, Object value) {
        HeapKey key = new HeapKey(heapId, register);
        update(key, value);
    }

    void update(HeapKey key, Object value) {
        /*
         * When replacing an uninitialized instance object, need to update all registers that also point to that object.
         * This would be a lot easier if Dalvik's "new-instance" or Java's "new" instruction were available at compile
         * time.
         */

        Object oldValue = get(key);
        for (HeapKey currentKey : heapKeyToValue.keySet()) {
            Object currentValue = get(currentKey);
            if (oldValue == currentValue) {
                set(currentKey, value);
            }
        }
    }
}
