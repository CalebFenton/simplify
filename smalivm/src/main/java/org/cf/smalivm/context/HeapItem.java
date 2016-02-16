package org.cf.smalivm.context;

import java.lang.reflect.Array;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;

import com.rits.cloning.Cloner;
import com.rits.cloning.ObjenesisInstantiationStrategy;

public class HeapItem {

    // private static final Cloner cloner = new Cloner();
    private final Cloner cloner = new Cloner(new ObjenesisInstantiationStrategy());

    private String type;
    private Object value;

    public HeapItem(Object value, String type) {
        this.value = value;
        this.type = type;
    }

    HeapItem(HeapItem other) {
        value = cloner.deepClone(other.getValue());
        type = other.getType();
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

    public String getComponentBase() {
        return ClassNameUtils.getComponentBase(getType());
    }

    public double getDoubleValue() {
        return Utils.getDoubleValue(getValue());
    }

    public float getFloatValue() {
        return Utils.getFloatValue(getValue());
    }

    public int getIntegerValue() {
        return Utils.getIntegerValue(getValue());
    }

    public long getLongValue() {
        return Utils.getLongValue(getValue());
    }

    public String getType() {
        return type;
    }

    public String getUnboxedValueType() {
        String unboxedType = ClassNameUtils.getPrimitive(getType());
        if (unboxedType == null) {
            unboxedType = type;
        }

        return unboxedType;
    }

    public @Nullable Object getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(123, 51).append(getType()).append(getValue()).hashCode();
    }

    public boolean isImmutable() {
        return Configuration.instance().isImmutable(getType());
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

    public boolean valueIdentity(HeapItem other) {
        return getValue() == other.getValue();
    }

    public static HeapItem newUnknown(String type) {
        return new HeapItem(new UnknownValue(), type);
    }

}
