package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.type.VirtualType;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.TypeReference;

public class InstanceOfOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction22c instr = (Instruction22c) location.getInstruction();
        int destRegister = instr.getRegisterA();
        int arg1Register = instr.getRegisterB();
        TypeReference typeReference = (TypeReference) instr.getReference();
        VirtualType referenceType = vm.getClassManager().getVirtualType(typeReference);

        return new InstanceOfOp(location, child, destRegister, arg1Register, referenceType, vm);
    }

}
