package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cf.smalivm.context.InstanceState;

/**
 * Represents an object instance of a virtual defined by a local class such as an input or framework class.
 */
public class InitializedInstance extends Instance {

    private InstanceState iState;

    public InitializedInstance(VirtualType type) {
        super(type);
        iState = null;
    }

    public InstanceState getState() {
        return iState;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(223, 197).append(getType()).append(getState()).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (!(other instanceof InitializedInstance)) {
            return false;
        }

        InitializedInstance rhs = (InitializedInstance) other;

        return new EqualsBuilder().append(getType(), rhs.getType()).append(getState(), rhs.getState()).isEquals();
    }

    public boolean isInitialized() {
        return getState() == null;
    }

    public void setContext(InstanceState iState) {
        this.iState = iState;
    }

}
