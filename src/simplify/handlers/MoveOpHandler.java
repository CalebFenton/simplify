package simplify.handlers;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

import simplify.SmaliClassUtils;
import simplify.vm.MethodContext;
import simplify.vm.RegisterStore;
import simplify.vm.UnknownValue;

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
        switch (moveType) {
        case EXCEPTION:
            // TODO: implement with try/catch stuff?
            moveException(mctx, address, destRegister);
            break;
        case RESULT:
            moveResult(mctx, address, destRegister);
            break;
        case REGISTER:
            moveRegister(mctx, address, destRegister, targetRegister);
            break;
        }

        return getPossibleChildren();
    }

    private static void moveException(MethodContext mctx, int address, int destRegister) {
        String type = "Ljava/lang/Exception;";
        Object value = new UnknownValue();
        mctx.setRegister(destRegister, type, value, address);
    }

    private static void moveResult(MethodContext mctx, int address, int destRegister) {
        RegisterStore target = mctx.getResultRegister(address);
        String type = target.getType();
        Object value = target.getValue();

        // Handle edge case when result register store was an argument or an instance of the called method.
        if (!SmaliClassUtils.isPrimitiveType(type)) {
            for (RegisterStore rs : mctx.getRegisterToStore().getValues()) {
                if (value == rs.getValue()) {
                    System.out.println("Relinking result register store.");
                    rs.setValue(value);
                    mctx.setRegister(destRegister, rs, address);
                    return;
                }
            }
        }

        mctx.setRegister(destRegister, type, value, address);
    }

    private static void moveRegister(MethodContext mctx, int address, int destRegister, int targetRegister) {
        RegisterStore target = mctx.getRegister(targetRegister, address);
        mctx.setRegister(destRegister, target, address);
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
