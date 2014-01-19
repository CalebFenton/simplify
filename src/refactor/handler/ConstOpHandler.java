package refactor.handler;

import org.jf.dexlib2.iface.instruction.Instruction;

import refactor.vm.MethodContext;
import refactor.vm.VirtualMachine;

public class ConstOpHandler extends OpHandler {

    static ConstOpHandler create(Instruction instruction, int index) {
        ConstOpHandler result = null;

        return result;
    }

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
    public int getIndex() {
        return index;
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { VirtualMachine.ContinueNextInstruction };
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

}
