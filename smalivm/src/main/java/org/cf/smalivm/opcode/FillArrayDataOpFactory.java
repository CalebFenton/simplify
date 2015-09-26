package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;

public class FillArrayDataOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        BuilderInstruction instruction = (BuilderInstruction) location.getInstruction();
        int address = instruction.getLocation().getCodeAddress();
        int returnAddress = address + instruction.getCodeUnits();
        MethodLocation returnLocation = addressToLocation.get(returnAddress);
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int childAddress = address + branchOffset;
        MethodLocation child = addressToLocation.get(childAddress);
        Instruction31t instr = (Instruction31t) location.getInstruction();
        int register = instr.getRegisterA();

        return new FillArrayDataOp(location, child, returnLocation, register);
    }
}
