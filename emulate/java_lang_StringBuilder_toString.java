package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;
import simplify.exec.UnknownValue;

public class java_lang_StringBuilder_toString implements EmulatedMethod {

    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        RegisterStore thiz = ectx.peekRegister(paramStart);

        if (thiz.getValue() instanceof UnknownValue) {
            ectx.setReturnRegister(new RegisterStore("Ljava/lang/String;", new UnknownValue()));
            return;
        }

        // Modify ourselves with the new value
        StringBuilder value = (StringBuilder) thiz.getValue();
        String result = value.toString();
        ectx.setReturnRegister(new RegisterStore("Ljava/lang/String;", result));
    }

}
