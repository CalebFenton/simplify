package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EmulatedType implements LocalType {

    private String extra;
    private String smaliType;

    public EmulatedType(String smaliType) {
        this.smaliType = smaliType;
    }

    @Override
    public String getName() {
        return smaliType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(", extra=").append(extra);

        return sb.toString();
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
        EmulatedType rhs = (EmulatedType) obj;

        return new EqualsBuilder().append(getName(), rhs.getName()).append(getExtra(), rhs.getExtra()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(113, 21).append(getName()).append(getExtra()).hashCode();
    }

}
