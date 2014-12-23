package org.cf.smalivm.emulate;

import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_forName implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_forName.class.getSimpleName());

    private static final String UNKNOWN_TYPE = "Ljava/lang/Class;";

    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        // No checks because emulated methods require all known args.
        String javaClassName = (String) mState.peekParameter(0);
        String className = SmaliClassUtils.javaClassToSmali(javaClassName);

        /*
         * Reflect Class.forName if class is safe. Otherwise, simulate Class.forName with local classes.
         */
        if (MethodReflector.isSafe(className)) {
            try {
                Class<?> value = Class.forName(javaClassName);
                mState.assignReturnRegister(value);
            } catch (Exception e) {
                // Happens if JVM doesn't have class, which means there's probably a typo in safe classes definitions.
                mState.assignReturnRegister(new UnknownValue(UNKNOWN_TYPE));
                throw e;
            }
        } else {
            SmaliClassManager classManager = vm.getClassManager();
            if (classManager.isLocalClass(className)) {
                // TODO: easy, determine if dalvik clinit's for class.forname
                mState.assignReturnRegister(new LocalClass(className));
            } else {
                // TODO: fill with convincing stack trace
                mState.assignReturnRegister(new UnknownValue(UNKNOWN_TYPE));
                throw new ClassNotFoundException(className);
            }
        }
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
