package simplify.handlers;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;

import simplify.vm.MethodContext;
import simplify.vm.RegisterStore;
import simplify.vm.UnknownValue;

class UnimplementedOpHandler extends OpHandler {

    static UnimplementedOpHandler create(Instruction instruction, int address) {
        UnimplementedOpHandler result = null;
        Opcode op = instruction.getOpcode();
        int childAddress = address + instruction.getCodeUnits();
        if (op.setsRegister()) {
            // Can assume it has at least one register
            int destRegister = ((OneRegisterInstruction) instruction).getRegisterA();
            result = new UnimplementedOpHandler(address, op.name, childAddress, op.canContinue(), op.canThrow(),
                            op.setsResult(), destRegister);
        } else {
            result = new UnimplementedOpHandler(address, op.name, childAddress, op.canContinue(), op.canThrow(),
                            op.setsResult());
        }

        return result;
    }

    private final int address;
    private final String opName;
    private final int childAddress;
    private final boolean canContinue;
    private final boolean canThrow;
    private final boolean setsResult;
    private final int destRegister;

    UnimplementedOpHandler(int address, String opName, int childAddress, boolean canContinue, boolean canThrow,
                    boolean setsResult) {
        this(address, opName, childAddress, canContinue, canThrow, setsResult, -1);
    }

    UnimplementedOpHandler(int address, String opName, int childAddress, boolean canContinue, boolean canThrow,
                    boolean setsResult, int destRegister) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.canContinue = canContinue;
        this.canThrow = canThrow;
        this.setsResult = setsResult;
        this.destRegister = destRegister;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        if (setsResult) {
            mctx.setResultRegister(new RegisterStore("?", new UnknownValue()));
        }

        if (destRegister >= 0) {
            mctx.setRegister(destRegister, "?", new UnknownValue(), address);
        }

        if (canContinue) {
            return new int[] { childAddress };
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
        StringBuilder sb = new StringBuilder(opName);

        if (destRegister >= 0) {
            sb.append(" r").append(destRegister);
        }

        sb.append(" (unimplmented) @").append(address);

        return sb.toString();
    }

}
