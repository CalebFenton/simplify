package refactor.handler;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.iface.reference.TypeReference;

import refactor.vm.MethodContext;
import refactor.vm.RegisterStore;
import refactor.vm.VirtualMachine;

public class ConstOpHandler extends OpHandler {

    private static enum ConstType {
        NARROW,
        WIDE,
        STRING,
        CLASS
    };

    static ConstOpHandler create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int nextInstructionAddress = address + instruction.getCodeUnits();
        int destRegister = ((OneRegisterInstruction) instruction).getRegisterA();

        ConstType constType = null;
        Object literal = null;
        if (opName.endsWith("-string")) {
            ReferenceInstruction instr = (ReferenceInstruction) instruction;
            literal = ((StringReference) instr.getReference()).getString();
            constType = ConstType.STRING;
        } else if (opName.endsWith("class")) {
            ReferenceInstruction instr = (ReferenceInstruction) instruction;
            literal = instr.getReference();
            constType = ConstType.CLASS;
        } else if (opName.contains("-wide")) {
            WideLiteralInstruction instr = (WideLiteralInstruction) instruction;
            literal = instr.getWideLiteral();
            constType = ConstType.WIDE;
        } else {
            NarrowLiteralInstruction instr = (NarrowLiteralInstruction) instruction;
            literal = instr.getNarrowLiteral();
            constType = ConstType.NARROW;
        }

        return new ConstOpHandler(address, opName, nextInstructionAddress, destRegister, constType, literal);
    }

    private final int address;
    private final String opName;
    private final int nextInstructionAddress;
    private final int destRegister;
    private final ConstType constType;
    private final Object literal;

    private ConstOpHandler(int address, String opName, int nextInstructionAddress, int destRegister,
                    ConstType constType, Object literal) {
        this.address = address;
        this.opName = opName;
        this.nextInstructionAddress = nextInstructionAddress;
        this.destRegister = destRegister;
        this.constType = constType;
        this.literal = literal;
    }

    private ConstOpHandler(int address, String opName, int nextInstructionAddress, int destRegister, String literal) {
        this(address, opName, nextInstructionAddress, destRegister, ConstType.STRING, literal);
    }

    private ConstOpHandler(int address, String opName, int nextInstructionAddress, int destRegister,
                    TypeReference classRef) {
        this(address, opName, nextInstructionAddress, destRegister, ConstType.CLASS, classRef);
    }

    private ConstOpHandler(int address, String opName, int nextInstructionAddress, int destRegister, int literal) {
        this(address, opName, nextInstructionAddress, destRegister, ConstType.NARROW, literal);
    }

    private ConstOpHandler(int address, String opName, int nextInstructionAddress, int destRegister, long literal) {
        this(address, opName, nextInstructionAddress, destRegister, ConstType.WIDE, literal);
    }

    @Override
    public int[] execute(MethodContext mctx) {
        String type = null;
        switch (constType) {
        case CLASS:
            type = "Ljava/lang/Class;";
            break;
        case NARROW:
            type = "I";
            break;
        case STRING:
            type = "Ljava/lang/String;";
            break;
        case WIDE:
            type = "J";
            break;
        }

        mctx.setRegister(destRegister, new RegisterStore(type, literal));

        return getPossibleChildren();
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { nextInstructionAddress };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append(" r").append(destRegister).append(", ");
        switch (constType) {
        case CLASS:
            sb.append(((TypeReference) literal).getType());
            break;
        case NARROW:
            sb.append("0x").append(Integer.toHexString((int) literal));
            break;
        case STRING:
            sb.append("\"").append((String) literal).append("\"");
            break;
        case WIDE:
            sb.append("0x").append(Long.toHexString((long) literal));
            break;

        }

        return sb.toString();
    }

    @Override
    public int getAddress() {
        return address;
    }

}
