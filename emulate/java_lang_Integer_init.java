package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;

public class java_lang_Integer_init implements EmulatedMethod {

    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        Object value = ectx.peekRegisterValue(paramStart + 1);

        // Modify ourselves with the new value
        RegisterStore thiz = ectx.peekRegister(0);
        thiz.setValue(value);
    }

}