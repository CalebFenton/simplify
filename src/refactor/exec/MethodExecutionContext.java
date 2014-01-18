package refactor.exec;

public class MethodExecutionContext extends ExecutionContext {

    private final int parameterCount;

    MethodExecutionContext(int callDepth) {
        this(callDepth, 0, 0);
    }

    MethodExecutionContext(int callDepth, int registerCount, int parameterCount) {
        super(callDepth, registerCount);

        this.parameterCount = parameterCount;
    }

    MethodExecutionContext(MethodExecutionContext mectx) {
        super(mectx);

        parameterCount = mectx.parameterCount;
    }

}
