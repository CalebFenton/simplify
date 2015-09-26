package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.util.ReferenceUtil;

public class IGetOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction22c instr = (Instruction22c) location.getInstruction();
        int destRegister = instr.getRegisterA();
        int instanceRegister = instr.getRegisterB();
        FieldReference reference = (FieldReference) instr.getReference();
        String fieldDescriptor = ReferenceUtil.getFieldDescriptor(reference);

        return new IGetOp(location, child, destRegister, instanceRegister, fieldDescriptor, vm);
    }

}
