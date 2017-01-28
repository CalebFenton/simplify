package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UninitializedInstance extends Instance {

    public UninitializedInstance(VirtualType type) {
        super(type);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(337, 97).append(getType()).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (other.getClass() != getClass()) {
            return false;
        }
        UninitializedInstance rhs = (UninitializedInstance) other;

        return new EqualsBuilder().append(getType(), rhs.getType()).isEquals();
    }

    @Override
    public String toString() {
        return "Uninitialized " + getType();
    }

}
