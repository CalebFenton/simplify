package org.cf.smalivm.emulate;

import java.lang.reflect.Field;
import java.util.Collection;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
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

    public void execute(VirtualMachine vm, ExecutionContext ectx) throws Exception {
        MethodState mState = ectx.getMethodState();
        HeapItem fieldItem = mState.peekParameter(0);
        HeapItem instanceItem = mState.peekParameter(1);
        Object fieldValue = fieldItem.getValue();

        HeapItem getItem;
        if (fieldValue instanceof Field) {
            Field field = (Field) fieldValue;
            try {
                Object getObject = field.get(instanceItem.getValue());
                String type = SmaliClassUtils.javaClassToSmali(field.getType());
                getItem = new HeapItem(getObject, type);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                mState.assignReturnRegister(HeapItem.newUnknown(RETURN_TYPE));
                throw e;
            }
        } else if (fieldValue instanceof LocalField) {
            BuilderField field = getBuilderField(vm, ectx, (LocalField) fieldValue);
            if (null == field) {
                getItem = HeapItem.newUnknown(RETURN_TYPE);
            } else {
                // TODO: This doesn't check for private access, but it should.
                int accessFlags = field.getAccessFlags();
                boolean isStatic = ((accessFlags & AccessFlags.STATIC.getValue()) != 0);
                if (!isStatic) {
                    // Instance field lookup isn't supported yet.
                    getItem = HeapItem.newUnknown(field.getType());
                } else {
                    String fieldDescriptor = ReferenceUtil.getFieldDescriptor(field);
                    getItem = vm.getStaticFieldAccessor().getField(ectx, fieldDescriptor);
                }
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("Field.get with " + fieldItem + " has unexpected type and confuses me.");
            }
            getItem = HeapItem.newUnknown(RETURN_TYPE);
        }

        mState.assignReturnRegister(getItem);
    }

    private static BuilderField getBuilderField(VirtualMachine vm, ExecutionContext ectx, LocalField localField) {
        SmaliClassManager classManager = vm.getClassManager();
        String[] parts = localField.getName().split("->");
        String className = parts[0];
        String fieldName = parts[1].split(":")[0];

        if (!classManager.isLocalClass(className)) {
            return null;
        }

        ectx.staticallyInitializeClassIfNecessary(className);

        Collection<BuilderField> fields = classManager.getFields(className);
        for (BuilderField field : fields) {
            String currentName = field.getName();
            if (fieldName.equals(currentName)) {
                return field;
            }
        }

        return null;
    }

    public SideEffect.Level getSideEffectLevel() {
        // Accessing a field causes class static initialization
        return SideEffect.Level.WEAK;
    }

}
