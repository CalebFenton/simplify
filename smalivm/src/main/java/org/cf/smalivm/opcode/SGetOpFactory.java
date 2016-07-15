package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.FieldReference;

public class SGetOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction21c instr = (Instruction21c) location.getInstruction();
        int destRegister = instr.getRegisterA();
        FieldReference fieldReference = (FieldReference) instr.getReference();
        VirtualClass fieldClass = vm.getClassManager().getVirtualClass(fieldReference.getDefiningClass());
        VirtualField actualField = fieldClass.getField(fieldReference.getName());

        return new SGetOp(location, child, destRegister, fieldReference, actualField, vm);
    }

}
