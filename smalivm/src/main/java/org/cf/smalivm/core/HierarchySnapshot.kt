package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

internal data class ClassHierarchyDeclaration(
    val superclass: String?,
    val interfaces: PersistentList<String>,
    val accessFlags: Int,
    val framework: Boolean,
)

/** Complete immutable hierarchy facts captured with a program snapshot. */
class HierarchySnapshot internal constructor(
    internal val declarations: PersistentMap<String, ClassHierarchyDeclaration>,
) : TypeHierarchy, Superclasses, InstanceOfTest {
    private val throwableFacts = HashMap<VirtualType, ThrowableTypeFacts>()
    internal val throwableHierarchy: ThrowableHierarchy = ThrowableHierarchy(::throwableFactsFor)

    fun contains(type: String): Boolean = declarations.containsKey(type)

    override fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean {
        if (candidate == ancestor) return true
        if (!candidate.isReference || !ancestor.isReference) return false
        if (!isLoadable(candidate) || !isLoadable(ancestor)) {
            throw ClassLoadFailure(
                "Program snapshot cannot answer whether ${candidate.descriptor} is a subtype of ${ancestor.descriptor}",
                IllegalStateException("A type is absent from the program snapshot"),
            )
        }
        if (ancestor == VirtualType.OBJECT) return true
        if (candidate.isArray) return isArraySubtype(candidate, ancestor)

        val pending = ArrayDeque<String>()
        val seen = mutableSetOf<String>()
        pending.addLast(candidate.descriptor)
        while (pending.isNotEmpty()) {
            val descriptor = pending.removeFirst()
            if (!seen.add(descriptor)) continue
            if (descriptor == ancestor.descriptor) return true
            val declaration = declarations[descriptor] ?: throw ClassLoadFailure(
                "Program snapshot cannot answer whether ${candidate.descriptor} is a subtype of ${ancestor.descriptor}",
                IllegalStateException("No declaration for $descriptor in the program snapshot"),
            )
            declaration.superclass?.let(pending::addLast)
            declaration.interfaces.forEach(pending::addLast)
        }
        return false
    }

    override fun isInstanceOf(candidate: VirtualType, target: VirtualType): Boolean {
        if (!target.isReference) return false
        if (candidate == target) return true
        if (target == VirtualType.OBJECT) return candidate.isReference
        if (candidate.isArray && target.descriptor in ARRAY_SUPERINTERFACES) return true
        if (!candidate.isReference) return false
        return isSubtypeOf(candidate, target)
    }

    override fun superclassOf(type: VirtualType): VirtualType? {
        if (!type.isReference) return null
        if (type.isArray) return VirtualType.OBJECT
        if (type == VirtualType.OBJECT) return null
        val declaration = declarations[type.descriptor] ?: throw ClassLoadFailure(
            "Program snapshot cannot answer what ${type.descriptor} extends",
            IllegalStateException("No declaration for ${type.descriptor} in the program snapshot"),
        )
        return declaration.superclass?.let(VirtualType::of)
    }

    internal fun isLoadable(type: VirtualType): Boolean {
        var base = type
        while (base.isArray) base = requireNotNull(base.componentType)
        return !base.isReference || contains(base.descriptor)
    }

    internal fun isFrameworkClass(type: VirtualType): Boolean = declarations[type.descriptor]?.framework == true

    internal fun isInterface(type: VirtualType): Boolean {
        if (!type.isReference || type.isArray) return false
        val declaration = declarations[type.descriptor] ?: return false
        return java.lang.reflect.Modifier.isInterface(declaration.accessFlags)
    }

    internal fun directInterfaces(type: VirtualType): List<VirtualType> =
        declarations[type.descriptor]?.interfaces?.map(VirtualType::of).orEmpty()

    private fun isArraySubtype(candidate: VirtualType, ancestor: VirtualType): Boolean {
        if (ancestor.descriptor in ARRAY_SUPERINTERFACES) return true
        if (!ancestor.isArray) return false
        val candidateComponent = requireNotNull(candidate.componentType)
        val ancestorComponent = requireNotNull(ancestor.componentType)
        if (!candidateComponent.isReference || !ancestorComponent.isReference) {
            return candidateComponent == ancestorComponent
        }
        return isSubtypeOf(candidateComponent, ancestorComponent)
    }

    @Synchronized
    private fun throwableFactsFor(type: VirtualType): ThrowableTypeFacts =
        throwableFacts[type] ?: buildThrowableFacts(type)

    private fun buildThrowableFacts(root: VirtualType): ThrowableTypeFacts {
        val visiting = mutableSetOf<VirtualType>()
        val pending = ArrayDeque<ThrowableFactsBuildFrame>()
        pending.addLast(ThrowableFactsBuildFrame(root))
        var completed: ThrowableTypeFacts? = null

        fun cycleFacts(type: VirtualType): ThrowableTypeFacts = ThrowableTypeFacts(
            type,
            persistentSetOf(),
            persistentSetOf(),
            ancestryComplete = false,
            interfaceAncestryComplete = false,
            throwableRelation = TypeRelation.UNKNOWN,
            kind = ThrowableTypeKind.CLASS,
            descriptorPresent = true,
        )

        fun finish(frame: ThrowableFactsBuildFrame, facts: ThrowableTypeFacts) {
            throwableFacts[frame.type] = facts
            visiting.remove(frame.type)
            pending.removeLast()
            completed = facts
        }

        fun acceptInterface(frame: ThrowableFactsBuildFrame, interfaceType: VirtualType, facts: ThrowableTypeFacts) {
            frame.knownInterfaces = frame.knownInterfaces.adding(interfaceType).addingAll(facts.knownInterfaceAncestors)
            frame.interfacesComplete = frame.interfacesComplete &&
                facts.descriptorPresent &&
                facts.kind == ThrowableTypeKind.INTERFACE &&
                facts.interfaceAncestryComplete
            frame.nextInterface++
        }

        while (pending.isNotEmpty()) {
            val frame = pending.last()
            val waitingFor = frame.waitingFor
            if (waitingFor != null) {
                val facts = requireNotNull(completed)
                completed = null
                frame.waitingFor = null
                when (waitingFor) {
                    ThrowableDependency.INTERFACE -> {
                        val descriptor = requireNotNull(frame.declaration)
                        acceptInterface(frame, VirtualType.of(descriptor.interfaces[frame.nextInterface]), facts)
                    }
                    ThrowableDependency.SUPERCLASS -> {
                        frame.parentFacts = facts
                        frame.parentResolved = true
                    }
                }
                continue
            }

            if (frame.declaration == null) {
                throwableFacts[frame.type]?.let {
                    pending.removeLast()
                    completed = it
                    continue
                }
                if (!frame.type.isReference) {
                    finish(frame, terminalThrowableFacts(frame.type, TypeRelation.NO, ThrowableTypeKind.UNKNOWN))
                    continue
                }
                if (frame.type.isArray) {
                    finish(
                        frame,
                        ThrowableTypeFacts(
                            frame.type,
                            persistentSetOf(frame.type, VirtualType.OBJECT),
                            persistentSetOf(*ARRAY_SUPERINTERFACES.map(VirtualType::of).toTypedArray()),
                            ancestryComplete = true,
                            interfaceAncestryComplete = true,
                            throwableRelation = TypeRelation.NO,
                            kind = ThrowableTypeKind.CLASS,
                            descriptorPresent = true,
                        ),
                    )
                    continue
                }
                check(visiting.add(frame.type)) { "Throwable fact worklist scheduled a cycle as new work" }
                val declaration = declarations[frame.type.descriptor]
                if (declaration == null) {
                    finish(
                        frame,
                        ThrowableTypeFacts(
                            frame.type,
                            persistentSetOf(),
                            persistentSetOf(),
                            ancestryComplete = false,
                            interfaceAncestryComplete = false,
                            throwableRelation = TypeRelation.UNKNOWN,
                            kind = ThrowableTypeKind.UNKNOWN,
                            descriptorPresent = false,
                        ),
                    )
                    continue
                }
                frame.declaration = declaration
            }

            val declaration = requireNotNull(frame.declaration)
            if (frame.nextInterface < declaration.interfaces.size) {
                val interfaceType = VirtualType.of(declaration.interfaces[frame.nextInterface])
                val facts = throwableFacts[interfaceType]
                when {
                    facts != null -> acceptInterface(frame, interfaceType, facts)
                    interfaceType in visiting -> acceptInterface(frame, interfaceType, cycleFacts(interfaceType))
                    else -> {
                        frame.waitingFor = ThrowableDependency.INTERFACE
                        pending.addLast(ThrowableFactsBuildFrame(interfaceType))
                    }
                }
                continue
            }

            if (java.lang.reflect.Modifier.isInterface(declaration.accessFlags)) {
                finish(
                    frame,
                    ThrowableTypeFacts(
                        frame.type,
                        persistentSetOf(VirtualType.OBJECT),
                        frame.knownInterfaces,
                        ancestryComplete = true,
                        interfaceAncestryComplete = frame.interfacesComplete,
                        throwableRelation = TypeRelation.NO,
                        kind = ThrowableTypeKind.INTERFACE,
                        descriptorPresent = true,
                    ),
                )
                continue
            }

            val superclass = declaration.superclass?.let(VirtualType::of)
            if (!frame.parentResolved && superclass != null) {
                val facts = throwableFacts[superclass]
                when {
                    facts != null -> {
                        frame.parentFacts = facts
                        frame.parentResolved = true
                    }
                    superclass in visiting -> {
                        frame.parentFacts = cycleFacts(superclass)
                        frame.parentResolved = true
                    }
                    else -> {
                        frame.waitingFor = ThrowableDependency.SUPERCLASS
                        pending.addLast(ThrowableFactsBuildFrame(superclass))
                    }
                }
                continue
            }
            frame.parentResolved = true
            val parent = frame.parentFacts
            val ancestors = persistentSetOf(frame.type)
                .addingAll(listOfNotNull(superclass))
                .addingAll(parent?.knownClassAncestors ?: persistentSetOf())
            val complete = parent?.ancestryComplete ?: true
            val knownInterfaces = frame.knownInterfaces.addingAll(parent?.knownInterfaceAncestors ?: persistentSetOf())
            val interfacesComplete = frame.interfacesComplete && (parent?.interfaceAncestryComplete ?: true)
            finish(
                frame,
                ThrowableTypeFacts(
                    frame.type,
                    ancestors,
                    knownInterfaces,
                    ancestryComplete = complete,
                    interfaceAncestryComplete = interfacesComplete,
                    throwableRelation = when {
                        THROWABLE_TYPE in ancestors -> TypeRelation.YES
                        complete -> TypeRelation.NO
                        else -> TypeRelation.UNKNOWN
                    },
                    kind = ThrowableTypeKind.CLASS,
                    descriptorPresent = true,
                ),
            )
        }
        return requireNotNull(completed)
    }

    private fun terminalThrowableFacts(
        type: VirtualType,
        relation: TypeRelation,
        kind: ThrowableTypeKind,
    ): ThrowableTypeFacts = ThrowableTypeFacts(
        type,
        persistentSetOf(),
        persistentSetOf(),
        ancestryComplete = true,
        interfaceAncestryComplete = true,
        throwableRelation = relation,
        kind = kind,
        descriptorPresent = true,
    )

    private enum class ThrowableDependency { INTERFACE, SUPERCLASS }

    private class ThrowableFactsBuildFrame(val type: VirtualType) {
        var declaration: ClassHierarchyDeclaration? = null
        var nextInterface = 0
        var knownInterfaces: PersistentSet<VirtualType> = persistentSetOf()
        var interfacesComplete = true
        var parentFacts: ThrowableTypeFacts? = null
        var parentResolved = false
        var waitingFor: ThrowableDependency? = null
    }

    internal companion object {
        private val ARRAY_SUPERINTERFACES = setOf("Ljava/lang/Cloneable;", "Ljava/io/Serializable;")
        private val THROWABLE_TYPE = VirtualType.of("Ljava/lang/Throwable;")

        /** [applicationClasses] are the classes the importer defined, so none shares a name with another or the framework. */
        fun from(
            applicationClasses: Collection<ApplicationClassDefinition>,
            frameworkArtifact: FrameworkArtifact,
        ): HierarchySnapshot {
            val declarations = LinkedHashMap<String, ClassHierarchyDeclaration>()
            frameworkArtifact.classes.values.forEach { definition ->
                declarations[definition.type] = ClassHierarchyDeclaration(
                    definition.superclass,
                    definition.interfaces,
                    definition.accessFlags,
                    framework = true,
                )
            }
            applicationClasses.forEach { definition ->
                val previous = declarations.put(
                    definition.type,
                    ClassHierarchyDeclaration(
                        definition.superclass,
                        definition.interfaces.toPersistentList(),
                        definition.accessFlags,
                        framework = false,
                    ),
                )
                require(previous == null) { "Application class ${definition.type} is defined twice" }
            }
            declarations.keys.removeAll(circularClasses(declarations))
            return HierarchySnapshot(declarations.toPersistentMap())
        }

        /**
         * Classes whose superclass or interface chain reaches themselves. ART's class linker rejects each one
         * with `ClassCircularityError` while loading it, so none of them is in the loaded set. Computed as the
         * strongly connected components of the declared-ancestor graph, iteratively so inheritance depth
         * cannot exhaust the host stack.
         */
        private fun circularClasses(declarations: Map<String, ClassHierarchyDeclaration>): Set<String> {
            fun ancestors(descriptor: String): Iterator<String> {
                val declaration = declarations.getValue(descriptor)
                return (listOfNotNull(declaration.superclass) + declaration.interfaces)
                    .filter(declarations::containsKey)
                    .iterator()
            }

            val order = HashMap<String, Int>()
            val lowLink = HashMap<String, Int>()
            val open = ArrayDeque<String>()
            val isOpen = HashSet<String>()
            val frames = ArrayDeque<Pair<String, Iterator<String>>>()
            val circular = HashSet<String>()

            fun enter(descriptor: String) {
                order[descriptor] = order.size
                lowLink[descriptor] = order.getValue(descriptor)
                open.addLast(descriptor)
                isOpen.add(descriptor)
                frames.addLast(descriptor to ancestors(descriptor))
            }

            for (root in declarations.keys) {
                if (root in order) continue
                enter(root)
                while (frames.isNotEmpty()) {
                    val (descriptor, pending) = frames.last()
                    if (pending.hasNext()) {
                        val ancestor = pending.next()
                        if (ancestor !in order) {
                            enter(ancestor)
                        } else if (ancestor in isOpen) {
                            lowLink[descriptor] = minOf(lowLink.getValue(descriptor), order.getValue(ancestor))
                        }
                        continue
                    }
                    frames.removeLast()
                    frames.lastOrNull()?.let { (parent, _) ->
                        lowLink[parent] = minOf(lowLink.getValue(parent), lowLink.getValue(descriptor))
                    }
                    if (lowLink.getValue(descriptor) != order.getValue(descriptor)) continue
                    val component = ArrayList<String>()
                    do {
                        val member = open.removeLast()
                        isOpen.remove(member)
                        component += member
                    } while (member != descriptor)
                    val declaration = declarations.getValue(descriptor)
                    if (component.size > 1 || declaration.superclass == descriptor || descriptor in declaration.interfaces) {
                        circular += component
                    }
                }
            }
            return circular
        }
    }
}
