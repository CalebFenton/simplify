package simplify.exec.instruction;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

import simplify.Main;
import simplify.exec.MethodExecutionContext;

public class CheckCastInstruction {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static void execute(MethodExecutionContext ectx, Instruction21c instruction, int index) {
        int registerA = instruction.getRegisterA();
        TypeReference typeRef = (TypeReference) instruction.getReference();
        String type = typeRef.getType();
        Object value = ectx.getRegisterValue(registerA, index);

        log.info("CheckCast to " + type + " of " + value);
        ectx.addRegister(registerA, type, value, index);
    }

}
