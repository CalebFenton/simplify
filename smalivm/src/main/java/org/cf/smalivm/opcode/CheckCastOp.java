package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.smalivm.smali.ClassManager;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckCastOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(CheckCastOp.class.getSimpleName());

    private final String checkClassName;
    private final int targetRegister;
    private final VirtualMachine vm;

    CheckCastOp(MethodLocation location, MethodLocation child, int targetRegister, String className, VirtualMachine vm) {
        super(location, child);

        this.targetRegister = targetRegister;
        this.checkClassName = className;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(targetRegister);
        ClassManager classManager = vm.getClassManager();
        boolean isInstance = isInstance(item, classManager, checkClassName);

        if (isInstance) {
            node.clearExceptions();
            mState.assignRegister(targetRegister, item.getValue(), checkClassName);
        } else {
            // E.g. java.lang.ClassCastException: java.lang.String cannot be cast to java.io.File
            StringBuilder sb = new StringBuilder();
            sb.append(ClassNameUtils.internalToBinary(item.getType()));
            sb.append(" cannot be cast to ");
            sb.append(ClassNameUtils.internalToBinary(checkClassName));

            VirtualException exception = new VirtualException(ClassCastException.class, sb.toString());
            node.setException(exception);

            if (!item.isUnknown()) {
                // Had all type information, so exception is certain.
                node.clearChildren();
            }

            return;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(targetRegister).append(", ").append(checkClassName);

        return sb.toString();
    }

    private static boolean isInstance(HeapItem item, ClassManager classManager, String targetType) {
        try {
            if (!ClassNameUtils.isPrimitive(item.getType())) {
                if (item.getValue() == null) {
                    /*
                     * This covers cases where type info cannot be implied from value. E.g.
                     * Object o = null;
                     * String s = (String) o;
                     */
                    return true;
                }
            }

            for (String type : Utils.getTypes(item)) {
                if (classManager.isInstance(type, targetType)) {
                    return true;
                }
            }
        } catch (UnknownAncestors e) {
            log.error("Unable to determine ancestory for {}\n{}", targetType, e);
        }

        return false;
    }

}
