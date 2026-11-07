package org.cf.smalivm.core

/** Deterministic structural counters for one method edit transaction. */
internal data class MethodEditMetrics(
    val baselineInstructionVisits: Int,
    val editCommandsIndexed: Int,
    val methodBodyDraftsBuilt: Int,
    val draftInstructionVisits: Int,
    val methodBodiesAssembled: Int,
    val layoutRounds: Int,
    val branchWidenings: Int,
    val paddingChanges: Int,
    val layoutEdgeVisits: Int,
    val payloadElementsCopied: Int,
    val handlerClausesBuilt: Int,
    val referenceRequests: Int,
    val distinctStagedDexReferences: Int,
    val methodDexReferenceClosuresReplaced: Int,
    val jumboCommitMethodBodiesAssembled: Int,
    val collateralMethodRevisionsCreated: Int,
    val methodViewsBuilt: Int,
    val encodedBodyRecordsCreated: Int,
    val programSnapshotsPublished: Int,
    val openMethodEditTransactions: Int,
    val unrelatedMethodBodiesVisited: Int,
    val scratchReservationRequests: Int,
    val scratchWordsReserved: Int,
    val scratchHiddenWords: Int,
    val scratchSyntheticInstructions: Int,
)

internal class MethodEditMetricsCounter {
    var baselineInstructionVisits = 0
    var editCommandsIndexed = 0
    var methodBodyDraftsBuilt = 0
    var draftInstructionVisits = 0
    var methodBodiesAssembled = 0
    var layoutRounds = 0
    var branchWidenings = 0
    var paddingChanges = 0
    var layoutEdgeVisits = 0
    var payloadElementsCopied = 0
    var handlerClausesBuilt = 0
    var referenceRequests = 0
    var distinctStagedDexReferences = 0
    var methodDexReferenceClosuresReplaced = 0
    var jumboCommitMethodBodiesAssembled = 0
    var collateralMethodRevisionsCreated = 0
    var methodViewsBuilt = 0
    var encodedBodyRecordsCreated = 0
    var programSnapshotsPublished = 0
    var unrelatedMethodBodiesVisited = 0
    var scratchReservationRequests = 0
    var scratchWordsReserved = 0
    var scratchHiddenWords = 0
    var scratchSyntheticInstructions = 0

    fun snapshot(openTransactions: Int): MethodEditMetrics = MethodEditMetrics(
        baselineInstructionVisits,
        editCommandsIndexed,
        methodBodyDraftsBuilt,
        draftInstructionVisits,
        methodBodiesAssembled,
        layoutRounds,
        branchWidenings,
        paddingChanges,
        layoutEdgeVisits,
        payloadElementsCopied,
        handlerClausesBuilt,
        referenceRequests,
        distinctStagedDexReferences,
        methodDexReferenceClosuresReplaced,
        jumboCommitMethodBodiesAssembled,
        collateralMethodRevisionsCreated,
        methodViewsBuilt,
        encodedBodyRecordsCreated,
        programSnapshotsPublished,
        openTransactions,
        unrelatedMethodBodiesVisited,
        scratchReservationRequests,
        scratchWordsReserved,
        scratchHiddenWords,
        scratchSyntheticInstructions,
    )
}
