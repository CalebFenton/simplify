package org.cf.smalivm.opcode;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstOp extends MethodStateOp {

    static enum ConstantType {
        CLASS, LOCAL_CLASS, NARROW, STRING, WIDE
    }

    private static final Logger log = LoggerFactory.getLogger(ConstOp.class.getSimpleName());

    private final ConstantType constantType;
    private final int destRegister;
    private final Object literal;

    ConstOp(MethodLocation location, MethodLocation child, int destRegister, ConstantType constantType, Object literal) {
        super(location, child);

        this.destRegister = destRegister;
        this.constantType = constantType;
        this.literal = literal;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        Object constant = buildConstant();
        HeapItem constantItem = new HeapItem(constant, getConstantTypeString());
        mState.assignRegister(destRegister, constantItem);
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
                    log.warn("Unknown class: {}, assuming it's local.", className);
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

}
