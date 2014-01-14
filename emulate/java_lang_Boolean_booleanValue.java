package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;

public class java_lang_Boolean_booleanValue implements EmulatedMethod {

    public void execute(MethodExecutionContext ectx) {
        RegisterStore thiz = ectx.peekRegister(0);
        RegisterStore result = new RegisterStore("Z", thiz.getValue());

        ectx.setReturnRegister(result);
    }

}
