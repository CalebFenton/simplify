package org.cf.smalivm.core

/** Deterministic structural metrics for persistent-state copy tests. Not part of the state API. */
internal data class StateCopyCounts(
    val frameCopies: Int,
    val heapStoreCopies: Int,
    val classStoreCopies: Int,
    val registerElementsCopied: Int,
    val heapObjectsCopied: Int,
    val classElementsCopied: Int,
    val stateEntriesVisitedDuringCopy: Int,
    val hostGraphClones: Int,
    val hostObjectsCloned: Int,
    val opArmCopies: Int,
    val opArmStateEntriesVisited: Int,
    val throwableAllocations: Int,
    val arrayAllocationElementsInitialized: Int,
)

/** Logical root and object versions created by guest writes. */
internal data class StateVersionCounts(
    val registerRoots: Int,
    val assignedRoots: Int,
    val readRoots: Int,
    val heapRoots: Int,
    val heapObjects: Int,
    val arrayElements: Int,
    val objectFields: Int,
    val addressVisitRoots: Int,
    val classStateRoots: Int,
    val staticRoots: Int,
    val hostGraphRoots: Int,
)

/** Opaque root tokens let tests verify sharing without leaking persistent collection types. */
internal data class StateSharingSnapshot(
    val registerRoot: Any,
    val assignedRoot: Any,
    val readRoot: Any,
    val addressVisitRoot: Any,
    val heapRoot: Any,
    val classStateRoot: Any,
    val staticRoot: Any,
    val hostGraphRoot: Any,
    val copies: StateCopyCounts,
    val versions: StateVersionCounts,
)

/** Identity tokens for one immutable heap-object version and its nested persistent root. */
internal data class HeapObjectSharing(
    val objectToken: Any,
    val contentsRoot: Any?,
)

internal data class SelectedCompactionMetrics(
    val guestRefsVisited: Int,
    val graphBackedRefsVisited: Int,
    val hostNodesVisited: Int,
)

internal data class MirrorIndexMetrics(
    val mirroredEntriesVisited: Int,
    val dirtyEntriesVisited: Int,
)

/** Current host-graph size and deterministic root-version count. */
internal data class HostGraphMetrics(
    val nodes: Int,
    val rootVersions: Int,
)

/** Deterministic Task 45 counters. No elapsed-time threshold is involved. */
internal data class OpExecutionMetrics(
    val armCopies: Int,
    val persistentStateEntriesVisited: Int,
    val reconciliationFootprintsResolved: Int,
    val preArmThrowableAllocations: Int,
    val committedThrowableAllocations: Int,
)

/** Deterministic Task 47 completion-retention and exact-partition counters. */
internal data class CallPropagationMetrics(
    val openPaths: Int,
    val retainedFrames: Int,
    val retainedCollectionCapacity: Int,
    val pendingRoutingCommands: Int,
    val routingWorkChecks: Int,
    val routingCommands: Int,
    val schedulerWork: Int,
    val partitionMerges: Int,
    val sourcePartitionMerges: Int,
    val sourceThrowPartitions: Int,
    val sourceThrowExits: Int,
    val handlerResolutions: Int,
    val handlerClausesVisited: Int,
    val handlerRelationRequests: Int,
    val handlerRoutes: Int,
    val sourceDomainHashReads: Int,
    val sourceDomainHashEntryVisits: Int,
)

internal class ThrowableDomainHashMetricsCounter {
    var hashReads: Int = 0
    var entryVisits: Int = 0
}

internal class RegisterStateMetrics {
    var frameCopies: Int = 0
    var registerElementsCopied: Int = 0
    var registerRoots: Int = 0
    var assignedRoots: Int = 0
    var readRoots: Int = 0
    var addressVisitRoots: Int = 0
    var copyEntriesVisited: Int = 0
    var opArmCopies: Int = 0
    var opArmStateEntriesVisited: Int = 0
}

internal class HeapStateMetrics {
    var storeCopies: Int = 0
    var objectsCopied: Int = 0
    var heapRoots: Int = 0
    var heapObjects: Int = 0
    var arrayElements: Int = 0
    var objectFields: Int = 0
    var hostGraphClones: Int = 0
    var hostObjectsCloned: Int = 0
    var hostGraphRoots: Int = 0
    var selectedGuestRefsVisited: Int = 0
    var selectedGraphBackedRefsVisited: Int = 0
    var selectedHostNodesVisited: Int = 0
    var mirroredArrayIndexEntriesVisited: Int = 0
    var dirtyMirrorIndexEntriesVisited: Int = 0
    var throwableAllocations: Int = 0
    var arrayAllocationElementsInitialized: Int = 0
}

internal class ClassStateMetrics {
    var storeCopies: Int = 0
    var elementsCopied: Int = 0
    var classStateRoots: Int = 0
    var staticRoots: Int = 0
}
