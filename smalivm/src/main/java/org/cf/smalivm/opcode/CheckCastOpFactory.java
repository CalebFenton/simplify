package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

public class CheckCastOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction21c instr = (Instruction21c) location.getInstruction();
        int targetRegister = instr.getRegisterA();
        TypeReference typeRef = (TypeReference) instr.getReference();
        String className = typeRef.getType();

        return new CheckCastOp(location, child, targetRegister, className, vm);
    }

}
