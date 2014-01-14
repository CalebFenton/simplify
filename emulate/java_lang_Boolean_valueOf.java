package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;

public class java_lang_Boolean_valueOf implements EmulatedMethod {

    @Override
    public void execute(MethodExecutionContext ectx) {
        RegisterStore param1 = ectx.peekRegister(0);
        RegisterStore result = new RegisterStore("Ljava/lang/Boolean;", param1.getValue());

        ectx.setReturnRegister(result);
    }

}
