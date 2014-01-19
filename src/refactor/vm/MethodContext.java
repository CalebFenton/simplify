package refactor.vm;

public class MethodContext extends VirtualMachineContext {

    private final int parameterCount;
    private final int callDepth;

    MethodContext(int callDepth) {
        this(0, 0, callDepth);
    }

    MethodContext(int registerCount, int parameterCount, int callDepth) {
        super(registerCount);

        this.parameterCount = parameterCount;
        this.callDepth = callDepth;
    }

    MethodContext(MethodContext mectx) {
        super(mectx);

        parameterCount = mectx.parameterCount;
        callDepth = mectx.callDepth;
    }

    public int getCallDepth() {
        return callDepth;
    }

    // sb.append(", call depth: ").append(callDepth).append("\n");

}
