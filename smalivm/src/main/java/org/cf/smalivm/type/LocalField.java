package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LocalField implements LocalType {

    private final String fieldDescriptor;
    private boolean accessible;

    public LocalField(String fieldDescriptor) {
        this.fieldDescriptor = fieldDescriptor;
        accessible = false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (!(other instanceof LocalField)) {
            return false;
        }

        LocalField rhs = (LocalField) other;

        return new EqualsBuilder().append(fieldDescriptor, rhs.fieldDescriptor).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(325, 29).append(fieldDescriptor).toHashCode();
    }

    @Override
    public String getName() {
        return fieldDescriptor;
    }

    @Override
    public String toString() {
        return fieldDescriptor;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

}
