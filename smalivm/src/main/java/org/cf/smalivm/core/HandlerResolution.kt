package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet

/** One typed catch clause. */
public data class CatchClause(val exceptionType: VirtualType, val handlerAddress: Int)

/** One public try range. The range is start-inclusive and end-exclusive. */
public data class TryBlock(
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

    internal fun covers(address: Int): Boolean = address in startAddress until endAddress
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

/**
 * Throwable routing facts. Total over classes the program lacks: where [TypeHierarchy] throws [ClassLoadFailure],
 * this answers [TypeRelation.UNKNOWN]. Any other failure propagates.
 */
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

internal sealed interface HandlerResolution {
    data class Routed(
        val routes: List<HandlerRoute>,
        val unhandled: UnhandledThrow?,
    ) : HandlerResolution

    data object MalformedMetadata : HandlerResolution

    /** The class model failed while answering. The caller ends only the throwing path. */
    data class HostFailure(val failure: Throwable) : HandlerResolution
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
    val context = ResolverContext(hierarchy, domain.exactRuntimeFacts)
    val partition = partition(domain, targetType, context, validateHandlerType = false)
    check(!partition.malformedMetadata) { "A cast domain partition cannot reject handler metadata" }
    return ThrowableDomainPartition(partition.caught, partition.residual)
}

/**
 * Resolves one subject without frame access, allocation, host import, or route reordering. Every throwable the
 * class model raises becomes [HandlerResolution.HostFailure], so local and caller lookup contain it alike.
 */
internal fun resolveHandlers(
    throwAddress: Int,
    subject: ThrownSubject,
    methodView: MethodView,
    hierarchy: ThrowableHierarchy,
): HandlerResolution = try {
    route(throwAddress, subject, methodView, hierarchy)
} catch (failure: Throwable) {
    HandlerResolution.HostFailure(failure)
}

private fun route(
    throwAddress: Int,
    subject: ThrownSubject,
    methodView: MethodView,
    hierarchy: ThrowableHierarchy,
): HandlerResolution {
    val context = ResolverContext(hierarchy, subject.domain.exactRuntimeFacts)
    val table = when (val lookup = methodView.handlerTableAt(throwAddress)) {
        HandlerTableLookup.None -> return HandlerResolution.Routed(emptyList(), UnhandledThrow(subject))
        HandlerTableLookup.MalformedMetadata -> return HandlerResolution.MalformedMetadata
        is HandlerTableLookup.Found -> lookup.table
    }

    val routes = ArrayList<HandlerRoute>(table.clauses.size)
    var residual: ThrowableDomain? = subject.domain
    for (clause in table.clauses) {
        val remaining = residual ?: break
        when (clause) {
            is HandlerClause.CatchAll -> {
                routes += HandlerRoute(clause.handlerAddress, subject.refinedTo(remaining, hierarchy))
                residual = null
            }
            is HandlerClause.Typed -> {
                val partition = partition(remaining, clause.exceptionType, context, validateHandlerType = true)
                if (partition.malformedMetadata) return HandlerResolution.MalformedMetadata
                partition.caught?.let {
                    routes += HandlerRoute(clause.handlerAddress, subject.refinedTo(it, hierarchy))
                }
                residual = partition.residual
            }
        }
    }
    return HandlerResolution.Routed(routes, residual?.let { UnhandledThrow(subject.refinedTo(it, hierarchy)) })
}

private class ResolverContext(
    private val hierarchy: ThrowableHierarchy,
    exactRuntimeFacts: ThrowableTypeFacts?,
) {
    private data class RelationKey(val candidate: VirtualType, val ancestor: VirtualType)

    private val facts = HashMap<VirtualType, ThrowableTypeFacts>()
    private val relations = HashMap<RelationKey, TypeRelation>()

    init {
        exactRuntimeFacts?.let { facts[it.type] = it }
    }

    fun facts(type: VirtualType): ThrowableTypeFacts = facts.getOrPut(type) { hierarchy.facts(type) }

    fun relation(candidate: VirtualType, ancestor: VirtualType): TypeRelation =
        relations.getOrPut(RelationKey(candidate, ancestor)) {
            relationFromFacts(candidate, ancestor, facts(candidate), facts(ancestor))
        }
}

private fun partition(
    domain: ThrowableDomain,
    catchType: VirtualType,
    context: ResolverContext,
    validateHandlerType: Boolean,
): DomainPartition {
    if (catchType == GuestThrowables.THROWABLE) return DomainPartition(domain, null)

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
                domain.requireUnresolvedRoot(catchType),
                domain.exclude(catchType),
            )
        }
        return when (context.relation(exactType, catchType)) {
            TypeRelation.YES -> DomainPartition(domain, null)
            TypeRelation.NO -> DomainPartition(null, domain)
            TypeRelation.UNKNOWN -> DomainPartition(
                domain.requireUnresolvedRoot(catchType),
                domain.exclude(catchType),
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
            domain.narrowUpperBound(catchType),
            domain.exclude(catchType),
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
        domain.requireUnresolvedRoot(catchType),
        domain.exclude(catchType),
    )
}
