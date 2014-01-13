package simplify.exec.instruction;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

import simplify.Simplifier;
import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;

public class MoveInstruction {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    public static void execute(MethodExecutionContext ectx, TwoRegisterInstruction instruction, int index) {
        int registerA = instruction.getRegisterA();
        int registerB = instruction.getRegisterB();
        // log.finer("moving to " + registerA + " from " + registerB);
        RegisterStore rs = ectx.getRegister(registerB, index);

        ectx.addRegister(registerA, rs);
    }

}
