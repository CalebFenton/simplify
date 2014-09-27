package org.cf.smalivm.context;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.type.TypeUtil;
import org.cf.util.SmaliClassUtils;

public class MethodState extends BaseState {

    public static final int ResultRegister = -1;
    public static final int ReturnAddress = -3;
    public static final int ReturnRegister = -2;

    private static final String HEAP_ID = "method";

    private final TIntObjectMap<Object> mutableParameterIndexToValue;

    private final int parameterCount;
    public MethodState(ExecutionContext ectx, int parameterCount) {
        this(ectx, parameterCount, parameterCount);
    }

    public MethodState(ExecutionContext ectx, int registerCount, int parameterCount) {
        super(ectx, HEAP_ID, registerCount);

        this.parameterCount = parameterCount;
        this.mutableParameterIndexToValue = new TIntObjectHashMap<Object>();
    }

    MethodState(MethodState other, ExecutionContext ectx) {
        super(other, ectx);

        this.parameterCount = other.parameterCount;
        this.mutableParameterIndexToValue = new TIntObjectHashMap<Object>(other.mutableParameterIndexToValue);
    }

    public void assignParameter(int parameterIndex, Object value) {
        pokeRegister(getParameterStart() + parameterIndex, value);

        String type = TypeUtil.getValueType(value);
        type = SmaliClassUtils.javaClassToSmali(type);
        boolean mutable = !SmaliClassUtils.isImmutableClass(type);
        if (mutable) {
            mutableParameterIndexToValue.put(parameterIndex, value);
        }
    }

    public void assignResultRegister(Object value) {
        assignRegister(ResultRegister, value);
    }

    public void assignReturnRegister(Object value) {
        pokeRegister(ReturnRegister, value);
    }

    // This is for the optimizer.
    public Object getMutableParameter(int parameterIndex) {
        MethodState targetContext = this;
        if (!targetContext.mutableParameterIndexToValue.containsKey(parameterIndex)) {
            targetContext = getAncestorContextWithParameter(parameterIndex);
        }
        Object result = targetContext.mutableParameterIndexToValue.get(parameterIndex);

        return result;
    }

    // This is what you want for emulated methods.
    public Object getParameter(int parameterIndex) {
        return peekRegister(getParameterStart() + parameterIndex);
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public int getParameterStart() {
        return getRegisterCount() - parameterCount;
    }

    @Override
    public MethodState getParent() {
        return (MethodState) super.getParent();
    }

    public int getPseudoInstructionReturnAddress() {
        return (int) peekRegister(ReturnAddress);
    }

    @Override
    public Object peekRegister(int register) {
        Object[] parts = peekWithTargetContext(register);
        Object value = parts[0];
        MethodState targetContext = (MethodState) parts[1];

        if ((targetContext == this) || (targetContext == null) || (value == null)) {
            return value;
        }

        TIntObjectMap<Object> targetRegisterToValue = targetContext.getRegisterToValue();
        Object targetValue = targetRegisterToValue.get(register);
        TIntObjectMap<Object> targetParameterIndexToValue = targetContext.mutableParameterIndexToValue;
        for (int parameterIndex : targetParameterIndexToValue.keys()) {
            if (targetParameterIndexToValue.get(parameterIndex) == targetValue) {
                mutableParameterIndexToValue.put(parameterIndex, value);
            }
        }

        return value;
    }

    public Object readResultRegister() {
        Object result = readRegister(ResultRegister);
        // TODO: removeRegister and see what breaks..
        // removeRegister(ResultRegister);

        return result;
    }

    public Object readReturnRegister() {
        return peekRegister(ReturnRegister);
    }

    public void setPseudoInstructionReturnAddress(int address) {
        // Pseudo instructions like array-data-payload need return addresses.
        pokeRegister(ReturnAddress, address);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\nparameters: ").append(parameterCount).append("\n");
        if (hasRegister(ResultRegister)) {
            sb.append("result: ").append(registerToString(ResultRegister)).append("\n");
        }
        if (hasRegister(ReturnRegister)) {
            sb.append("return: ").append(registerToString(ReturnRegister));
        }

        return sb.toString();
    }

    private MethodState getAncestorContextWithParameter(int parameterIndex) {
        MethodState currentContext = this;
        do {
            if (currentContext.mutableParameterIndexToValue.containsKey(parameterIndex)) {
                return currentContext;
            }
            currentContext = currentContext.getParent();
        } while (currentContext != null);

        return null;
    }

    MethodState getChild(ExecutionContext childContext) {
        MethodState child = new MethodState(childContext, getRegisterCount(), getParameterCount());

        return child;
    }
}
