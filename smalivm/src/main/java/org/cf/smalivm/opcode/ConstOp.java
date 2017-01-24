package org.cf.smalivm.opcode;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.ExceptionFactory;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(ConstOp.class.getSimpleName());

    private final ConstantType constantType;
    private final int destRegister;
    private final Object literal;
    private final ClassLoader classLoader;

    ConstOp(MethodLocation location, MethodLocation child, int destRegister,
            ConstantType constantType, Object literal, ClassLoader classLoader,
            ExceptionFactory exceptionFactory) {
        super(location, child);
        this.destRegister = destRegister;
        this.constantType = constantType;
        this.literal = literal;
        this.classLoader = classLoader;

        if (ConstantType.CLASS.equals(constantType)) {
            addException(
                    exceptionFactory.build(this, ClassNotFoundException.class, (String) literal));
        }
    }

    public ConstantType getConstantType() {
        return constantType;
    }

    public int getDestRegister() {
        return destRegister;
    }

    public Object getLiteral() {
        return literal;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        Object constant = buildConstant();
        if (constant instanceof Throwable) {
            node.setException((Throwable) constant);
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
        Object constant;
        if (ConstantType.CLASS == constantType) {
            String className = (String) literal;
            try {
                String binaryClassName = ClassNameUtils.internalToBinary(className);
                constant = ClassUtils.getClass(classLoader, binaryClassName);
            } catch (ClassNotFoundException e) {
                return e;
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
                type = CommonTypes.CLASS;
                break;
            case NARROW:
                type = CommonTypes.INTEGER;
                break;
            case STRING:
                type = CommonTypes.STRING;
                break;
            case WIDE:
                if ("const-wide".equals(getName())) {
                    type = CommonTypes.DOUBLE;
                } else {
                    type = CommonTypes.LONG;
                }
                break;
            default:
                if (log.isWarnEnabled()) {
                    log.warn("Unexpected constant class (should never happen): {}", constantType);
                }
                type = CommonTypes.UNKNOWN;
        }

        return type;
    }

    public enum ConstantType {
        CLASS, NARROW, STRING, WIDE
    }
}
