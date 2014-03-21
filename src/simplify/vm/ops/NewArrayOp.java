package simplify.vm.ops;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;

import simplify.Utils;
import simplify.vm.MethodContext;
import simplify.vm.VirtualMachine;

public class NewArrayOp extends Op {

    static NewArrayOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction22c instr = (Instruction22c) instruction;
        int destRegister = instr.getRegisterA();
        int dimensionRegister = instr.getRegisterB();

        // [[Lsome_class;
        String typeReference = ReferenceUtil.getReferenceString(instr.getReference());

        String baseClassName = typeReference.replace("[", "");
        boolean isLocalClass = vm.isClassDefinedLocally(baseClassName);

        return new NewArrayOp(address, opName, childAddress, destRegister, dimensionRegister, typeReference,
                        isLocalClass);
    }

    private final int destRegister;
    private final int dimensionRegister;
    private final String typeReference;
    private final boolean isLocalClass;

    private NewArrayOp(int address, String opName, int childAddress, int destRegister, int dimensionRegister,
                    String typeReference, boolean isLocalClass) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.dimensionRegister = dimensionRegister;
        this.typeReference = typeReference;
        this.isLocalClass = isLocalClass;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        int dimension = (int) mctx.peekRegister(dimensionRegister);
        Object instance = null;
        try {
            instance = Utils.getArrayInstanceFromSmaliTypeReference(typeReference, dimension, isLocalClass);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
