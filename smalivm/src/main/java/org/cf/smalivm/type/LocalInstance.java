package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cf.smalivm.context.InstanceState;

/**
 * Represents an object instance of a type defined by a local class such as an input or framework class.
 * 
 * @author caleb
 *
 */
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder(223, 197).append(getName()).append(iState).hashCode();
    }

    public InstanceState getState() {
        // isInitialized -> iState != null ?
        return iState;
    }

    @Override
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
