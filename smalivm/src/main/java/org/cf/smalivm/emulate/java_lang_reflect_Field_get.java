package org.cf.smalivm.emulate;

import org.cf.smalivm.ExceptionFactory;
import org.cf.smalivm.StaticFieldAccessor;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualType;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class java_lang_reflect_Field_get extends ExecutionContextMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_reflect_Field_get.class.getSimpleName());

    @Override
    public void execute(VirtualMachine vm, Op op, ExecutionContext context) {
        MethodState mState = context.getMethodState();
        HeapItem fieldItem = mState.peekParameter(0);
        HeapItem instanceItem = mState.peekParameter(1);
        Field field = (Field) fieldItem.getValue();

        int accessFlags = field.getModifiers();
        String fieldClassName = ClassNameUtils.toInternal(field.getDeclaringClass());
        if (!field.isAccessible()) {
            VirtualType callingClass = context.getCallerContext().getMethod().getDefiningClass();
            ClassManager classManager = vm.getClassManager();
            VirtualClass fieldClass = classManager.getVirtualClass(fieldClassName);

            boolean hasAccess = checkAccess(callingClass, fieldClass, accessFlags, op, vm.getExceptionFactory());
            if (!hasAccess) {
                return;
            }
        }

        Object instance = instanceItem.getValue();
        HeapItem getItem = get(field, instance, fieldClassName, accessFlags, context, vm, op);
        mState.assignReturnRegister(getItem);
    }

    private boolean checkAccess(VirtualType callingClass, VirtualType fieldClass, int accessFlags, Op op,
                                ExceptionFactory exceptionFactory) {
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
            Throwable exception = exceptionFactory.build(op, IllegalAccessException.class, error);
            setException(exception);

            return false;
        }

        return true;
    }

    private HeapItem get(Field field, Object instance, String className, int accessFlags, ExecutionContext context,
                         VirtualMachine vm, Op op) {
        if (vm.getConfiguration().isSafe(className)) {
            return getSafeField(field, instance, context, op, vm.getExceptionFactory());
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

    private HeapItem getSafeField(Field field, Object instance, ExecutionContext context, Op op,
                                  ExceptionFactory exceptionFactory) {
        HeapItem item = null;
        try {
            Object getObject = field.get(instance);
            String type = ClassNameUtils.toInternal(field.getType());
            item = new HeapItem(getObject, type);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            String message = e.getMessage();
            VirtualMethod callingMethod = context.getCallerContext().getMethod();
            VirtualType callingClass = callingMethod.getDefiningClass();
            message = message.replace(java_lang_reflect_Field_get.class.getName(), callingClass.getBinaryName());

            Throwable exception = exceptionFactory.build(op, e.getClass(), message);
            setException(exception);
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

}
