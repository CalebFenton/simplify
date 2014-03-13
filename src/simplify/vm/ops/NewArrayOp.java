package simplify.vm.ops;

import java.lang.reflect.Array;

import org.apache.commons.lang3.ClassUtils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;

import simplify.SmaliClassUtils;
import simplify.vm.MethodContext;
import simplify.vm.VirtualMachine;
import simplify.vm.types.UninitializedInstance;

public class NewArrayOp extends Op {

    private static void populateLocalTypeArray(Object array, String className) {
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, new UninitializedInstance(className));
        }
    }

    static NewArrayOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction22c instr = (Instruction22c) instruction;
        int destRegister = instr.getRegisterA();
        int dimensionRegister = instr.getRegisterB();

        // [[Lsome_class;
        String typeReference = ReferenceUtil.getReferenceString(instr.getReference());

        return new NewArrayOp(address, opName, childAddress, typeReference, destRegister, dimensionRegister, vm);
    }
    private final int destRegister;
    private final int dimensionRegister;
    private final String typeReference;

    private final VirtualMachine vm;

    private NewArrayOp(int address, String opName, int childAddress, String typeReference, int destRegister,
                    int dimensionRegister, VirtualMachine vm) {
        super(address, opName, childAddress);

        this.typeReference = typeReference;
        this.destRegister = destRegister;
        this.dimensionRegister = dimensionRegister;
        this.vm = vm;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        String className = typeReference.replace("[", "");
        int dimensions = (int) mctx.peekRegister(dimensionRegister);

        Object instance = null;
        if (vm.isClassDefinedLocally(className)) {
            instance = Array.newInstance(UninitializedInstance.class, dimensions);
            populateLocalTypeArray(instance, className);
        } else {
            String javaClassName = null;
            if (SmaliClassUtils.isPrimitiveType(className)) {
                javaClassName = SmaliClassUtils.smaliPrimitiveToJavaWrapper(className);
            } else {
                javaClassName = SmaliClassUtils.smaliClassToJava(className);
            }

            try {
                Class<?> klazz = ClassUtils.getClass(javaClassName);
                instance = Array.newInstance(klazz, dimensions);
            } catch (NegativeArraySizeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        mctx.assignRegister(destRegister, instance);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(destRegister).append(", r").append(dimensionRegister).append(", ").append(typeReference);

        return sb.toString();
    }
}
