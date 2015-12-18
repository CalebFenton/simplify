package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.VirtualMachine;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewArrayOpFactory implements OpFactory {

    private static final Logger log = LoggerFactory.getLogger(NewArrayOpFactory.class.getSimpleName());

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
        // Create arrays of LocalInstance
        boolean useLocalClass = false;
        if (MethodReflector.isSafe(baseClassName)) {
            useLocalClass = false;
        } else if (classManager.isFrameworkClass(baseClassName)) {
            if (classManager.isSafeFrameworkClass(baseClassName)) {
                useLocalClass = true;
            } else {
                log.warn("{} is framework but not safe; will treat as if it doesn't exist.", baseClassName);
            }
        } else {
            useLocalClass = classManager.isLocalClass(baseClassName);
        }

        return new NewArrayOp(location, child, destRegister, sizeRegister, arrayType, useLocalClass);
    }

}
