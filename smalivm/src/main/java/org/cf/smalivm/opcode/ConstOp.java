package org.cf.smalivm.opcode;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstOp extends MethodStateOp {

    static enum ConstantType {
        CLASS, NARROW, STRING, WIDE
    }

    private static final Logger log = LoggerFactory.getLogger(ConstOp.class.getSimpleName());

    private final ConstantType constantType;
    private final int destRegister;
    private final Object literal;
    private final ClassLoader classLoader;

    ConstOp(MethodLocation location, MethodLocation child, int destRegister, ConstantType constantType, Object literal,
                    ClassLoader classLoader) {
        super(location, child);

        this.destRegister = destRegister;
        this.constantType = constantType;
        this.literal = literal;
        this.classLoader = classLoader;

        if (ConstantType.CLASS.equals(constantType)) {
            addException(new VirtualException(ClassNotFoundException.class, (String) literal));
        }
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        Object constant = buildConstant();
        if (constant instanceof VirtualException) {
            node.setException((VirtualException) constant);
            node.clearChildren();
            return;
        } else {
            node.clearExceptions();
        }

        mState.assignRegister(destRegister, constant, getConstantTypeString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", ");
        String val;
        switch (constantType) {
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

    private Object buildConstant() {
        Object constant = null;
        if (ConstantType.CLASS == constantType) {
            String className = (String) literal;
            try {
                String binaryClassName = ClassNameUtils.internalToBinary(className);
                constant = ClassUtils.getClass(classLoader, binaryClassName);
            } catch (ClassNotFoundException e) {
                return new VirtualException(e);
            }
        } else {
            constant = literal;
        }

        return constant;
    }

    private String getConstantTypeString() {
        // Type string is ambiguous because Dalvik treats a multiple types as int (char, byte, etc.) and multiple
        // types as "wide" (long, float). Logic elsewhere must infer actual types when necessary, such as by opcode.
        String type;
        switch (constantType) {
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
            if ("const-wide".equals(getName())) {
                type = "D";
            } else {
                type = "J";
            }
            break;
        default:
            if (log.isWarnEnabled()) {
                log.warn("Unexpected constant type: {} (should never happen)", constantType);
            }
            type = "?";
        }

        return type;
    }

}
