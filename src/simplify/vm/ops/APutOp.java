package simplify.vm.ops;

import java.lang.reflect.Array;
import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

import simplify.Main;
import simplify.vm.MethodContext;

public class APutOp extends Op {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    static APutOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction23x instr = (Instruction23x) instruction;
        int valueRegister = instr.getRegisterA();
        int arrayRegister = instr.getRegisterB();
        int indexRegister = instr.getRegisterC();

        return new APutOp(address, opName, childAddress, valueRegister, arrayRegister, indexRegister);
    }
    private final int arrayRegister;
    private final int indexRegister;

    private final int valueRegister;

    public APutOp(int address, String opName, int childAddress, int valueRegister, int arrayRegister, int indexRegister) {
        super(address, opName, childAddress);

        this.valueRegister = valueRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        Object value = mctx.readRegister(valueRegister);
        Object array = mctx.readRegister(arrayRegister);
        int index = (int) mctx.readRegister(indexRegister);

        Array.set(array, index, value);

        // This is only to let the optimizer know the array was modified.
        mctx.assignRegister(arrayRegister, array);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(valueRegister).append(" r").append(arrayRegister).append(" r").append(indexRegister);

        return sb.toString();
    }

}
