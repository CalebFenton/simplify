package org.cf.smalivm.opcode;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceOfOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(InstanceOfOp.class.getSimpleName());

    private final String className;
    private final int destRegister;
    private final int arg1Register;
    private final VirtualMachine vm;

    InstanceOfOp(BuilderInstruction instruction, BuilderInstruction child, int destRegister, int arg1Register,
                    String className, VirtualMachine vm) {
        super(instruction, child);

        this.destRegister = destRegister;
        this.arg1Register = arg1Register;
        this.className = className;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(arg1Register);
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

        if (!isInstance && item.isUnknown()) {
            // Don't have all of the type information, only declared type.
            // Object obj = method.reflect() for example may return a String
            // but the declared type is Object.
            mState.assignRegister(destRegister, HeapItem.newUnknown("Z"));
        } else {
            mState.assignRegister(destRegister, new HeapItem(isInstance, "Z"));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", ").append(arg1Register).append(", ").append(className);

        return sb.toString();
    }

}
