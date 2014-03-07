package simplify.vm.handlers;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;

import simplify.vm.MethodContext;
import simplify.vm.VirtualMachine;

public class NewArrayOpHandler extends OpHandler {

    static NewArrayOpHandler create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction22c instr = (Instruction22c) instruction;
        int destRegister = instr.getRegisterA();
        int arraySize = instr.getRegisterA();

        // [[Lsome_class;
        String typeRef = ReferenceUtil.getReferenceString(instr.getReference());

        // int[] dimA = {12};
        // int[][] twoDimA = (int[][]) Array.newInstance(int[].class, dimA);

        // Object instance = null;
        // if (vm.isClassDefinedLocally(className)) {
        // instance = new SmaliClassInstance(className);
        // } else {
        // instance = new UninitializedInstance(className);
        // }

        return new NewArrayOpHandler(address, opName, childAddress);
    }

    private NewArrayOpHandler(int address, String opName, int childAddress) {
        super(address, opName, childAddress);

    }

    @Override
    public int[] execute(MethodContext mctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

}
