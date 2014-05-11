package simplify.vm;

import java.util.HashMap;

import simplify.SmaliClassUtils;
import util.SparseArray;

public class MethodContext extends VirtualMachineContext {

    public static final int ResultRegister = -1;
    public static final int ReturnAddress = -3;
    public static final int ReturnRegister = -2;

    public static SparseArray<Object> buildRegisterState(Object... params) {
        SparseArray<Object> result = new SparseArray<Object>(params.length / 2);
        for (int i = 0; i < params.length; i += 2) {
            result.append((int) params[i], params[i + 1]);
        }

        return result;
    }

    public static MethodContext buildFromRegisterState(SparseArray<Object> registers) {
        MethodContext ctx = new MethodContext(registers.size());
        for (int i = 0; i < registers.size(); i++) {
            int register = registers.keyAt(i);
            Object value = registers.get(register);

            ctx.assignRegister(register, value);
        }

        return ctx;
    }

    private int callDepth;
    private int parameterCount;
    private final HashMap<Object, Object> mutatedToOriginalParameter;

    public MethodContext(int registerCount, int parameterCount, int callDepth) {
        super(registerCount);

        this.parameterCount = parameterCount;
        this.callDepth = callDepth;

        mutatedToOriginalParameter = new HashMap<Object, Object>();
    }

    MethodContext(int parameterCount) {
        this(parameterCount, parameterCount, 0);
    }

    MethodContext(int parameterCount, int callDepth) {
        this(parameterCount, parameterCount, callDepth);
    }

    MethodContext(MethodContext parent) {
        super(parent);

        parameterCount = parent.parameterCount;
        callDepth = parent.callDepth;
        mutatedToOriginalParameter = parent.mutatedToOriginalParameter;
        // pseudoInstructionReturnAddress is expected to remain in parent
    }

    public void assignParameter(int parameterIndex, Object value) {
        // The parameter object is never modified directly because each instruction gets a clone. To propagate changes
        // in mutable object parameters back to the caller, it's necessary to maintain a mapping from the parameter to
        // any mutations.
        String smaliClass = SmaliClassUtils.javaClassToSmali(value.getClass().getName());
        if (!SmaliClassUtils.isImmutableClass(smaliClass)) {
            mutatedToOriginalParameter.put(value, value);
        }

        pokeRegister(getParameterStart() + parameterIndex, value);
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

    @Override
    public MethodContext getParent() {
        return (MethodContext) super.getParent();
    }

    public int getPseudoInstructionReturnAddress() {
        return (int) peekRegister(ReturnAddress);
    }

    public void setCallDepth(int callDepth) {
        this.callDepth = callDepth;
    }

    public Object peekParameter(int parameterIndex) {
        return peekRegister(getParameterStart() + parameterIndex);
    }

    @Override
    Object cloneRegisterValue(Object value) {
        Object result = super.cloneRegisterValue(value);
        Object original = mutatedToOriginalParameter.get(value);
        if (original != null) {
            mutatedToOriginalParameter.put(result, original);
        }

        return result;
    }

    public Object readResultRegister() {
        Object result = readRegister(ResultRegister);
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
        sb.append("\nparameters: ").append(parameterCount).append(", callDepth: ").append(callDepth).append("\n");
        if (hasRegister(ResultRegister)) {
            sb.append("result: ").append(registerToString(ResultRegister)).append("\n");
        }
        if (hasRegister(ReturnRegister)) {
            sb.append("return: ").append(registerToString(ReturnRegister));
        }

        return sb.toString();
    }

    protected int getParameterStart() {
        return getRegisterCount() - parameterCount;
    }

}
