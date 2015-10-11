package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderOffsetInstruction;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.MethodLocation;

public class GotoOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        BuilderInstruction instruction = (BuilderInstruction) location.getInstruction();
        Label target = ((BuilderOffsetInstruction) instruction).getTarget();
        int targetAddress = target.getCodeAddress();
        MethodLocation child = addressToLocation.get(targetAddress);

        return new GotoOp(location, child);
    }
}
