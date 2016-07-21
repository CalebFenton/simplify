package org.cf.smalivm.opcode;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.UninitializedInstance;
import org.jf.dexlib2.builder.MethodLocation;

public class NewInstanceOp extends ExecutionContextOp {

    private final VirtualClass virtualClass;
    private final int destRegister;
    private final VirtualMachine vm;
    private SideEffect.Level sideEffectLevel;

    NewInstanceOp(MethodLocation location, MethodLocation child, int destRegister, VirtualClass virtualClass,
                  VirtualMachine vm) {
        super(location, child);

        this.destRegister = destRegister;
        this.virtualClass = virtualClass;
        this.vm = vm;
        sideEffectLevel = SideEffect.Level.STRONG;
    }

    @Override
    public void execute(ExecutionNode node, ExecutionContext context) {
        Object instance = new UninitializedInstance(virtualClass);
        if (vm.isSafe(virtualClass)) {
            sideEffectLevel = SideEffect.Level.NONE;
        } else {
            // New-instance causes static initialization (but not new-array!)
            context.readClassState(virtualClass); // access will initialize if necessary
            sideEffectLevel = context.getClassSideEffectLevel(virtualClass);
        }

        MethodState mState = context.getMethodState();
        HeapItem instanceItem = new HeapItem(instance, virtualClass.getName());
        mState.assignRegister(destRegister, instanceItem);
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return sideEffectLevel;
    }

    @Override
    public String toString() {
        return getName() + " r" + destRegister + ", " + virtualClass;
    }

}
