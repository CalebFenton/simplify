package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;

public class NewArrayOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        MethodLocation child = Utils.getNextLocation(location, addressToLocation);
        Instruction22c instr = (Instruction22c) location.getInstruction();
        int destRegister = instr.getRegisterA();
        int sizeRegister = instr.getRegisterB();

        // [[Lsome_class;
        String arrayType = ReferenceUtil.getReferenceString(instr.getReference());
        // Lsome_class;
        String baseClassName = SmaliClassUtils.getBaseClass(arrayType);
        ClassManager classManager = vm.getClassManager();
        boolean useLocalClass = false;
        if (classManager.isFrameworkClass(baseClassName)) {
            // Create arrays of LocalInstance
            useLocalClass = classManager.isSafeFrameworkClass(baseClassName);
        } else {
            useLocalClass = classManager.isLocalClass(baseClassName);
        }

        return new NewArrayOp(location, child, destRegister, sizeRegister, arrayType, useLocalClass);
    }

}
