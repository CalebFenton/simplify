package org.cf.smalivm.emulate;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.smali.ClassManager;
import org.cf.util.ClassNameUtils;
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

    private @Nonnull Class<?> forSafeClass(MethodState mState, String binaryName) throws ClassNotFoundException {
        return Class.forName(binaryName);
    }

    private @Nonnull Class<?> forUnsafeClass(ExecutionContext ectx, MethodState mState, String internalName,
                    ClassLoader classLoader, ClassManager classManager) throws ClassNotFoundException {
        Class<?> klazz = classLoader.loadClass(internalName);

        if (classManager.isLocalClass(internalName)) {
        }

        return klazz;
    }

    private void setException(VirtualException exception) {
        exceptions.add(exception);
    }

    @Override
    public void execute(VirtualMachine vm, ExecutionContext ectx) {
        MethodState mState = ectx.getMethodState();
        String binaryName = (String) mState.peekParameter(0).getValue();
        String className = ClassNameUtils.binaryToInternal(binaryName);

        ClassManager classManager = vm.getClassManager();
        if (classManager.isLocalClass(className)) {
            // If it's not local, it's not framework. That only leaves JVM classes not part of Android.
            // No. Leave the JVM alone.
            setException(new VirtualException(ClassNotFoundException.class, binaryName));
        }

        Class<?> value = null;
        try {
            if (vm.getConfiguration().isSafe(className)) {
                value = Class.forName(binaryName);
            } else {
                ClassLoader classLoader = vm.getClassLoader();
                value = classLoader.loadClass(binaryName);
                /*
                 * While the VM class loader provides actual Java classes for local Smali classes, the fields and
                 * methods should never be used. To this end, static initialization is done locally as well as when the
                 * class is loaded and only the local values are used.
                 * Note: this is done after trying to load the class in case there's an exception
                 */
                if (!ectx.isClassInitialized(className)) {
                    ectx.staticallyInitializeClassIfNecessary(className);
                    level = ectx.getClassSideEffectLevel(className);
                }
            }
            mState.assignReturnRegister(value, RETURN_TYPE);
        } catch (ClassNotFoundException e) {
            setException(new VirtualException(ClassNotFoundException.class, binaryName));
        }
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return level;
    }

}
