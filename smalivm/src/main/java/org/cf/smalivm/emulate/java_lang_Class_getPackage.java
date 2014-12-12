package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.EmulatedType;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_getPackage implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_getPackage.class.getSimpleName());

    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        Object classValue = mState.peekParameter(0);
        Object packageValue;
        if (classValue instanceof LocalClass) {
            String smaliType = ((LocalClass) classValue).getName();
            String packageName = SmaliClassUtils.getPackageName(smaliType);
            EmulatedType emulatedPackage = new EmulatedType("Ljava/lang/Package;");
            emulatedPackage.setExtra(packageName);
            packageValue = emulatedPackage;
        } else if (classValue instanceof Class) {
            packageValue = ((Class<?>) classValue).getPackage();
        } else {
            assert classValue instanceof UnknownValue;
            packageValue = new UnknownValue("Ljava/lang/Package;");
        }

        mState.assignReturnRegister(packageValue);
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
