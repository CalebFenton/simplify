package org.cf.smalivm.emulate;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.VirtualClass;
import org.cf.util.ClassNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class java_lang_Class_forName extends ExecutionContextMethod {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_forName.class.getSimpleName());

    private static final String RETURN_TYPE = CommonTypes.CLASS;

    @Override
    public void execute(VirtualMachine vm, Op op, ExecutionContext context) {
        MethodState mState = context.getMethodState();
        String binaryClassName = (String) mState.peekParameter(0).getValue();
        String className = ClassNameUtils.binaryToInternal(binaryClassName);

        Class<?> value;
        try {
            if (vm.getConfiguration().isSafe(className)) {
                value = Class.forName(binaryClassName);
            } else {
                ClassLoader classLoader = vm.getClassLoader();
                value = classLoader.loadClass(binaryClassName);
                /*
                 * While the VM's class loader provides actual Java classes for local Smali classes, the field values
                 * shouldn't be used. To this end, static initialization is done locally as well as when the class
                 * is loaded and only the local values are used.
                 * Note: this is done *after* trying to load the class in case there's an exception
                 */
                VirtualClass virtualClass;
                try {
                    virtualClass = vm.getClassManager().getVirtualClass(className);
                } catch (RuntimeException e) {
                    throw new ClassNotFoundException();
                }
                if (!context.isClassInitialized(virtualClass) && !className.equals("Lorg/cf/obfuscated/Reflection;")) {
                    context.staticallyInitializeClassIfNecessary(virtualClass);
                    level = context.getClassSideEffectLevel(virtualClass);
                }
            }
            mState.assignReturnRegister(value, RETURN_TYPE);
        } catch (ClassNotFoundException e) {
            Throwable exception = vm.getExceptionFactory().build(op, ClassNotFoundException.class, binaryClassName);
            setException(exception);
        }
    }

}
