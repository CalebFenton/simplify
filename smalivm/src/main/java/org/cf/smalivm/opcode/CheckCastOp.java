package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualGeneric;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckCastOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(CheckCastOp.class.getSimpleName());

    private final VirtualGeneric castType;
    private final int targetRegister;
    private final VirtualMachine vm;

    CheckCastOp(MethodLocation location, MethodLocation child, int targetRegister, VirtualGeneric castType,
                VirtualMachine vm) {
        super(location, child);
        this.targetRegister = targetRegister;
        this.castType = castType;
        this.vm = vm;
    }

    private static boolean isInstance(HeapItem item, VirtualGeneric referenceType, ClassManager classManager) {
        /*
         * This covers cases where class type cannot be implied from value. E.g.
         * Object o = null;
         * String s = (String) o; // check-cast o as null passes
         */
        VirtualGeneric itemType = classManager.getVirtualType(item.getType());
        if (!itemType.isPrimitive() && item.isNull()) {
            return true;
        }

        for (String typeName : Utils.getDeclaredAndValueTypeNames(item)) {
            VirtualGeneric type = classManager.getVirtualType(typeName);
            if (type.instanceOf(referenceType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(targetRegister);
        ClassManager classManager = vm.getClassManager();
        boolean isInstance = isInstance(item, castType, classManager);
        if (isInstance) {
            node.clearExceptions();
            mState.assignRegister(targetRegister, item.getValue(), castType.getName());
        } else {
            // E.g. java.lang.ClassCastException: java.lang.String cannot be cast to java.io.File
            String error = ClassNameUtils.internalToBinary(item.getType()) + " cannot be cast to " + castType
                    .getBinaryName();
            VirtualException exception = new VirtualException(ClassCastException.class, error);
            node.setException(exception);

            if (!item.isUnknown()) {
                // Had all virtual information, so exception is certain.
                node.clearChildren();
            }
        }
    }

    @Override
    public String toString() {
        return getName() + " r" + targetRegister + ", " + castType;
    }

}
