package org.cf.smalivm.opcode;

import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

public class MoveOp extends MethodStateOp {

    private static enum MoveType {
        EXCEPTION,
        REGISTER,
        RESULT
    };

    private static void moveException(MethodState mctx, int toRegister) {
        String type = "Ljava/lang/Exception;";
        Object value = new UnknownValue(type);
        mctx.assignRegister(toRegister, value);
    }

    private static void moveRegister(MethodState mctx, int toRegister, int fromRegister) {
        Object value = mctx.readRegister(fromRegister);
        mctx.assignRegister(toRegister, value);
    }
    private static void moveResult(MethodState mctx, int toRegister) {
        Object value = mctx.readResultRegister();
        mctx.assignRegister(toRegister, value);
    }
    static MoveOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
        int toRegister = ((OneRegisterInstruction) instruction).getRegisterA();

        MoveType moveType = null;
        if (opName.contains("-result")) {
            moveType = MoveType.RESULT;
            return new MoveOp(address, opName, childAddress, toRegister, moveType);
        } else if (opName.contains("-exception")) {
            moveType = MoveType.EXCEPTION;
            return new MoveOp(address, opName, childAddress, toRegister, moveType);
        } else {
            int targetRegister = ((TwoRegisterInstruction) instruction).getRegisterB();
            return new MoveOp(address, opName, childAddress, toRegister, targetRegister);
        }
    }

    private final MoveType moveType;

    private int targetRegister;

    private final int toRegister;

    private MoveOp(int address, String opName, int nextInstructionAddress, int toRegister, int targetRegister) {
        this(address, opName, nextInstructionAddress, toRegister, MoveType.REGISTER);
        this.targetRegister = targetRegister;
    }

    private MoveOp(int address, String opName, int childAddress, int toRegister, MoveType moveType) {
        super(address, opName, childAddress);
        this.toRegister = toRegister;
        this.moveType = moveType;
    }

    @Override
    public int[] execute(MethodState mctx) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(toRegister);
        if (moveType == MoveType.REGISTER) {
            sb.append(", r").append(targetRegister);
        }

        return sb.toString();
    }

}
