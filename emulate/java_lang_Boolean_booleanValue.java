package simplify.emulate;

import simplify.exec.MethodExecutionContext;

public class java_lang_Boolean_booleanValue {

    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        Object value = ectx.getRegisterValue(paramStart, 0);

        ectx.addRegister(paramStart, "Z", value, 0);
        ectx.setReturnRegister(paramStart, 0);
    }

}
