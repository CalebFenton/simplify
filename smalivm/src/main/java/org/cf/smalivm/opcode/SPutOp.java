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

public class SPutOp extends ExecutionContextOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SPutOp.class.getSimpleName());

    private final String fieldDescriptor;
    private final int valueRegister;
    private final VirtualMachine vm;

    public SPutOp(MethodLocation location, MethodLocation child, int valueRegister, String fieldDescriptor,
                    VirtualMachine vm) {
        super(location, child);

        this.valueRegister = valueRegister;
        this.fieldDescriptor = fieldDescriptor;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext ectx) {
        MethodState mState = ectx.getMethodState();
        HeapItem item = mState.readRegister(valueRegister);
        // TODO: check if this is <clinit> and only allow static final fields to be initialized here
        vm.getStaticFieldAccessor().putField(ectx, fieldDescriptor, item);
    }

    @Override
    public SideEffect.Level sideEffectLevel() {
        return SideEffect.Level.WEAK;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(valueRegister).append(", ").append(fieldDescriptor);

        return sb.toString();
    }

}
