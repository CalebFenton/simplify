package simplify.vm;

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

    public MethodContext(int registerCount, int parameterCount, int callDepth) {
        super(registerCount);

        this.parameterCount = parameterCount;
        this.callDepth = callDepth;
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
        // pseudoInstructionReturnAddress is expected to remain in parent
    }

    public void assignParameter(int parameterIndex, Object value) {
        // TODO: Maintain parameter-over-clone mappings by intintmaps using hashcode. Use them when updating
        // non-immutable object parameters after executing local smali method.
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

    public void incrementCallDepth() {
        callDepth++;
    }

    public Object peekParameter(int parameterIndex) {
        return peekRegister(getParameterStart() + parameterIndex);
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
