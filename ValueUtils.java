package simplify;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ValueUtils {

    private static Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    public static Object tryToClone(String type, Object value) {

        Object newValue = value;

        Constructor<?> ctor = null;
        Class<?> clazz = value.getClass();
        ctor = clazz.getConstructor(clazz);
        try {
            newValue = ctor.newInstance(value);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        try {
            // First, try copy constructor
            System.out.println("getting ctor: " + clazz.getName());
            ctor = clazz.getConstructor(clazz);
            newValue = ctor.newInstance(value);

            try {
                newValue = ctor.newInstance(value);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                log.fine("Error invoking new instance, using a reference.");
                return value;
            }
        } catch (NoSuchMethodException e) {
            // No? Lame. Look for a public clone.
            e.printStackTrace();
            try {
                Method m = clazz.getMethod("clone");
                try {
                    newValue = m.invoke(value);
                } catch (Exception ex) {
                    log.fine("Error invoking clone, using a reference.");
                    return value;
                }
            } catch (NoSuchMethodException e1) {
                // No clone either
                log.fine("No copy constructor or clone, using a reference.");
                e1.printStackTrace();
                return value;
            } catch (SecurityException e1) {
                e1.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return newValue;
    }
}
