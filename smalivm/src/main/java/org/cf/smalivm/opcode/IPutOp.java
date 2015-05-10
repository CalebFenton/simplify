package org.cf.smalivm.opcode;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPutOp extends ExecutionContextOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(IPutOp.class.getSimpleName());

    static IPutOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction22c instr = (Instruction22c) instruction;
        int valueRegister = instr.getRegisterA();
        int instanceRegister = instr.getRegisterB();
        FieldReference reference = (FieldReference) instr.getReference();
        String fieldDescriptor = ReferenceUtil.getFieldDescriptor(reference);

        return new IPutOp(address, opName, childAddress, valueRegister, instanceRegister, fieldDescriptor, vm);
    }

    private final String fieldDescriptor;
    private final int valueRegister;
    private final int instanceRegister;
    private final VirtualMachine vm;

    public IPutOp(int address, String opName, int childAddress, int valueRegister, int instanceRegister,
                    String fieldDescriptor, VirtualMachine vm) {
        super(address, opName, childAddress);

        this.valueRegister = valueRegister;
        this.instanceRegister = instanceRegister;
        this.fieldDescriptor = fieldDescriptor;
        this.vm = vm;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext ectx) {
        MethodState mState = ectx.getMethodState();
        HeapItem valueItem = mState.readRegister(valueRegister);
        HeapItem instanceItem = mState.readRegister(instanceRegister);

        mState.assignRegister(instanceRegister, instanceItem);
    }

    @Override
    public SideEffect.Level sideEffectLevel() {
        return SideEffect.Level.WEAK;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(valueRegister).append(", r").append(instanceRegister).append(", ")
                        .append(fieldDescriptor);

        return sb.toString();
    }

}
