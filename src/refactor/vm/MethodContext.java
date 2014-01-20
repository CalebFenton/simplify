package refactor.vm;

public class MethodContext extends VirtualMachineContext {

    private final int parameterCount;
    private final int callDepth;

    private RegisterStore resultRegister;
    private RegisterStore returnRegister;

    MethodContext(int registerCount, int parameterCount) {
        this(registerCount, parameterCount, 0);
    }

    MethodContext(int registerCount, int parameterCount, int callDepth) {
        super(registerCount);

        this.parameterCount = parameterCount;
        this.callDepth = callDepth;
    }

    MethodContext(MethodContext mctx) {
        super(mctx);

        parameterCount = mctx.parameterCount;
        callDepth = mctx.callDepth;
        resultRegister = mctx.resultRegister;
        returnRegister = mctx.returnRegister;
    }

    public int getCallDepth() {
        return callDepth;
    }

    public void setResultRegister(RegisterStore registerStore) {
        resultRegister = registerStore;
    }

    public RegisterStore getResultRegister() {
        RegisterStore result = resultRegister;
        resultRegister = null;
        return result;
    }

    public RegisterStore getReturnRegister() {
        return returnRegister;
    }

    public void setReturnRegister(RegisterStore registerStore) {
        returnRegister = registerStore;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());

        sb.append("\nparameters: ").append(parameterCount).append(", callDepth: ").append(callDepth).append("\n");

        if (resultRegister != null) {
            sb.append("result: ").append(resultRegister).append("\n");
        }

        if (returnRegister != null) {
            sb.append("return: ").append(returnRegister);
        }

        return sb.toString();
    }

    public void setParameter(int paramRegister, RegisterStore registerStore) {
        int parameterStart = this.getRegisterCount() - parameterCount;

        setRegister(parameterStart + paramRegister, registerStore);
    }

}
