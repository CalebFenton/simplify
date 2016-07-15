package org.cf.smalivm.type;

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
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderEncodedValues;
import org.jf.dexlib2.writer.builder.BuilderField;

import java.lang.reflect.Modifier;

public class VirtualField extends VirtualClassItem {

    private final BuilderField field;

    VirtualField(BuilderField field, VirtualClass virtualClass) {
        super(field, virtualClass);
        this.field = field;
    }

    public BuilderField getField() {
        return field;
    }

    public Object getInitialValue() {
        if (!Modifier.isStatic(field.getAccessFlags())) {
            return new UnknownValue();
        }

        BuilderEncodedValues.BuilderEncodedValue bev = field.getInitialValue();
        if (bev != null) {
            // I *believe* these are the only types which can be literals
            switch (bev.getValueType()) {
                case ValueType.BYTE:
                    return ((ByteEncodedValue) bev).getValue();
                case ValueType.SHORT:
                    return ((ShortEncodedValue) bev).getValue();
                case ValueType.CHAR:
                    return ((CharEncodedValue) bev).getValue();
                case ValueType.INT:
                    return ((IntEncodedValue) bev).getValue();
                case ValueType.LONG:
                    return ((LongEncodedValue) bev).getValue();
                case ValueType.FLOAT:
                    return ((FloatEncodedValue) bev).getValue();
                case ValueType.DOUBLE:
                    return ((DoubleEncodedValue) bev).getValue();
                case ValueType.STRING:
                    return ((StringEncodedValue) bev).getValue();
                case ValueType.BOOLEAN:
                    return ((BooleanEncodedValue) bev).getValue();
            }
        } else {
            switch (getType().charAt(0)) {
                case 'Z':
                    return false;
                case 'B':
                    return (byte) 0;
                case 'S':
                    return (short) 0;
                case 'C':
                    return '\0';
                case 'I':
                    return 0;
                case 'J':
                    return 0L;
                case 'F':
                    return 0F;
                case 'D':
                    return 0D;
            }
        }

        return null;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public String getSignature() {
        return ReferenceUtil.getFieldDescriptor(field);
    }

    @Override
    public String toString() {
        return getSignature();
    }

    public String getType() {
        return field.getType();
    }

    public boolean isStatic() {
        return Modifier.isStatic(field.getAccessFlags());
    }

}
