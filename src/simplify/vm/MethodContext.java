package simplify.vm;

public class MethodContext extends VirtualMachineContext {

    public static final int ResultRegister = -1;
    public static final int ReturnRegister = -2;

    private int parameterCount;
    private int callDepth;

    MethodContext(int parameterCount) {
        this(parameterCount, parameterCount, 0);
    }

    MethodContext(int parameterCount, int callDepth) {
        this(parameterCount, parameterCount, callDepth);
    }

    public MethodContext(int registerCount, int parameterCount, int callDepth) {
        super(registerCount);

        this.parameterCount = parameterCount;
        this.callDepth = callDepth;
    }

    MethodContext(MethodContext parent) {
        super(parent);

        parameterCount = parent.parameterCount;
        callDepth = parent.callDepth;
    }

    public int getCallDepth() {
        return callDepth;
    }

    public void assignResultRegister(Object value) {
        assignRegister(ResultRegister, value);
    }

    public Object readResultRegister() {
        Object result = readRegister(ResultRegister);
        // removeRegister(ResultRegister);

        return result;
    }

    public Object readReturnRegister() {
        return peekRegister(ReturnRegister);
    }

    public void assignReturnRegister(Object value) {
        pokeRegister(ReturnRegister, value);
    }

    public Object peekParameter(int parameterIndex) {
        return peekRegister(getParameterStart() + parameterIndex);
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

    public void assignParameter(int parameterIndex, Object value) {
        // TODO: Maintain parameter-over-clone mappings by intintmaps using hashcode. Use them when updating
        // non-immutable object parameters after executing local smali method.
        pokeRegister(getParameterStart() + parameterIndex, value);
    }

    protected int getParameterStart() {
        return getRegisterCount() - parameterCount;
    }

    public void incrementCallDepth() {
        callDepth++;
    }

}
