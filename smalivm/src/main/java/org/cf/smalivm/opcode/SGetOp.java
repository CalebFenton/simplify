package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SGetOp extends ExecutionContextOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SGetOp.class.getSimpleName());

    private final int destRegister;
    private final String fieldDescriptor;
    private final VirtualMachine vm;

    SGetOp(BuilderInstruction instruction, BuilderInstruction child, int destRegister, String fieldDescriptor,
                    VirtualMachine vm) {
        super(instruction, child);

        this.destRegister = destRegister;
        this.fieldDescriptor = fieldDescriptor;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext ectx) {
        HeapItem item = vm.getStaticFieldAccessor().getField(ectx, fieldDescriptor);
        MethodState mState = ectx.getMethodState();
        mState.assignRegister(destRegister, item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", ").append(fieldDescriptor);

        return sb.toString();
    }

}
