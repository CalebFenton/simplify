package org.cf.smalivm.opcode;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPutOp extends ExecutionContextOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SPutOp.class.getSimpleName());

    static SPutOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction21c instr = (Instruction21c) instruction;
        int destRegister = instr.getRegisterA();
        FieldReference reference = (FieldReference) instr.getReference();
        String fieldDescriptor = ReferenceUtil.getFieldDescriptor(reference);

        return new SPutOp(address, opName, childAddress, destRegister, fieldDescriptor, vm);
    }

    private final String fieldDescriptor;
    private final int valueRegister;
    private final VirtualMachine vm;

    public SPutOp(int address, String opName, int childAddress, int valueRegister, String fieldDescriptor,
                    VirtualMachine vm) {
        super(address, opName, childAddress);

        this.valueRegister = valueRegister;
        this.fieldDescriptor = fieldDescriptor;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionContext ectx) {
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
