package simplify.exec.instruction;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

import simplify.Main;
import simplify.exec.MethodExecutionContext;
import simplify.exec.UnknownValue;

public class NewInstanceInstruction {

    private static Logger log = Logger.getLogger(Main.class.getSimpleName());

    /*
     * Use an actual new instance for framework classes, but since reflection isn't available for Smali classes, and we
     * don't maintain class state, just use UnknownValue().
     */
    public static void execute(MethodExecutionContext ectx, Instruction21c instruction, int index) {
        String type = ((TypeReference) instruction.getReference()).toString();

        // Class<?> clazz = null;
        Object value = new UnknownValue();
        // try {
        // clazz = SmaliClassUtils.getClass(type);
        // value = clazz.newInstance();
        //
        // } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
        // log.warning("Unable to new-instance. " + ex);
        // }

        ectx.addRegister(instruction.getRegisterA(), type, value, index);
    }
}
