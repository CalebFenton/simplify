package org.cf.smalivm.type;

import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.writer.builder.BuilderClassDef;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VirtualPrimitive extends VirtualGeneric {

    VirtualPrimitive(TypeReference typeReference) {
        super(typeReference, typeReference.getType(), ClassNameUtils.internalToBinary(typeReference.getType()),
                ClassNameUtils.internalToSource(typeReference.getType()));
    }

    @Override
    public Set<VirtualPrimitive> getAncestors() {
        return new HashSet<>(0);
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
    public boolean instanceOf(VirtualGeneric targetType) {
        // Not technically possible with primitives; will fail verification
        // But used to determine if primitive can fit in array
        return this.equals(targetType);
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
