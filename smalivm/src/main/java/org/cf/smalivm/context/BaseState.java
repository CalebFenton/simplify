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

    private final ExecutionContext ectx;

    BaseState(BaseState parent, ExecutionContext ectx) {
        registerCount = parent.registerCount;
        registersAssigned = new TIntHashSet();
        registersRead = new TIntHashSet();
        this.ectx = ectx;
    }

    BaseState(ExecutionContext ectx) {
        this(ectx, 0);
    }

    BaseState(ExecutionContext ectx, int registerCount) {
        // The number of instances of contexts in memory could be very high. Allocate minimally.
        registersAssigned = new TIntHashSet();
        registersRead = new TIntHashSet();

        // This is locals + parameters
        this.registerCount = registerCount;

        this.ectx = ectx;
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public TIntSet getRegistersAssigned() {
        return registersAssigned;
    }

    public TIntSet getRegistersRead() {
        return registersRead;
    }

    public boolean wasRegisterAssigned(int register) {
        return getRegistersAssigned().contains(register);
    }

    void assignRegister(int register, HeapItem item, String heapId) {
        getRegistersAssigned().add(register);

        pokeRegister(register, item, heapId);
    }

    void assignRegisterAndUpdateIdentities(int register, HeapItem item, String heapId) {
        getRegistersAssigned().add(register);
        ectx.getHeap().update(heapId, register, item);
    }

    ExecutionContext getExecutionContext() {
        return ectx;
    }

    BaseState getParent() {
        ExecutionContext parentContext = ectx.getParent();
        MethodState parent = null;
        if (parentContext != null) {
            parent = parentContext.getMethodState();
        }

        return parent;
    }

    boolean hasRegister(int register, String heapId) {
        return ectx.getHeap().hasRegister(heapId, register);
    }

    HeapItem peekRegister(int register, String heapId) {
        return ectx.getHeap().get(heapId, register);
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

        ectx.getHeap().set(heapId, register, item);
    }

    HeapItem readRegister(int register, String heapId) {
        getRegistersRead().add(register);

        return peekRegister(register, heapId);
    }

    String registerToString(int register, String heapId) {
        HeapItem item = peekRegister(register, heapId);

        return item.toString();
    }

    void removeRegister(int register, String heapId) {
        ectx.getHeap().remove(heapId, register);
    }

    boolean wasRegisterRead(int register, String heapId) {
        if (getRegistersRead().contains(register)) {
            return true;
        }

        HeapItem item = peekRegister(register, heapId);
        if (null == item) {
            return false;
        }

        // Don't just examine registersRead. v0 and v1 may contain the same object reference, but v0 is never read.
        for (int currentRegister : getRegistersRead().toArray()) {
            HeapItem currentItem = peekRegister(currentRegister, heapId);
            if (item.getValue() == currentItem.getValue()) {
                return true;
            }
        }

        return false;
    }

}
