package org.cf.smalivm.context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.cf.util.SmaliClassUtils;

import com.rits.cloning.Cloner;

public class HeapValue {

    private static final Cloner cloner = new Cloner();

    private Object value;
    private String type;

    public HeapValue(Object value, String type) {
        this.value = value;
        this.type = type;
    }

    HeapValue(HeapValue other) {
        this(cloner.deepClone(other.getValue()), other.getType());
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
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
        HeapValue rhs = (HeapValue) obj;

        return new EqualsBuilder().append(getType(), rhs.getType()).append(getValue(), rhs.getValue()).isEquals();
    }

    public boolean isPrimitive() {
        return SmaliClassUtils.isPrimitiveType(getType());
    }

    public boolean isPrimitiveOrWrapper() {
        return SmaliClassUtils.isPrimitiveOrWrapperType(getType());
    }

    public boolean isPrimitiveWrapper() {
        return SmaliClassUtils.isWrapperType(getType());
    }

}
