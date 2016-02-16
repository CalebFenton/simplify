package org.cf.smalivm.emulate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.StaticFieldAccessor;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.smalivm.smali.ClassManager;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_reflect_Field_get implements ExecutionContextMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_reflect_Field_get.class.getSimpleName());

    private final Set<VirtualException> exceptions;
    private SideEffect.Level level;

    java_lang_reflect_Field_get() {
        exceptions = new HashSet<VirtualException>();
        level = SideEffect.Level.NONE;
    }

    @Override
    public void execute(VirtualMachine vm, ExecutionContext ectx) throws Exception {
        MethodState mState = ectx.getMethodState();
        HeapItem fieldItem = mState.peekParameter(0);
        HeapItem instanceItem = mState.peekParameter(1);
        Field field = (Field) fieldItem.getValue();

        String className = ClassNameUtils.toInternal(field.getDeclaringClass());
        String callingMethodDescriptor = ectx.getCallerContext().getMethodSignature();
        String callingClassInternal = callingMethodDescriptor.split("->")[0];
        int accessFlags = field.getModifiers();
        if (!field.isAccessible()) {
            boolean hasAccess = checkAccess(callingClassInternal, className, accessFlags, vm.getClassManager());
            if (!hasAccess) {
                return;
            }
        }

        Object instance = instanceItem.getValue();
        HeapItem getItem = get(field, instance, className, accessFlags, ectx, vm);

        mState.assignReturnRegister(getItem);
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return level;
    }

    private boolean checkAccess(String callingClassInternal, String definingClassInternal, int accessFlags,
                    ClassManager classManager) {
        if (Modifier.isPublic(accessFlags)) {
            return true;
        }

        if (callingClassInternal.equals(definingClassInternal)) {
            return true;
        }

        String callingClassBinary = ClassNameUtils.internalToBinary(callingClassInternal);
        String definingClassBinary = ClassNameUtils.internalToBinary(definingClassInternal);
        boolean innerClass = classManager.isInnerClass(callingClassBinary, definingClassBinary);
        if (innerClass) {
            return true;
        }

        StringBuilder sb = new StringBuilder();
        String modifiers = Modifier.toString(accessFlags);
        sb.append("Class ").append(callingClassBinary).append(" can not access a member of class ")
                        .append(definingClassBinary).append(" with modifiers \"").append(modifiers).append("\"");

        if (Modifier.isPrivate(accessFlags)) {
            setException(new VirtualException(IllegalAccessException.class, sb.toString()));
            return false;
        }

        if (Modifier.isProtected(accessFlags)) {
            boolean isInstance = false;
            try {
                isInstance = classManager.isInstance(callingClassInternal, definingClassInternal);
            } catch (UnknownAncestors e) {
                e.printStackTrace();
            }

            if (isInstance) {
                return true;
            }

            setException(new VirtualException(IllegalAccessException.class, sb.toString()));
            return false;
        }

        return true;
    }

    private HeapItem get(Field field, Object instance, String className, int accessFlags, ExecutionContext ectx,
                    VirtualMachine vm) {
        if (vm.getConfiguration().isSafe(className)) {
            return getSafeField(field, instance, ectx);
        } else {
            boolean isStatic = Modifier.isStatic(accessFlags);
            if (!isStatic) {
                // Instance field lookup isn't supported yet.
                String internalName = ClassNameUtils.toInternal(field.getType());
                return HeapItem.newUnknown(internalName);
            }

            return getLocalField(field, vm, ectx);
        }
    }

    private HeapItem getLocalField(Field field, VirtualMachine vm, ExecutionContext ectx) {
        String fieldDescriptor = Utils.buildFieldDescriptor(field);
        StaticFieldAccessor accessor = vm.getStaticFieldAccessor();

        return accessor.getField(ectx, fieldDescriptor);
    }

    private HeapItem getSafeField(Field field, Object instance, ExecutionContext ectx) {
        HeapItem item = null;
        try {
            Object getObject = field.get(instance);
            String type = ClassNameUtils.toInternal(field.getType());
            item = new HeapItem(getObject, type);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            String message = e.getMessage();
            String callingMethodDescriptor = ectx.getCallerContext().getMethodSignature();
            String callingClass = callingMethodDescriptor.split("->")[0];
            String callingClassJava = ClassNameUtils.internalToBinary(callingClass);
            message = message.replace(java_lang_reflect_Field_get.class.getName(), callingClassJava);
            setException(new VirtualException(e.getClass(), message));
        }

        return item;
    }

    private void setException(VirtualException exception) {
        exceptions.add(exception);
    }

}
