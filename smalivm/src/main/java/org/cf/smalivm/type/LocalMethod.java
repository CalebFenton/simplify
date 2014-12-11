package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class LocalMethod implements LocalType {

    private final String smaliType;

    public LocalMethod(String methodName) {
        this.smaliType = methodName;
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

        return new EqualsBuilder().append(smaliType, rhs.smaliType).isEquals();
    }

    public String getName() {
        return smaliType;
    }

    @Override
    public String toString() {
        return smaliType;
    }

}
