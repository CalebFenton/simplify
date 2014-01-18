package refactor.op;

import refactor.exec.MethodExecutionContext;

public interface OpHandler {

    static final int ContinueNextInstruction = -1;

    public int[] execute(MethodExecutionContext mectx);

    public String toString();
}
