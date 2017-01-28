package org.cf.smalivm.opcode;

import org.cf.smalivm.ExceptionFactory;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualType;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckCastOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(CheckCastOp.class.getSimpleName());

    private final VirtualType castType;
    private final int targetRegister;
    private final ClassManager classManager;
    private final ExceptionFactory exceptionFactory;

    CheckCastOp(MethodLocation location, MethodLocation child, int targetRegister, VirtualType castType,
                ClassManager classManager, ExceptionFactory exceptionFactory) {
        super(location, child);
        this.targetRegister = targetRegister;
        this.castType = castType;
        this.classManager = classManager;
        this.exceptionFactory = exceptionFactory;
    }

    private static boolean isInstance(HeapItem item, VirtualType referenceType, ClassManager classManager) {
        /*
         * This covers cases where class type cannot be implied from value. E.g.
         * Object o = null;
         * String s = (String) o; // check-cast o as null passes
         */
        VirtualType itemType = classManager.getVirtualType(item.getType());
        if (!itemType.isPrimitive() && item.isNull()) {
            return true;
        }

        for (String typeName : Utils.getDeclaredAndValueTypeNames(item)) {
            VirtualType type = classManager.getVirtualType(typeName);
            if (type.instanceOf(referenceType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(targetRegister);
        boolean isInstance = isInstance(item, castType, classManager);
        if (isInstance) {
            node.clearExceptions();
            mState.assignRegister(targetRegister, item.getValue(), castType.getName());
        } else {
            // E.g. java.lang.ClassCastException: java.lang.String cannot be cast to java.io.File
            String error =
                    ClassNameUtils.internalToBinary(item.getType()) + " cannot be cast to " + castType.getBinaryName();
            Throwable exception = exceptionFactory.build(this, ClassCastException.class, error);
            node.setException(exception);

            if (!item.isUnknown()) {
                // Exception is certain to happen since we had all class information
                // exception is certain.
                node.clearChildren();
            }
        }
    }

    @Override
    public String toString() {
        return getName() + " r" + targetRegister + ", " + castType;
    }

}
