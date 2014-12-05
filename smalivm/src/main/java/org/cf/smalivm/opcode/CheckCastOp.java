package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.smalivm.type.Type;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckCastOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(CheckCastOp.class.getSimpleName());

    static CheckCastOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction21c instr = (Instruction21c) instruction;
        int targetRegister = instr.getRegisterA();
        TypeReference typeRef = (TypeReference) instr.getReference();
        String className = typeRef.getType();

        return new CheckCastOp(address, opName, childAddress, targetRegister, className, vm);
    }

    private final String className;
    private final int targetRegister;
    private final VirtualMachine vm;

    CheckCastOp(int address, String opName, int childAddress, int targetRegister, String className, VirtualMachine vm) {
        super(address, opName, childAddress);

        this.targetRegister = targetRegister;
        this.className = className;
        this.vm = vm;
    }

    @Override
    public int[] execute(MethodState mState) {
        Object value = mState.readRegister(targetRegister);
        String type;
        if (value instanceof Type) {
            type = ((Type) value).getType();
        } else {
            type = SmaliClassUtils.javaClassToSmali(value.getClass());
        }

        try {
            if (!vm.getClassManager().isInstance(type, className)) {
                // TODO: exception should be filled with spoofed stack trace
                // throw new ClassCastException("Class - " + className);
            }
        } catch (UnknownAncestors e) {
            log.warn("Unable to determine ancestory for class", e);
            // Could be framework class that's just not part of the VM (yet)
        }

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(targetRegister).append(", ").append(className);

        return sb.toString();
    }

}
