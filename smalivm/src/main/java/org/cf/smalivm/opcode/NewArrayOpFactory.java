package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewArrayOpFactory implements OpFactory {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(NewArrayOpFactory.class.getSimpleName());

    @Override
    public NewArrayOp create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction22c instr = (Instruction22c) location.getInstruction();
        int destRegister = instr.getRegisterA();
        int sizeRegister = instr.getRegisterB();
        String arrayType = ReferenceUtil.getReferenceString(instr.getReference());

        return new NewArrayOp(location, child, destRegister, sizeRegister, arrayType, vm.getClassLoader());
    }

}
