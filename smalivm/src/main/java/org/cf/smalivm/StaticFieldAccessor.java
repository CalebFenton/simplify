package org.cf.smalivm;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.VirtualField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFieldAccessor {

    private static Logger log = LoggerFactory.getLogger(StaticFieldAccessor.class.getSimpleName());

    private final VirtualMachine vm;

    StaticFieldAccessor(VirtualMachine vm) {
        this.vm = vm;
    }

    public HeapItem getField(ExecutionContext context, VirtualField field) {
        if (isSafe(field)) {
            return getSafeField(context, field);
        } else {
            return getLocalField(context, field);
        }
    }

    private HeapItem getLocalField(ExecutionContext context, VirtualField field) {
        ClassState cState = context.readClassState(field.getDefiningClass());
        HeapItem fieldItem = cState.peekField(field);

        return fieldItem;
    }

    private HeapItem getSafeField(ExecutionContext context, VirtualField localField) {
        String className = localField.getDefiningClass().getBinaryName();
        try {
            Class<?> klazz = Class.forName(className);
            java.lang.reflect.Field field = FieldUtils.getField(klazz, localField.getName());
            Object fieldValue = field.get(null);

            return new HeapItem(fieldValue, localField.getType());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO: medium - throw these exceptions and handle them by setting correct virtual exceptions
            if (log.isWarnEnabled()) {
                log.warn("Couldn't access field: {}", localField.toString());
            }
            if (log.isDebugEnabled()) {
                log.debug("Stack trace:", e);
            }
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.warn("Could not find class : {}", className);
        }

        return HeapItem.newUnknown(localField.getType());
    }

    private boolean isSafe(VirtualField field) {
        return vm.isSafe(field.getDefiningClass());
    }

    public void putField(ExecutionContext context, VirtualField field, HeapItem putItem) {
        if (isSafe(field)) {
            if (log.isWarnEnabled()) {
                log.warn("Ignoring static assignment of non-local field: {} = {}", field, putItem);
            }
        } else {
            ClassState cState = context.readClassState(field.getDefiningClass());
            cState.assignField(field, putItem);
        }
    }

}
