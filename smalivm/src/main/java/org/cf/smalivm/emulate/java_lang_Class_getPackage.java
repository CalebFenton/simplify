package org.cf.smalivm.emulate;

import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.EmulatedType;
import org.cf.smalivm.type.LocalInstance;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_getPackage implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_getPackage.class.getSimpleName());

    public void execute(MethodState mState) throws Exception {
        // No checks because emulated methods require all known args.
        LocalInstance localInstance = (LocalInstance) mState.peekParameter(0);
        String smaliType = localInstance.getType();
        String packageName = SmaliClassUtils.getPackageName(smaliType);
        EmulatedType emulatedPackage = new EmulatedType("Ljava/lang/Package;");
        emulatedPackage.setExtra(packageName);

        if (MethodReflector.isSafe(smaliType)) {
            String javaClassName = SmaliClassUtils.smaliClassToJava(smaliType);
            try {
                Class<?> klazz = Class.forName(javaClassName);
                Package value = klazz.getPackage();
                mState.assignReturnRegister(value);
            } catch (Exception e) {
                mState.assignReturnRegister(emulatedPackage);
                throw e;
            }
        } else {
            mState.assignReturnRegister(emulatedPackage);
        }
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
