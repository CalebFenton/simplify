package org.cf.smalivm;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodReflector {

    private static Logger log = LoggerFactory.getLogger(MethodReflector.class.getSimpleName());

    private static List<String> SafeClasses;
    private static List<String> SafeMethods;
    static {
        // No method from any safe class should ever have any side effects (e.g. IO)
        // TODO: get exhaustive list
        SafeClasses = new ArrayList<String>();
        SafeClasses.add("Ljava/lang/Object;");
        SafeClasses.add("Ljava/lang/Boolean;");
        SafeClasses.add("Ljava/lang/Byte;");
        SafeClasses.add("Ljava/lang/Character;");
        SafeClasses.add("Ljava/lang/CharSequence;");
        SafeClasses.add("Ljava/lang/Double;");
        SafeClasses.add("Ljava/lang/Float;");
        SafeClasses.add("Ljava/lang/Integer;");
        SafeClasses.add("Ljava/lang/Long;");
        SafeClasses.add("Ljava/lang/Number;");
        SafeClasses.add("Ljava/lang/Short;");
        SafeClasses.add("Ljava/lang/String;");
        SafeClasses.add("Ljava/lang/System;");

        SafeClasses.add("Ljava/lang/StringBuffer;");
        SafeClasses.add("Ljava/lang/StringBuilder;");

        SafeClasses.add("Ljava/math/BigDecimal;");
        SafeClasses.add("Ljava/math/BigInteger;");

        SafeClasses.add("Ljava/util/Map;");
        SafeClasses.add("Ljava/util/HashMap;");
        SafeClasses.add("Ljava/util/Hashtable;");

        SafeClasses.add("Ljava/util/List;");
        SafeClasses.add("Ljava/util/ArrayList;");

        SafeClasses.add("Ljava/util/Set;");
        SafeClasses.add("Ljava/util/HashSet;");

        SafeClasses.add("Lorg/json/JSONObject;");
        SafeClasses.add("Lorg/json/JSONArray;");

        SafeClasses.add("Ljavax/crypto/Cipher;");
        SafeClasses.add("Ljavax/crypto/spec/SecretKeySpec;");
        SafeClasses.add("Ljavax/crypto/spec/IvParameterSpec;");
        SafeClasses.add("Ljava/security/Key;");
        SafeClasses.add("Ljava/security/spec/AlgorithmParameterSpec;");

        SafeMethods = new ArrayList<String>();
        SafeMethods.add("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;");
    }

    public static boolean canReflect(String methodDescriptor) {
        String[] parts = methodDescriptor.split("->");
        String className = parts[0];

        if (SafeClasses.contains(className)) {
            return true;
        }

        if (SafeMethods.contains(methodDescriptor)) {
            return true;
        }

        return false;
    }

    public static boolean isWhitelisted(String typeDescriptor) {
        String[] parts = typeDescriptor.split("->");
        String className = parts[0];

        if (SafeClasses.contains(className)) {
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
        log.debug("Reflecting " + methodDescriptor + " with context:\n" + calleeContext);

        Object result = null;
        try {
            // Class<?> clazz = ClassUtils.getClass(className, false);
            // String leading 'L' and trailing ';' from smali type descriptor
            Class<?> clazz = Class.forName(className.substring(1, className.length() - 1));

            Object[] args = getArguments(calleeContext);
            if (methodName.equals("<init>")) {
                // This class is used by the JVM to do instance initialization, i.e. newInstance. Can't just reflect it.
                log.debug("Reflecting " + methodDescriptor + ", clazz=" + clazz + " args=" + Arrays.toString(args));
                result = ConstructorUtils.invokeConstructor(clazz, args);
                calleeContext.assignRegister(0, result);
            } else {
                if (isStatic) {
                    log.debug("Reflecting " + methodDescriptor + ", clazz=" + clazz + " args=" + Arrays.toString(args));
                    result = MethodUtils.invokeStaticMethod(clazz, methodName, args);
                } else {
                    Object target = calleeContext.peekRegister(0);
                    log.debug("Reflecting " + methodDescriptor + ", target=" + target + " args="
                                    + Arrays.toString(args));
                    result = MethodUtils.invokeMethod(target, methodName, args);
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            result = new UnknownValue(returnType);
            log.warn("Failed to reflect " + methodDescriptor);
            log.debug("Stack trace:", e);
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

        List<Object> args = new ArrayList<Object>();
        for (int i = offset; i < mState.getRegisterCount(); i++) {
            Object arg = mState.getParameter(i);
            String type = parameterTypes.get(i);
            if (type.equals("Z") || type.equals("Ljava/lang/Boolean;")) {
                // Booleans are represented in Smali and stored internally as integers. Convert to boolean.
                if (arg.getClass() == Integer.class) {
                    int intValue = (Integer) arg;
                    arg = intValue == 0 ? false : true;
                }
            }
            args.add(arg);

            if (type.equals("J") || type.equals("D")) {
                // Long tried every diet but is still fat and takes 2 registers. Could be thyroid.
                i++;
            }
        }

        return args.toArray();
    }

}
