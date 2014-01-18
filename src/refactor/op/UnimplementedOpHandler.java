package refactor.op;

import refactor.exec.MethodExecutionContext;

class UnimplementedOpHandler extends OpHandler {

    private final int index;
    private final int destRegister;
    private final boolean canContinue;
    private final boolean canThrow;

    UnimplementedOpHandler(int index, boolean canContinue, boolean canThrow) {
        this(index, canContinue, canThrow, -1);
    }

    UnimplementedOpHandler(int index, boolean canContinue, boolean canThrow, int destRegister) {
        this.index = index;
        this.canContinue = canContinue;
        this.canThrow = canThrow;
        this.destRegister = destRegister;
    }

    @Override
    public int[] execute(MethodExecutionContext mectx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

}
