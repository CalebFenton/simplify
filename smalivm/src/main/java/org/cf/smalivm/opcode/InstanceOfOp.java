package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualType;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceOfOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(InstanceOfOp.class.getSimpleName());

    private final VirtualType referenceType;
    private final int destRegister;
    private final int arg1Register;
    private final VirtualMachine vm;

    InstanceOfOp(MethodLocation location, MethodLocation child, int destRegister, int arg1Register, VirtualType
            referenceType, VirtualMachine vm) {
        super(location, child);
        this.destRegister = destRegister;
        this.arg1Register = arg1Register;
        this.referenceType = referenceType;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(arg1Register);
        Object value = item.getValue();

        if (value == null) {
            mState.assignRegister(destRegister, new HeapItem(false, CommonTypes.BOOL));
            return;
        }

        ClassManager classManager = vm.getClassManager();
        for (String typeName : Utils.getDeclaredAndValueTypeNames(item)) {
            VirtualType itemType = classManager.getVirtualType(typeName);
            if (itemType.instanceOf(referenceType)) {
                mState.assignRegister(destRegister, new HeapItem(true, CommonTypes.BOOL));
                return;
            }
        }

        if (item.isUnknown()) {
            // Since value is unknown, we can't get the actual type of the value. Declared type isn't enough.
            mState.assignRegister(destRegister, HeapItem.newUnknown(CommonTypes.BOOL));
        } else {
            mState.assignRegister(destRegister, new HeapItem(false, CommonTypes.BOOL));
        }
    }

    @Override
    public String toString() {
        return getName() + " r" + destRegister + ", " + arg1Register + ", " + referenceType;
    }

}
