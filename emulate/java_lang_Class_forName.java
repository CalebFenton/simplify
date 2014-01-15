package simplify.emulate;

import java.util.logging.Logger;

import simplify.Simplifier;
import simplify.exec.MethodExecutionContext;

public class java_lang_Class_forName implements EmulatedMethod {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        String classStr = (String) ectx.getRegisterValue(paramStart, 0);

        Class<?> clazz = null;
        try {
            log.info("Class.forName(" + classStr + ")");
            clazz = Class.forName(classStr);
            ectx.addRegister(paramStart, "Ljava/lang/Class;", clazz, 0);
        } catch (ClassNotFoundException e) {
            // Class may be part of app, not part of framework.
            // Store a string of the class name here and just be mindful the register value could be a String or a
            // Class.
            ectx.addRegister(paramStart, "Ljava/lang/Class;", classStr, 0);
        }

        ectx.setReturnRegister(paramStart, 0);
    }

}
