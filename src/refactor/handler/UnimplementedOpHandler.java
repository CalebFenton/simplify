package refactor.handler;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;

import refactor.vm.MethodContext;
import refactor.vm.VirtualMachine;

class UnimplementedOpHandler extends OpHandler {

    static UnimplementedOpHandler create(Instruction instruction, int index) {
        Opcode op = instruction.getOpcode();
        UnimplementedOpHandler result = null;
        if (op.setsRegister()) {
            // Can assume it has at least one register
            int destRegister = ((OneRegisterInstruction) instruction).getRegisterA();
            result = new UnimplementedOpHandler(index, op.name, op.canContinue(), op.canThrow(), destRegister);
        } else {
            result = new UnimplementedOpHandler(index, op.name, op.canContinue(), op.canThrow());
        }

        return result;
    }

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
    public int[] execute(MethodContext mectx) {
        if (canContinue) {
            return new int[] { VirtualMachine.ContinueNextInstruction };
        } else {
            return new int[0];
        }
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int[] getPossibleChildren() {
        // TODO: implement
        return null;
    }

    @Override
    public String toString() {
        return opName + " (unimplmented) @" + index;
    }

}
