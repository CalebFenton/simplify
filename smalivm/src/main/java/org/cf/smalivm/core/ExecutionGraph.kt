package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

/**
 * An immutable, single-revision execution result for one method (spec §5.4): the nodes every explored path
 * produced, piled by address. Every read is pure, so an observer cannot perturb what it measures.
 *
 * The value queries live on [ExploredPaths]: [explored] answers them over whatever this run walked, and
 * [trusted] hands out the same queries as a [TrustedGraphView] only when the run is `canTrustConsensus`. This
 * class keeps the reads that describe the run itself, for a debugger or a test: the raw [nodePile], [wasReached],
 * liveness and [completion]. Which nodes each query reads, and why, is stated in
 * `docs/architecture/graph-optimizer-contract.md`.
 */
class ExecutionGraph internal constructor(
    val methodView: MethodView,
    /** Union over every activation of [methodView], including recursive ones. */
    private val addressToNodes: Map<Int, List<ExecutionNode>>,
    /** The run's OWN activation only. Feeds [returningNodes] and therefore the whole terminating family. */
    private val addressToNodesInThisRunsActivation: Map<Int, List<ExecutionNode>>,
    outcomes: Iterable<Outcome>,
    private val completion: CompletionStatus,
    private val executedNodes: Set<ExecutionNode>,
    /** The class hierarchy every consensus join consults, so `ref ⊔ ref` answers the nearest common superclass. */
    internal val superclasses: Superclasses,
) {

    val programRevision: ProgramRevision = methodView.programRevision
    val methodRevision: ApplicationMethodRevision? = methodView.methodRevision

    private val storedOutcomes: PersistentList<Outcome> = outcomes.toPersistentList()

    /**
     * Structurally isolated path-outcome list. Duplicate outcomes are retained. Use [completion] to determine
     * whether other feasible paths were omitted; a partial graph still reports ended paths despite aborts on
     * other paths. This list contract does not imply deep immutability of [Outcome] payloads.
     */
    val outcomes: List<Outcome> get() = storedOutcomes

    /** The value queries over the paths this run walked, with no completion gate. */
    val explored: ExploredPaths = ExploredPaths(this)

    private val trustedView: TrustedGraphView by lazy { TrustedGraphView(this) }

    // --- structure ---

    /** Every code address in the method, ascending; a fresh array on every call. */
    fun addresses(): IntArray = methodView.addresses

    /**
     * True when a node at [address] ran its op. Ungated, for coverage on a truncated run: a `false` proves
     * nothing until the run completes, and never for a payload. [TrustedGraphView.isDead] is the dead-code proof.
     */
    fun wasReached(address: Int): Boolean = nodePile(address).any { it in executedNodes }

    /** True when the scheduler ever dequeued a node at [address], whether or not its op ran. */
    fun wasAttempted(address: Int): Boolean = nodePile(address).isNotEmpty()

    /**
     * The op decoded at [address]. Recorded on every attempted node, including one a bound stopped before it ran,
     * so this answers where [wasAttempted] does; it throws where it does not.
     */
    fun getOp(address: Int): Op =
        requireNotNull(opOrNull(address)) { "Address $address was never attempted; no op recorded" }

    /**
     * Every node the scheduler dequeued at [address], oldest first, across every activation of [methodView]. It
     * includes a node a bound stopped before its op ran, whose frame is not this address's post-op state; the
     * value queries skip such nodes.
     */
    fun nodePile(address: Int): List<ExecutionNode> = addressToNodes[address].orEmpty()

    /** [nodePile] restricted to nodes whose op ran: the only nodes whose frame is this address's post-op state. */
    internal fun executedPile(address: Int): List<ExecutionNode> = nodePile(address).filter { it in executedNodes }

    /**
     * The nodes that returned normally in this run's own activation, in address order. Only the entry
     * activation's returns are what the run returned; a recursive activation's return is not.
     */
    internal fun returningNodes(): List<ExecutionNode> = methodView.instructionAddresses
        .flatMap { addressToNodesInThisRunsActivation[it].orEmpty() }
        .filter { it.outcome is Outcome.Returned }

    // --- liveness ---

    fun registersAssigned(address: Int): Set<Int> =
        opOrNull(address)?.registersAssigned?.toSortedSet() ?: emptySet()

    /** Registers the op at [address] reads; a wide source is named by its low half only. */
    fun registersRead(address: Int): Set<Int> =
        opOrNull(address)?.registersRead?.toSortedSet() ?: emptySet()

    fun wasRegisterAssigned(address: Int, register: Int): Boolean = register in registersAssigned(address)

    fun wasRegisterRead(address: Int, register: Int): Boolean = register in registersRead(address)

    /** Every register some op in the method reads or assigns, or that holds a parameter, both halves of wide ones. */
    private val usedRegisters: Set<Int> by lazy {
        val used = mutableSetOf<Int>()
        for (a in methodView.instructionAddresses) {
            opOrNull(a)?.let { used.addAll(it.registersRead); used.addAll(it.registersAssigned) }
        }
        methodView.parameters.forEach { used.add(it.register); if (it.type.isWide) used.add(it.register + 1) }
        used
    }

    /**
     * Registers free for scratch use anywhere in the method: those no op touches and no parameter holds. Both
     * halves of a wide value count as used, because [Op.destinationRegisters] names both halves of a wide
     * result. [address] is unused: this is whole-method, coarser than per-point liveness, and never fails on an
     * unreached address.
     */
    fun availableRegisters(address: Int): IntArray =
        (0 until methodView.registerCount).filterNot { it in usedRegisters }.toIntArray()

    // --- completion and the trust gate ---

    /** Whether exploration completed, aborted, or has not finished yet. */
    fun completion(): CompletionStatus = completion

    /**
     * The value queries as a [TrustedGraphView], or null when [completion] is not `canTrustConsensus`: a run that
     * stopped at a bound, hit an unimplemented opcode or a host failure, or has not finished yet. [completion]
     * says why.
     */
    fun trusted(): TrustedGraphView? = if (completion.canTrustConsensus) trustedView else null

    override fun toString(): String =
        "ExecutionGraph(${methodView.signature}, " +
            "reached=${methodView.instructionAddresses.filter { wasReached(it) }}, completion=$completion)"

    private fun opOrNull(address: Int): Op? = nodePile(address).firstOrNull()?.op
}
