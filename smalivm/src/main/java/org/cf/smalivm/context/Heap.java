package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rits.cloning.Cloner;

class Heap {

    private static final Logger log = LoggerFactory.getLogger(Heap.class.getSimpleName());

    // TODO: add immutable classes to cloner so it avoids cloning them
    private static final Cloner cloner = new Cloner();

    private final Map<String, Object> keyToValue;

    private Heap parent;

    Heap() {
        keyToValue = new HashMap<String, Object>();
    }

    Heap(Heap other) {
        keyToValue = new HashMap<String, Object>(other.keyToValue);
    }

    private static Object cloneRegisterValue(Object value) {
        Object result = cloner.deepClone(value);

        return result;
    }

    private static Set<String> getReassignedKeysBetweenChildAndAncestor(Heap child, Heap ancestor) {
        Heap current = child;
        List<String> reassigned = new LinkedList<String>();
        while (current != ancestor) {
            reassigned.addAll(current.keySet());
            current = current.getParent();
        }
        Set<String> result = new HashSet<String>(reassigned);

        return result;
    }

    private Set<String> keySet() {
        // It's not that I don't trust you to mutate the keys, but I don't trust you.
        return new HashSet<String>(keyToValue.keySet());
    }

    void setParent(Heap parent) {
        this.parent = parent;
    }

    private Heap getParent() {
        return parent;
    }

    Object get(String heapId, int register) {
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

    Object get(String key) {
        if (hasKey(key)) {
            return keyToValue.get(key);
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
        Object targetValue = ancestor.get(key);
        Object cloneValue = cloneRegisterValue(targetValue);
        Set<String> reassigned = getReassignedKeysBetweenChildAndAncestor(this, ancestor);
        Set<String> potential = ancestor.keySet();
        for (String currentKey : potential) {
            if (reassigned.contains(currentKey)) {
                continue;
            }

            Object currentValue = ancestor.get(currentKey);
            if (targetValue == currentValue) {
                set(currentKey, cloneValue);
            }
        }

        return cloneValue;
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
        return keyToValue.containsKey(key);
    }

    void remove(String heapId, int register) {
        String key = buildKey(heapId, register);

        remove(key);
    }

    private void remove(String key) {
        keyToValue.remove(key);
    }

    void set(String heapId, int register, Object value) {
        String key = buildKey(heapId, register);
        set(key, value);
    }

    private void set(String key, Object value) {
        keyToValue.put(key, value);
    }

    Map<String, Object> getKeyToValue() {
        return keyToValue;
    }

    void update(String heapId, int register, Object value) {
        String key = buildKey(heapId, register);
        update(key, value);
    }

    void update(String key, Object value) {
        /*
         * When replacing an uninitialized instance object, need to update all registers that also point to that object.
         * This would be a lot easier if Dalvik's "new-instance" or Java's "new" instruction were available at compile
         * time.
         */

        Object oldValue = get(key);
        for (String currentKey : keySet()) {
            Object currentValue = get(currentKey);
            if (oldValue == currentValue) {
                set(currentKey, value);
            }
        }
    }
}
