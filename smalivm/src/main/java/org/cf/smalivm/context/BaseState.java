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

    final ExecutionContext ectx;

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

    public void assignRegister(int register, Object value, String type, String heapId) {
        getRegistersAssigned().add(register);

        pokeRegister(register, value, type, heapId);
    }

    public void assignRegisterAndUpdateIdentities(int register, HeapValue value, String heapId) {
        getRegistersAssigned().add(register);
        ectx.getHeap().update(heapId, register, value);
    }

    public BaseState getParent() {
        ExecutionContext parentContext = ectx.getParent();
        MethodState parent = null;
        if (parentContext != null) {
            parent = parentContext.getMethodState();
        }

        return parent;
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

    boolean hasRegister(int register, String heapId) {
        return ectx.getHeap().hasRegister(heapId, register);
    }

    public HeapValue peekRegister(int register, String heapId) {
        return ectx.getHeap().get(heapId, register);
    }

    public String peekRegisterType(int register, String heapId) {
        HeapValue value = peekRegister(register, heapId);

        return value.getType();
    }

    public void pokeRegister(int register, Object value, String type, String heapId) {
        pokeRegister(register, new HeapValue(value, type), heapId);
    }

    public void pokeRegister(int register, HeapValue value, String heapId) {
        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Setting ").append(heapId).append(":").append(register).append(" = ")
                            .append(registerValueToString(value));
            // VERY noisy
            // StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            // for (int i = 2; i < ste.length; i++) {
            // sb.append("\n\t").append(ste[i]);
            // }
            log.trace(sb.toString());
        }

        ectx.getHeap().set(heapId, register, value);
    }

    public HeapValue readRegister(int register, String heapId) {
        getRegistersRead().add(register);

        return peekRegister(register, heapId);
    }

    public void removeRegister(int register, String heapId) {
        ectx.getHeap().remove(heapId, register);
    }

    public boolean wasRegisterAssigned(int register) {
        return getRegistersAssigned().contains(register);
    }

    public boolean wasRegisterRead(int register, String heapId) {
        HeapValue value = peekRegister(register, heapId);
        if (getRegistersRead().contains(register)) {
            return true;
        }

        // Don't just examine registersRead, v0 and v1 may contain the same object reference, but v0 is never read.
        for (int currentRegister : getRegistersRead().toArray()) {
            HeapValue currentValue = peekRegister(currentRegister, heapId);
            if (value.getValue() == currentValue.getValue()) {
                return true;
            }
        }

        return false;
    }

    protected String registerToString(int register, String heapId) {
        HeapValue value = peekRegister(register, heapId);

        return registerValueToString(value);
    }

    protected String registerValueToString(HeapValue value) {
        StringBuilder sb = new StringBuilder();
        sb.append("type=").append(value.getType()).append(", value=").append(value.getValue());
        if (value.getValue() != null) {
            sb.append(", hc=").append(value.getValue().hashCode());
        }

        return sb.toString();
    }

}
