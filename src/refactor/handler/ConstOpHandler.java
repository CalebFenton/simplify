package refactor.handler;

import org.jf.dexlib2.iface.instruction.Instruction;

import refactor.vm.MethodContext;

public class ConstOpHandler extends OpHandler {

    private static enum ConstType {
        NARROW,
        WIDE,
        STRING,
        CLASS
    };

    static ConstOpHandler create(Instruction instruction, int address) {
        ConstOpHandler result = null;

        int nextInstructionAddress = address + instruction.getCodeUnits();

        return result;
    }

    private final int address;
    private final int nextInstructionAddress;
    private final int destRegister;
    private final ConstType constType;
    private final Object literal;

    private ConstOpHandler(int address, int nextInstructionAddress, int destRegister, ConstType constType,
                    Object literal) {
        this.address = address;
        this.nextInstructionAddress = nextInstructionAddress;
        this.destRegister = destRegister;
        this.constType = constType;
        this.literal = literal;
    }

    private ConstOpHandler(int address, int nextInstructionAddress, int destRegister, ConstType constType,
                    String literal) {
        this(address, nextInstructionAddress, destRegister, constType, (Object) literal);
    }

    private ConstOpHandler(int address, int nextInstructionAddress, int destRegister, int literal) {
        this(address, nextInstructionAddress, destRegister, ConstType.NARROW, literal);
    }

    private ConstOpHandler(int address, int nextInstructionAddress, int destRegister, long literal) {
        this(address, nextInstructionAddress, destRegister, ConstType.WIDE, literal);
    }

    @Override
    public int[] execute(MethodContext mectx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { nextInstructionAddress };
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

}
