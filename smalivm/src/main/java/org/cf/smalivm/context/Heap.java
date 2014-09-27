package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Heap {

    Map<Pair<String, Integer>, Object> heapKeyToValue;

    Heap() {
        heapKeyToValue = new HashMap<Pair<String, Integer>, Object>();
    }

    Heap(Heap other) {
        heapKeyToValue = new HashMap<Pair<String, Integer>, Object>(other.heapKeyToValue);
    }

    Object get(String heapId, int register) {
        Pair<String, Integer> key = new ImmutablePair<String, Integer>(heapId, register);

        return heapKeyToValue.get(key);
    }

    void remove(String heapId, int register) {
        Pair<String, Integer> key = new ImmutablePair<String, Integer>(heapId, register);

        heapKeyToValue.remove(key);
    }

    void set(String heapId, int register, Object value) {
        Pair<String, Integer> key = new ImmutablePair<String, Integer>(heapId, register);

        heapKeyToValue.put(key, value);
    }

}
