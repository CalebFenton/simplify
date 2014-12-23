package org.cf.smalivm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.TypeUtil;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ConfigLoader;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodReflector {

    private static Logger log = LoggerFactory.getLogger(MethodReflector.class.getSimpleName());

    private static final String SAFE_CLASSES_PATH = "safe_classes.cfg";
    private static final String SAFE_METHODS_PATH = "safe_methods.cfg";
    private static final String UNSAFE_METHODS_PATH = "unsafe_methods.cfg";

    private static Set<String> SafeClasses;
    private static Set<String> SafeMethods;
    private static Set<String> UnsafeMethods;

    static {
        try {
            loadSafeClasses();
            loadSafeMethods();
            loadUnsafeMethods();
        } catch (Exception e) {
            log.warn("Error loading safe class definitions.", e);
        }
    }

    public static boolean canReflect(String typeDescriptor) {
        return isSafe(typeDescriptor);
    }

    public static boolean isSafe(String typeDescriptor) {
        String[] parts = typeDescriptor.split("->");
        String className = parts[0];

        if (SafeClasses.contains(className) && !UnsafeMethods.contains(typeDescriptor)) {
            return true;
        }

        if (parts.length > 1) {
            // It's a method name
            if (SafeMethods.contains(typeDescriptor)) {
                return true;
            }
        }

        return false;
    }

    private static void loadSafeClasses() throws IOException {
        // Methods from safe classes must not have any side effects, e.g. any IO.
        List<String> lines = ConfigLoader.loadConfig(SAFE_CLASSES_PATH);
        SafeClasses = new HashSet<String>(lines);
    }

    private static void loadSafeMethods() throws IOException {
        List<String> lines = ConfigLoader.loadConfig(SAFE_METHODS_PATH);
        SafeMethods = new HashSet<String>(lines);
    }

    private static void loadUnsafeMethods() throws IOException {
        List<String> lines = ConfigLoader.loadConfig(UNSAFE_METHODS_PATH);
        UnsafeMethods = new HashSet<String>(lines);
    }

    private final String className;
    private final boolean isStatic;
    private final String methodDescriptor;
    private final String methodName;
    private final List<String> parameterTypes;
    private final String returnType;

    public MethodReflector(String methodDescriptor, String returnType, List<String> parameterTypes, boolean isStatic) {
        this.methodDescriptor = methodDescriptor;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.isStatic = isStatic;

        String[] parts = methodDescriptor.split("->");
        // ClassUtils expects "Ljava.lang.Class;"
        className = parts[0].replaceAll("/", ".");
        methodName = parts[1].substring(0, parts[1].indexOf("("));
    }

    public void reflect(MethodState calleeContext) {
        if (log.isDebugEnabled()) {
            log.debug("Reflecting " + methodDescriptor + " with context:\n" + calleeContext);
        }

        Object result = null;
        try {
            // Class<?> clazz = ClassUtils.getClass(className, false);
            // String leading 'L' and trailing ';' from smali type descriptor
            Class<?> clazz = Class.forName(className.substring(1, className.length() - 1));
            Object[] args = getArguments(calleeContext);
            if ("<init>".equals(methodName)) {
                // This class is used by the JVM to do instance initialization, i.e. newInstance. Can't just reflect it.
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting " + methodDescriptor + ", clazz=" + clazz + " args=" + Arrays.toString(args));
                }
                result = ConstructorUtils.invokeConstructor(clazz, args);
                calleeContext.assignParameter(0, result); // kind of a hack, just store newly init'ed value here
            } else {
                if (isStatic) {
                    if (log.isDebugEnabled()) {
                        log.debug("Reflecting " + methodDescriptor + ", clazz=" + clazz + " args="
                                        + Arrays.toString(args));
                    }
                    result = MethodUtils.invokeStaticMethod(clazz, methodName, args);
                } else {
                    Object target = calleeContext.peekRegister(0);
                    if (log.isDebugEnabled()) {
                        log.debug("Reflecting " + methodDescriptor + ", target=" + target + " args="
                                        + Arrays.toString(args));
                    }
                    result = MethodUtils.invokeMethod(target, methodName, args);
                }
            }
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | SecurityException
                        | InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
            result = new UnknownValue(returnType);
            if (log.isWarnEnabled()) {
                log.warn("Failed to reflect " + methodDescriptor);
            }

            if (log.isDebugEnabled()) {
                log.debug("Stack trace:", e);
            }
        }

        boolean returnsVoid = returnType.equals("V");
        if (!returnsVoid) {
            calleeContext.assignReturnRegister(result);
        }
    }

    private Object[] getArguments(MethodState mState) {
        int offset = 0;
        if (!isStatic) {
            // First element in context will be instance reference if non-static method.
            offset = 1;
        }

        int size = parameterTypes.size() - offset;
        Object[] args = new Object[size];
        for (int i = offset; i < mState.getRegisterCount(); i++) {
            Object arg = mState.peekParameter(i);
            String parameterType = parameterTypes.get(i);
            if (null != arg) {
                // In Dalvik, integer is overloaded and may represent a few primitives.
                String argType = TypeUtil.getValueType(arg);
                if (SmaliClassUtils.isPrimitiveOrWrapperType(argType)) {
                    arg = Utils.castToPrimitiveWrapper(arg, parameterType);
                }
            }
            args[i - offset] = arg;

            if ("J".equals(parameterType) || "D".equals(parameterType)) {
                // Long tried every diet but is still fat and takes 2 registers. Could be thyroid.
                i++;
            }
        }

        return args;
    }

}
