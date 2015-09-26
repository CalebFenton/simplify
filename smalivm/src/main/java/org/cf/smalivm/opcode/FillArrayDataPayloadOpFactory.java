package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import java.util.List;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.ArrayPayload;

public class FillArrayDataPayloadOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        ArrayPayload instr = (ArrayPayload) location.getInstruction();
        int elementWidth = instr.getElementWidth();
        List<Number> arrayElements = instr.getArrayElements();

        return new FillArrayDataPayloadOp(location, elementWidth, arrayElements);
    }

}
