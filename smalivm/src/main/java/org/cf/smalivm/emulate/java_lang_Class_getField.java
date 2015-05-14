package org.cf.smalivm.emulate;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalField;
import org.cf.smalivm.type.UnknownValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_getField implements MethodStateMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_getField.class.getSimpleName());

    private static final String RETURN_TYPE = "Ljava/lang/reflect/Field;";

    private final Set<VirtualException> exceptions;

    java_lang_Class_getField() {
        exceptions = new HashSet<VirtualException>();
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    @Override
    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        HeapItem classItem = mState.peekParameter(0);
        Object classValue = classItem.getValue();
        String fieldName = (String) mState.peekParameter(1).getValue();

        Object fieldValue;
        if (classValue instanceof Class<?>) {
            // It's a real class. Try and return a real field.
            try {
                fieldValue = getNonLocalField((Class<?>) classValue, fieldName);
            } catch (NoSuchFieldException | SecurityException e) {
                // Assuming Android doesn't have this method since our JVM doesn't.
                mState.assignReturnRegister(HeapItem.newUnknown(RETURN_TYPE));
                // TODO: fill with convincing stacktrace
                throw e;
            }
        } else if (classValue instanceof LocalClass) {
            LocalClass localClass = (LocalClass) classValue;
            fieldValue = getLocalField(vm, localClass, fieldName);
            if ((fieldValue instanceof UnknownValue)) {
                mState.assignReturnRegister(HeapItem.newUnknown(RETURN_TYPE));
                // TODO: fill with convincing lies
                throw new NoSuchFieldException(fieldName);
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("Class.getField with " + classItem + " has unexpected type and confuses me.");
            }
            fieldValue = new UnknownValue();
        }

        mState.assignReturnRegister(fieldValue, RETURN_TYPE);
    }

    private static Object getLocalField(VirtualMachine vm, LocalClass localClass, String fieldName) {
        SmaliClassManager classManager = vm.getClassManager();
        String className = localClass.getName();
        if (!classManager.isLocalClass(className)) {
            return new UnknownValue();
        }

        List<String> fieldNameAndTypes = vm.getClassManager().getFieldNameAndTypes(className);
        for (String fieldNameAndType : fieldNameAndTypes) {
            if (fieldNameAndType.startsWith(fieldName)) {
                StringBuilder sb = new StringBuilder(className);
                sb.append("->").append(fieldNameAndType);
                return new LocalField(sb.toString());
            }
        }

        return new UnknownValue();
    }

    private static Field getNonLocalField(Class<?> klazz, String fieldName) throws NoSuchFieldException,
                    SecurityException {
        return klazz.getField(fieldName);
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
