package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

internal val THROWABLE_TYPE: VirtualType = VirtualType.of("Ljava/lang/Throwable;")

/** Evidence supplied before one throw commits to its arm-owned frame. */
internal sealed interface ThrowEvidence {
    data class Existing(val frameValue: FrameValue) : ThrowEvidence {
        constructor(value: Value) : this(FrameValue.plain(value))
        val value: Value get() = frameValue.value
    }

    data class Fresh(val recipe: ThrowableRecipe) : ThrowEvidence
}

/**
 * One atomic value held inside a frame.
 *
 * Public state APIs project this back to [Value]. The refined form is internal path evidence. Keeping the
 * value and its throwable domain in one object prevents register, slot, and call transport from separating
 * the two facts during copies or writes.
 */
internal sealed class FrameValue private constructor() {
    abstract val value: Value

    class Plain private constructor(override val value: Value) : FrameValue() {
        override fun equals(other: Any?): Boolean = other is Plain && value == other.value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Plain($value)"

        companion object {
            internal fun of(value: Value): Plain = Plain(value)
        }
    }

    class RefinedThrowable private constructor(
        override val value: Value,
        val domain: ThrowableDomain,
    ) : FrameValue() {
        init {
            require((value as? Value.Known)?.raw.let { it == null || it is HeapRef }) {
                "A known refined throwable must hold a HeapRef"
            }
            require(!value.readsAsNullReference) { "A refined throwable cannot be a null reference" }
        }

        override fun equals(other: Any?): Boolean = other is RefinedThrowable &&
            value == other.value && domain == other.domain

        override fun hashCode(): Int = 31 * value.hashCode() + domain.hashCode()
        override fun toString(): String = "RefinedThrowable($value, $domain)"

        companion object {
            internal fun of(value: Value, domain: ThrowableDomain): RefinedThrowable =
                RefinedThrowable(value, domain)
        }
    }

    companion object {
        fun plain(value: Value): FrameValue = Plain.of(value)

        internal fun refined(value: Value, domain: ThrowableDomain): RefinedThrowable =
            RefinedThrowable.of(value, domain)
    }
}

/** One non-empty throwable domain, including object-local runtime facts when a host object supplied them. */
internal class ThrowableDomain private constructor(
    val exactRuntimeType: VirtualType?,
    val exactRuntimeFacts: ThrowableTypeFacts?,
    val trustedUpperBound: VirtualType,
    val unresolvedRequiredRoots: PersistentSet<VirtualType>,
    val excludedRoots: PersistentSet<VirtualType>,
    private val requiredRootsHash: Int,
    private val excludedRootsHash: Int,
) {
    private val cachedHash: Int = run {
        var result = exactRuntimeType?.hashCode() ?: 0
        result = 31 * result + trustedUpperBound.hashCode()
        result = 31 * result + requiredRootsHash
        31 * result + excludedRootsHash
    }
    internal fun sourceKeyHash(metrics: ThrowableDomainHashMetricsCounter): Int {
        metrics.hashReads++
        return cachedHash
    }

    internal fun requireUnresolvedRoot(
        type: VirtualType,
        metrics: ResolverMetricsCounter,
    ): ThrowableDomain? {
        validateNewRoot(type, metrics)
        if (type in excludedRoots) return null
        if (type == exactRuntimeType || type == trustedUpperBound) return this
        val updated = unresolvedRequiredRoots.adding(type)
        if (updated === unresolvedRequiredRoots) return this
        metrics.persistentUpdates++
        return create(
            exactRuntimeType,
            exactRuntimeFacts,
            trustedUpperBound,
            updated,
            excludedRoots,
            requiredRootsHash + type.hashCode(),
            excludedRootsHash,
            metrics,
        )
    }

    internal fun narrowUpperBound(
        type: VirtualType,
        metrics: ResolverMetricsCounter,
    ): ThrowableDomain? {
        validateNewRoot(type, metrics)
        if (type in excludedRoots) return null
        if (type == trustedUpperBound) return this
        return create(
            exactRuntimeType,
            exactRuntimeFacts,
            type,
            unresolvedRequiredRoots,
            excludedRoots,
            requiredRootsHash,
            excludedRootsHash,
            metrics,
        )
    }

    internal fun exclude(
        type: VirtualType,
        metrics: ResolverMetricsCounter,
    ): ThrowableDomain? {
        validateNewRoot(type, metrics)
        if (exactRuntimeType == type || trustedUpperBound == type || type in unresolvedRequiredRoots) return null
        val updated = excludedRoots.adding(type)
        if (updated === excludedRoots) return this
        metrics.persistentUpdates++
        return create(
            exactRuntimeType,
            exactRuntimeFacts,
            trustedUpperBound,
            unresolvedRequiredRoots,
            updated,
            requiredRootsHash,
            excludedRootsHash + type.hashCode(),
            metrics,
        )
    }

    private fun validateNewRoot(type: VirtualType, metrics: ResolverMetricsCounter) {
        metrics.domainValidationVisits++
        require(type.isReference) { "A throwable refinement root must be a reference type" }
    }

    override fun equals(other: Any?): Boolean = this === other || other is ThrowableDomain &&
        exactRuntimeType == other.exactRuntimeType &&
        exactRuntimeFacts == other.exactRuntimeFacts &&
        trustedUpperBound == other.trustedUpperBound &&
        unresolvedRequiredRoots == other.unresolvedRequiredRoots &&
        excludedRoots == other.excludedRoots

    override fun hashCode(): Int = cachedHash

    override fun toString(): String =
        "ThrowableDomain(exact=$exactRuntimeType, upper=$trustedUpperBound, " +
            "required=$unresolvedRequiredRoots, excluded=$excludedRoots)"

    companion object {
        private val anyThrowable = ThrowableDomain(
            exactRuntimeType = null,
            exactRuntimeFacts = null,
            trustedUpperBound = THROWABLE_TYPE,
            unresolvedRequiredRoots = persistentSetOf(),
            excludedRoots = persistentSetOf(),
            requiredRootsHash = 0,
            excludedRootsHash = 0,
        )

        fun any(): ThrowableDomain = anyThrowable

        fun exact(
            type: VirtualType,
            hierarchy: ThrowableHierarchy,
            runtimeFacts: ThrowableTypeFacts? = null,
        ): ThrowableDomain {
            require(type.isReference) { "An exact throwable runtime type must be a reference type" }
            require(runtimeFacts == null || runtimeFacts.type == type) {
                "Exact runtime facts for ${runtimeFacts?.type} cannot describe $type"
            }
            val relation = runtimeFacts?.throwableRelation ?: hierarchy.relation(type, THROWABLE_TYPE)
            require(relation != TypeRelation.NO) {
                "An exact throwable runtime type must be Throwable-compatible: $type"
            }
            return ThrowableDomain(
                type,
                runtimeFacts,
                THROWABLE_TYPE,
                persistentSetOf(),
                persistentSetOf(),
                0,
                0,
            )
        }

        internal fun constrainedTo(type: VirtualType, hierarchy: ThrowableHierarchy): ThrowableDomain {
            require(type.isReference) { "A throwable constraint must be a reference type" }
            require(hierarchy.relation(type, THROWABLE_TYPE) == TypeRelation.YES) {
                "A trusted throwable upper bound must be a known Throwable subtype: $type"
            }
            return if (type == THROWABLE_TYPE) any() else {
                ThrowableDomain(null, null, type, persistentSetOf(), persistentSetOf(), 0, 0)
            }
        }

        internal fun unresolved(type: VirtualType, hierarchy: ThrowableHierarchy): ThrowableDomain {
            require(type.isReference) { "A throwable constraint must be a reference type" }
            require(hierarchy.relation(type, THROWABLE_TYPE) == TypeRelation.UNKNOWN) {
                "An unresolved throwable root must have an unknown Throwable relation: $type"
            }
            return ThrowableDomain(
                null,
                null,
                THROWABLE_TYPE,
                persistentSetOf(type),
                persistentSetOf(),
                type.hashCode(),
                0,
            )
        }

        private fun create(
            exactRuntimeType: VirtualType?,
            exactRuntimeFacts: ThrowableTypeFacts?,
            trustedUpperBound: VirtualType,
            unresolvedRequiredRoots: PersistentSet<VirtualType>,
            excludedRoots: PersistentSet<VirtualType>,
            requiredRootsHash: Int,
            excludedRootsHash: Int,
            metrics: ResolverMetricsCounter,
        ): ThrowableDomain {
            metrics.domainWrappers++
            metrics.observeRoots(unresolvedRequiredRoots.size, excludedRoots.size)
            return ThrowableDomain(
                exactRuntimeType,
                exactRuntimeFacts,
                trustedUpperBound,
                unresolvedRequiredRoots,
                excludedRoots,
                requiredRootsHash,
                excludedRootsHash,
            )
        }
    }
}

/** One validated refined frame value used for handler routing. */
internal class ThrownSubject private constructor(val frameValue: FrameValue.RefinedThrowable) {
    val value: Value get() = frameValue.value
    val concreteRef: HeapRef? get() = (value as? Value.Known)?.raw as? HeapRef
    val domain: ThrowableDomain get() = frameValue.domain

    internal fun refinedTo(domain: ThrowableDomain, hierarchy: ThrowableHierarchy): ThrownSubject {
        if (domain === this.domain) return this
        val runtimeType = this.domain.exactRuntimeType
        if (concreteRef != null) {
            require(domain.exactRuntimeType == runtimeType) {
                "A concrete throwable refinement cannot replace runtime type $runtimeType with ${domain.exactRuntimeType}"
            }
            require(runtimeType != null && hierarchy.relation(runtimeType, domain.trustedUpperBound) != TypeRelation.NO) {
                "Concrete throwable $runtimeType is outside trusted upper bound ${domain.trustedUpperBound}"
            }
        }
        return ThrownSubject(FrameValue.refined(value, domain))
    }

    internal fun validateInFrame(frame: Frame, hierarchy: ThrowableHierarchy) {
        validateReferenceInFrame(frame)
        val ref = concreteRef ?: return
        val runtimeType = requireNotNull(frame.heapObject(ref)).type
        require(domain.exactRuntimeType == runtimeType) {
            "Refined throwable domain ${domain.exactRuntimeType} disagrees with heap runtime type $runtimeType"
        }
        require(hierarchy.relation(runtimeType, THROWABLE_TYPE) != TypeRelation.NO) {
            "Concrete thrown ref $ref denotes non-throwable $runtimeType"
        }
        require(hierarchy.relation(runtimeType, domain.trustedUpperBound) != TypeRelation.NO) {
            "Concrete throwable $runtimeType is outside trusted upper bound ${domain.trustedUpperBound}"
        }
    }

    internal fun validateReferenceInFrame(frame: Frame) {
        val ref = concreteRef ?: return
        val runtimeType = requireNotNull(frame.heapObject(ref)) {
            "Refined throwable ref $ref does not exist in its frame"
        }.type
        require(domain.exactRuntimeType == runtimeType) {
            "Refined throwable domain ${domain.exactRuntimeType} disagrees with heap runtime type $runtimeType"
        }
    }

    override fun equals(other: Any?): Boolean = other is ThrownSubject &&
        frameValue == other.frameValue

    override fun hashCode(): Int {
        return frameValue.hashCode()
    }

    override fun toString(): String = "ThrownSubject(value=$value, ref=$concreteRef, domain=$domain)"

    companion object {
        fun symbolic(value: Value, domain: ThrowableDomain): ThrownSubject {
            require((value as? Value.Known)?.raw !is HeapRef) {
                "A known HeapRef throwable must be validated against its heap"
            }
            return ThrownSubject(FrameValue.refined(value, domain))
        }

        fun fromHeap(value: Value.Known, frame: Frame, hierarchy: ThrowableHierarchy): ThrownSubject {
            val ref = value.raw as? HeapRef
                ?: throw IllegalArgumentException("A concrete thrown value must hold a HeapRef: $value")
            val runtimeType = requireNotNull(frame.heapObject(ref)) {
                "Concrete thrown ref $ref does not exist in its arm frame"
            }.type
            require(hierarchy.facts(runtimeType).throwableRelation != TypeRelation.NO) {
                "Concrete thrown ref $ref denotes non-throwable $runtimeType"
            }
            val runtimeFacts = (frame.hostObjectAt(ref) as? Throwable)
                ?.let { host -> trustedHostThrowableFacts(host.javaClass) }
                ?.takeIf { it.type == runtimeType }
            return ThrownSubject(
                FrameValue.refined(value, ThrowableDomain.exact(runtimeType, hierarchy, runtimeFacts)),
            )
        }

        fun fromFrameValue(
            value: FrameValue.RefinedThrowable,
            frame: Frame,
            hierarchy: ThrowableHierarchy,
        ): ThrownSubject {
            return ThrownSubject(value).also { it.validateInFrame(frame, hierarchy) }
        }
    }
}

/** Complete JVM ancestry captured only from an imported throwable's actual runtime class. */
private fun trustedHostThrowableFacts(hostClass: Class<*>): ThrowableTypeFacts {
    require(Throwable::class.java.isAssignableFrom(hostClass)) {
        "Host throwable evidence requires a Throwable class"
    }
    var classAncestors = persistentSetOf<VirtualType>()
    var interfaceAncestors = persistentSetOf<VirtualType>()

    fun collectInterfaces(type: Class<*>) {
        for (hostInterface in type.interfaces) {
            val interfaceType = virtualTypeOfHostClass(hostInterface)
            if (interfaceType in interfaceAncestors) continue
            interfaceAncestors = interfaceAncestors.adding(interfaceType)
            collectInterfaces(hostInterface)
        }
    }

    var current: Class<*>? = hostClass
    while (current != null) {
        classAncestors = classAncestors.adding(virtualTypeOfHostClass(current))
        collectInterfaces(current)
        current = current.superclass
    }
    return ThrowableTypeFacts(
        type = virtualTypeOfHostClass(hostClass),
        knownClassAncestors = classAncestors,
        knownInterfaceAncestors = interfaceAncestors,
        ancestryComplete = true,
        interfaceAncestryComplete = true,
        throwableRelation = TypeRelation.YES,
        kind = ThrowableTypeKind.CLASS,
        descriptorPresent = true,
    )
}

/** A supported fresh throwable construction. It allocates nothing. */
internal class ThrowableRecipe private constructor(
    val type: VirtualType,
    val message: String?,
    val cause: ThrownSubject?,
) {
    companion object {
        private val supported = setOf(
            "Ljava/lang/ArithmeticException;",
            "Ljava/lang/ArrayIndexOutOfBoundsException;",
            "Ljava/lang/ArrayStoreException;",
            "Ljava/lang/ClassCastException;",
            "Ljava/lang/ExceptionInInitializerError;",
            "Ljava/lang/InternalError;",
            "Ljava/lang/NegativeArraySizeException;",
            "Ljava/lang/NoClassDefFoundError;",
            "Ljava/lang/NoSuchFieldError;",
            "Ljava/lang/NoSuchMethodError;",
            "Ljava/lang/NullPointerException;",
        )

        fun of(type: VirtualType, message: String? = null, cause: ThrownSubject? = null): ThrowableRecipe {
            require(type.descriptor in supported) { "No throwable construction rule exists for $type" }
            return ThrowableRecipe(type, message, cause)
        }
    }
}

/** One normalized throw path. The frame and subject cannot be transported independently. */
internal class ThrowPath private constructor(
    val frame: Frame,
    val subject: ThrownSubject,
) {
    companion object {
        fun create(frame: Frame, subject: ThrownSubject, hierarchy: ThrowableHierarchy): ThrowPath {
            subject.validateInFrame(frame, hierarchy)
            return ThrowPath(frame, subject)
        }
    }
}
