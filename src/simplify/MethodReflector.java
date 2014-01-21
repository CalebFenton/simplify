package simplify;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;

import simplify.vm.MethodContext;
import simplify.vm.RegisterStore;

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

        SafeClasses.add("Ljava/util/Map;");
        SafeClasses.add("Ljava/util/HashMap;");
        SafeClasses.add("Ljava/util/List;");
        SafeClasses.add("Ljava/util/ArrayList;");

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

    public static void reflect(MethodContext mctx, MethodReference methodReference) {
        String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);
        boolean returnsVoid = methodReference.getReturnType().equals("V");

        log.finer("Reflecting " + methodDescriptor + " with context:\n" + mctx);

        Object result = null;
        try {
            Class<?> clazz = SmaliClassUtils.getClass(methodReference.getDefiningClass());

            Object[] args = getArguments(mctx);
            List<? extends CharSequence> paramTypes = methodReference.getParameterTypes();
            Class<?>[] paramClasses = new Class<?>[paramTypes.size()];
            for (int i = 0; i < paramClasses.length; i++) {
                String className = paramTypes.get(i).toString();
                paramClasses[i] = SmaliClassUtils.getClass(className);
            }

            String methodName = methodReference.getName();
            if (methodName.equals("<init>")) {
                // This class is used by the JVM to do instance initialization, i.e. newInstance. Can't just reflect it.
                result = getNewInstance(clazz, paramClasses, args);

                // This isn't a clone. It's a reference to the caller method's register store. This way any other
                // objects pointing to this register store also get updated.
                RegisterStore rs = mctx.peekRegister(mctx.getParameterStart());
                rs.setValue(result);
            } else {
                Method targetMethod = clazz.getDeclaredMethod(methodName, paramClasses);

                result = invokeMethod(targetMethod, args);

                if (!returnsVoid) {
                    mctx.setReturnRegister(new RegisterStore(methodReference.getReturnType(), result));
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
            // First parameter will be instance reference, not an actual argument.
            target = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        log.fine("Reflecting method " + targetMethod + " with " + target + " args=" + Arrays.toString(args));

        return targetMethod.invoke(target, args);
    }

    private static Object getNewInstance(Class<?> clazz, Class<?>[] paramClasses, Object[] args)
                    throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        // First parameter will be instance reference, not an actual argument.
        if (args.length > 0) {
            // paramClasses = Arrays.copyOfRange(paramClasses, 1, paramClasses.length);
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        log.fine("Reflecting newInstance of " + clazz.getName() + " with args=" + Arrays.toString(args));

        Constructor<?> ctor = clazz.getConstructor(paramClasses);

        return ctor.newInstance(args);
    }

    private static Object[] getArguments(MethodContext mctx) {
        System.out.println("get args, size=" + mctx.getRegisterCount());
        System.out.println("context: " + mctx);
        Object[] args = new Object[mctx.getRegisterCount()];
        for (int i = 0; i < args.length; i++) {
            args[i] = mctx.peekRegisterValue(i);
        }

        return args;
    }

}
