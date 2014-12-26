package org.cf.smalivm;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFieldAccessor {

    private static Logger log = LoggerFactory.getLogger(StaticFieldAccessor.class.getSimpleName());

    public static HeapItem getField(VirtualMachine vm, ExecutionContext ectx, String fieldDescriptor) {
        String[] parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];
        parts = fieldNameAndType.split(":");
        String fieldName = parts[0];
        String fieldType = parts[1];

        HeapItem fieldItem;
        if (vm.isLocalClass(className)) {
            ClassState cState = ectx.readClassState(className);
            fieldItem = cState.peekField(fieldNameAndType);
        } else if (MethodReflector.isSafe(className)) {
            // Use reflection
            try {
                String javaClassName = SmaliClassUtils.smaliClassToJava(className);
                Class<?> klazz = Class.forName(javaClassName);
                Field field = FieldUtils.getField(klazz, fieldName);
                Object fieldValue = field.get(null);
                fieldItem = new HeapItem(fieldValue, fieldType);
            } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
                fieldItem = HeapItem.newUnknown(fieldType);
                if (log.isWarnEnabled()) {
                    log.warn("Couldn't access field: " + fieldDescriptor);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Stack trace:", e);
                }
            }
        } else {
            // Access denied!
            fieldItem = HeapItem.newUnknown(fieldType);
        }

        return fieldItem;
    }

    public static void putField(VirtualMachine vm, ExecutionContext ectx, String fieldDescriptor, HeapItem putItem) {
        String[] parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];
        if (vm.isLocalClass(className)) {
            ClassState cState = ectx.readClassState(className);
            cState.assignField(fieldNameAndType, putItem);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Ignoring non-local static assignment: " + fieldDescriptor + " = " + putItem);
            }
        }
    }
}
