package refactor.handler;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;

import refactor.vm.MethodContext;
import refactor.vm.RegisterStore;
import simplify.exec.UnknownValue;

class UnimplementedOpHandler extends OpHandler {

    static UnimplementedOpHandler create(Instruction instruction, int address) {
        UnimplementedOpHandler result = null;
        Opcode op = instruction.getOpcode();
        int nextInstructionAddress = address + instruction.getCodeUnits();
        if (op.setsRegister()) {
            // Can assume it has at least one register
            int destRegister = ((OneRegisterInstruction) instruction).getRegisterA();
            result = new UnimplementedOpHandler(address, op.name, nextInstructionAddress, op.canContinue(),
                            op.canThrow(), op.setsResult(), destRegister);
        } else {
            result = new UnimplementedOpHandler(address, op.name, nextInstructionAddress, op.canContinue(),
                            op.canThrow(), op.setsResult());
        }

        return result;
    }

    private final int address;
    private final String opName;
    private final int nextInstructionAddress;
    private final boolean canContinue;
    private final boolean canThrow;
    private final boolean setsResult;
    private final int destRegister;

    UnimplementedOpHandler(int address, String opName, int nextInstructionAddress, boolean canContinue,
                    boolean canThrow, boolean setsResult) {
        this(address, opName, nextInstructionAddress, canContinue, canThrow, setsResult, -1);
    }

    UnimplementedOpHandler(int address, String opName, int nextInstructionAddress, boolean canContinue,
                    boolean canThrow, boolean setsResult, int destRegister) {
        this.address = address;
        this.opName = opName;
        this.nextInstructionAddress = nextInstructionAddress;
        this.canContinue = canContinue;
        this.canThrow = canThrow;
        this.setsResult = setsResult;
        this.destRegister = destRegister;
    }

    @Override
    public int[] execute(MethodContext mectx) {
        if (setsResult) {
            mectx.setResultRegister(new RegisterStore("?", new UnknownValue()));
        }

        if (destRegister >= 0) {
            mectx.setRegister(destRegister, "?", new UnknownValue(), address);
        }

        if (canContinue) {
            return new int[] { nextInstructionAddress };
        } else {
            return new int[0];
        }
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public int[] getPossibleChildren() {
        // TODO: implement
        return null;
    }

    @Override
    public String toString() {
        return opName + " (unimplmented) @" + address;
    }

}
