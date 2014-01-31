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
        removeRegister(ResultRegister);

        return result;
    }

    public Object readReturnRegister() {
        return peekRegister(ReturnRegister);
    }

    public void assignReturnRegister(Object value) {
        pokeRegister(ReturnRegister, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());

        sb.append("\nparameters: ").append(parameterCount).append(", callDepth: ").append(callDepth).append("\n");

        Object resultValue = peekRegister(ResultRegister);
        if (resultValue != null) {
            sb.append("result: ").append(registerToString(ResultRegister)).append("\n");
        }

        Object returnValue = peekRegister(ReturnRegister);
        if (returnValue != null) {
            sb.append("return: ").append(registerToString(ReturnRegister));
        }

        return sb.toString();
    }

    public void setParameter(int paramRegister, Object value) {
        pokeRegister(getParameterStart() + paramRegister, value);
    }

    public int getParameterStart() {
        return getRegisterCount() - parameterCount;
    }

    public void incrementCallDepth() {
        callDepth++;
    }

}
