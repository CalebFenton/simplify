package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.opcode.MoveOp.MoveType;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
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
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        BuilderInstruction instruction = (BuilderInstruction) location.getInstruction();
        String opName = instruction.getOpcode().name;
        int toRegister = ((OneRegisterInstruction) instruction).getRegisterA();
        MoveType moveType = getMoveType(opName);
        switch (moveType) {
        case RESULT:
        case EXCEPTION:
            return new MoveOp(location, child, toRegister, moveType);
        case REGISTER:
            int targetRegister = ((TwoRegisterInstruction) instruction).getRegisterB();
            return new MoveOp(location, child, toRegister, targetRegister);
        default:
            return null;
        }
    }

}
