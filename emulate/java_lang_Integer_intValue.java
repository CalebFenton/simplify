package simplify.emulate;

import simplify.exec.ExecutionContext;

public class java_lang_Integer_intValue implements EmulatedVirtualMethod {

    @Override
    public void execute(ExecutionContext ectx) {
        int paramStart = ectx.getParameterStart();
        Object value = ectx.getRegisterValue(paramStart, 0);

        ectx.addRegister(paramStart, "I", value, 0);
        ectx.setReturnRegister(paramStart, 0);
    }
}
