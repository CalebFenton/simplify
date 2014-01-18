package refactor.op;

import refactor.exec.MethodExecutionContext;

class UnimplementedOpHandler extends OpHandler {

    private final int index;
    private final boolean canContinue;
    private final boolean canThrow;
    private final String opName;
    private final int destRegister;

    UnimplementedOpHandler(int index, boolean canContinue, boolean canThrow, String opName) {
        this(index, canContinue, canThrow, opName, -1);
    }

    UnimplementedOpHandler(int index, boolean canContinue, boolean canThrow, String opName, int destRegister) {
        this.index = index;
        this.canContinue = canContinue;
        this.canThrow = canThrow;
        this.opName = opName;
        this.destRegister = destRegister;
    }

    @Override
    public int[] execute(MethodExecutionContext mectx) {
        return new int[] { OpHandler.ContinueNextInstruction };
    }

    @Override
    public String toString() {
        return opName + " @" + index;
    }

}
