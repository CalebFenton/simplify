package org.cf.smalivm.core

/** Session-local resolution boundary over one captured program snapshot. */
internal class ProgramSnapshotResolver(
    val snapshot: ProgramSnapshot,
    internal val classIndex: ClassIndex,
) {
    val hierarchy: HierarchySnapshot get() = snapshot.hierarchy
    val throwableHierarchy: ThrowableHierarchy get() = hierarchy.throwableHierarchy
    val classLoader: ClassLoader get() = classIndex.classLoader

    fun owns(view: MethodView): Boolean = snapshot.owns(view)

    fun typeOf(descriptor: String): VirtualType = VirtualType.of(descriptor)

    fun isLoadable(type: VirtualType): Boolean = hierarchy.isLoadable(type)

    fun isFrameworkClass(type: VirtualType): Boolean = hierarchy.isFrameworkClass(type)

    fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean =
        hierarchy.isSubtypeOf(candidate, ancestor)

    fun superclassOf(type: VirtualType): VirtualType? = hierarchy.superclassOf(type)

    fun loadClassConstant(descriptor: String): Class<*>? = classIndex.loadClassConstant(descriptor)

    fun instanceFieldDefaults(type: VirtualType): Map<String, Value>? = classIndex.instanceFieldDefaults(type)

    fun resolveStaticField(
        referencedClass: VirtualType,
        name: String,
        fieldTypeDescriptor: String,
    ): StaticFieldLookup = classIndex.resolveStaticField(referencedClass, name, fieldTypeDescriptor)

    fun resolveInstanceField(
        referencedClass: VirtualType,
        name: String,
        fieldTypeDescriptor: String,
    ): InstanceFieldLookup = classIndex.resolveInstanceField(referencedClass, name, fieldTypeDescriptor)

    fun resolveInvocation(
        target: MethodDescriptor,
        kind: InvokeKind,
        callerClass: VirtualType,
        receiverType: VirtualType?,
    ): InvocationLookup = classIndex.resolveInvocation(target, kind, callerClass, receiverType)

    fun hostInitializationChain(type: VirtualType): List<VirtualType> = classIndex.hostInitializationChain(type)

    fun hasClassInitializer(type: VirtualType): Boolean = classIndex.hasClassInitializer(type)

    fun lookupClassInitializer(type: VirtualType): MethodLookup = classIndex.lookupClassInitializer(type)

    fun declaredStaticFields(type: VirtualType): List<StaticFieldDeclaration> = classIndex.declaredStaticFields(type)
}
