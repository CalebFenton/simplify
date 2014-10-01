package org.cf.smalivm.context;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.type.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BaseState {

    private static final Logger log = LoggerFactory.getLogger(BaseState.class.getSimpleName());

    private final int registerCount;
    private final TIntList registersAssigned;
    private final TIntList registersRead;

    final ExecutionContext ectx;

    BaseState(BaseState parent, ExecutionContext ectx) {
        registerCount = parent.registerCount;
        registersAssigned = new TIntArrayList(0);
        registersRead = new TIntArrayList(0);
        this.ectx = ectx;
    }

    BaseState(ExecutionContext ectx) {
        this(ectx, 0);
    }

    BaseState(ExecutionContext ectx, int registerCount) {
        // The number of instances of contexts in memory could be very high. Allocate minimally.
        registersAssigned = new TIntArrayList(0);
        registersRead = new TIntArrayList(0);

        // This is locals + parameters
        this.registerCount = registerCount;

        this.ectx = ectx;
    }

    public void assignRegister(int register, Object value, String heapId) {
        getRegistersAssigned().add(register);

        pokeRegister(register, value, heapId);
    }

    public void assignRegisterAndUpdateIdentities(int register, Object value, String heapId) {
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

    public TIntList getRegistersAssigned() {
        return registersAssigned;
    }

    public TIntList getRegistersRead() {
        return registersRead;
    }

    boolean hasRegister(int register, String heapId) {
        return ectx.getHeap().hasRegister(heapId, register);
    }

    public Object peekRegister(int register, String heapId) {
        return ectx.getHeap().get(heapId, register);
    }

    public String peekRegisterType(int register, String heapId) {
        Object value = peekRegister(register, heapId);

        return TypeUtil.getValueType(value);
    }

    public void pokeRegister(int register, Object value, String heapId) {
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            // StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            // for (int i = 2; i < ste.length; i++) {
            // sb.append("\n\t").append(ste[i]);
            // }
            log.debug("Setting r" + register + " = " + registerValueToString(value) + sb.toString());
        }

        ectx.getHeap().set(heapId, register, value);
    }

    public Object readRegister(int register, String heapId) {
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
        Object value = peekRegister(register, heapId);
        if (ClassUtils.isPrimitiveOrWrapper(value.getClass()) || (value.getClass() == String.class)) {
            /*
             * This is a hack which suggests maintaining register types. Primitives are stored internally as their
             * wrappers. They'll equals() even if they're different object instances and the check in the else below
             * will cause any register containing a value which might also be contained in a primitive wrapper to appear
             * to be referencing the same object, which throws off the optimizer.
             */
            return registersRead.contains(register);
        } else {
            /*
             * It's not enough to examine registersRead for object references. v0 and v1 may contain the same object,
             * and v0 is never read.
             */
            TIntList registers = getRegistersRead();
            for (int currentRegister : registers.toArray()) {
                Object currentValue = peekRegister(currentRegister, heapId);
                if (value == currentValue) {
                    return true;
                }
            }
        }

        return false;
    }

    protected String registerToString(int register, String heapId) {
        Object value = peekRegister(register, heapId);

        return registerValueToString(value);
    }

    protected String registerValueToString(Object value) {
        StringBuilder sb = new StringBuilder();
        if (value == null) {
            sb.append("type=null, value=null");
        } else {
            sb.append("type=").append(TypeUtil.getValueType(value)).append(", value=").append(value.toString())
            .append(", hc=").append(value.hashCode());
        }

        return sb.toString();
    }

}
