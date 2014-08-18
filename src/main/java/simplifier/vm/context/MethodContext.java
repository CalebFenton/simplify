package simplifier.vm.context;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import util.SmaliClassUtils;

public class MethodContext extends BaseContext {

    public static final int ResultRegister = -1;
    public static final int ReturnAddress = -3;
    public static final int ReturnRegister = -2;

    public static MethodContext build(TIntObjectMap<Object> registers) {
        MethodContext ctx = new MethodContext(registers.size());
        for (int register : registers.keys()) {
            Object value = registers.get(register);
            ctx.assignRegister(register, value);
        }

        return ctx;
    }

    private MethodContext getAncestorContextWithParameter(int parameterIndex) {
        MethodContext currentContext = this;
        do {
            if (currentContext.mutableParameterIndexToValue.containsKey(parameterIndex)) {
                return currentContext;
            }
            currentContext = currentContext.getParent();
        } while (currentContext != null);

        return null;
    }

    private int callDepth;
    private final int parameterCount;

    private final TIntObjectMap<Object> mutableParameterIndexToValue;

    public MethodContext(int registerCount, int parameterCount, int callDepth) {
        super(registerCount);

        this.parameterCount = parameterCount;
        this.callDepth = callDepth;
        this.mutableParameterIndexToValue = new TIntObjectHashMap<Object>();
    }

    MethodContext(int parameterCount) {
        this(parameterCount, parameterCount, 0);
    }

    MethodContext(int parameterCount, int callDepth) {
        this(parameterCount, parameterCount, callDepth);
    }

    public MethodContext(MethodContext parent) {
        super(parent);
        this.parameterCount = parent.parameterCount;
        this.callDepth = parent.callDepth;
        this.mutableParameterIndexToValue = new TIntObjectHashMap<Object>();
    }

    public void assignParameter(int parameterIndex, Object value) {
        pokeRegister(getParameterStart() + parameterIndex, value);

        String type = SmaliClassUtils.getValueType(value);
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

    public int getCallDepth() {
        return callDepth;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public int getParameterStart() {
        return getRegisterCount() - parameterCount;
    }

    public Object getParameter(int parameterIndex) {
        return peekRegister(getParameterStart() + parameterIndex);
    }

    public Object getMutableParameter(int parameterIndex) {
        MethodContext targetContext;
        if (mutableParameterIndexToValue.containsKey(parameterIndex)) {
            targetContext = this;
        } else {
            targetContext = getAncestorContextWithParameter(parameterIndex);
        }

        Object result = targetContext.mutableParameterIndexToValue.get(parameterIndex);

        return result;
    }

    @Override
    public MethodContext getParent() {
        return (MethodContext) super.getParent();
    }

    public int getPseudoInstructionReturnAddress() {
        return (int) peekRegister(ReturnAddress);
    }

    @Override
    public Object peekRegister(int register) {
        Object[] parts = peekWithTargetContext(register);
        Object value = parts[0];
        MethodContext targetContext = (MethodContext) parts[1];

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

    public void setCallDepth(int callDepth) {
        this.callDepth = callDepth;
    }

    public void setPseudoInstructionReturnAddress(int address) {
        // Pseudo instructions like array-data-payload need return addresses.
        pokeRegister(ReturnAddress, address);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\nparameters: ").append(parameterCount).append(", callDepth: ").append(callDepth).append("\n");
        if (hasRegister(ResultRegister)) {
            sb.append("result: ").append(registerToString(ResultRegister)).append("\n");
        }
        if (hasRegister(ReturnRegister)) {
            sb.append("return: ").append(registerToString(ReturnRegister));
        }

        return sb.toString();
    }

}
