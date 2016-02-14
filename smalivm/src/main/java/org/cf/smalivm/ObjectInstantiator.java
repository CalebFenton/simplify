package org.cf.smalivm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Defaults;

public class ObjectInstantiator {

    private static final Logger log = LoggerFactory.getLogger(ObjectInstantiator.class.getSimpleName());

    public static Object newInstance(Class<?> klazz) throws InstantiationException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        if (Enum.class.isAssignableFrom(klazz)) {
            return newEnumInstance(klazz);
        }

        Object newInstance;
        try {
            // Try it without any funny business.
            newInstance = klazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            if (log.isTraceEnabled()) {
                log.trace("{} has no default constructor, picking another", klazz);
            }

            // Alright, we'll try it the hard way.
            Constructor<?>[] ctors = klazz.getDeclaredConstructors();
            Constructor<?> ctor = ctors[0];
            ctor.setAccessible(true); // the little games we play, java...
            Object[] args = new Object[ctor.getParameterCount()];
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            for (int i = 0; i < args.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                args[i] = Defaults.defaultValue(parameterType);
            }
            newInstance = ctor.newInstance(args);
        }

        return newInstance;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object newEnumInstance(Class<?> klazz) {
        return Enum.valueOf((Class<? extends Enum>) klazz, "$shadow_instance");
    }

}
