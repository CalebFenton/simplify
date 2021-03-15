package org.cf.smalivm;

import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.smalivm.type.VirtualType;
import org.cf.util.Utils;

import java.util.List;

// TODO: this can be deleted after refactoring. state knows how to build itself.
public class TemplateStateFactory {

    public static MethodState forMethod(ExecutionContext context) {
        VirtualMethod method = context.getMethod();
        int registerCount = method.getRegisterCount();
        List<String> parameterTypes = method.getParameterTypeNames();
        int parameterSize = Utils.getRegisterSize(parameterTypes);
        MethodState mState = new MethodState(context, registerCount, parameterTypes.size(), parameterSize);
        int firstParameter = mState.getParameterStart();
        int parameterRegister = firstParameter;

        for (String type : parameterTypes) {
            HeapItem item;
            if (parameterRegister == firstParameter && !method.isStatic() && method.getName().equals("<init>")) {
                UninitializedInstance instance = new UninitializedInstance(method.getDefiningClass());
                item = new HeapItem(instance, type);
            } else {
                item = HeapItem.newUnknown(type);
            }
            mState.assignParameter(parameterRegister, item);
            parameterRegister += Utils.getRegisterSize(type);
        }

        return mState;
    }

    public static ClassState forClass(ExecutionContext context, VirtualType virtualClass) {
        ClassState cState = new ClassState(virtualClass, context);
        for (VirtualField field : virtualClass.getFields()) {
            Object value = field.getInitialValue();
            String type = field.getType();
            cState.pokeField(field, new HeapItem(value, type));
        }

        return cState;
    }
}
