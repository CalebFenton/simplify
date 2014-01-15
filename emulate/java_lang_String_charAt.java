package simplify.emulate;

import simplify.exec.MethodExecutionContext;
import simplify.exec.RegisterStore;
import simplify.exec.UnknownValue;

public class java_lang_String_charAt implements EmulatedMethod {

    @Override
    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        RegisterStore thiz = ectx.peekRegister(paramStart);
        RegisterStore param1 = ectx.peekRegister(paramStart + 1);

        if ((thiz.getValue() instanceof UnknownValue) || (param1.getValue() instanceof UnknownValue)) {
            ectx.setReturnRegister(new RegisterStore("C", new UnknownValue()));
            return;
        }

        String value = (String) thiz.getValue();
        Integer index = (Integer) param1.getValue();
        RegisterStore result = new RegisterStore("C", value.charAt(index));
        ectx.setReturnRegister(result);
    }

}
