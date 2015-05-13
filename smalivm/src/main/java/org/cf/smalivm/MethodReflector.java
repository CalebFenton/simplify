package org.cf.smalivm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ConfigLoader;
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

    private final String smaliClassName;
    private final String javaIshClassName;
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
        smaliClassName = parts[0];
        javaIshClassName = smaliClassName.replaceAll("/", ".");
        methodName = parts[1].substring(0, parts[1].indexOf("("));
    }

    public void reflect(MethodState calleeContext) {
        if (log.isDebugEnabled()) {
            log.debug("Reflecting {} with context:\n{}", methodDescriptor, calleeContext);
        }

        Object resultValue = null;
        try {
            // Class<?> clazz = ClassUtils.getClass(javaIshClassName, false);
            // Strip leading 'L' and trailing ';' from smali type descriptor
            Class<?> clazz = Class.forName(javaIshClassName.substring(1, javaIshClassName.length() - 1));
            Object[] args = getArguments(calleeContext);
            if ("<init>".equals(methodName)) {
                // This class is used by the JVM to do instance initialization, i.e. newInstance. Can't just reflect it.
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting {}, clazz={} args={}", methodDescriptor, clazz, Arrays.toString(args));
                }
                resultValue = ConstructorUtils.invokeConstructor(clazz, args);
                // kind of a hack. store newly init'ed value here
                calleeContext.assignParameter(0, new HeapItem(resultValue, smaliClassName));
            } else {
                if (isStatic) {
                    if (log.isDebugEnabled()) {
                        log.debug("Reflecting {}, clazz={} args={}", methodDescriptor, clazz, Arrays.toString(args));
                    }
                    resultValue = MethodUtils.invokeStaticMethod(clazz, methodName, args);
                } else {
                    HeapItem targetItem = calleeContext.peekRegister(0);
                    if (log.isDebugEnabled()) {
                        log.debug("Reflecting {}, target={} args={}", methodDescriptor, targetItem,
                                        Arrays.toString(args));
                    }
                    resultValue = MethodUtils.invokeMethod(targetItem.getValue(), methodName, args);
                }
            }
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            resultValue = new UnknownValue();
            if (log.isWarnEnabled()) {
                log.warn("Failed to reflect {}", methodDescriptor);
            }

            if (log.isDebugEnabled()) {
                log.debug("Stack trace:", e);
            }
        }

        boolean returnsVoid = "V".equals(returnType);
        if (!returnsVoid) {
            HeapItem resultItem = new HeapItem(resultValue, returnType);
            calleeContext.assignReturnRegister(resultItem);
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
            HeapItem argItem = mState.peekParameter(i);
            Object arg = argItem.getValue();
            String parameterType = parameterTypes.get(i);
            if (null != arg) {
                // In Dalvik, I type is overloaded and can represent multiple primitives, e.g. B, S, C
                if (argItem.isPrimitiveOrWrapper()) {
                    arg = Utils.castToPrimitiveWrapper(arg, parameterType);
                }
            }
            args[i - offset] = arg;

            if (Utils.getRegisterSize(parameterType) == 2) {
                // Long tried every diet but is still fat and takes 2 registers. Could be thyroid.
                i++;
            }
        }

        return args;
    }

}
