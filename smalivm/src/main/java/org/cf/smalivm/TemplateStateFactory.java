package org.cf.smalivm;

import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.reference.LocalMethod;
import org.cf.smalivm.smali.ClassManager;
import org.cf.util.Utils;
import org.jf.dexlib2.ValueType;
import org.jf.dexlib2.iface.value.BooleanEncodedValue;
import org.jf.dexlib2.iface.value.ByteEncodedValue;
import org.jf.dexlib2.iface.value.CharEncodedValue;
import org.jf.dexlib2.iface.value.DoubleEncodedValue;
import org.jf.dexlib2.iface.value.FloatEncodedValue;
import org.jf.dexlib2.iface.value.IntEncodedValue;
import org.jf.dexlib2.iface.value.LongEncodedValue;
import org.jf.dexlib2.iface.value.ShortEncodedValue;
import org.jf.dexlib2.iface.value.StringEncodedValue;
import org.jf.dexlib2.writer.builder.BuilderEncodedValues;
import org.jf.dexlib2.writer.builder.BuilderField;

import java.util.List;

/**
 * Created by caleb on 7/13/16.
 */
public class TemplateStateFactory {

    public static MethodState forMethod(ExecutionContext ectx) {
        LocalMethod localMethod = ectx.getMethod();
        int registerCount = localMethod.getRegisterCount();
        List<String> parameterTypes = localMethod.getParameterTypes();
        int parameterSize = Utils.getRegisterSize(parameterTypes);
        MethodState mState = new MethodState(ectx, registerCount, parameterTypes.size(), parameterSize);
        int firstParameter = mState.getParameterStart();
        int parameterRegister = firstParameter;

        // Assume all input values are unknown.
        for (String type : parameterTypes) {
            HeapItem item = HeapItem.newUnknown(type);
            mState.assignParameter(parameterRegister, item);
            parameterRegister += Utils.getRegisterSize(type);
        }

        return mState;
    }

    public static ClassState forClass(ExecutionContext ectx, String className, ClassManager classManager) {
        List<String> fieldNameAndTypes = classManager.getFieldNameAndTypes(className);
        ClassState cState = new ClassState(ectx, className, fieldNameAndTypes.size());
        for (BuilderField field : classManager.getFields(className)) {
            String type = field.getType();
            String fieldNameAndType = field.getName() + ":" + type;
            Object value = null;
            BuilderEncodedValues.BuilderEncodedValue bev = field.getInitialValue();
            if (bev != null) {
                // I *believe* these are the only types which can be literals
                switch (bev.getValueType()) {
                    case ValueType.BYTE:
                        value = ((ByteEncodedValue) bev).getValue();
                        break;
                    case ValueType.SHORT:
                        value = ((ShortEncodedValue) bev).getValue();
                        break;
                    case ValueType.CHAR:
                        value = ((CharEncodedValue) bev).getValue();
                        break;
                    case ValueType.INT:
                        value = ((IntEncodedValue) bev).getValue();
                        break;
                    case ValueType.LONG:
                        value = ((LongEncodedValue) bev).getValue();
                        break;
                    case ValueType.FLOAT:
                        value = ((FloatEncodedValue) bev).getValue();
                        break;
                    case ValueType.DOUBLE:
                        value = ((DoubleEncodedValue) bev).getValue();
                        break;
                    case ValueType.STRING:
                        value = ((StringEncodedValue) bev).getValue();
                        break;
                    case ValueType.BOOLEAN:
                        value = ((BooleanEncodedValue) bev).getValue();
                        break;
                }
            } else {
                switch (type.charAt(0)) {
                    case 'Z':
                        value = false;
                        break;
                    case 'B':
                        value = (byte) 0;
                        break;
                    case 'S':
                        value = (short) 0;
                        break;
                    case 'C':
                        value = '\0';
                        break;
                    case 'I':
                        value = 0;
                        break;
                    case 'J':
                        value = 0L;
                        break;
                    case 'F':
                        value = 0F;
                        break;
                    case 'D':
                        value = 0D;
                        break;
                }
            }

            cState.pokeField(fieldNameAndType, new HeapItem(value, type));
        }

        return cState;
    }


}
