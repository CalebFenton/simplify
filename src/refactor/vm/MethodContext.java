package refactor.vm;


public class MethodContext extends VirtualMachineContext {

    public static final int ResultRegister = -1;
    public static final int ReturnRegister = -2;

    private final int parameterCount;
    private final int callDepth;

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

    MethodContext(MethodContext mctx) {
        super(mctx);

        parameterCount = mctx.parameterCount;
        callDepth = mctx.callDepth;
    }

    public int getCallDepth() {
        return callDepth;
    }

    public void setResultRegister(RegisterStore registerStore) {
        setRegister(ResultRegister, registerStore);
    }

    public RegisterStore getResultRegister(int address) {
        RegisterStore result = getRegister(ResultRegister, address);
        setRegister(ResultRegister, null);

        return result;
    }

    public RegisterStore getReturnRegister() {
        // Result register is stored outside of the method being analyzed, so address is irrelevant.
        return getRegister(ResultRegister, 0);
    }

    public void setReturnRegister(RegisterStore registerStore) {
        setRegister(ReturnRegister, registerStore);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());

        sb.append("\nparameters: ").append(parameterCount).append(", callDepth: ").append(callDepth).append("\n");

        RegisterStore resultRegister = peekRegister(ResultRegister);
        if (resultRegister != null) {
            sb.append("result: ").append(resultRegister).append("\n");
        }

        RegisterStore returnRegister = peekRegister(ReturnRegister);
        if (returnRegister != null) {
            sb.append("return: ").append(returnRegister);
        }

        return sb.toString();
    }

    public void setParameter(int paramRegister, RegisterStore registerStore) {
        int parameterStart = getRegisterCount() - parameterCount;

        setRegister(parameterStart + paramRegister, registerStore);
    }

}
