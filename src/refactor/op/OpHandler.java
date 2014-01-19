package refactor.op;

import refactor.exec.MethodExecutionContext;

public abstract class OpHandler {

    public abstract int[] execute(MethodExecutionContext mectx);

    @Override
    public abstract String toString();

    public abstract int[] getPossibleChildren();

    public abstract int getIndex();
}
