package simplify.vm.ops;

import java.util.logging.Logger;

import org.apache.commons.lang3.ClassUtils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.util.ReferenceUtil;

import simplify.Main;
import simplify.SmaliClassUtils;
import simplify.vm.MethodContext;
import simplify.vm.VirtualMachine;
import simplify.vm.types.LocalInstance;
import simplify.vm.types.UnknownValue;

public class ConstOp extends Op {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static enum ConstType {
        NARROW,
        WIDE,
        STRING,
        LOCAL_TYPE,
        CLASS
    };

    static ConstOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
        int destRegister = ((OneRegisterInstruction) instruction).getRegisterA();

        ConstType constType = null;
        Object literal = null;
        if (opName.matches("const-string(?:/jumbo)?")) {
            ReferenceInstruction instr = (ReferenceInstruction) instruction;
            literal = ((StringReference) instr.getReference()).getString();
            constType = ConstType.STRING;
        } else if (opName.endsWith("-class")) {
            ReferenceInstruction instr = (ReferenceInstruction) instruction;
            Reference classRef = instr.getReference();
            String className = ReferenceUtil.getReferenceString(classRef);
            literal = className;

            // Defer class lookup for execute. We may want to handle any possible errors there.
            if (vm.isClassDefinedLocally(className)) {
                constType = ConstType.LOCAL_TYPE;
            } else {
                constType = ConstType.CLASS;
            }
        } else if (opName.contains("-wide")) {
            WideLiteralInstruction instr = (WideLiteralInstruction) instruction;
            literal = instr.getWideLiteral();
            constType = ConstType.WIDE;
        } else {
            NarrowLiteralInstruction instr = (NarrowLiteralInstruction) instruction;
            literal = instr.getNarrowLiteral();
            constType = ConstType.NARROW;
        }

        return new ConstOp(address, opName, childAddress, destRegister, constType, literal);
    }

    private final int destRegister;
    private final ConstType constType;
    private final Object literal;

    private ConstOp(int address, String opName, int childAddress, int destRegister, ConstType constType, Object literal) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.constType = constType;
        this.literal = literal;
    }

    private ConstOp(int address, String opName, int childAddress, int destRegister, String literal) {
        this(address, opName, childAddress, destRegister, ConstType.STRING, literal);
    }

    private ConstOp(int address, String opName, int childAddress, int destRegister, int literal) {
        this(address, opName, childAddress, destRegister, ConstType.NARROW, literal);
    }

    private ConstOp(int address, String opName, int childAddress, int destRegister, long literal) {
        this(address, opName, childAddress, destRegister, ConstType.WIDE, literal);
    }

    @Override
    public int[] execute(MethodContext mctx) {
        Object result = null;
        if (constType == ConstType.CLASS) {
            String className = (String) literal;
            try {
                result = ClassUtils.getClass(SmaliClassUtils.smaliClassToJava(className));
            } catch (ClassNotFoundException e) {
                result = new UnknownValue(className);
                // log.warning("Could not find class for const-class: " + className);
                // e.printStackTrace();
            }
        } else if (constType == ConstType.LOCAL_TYPE) {
            String className = (String) literal;
            result = new LocalInstance(className);
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
        case LOCAL_TYPE:
        case CLASS:
            sb.append(literal);
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
        default:
            break;

        }

        return sb.toString();
    }

}
