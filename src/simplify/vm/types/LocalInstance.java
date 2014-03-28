package simplify.vm.types;

import org.apache.commons.lang3.builder.EqualsBuilder;

import simplify.vm.InstanceContext;

public class LocalInstance implements Type {

    private InstanceContext ctx;

    private final String type;

    public LocalInstance(String type) {
        this.type = type;
        ctx = null;
    }

    public InstanceContext getContext() {
        // isInitialized -> ctx != null ?
        return ctx;
    }

    public String getType() {
        return type;
    }

    public boolean isInitialized() {
        return getContext() == null;
    }

    public void setContext(InstanceContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        return type;
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

        return new EqualsBuilder().append(type, rhs.type).append(ctx, rhs.ctx).isEquals();
    }

}
