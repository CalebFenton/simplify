package org.cf.smalivm;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cf.smalivm.context.ClassContext;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFieldAccessor {

    private static Logger log = LoggerFactory.getLogger(StaticFieldAccessor.class.getSimpleName());

    public static Object getField(VirtualMachine vm, String fieldDescriptor) {
        String[] parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];
        parts = fieldNameAndType.split(":");
        String fieldName = parts[0];
        String fieldType = parts[1];

        Object result;
        if (vm.isClassDefinedLocally(className)) {
            ClassContext classCtx = vm.getStaticClassContext(className);
            result = classCtx.readField(fieldNameAndType);
        } else if (MethodReflector.isWhitelisted(className)) {
            // Use reflection
            try {
                String javaClassName = SmaliClassUtils.smaliClassToJava(className);
                Class<?> klazz = Class.forName(javaClassName);
                Field field = FieldUtils.getField(klazz, fieldName);
                result = field.get(null);
            } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
                result = new UnknownValue(fieldType);
                log.warn("Couldn't access field: " + fieldDescriptor);
                log.debug("Stack trace:", e);
            }
        } else {
            // Access denied!
            result = new UnknownValue(fieldType);
        }

        return result;
    }

    public static void putField(VirtualMachine vm, String fieldDescriptor, Object value) {
        String[] parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];

        if (vm.isClassDefinedLocally(className)) {
            ClassContext classCtx = vm.getStaticClassContext(className);
            classCtx.assignField(fieldNameAndType, value);
        } else {
            log.warn("Ignoring untrusted static put of " + value + " to " + fieldDescriptor);
        }
    }

}
