package refactor.handler;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

import refactor.vm.MethodContext;

public class NewInstanceOpHandler extends OpHandler {

    static NewInstanceOpHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction21c instr = (Instruction21c) instruction;
        int destRegister = instr.getRegisterA();
        TypeReference typeRef = (TypeReference) instr.getReference();
        String targetClassName = typeRef.getType();

        return new NewInstanceOpHandler(address, opName, childAddress, destRegister, targetClassName);
    }

    private final int address;
    private final String opName;
    private final int childAddress;
    private final int destRegister;
    private final String targetClassName;

    NewInstanceOpHandler(int address, String opName, int childAddress, int destRegister, String targetClassName) {
        this.address = address;
        this.opName = opName;
        this.childAddress = childAddress;
        this.destRegister = destRegister;
        this.targetClassName = targetClassName;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        mctx.setRegister(destRegister, targetClassName, null, address);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append(" r").append(destRegister).append(", ").append(targetClassName);

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
