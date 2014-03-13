package simplify.vm.types;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class UninitializedInstance implements LocalType {

    private final String type;

    public UninitializedInstance(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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
        } else if (!(other instanceof UninitializedInstance)) {
            return false;
        }

        UninitializedInstance rhs = (UninitializedInstance) other;

        return new EqualsBuilder().append(type, rhs.getType()).isEquals();
    }
}
