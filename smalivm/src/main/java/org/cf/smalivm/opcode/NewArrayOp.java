package org.cf.smalivm.opcode;

import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
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
        String typeReference = ReferenceUtil.getReferenceString(instr.getReference());
        String baseClassName = typeReference.replace("[", "");
        SmaliClassManager classManager = vm.getClassManager();
        boolean useLocalClass;
        if (classManager.isFramework(baseClassName)) {
            useLocalClass = classManager.isSafeFramework(baseClassName);
        } else {
            useLocalClass = classManager.isLocalClass(baseClassName);
        }

        return new NewArrayOp(address, opName, childAddress, destRegister, dimensionRegister, typeReference,
                        useLocalClass);
    }

    private final int destRegister;
    private final int dimensionRegister;
    private final boolean useLocalClass;
    private final String typeReference;

    private NewArrayOp(int address, String opName, int childAddress, int destRegister, int dimensionRegister,
                    String typeReference, boolean useLocalClass) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.dimensionRegister = dimensionRegister;
        this.typeReference = typeReference;
        this.useLocalClass = useLocalClass;
    }

    @Override
    public int[] execute(MethodState mState) {
        Object dimensionValue = mState.readRegister(dimensionRegister);

        Object instance = null;
        if (dimensionValue instanceof UnknownValue) {
            instance = new UnknownValue(typeReference);
        } else {
            int dimension = (int) dimensionValue;
            try {
                // TODO: easy, determine if dalvik clinit's classes on new-array
                instance = Utils.getArrayInstanceFromSmaliTypeReference(typeReference, dimension, useLocalClass);
            } catch (ClassNotFoundException e) {
                if (log.isErrorEnabled()) {
                    log.error("Couldn't find class: " + typeReference + " @" + toString(), e);
                }
            }
        }
        mState.assignRegister(destRegister, instance);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(dimensionRegister).append(", ").append(typeReference);

        return sb.toString();
    }
}
