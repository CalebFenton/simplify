package refactor.handler;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

import refactor.vm.MethodContext;
import refactor.vm.RegisterStore;
import simplify.exec.UnknownValue;

public class MoveOpHandler extends OpHandler {

    private static enum MoveType {
        RESULT,
        EXCEPTION,
        REGISTER
    };

    static MoveOpHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
        int destRegister = ((OneRegisterInstruction) instruction).getRegisterA();

        MoveType moveType = null;
        if (opName.contains("-result")) {
            moveType = MoveType.RESULT;
            return new MoveOpHandler(address, opName, childAddress, destRegister, moveType);
        } else if (opName.contains("-exception")) {
            moveType = MoveType.EXCEPTION;
            return new MoveOpHandler(address, opName, childAddress, destRegister, moveType);
        } else {
            int targetRegister = ((TwoRegisterInstruction) instruction).getRegisterB();
            return new MoveOpHandler(address, opName, childAddress, destRegister, targetRegister);
        }
    }

    private final int address;
    private final String opName;
    private final int childAddress;
    private final int destRegister;
    private final MoveType moveType;
    private int targetRegister;

    private MoveOpHandler(int address, String opName, int childAddress, int destRegister, MoveType moveType) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.destRegister = destRegister;
        this.moveType = moveType;
    }

    private MoveOpHandler(int address, String opName, int nextInstructionAddress, int destRegister, int targetRegister) {
        this(address, opName, nextInstructionAddress, destRegister, MoveType.REGISTER);
        this.targetRegister = targetRegister;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        RegisterStore target = null;
        String type = null;
        Object value = null;
        switch (moveType) {
        case EXCEPTION:
            // TODO: implement with try/catch stuff?
            type = "Ljava/lang/Exception;";
            value = new UnknownValue();
            mctx.setRegister(destRegister, type, value, address);
            break;
        case RESULT:
            target = mctx.getResultRegister(address);
            mctx.setResultRegister(target);
            break;
        case REGISTER:
            target = mctx.getRegister(targetRegister, address);
            type = target.getType();
            value = target.getValue();
            mctx.setRegister(destRegister, type, value, address);
            break;
        }

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);
        sb.append(" r").append(destRegister);
        if (moveType == MoveType.REGISTER) {
            sb.append(", r").append(targetRegister);
        }

        return sb.toString();
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { childAddress };
    }

    @Override
    public int getAddress() {
        return address;
    }

}
