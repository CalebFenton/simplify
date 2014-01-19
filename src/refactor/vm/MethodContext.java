package refactor.vm;

public class MethodContext extends VirtualMachineContext {

    private final int parameterCount;

    MethodContext(int callDepth) {
        this(callDepth, 0, 0);
    }

    MethodContext(int callDepth, int registerCount, int parameterCount) {
        super(callDepth, registerCount);

        this.parameterCount = parameterCount;
    }

    MethodContext(MethodContext mectx) {
        super(mectx);

        parameterCount = mectx.parameterCount;
    }

}
