package org.cf.smalivm;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.VirtualField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

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

        return cState.peekField(field);
    }

    private HeapItem getSafeField(ExecutionContext context, VirtualField field) {
        String className = field.getDefiningClass().getBinaryName();
        try {
            Class<?> klazz = Class.forName(className);
            Field realField = FieldUtils.getField(klazz, field.getName());
            Object fieldValue = realField.get(null);

            return new HeapItem(fieldValue, field.getType());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO: medium - throw these exceptions and handle them by setting correct virtual exceptions
            if (log.isWarnEnabled()) {
                log.warn("Couldn't access field: {}", field.toString());
            }
            if (log.isDebugEnabled()) {
                log.debug("Stack trace:", e);
            }
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.warn("Could not find class : {}", className);
        }

        return HeapItem.newUnknown(field.getType());
    }

    private boolean isSafe(VirtualField field) {
        return vm.isSafe(field.getDefiningClass());
    }

    public void putField(ExecutionContext context, VirtualField field, HeapItem putItem) {
        if (isSafe(field)) {
            // TODO: consider enabling this, is it safe?
            if (log.isWarnEnabled()) {
                log.warn("Ignoring static assignment of non-local field: {} = {}", field, putItem);
            }
        } else {
            ClassState cState = context.readClassState(field.getDefiningClass());
            cState.assignField(field, putItem);
        }
    }

}
