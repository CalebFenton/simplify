package org.cf.smalivm.opcode;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstOp extends MethodStateOp {

    private static enum ConstantType {
        CLASS,
        LOCAL_CLASS,
        NARROW,
        STRING,
        WIDE
    }

    private static final Logger log = LoggerFactory.getLogger(ConstOp.class.getSimpleName());

    static ConstOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
        int destRegister = ((OneRegisterInstruction) instruction).getRegisterA();

        ConstantType constantType = null;
        Object literal = null;
        if (opName.matches("const-string(?:/jumbo)?")) {
            ReferenceInstruction instr = (ReferenceInstruction) instruction;
            literal = ((StringReference) instr.getReference()).getString();
            constantType = ConstantType.STRING;
        } else if (opName.endsWith("-class")) {
            ReferenceInstruction instr = (ReferenceInstruction) instruction;
            Reference classRef = instr.getReference();
            String className = ReferenceUtil.getReferenceString(classRef);
            literal = className;

            // Defer class lookup for execute. We may want to handle any possible errors there.
            if (vm.isLocalClass(className)) {
                constantType = ConstantType.LOCAL_CLASS;
            } else {
                constantType = ConstantType.CLASS;
            }
        } else if (opName.contains("-wide")) {
            WideLiteralInstruction instr = (WideLiteralInstruction) instruction;
            literal = instr.getWideLiteral();
            constantType = ConstantType.WIDE;
        } else {
            NarrowLiteralInstruction instr = (NarrowLiteralInstruction) instruction;
            literal = instr.getNarrowLiteral();
            constantType = ConstantType.NARROW;
        }

        return new ConstOp(address, opName, childAddress, destRegister, constantType, literal);
    }

    private final ConstantType constantType;
    private final int destRegister;
    private final Object literal;

    private ConstOp(int address, String opName, int childAddress, int destRegister, ConstantType constantType,
                    Object literal) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.constantType = constantType;
        this.literal = literal;
    }

    private ConstOp(int address, String opName, int childAddress, int destRegister, int literal) {
        this(address, opName, childAddress, destRegister, ConstantType.NARROW, literal);
    }

    private ConstOp(int address, String opName, int childAddress, int destRegister, long literal) {
        this(address, opName, childAddress, destRegister, ConstantType.WIDE, literal);
    }

    private ConstOp(int address, String opName, int childAddress, int destRegister, String literal) {
        this(address, opName, childAddress, destRegister, ConstantType.STRING, literal);
    }

    @Override
    public int[] execute(MethodState mState) {
        Object constant = buildConstant();
        HeapItem constantItem = new HeapItem(constant, getConstantTypeString());
        mState.assignRegister(destRegister, constantItem);

        return getChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", ");
        String val;
        switch (constantType) {
        case LOCAL_CLASS:
        case CLASS:
            sb.append(literal);
            break;
        case NARROW:
            val = Integer.toString((int) literal, 16);
            if (val.startsWith("-")) {
                sb.append('-');
                val = val.substring(1);
            }
            sb.append("0x").append(val);
            break;
        case STRING:
            sb.append('"').append((String) literal).append('"');
            break;
        case WIDE:
            val = Long.toString((long) literal, 16);
            if (val.startsWith("-")) {
                sb.append('-');
                val = val.substring(1);
            }
            sb.append("0x").append(val);
            break;
        default:
            break;
        }

        return sb.toString();
    }

    private String getConstantTypeString() {
        // Type strings are somewhat ambiguous here. Dalvik treats a multiple types as I (char, byte, etc.) and multiple
        // types as "wide" (long, float). Logic elsewhere must infer actual types when necessary, such as by opcode.
        String type;
        switch (constantType) {
        case CLASS:
        case LOCAL_CLASS:
            type = "Ljava/lang/Class;";
            break;
        case NARROW:
            type = "I";
            break;
        case STRING:
            type = "Ljava/lang/String;";
            break;
        case WIDE:
            if ("const-wide".equals(getName())) {
                type = "D";
            } else {
                type = "J";
            }
            break;
        default:
            if (log.isWarnEnabled()) {
                log.warn("Unexpected constant type: " + constantType);
            }
            type = "?"; // should never happen
        }

        return type;
    }

    private Object buildConstant() {
        Object result = null;
        if (ConstantType.CLASS == constantType) {
            String className = (String) literal;
            try {
                String javaClassName = SmaliClassUtils.smaliClassToJava(className);
                result = ClassUtils.getClass(javaClassName);
            } catch (ClassNotFoundException e) {
                // It could be a framework class we're not aware of.
                if (log.isWarnEnabled()) {
                    log.warn("Unknown class: " + className + ", assuming it's local.");
                }
                result = new LocalClass(className);
            }
        } else if (ConstantType.LOCAL_CLASS == constantType) {
            String className = (String) literal;
            result = new LocalClass(className);
        } else {
            result = literal;
        }

        return result;
    }

}
