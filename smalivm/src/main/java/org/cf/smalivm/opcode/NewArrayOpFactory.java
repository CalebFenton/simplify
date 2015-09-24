package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;

public class NewArrayOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        Instruction22c instr = (Instruction22c) instruction;
        int destRegister = instr.getRegisterA();
        int sizeRegister = instr.getRegisterB();

        // [[Lsome_class;
        String arrayType = ReferenceUtil.getReferenceString(instr.getReference());
        // Lsome_class;
        String baseClassName = SmaliClassUtils.getBaseClass(arrayType);
        ClassManager classManager = vm.getClassManager();
        boolean useLocalClass = false;
        if (classManager.isFramework(baseClassName)) {
            // Create arrays of LocalInstance
            useLocalClass = classManager.isSafeFramework(baseClassName);
        } else {
            useLocalClass = classManager.isLocalClass(baseClassName);
        }

        return new NewArrayOp(instruction, child, destRegister, sizeRegister, arrayType, useLocalClass);
    }

}
