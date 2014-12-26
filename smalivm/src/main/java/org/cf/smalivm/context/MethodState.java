package org.cf.smalivm.context;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.cf.util.ImmutableUtils;

public class MethodState extends BaseState {

    public static final int ResultRegister = -1;
    public static final int ReturnAddress = -3;
    public static final int ReturnRegister = -2;

    public static final String MUTABLE_PARAMETER_HEAP = "mutable";
    public static final String METHOD_HEAP = "method";

    private final int parameterCount;
    private final int parameterSize;
    private final TIntSet mutableParameters;

    public MethodState(ExecutionContext ectx, int registerCount) {
        this(ectx, registerCount, 0, 0);
    }

    public MethodState(ExecutionContext ectx, int registerCount, int parameterCount, int parameterSize) {
        super(ectx, registerCount);

        this.parameterCount = parameterCount;
        this.parameterSize = parameterSize;
        mutableParameters = new TIntHashSet(parameterCount);
    }

    MethodState(MethodState other, ExecutionContext ectx) {
        super(other, ectx);

        this.parameterCount = other.parameterCount;
        this.parameterSize = other.parameterSize;
        mutableParameters = new TIntHashSet(other.mutableParameters);
    }

    private MethodState(MethodState parent, ExecutionContext ectx, TIntSet mutableParameters) {
        super(parent, ectx);

        this.parameterCount = parent.parameterCount;
        this.parameterSize = parent.parameterSize;
        this.mutableParameters = parent.mutableParameters;
    }

    public void assignParameter(int parameterRegister, Object value, String type) {
        assignRegister(parameterRegister, value, type, METHOD_HEAP);

        boolean mutable = !ImmutableUtils.isImmutableClass(type);
        if (mutable) {
            pokeRegister(parameterRegister, value, type, MUTABLE_PARAMETER_HEAP);
            mutableParameters.add(parameterRegister);
        }
    }

    public void assignRegister(int register, Object value, String type) {
        super.assignRegister(register, value, type, METHOD_HEAP);
    }

    public void assignResultRegister(Object value, String type) {
        assignRegister(ResultRegister, value, type, METHOD_HEAP);
    }

    public void assignReturnRegister(Object value, String type) {
        pokeRegister(ReturnRegister, value, type, METHOD_HEAP);
    }

    public void pokeRegister(int register, Object value, String type) {
        super.pokeRegister(register, value, type, METHOD_HEAP);
    }

    public HeapValue readRegister(int register) {
        return readRegister(register, METHOD_HEAP);
    }

    public HeapValue peekParameter(int parameterRegister) {
        HeapValue value;
        if (mutableParameters.contains(parameterRegister)) {
            value = peekRegister(parameterRegister, MUTABLE_PARAMETER_HEAP);
        } else {
            value = peekRegister(parameterRegister);
        }

        return value;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public int getParameterStart() {
        return getRegisterCount() - parameterSize;
    }

    @Override
    public MethodState getParent() {
        return (MethodState) super.getParent();
    }

    public int getPseudoInstructionReturnAddress() {
        return (int) peekRegister(ReturnAddress).getValue();
    }

    public HeapValue peekRegister(int register) {
        return super.peekRegister(register, METHOD_HEAP);
    }

    public HeapValue readResultRegister() {
        HeapValue result = readRegister(ResultRegister, METHOD_HEAP);
        ectx.getHeap().remove(METHOD_HEAP, ResultRegister);

        return result;
    }

    public HeapValue readReturnRegister() {
        return peekRegister(ReturnRegister);
    }

    public void setPseudoInstructionReturnAddress(int address) {
        // Pseudo instructions like array-data-payload need return addresses.
        pokeRegister(ReturnAddress, address, METHOD_HEAP);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getParameterCount() > 0) {
            sb.append("parameters: ").append(parameterCount).append("\n[");
            boolean printingAtLeastOneParameter = false;
            for (int parameterRegister = getParameterStart(); parameterRegister < getRegisterCount();) {
                sb.append('p').append(parameterRegister).append(": ");
                if (super.hasRegister(parameterRegister, METHOD_HEAP)) {
                    printingAtLeastOneParameter = true;
                    HeapValue value = peekRegister(parameterRegister);
                    sb.append(value);
                    if ("J".equals(value.getType()) || "D".equals(value.getType())) {
                        parameterRegister += 1;
                    }
                    sb.append(",\n");
                } else {
                    sb.append("*in ancestor*\n");
                }
                parameterRegister++;
            }
            if (printingAtLeastOneParameter) {
                sb.setLength(sb.length() - 2);
            }
            sb.append(']');
        }

        int localsCount = getRegisterCount() - getParameterCount();
        if (localsCount > 0) {
            boolean hadAtLeastOneLocal = false;
            sb.append("\nlocals: ").append(localsCount).append("\n[");
            for (int register = 0; register < (getRegisterCount() - getParameterCount()); register++) {
                if (!hasRegister(register, METHOD_HEAP)) {
                    continue;
                }
                hadAtLeastOneLocal = true;
                sb.append('v').append(register).append(": ").append(registerToString(register, METHOD_HEAP))
                .append(",\n");
            }
            if (hadAtLeastOneLocal) {
                sb.setLength(sb.length() - 2);
            }
            sb.append(']');
        }

        if (hasRegister(ResultRegister, METHOD_HEAP)) {
            sb.append("\nresult: ").append(registerToString(ResultRegister, METHOD_HEAP));
        }

        if (hasRegister(ReturnRegister, METHOD_HEAP)) {
            sb.append("\nreturn: ").append(registerToString(ReturnRegister, METHOD_HEAP));
        }

        return sb.toString();
    }

    public boolean wasRegisterRead(int register) {
        return wasRegisterRead(register, METHOD_HEAP);
    }

    public void assignRegisterAndUpdateIdentities(int register, Object value, String type) {
        assignRegisterAndUpdateIdentities(register, new HeapValue(value, type), METHOD_HEAP);
    }

    MethodState getChild(ExecutionContext childContext) {
        MethodState child = new MethodState(this, childContext, mutableParameters);

        return child;
    }

}
