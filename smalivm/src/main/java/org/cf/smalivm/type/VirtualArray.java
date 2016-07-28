package org.cf.smalivm.type;

import org.cf.smalivm.dex.CommonTypes;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.reference.TypeReference;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class VirtualArray extends VirtualGeneric {

    private final int rank;
    private Set<VirtualGeneric> ancestors;

    VirtualArray(TypeReference typeReference) {
        super(typeReference, typeReference.getType(), ClassNameUtils.internalToBinary(typeReference.getType()),
                ClassNameUtils.internalToSource(typeReference.getType()));
        rank = ClassNameUtils.getDimensionCount(typeReference.getType());
    }

    private static String buildRankString(int rank) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rank; i++) {
            sb.append('[');
        }

        return sb.toString();
    }

    @Override
    public Set<? extends VirtualGeneric> getAncestors() {
        if (ancestors != null) {
            return ancestors;
        }
        ancestors = new LinkedHashSet<>();

        TypeReference typeReference = getTypeReference();
        String baseType = ClassNameUtils.getComponentBase(typeReference.getType());
        if (!ClassNameUtils.isPrimitive(baseType)) {
            // It's an array of object type, so get ancestry for that object
            // Primitives have no ancestors. They're primordial. THEY ARE ETERNAL. ALL ONE OR NONE!
            ClassManager classManager = getClassManager();
            VirtualClass baseClass = classManager.getVirtualClass(baseType);
            String rankString = buildRankString(rank);

            // All ancestors of the base type with the same dimension rank
            for (VirtualClass baseAncestor : baseClass.getAncestors()) {
                String ancestorName = rankString + baseAncestor.getTypeReference().getType();
                VirtualArray ancestor = (VirtualArray) classManager.getVirtualType(ancestorName);
                ancestors.add(ancestor);
            }
        }
        // All arrays are an instance of an Object
        // http://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.3.1
        // Also, [[[String is an instance of [[Object and [Object
        for (int i = rank - 1; i >= 0; i--) {
            String objectType = buildRankString(i) + CommonTypes.OBJECT;
            VirtualGeneric ancestor = classManager.getVirtualType(objectType);
            ancestors.add(ancestor);
        }

        return ancestors;
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
        if (equals(targetType)) {
            return true;
        }
        for (VirtualGeneric ancestor : getAncestors()) {
            if (ancestor.equals(targetType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    public VirtualGeneric getBaseType() {
        String baseType = ClassNameUtils.getComponentBase(getName());

        return classManager.getVirtualType(baseType);
    }

    public VirtualGeneric getComponentType() {
        String componentType = ClassNameUtils.getComponentType(getName());

        return classManager.getVirtualType(componentType);
    }

}
