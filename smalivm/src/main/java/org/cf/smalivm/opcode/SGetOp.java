package org.cf.smalivm.opcode;

import org.cf.smalivm.StaticFieldAccessor;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.VirtualField;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SGetOp extends ExecutionContextOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SGetOp.class.getSimpleName());

    private final int destRegister;
    private final FieldReference fieldReference;
    private final VirtualField actualField;
    private final VirtualMachine vm;

    SGetOp(MethodLocation location, MethodLocation child, int destRegister, FieldReference fieldReference,
           VirtualField actualField, VirtualMachine vm) {
        super(location, child);

        this.destRegister = destRegister;
        this.fieldReference = fieldReference;
        this.actualField = actualField;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext context) {
        StaticFieldAccessor accessor = vm.getStaticFieldAccessor();
        HeapItem item = accessor.getField(context, actualField);
        MethodState mState = context.getMethodState();
        mState.assignRegister(destRegister, item);
    }

    @Override
    public String toString() {
        return getName() + " r" + destRegister + ", " + ReferenceUtil.getFieldDescriptor(fieldReference);
    }

}
