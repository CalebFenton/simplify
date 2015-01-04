package org.cf.smalivm.opcode;

import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewArrayOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(NewArrayOp.class.getSimpleName());

    static NewArrayOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction22c instr = (Instruction22c) instruction;
        int destRegister = instr.getRegisterA();
        int dimensionRegister = instr.getRegisterB();

        // [[Lsome_class;
        String arrayType = ReferenceUtil.getReferenceString(instr.getReference());
        String baseClassName = arrayType.replace("[", "");
        SmaliClassManager classManager = vm.getClassManager();
        boolean useLocalClass;
        if (classManager.isFramework(baseClassName)) {
            useLocalClass = classManager.isSafeFramework(baseClassName);
        } else {
            useLocalClass = classManager.isLocalClass(baseClassName);
        }

        return new NewArrayOp(address, opName, childAddress, destRegister, dimensionRegister, arrayType, useLocalClass);
    }

    private final int destRegister;
    private final int dimensionRegister;
    private final boolean useLocalClass;
    private final String arrayType;

    private NewArrayOp(int address, String opName, int childAddress, int destRegister, int dimensionRegister,
                    String arrayType, boolean useLocalClass) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.dimensionRegister = dimensionRegister;
        this.arrayType = arrayType;
        this.useLocalClass = useLocalClass;
    }

    @Override
    public int[] execute(MethodState mState) {
        HeapItem dimensionItem = mState.readRegister(dimensionRegister);
        HeapItem instanceItem;
        if (dimensionItem.isUnknown()) {
            instanceItem = HeapItem.newUnknown(arrayType);
        } else {
            int dimension = dimensionItem.getIntegerValue();
            try {
                // Dalvik does not statically initialize classes because of new-array
                Object instance = Utils.getArrayInstanceFromSmaliTypeReference(arrayType, dimension, useLocalClass);
                instanceItem = new HeapItem(instance, arrayType);
            } catch (ClassNotFoundException e) {
                if (log.isErrorEnabled()) {
                    log.error("Couldn't find class: " + arrayType + " @" + toString(), e);
                }
                instanceItem = HeapItem.newUnknown(arrayType);
            }
        }
        mState.assignRegister(destRegister, instanceItem);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(dimensionRegister).append(", ").append(arrayType);

        return sb.toString();
    }
}
