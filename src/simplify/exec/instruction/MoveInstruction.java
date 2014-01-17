package simplify.exec.instruction;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

import simplify.Main;
import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;

public class MoveInstruction {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static void execute(MethodExecutionContext ectx, TwoRegisterInstruction instruction, int index) {
        int registerA = instruction.getRegisterA();
        int registerB = instruction.getRegisterB();

        log.finer("moving r" + registerB + " to r" + registerA);

        RegisterStore rs = ectx.getRegister(registerB, index);
        ectx.addRegister(registerA, rs);
    }

}
