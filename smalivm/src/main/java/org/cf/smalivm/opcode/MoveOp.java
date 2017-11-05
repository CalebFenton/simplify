package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;

public class MoveOp extends MethodStateOp {

    private final MoveType moveType;
    private final int toRegister;
    private int targetRegister;

    MoveOp(MethodLocation location, MethodLocation child, int toRegister, int targetRegister) {
        this(location, child, toRegister, MoveType.REGISTER);
        this.targetRegister = targetRegister;
    }

    MoveOp(MethodLocation location, MethodLocation child, int toRegister, MoveType moveType) {
        super(location, child);
        this.toRegister = toRegister;
        this.moveType = moveType;
    }

    private static void moveException(MethodState mState, int toRegister) {
        HeapItem exception = mState.peekExceptionRegister();
        mState.assignRegister(toRegister, exception);
    }

    private static void moveRegister(MethodState mState, int toRegister, int fromRegister) {
        HeapItem item = mState.readRegister(fromRegister);
        mState.assignRegister(toRegister, item);
    }

    private static void moveResult(MethodState mState, int toRegister) {
        HeapItem item = mState.readResultRegister();
        mState.assignRegister(toRegister, item);
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
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

    public MoveType getMoveType() {
        return moveType;
    }

    public int getToRegister() {
        return toRegister;
    }

    public int getTargetRegister() {
        return targetRegister;
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

    enum MoveType {
        EXCEPTION, REGISTER, RESULT
    }

}
