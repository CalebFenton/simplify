package org.cf.smalivm.opcode;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPutOp extends ExecutionContextOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(IPutOp.class.getSimpleName());

    private final String fieldDescriptor;
    private final int valueRegister;
    private final int instanceRegister;
    private final VirtualMachine vm;

    IPutOp(MethodLocation location, MethodLocation child, int valueRegister, int instanceRegister,
           String fieldDescriptor, VirtualMachine vm) {
        super(location, child);

        this.valueRegister = valueRegister;
        this.instanceRegister = instanceRegister;
        this.fieldDescriptor = fieldDescriptor;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext context) {
        MethodState mState = context.getMethodState();
        HeapItem valueItem = mState.readRegister(valueRegister);
        HeapItem instanceItem = mState.readRegister(instanceRegister);

        mState.assignRegister(instanceRegister, instanceItem);
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.WEAK;
    }

    @Override
    public String toString() {
        return getName() + " r" + valueRegister + ", r" + instanceRegister + ", " + fieldDescriptor;
    }

}
