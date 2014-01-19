package refactor.op;

import refactor.exec.MethodExecutionContext;
import refactor.exec.VirtualMachine;

class UnimplementedOpHandler extends OpHandler {

    private final int index;
    private final boolean canContinue;
    private final boolean canThrow;
    private final String opName;
    private final int destRegister;

    UnimplementedOpHandler(int index, String opName, boolean canContinue, boolean canThrow) {
        this(index, opName, canContinue, canThrow, -1);
    }

    UnimplementedOpHandler(int index, String opName, boolean canContinue, boolean canThrow, int destRegister) {
        this.index = index;
        this.opName = opName;
        this.canContinue = canContinue;
        this.canThrow = canThrow;
        this.destRegister = destRegister;
    }

    @Override
    public int[] execute(MethodExecutionContext mectx) {
        if (canContinue) {
            return new int[] { VirtualMachine.ContinueNextInstruction };
        } else {
            return new int[0];
        }
    }

    @Override
    public String toString() {
        return opName + " (unimplmented) @" + index;
    }

    @Override
    public int[] getPossibleChildren() {
        // TODO: implement
        return null;
    }

    @Override
    public int getIndex() {
        return index;
    }

}
