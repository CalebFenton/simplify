package org.cf.smalivm.context;

import com.rits.cloning.Cloner;
import com.rits.cloning.ObjenesisInstantiationStrategy;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Arrays;

public class HeapItem {

    // private static final Cloner cloner = new Cloner();
    private static final Cloner cloner = new Cloner(new ObjenesisInstantiationStrategy());

    private String type;
    private Object value;

    public HeapItem(Object value, String type) {
        this.value = value;
        this.type = type.intern();
    }

    public HeapItem(Object value) {
        this(value, ClassNameUtils.toInternal(value.getClass()));
    }

    HeapItem(HeapItem other) {
        value = cloner.deepClone(other.getValue());
        type = other.getType();
    }

    public static HeapItem newUnknown(String type) {
        return new HeapItem(new UnknownValue(), type);
    }

    public double asDouble() {
        return Utils.getDoubleValue(getValue());
    }

    public float asFloat() {
        return Utils.getFloatValue(getValue());
    }

    public int asInteger() {
        return Utils.getIntegerValue(getValue());
    }

    public long asLong() {
        return Utils.getLongValue(getValue());
    }

    public String getComponentBase() {
        return ClassNameUtils.getComponentBase(getType());
    }

    public String getType() {
        return type;
    }

    public String getUnboxedType() {
        String unboxedType = ClassNameUtils.getPrimitive(type);
        if (unboxedType == null) {
            unboxedType = type;
        }

        return unboxedType;
    }

    public String getUnboxedValueType() {
        String valueType = getValueType();
        String unboxedType = ClassNameUtils.getPrimitive(valueType);
        if (unboxedType == null) {
            unboxedType = valueType;
        }

        return unboxedType;
    }

    public
    @Nullable
    Object getValue() {
        return value;
    }

    public String getValueType() {
        return isNull() ? getType() : ClassNameUtils.toInternal(getValue().getClass());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(123, 51).append(getType()).append(getValue()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        HeapItem rhs = (HeapItem) obj;

        return new EqualsBuilder().append(getType(), rhs.getType()).append(getValue(), rhs.getValue()).isEquals();
    }

    @Override
    public String toString() {
        Object value = getValue();
        StringBuilder sb = new StringBuilder("type=");
        sb.append(getType()).append(", value=");
        if (value == null) {
            sb.append("null");
        } else {
            if (value.getClass().isArray()) {
                Object[] objArray;
                if (value.getClass().getComponentType().isPrimitive()) {
                    int arrayLen = Array.getLength(value);
                    objArray = new Object[arrayLen];
                    for (int i = 0; i < arrayLen; i++) {
                        objArray[i] = Array.get(value, i);
                    }
                } else {
                    objArray = (Object[]) value;
                }
                String arrayString = Arrays.deepToString(objArray);
                sb.append(arrayString);
            } else {
                sb.append(value);
            }
        }

        return sb.toString();
    }

    public boolean isImmutable() {
        return Configuration.instance().isImmutable(getType());
    }

    @Nonnull
    public boolean isNull() {
        return getValue() == null;
    }

    public boolean isObject() {
        return ClassNameUtils.isObject(getType());
    }

    public boolean isPrimitive() {
        return ClassNameUtils.isPrimitive(getType());
    }

    public boolean isPrimitiveOrWrapper() {
        return ClassNameUtils.isPrimitiveOrWrapper(getType());
    }

    public boolean isPrimitiveWrapper() {
        return ClassNameUtils.isWrapper(getType());
    }

    public boolean isUnknown() {
        return getValue() instanceof UnknownValue;
    }

    public boolean isKnown() {
        return !isUnknown();
    }

    public boolean valueIdentity(HeapItem other) {
        return getValue() == other.getValue();
    }

}
