package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class UninitializedInstance implements Type {

    private final String smaliType;

    public UninitializedInstance(String smaliType) {
        this.smaliType = smaliType;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (!(other instanceof UninitializedInstance)) {
            return false;
        }

        UninitializedInstance rhs = (UninitializedInstance) other;

        return new EqualsBuilder().append(smaliType, rhs.getType()).isEquals();
    }

    public String getType() {
        return smaliType;
    }

    @Override
    public String toString() {
        return "Uninitialized " + smaliType;
    }
}
