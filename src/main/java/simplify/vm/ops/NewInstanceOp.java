package simplify.vm.ops;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

import simplify.vm.MethodContext;
import simplify.vm.MethodReflector;
import simplify.vm.SideEffect;
import simplify.vm.VirtualMachine;
import simplify.vm.types.LocalInstance;
import simplify.vm.types.UninitializedInstance;

public class NewInstanceOp extends Op {

    static NewInstanceOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction21c instr = (Instruction21c) instruction;
        int destRegister = instr.getRegisterA();
        TypeReference typeRef = (TypeReference) instr.getReference();
        String className = typeRef.getType();

        return new NewInstanceOp(address, opName, childAddress, destRegister, className, vm);
    }

    private final int destRegister;
    private final String className;
    private final VirtualMachine vm;
    private SideEffect.Type sideEffectType;

    NewInstanceOp(int address, String opName, int childAddress, int destRegister, String className, VirtualMachine vm) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.className = className;
        this.vm = vm;
        sideEffectType = SideEffect.Type.STRONG;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        Object instance = null;
        if (vm.isClassDefinedLocally(className)) {
            // New-instance causes static initialization (but not new-array!)
            vm.staticallyInitializeClassIfNecessary(className);
            sideEffectType = vm.getClassStaticInitializerSideEffectType(className);
            instance = new LocalInstance(className);
        } else {
            if (MethodReflector.isWhitelisted(className)) {
                sideEffectType = SideEffect.Type.NONE;
            }
            instance = new UninitializedInstance(className);
        }

        mctx.assignRegister(destRegister, instance);

        return getPossibleChildren();
    }

    @Override
    public SideEffect.Type sideEffectType() {
        return sideEffectType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

        sb.append(" r").append(destRegister).append(", ").append(className);

        return sb.toString();
    }

}
