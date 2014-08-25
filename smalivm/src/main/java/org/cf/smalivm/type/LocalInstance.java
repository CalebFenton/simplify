package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.cf.smalivm.context.InstanceContext;

public class LocalInstance implements Type {

    private InstanceContext ctx;

    private final String smaliType;

    public LocalInstance(String smaliType) {
        this.smaliType = smaliType;
        ctx = null;
    }

    public InstanceContext getContext() {
        // isInitialized -> ctx != null ?
        return ctx;
    }

    public String getType() {
        return smaliType;
    }

    public boolean isInitialized() {
        return getContext() == null;
    }

    public void setContext(InstanceContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        return smaliType;
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

        return new EqualsBuilder().append(smaliType, rhs.smaliType).append(ctx, rhs.ctx).isEquals();
    }

}
