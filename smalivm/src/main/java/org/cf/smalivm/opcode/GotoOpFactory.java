package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;

public class GotoOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        BuilderInstruction instruction = (BuilderInstruction) location.getInstruction();
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = instruction.getLocation().getCodeAddress() + branchOffset;
        MethodLocation child = addressToLocation.get(targetAddress);

        return new GotoOp(location, child);
    }
}
