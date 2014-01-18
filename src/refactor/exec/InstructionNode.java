package refactor.exec;

import refactor.op.OpHandler;

public class InstructionNode {

    private final OpHandler handler;

    private MethodExecutionContext mectx;

    InstructionNode(OpHandler handler) {
        this.handler = handler;
    }

    public int[] execute() {
        return handler.execute(mectx);
    }

    protected void setContext(MethodExecutionContext mectx) {
        this.mectx = mectx;
    }

    public MethodExecutionContext getContext() {
        return mectx;
    }

}
