package org.cf.smalivm.emulate;

import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_forName implements ExecutionContextMethod {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_forName.class.getSimpleName());

    private static final String RETURN_TYPE = "Ljava/lang/Class;";

    private final Set<VirtualException> exceptions;
    private SideEffect.Level level;

    java_lang_Class_forName() {
        exceptions = new HashSet<VirtualException>();
        level = SideEffect.Level.NONE;
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    private void reflectRealMethod(MethodState mState, String javaClassName) throws ClassNotFoundException {
        try {
            Class<?> value = Class.forName(javaClassName);
            mState.assignReturnRegister(value, RETURN_TYPE);
        } catch (ClassNotFoundException e) {
            // Happens if JVM doesn't have class. Typo in safe classes definitions?
            throw e;
        }
    }

    private void setException(VirtualException exception) {
        exceptions.add(exception);
    }

    @Override
    public void execute(VirtualMachine vm, ExecutionContext ectx) throws ClassNotFoundException {
        MethodState mState = ectx.getMethodState();
        String javaClassName = (String) mState.peekParameter(0).getValue();
        String className = SmaliClassUtils.javaClassToSmali(javaClassName);

        /*
         * Reflect Class.forName if class is safe. Otherwise, emulate Class.forName with local classes.
         */
        if (MethodReflector.isSafe(className)) {
            reflectRealMethod(mState, javaClassName);
        } else {
            ClassManager classManager = vm.getClassManager();
            if (classManager.isLocalClass(className)) {
                // Class.forName will statically initialize a class
                if (!ectx.isClassInitialized(className)) {
                    ectx.staticallyInitializeClassIfNecessary(className);
                    level = ectx.getClassSideEffectLevel(className);
                }
                mState.assignReturnRegister(new LocalClass(className), RETURN_TYPE);
            } else {
                setException(new VirtualException(ClassNotFoundException.class, className));
            }
        }
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return level;
    }

}
