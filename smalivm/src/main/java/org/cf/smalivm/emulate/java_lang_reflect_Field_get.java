package org.cf.smalivm.emulate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.smalivm.type.LocalField;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_reflect_Field_get implements ExecutionContextMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_reflect_Field_get.class.getSimpleName());

    private static final String RETURN_TYPE = "Ljava/lang/Object;";

    private final Set<VirtualException> exceptions;
    private SideEffect.Level level;

    java_lang_reflect_Field_get() {
        exceptions = new HashSet<VirtualException>();
        level = SideEffect.Level.NONE;
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    @Override
    public void execute(VirtualMachine vm, ExecutionContext ectx) throws Exception {
        MethodState mState = ectx.getMethodState();
        HeapItem fieldItem = mState.peekParameter(0);
        HeapItem instanceItem = mState.peekParameter(1);
        Object fieldValue = fieldItem.getValue();

        HeapItem getItem;
        if (fieldValue instanceof Field) {
            Object instance = instanceItem.getValue();
            getItem = getNonLocalField((Field) fieldValue, instance, ectx);
        } else if (fieldValue instanceof LocalField) {
            getItem = getLocalField((LocalField) fieldValue, vm, ectx);
        } else {
            if (log.isErrorEnabled()) {
                log.error("Field.get with {} has unexpected type and confuses me.", fieldItem);
            }
            getItem = HeapItem.newUnknown(RETURN_TYPE);
        }

        if (getItem == null) {
            return;
        }

        mState.assignReturnRegister(getItem);
    }

    private void setException(VirtualException exception) {
        exceptions.add(exception);
    }

    private boolean checkAccess(String callingClassSmali, String definingClassSmali, int accessFlags,
                    SmaliClassManager classManager) {
        boolean isPrivate = ((accessFlags & AccessFlags.PRIVATE.getValue()) != 0);
        boolean isProtected = ((accessFlags & AccessFlags.PROTECTED.getValue()) != 0);

        if (isPrivate || isProtected) {
            String callingClassJava = SmaliClassUtils.smaliClassToJava(callingClassSmali);
            String definingClassJava = SmaliClassUtils.smaliClassToJava(definingClassSmali);
            if (!callingClassSmali.equals(definingClassSmali)) {
                if (isPrivate) {
                    StringBuilder sb = new StringBuilder();
                    String modifiers = Modifier.toString(accessFlags);
                    sb.append("Class ").append(callingClassJava).append(" can not access a member of class ")
                                    .append(definingClassJava).append(" with modifiers \"").append(modifiers)
                                    .append("\"");
                    setException(new VirtualException(IllegalAccessException.class, sb.toString()));
                    return false;
                } else { // isProtected
                    boolean isInstance = false;
                    try {
                        isInstance = classManager.isInstance(callingClassSmali, definingClassSmali);
                    } catch (UnknownAncestors e) {
                        e.printStackTrace();
                    }

                    if (!isInstance) {
                        StringBuilder sb = new StringBuilder();
                        String modifiers = Modifier.toString(accessFlags);
                        sb.append("Class ").append(callingClassJava).append(" can not access a member of class ")
                                        .append(definingClassJava).append(" with modifiers \"").append(modifiers)
                                        .append("\"");
                        setException(new VirtualException(IllegalAccessException.class, sb.toString()));
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private HeapItem getLocalField(LocalField localField, VirtualMachine vm, ExecutionContext ectx) {
        BuilderField field = getBuilderField(vm, ectx, localField);
        int accessFlags = field.getAccessFlags();
        HeapItem item = null;

        if (!localField.isAccessible()) {
            String callingMethodDescriptor = ectx.getCallerContext().getMethodDescriptor();
            String callingClassSmali = callingMethodDescriptor.split("->")[0];
            String definingClassSmali = field.getDefiningClass();
            boolean hasAccess = checkAccess(callingClassSmali, definingClassSmali, accessFlags, vm.getClassManager());
            if (!hasAccess) {
                return null;
            }
        }
        boolean isStatic = ((accessFlags & AccessFlags.STATIC.getValue()) != 0);
        if (!isStatic) {
            // Instance field lookup isn't supported yet.
            item = HeapItem.newUnknown(field.getType());
        } else {
            String fieldDescriptor = ReferenceUtil.getFieldDescriptor(field);
            item = vm.getStaticFieldAccessor().getField(ectx, fieldDescriptor);
        }

        return item;
    }

    private HeapItem getNonLocalField(Field field, Object instance, ExecutionContext ectx) {
        HeapItem item = null;
        try {
            Object getObject = field.get(instance);
            String type = SmaliClassUtils.javaClassToSmali(field.getType());
            item = new HeapItem(getObject, type);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            String message = e.getMessage();
            String callingMethodDescriptor = ectx.getCallerContext().getMethodDescriptor();
            String callingClass = callingMethodDescriptor.split("->")[0];
            String callingClassJava = SmaliClassUtils.smaliClassToJava(callingClass);
            message = message.replace(java_lang_reflect_Field_get.class.getName(), callingClassJava);
            setException(new VirtualException(e.getClass(), message));
        }

        return item;
    }

    private BuilderField getBuilderField(VirtualMachine vm, ExecutionContext ectx, LocalField localField) {
        SmaliClassManager classManager = vm.getClassManager();
        String[] parts = localField.getName().split("->");
        String className = parts[0];
        String fieldName = parts[1].split(":")[0];

        if (!classManager.isLocalClass(className)) {
            return null;
        }

        // Accessing a field causes class static initialization
        if (!ectx.isClassInitialized(className)) {
            ectx.staticallyInitializeClassIfNecessary(className);
            level = ectx.getClassSideEffectLevel(className);
        }

        Collection<BuilderField> fields = classManager.getFields(className);
        for (BuilderField field : fields) {
            String currentName = field.getName();
            if (fieldName.equals(currentName)) {
                return field;
            }
        }

        return null;
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return level;
    }

}
