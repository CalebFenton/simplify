package simplify;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
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

        SafeClasses.add("Ljava/util/Set;");
        SafeClasses.add("Ljava/util/HashSet;");

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

    private final String methodDescriptor;
    private final String methodName;
    private final String className;
    private final String returnType;
    private final boolean isStatic;
    private final List<String> parameterTypes;

    public MethodReflector(MethodReference methodReference, boolean isStatic) {
        methodDescriptor = ReferenceUtil.getMethodDescriptor(methodReference);
        methodName = methodReference.getName();

        // ClassUtils expects Lthis.class.Format;
        className = methodReference.getDefiningClass().replaceAll("/", ".");

        returnType = methodReference.getReturnType();
        this.isStatic = isStatic;

        List<? extends CharSequence> paramTypes = methodReference.getParameterTypes();
        parameterTypes = new ArrayList<String>(paramTypes.size());
        for (CharSequence type : paramTypes) {
            parameterTypes.add(type.toString());
        }
    }

    public void reflect(MethodContext mctx) {
        log.finer("Reflecting " + methodDescriptor + " with context:\n" + mctx);

        Object result = null;
        try {
            // Class<?> clazz = ClassUtils.getClass(className, false);
            Class<?> clazz = Class.forName(className.substring(1, className.length() - 1));

            Object target = getTarget(mctx, isStatic);
            Object[] args = getArguments(mctx, isStatic, parameterTypes);

            // List<Class<?>> paramClasses = ClassUtils.convertClassNamesToClasses(parameterTypes);
            log.fine("Reflecting " + methodDescriptor + ", target=" + target + " args=" + Arrays.toString(args)
                            + ", static=" + isStatic);
            if (methodName.equals("<init>")) {
                // This class is used by the JVM to do instance initialization, i.e. newInstance. Can't just reflect it.
                result = ConstructorUtils.invokeConstructor(clazz, args);

                // This isn't a clone. It's a reference to the caller method's register store. This way any other
                // objects pointing to this register store also get updated.
                RegisterStore registerStore = mctx.peekRegister(mctx.getParameterStart() - 1);
                registerStore.setValue(result);
            } else {
                if (isStatic) {
                    result = MethodUtils.invokeStaticMethod(clazz, methodName, args);
                } else {
                    result = MethodUtils.invokeMethod(target, methodName, args);
                }

                boolean returnsVoid = returnType.equals("V");
                if (!returnsVoid) {
                    mctx.setReturnRegister(new RegisterStore(returnType, result));
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Object getTarget(MethodContext mctx, boolean isStatic) {
        if (isStatic) {
            return null;
        }

        return mctx.peekRegisterValue(0);
    }

    private static Object[] getArguments(MethodContext mctx, boolean isStatic, List<String> parameterTypes) {
        int offset = 0;
        if (!isStatic) {
            // First element in context will be instance reference if non-static method.
            offset = 1;
        }

        List<Object> args = new ArrayList<Object>(mctx.getRegisterCount() - offset);
        // For reflected methods, there are no locals. Instance and arguments start at 0.
        // 0=[instance register or arg1], 1=[arg#], ...
        for (int i = offset; i < mctx.getRegisterCount(); i++) {
            RegisterStore registerStore = mctx.peekRegister(i);
            Object value = registerStore.getValue();

            String paramType = parameterTypes.get(i - offset);
            if (paramType.equals("Z") || paramType.equals("Ljava/lang/Boolean;")) {
                if (value.getClass() == Integer.class) {
                    int intValue = (Integer) value;
                    value = intValue == 0 ? false : true;
                }
            }
            args.add(value);

            if (registerStore.getType().equals("J")) {
                // Long tried every diet but is still fat and takes 2 registers. Could be thyroid.
                i++;
            }
        }

        return args.toArray();
    }

}
