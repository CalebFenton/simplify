package org.cf.smalivm.emulate;

import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_forName implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_forName.class.getSimpleName());

    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        // No checks because emulated methods require all known args.
        String javaClassName = (String) mState.peekParameter(0);
        String className = SmaliClassUtils.javaClassToSmali(javaClassName);

        if (!MethodReflector.isSafe(className)) {
            mState.assignReturnRegister(new UnknownValue(className));
            return;
        }

        try {
            Class<?> value = Class.forName(javaClassName);
            mState.assignReturnRegister(value);
        } catch (Exception e) {
            mState.assignReturnRegister(new UnknownValue("Ljava/lang/Class;"));
            throw e;
        }
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
