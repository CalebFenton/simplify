package simplify;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ValueUtils {

    private static Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    public static final Map<String, Class> ConstructorCloners;
    public static final Map<String, Class> Primitives;
    static {
        Primitives = new HashMap<String, Class>();
        Primitives.put("I", int.class);
        Primitives.put("Z", boolean.class);
        Primitives.put("B", byte.class);
        Primitives.put("S", short.class);
        Primitives.put("J", long.class);
        Primitives.put("C", char.class);
        Primitives.put("F", float.class);
        Primitives.put("D", double.class);

        ConstructorCloners = new HashMap<String, Class>();
        ConstructorCloners.put("I", Integer.class);
        ConstructorCloners.put("Z", Boolean.class);
        ConstructorCloners.put("B", Byte.class);
        ConstructorCloners.put("S", Short.class);
        ConstructorCloners.put("J", Long.class);
        ConstructorCloners.put("C", Character.class);
        ConstructorCloners.put("F", Float.class);
        ConstructorCloners.put("D", Double.class);

        ConstructorCloners.put(Integer.class.getName(), Integer.class);
        ConstructorCloners.put(Boolean.class.getName(), Boolean.class);
        ConstructorCloners.put(Byte.class.getName(), Byte.class);
        ConstructorCloners.put(Short.class.getName(), Short.class);
        ConstructorCloners.put(Long.class.getName(), Long.class);
        ConstructorCloners.put(Character.class.getName(), Character.class);
        ConstructorCloners.put(Float.class.getName(), Float.class);
        ConstructorCloners.put(Double.class.getName(), Double.class);

        ConstructorCloners.put(String.class.getName(), String.class);
        ConstructorCloners.put(List.class.getName(), List.class);
        ConstructorCloners.put(StringBuilder.class.getName(), StringBuilder.class);
    }

    public static Object tryToClone(String type, Object value) {
        Object newValue = null;

        Class clazz = ConstructorCloners.get(type);
        if (clazz != null) {
            try {
                Class pclazz = Primitives.get(type);
                Constructor ctor = null;
                if (pclazz != null) {
                    ctor = clazz.getConstructor(pclazz);
                    newValue = ctor.newInstance(value);
                } else {
                    ctor = clazz.getConstructor(clazz);
                    newValue = ctor.newInstance(clazz.cast(value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.finer("Can't clone object of type: " + type + ". Will reference same object.");
            newValue = value;
        }

        return newValue;
    }

}
