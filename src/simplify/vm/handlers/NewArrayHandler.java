package simplify.vm.handlers;

import org.jf.dexlib2.iface.instruction.Instruction;

import simplify.vm.MethodContext;

public class NewArrayHandler extends OpHandler {

    static NewArrayHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        return new NewArrayHandler(address, opName, childAddress);
    }

    private NewArrayHandler(int address, String opName, int childAddress) {
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
