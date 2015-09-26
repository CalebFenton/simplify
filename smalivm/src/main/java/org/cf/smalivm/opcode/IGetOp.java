package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IGetOp extends ExecutionContextOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(IGetOp.class.getSimpleName());

    private final int destRegister;
    private final int instanceRegister;
    private final String fieldDescriptor;
    private final VirtualMachine vm;

    public IGetOp(MethodLocation location, MethodLocation child, int destRegister, int instanceRegister,
                    String fieldDescriptor, VirtualMachine vm) {
        super(location, child);

        this.destRegister = destRegister;
        this.instanceRegister = instanceRegister;
        this.fieldDescriptor = fieldDescriptor;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext ectx) {
        // TODO: https://github.com/CalebFenton/simplify/issues/22
        MethodState mState = ectx.getMethodState();
        HeapItem instanceItem = mState.readRegister(instanceRegister);

        String type = fieldDescriptor.split(":")[1];
        mState.assignRegister(destRegister, HeapItem.newUnknown(type));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(instanceRegister).append(", ")
                        .append(fieldDescriptor);

        return sb.toString();
    }

}
