package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet

/** One typed catch clause. */
data class CatchClause(val exceptionType: VirtualType, val handlerAddress: Int)

/** One public try range. The range is start-inclusive and end-exclusive. */
data class TryBlock(
    val startAddress: Int,
    val endAddress: Int,
    val catches: List<CatchClause>,
    val catchAllAddress: Int?,
) {
    init {
        require(startAddress <= endAddress) {
            "TryBlock startAddress ($startAddress) must be <= endAddress ($endAddress)"
        }
    }

    fun covers(address: Int): Boolean = address in startAddress until endAddress
}

/** Boolean subtype capability used by ordinary value operations. It can throw [ClassLoadFailure]. */
fun interface TypeHierarchy {
    fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean
}

internal enum class TypeRelation { YES, NO, UNKNOWN }

internal enum class ThrowableTypeKind { CLASS, INTERFACE, UNKNOWN }

/** Immutable throwable facts from one loaded descriptor or one imported runtime class. */
internal data class ThrowableTypeFacts(
    val type: VirtualType,
    val knownClassAncestors: PersistentSet<VirtualType>,
    val knownInterfaceAncestors: PersistentSet<VirtualType>,
    val ancestryComplete: Boolean,
    val interfaceAncestryComplete: Boolean,
    val throwableRelation: TypeRelation,
    val kind: ThrowableTypeKind,
    val descriptorPresent: Boolean,
)

/** Absence-total throwable hierarchy, separate from Boolean subtyping. Present-model defects propagate. */
internal fun interface ThrowableHierarchy {
    fun facts(type: VirtualType): ThrowableTypeFacts

    fun relation(candidate: VirtualType, ancestor: VirtualType): TypeRelation = relationFromFacts(
        candidate,
        ancestor,
        facts(candidate),
        facts(ancestor),
    )
}

private fun relationFromFacts(
    candidate: VirtualType,
    ancestor: VirtualType,
    candidateFacts: ThrowableTypeFacts,
    ancestorFacts: ThrowableTypeFacts,
): TypeRelation = when {
    candidate == ancestor -> TypeRelation.YES
    ancestor in candidateFacts.knownClassAncestors -> TypeRelation.YES
    ancestor in candidateFacts.knownInterfaceAncestors -> TypeRelation.YES
    !candidateFacts.descriptorPresent || !ancestorFacts.descriptorPresent -> TypeRelation.UNKNOWN
    ancestorFacts.kind == ThrowableTypeKind.INTERFACE ->
        if (candidateFacts.interfaceAncestryComplete) TypeRelation.NO else TypeRelation.UNKNOWN
    ancestorFacts.kind == ThrowableTypeKind.CLASS && candidateFacts.kind == ThrowableTypeKind.INTERFACE ->
        if (ancestor == VirtualType.OBJECT) TypeRelation.YES else TypeRelation.NO
    ancestorFacts.kind == ThrowableTypeKind.CLASS && candidateFacts.kind == ThrowableTypeKind.CLASS ->
        if (candidateFacts.ancestryComplete) TypeRelation.NO else TypeRelation.UNKNOWN
    else -> TypeRelation.UNKNOWN
}

internal sealed interface HandlerClause {
    val handlerAddress: Int

    data class Typed(
        val exceptionType: VirtualType,
        override val handlerAddress: Int,
    ) : HandlerClause

    data class CatchAll(override val handlerAddress: Int) : HandlerClause
}

/** One canonical handler table after identical encoded ranges are grouped. */
internal data class HandlerTable(
    val startAddress: Int,
    val endAddress: Int,
    val clauses: PersistentList<HandlerClause>,
) {
    init {
        require(startAddress <= endAddress) { "Handler table range must be half-open and ordered" }
    }

    fun covers(address: Int): Boolean = address in startAddress until endAddress
}

internal sealed interface HandlerTableLookup {
    data object None : HandlerTableLookup
    data object MalformedMetadata : HandlerTableLookup
    data class Found(val table: HandlerTable) : HandlerTableLookup
}

/** One feasible first-match route with its refined thrown subject. */
internal data class HandlerRoute(
    val handlerAddress: Int,
    val subject: ThrownSubject,
)

internal data class UnhandledThrow(val subject: ThrownSubject)

internal data class HandlerResolverMetrics(
    val clausesVisited: Int,
    val factBuilds: Int,
    val relationRequests: Int,
    val relationCacheMisses: Int,
    val missingDescriptors: Int,
    val domainWrappers: Int,
    val persistentUpdates: Int,
    val domainValidationVisits: Int,
    val routes: Int,
    val maxRequiredRoots: Int,
    val maxExcludedRoots: Int,
)

internal class ResolverMetricsCounter {
    var clausesVisited: Int = 0
    var factBuilds: Int = 0
    var relationRequests: Int = 0
    var relationCacheMisses: Int = 0
    var missingDescriptors: Int = 0
    var domainWrappers: Int = 0
    var persistentUpdates: Int = 0
    var domainValidationVisits: Int = 0
    var routes: Int = 0
    var maxRequiredRoots: Int = 0
    var maxExcludedRoots: Int = 0

    fun observeRoots(required: Int, excluded: Int) {
        maxRequiredRoots = maxOf(maxRequiredRoots, required)
        maxExcludedRoots = maxOf(maxExcludedRoots, excluded)
    }

    fun snapshot(): HandlerResolverMetrics = HandlerResolverMetrics(
        clausesVisited = clausesVisited,
        factBuilds = factBuilds,
        relationRequests = relationRequests,
        relationCacheMisses = relationCacheMisses,
        missingDescriptors = missingDescriptors,
        domainWrappers = domainWrappers,
        persistentUpdates = persistentUpdates,
        domainValidationVisits = domainValidationVisits,
        routes = routes,
        maxRequiredRoots = maxRequiredRoots,
        maxExcludedRoots = maxExcludedRoots,
    )
}

internal sealed interface HandlerResolution {
    val metrics: HandlerResolverMetrics

    data class Routed(
        val routes: List<HandlerRoute>,
        val unhandled: UnhandledThrow?,
        override val metrics: HandlerResolverMetrics,
    ) : HandlerResolution

    data class MalformedMetadata(
        override val metrics: HandlerResolverMetrics,
    ) : HandlerResolution
}

private data class DomainPartition(
    val caught: ThrowableDomain?,
    val residual: ThrowableDomain?,
    val malformedMetadata: Boolean = false,
)

/** The intersection with one cast type and the excluded residual. Empty domains are null. */
internal data class ThrowableDomainPartition(
    val matching: ThrowableDomain?,
    val residual: ThrowableDomain?,
)

/** Uses the handler resolver's tri-state partition for throwable-preserving `check-cast`. */
internal fun partitionThrowableDomain(
    domain: ThrowableDomain,
    targetType: VirtualType,
    hierarchy: ThrowableHierarchy,
): ThrowableDomainPartition {
    val context = ResolverContext(hierarchy, ResolverMetricsCounter(), domain.exactRuntimeFacts)
    val partition = partition(domain, targetType, context, validateHandlerType = false)
    check(!partition.malformedMetadata) { "A cast domain partition cannot reject handler metadata" }
    return ThrowableDomainPartition(partition.caught, partition.residual)
}

/** Resolves one subject without frame access, allocation, host import, or route reordering. */
internal fun resolveHandlers(
    throwAddress: Int,
    subject: ThrownSubject,
    methodView: MethodView,
    hierarchy: ThrowableHierarchy,
): HandlerResolution {
    val metrics = ResolverMetricsCounter()
    metrics.observeRoots(
        subject.domain.unresolvedRequiredRoots.size,
        subject.domain.excludedRoots.size,
    )
    val context = ResolverContext(hierarchy, metrics, subject.domain.exactRuntimeFacts)
    val table = when (val lookup = methodView.handlerTableAt(throwAddress)) {
        HandlerTableLookup.None -> return HandlerResolution.Routed(
            emptyList(),
            UnhandledThrow(subject),
            metrics.snapshot(),
        )
        HandlerTableLookup.MalformedMetadata -> return HandlerResolution.MalformedMetadata(metrics.snapshot())
        is HandlerTableLookup.Found -> lookup.table
    }

    val routes = ArrayList<HandlerRoute>(table.clauses.size)
    var residual: ThrowableDomain? = subject.domain
    for (clause in table.clauses) {
        val remaining = residual ?: break
        metrics.clausesVisited++
        when (clause) {
            is HandlerClause.CatchAll -> {
                routes += HandlerRoute(clause.handlerAddress, subject.refinedTo(remaining, hierarchy))
                metrics.routes++
                residual = null
            }
            is HandlerClause.Typed -> {
                val partition = partition(remaining, clause.exceptionType, context, validateHandlerType = true)
                if (partition.malformedMetadata) {
                    return HandlerResolution.MalformedMetadata(metrics.snapshot())
                }
                partition.caught?.let {
                    routes += HandlerRoute(clause.handlerAddress, subject.refinedTo(it, hierarchy))
                    metrics.routes++
                }
                residual = partition.residual
            }
        }
    }
    return HandlerResolution.Routed(
        routes,
        residual?.let { UnhandledThrow(subject.refinedTo(it, hierarchy)) },
        metrics.snapshot(),
    )
}

private class ResolverContext(
    private val hierarchy: ThrowableHierarchy,
    val metrics: ResolverMetricsCounter,
    exactRuntimeFacts: ThrowableTypeFacts?,
) {
    private data class RelationKey(val candidate: VirtualType, val ancestor: VirtualType)

    private val facts = HashMap<VirtualType, ThrowableTypeFacts>()
    private val relations = HashMap<RelationKey, TypeRelation>()

    init {
        exactRuntimeFacts?.let { facts[it.type] = it }
    }

    fun facts(type: VirtualType): ThrowableTypeFacts = facts.getOrPut(type) {
        metrics.factBuilds++
        hierarchy.facts(type).also {
            if (!it.descriptorPresent) metrics.missingDescriptors++
        }
    }

    fun relation(candidate: VirtualType, ancestor: VirtualType): TypeRelation {
        metrics.relationRequests++
        val key = RelationKey(candidate, ancestor)
        relations[key]?.let { return it }
        metrics.relationCacheMisses++
        val answer = relationFromFacts(candidate, ancestor, facts(candidate), facts(ancestor))
        relations[key] = answer
        return answer
    }
}

private fun partition(
    domain: ThrowableDomain,
    catchType: VirtualType,
    context: ResolverContext,
    validateHandlerType: Boolean,
): DomainPartition {
    if (catchType == THROWABLE_TYPE) return DomainPartition(domain, null)

    val catchFacts = context.facts(catchType)
    if (validateHandlerType && (
        catchFacts.kind == ThrowableTypeKind.INTERFACE ||
        catchFacts.throwableRelation == TypeRelation.NO
    )) {
        return DomainPartition(null, null, malformedMetadata = true)
    }

    if (catchType in domain.excludedRoots) return DomainPartition(null, domain)
    if (
        catchFacts.knownClassAncestors.any(domain.excludedRoots::contains) ||
        catchFacts.knownInterfaceAncestors.any(domain.excludedRoots::contains)
    ) {
        return DomainPartition(null, domain)
    }
    if (catchType in domain.unresolvedRequiredRoots) return DomainPartition(domain, null)
    if (domain.unresolvedRequiredRoots.any { context.relation(it, catchType) == TypeRelation.YES }) {
        return DomainPartition(domain, null)
    }

    domain.exactRuntimeType?.let { exactType ->
        if (catchFacts.throwableRelation == TypeRelation.UNKNOWN) {
            return DomainPartition(
                domain.requireUnresolvedRoot(catchType, context.metrics),
                domain.exclude(catchType, context.metrics),
            )
        }
        return when (context.relation(exactType, catchType)) {
            TypeRelation.YES -> DomainPartition(domain, null)
            TypeRelation.NO -> DomainPartition(null, domain)
            TypeRelation.UNKNOWN -> DomainPartition(
                domain.requireUnresolvedRoot(catchType, context.metrics),
                domain.exclude(catchType, context.metrics),
            )
        }
    }

    val upperWithinCatch = context.relation(domain.trustedUpperBound, catchType)
    if (upperWithinCatch == TypeRelation.YES) {
        return DomainPartition(domain, null)
    }

    val catchWithinUpper = context.relation(catchType, domain.trustedUpperBound)
    if (catchWithinUpper == TypeRelation.YES) {
        return DomainPartition(
            domain.narrowUpperBound(catchType, context.metrics),
            domain.exclude(catchType, context.metrics),
        )
    }

    val upperFacts = context.facts(domain.trustedUpperBound)
    if (
        upperWithinCatch == TypeRelation.NO &&
        catchWithinUpper == TypeRelation.NO &&
        upperFacts.kind == ThrowableTypeKind.CLASS &&
        catchFacts.kind == ThrowableTypeKind.CLASS
    ) {
        return DomainPartition(null, domain)
    }

    return DomainPartition(
        domain.requireUnresolvedRoot(catchType, context.metrics),
        domain.exclude(catchType, context.metrics),
    )
}
