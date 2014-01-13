package simplify.emulate;

import simplify.exec.MethodExecutionContext;

public class java_lang_Integer_init implements EmulatedVirtualMethod {
    public void execute(MethodExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        Object value = ectx.peekRegisterValue(paramStart + 1);

        ectx.updateOrAddRegister(paramStart, "Ljava/lang/Integer;", value, 0);
    }
}