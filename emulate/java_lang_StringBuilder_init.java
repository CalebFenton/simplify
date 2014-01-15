package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;
import simplify.exec.UnknownValue;

public class java_lang_StringBuilder_init implements EmulatedMethod {

    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        RegisterStore thiz = ectx.peekRegister(paramStart);
        Object value = ectx.peekRegisterValue(paramStart + 1);

        if ((thiz.getValue() instanceof UnknownValue) || (value instanceof UnknownValue)) {
            thiz.setValue(new UnknownValue());
            return;
        }

        // Modify ourselves with the new value
        thiz.setValue(value);
    }

}