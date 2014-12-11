package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.cf.smalivm.context.InstanceState;

public class LocalInstance implements LocalType {

    private InstanceState iState;

    private final String smaliType;

    public LocalInstance(String smaliType) {
        this.smaliType = smaliType;
        iState = null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (!(other instanceof LocalInstance)) {
            return false;
        }

        LocalInstance rhs = (LocalInstance) other;

        return new EqualsBuilder().append(smaliType, rhs.smaliType).append(iState, rhs.iState).isEquals();
    }

    public InstanceState getState() {
        // isInitialized -> iState != null ?
        return iState;
    }

    public String getName() {
        return smaliType;
    }

    public boolean isInitialized() {
        return getState() == null;
    }

    public void setContext(InstanceState iState) {
        this.iState = iState;
    }

    @Override
    public String toString() {
        return smaliType;
    }

}
