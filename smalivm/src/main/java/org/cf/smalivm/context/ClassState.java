package org.cf.smalivm.context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassState extends BaseState {

    private static final Logger log = LoggerFactory.getLogger(ClassState.class.getSimpleName());

    private final VirtualType virtualClass;

    public ClassState(VirtualType virtualClass, ExecutionContext context) {
        super(context, virtualClass.getFields().size());

        this.virtualClass = virtualClass;
    }

    private ClassState(ClassState parent, ExecutionContext context) {
        this(parent.virtualClass, context);
    }

    public void assignField(VirtualField field, Object value) {
        int register = 0;
        String heapId = getHeapId(field);
        String type = field.getType();
        assignRegister(register, new HeapItem(value, type), heapId);
    }

    public void assignField(VirtualField field, HeapItem item) {
        int register = 0;
        String heapId = getHeapId(field);
        assignRegister(register, item, heapId);
    }

    public ClassState getChild(ExecutionContext childContext) {
        return new ClassState(this, childContext);
    }

    public VirtualType getVirtualClass() {
        return virtualClass;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1337, 13).append(virtualClass).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (obj.getClass() != getClass()) {
            return false;
        }
        ClassState other = (ClassState) obj;

        return new EqualsBuilder().append(virtualClass, other.virtualClass).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fields:\n");
        for (VirtualField field : virtualClass.getFields()) {
            sb.append(field).append(" = ").append(peekField(field)).append('\n');
        }
        sb.setLength(sb.length() - 1);
        sb.append('\n');

        return sb.toString();
    }

    public HeapItem peekField(VirtualField field) {
        int register = 0;
        String heapId = getHeapId(field);
        HeapItem fieldItem = peekRegister(register, heapId);
        if (fieldItem == null) {
            log.error("Undefined field: {}; returning unknown", field);
            fieldItem = HeapItem.newUnknown(field.getType());
        }

        return fieldItem;
    }

    public void pokeField(VirtualField field, Object value) {
        int register = 0;
        String heapId = getHeapId(field);
        String type = field.getType();
        pokeRegister(register, new HeapItem(value, type), heapId);
    }

    public void pokeField(VirtualField field, HeapItem item) {
        int register = 0;
        String heapId = getHeapId(field);
        pokeRegister(register, item, heapId);
    }

    public void updateIdentities(VirtualField field, HeapItem item) {
        int register = 0;
        String heapId = getHeapId(field);
        String heapKey = heapId + ":" + register;
        updateKey(heapKey, item);
    }

    private String getHeapId(VirtualField field) {
        return field.toString();
    }
}
