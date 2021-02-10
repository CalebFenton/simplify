package org.cf.smalivm.type;

import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.reference.TypeReference;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class VirtualPrimitive extends VirtualType {

    VirtualPrimitive(TypeReference typeReference) {
        super(typeReference, null);
    }

    @Override
    public Set<VirtualPrimitive> getImmediateAncestors() {
        return Collections.emptySet();
    }

    @Override
    public Set<VirtualPrimitive> getAncestors() {
        return Collections.emptySet();
    }

    @Override
    public Collection<VirtualField> getFields() {
        return Collections.emptyList();
    }

    @Override
    public Collection<VirtualMethod> getMethods() {
        return Collections.emptyList();
    }

    @Override
    public boolean instanceOf(VirtualType targetType) {
        // Not technically possible with primitives; will fail verification
        // But used to determine if primitive can fit in array
        return equals(targetType);
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }
}
