package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LocalMethod implements LocalType {

    private final String methodDescriptor;

    public LocalMethod(String methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (!(other instanceof LocalMethod)) {
            return false;
        }

        LocalMethod rhs = (LocalMethod) other;

        return new EqualsBuilder().append(methodDescriptor, rhs.methodDescriptor).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(325, 29).append(methodDescriptor).toHashCode();
    }

    public String getName() {
        return methodDescriptor;
    }

    @Override
    public String toString() {
        return methodDescriptor;
    }

}
