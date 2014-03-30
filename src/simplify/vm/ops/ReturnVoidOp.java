package simplify.vm.ops;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;

import simplify.Main;
import simplify.vm.MethodContext;

public class ReturnVoidOp extends Op {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    static ReturnVoidOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        return new ReturnVoidOp(address, opName);
    }

    ReturnVoidOp(int address, String opName) {
        super(address, opName, new int[0]);
    }

    @Override
    public int[] execute(MethodContext mctx) {
        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        return sb.toString();
    }

}
