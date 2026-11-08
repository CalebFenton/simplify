package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

/**
 * An **immutable, single-revision** execution result (spec §5.4) and the consumer's query surface
 * (spec §9). Splitting this from the static [MethodView] and from the (Plan 13) editor is what stops
 * a graph from ever describing two revisions of a method at once.
 *
 * **It describes one METHOD, and the scope is deliberately ASYMMETRIC between two families of query. Read
 * this before adding one.** A callee's nodes are never here -- they belong to the callee's method, reachable
 * through the call tree ([ExecutionNode.activation] / [Activation.caller]) -- but a **recursive activation of
 * this same method** is a different question, and the two families answer it differently:
 *
 *  - **Reachability, liveness, structure and effects UNION every activation of [methodView]**
 *    ([nodePile] and everything derived from it: [wasReached], [wasAttempted], [getOp], [children],
 *    [parentAddresses], [sideEffectLevel], and per-address register consensus). These are existential or
 *    err-upward, so more is sound (spec §2.2, "explore more, never fewer"). Scoping them to the entry
 *    activation was a **live defect**: a recursive activation that branched where the entry activation did
 *    not left `wasReached` answering `false`, on a `Complete` run, for an address that had executed -- and
 *    [AnalysisContracts]'s `wasReached` contract states in as many words that a `false` on a trustworthy run
 *    is a dead-code PROOF, with exactly one enumerated exception (payload addresses). A dead-code pass doing
 *    exactly what that contract prescribes would delete the code that produced the run's own answer.
 *  - **The TERMINATING family stays scoped to the run's OWN activation**
 *    ([terminatingConsensusOverExploredPaths], [terminatingRegisterOccupancyOverExploredPaths],
 *    [didAnyPathReturnOverExploredPaths], [terminatingHeapObjectOverExploredPaths], all via
 *    [returningNodes]). "What this method returns *on this run*" is a property of the entry activation alone;
 *    unioning it lets a recursive activation's own return vote, which is `CARRY-FORWARD.md` Plan 9 item 2's
 *    invented agreement wearing recursion as a disguise instead of a callee.
 *
 * **Why the per-address consensus goes with the first group and not the second**, since that is the pairing
 * most likely to be reversed by a later edit: a fold at address *a* is a REWRITE, and a rewrite must be valid
 * on every activation that executes *a*. Entry-only scoping there would authorize a constant that is wrong in
 * the recursive activation -- which is a wrong-answer defect, while the union's only cost is that the join
 * moves up the lattice. Pinned by `core/InvokerTest`'s
 * `aRecursiveActivationsOwnValuesJoinIntoPerAddressConsensusButNotIntoTheRunsReturnValue`, whose two
 * assertions fail under the *opposite* mutations.
 *
 * For a method that never recurses the two maps are identical, so none of this costs the non-recursive case
 * anything.
 *
 * A callee's own side-effect declaration is invisible here whichever family asks, so [ExecutionSession]
 * raises it onto the caller's invoke node instead -- see [Activation.declaredEffect] and
 * [Activation.raiseCallSiteEffect].
 *
 * Every query here is pure: it reads recorded nodes and never mutates anything, so an observer --
 * a debugger, a strategy, a test -- cannot perturb what it measures.
 *
 * **The trust split (spec §9).** The queries a consumer would ACT on live on [TrustedGraphView],
 * reached through [trusted], which is null unless the run completed -- and a mid-run snapshot
 * ([ExecutionSession.graph]) has not, so it gets no trusted view either. What remains here under
 * `...OverExploredPaths` names is the same computation with no gate -- the truncated or not-yet-
 * finished picture, which a debugger and Plan 11's differential legitimately want. See
 * [TrustedGraphView] for why the split is a type and not a comment.
 *
 * **The piling decision (spec §6.4).** [ExecutionSession.step] dequeues a node before it knows
 * whether a bound (address-visit, step or timeout limit) will stop it running that node's op.
 * Piling only nodes that got to run -- the behavior this plan replaces -- left a limit-aborted node
 * belonging to NO pile at all: it existed only as some sibling's unqueried [ExecutionNode.children]
 * entry, so [nodePile], [wasReached] and [terminatingConsensusOverExploredPaths] were all blind to it even though
 * [outcomes] recorded its abort. [ExecutionSession] now piles every node it ever dequeues --
 * including one a limit's drain abandons still sitting in the frontier -- unconditionally, so
 * [nodePile] can show it and a consumer can attribute the abort to the address it happened at.
 * [executedNodes] is the companion fact that keeps this safe: only a node whose op actually ran has
 * a genuine *post-op* [Frame] (spec §9's load-bearing state contract) -- a limit-aborted node's
 * frame is still its parent's copy, frozen before this address's own op ever touched it. Every
 * query that would read or count a node's FRAME VALUE as "this address's value" ([wasReached],
 * [consensusOverExploredPaths], [registerValuesOverExploredPaths]) therefore filters to
 * [executedNodes] first; [nodePile] deliberately
 * does not, so a consumer inspecting the raw pile (a debugger, Plan 11's differential) can still see
 * and attribute the abort. [terminatingConsensusOverExploredPaths] needs no separate filter: only an executed node
 * can ever carry [Outcome.Returned]. Liveness ([getOp], [registersRead], [registersAssigned]) does
 * NOT filter through [executedNodes], and that is deliberate, not an oversight: [OpBuilder] decodes
 * an [Op] from `(address, view)` alone and never touches a [Frame], so the op recorded on a
 * limit-aborted node is exactly as correct as one from an executed node -- filtering there would
 * only shrink totality for no soundness benefit.
 */
class ExecutionGraph internal constructor(
    val methodView: MethodView,
    /** Union over EVERY activation of [methodView], including recursive ones -- see the class KDoc. */
    private val addressToNodes: Map<Int, List<ExecutionNode>>,
    /**
     * The run's OWN activation only. Feeds [returningNodes] and therefore the whole terminating family, and
     * nothing else. Defaulted to [addressToNodes] so a hand-built test graph (there are several) states one
     * map when it has no recursion to distinguish -- which is also the honest default, since for a
     * non-recursive run the two maps are equal by construction.
     */
    private val addressToNodesInThisRunsActivation: Map<Int, List<ExecutionNode>> = addressToNodes,
    outcomes: Iterable<Outcome>,
    private val completion: CompletionStatus,
    private val executedNodes: Set<ExecutionNode>,
    /**
     * The class hierarchy every consensus query's join consults (Plan 8 Task 7). Held rather than
     * defaulted so `ref ⊔ ref` answers the nearest common superclass instead of always `Ljava/lang/Object;`
     * -- see [joinTypes], whose KDoc also carries why a failed walk degrades here instead of propagating.
     * [ExecutionSession] passes the `ClassIndex` it already holds; it is the only production constructor
     * call site.
     */
    private val superclasses: Superclasses,
) : ExecutionGraphView {

    val programRevision: ProgramRevision = methodView.programRevision
    val methodRevision: ApplicationMethodRevision? = methodView.methodRevision

    private val storedOutcomes: PersistentList<Outcome> = outcomes.toPersistentList()

    /**
     * Structurally isolated path-outcome list. Duplicate outcomes are retained. Use [completion] to determine
     * whether other feasible paths were omitted; a partial graph still reports ended paths despite aborts on
     * other paths. This list contract does not imply deep immutability of [Outcome] payloads.
     */
    val outcomes: List<Outcome> get() = storedOutcomes

    // --- structure ---

    // MethodView returns a defensive code-address array. Graph snapshots therefore cannot share a
    // caller-mutable address array with the view or with another graph.
    override fun addresses(): IntArray = methodView.addresses

    /**
     * True iff a node at [address] actually ran its op. Deliberately stricter than "[nodePile] is
     * non-empty": the piling decision above means a pile can hold a node the scheduler abandoned
     * before running it, and counting that as "reached" would let a consumer trust a stale, pre-op
     * [Frame] as though it were this address's produced value.
     *
     * **A `false` proves dead code only on a run whose [completion] is `canTrustConsensus`, AND only for
     * an address that is not a payload** -- see [ExecutionGraphView.wasReached] for the full caveat and
     * [isPayload] for the second half of it. Ungated deliberately (a debugger's coverage view needs it on
     * a truncated run too), but it is the one query a dead-code strategy reaches for before it ever gets
     * near the gated ones, so the caveat is stated on both the interface and here.
     */
    override fun wasReached(address: Int): Boolean = nodePile(address).any { it in executedNodes }

    /**
     * Delegated to [methodView], which reads the one definition of "which opcodes are payloads"
     * ([OpcodeGroups.PAYLOAD]). Deliberately a delegation and not a second derivation: two answers
     * to this question is how the differential's projection and the consumer's query would drift apart.
     * See [ExecutionGraphView.isPayload] for why the query is on the graph at all when the data is
     * static.
     */
    override fun isPayload(address: Int): Boolean = methodView.isPayload(address)

    override fun tryBlocks(): List<TryBlock> = methodView.tryBlocks

    /**
     * The op decoded at [address]. Sourced from any piled node there, executed or not: [OpBuilder]
     * decodes from `(address, method)` alone and never touches a [Frame], so even a node the
     * scheduler abandoned before running still carries the correct, real op for that address.
     * Throws only when [address] was never even attempted -- check [wasAttempted] first. Use
     * [wasReached] to ask the stricter, different question "did an op actually run there", and
     * [MethodView.opcodeAt] for a purely static answer needing no execution at all.
     *
     * Property, not contract: this returns the exact [Op] instance recorded on the node rather than
     * re-decoding, so `getOp(a) === getOp(a)` happens to hold today. Nothing here promises that --
     * an op cache, or a switch to re-decoding on every read, could silently change it.
     */
    override fun getOp(address: Int): Op =
        requireNotNull(opOrNull(address)) { "Address $address was never attempted; no op recorded" }

    /**
     * Every node the scheduler ever dequeued at [address], across all explored paths, oldest
     * first -- including one a limit stopped before its op ran (the piling decision above).
     * Deliberately UNFILTERED: this is the raw material [consensusOverExploredPaths] and [wasReached] filter down to
     * genuinely executed nodes themselves, so a consumer inspecting a pile directly can still see
     * and attribute a limit abort to the address it happened at, which [outcomes] alone cannot do.
     *
     * **Scoped to activations of [methodView], and that is what makes a bare-`Int` key safe here**
     * (`CARRY-FORWARD.md` Plan 9 item 2, discharged in Plan 9 Task 14). [ExecutionSession] pools nodes on the
     * [Activation] they belong to, so a CALLEE's address 0 is never in this map -- it used to be a
     * session-global map, and a callee's own `Outcome.Returned` node piled at a colliding address then voted
     * in [terminatingConsensusOverExploredPaths]. Callee nodes are reached through the call tree
     * ([ExecutionNode.activation] / [Activation.caller]) instead.
     *
     * **RECURSIVE activations of this same method ARE unioned in here**, deliberately, and the terminating
     * family deliberately does not follow -- see the class KDoc for the full argument. Nothing derived from
     * this pile is a claim about what the run RETURNED; those all go through [returningNodes].
     */
    override fun nodePile(address: Int): List<ExecutionNode> = addressToNodes[address].orEmpty()

    override fun parentAddresses(address: Int): IntArray =
        nodePile(address).mapNotNull { it.parent?.address }.distinct().sorted().toIntArray()

    override fun children(address: Int): IntArray =
        nodePile(address).flatMap { node -> node.children.map { it.address } }.distinct().sorted().toIntArray()

    // --- consensus (ungated; see the trust gate below) ---

    override fun consensusOverExploredPaths(address: Int, register: Int): Value =
        consensusOverExploredPaths(intArrayOf(address), register)

    override fun consensusOverExploredPaths(addresses: IntArray, register: Int): Value {
        val nodes = addresses.flatMap { executedPile(it) }
        if (nodes.isEmpty()) return Value.Top
        return joinAll(nodes.map { valueIn(it, register) }, superclasses) ?: Value.Top
    }

    override fun reflectedMethodOverExploredPaths(address: Int, register: Int): ReflectedMethodMetadata? =
        unanimousValue(1, { address }) { reflectedMethodIn(it, register) }

    override fun reflectedMethodOverExploredPaths(
        addresses: IntArray,
        register: Int,
    ): ReflectedMethodMetadata? =
        unanimousValue(addresses.size, { addresses[it] }) { reflectedMethodIn(it, register) }

    private fun reflectedMethodIn(node: ExecutionNode, register: Int): ReflectedMethodMetadata? {
        val ref = (valueIn(node, register) as? Value.Known)?.raw as? HeapRef ?: return null
        return (node.frame.heapObject(ref) as? HeapObject.HostObject)?.reflectedMethodMetadata
    }

    override fun reflectedFieldOverExploredPaths(address: Int, register: Int): ReflectedFieldMetadata? =
        unanimousValue(1, { address }) { reflectedFieldIn(it, register) }?.toReflectedFieldMetadata()

    override fun reflectedFieldOverExploredPaths(
        addresses: IntArray,
        register: Int,
    ): ReflectedFieldMetadata? =
        unanimousValue(addresses.size, { addresses[it] }) { reflectedFieldIn(it, register) }?.toReflectedFieldMetadata()

    private fun reflectedFieldIn(node: ExecutionNode, register: Int): TrackedFieldValue? {
        val ref = (valueIn(node, register) as? Value.Known)?.raw as? HeapRef ?: return null
        return node.frame.trackedFieldAt(ref)?.metadata
    }

    private fun TrackedFieldValue.toReflectedFieldMetadata() = ReflectedFieldMetadata(
        ImmutableFieldReference(declaringType.descriptor, name, fieldType.descriptor),
        modifiers,
    )

    private inline fun <T : Any> unanimousValue(
        addressCount: Int,
        addressAt: (Int) -> Int,
        valueAt: (ExecutionNode) -> T?,
    ): T? {
        var result: T? = null
        for (index in 0 until addressCount) {
            for (node in nodePile(addressAt(index))) {
                if (node !in executedNodes) continue
                val value = valueAt(node) ?: return null
                if (result != null && result != value) return null
                result = value
            }
        }
        return result
    }

    override fun arrayLengthOverExploredPaths(addresses: IntArray, register: Int): Int? {
        var length = -1
        for (address in addresses) {
            for (node in nodePile(address)) {
                if (node !in executedNodes) continue
                val ref = (valueIn(node, register) as? Value.Known)?.raw as? HeapRef ?: return null
                val array = node.frame.heapObject(ref) as? HeapObject.TrackedArray ?: return null
                if (length >= 0 && length != array.length) return null
                length = array.length
            }
        }
        return if (length >= 0) length else null
    }

    override fun registerValuesOverExploredPaths(address: Int, register: Int): Set<Value> =
        executedPile(address).map { valueIn(it, register) }.toSet()

    override fun knownStringOverExploredPaths(address: Int, register: Int): String? =
        unanimousValue(1, { address }) { node ->
            when (val raw = (valueIn(node, register) as? Value.Known)?.raw) {
                is String -> raw
                is HeapRef -> node.frame.hostObjectAt(raw) as? String
                else -> null
            }
        }

    /**
     * Consensus over **return** paths only (spec §6.5): a value every returning path agrees on.
     * Throwing and aborted paths are excluded so they can never contaminate it -- and because only
     * a node whose op actually ran can ever carry [Outcome.Returned], this filter alone already
     * keeps out every limit-aborted node from the piling decision above too. [Value.Top] when no
     * path returned. Iterates [MethodView.instructionAddresses] (already ascending) rather than the backing
     * map directly, so the join order -- and therefore this result -- never depends on map
     * iteration order.
     *
     * **The query the callee-collision hazard was really about**, so it is worth stating what protects it: a
     * callee's own [Outcome.Returned] node piled at an address this method also has would be counted here,
     * inventing agreement about a value this method never returned. It cannot be, because [nodePile] holds
     * one activation's nodes only -- see its KDoc. Pinned by
     * `InvokerTest.aCalleesReturningNodeDoesNotVoteInTheCallersReturnConsensus`, whose fixtures deliberately
     * give the caller and the callee an op at the same two addresses.
     */
    fun terminatingConsensusOverExploredPaths(register: Int): Value {
        val returned = returningNodes()
        if (returned.isEmpty()) return Value.Top
        return joinAll(returned.map { valueInEnding(it, register) }, superclasses) ?: Value.Top
    }

    /**
     * The nodes every `terminating...` query reconciles: those whose own outcome is [Outcome.Returned].
     *
     * Extracted rather than spelled four times, and the reason is not brevity: [RegisterOccupancy] exists to
     * explain a [Value.Top] that [terminatingConsensusOverExploredPaths] produced, so the two must be about
     * the *same* node set by construction. Two copies of this filter could drift and the pairing a consumer
     * relies on would quietly stop holding.
     *
     * Iterates [MethodView.instructionAddresses] (already ascending) rather than the backing map, so the order -- and
     * therefore any join folded over it -- never depends on map iteration order. Needs no
     * [executedNodes] filter: only a node whose op actually ran can carry [Outcome.Returned].
     *
     * **Reads [addressToNodesInThisRunsActivation], NOT [nodePile]**, and that one word is the whole
     * asymmetry the class KDoc describes. A recursive activation's returning node carries its own
     * [Outcome.Returned] (`Ending.ActivationOnly` -- the activation ended even though the run's path did
     * not), so taking it from the unioned pile would let a nested call's return value join into "what this
     * method returned" and answer `Unknown` where the truth is a foldable constant, or worse agree on a value
     * this run never returned. Same defect as `CARRY-FORWARD.md` Plan 9 item 2, with recursion in place of a
     * callee.
     */
    private fun returningNodes(): List<ExecutionNode> = methodView.instructionAddresses
        .flatMap { addressToNodesInThisRunsActivation[it].orEmpty() }
        .filter { it.outcome is Outcome.Returned }

    override fun registerOccupancyOverExploredPaths(address: Int, register: Int): RegisterOccupancy =
        occupancyOver(executedPile(address), register)

    override fun terminatingRegisterOccupancyOverExploredPaths(register: Int): RegisterOccupancy =
        occupancyOver(returningNodes(), register, useEndingFrame = true)

    override fun didAnyPathReturnOverExploredPaths(): Boolean = returningNodes().isNotEmpty()

    /**
     * The one implementation of [RegisterOccupancy], shared by both queries so "held" cannot come to mean
     * two things depending on which scope asked.
     *
     * [heldIn] is the whole predicate, and it is deliberately the exact negation of what makes
     * [valueIn] fabricate a [Value.Top]: same range guard, same [Frame.peek]. `peek` **throws** for an
     * out-of-range register (`Frame.requireInRange`), so the guard is what keeps this query total (spec §9:
     * never throws on ordinary traversal) -- and answering [RegisterOccupancy.NEVER_ASSIGNED] for a register
     * the method does not have is the honest total answer, matching [valueIn]'s [Value.Top] for the same
     * input.
     */
    private fun occupancyOver(
        nodes: List<ExecutionNode>,
        register: Int,
        useEndingFrame: Boolean = false,
    ): RegisterOccupancy = when {
        nodes.isEmpty() -> RegisterOccupancy.NO_PATH
        nodes.any { heldIn(it, register, useEndingFrame) } -> RegisterOccupancy.HELD
        else -> RegisterOccupancy.NEVER_ASSIGNED
    }

    private fun heldIn(node: ExecutionNode, register: Int, useEndingFrame: Boolean): Boolean =
        register in 0 until node.view.registerCount &&
            (if (useEndingFrame) node.endingFrame ?: node.frame else node.frame).peek(register) != null

    /**
     * A register with no value on a path contributes "nothing known", never a fabricated value.
     *
     * Range-checked against [node]'s OWN [ExecutionNode.view], not this graph's [methodView]: a
     * [Frame] range-checks against the [MethodView] it was built for, which is the node's [view]
     * (see [ExecutionNode.view]'s own contract), not necessarily this graph's entry method. Using
     * [methodView]'s (possibly wider) bound here would let a register pass this check yet still
     * blow up inside [Frame.peek] -- a query the spec says must never throw on ordinary traversal.
     * Unreachable through any public API today (one session still walks one method), but this is
     * exactly the shape Plan 9's callee nodes guarantee.
     */
    private fun valueIn(node: ExecutionNode, register: Int): Value =
        if (register in 0 until node.view.registerCount) node.frame.peek(register) ?: Value.Top else Value.Top

    private fun valueInEnding(node: ExecutionNode, register: Int): Value {
        val frame = node.endingFrame ?: node.frame
        return if (register in 0 until node.view.registerCount) frame.peek(register) ?: Value.Top else Value.Top
    }

    /**
     * See [TrustedGraphView.terminatingHeapObject]. Returning nodes are found the same way
     * [terminatingConsensusOverExploredPaths] finds them -- by iterating [MethodView.instructionAddresses], which is
     * already ascending, so the fold order never depends on map iteration order.
     *
     * A returning path that has NO object at [ref] makes the answer null rather than being skipped: the
     * paths then disagree about whether the object exists at all, which is not something an element-wise
     * join can express.
     */
    override fun terminatingHeapObjectOverExploredPaths(ref: HeapRef): HeapObject? {
        val returned = returningNodes()
        if (returned.isEmpty()) return null
        val objects = returned.map { (it.endingFrame ?: it.frame).heapObject(ref) ?: return null }
        val first = objects.first()
        val firstFrame = returned.first().endingFrame ?: returned.first().frame
        val sameGraph = returned.all {
            (it.endingFrame ?: it.frame).hostGraphVersionToken() === firstFrame.hostGraphVersionToken()
        }
        return when (first) {
            is HeapObject.TrackedField -> {
                val fields = objects.map { it as? HeapObject.TrackedField ?: return null }
                if (fields.any { it.metadata != first.metadata }) return null
                if (fields.all { it.accessibility == first.accessibility }) first
                else HeapObject.TrackedField(first.metadata, FieldAccessibility.UNKNOWN)
            }
            is HeapObject.HostObject -> {
                val hosts = objects.map { it as? HeapObject.HostObject ?: return null }
                if (hosts.any { it.type != first.type }) return null
                val contents = if (sameGraph && hosts.all { it.contents == first.contents }) {
                    first.contents
                } else {
                    HostGraphContents.Unknown
                }
                val metadata = first.reflectedMethodMetadata
                    .takeIf { candidate -> hosts.all { it.reflectedMethodMetadata == candidate } }
                if (contents == first.contents && metadata == first.reflectedMethodMetadata) first
                else HeapObject.HostObject(first.type, contents, metadata)
            }
            is HeapObject.GuestThrowable -> {
                val throwables = objects.map { it as? HeapObject.GuestThrowable ?: return null }
                if (throwables.any { it.type != first.type }) return null
                val cause = joinThrowableCauses(throwables.map(HeapObject.GuestThrowable::cause), superclasses)
                val contents = if (sameGraph && throwables.all { it.contents == first.contents }) {
                    first.contents
                } else {
                    HostGraphContents.Unknown
                }
                if (contents == first.contents && cause == first.cause) first
                else HeapObject.GuestThrowable(first.type, contents, cause)
            }
            is HeapObject.TrackedArray -> {
                val arrays = objects.map { it as? HeapObject.TrackedArray ?: return null }
                if (arrays.any { it.type != first.type || it.length != first.length }) return null
                val mirror = when {
                    arrays.all { it.mirror == null } -> null
                    sameGraph && arrays.all { it.mirror == first.mirror } -> first.mirror
                    else -> HostGraphContents.Unknown
                }
                val dirty = mirror is HostGraphContents.Known && arrays.any { it.mirrorDirty }
                var elements = first.elementsRoot()
                if (arrays.any { it.elementsRoot() !== first.elementsRoot() }) {
                    for (index in 0 until first.length) {
                        val value = joinAll(arrays.map { it.element(index) }, superclasses) ?: Value.Top
                        if (value != elements[index]) elements = elements.replacingAt(index, value)
                    }
                }
                if (elements === first.elementsRoot() && mirror == first.mirror && dirty == first.mirrorDirty) first
                else first.withElementsRoot(elements, mirror, dirty)
            }
            is HeapObject.TrackedObject -> {
                val tracked = objects.map { it as? HeapObject.TrackedObject ?: return null }
                if (tracked.any { it.type != first.type || it.uninitialized != first.uninitialized }) return null
                if (tracked.all { it === first || it.contentsRootToken() === first.contentsRootToken() }) return first
                val keys = linkedSetOf<String>()
                tracked.forEach { keys.addAll(it.fields().keys) }
                HeapObject.TrackedObject(
                    first.type,
                    first.uninitialized,
                    LinkedHashMap<String, Value>().apply {
                        for (key in keys) {
                            this[key] = joinAll(tracked.map { it.field(key) ?: Value.Top }, superclasses) ?: Value.Top
                        }
                    },
                )
            }
        }
    }

    /** Test/harness-only Java resolution. The public heap query never exposes mutable host state. */
    internal fun terminatingHostObjectOverExploredPaths(ref: HeapRef): Any? {
        val returned = returningNodes()
        if (returned.isEmpty()) return null
        val first = (returned.first().endingFrame ?: returned.first().frame).hostObjectAt(ref) ?: return null
        return if (returned.all { (it.endingFrame ?: it.frame).hostObjectAt(ref) === first }) first else null
    }

    /**
     * See [ExecutionGraphView.terminatingStaticFieldConsensusOverExploredPaths]. Returning nodes are found the
     * same way [terminatingConsensusOverExploredPaths] finds them, so a register answer and a field answer from
     * one graph always describe the same set of paths.
     *
     * Joined rather than required-identical, unlike [terminatingHeapObjectOverExploredPaths]'s shape checks: a
     * static field's value is one lattice point, so two returning paths that wrote different constants have a
     * perfectly good join (`Unknown`, or `Top` for incompatible types) and there is no shape to disagree about.
     * Two paths whose class-init state differs -- one holding a cell, one not -- join `Value.Top` against the
     * other's value, which is the honest "the paths do not agree that this field has a value".
     */
    override fun terminatingStaticFieldConsensusOverExploredPaths(type: VirtualType, fieldDescriptor: String): Value {
        val returned = returningNodes()
        if (returned.isEmpty()) return Value.Top
        return joinAll(
            returned.map { (it.endingFrame ?: it.frame).getStaticField(type, fieldDescriptor) },
            superclasses,
        ) ?: Value.Top
    }

    /**
     * [nodePile], restricted to nodes whose op actually ran -- the only ones whose
     * [ExecutionNode.frame] is genuine post-op state (the piling decision above). What every
     * value-bearing query filters through before reading a frame.
     */
    private fun executedPile(address: Int): List<ExecutionNode> = nodePile(address).filter { it in executedNodes }

    // --- liveness ---

    override fun registersAssigned(address: Int): Set<Int> =
        opOrNull(address)?.registersAssigned?.toSortedSet() ?: emptySet()

    override fun registersRead(address: Int): Set<Int> =
        opOrNull(address)?.registersRead?.toSortedSet() ?: emptySet()

    override fun wasRegisterAssigned(address: Int, register: Int): Boolean = register in registersAssigned(address)

    override fun wasRegisterRead(address: Int, register: Int): Boolean = register in registersRead(address)

    /**
     * Every register some op in the method reads or assigns, or that holds a parameter. Computed
     * lazily and memoized: [ExecutionGraph] is immutable and this answer is the same for every
     * address (see [availableRegisters]'s own KDoc for why), so recomputing it on every call made
     * an ordinary per-address traversal over a method's addresses -- exactly what a consumer
     * checking every address for available registers does -- O(addresses²) instead of O(addresses).
     */
    private val usedRegisters: Set<Int> by lazy {
        val used = mutableSetOf<Int>()
        for (a in methodView.instructionAddresses) {
            opOrNull(a)?.let { used.addAll(it.registersRead); used.addAll(it.registersAssigned) }
        }
        methodView.parameters.forEach { used.add(it.register); if (it.type.isWide) used.add(it.register + 1) }
        used
    }

    /**
     * Registers free for scratch use: those no op in the method touches and that hold no
     * parameter. Total by construction -- it never requires [address] itself to be reached, so it
     * cannot fail on an unreached or non-existent address (the old engine's equivalent crashed
     * during ordinary traversal). [address] is intentionally unused: this is a whole-method-
     * conservative notion of "free ANYWHERE in the method", not a per-program-point liveness
     * analysis, per the spec's own definition -- coarser than per-point liveness, and therefore
     * usable at any address without re-deriving it per call site.
     *
     * **Wide pairs ARE accounted for, on both sides of [usedRegisters].** A wide (`J`/`D`) value owns
     * two consecutive registers, and this aggregation learns about the high half from either source a
     * wide value can come from: a wide *parameter* contributes `register + 1` directly, and a wide
     * *result* does too, because [Op.destinationRegisters] -- the single expression of that rule, which
     * every single-destination op routes through and whose `isWide` parameter has no default -- names
     * `dest` and `dest + 1`.
     *
     * This was once the opposite, and the case that demonstrated it is worth keeping because nothing
     * else in the graph would catch a regression: `Lspine/spine;.returnWide()J` has `.registers 2` and a
     * single `const-wide v0`, and this query answered `[1]` -- offering v1, the high half of the very
     * pair the next instruction returns, to the first consumer wanting scratch space (Plan 14's
     * unreflection). [Frame] records that high half as an internal wide-partner slot but [Frame.peek]
     * answers null for it, so `consensus`/`registerValues` at v1 read [Value.Top], indistinguishable
     * from a genuinely unassigned register -- the corruption would have been silent. It now answers
     * `[]`, and `ExecutionGraphTest.theHighHalfOfAWideResultIsNotOfferedAsAFreeRegister` pins that
     * through this query, which is where the defect lived: [Op.registersAssigned] named only the low
     * register while the parameter half of the same function named both.
     *
     * **The residual gap is narrower, and it is on the READ side rather than here.**
     * [Op.registersRead] still names only the low half of a wide *source*, so `move-wide v2, v0`
     * reports reading v0 and not v1. That cannot make THIS query unsound -- a wide value can only be in
     * a register because a wide parameter or a wide-result assignment put it there, and both name both
     * halves -- so it is unreachable through `availableRegisters`. It is reachable through
     * [registersRead]/[wasRegisterRead] directly: a consumer asking "does the op here read v1?" about a
     * wide pair's high half is told `false`. Deliberately not widened on speculation -- no fixture in
     * the tree contains a `move-wide`, so the change would be untested.
     *
     * The coarseness in the first paragraph is therefore about *address-independence*, not a blanket
     * safety argument: a register reported free is free ANYWHERE in the method, which is a stronger and
     * cruder claim than per-program-point liveness, and callers must not read it as the latter.
     */
    override fun availableRegisters(address: Int): IntArray =
        (0 until methodView.registerCount).filterNot { it in usedRegisters }.toIntArray()

    // --- effects and completion ---

    /**
     * The MAXIMUM of two independent claims about [address], because effects err upward (spec §4.5) and
     * the two are about different things:
     *
     *  1. **What the op DECLARED**, on any path through this address ([ExecutionNode.sideEffectLevel]).
     *     `NONE` for every op that touches only its own frame and the tracked heap, which is most of
     *     them; `STRONG` where a write's TARGET could not be identified, so some object the engine does
     *     not name may have changed. `APutOp`'s unprovable-array and non-array rows are the first ops in
     *     this build to declare it -- and the reason this half exists at all: without it,
     *     `void f(int[] a) { a[0] = 5; }` reported `Complete` **and** `NONE`, i.e. "safe to delete",
     *     for a method whose entire observable behaviour is a write the caller can see. That was not a
     *     missing feature but a REGRESSION introduced the moment `aput` became supported: while it was
     *     an `UnsupportedOp`, rule 2 below answered `STRONG` and the trust gate refused to answer at all.
     *  2. **An unimplemented opcode**: what the real opcode would have done is unknown, so an address
     *     whose path aborted on `UNSUPPORTED_OP` reports `STRONG` regardless of what any op declared --
     *     it never ran and so could not declare anything. That outcome can only come from a node whose
     *     op did run (`UnsupportedOp.execute` itself returns the Abort transition), so this query needs
     *     no executed-only filter; a limit-aborted node can never carry it.
     *
     * A node that never ran has a declared level of `NONE` by construction, which is why rule 1 needs no
     * filter either.
     */
    override fun sideEffectLevel(address: Int): SideEffectLevel {
        val pile = nodePile(address)
        val declared = pile.fold(SideEffectLevel.NONE) { acc, node -> maxOf(acc, node.sideEffectLevel) }
        val unsupported = pile.any { it.outcome == Outcome.Aborted(AbortReason.UNSUPPORTED_OP) }
        return if (unsupported) SideEffectLevel.STRONG else declared
    }

    override fun didEveryExecutionContinueNormallyOverExploredPaths(address: Int): Boolean {
        val pile = nodePile(address)
        return pile.isNotEmpty() && pile.all {
            it.opExecutionStarted &&
                it.normalContinuationObserved &&
                !it.exceptionalContinuationObserved &&
                it.outcome !is Outcome.Aborted &&
                it.outcome !is Outcome.Threw
        }
    }

    override fun methodSideEffectLevelOverExploredPaths(): SideEffectLevel =
        methodView.instructionAddresses.map { sideEffectLevel(it) }.maxByOrNull { it.ordinal }
            ?: SideEffectLevel.NONE

    override fun completion(): CompletionStatus = completion

    // --- the trust gate (spec §9) ---

    /**
     * Built lazily so a `Partial` graph -- the common case in a real obfuscated APK -- never allocates
     * one at all, and memoized because a strategy asks per address.
     */
    private val trustedView: TrustedGraphView by lazy { TrustedView() }

    /**
     * The trust-bearing queries, or null for a `Partial` run -- and for a
     * [CompletionStatus.InProgress] one, which is what a mid-run [ExecutionSession.graph] snapshot
     * carries. See [ExecutionGraphView.trusted] for the contract and [TrustedGraphView] for why the
     * split exists.
     *
     * Gated on [CompletionStatus.canTrustConsensus] and NOT on a second `is Complete` test of its own:
     * that predicate is the spec's single definition of "may be acted on", so routing this through it
     * means a later change to what counts as trustworthy cannot leave the gate behind.
     */
    override fun trusted(): TrustedGraphView? = if (completion.canTrustConsensus) trustedView else null

    /**
     * Pure delegation to the ungated members (`isDead` only combines `wasReached` and `isPayload`), and it
     * must stay that way: the gate decides WHO may act on an answer, never WHAT the answer is (spec §9's
     * queries are pure reads of recorded nodes). Re-deriving anything here -- even "conservatively", e.g. collapsing to
     * [Value.Top] -- would make the trusted path useless and push consumers back onto the ungated one,
     * reintroducing exactly the hole this gate closes.
     *
     * Deliberately a private class rather than [ExecutionGraph] implementing [TrustedGraphView]
     * itself: if the graph carried these members, `graph.consensus(a, r)` would compile again and the
     * gate would be decorative.
     */
    private inner class TrustedView : TrustedGraphView {
        override val methodView: MethodView get() = this@ExecutionGraph.methodView
        override val programRevision: ProgramRevision get() = this@ExecutionGraph.programRevision
        override val methodRevision: ApplicationMethodRevision? get() = this@ExecutionGraph.methodRevision

        override fun consensus(address: Int, register: Int): Value =
            consensusOverExploredPaths(address, register)

        override fun consensus(addresses: IntArray, register: Int): Value =
            consensusOverExploredPaths(addresses, register)

        override fun reflectedMethod(address: Int, register: Int): ReflectedMethodMetadata? =
            reflectedMethodOverExploredPaths(address, register)

        override fun reflectedMethod(addresses: IntArray, register: Int): ReflectedMethodMetadata? =
            reflectedMethodOverExploredPaths(addresses, register)

        override fun reflectedField(address: Int, register: Int): ReflectedFieldMetadata? =
            reflectedFieldOverExploredPaths(address, register)

        override fun reflectedField(addresses: IntArray, register: Int): ReflectedFieldMetadata? =
            reflectedFieldOverExploredPaths(addresses, register)

        override fun arrayLength(addresses: IntArray, register: Int): Int? =
            arrayLengthOverExploredPaths(addresses, register)

        override fun terminatingConsensus(register: Int): Value =
            terminatingConsensusOverExploredPaths(register)

        override fun registerValues(address: Int, register: Int): Set<Value> =
            registerValuesOverExploredPaths(address, register)

        override fun knownString(address: Int, register: Int): String? =
            knownStringOverExploredPaths(address, register)

        override fun registerOccupancy(address: Int, register: Int): RegisterOccupancy =
            registerOccupancyOverExploredPaths(address, register)

        override fun terminatingRegisterOccupancy(register: Int): RegisterOccupancy =
            terminatingRegisterOccupancyOverExploredPaths(register)

        override fun didAnyPathReturn(): Boolean = didAnyPathReturnOverExploredPaths()

        override fun methodSideEffectLevel(): SideEffectLevel = methodSideEffectLevelOverExploredPaths()

        override fun didEveryExecutionContinueNormally(address: Int): Boolean =
            didEveryExecutionContinueNormallyOverExploredPaths(address)

        override fun isDead(address: Int): Boolean =
            methodView.hasAddress(address) && !isPayload(address) && !wasReached(address)

        override fun parentAddresses(address: Int): IntArray = this@ExecutionGraph.parentAddresses(address)

        override fun children(address: Int): IntArray = this@ExecutionGraph.children(address)

        override fun sideEffectLevel(address: Int): SideEffectLevel = this@ExecutionGraph.sideEffectLevel(address)

        override fun terminatingHeapObject(ref: HeapRef): HeapObject? =
            terminatingHeapObjectOverExploredPaths(ref)

        override fun terminatingStaticFieldConsensus(type: VirtualType, fieldDescriptor: String): Value =
            terminatingStaticFieldConsensusOverExploredPaths(type, fieldDescriptor)

        override fun toString(): String = "TrustedGraphView(${methodView.signature})"
    }

    override fun toString(): String =
        "ExecutionGraph(${methodView.signature}, " +
            "reached=${methodView.instructionAddresses.filter { wasReached(it) }}, completion=$completion)"

    private fun opOrNull(address: Int): Op? = nodePile(address).firstOrNull()?.op
}
