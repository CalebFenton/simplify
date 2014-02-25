package simplify.vm.handlers;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.iface.reference.TypeReference;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VirtualMachine;

public class ConstOpHandler extends OpHandler {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static enum ConstType {
        NARROW,
        WIDE,
        STRING,
        CLASS
    };

    static ConstOpHandler create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
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

        return new ConstOpHandler(address, opName, childAddress, destRegister, constType, literal);
    }

    private final int destRegister;
    private final ConstType constType;
    private final Object literal;

    private ConstOpHandler(int address, String opName, int childAddress, int destRegister, ConstType constType,
                    Object literal) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.constType = constType;
        this.literal = literal;
    }

    private ConstOpHandler(int address, String opName, int childAddress, int destRegister, String literal) {
        this(address, opName, childAddress, destRegister, ConstType.STRING, literal);
    }

    private ConstOpHandler(int address, String opName, int childAddress, int destRegister, TypeReference classRef) {
        this(address, opName, childAddress, destRegister, ConstType.CLASS, classRef);
    }

    private ConstOpHandler(int address, String opName, int childAddress, int destRegister, int literal) {
        this(address, opName, childAddress, destRegister, ConstType.NARROW, literal);
    }

    private ConstOpHandler(int address, String opName, int childAddress, int destRegister, long literal) {
        this(address, opName, childAddress, destRegister, ConstType.WIDE, literal);
    }

    @Override
    public int[] execute(MethodContext mctx) {
        Object result = null;
        if (constType == ConstType.CLASS) {
            result = Class.class;
        } else {
            result = literal;
        }

        mctx.assignRegister(destRegister, result);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

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

}
