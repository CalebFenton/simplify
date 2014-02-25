package simplify.vm.handlers;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;

import simplify.vm.MethodContext;
import simplify.vm.types.UnknownValue;

class UnimplementedOpHandler extends OpHandler {

    static UnimplementedOpHandler create(Instruction instruction, int address) {
        UnimplementedOpHandler result = null;

        int childAddress = address + instruction.getCodeUnits();
        Opcode op = instruction.getOpcode();

        if (instruction instanceof OneRegisterInstruction) {
            OneRegisterInstruction instr = (OneRegisterInstruction) instruction;
            int registerA = instr.getRegisterA();

            result = new UnimplementedOpHandler(address, op.name, childAddress, op.canContinue(), op.canThrow(),
                            op.setsResult(), op.setsRegister(), registerA);
        } else {
            result = new UnimplementedOpHandler(address, op.name, childAddress, op.canContinue(), op.canThrow(),
                            op.setsResult());
        }

        return result;
    }

    private final boolean canContinue;
    private final boolean canThrow;
    private final boolean setsResult;
    private final boolean setsRegister;
    private final int registerA;

    UnimplementedOpHandler(int address, String opName, int childAddress, boolean canContinue, boolean canThrow,
                    boolean setsResult) {
        this(address, opName, childAddress, canContinue, canThrow, setsResult, false, -1);
    }

    UnimplementedOpHandler(int address, String opName, int childAddress, boolean canContinue, boolean canThrow,
                    boolean setsResult, boolean setsRegister, int registerA) {
        super(address, opName, canContinue ? childAddress : 0);

        this.canContinue = canContinue;
        this.canThrow = canThrow;
        this.setsResult = setsResult;
        this.setsRegister = setsRegister;
        this.registerA = registerA;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        if (setsResult) {
            mctx.assignResultRegister(new UnknownValue("?"));
        }

        if (registerA >= 0) {
            if (setsRegister) {
                mctx.assignRegister(registerA, new UnknownValue("?"));
            } else {
                mctx.readRegister(registerA);
            }
        }

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

        if (registerA >= 0) {
            sb.append(" r").append(registerA);
        }

        sb.append(" (unimplmented)");

        return sb.toString();
    }

}
