package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.opcode.MoveOp.MoveType;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

public class MoveOpFactory implements OpFactory {

    private static MoveType getMoveType(String opName) {
        if (opName.contains("-result")) {
            return MoveType.RESULT;
        } else if (opName.contains("-exception")) {
            return MoveType.EXCEPTION;
        } else {
            return MoveType.REGISTER;
        }
    }

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        String opName = instruction.getOpcode().name;
        int toRegister = ((OneRegisterInstruction) instruction).getRegisterA();
        MoveType moveType = getMoveType(opName);
        switch (moveType) {
        case RESULT:
        case EXCEPTION:
            return new MoveOp(instruction, child, toRegister, moveType);
        case REGISTER:
            int targetRegister = ((TwoRegisterInstruction) instruction).getRegisterB();
            return new MoveOp(instruction, child, toRegister, targetRegister);
        default:
            return null;
        }
    }

}
