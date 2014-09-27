package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.cf.smalivm.context.InstanceState;

public class LocalInstance implements Type {

    private InstanceState istate;

    private final String smaliType;

    public LocalInstance(String smaliType) {
        this.smaliType = smaliType;
        istate = null;
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

        return new EqualsBuilder().append(smaliType, rhs.smaliType).append(istate, rhs.istate).isEquals();
    }

    public InstanceState getState() {
        // isInitialized -> istate != null ?
        return istate;
    }

    public String getType() {
        return smaliType;
    }

    public boolean isInitialized() {
        return getState() == null;
    }

    public void setContext(InstanceState ictx) {
        this.istate = ictx;
    }

    @Override
    public String toString() {
        return smaliType;
    }

}
