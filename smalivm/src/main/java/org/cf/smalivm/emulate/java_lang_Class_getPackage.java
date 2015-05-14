package org.cf.smalivm.emulate;

import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.EmulatedType;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_getPackage implements MethodStateMethod {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_getPackage.class.getSimpleName());

    private static final String RETURN_TYPE = "Ljava/lang/Package;";

    private final Set<VirtualException> exceptions;

    java_lang_Class_getPackage() {
        exceptions = new HashSet<VirtualException>();
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    @Override
    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        Object classValue = mState.peekParameter(0).getValue();
        Object packageValue;
        if (classValue instanceof LocalClass) {
            String smaliType = ((LocalClass) classValue).getName();
            String packageName = SmaliClassUtils.getPackageName(smaliType);
            EmulatedType emulatedPackage = new EmulatedType(RETURN_TYPE);
            emulatedPackage.setExtra(packageName);
            packageValue = emulatedPackage;
        } else if (classValue instanceof Class) {
            packageValue = ((Class<?>) classValue).getPackage();
        } else {
            assert classValue instanceof UnknownValue;
            packageValue = new UnknownValue();
        }

        mState.assignReturnRegister(packageValue, RETURN_TYPE);
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
