package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;

public class java_lang_Integer_intValue implements EmulatedMethod {

    public void execute(MethodExecutionContext ectx) {
        RegisterStore thiz = ectx.peekRegister(0);
        RegisterStore result = new RegisterStore("I", thiz.getValue());

        ectx.setReturnRegister(result);
    }

}
