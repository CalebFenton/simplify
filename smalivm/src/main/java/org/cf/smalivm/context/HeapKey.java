package org.cf.smalivm.context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HeapKey {

    private final String heapId;
    private final int register;

    HeapKey(String heapId, int register) {
        this.heapId = heapId;
        this.register = register;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(5, 87);
        hcb.append(heapId).append(register);

        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof HeapKey)) {
            return false;
        }

        HeapKey other = (HeapKey) o;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(heapId, other.heapId).append(register, other.register);

        return eb.isEquals();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(heapId).append(":").append(register);

        return sb.toString();
    }

}
