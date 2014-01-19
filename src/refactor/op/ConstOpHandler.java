package refactor.op;

import refactor.exec.MethodExecutionContext;
import refactor.exec.VirtualMachine;

public class ConstOpHandler extends OpHandler {

    private final int index;

    ConstOpHandler(int index) {
        this.index = index;
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

    @Override
    public int[] getPossibleChildren() {
        return new int[] { VirtualMachine.ContinueNextInstruction };
    }

    @Override
    public int getIndex() {
        return index;
    }

}
