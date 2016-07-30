package org.cf.smalivm.context;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BaseState {

    private static final Logger log = LoggerFactory.getLogger(BaseState.class.getSimpleName());

    private final int registerCount;
    private final TIntSet registersAssigned;
    private final TIntSet registersRead;

    private final ExecutionContext context;

    BaseState(BaseState parent, ExecutionContext context) {
        registerCount = parent.registerCount;
        registersAssigned = new TIntHashSet();
        registersRead = new TIntHashSet();
        this.context = context;
    }

    BaseState(ExecutionContext context) {
        this(context, 0);
    }

    BaseState(ExecutionContext context, int registerCount) {
        // The number of instances of contexts in memory could be very high. Allocate minimally.
        registersAssigned = new TIntHashSet();
        registersRead = new TIntHashSet();

        // This is locals + parameters
        this.registerCount = registerCount;

        this.context = context;
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public int[] getRegistersAssigned() {
        return registersAssigned.toArray();
    }

    public int[] getRegistersRead() {
        return registersRead.toArray();
    }

    public boolean wasRegisterAssigned(int register) {
        return registersAssigned.contains(register);
    }

    void assignRegister(int register, HeapItem item, String heapId) {
        registersAssigned.add(register);
        pokeRegister(register, item, heapId);
    }

    void assignRegisterAndUpdateIdentities(int register, HeapItem item, String heapId) {
        registersAssigned.add(register);
        context.getHeap().update(heapId, register, item);
    }

    void updateKey(String key, HeapItem item) {
        context.getHeap().update(key, item);
    }

    ExecutionContext getContext() {
        return context;
    }

    BaseState getParent() {
        ExecutionContext parentContext = context.getParent();
        MethodState parent = null;
        if (parentContext != null) {
            parent = parentContext.getMethodState();
        }

        return parent;
    }

    boolean hasRegister(int register, String heapId) {
        return context.getHeap().hasRegister(heapId, register);
    }

    HeapItem peekRegister(int register, String heapId) {
        return context.getHeap().get(heapId, register);
    }

    void pokeRegister(int register, HeapItem item, String heapId) {
        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Setting ").append(heapId).append(':').append(register).append(" = ").append(item);
            // VERY noisy
            // StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            // for (int i = 2; i < ste.length; i++) {
            // sb.append("\n\t").append(ste[i]);
            // }
            log.trace(sb.toString());
        }

        context.getHeap().set(heapId, register, item);
    }

    HeapItem readRegister(int register, String heapId) {
        registersRead.add(register);

        return peekRegister(register, heapId);
    }

    void removeRegister(int register, String heapId) {
        context.getHeap().remove(heapId, register);
    }

    boolean wasRegisterRead(int register, String heapId) {
        if (registersRead.contains(register)) {
            return true;
        }

        HeapItem item = peekRegister(register, heapId);
        if (null == item) {
            return false;
        }

        /*
         * Since multiple registers may hold the same object class, need to examine other registers for identity.
         * However, result register must be excluded because move-result will always read and assign an identical object
         * every time it's executed.
         */
        for (int currentRegister : getRegistersRead()) {
            if (currentRegister == MethodState.ResultRegister) {
                continue;
            }
            HeapItem currentItem = peekRegister(currentRegister, heapId);
            if (item.getValue() == currentItem.getValue()) {
                return true;
            }
        }

        return false;
    }

}
