package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;
import simplify.exec.UnknownValue;

public class java_lang_Boolean_booleanValue implements EmulatedMethod {

    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        RegisterStore thiz = ectx.peekRegister(paramStart);

        if (thiz.getValue() instanceof UnknownValue) {
            ectx.setReturnRegister(new RegisterStore("Z", new UnknownValue()));
            return;
        }

        Boolean value = (Boolean) thiz.getValue();
        RegisterStore result = new RegisterStore("Z", value.booleanValue());
        ectx.setReturnRegister(result);
    }

}
