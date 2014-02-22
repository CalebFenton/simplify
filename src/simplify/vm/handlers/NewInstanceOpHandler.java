package simplify.vm.handlers;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

import simplify.vm.MethodContext;
import simplify.vm.VirtualMachine;
import simplify.vm.types.SmaliClassInstance;
import simplify.vm.types.UninitializedInstance;

public class NewInstanceOpHandler extends OpHandler {

    static NewInstanceOpHandler create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction21c instr = (Instruction21c) instruction;
        int destRegister = instr.getRegisterA();
        TypeReference typeRef = (TypeReference) instr.getReference();
        String className = typeRef.getType();

        return new NewInstanceOpHandler(address, opName, childAddress, destRegister, className, vm);
    }

    private final int address;
    private final String opName;
    private final int childAddress;
    private final int destRegister;
    private final String className;
    private final VirtualMachine vm;

    NewInstanceOpHandler(int address, String opName, int childAddress, int destRegister, String className,
                    VirtualMachine vm) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.destRegister = destRegister;
        this.className = className;
        this.vm = vm;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        Object instance = null;
        if (vm.isClassDefinedLocally(className)) {
            instance = new SmaliClassInstance(className);
        } else {
            instance = new UninitializedInstance(className);
        }

        mctx.assignRegister(destRegister, instance);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append(" r").append(destRegister).append(", ").append(className);

        return sb.toString();
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { childAddress };
    }

    @Override
    public int getAddress() {
        return address;
    }

}
