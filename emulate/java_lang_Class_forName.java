package simplify.emulate;

import java.util.logging.Logger;

import simplify.Simplifier;
import simplify.exec.MethodExecutionContext;
import simplify.exec.UnknownValue;

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
            log.warning(e.getMessage());
            ectx.addRegister(paramStart, "?", new UnknownValue(), 0);
        }

        ectx.setReturnRegister(paramStart, 0);
    }

}
