package simplify.exec;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import simplify.Main;

public class MethodReflector {

    private static Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static List<String> SafeClasses;
    private static List<String> SafeMethods;
    static {
        SafeClasses = new ArrayList<String>();
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
        SafeClasses.add("Ljava/lang/StringBuffer;");
        SafeClasses.add("Ljava/lang/StringBuilder;");
        SafeClasses.add("Ljava/math/BigDecimal;");
        SafeClasses.add("Ljava/math/BigInteger;");

        SafeMethods = new ArrayList<String>();
        SafeMethods.add("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;");
    }

    public static boolean isSafeToReflect(String methodDescriptor) {
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

    public static void reflect(MethodExecutionContext ectx, List<? extends CharSequence> parameterTypes,
                    String methodDescriptor) {
        String[] parts = methodDescriptor.split("->");
        String className = dexClassToJava(parts[0]);
        String methodName = parts[1].substring(0, parts[1].indexOf("("));

        Object result = null;
        try {
            Class<?> clazz = Class.forName(className);
            Class<?>[] paramClasses = getParameterClasses(parameterTypes);
            Object[] args = getArguments(ectx);
            if (methodName.equals("<init>")) {
                // A call to this class must be interpted as a newInstance.
                result = getNewInstance(clazz, paramClasses, args);

                // This isn't a clone. It's a reference to the caller method's register store.
                RegisterStore rs = ectx.peekRegister(ectx.getParameterStart());
                rs.setValue(result);
            } else {
                Method targetMethod = clazz.getDeclaredMethod(methodName, paramClasses);
                result = invokeMethod(targetMethod, args);

                Class<?> returnType = targetMethod.getReturnType();
                if (!returnType.equals(Void.TYPE)) {
                    String resultType = javaClassToDex(returnType.getName());
                    ectx.setReturnRegister(new RegisterStore(resultType, result));
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private static Object invokeMethod(Method targetMethod, Object[] args) throws IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        boolean isStatic = (targetMethod.getModifiers() & Modifier.STATIC) != 0;
        Object target = null;
        if (!isStatic) {
            target = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        log.finer("reflecting " + targetMethod + " with " + target + " args=" + Arrays.toString(args));
        return targetMethod.invoke(target, args);
    }

    private static Object getNewInstance(Class<?> clazz, Class<?>[] paramClasses, Object[] args)
                    throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        // First parameter will be instance reference, not an argument.
        if (paramClasses.length > 0) {
            paramClasses = Arrays.copyOfRange(paramClasses, 1, paramClasses.length);
        }

        if (args.length > 0) {
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        Constructor<?> ctor = clazz.getConstructor(paramClasses);

        return ctor.newInstance(args);
    }

    private static Object[] getArguments(MethodExecutionContext ectx) {
        Object[] args = new Object[ectx.getRegisterCount()];
        for (int i = 0; i < args.length; i++) {
            args[i] = ectx.peekRegisterValue(i);
        }

        return args;
    }

    private static Class<?>[] getParameterClasses(List<? extends CharSequence> parameterTypes)
                    throws ClassNotFoundException {
        Class<?>[] classes = new Class<?>[parameterTypes.size()];
        for (int i = 0; i < parameterTypes.size(); i++) {
            String paramType = parameterTypes.get(i).toString();
            paramType = dexClassToJava(paramType);
            classes[i] = Class.forName(paramType);
        }

        return classes;
    }

    private static String dexClassToJava(String dexClassName) {
        return dexClassName.substring(1, dexClassName.length() - 1).replaceAll("/", ".");
    }

    private static String javaClassToDex(String javaClassName) {
        return "L" + javaClassName.replaceAll("\\.", "/") + ";";
    }
}
