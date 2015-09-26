package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;

public class SwitchOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        int address = location.getCodeAddress();
        int branchOffset = ((OffsetInstruction) location.getInstruction()).getCodeOffset();
        int targetAddress = address + branchOffset;
        MethodLocation target = addressToLocation.get(targetAddress);
        Instruction31t instr = (Instruction31t) location.getInstruction();
        int register = instr.getRegisterA();

        return new SwitchOp(location, child, target, register);
    }

}
