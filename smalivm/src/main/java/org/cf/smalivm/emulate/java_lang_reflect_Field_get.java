package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.StaticFieldAccessor;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

class java_lang_reflect_Field_get implements ExecutionContextMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_reflect_Field_get.class.getSimpleName());

    private final Set<VirtualException> exceptions;
    private SideEffect.Level level;

    java_lang_reflect_Field_get() {
        exceptions = new HashSet<VirtualException>();
        level = SideEffect.Level.NONE;
    }

    @Override
    public void execute(VirtualMachine vm, ExecutionContext context) {
        MethodState mState = context.getMethodState();
        HeapItem fieldItem = mState.peekParameter(0);
        HeapItem instanceItem = mState.peekParameter(1);
        Field field = (Field) fieldItem.getValue();

        int accessFlags = field.getModifiers();
        String fieldClassName = ClassNameUtils.toInternal(field.getDeclaringClass());
        if (!field.isAccessible()) {
            VirtualClass callingClass = context.getCallerContext().getMethod().getDefiningClass();
            ClassManager classManager = vm.getClassManager();
            VirtualClass fieldClass = classManager.getVirtualClass(fieldClassName);

            boolean hasAccess = checkAccess(callingClass, fieldClass, accessFlags);
            if (!hasAccess) {
                return;
            }
        }

        Object instance = instanceItem.getValue();
        HeapItem getItem = get(field, instance, fieldClassName, accessFlags, context, vm);
        mState.assignReturnRegister(getItem);
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return level;
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    private boolean checkAccess(VirtualClass callingClass, VirtualClass fieldClass, int accessFlags) {
        boolean isPublic = Modifier.isPublic(accessFlags);
        if (isPublic) {
            return true;
        }

        if (callingClass.equals(fieldClass)) {
            return true;
        }

        if (callingClass.isInnerClassOf(fieldClass)) {
            return true;
        }

        boolean isPrivate = Modifier.isPrivate(accessFlags);
        boolean isProtected = Modifier.isProtected(accessFlags);
        boolean isPackagePrivate = !(isProtected || isPrivate);
        if (isPrivate || (isProtected && !callingClass.instanceOf(fieldClass)) ||
            (isPackagePrivate && !callingClass.isSamePackageOf(fieldClass))) {
            String error = callingClass.getBinaryName() + " can't access a member of " + fieldClass.getBinaryName() +
                           " with modifiers \"" + Modifier.toString(accessFlags) + "\"";
            setException(new VirtualException(IllegalAccessException.class, error));

            return false;
        }

        return true;
    }

    private HeapItem get(Field field, Object instance, String className, int accessFlags, ExecutionContext context,
                         VirtualMachine vm) {
        if (vm.getConfiguration().isSafe(className)) {
            return getSafeField(field, instance, context);
        } else {
            boolean isStatic = Modifier.isStatic(accessFlags);
            if (!isStatic) {
                // Instance field lookup isn't supported yet.
                String internalName = ClassNameUtils.toInternal(field.getType());
                return HeapItem.newUnknown(internalName);
            }

            return getVirtualField(field, vm, context);
        }
    }

    private HeapItem getSafeField(Field field, Object instance, ExecutionContext context) {
        HeapItem item = null;
        try {
            Object getObject = field.get(instance);
            String type = ClassNameUtils.toInternal(field.getType());
            item = new HeapItem(getObject, type);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            String message = e.getMessage();
            VirtualMethod callingMethod = context.getCallerContext().getMethod();
            VirtualClass callingClass = callingMethod.getDefiningClass();
            message = message.replace(java_lang_reflect_Field_get.class.getName(), callingClass.getBinaryName());
            setException(new VirtualException(e.getClass(), message));
        }

        return item;
    }

    private HeapItem getVirtualField(Field field, VirtualMachine vm, ExecutionContext context) {
        String className = ClassNameUtils.toInternal(field.getDeclaringClass());
        VirtualClass fieldClass = vm.getClassManager().getVirtualClass(className);
        VirtualField virtualField = fieldClass.getField(field.getName());
        StaticFieldAccessor accessor = vm.getStaticFieldAccessor();

        return accessor.getField(context, virtualField);
    }

    private void setException(VirtualException exception) {
        exceptions.add(exception);
    }

}
