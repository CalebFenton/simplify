package refactor.op;

import refactor.exec.MethodExecutionContext;

public abstract class OpHandler {

    static final int ContinueNextInstruction = -1;

    public abstract int[] execute(MethodExecutionContext mectx);

    @Override
    public abstract String toString();
}
