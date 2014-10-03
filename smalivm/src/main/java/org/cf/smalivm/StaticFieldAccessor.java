package org.cf.smalivm;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFieldAccessor {

    private static Logger log = LoggerFactory.getLogger(StaticFieldAccessor.class.getSimpleName());

    public static Object getField(VirtualMachine vm, ExecutionContext ectx, String fieldDescriptor) {
        String[] parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];
        parts = fieldNameAndType.split(":");
        String fieldName = parts[0];
        String fieldType = parts[1];

        Object result;
        if (vm.isLocalClass(className)) {
            ClassState cState = ectx.readClassState(className);
            result = cState.peekField(fieldNameAndType);
        } else if (MethodReflector.isSafe(className)) {
            // Use reflection
            try {
                String javaClassName = SmaliClassUtils.smaliClassToJava(className);
                Class<?> klazz = Class.forName(javaClassName);
                Field field = FieldUtils.getField(klazz, fieldName);
                result = field.get(null);
            } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
                result = new UnknownValue(fieldType);
                if (log.isWarnEnabled()) {
                    log.warn("Couldn't access field: " + fieldDescriptor);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Stack trace:", e);
                }
            }
        } else {
            // Access denied!
            result = new UnknownValue(fieldType);
        }

        return result;
    }

    public static void putField(VirtualMachine vm, ExecutionContext ectx, String fieldDescriptor, Object value) {
        String[] parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];

        if (vm.isLocalClass(className)) {
            ClassState cState = ectx.readClassState(className);
            cState.assignField(fieldNameAndType, value);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Ignoring non-local static assignment: " + fieldDescriptor + " = " + value);
            }
        }
    }

}
