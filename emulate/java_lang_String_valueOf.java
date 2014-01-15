package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;
import simplify.exec.UnknownValue;

public class java_lang_String_valueOf implements EmulatedMethod {

    @Override
    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        RegisterStore param1 = ectx.peekRegister(paramStart);

        if (param1.getValue() instanceof UnknownValue) {
            ectx.setReturnRegister(new RegisterStore("Ljava/lang/String;", new UnknownValue()));
            return;
        }

        RegisterStore result = new RegisterStore("Ljava/lang/String;", String.valueOf(param1.getValue()));
        ectx.setReturnRegister(result);
    }

}
