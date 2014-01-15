package simplify.exec.instruction;

import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

import simplify.exec.MethodExecutionContext;
import simplify.exec.UnknownValue;

public class NewInstanceInstruction {

    /*
     * Use an actual new instance for framework classes, but since reflection isn't available for dex classes, and we
     * don't maintain class state, just use UnknownValue().
     */
    public static void execute(MethodExecutionContext ectx, Instruction21c instruction, int index) {
        String type = ((TypeReference) instruction.getReference()).toString();

        Class<?> clazz = null;
        Object value = new UnknownValue();
        try {
            String classStr = type.substring(1, type.length() - 1).replaceAll("/", ".");
            clazz = Class.forName(classStr);
            value = clazz.newInstance();
        } catch (Exception e) {
        }

        ectx.addRegister(instruction.getRegisterA(), type, value, index);
    }

}
