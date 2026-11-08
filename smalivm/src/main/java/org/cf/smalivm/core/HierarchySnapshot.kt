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

/**
 * A hierarchy question about a well-formed descriptor has no answer because the program does not contain a class
 * the answer depends on. Obfuscated input names absent classes routinely, so this is a fact about the input that
 * callers answer conservatively, and it carries no stack trace.
 */
class ClassLoadFailure internal constructor(message: String) : RuntimeException(message, null, false, false)

/**
 * Class-hierarchy questions whose answer depends on declared ancestry. Each throws [ClassLoadFailure] when the
 * program does not contain a class the answer depends on, and never answers such a question with a guess.
 * [ThrowableHierarchy] answers throwable routing as tri-state facts instead.
 */
interface TypeHierarchy {
    /**
     * True if [candidate] is [ancestor] or inherits from it through superclasses, interfaces, or array
     * covariance. Primitives and `void` relate only to themselves.
     */
    fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean

    /**
     * Whether a value of type [candidate] passes `instance-of` [target]. A primitive or `void` target is never
     * satisfied ([BC] `instance-of`); for `check-cast` that is a definite `ClassCastException`. Every reference
     * is an instance of `Ljava/lang/Object;`, and every array of `Ljava/lang/Cloneable;` and
     * `Ljava/io/Serializable;` (JLS 4.10.3), with no class lookup, so obfuscated input that omits those classes
     * still answers. A primitive candidate reads as a null reference, which is an instance of nothing.
     */
    fun isInstanceOf(candidate: VirtualType, target: VirtualType): Boolean

    /**
     * The declared direct superclass of [type]: one link, so a walk over it is a total order. Null for
     * `Ljava/lang/Object;`, primitives, and `void`; arrays answer `Ljava/lang/Object;` (JLS 4.10.3).
     */
    fun superclassOf(type: VirtualType): VirtualType?
}

/**
 * How a class on an inheritance cycle reaches itself, in the order ART's `LoadSuperAndInterfaces` checks: a
 * superclass naming the class itself, a superclass on the cycle, then each interface in declaration order. It
 * decides the text of the `ClassCircularityError` that resolving the class throws.
 */
internal enum class ClassCircularity { EXTENDS_ITSELF, IMPLEMENTS_ITSELF, THROUGH_ANOTHER_CLASS }

/**
 * Complete immutable hierarchy facts captured with a program snapshot, and the one implementation of every
 * hierarchy query. Built only by [from], which removes circular classes, so every ancestor walk terminates.
 */
internal class HierarchySnapshot private constructor(
    internal val declarations: PersistentMap<String, ClassHierarchyDeclaration>,
    /** Every class reachable from an application class's superclass and interfaces is in the program. */
    internal val applicationAncestryComplete: Boolean,
    private val circularClasses: PersistentMap<String, ClassCircularity>,
) : TypeHierarchy, ThrowableHierarchy {
    private val throwableFacts = HashMap<VirtualType, ThrowableTypeFacts>()

    /**
     * Each instantiable application class under itself, `Ljava/lang/Object;`, and every ancestor its declarations
     * reach, in descriptor order. Built once so unknown-receiver dispatch does not walk the program per invoke.
     */
    private val applicationSubtypeIndex: Map<String, List<VirtualType>> by lazy {
        val index = HashMap<String, MutableList<VirtualType>>()
        val applicationTypes = declarations.filterValues { !it.framework && isInstantiable(it.accessFlags) }.keys
        for (descriptor in applicationTypes.sorted()) {
            val type = VirtualType.of(descriptor)
            val supertypes = declaredAncestors(descriptor) + descriptor + VirtualType.OBJECT.descriptor
            supertypes.forEach { index.getOrPut(it, ::ArrayList) += type }
        }
        index
    }

    fun contains(type: String): Boolean = declarations.containsKey(type)

    override fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean {
        if (candidate == ancestor) return true
        if (!candidate.isReference || !ancestor.isReference) return false
        if (!isLoadable(candidate) || !isLoadable(ancestor)) throw notASubtypeAnswer(candidate, ancestor)
        if (ancestor == VirtualType.OBJECT) return true
        if (candidate.isArray) return isArraySubtype(candidate, ancestor)

        val pending = ArrayDeque<String>()
        val seen = mutableSetOf<String>()
        pending.addLast(candidate.descriptor)
        while (pending.isNotEmpty()) {
            val descriptor = pending.removeFirst()
            if (!seen.add(descriptor)) continue
            if (descriptor == ancestor.descriptor) return true
            val declaration = declarations[descriptor] ?: throw notASubtypeAnswer(candidate, ancestor)
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
        val declaration = declarations[type.descriptor] ?: throw absent(type, "extends")
        return declaration.superclass?.let(VirtualType::of)
    }

    /**
     * The interfaces [type] declares, in declaration order. Primitives and arrays declare none. An absent class
     * throws [ClassLoadFailure] like [superclassOf]: an empty answer would claim the class implements nothing, and
     * a member walk that took it would report a definite miss for a member the absent interface may declare.
     */
    internal fun directInterfaces(type: VirtualType): List<VirtualType> {
        if (!type.isReference || type.isArray) return emptyList()
        val declaration = declarations[type.descriptor] ?: throw absent(type, "implements")
        return declaration.interfaces.map(VirtualType::of)
    }

    /** Whether the program contains [type]'s base class. Primitives and arrays of them always load. */
    internal fun isLoadable(type: VirtualType): Boolean {
        var base = type
        while (base.isArray) base = requireNotNull(base.componentType)
        return !base.isReference || contains(base.descriptor)
    }

    /** How [type]'s base class reaches itself, or null when it is on no inheritance cycle. */
    internal fun circularityOf(type: VirtualType): ClassCircularity? {
        var base = type
        while (base.isArray) base = requireNotNull(base.componentType)
        return circularClasses[base.descriptor]
    }

    /** Whether [type] is a bundled framework class rather than an application class; absent classes are neither. */
    internal fun isFrameworkClass(type: VirtualType): Boolean = declarations[type.descriptor]?.framework == true

    internal fun isInterface(type: VirtualType): Boolean {
        if (!type.isReference || type.isArray) return false
        val declaration = declarations[type.descriptor] ?: return false
        return java.lang.reflect.Modifier.isInterface(declaration.accessFlags)
    }

    internal fun isFinal(type: VirtualType): Boolean {
        if (!type.isReference || type.isArray) return false
        val declaration = declarations[type.descriptor] ?: return false
        return java.lang.reflect.Modifier.isFinal(declaration.accessFlags)
    }

    /** Instantiable application classes whose declared ancestry reaches [type], [type] included, in descriptor order. */
    internal fun instantiableApplicationSubtypes(type: VirtualType): List<VirtualType> =
        applicationSubtypeIndex[type.descriptor].orEmpty()

    /** Every class [descriptor]'s superclass and interface declarations reach, itself excluded; absent ones are skipped. */
    internal fun declaredAncestors(descriptor: String): Set<String> {
        val ancestors = LinkedHashSet<String>()
        val pending = ArrayDeque<String>()
        declarations[descriptor]?.let { pending.addParents(it) }
        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()
            if (!ancestors.add(current)) continue
            declarations[current]?.let { pending.addParents(it) }
        }
        return ancestors
    }

    @Synchronized
    override fun facts(type: VirtualType): ThrowableTypeFacts = throwableFacts[type] ?: buildThrowableFacts(type)

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

    private fun absent(type: VirtualType, relation: String) =
        ClassLoadFailure("The program cannot answer what ${type.descriptor} $relation")

    private fun notASubtypeAnswer(candidate: VirtualType, ancestor: VirtualType) = ClassLoadFailure(
        "The program cannot answer whether ${candidate.descriptor} is a subtype of ${ancestor.descriptor}",
    )

    private fun ArrayDeque<String>.addParents(declaration: ClassHierarchyDeclaration) {
        declaration.superclass?.let(::addLast)
        declaration.interfaces.forEach(::addLast)
    }

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
            val circular = circularClasses(declarations)
            declarations.keys.removeAll(circular.keys)
            return HierarchySnapshot(
                declarations.toPersistentMap(),
                ancestryIsComplete(applicationClasses.map(ApplicationClassDefinition::type), declarations),
                circular.toPersistentMap(),
            )
        }

        private fun isInstantiable(accessFlags: Int): Boolean =
            !java.lang.reflect.Modifier.isAbstract(accessFlags) && !java.lang.reflect.Modifier.isInterface(accessFlags)

        /** Whether every class reachable from [roots] through declared superclasses and interfaces is declared. */
        private fun ancestryIsComplete(roots: List<String>, declarations: Map<String, ClassHierarchyDeclaration>): Boolean {
            val pending = ArrayDeque(roots)
            val seen = HashSet<String>()
            while (pending.isNotEmpty()) {
                val descriptor = pending.removeLast()
                if (!seen.add(descriptor)) continue
                val declaration = declarations[descriptor] ?: return false
                declaration.superclass?.let(pending::addLast)
                pending.addAll(declaration.interfaces)
            }
            return true
        }

        /**
         * Classes whose superclass or interface chain reaches themselves, and how. ART's class linker rejects each
         * one with `ClassCircularityError` while loading it, so none of them is in the loaded set. Computed as the
         * strongly connected components of the declared-ancestor graph, iteratively so inheritance depth
         * cannot exhaust the host stack.
         */
        private fun circularClasses(
            declarations: Map<String, ClassHierarchyDeclaration>,
        ): Map<String, ClassCircularity> {
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
            val circular = HashMap<String, ClassCircularity>()

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
                        val members = component.toHashSet()
                        for (member in component) {
                            circular[member] = circularity(member, declarations.getValue(member), members)
                        }
                    }
                }
            }
            return circular
        }

        /** The first of [declaration]'s links that ART's `LoadSuperAndInterfaces` follows back into [cycle]. */
        private fun circularity(
            descriptor: String,
            declaration: ClassHierarchyDeclaration,
            cycle: Set<String>,
        ): ClassCircularity {
            if (declaration.superclass == descriptor) return ClassCircularity.EXTENDS_ITSELF
            if (declaration.superclass in cycle) return ClassCircularity.THROUGH_ANOTHER_CLASS
            val throughInterface = declaration.interfaces.first { it in cycle }
            return if (throughInterface == descriptor) {
                ClassCircularity.IMPLEMENTS_ITSELF
            } else {
                ClassCircularity.THROUGH_ANOTHER_CLASS
            }
        }
    }
}
