package org.cf.smalivm.opcode;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckCastOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(CheckCastOp.class.getSimpleName());

    private final String className;
    private final int targetRegister;
    private final VirtualMachine vm;

    CheckCastOp(MethodLocation location, MethodLocation child, int targetRegister, String className, VirtualMachine vm) {
        super(location, child);

        this.targetRegister = targetRegister;
        this.className = className;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(targetRegister);
        boolean isInstance = false;
        try {
            ClassManager classManager = vm.getClassManager();
            for (String type : Utils.getTypes(item)) {
                isInstance = classManager.isInstance(type, className);
                if (isInstance) {
                    break;
                }
            }
        } catch (UnknownAncestors e) {
            if (log.isErrorEnabled()) {
                log.error("Unable to determine ancestory for {}\n{}", className, e);
            }
        }

        if (isInstance) {
            node.clearExceptions();
        } else {
            // java.lang.ClassCastException: java.lang.String cannot be cast to java.io.File
            StringBuilder sb = new StringBuilder();
            sb.append(SmaliClassUtils.smaliClassToJava(item.getType()));
            sb.append(" cannot be cast to ");
            sb.append(SmaliClassUtils.smaliClassToJava(className));

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
        sb.append(" r").append(targetRegister).append(", ").append(className);

        return sb.toString();
    }

}
