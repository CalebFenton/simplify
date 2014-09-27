package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

public class CheckCastOp extends MethodStateOp {

    static CheckCastOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction21c instr = (Instruction21c) instruction;
        int targetRegister = instr.getRegisterA();
        TypeReference typeRef = (TypeReference) instr.getReference();
        String className = typeRef.getType();

        return new CheckCastOp(address, opName, childAddress, targetRegister, className, vm);
    }

    private final int targetRegister;
    private final String className;
    private final VirtualMachine vm;

    CheckCastOp(int address, String opName, int childAddress, int targetRegister, String className, VirtualMachine vm) {
        super(address, opName, childAddress);

        this.targetRegister = targetRegister;
        this.className = className;
        this.vm = vm;
    }

    @Override
    public int[] execute(MethodState mctx) {
        Object value = mctx.readRegister(targetRegister);
        if (value instanceof UnknownValue) {
            value = new UnknownValue(className);
        } else if (vm.isLocalClass(className)) {
            // TODO: make VM keep track of local class hierarchy
        } else {
            // TODO: make exception framework and throw exception if can't cast
        }
        mctx.assignRegister(targetRegister, value);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(targetRegister).append(", ").append(className);

        return sb.toString();
    }

}
