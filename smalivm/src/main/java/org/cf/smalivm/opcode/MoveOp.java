package org.cf.smalivm.opcode;

import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

public class MoveOp extends MethodStateOp {

    private static enum MoveType {
        EXCEPTION, REGISTER, RESULT
    };

    private static void moveException(MethodState mState, int toRegister) {
        String type = "Ljava/lang/Exception;";
        mState.assignRegister(toRegister, HeapItem.newUnknown(type));
    }

    private static void moveRegister(MethodState mState, int toRegister, int fromRegister) {
        HeapItem item = mState.readRegister(fromRegister);
        mState.assignRegister(toRegister, item);
    }

    private static void moveResult(MethodState mState, int toRegister) {
        HeapItem item = mState.readResultRegister();
        mState.assignRegister(toRegister, item);
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
    public void execute(MethodState mState) {
        switch (moveType) {
        case EXCEPTION:
            moveException(mState, toRegister);
            break;
        case RESULT:
            moveResult(mState, toRegister);
            break;
        case REGISTER:
            moveRegister(mState, toRegister, targetRegister);
            break;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(toRegister);
        if (MoveType.REGISTER == moveType) {
            sb.append(", r").append(targetRegister);
        }

        return sb.toString();
    }

}
