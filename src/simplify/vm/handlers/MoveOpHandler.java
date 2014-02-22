package simplify.vm.handlers;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

import simplify.vm.MethodContext;
import simplify.vm.types.UnknownValue;

public class MoveOpHandler extends OpHandler {

    private static enum MoveType {
        RESULT,
        EXCEPTION,
        REGISTER
    };

    static MoveOpHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
        int toRegister = ((OneRegisterInstruction) instruction).getRegisterA();

        MoveType moveType = null;
        if (opName.contains("-result")) {
            moveType = MoveType.RESULT;
            return new MoveOpHandler(address, opName, childAddress, toRegister, moveType);
        } else if (opName.contains("-exception")) {
            moveType = MoveType.EXCEPTION;
            return new MoveOpHandler(address, opName, childAddress, toRegister, moveType);
        } else {
            int targetRegister = ((TwoRegisterInstruction) instruction).getRegisterB();
            return new MoveOpHandler(address, opName, childAddress, toRegister, targetRegister);
        }
    }

    private final int address;
    private final String opName;
    private final int childAddress;
    private final int toRegister;
    private final MoveType moveType;
    private int targetRegister;

    private MoveOpHandler(int address, String opName, int childAddress, int toRegister, MoveType moveType) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.toRegister = toRegister;
        this.moveType = moveType;
    }

    private MoveOpHandler(int address, String opName, int nextInstructionAddress, int toRegister, int targetRegister) {
        this(address, opName, nextInstructionAddress, toRegister, MoveType.REGISTER);
        this.targetRegister = targetRegister;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        switch (moveType) {
        case EXCEPTION:
            // TODO: implement with try/catch stuff?
            moveException(mctx, toRegister);
            break;
        case RESULT:
            moveResult(mctx, toRegister);
            break;
        case REGISTER:
            moveRegister(mctx, toRegister, targetRegister);
            break;
        }

        return getPossibleChildren();
    }

    private static void moveException(MethodContext mctx, int toRegister) {
        String type = "Ljava/lang/Exception;";
        Object value = new UnknownValue(type);
        mctx.assignRegister(toRegister, value);
    }

    private static void moveResult(MethodContext mctx, int toRegister) {
        Object value = mctx.readResultRegister();
        mctx.assignRegister(toRegister, value);
    }

    private static void moveRegister(MethodContext mctx, int toRegister, int fromRegister) {
        Object value = mctx.readRegister(fromRegister);
        mctx.assignRegister(toRegister, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);
        sb.append(" r").append(toRegister);
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
