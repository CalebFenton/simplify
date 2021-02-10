package org.cf.smalivm.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;

public abstract class VirtualType {

    private static final Logger log = LoggerFactory.getLogger(VirtualType.class.getSimpleName());

    protected final ClassManager classManager;
    private final TypeReference typeReference;
    private final String internalName;
    private final String binaryName;
    private final String sourceName;

    VirtualType(TypeReference typeReference, ClassManager classManager) {
        this.typeReference = typeReference;
        this.classManager = classManager;
        this.internalName = typeReference.getType();
        this.binaryName = ClassNameUtils.internalToBinary(typeReference.getType());
        this.sourceName = ClassNameUtils.internalToSource(typeReference.getType());
    }

    public abstract Set<? extends VirtualType> getImmediateAncestors();

    public abstract Set<? extends VirtualType> getAncestors();

    public boolean isAncestorOf(VirtualType other) {
        return other.getAncestors().contains(this);
    }

    public boolean isChildOf(VirtualType other) {
        return getAncestors().contains(other);
    }

    public String getPackage() {
        return null;
    }

    public String getBinaryName() {
        return binaryName;
    }

    public
    @Nullable
    VirtualField getField(String fieldName) {
        return null;
    }

    public abstract Collection<VirtualField> getFields();

    public
    @Nullable
    VirtualMethod getMethod(String methodDescriptor) {
        return null;
    }

    public abstract Collection<VirtualMethod> getMethods();

    public String getName() {
        return internalName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public final TypeReference getTypeReference() {
        return typeReference;
    }

    public BuilderClassDef getClassDef() {
        return null;
    }

    public boolean isInnerClassOf(VirtualType other) {
        return false;
    }

    public boolean isSamePackageOf(VirtualType other) {
        return false;
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder(423, 15).append(getName()).hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        VirtualType rhs = (VirtualType) obj;

        return new EqualsBuilder().append(getName(), rhs.getName()).isEquals();
    }

    @Override
    public final String toString() {
        return getName();
    }

    public abstract boolean instanceOf(VirtualType targetType);

    public abstract boolean isArray();

    public abstract boolean isPrimitive();
}
