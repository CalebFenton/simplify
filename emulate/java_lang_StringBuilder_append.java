package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;
import simplify.exec.UnknownValue;

public class java_lang_StringBuilder_append implements EmulatedMethod {

    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        RegisterStore thiz = ectx.peekRegister(paramStart);
        Object param1 = ectx.peekRegisterValue(paramStart + 1);

        if ((thiz.getValue() instanceof UnknownValue) || (param1 instanceof UnknownValue)) {
            thiz.setValue(new UnknownValue());
            return;
        }

        // Modify ourselves with the new value
        StringBuilder value = (StringBuilder) thiz.getValue();
        value.append(param1);

        // And return ourselves because it's expected
        ectx.setReturnRegister(thiz);
    }

}
