package refactor.handler;

import refactor.vm.MethodContext;
import refactor.vm.VirtualMachine;

public class ConstOpHandler extends OpHandler {

    private final int index;

    ConstOpHandler(int index) {
        this.index = index;
    }

    @Override
    public int[] execute(MethodContext mectx) {
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
