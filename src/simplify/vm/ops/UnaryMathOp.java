package simplify.vm.ops;

import org.jf.dexlib2.iface.instruction.Instruction;

import simplify.vm.MethodContext;

public class UnaryMathOp extends Op {

    static UnaryMathOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        return new UnaryMathOp(address, opName, childAddress);
    }

    UnaryMathOp(int address, String opName, int childAddress) {
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
