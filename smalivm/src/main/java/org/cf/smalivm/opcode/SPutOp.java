package org.cf.smalivm.opcode;

import org.cf.smalivm.SideEffect;
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

public class SPutOp extends ExecutionContextOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SPutOp.class.getSimpleName());

    private final FieldReference fieldReference;
    private final VirtualField actualField;
    private final int valueRegister;
    private final VirtualMachine vm;

    SPutOp(MethodLocation location, MethodLocation child, int valueRegister,
                  FieldReference fieldReference, VirtualField actualField, VirtualMachine vm) {
        super(location, child);

        this.fieldReference = fieldReference;
        this.actualField = actualField;
        this.valueRegister = valueRegister;
        this.vm = vm;
    }

    public FieldReference getFieldReference() {
        return fieldReference;
    }

    public VirtualField getActualField() {
        return actualField;
    }

    public int getValueRegister() {
        return valueRegister;
    }

    public VirtualMachine getVm() {
        return vm;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext context) {
        MethodState mState = context.getMethodState();
        HeapItem item = mState.readRegister(valueRegister);
        // TODO: check if this is <clinit> and only allow static final fields to be initialized here
        vm.getStaticFieldAccessor().putField(context, actualField, item);
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.WEAK;
    }

    @Override
    public String toString() {
        return getName() + " r" + valueRegister + ", " + ReferenceUtil
                .getFieldDescriptor(fieldReference);
    }

}
