package org.cf.smalivm.core


/**
 * Queries over the paths one run executed. An answer describes only the paths the run walked, so it is
 * sound to read and unsafe to act on until the run is `canTrustConsensus`; [TrustedGraphView] is the same queries
 * over a complete run. Every query is pure and total. The rules they follow are stated in
 * `docs/architecture/graph-optimizer-contract.md`.
 */
public open class ExploredPaths internal constructor(protected val graph: ExecutionGraph) {
    /** The method revision these paths executed. */
    public val methodView: MethodView get() = graph.methodView
    /** Program revision whose whole-program facts the run used. */
    public val programRevision: ProgramRevision get() = graph.programRevision
    /** Application method revision the run executed. */
    public val methodRevision: ApplicationMethodRevision? get() = graph.methodRevision

    /** Addresses whose op flows into [address] on some explored path. */
    public fun parentAddresses(address: Int): IntArray =
        graph.nodePile(address).mapNotNull { it.parent?.address }.distinct().sorted().toIntArray()

    /** Addresses [address]'s op flows into on some explored path. */
    public fun children(address: Int): IntArray =
        graph.nodePile(address).flatMap { node -> node.children.map { it.address } }.distinct().sorted().toIntArray()

    /** Join of [register] after the op at [address] over every node whose op ran there; [Value.Top] when none ran. */
    public fun consensus(address: Int, register: Int): Value = consensus(intArrayOf(address), register)

    /** [consensus] over the union of nodes at [addresses]. */
    public fun consensus(addresses: IntArray, register: Int): Value {
        val nodes = addresses.flatMap(graph::executedPile)
        if (nodes.isEmpty()) return Value.Top
        return joinAll(nodes.map { valueIn(it.frame, register) }, graph.superclasses) ?: Value.Top
    }

    /** The distinct values [register] holds after [address]; empty when no op ran there. */
    public fun registerValues(address: Int, register: Int): Set<Value> =
        graph.executedPile(address).map { valueIn(it.frame, register) }.toSet()

    /** Whether any node whose op ran at [address] holds a value in [register]. */
    internal fun registerOccupancy(address: Int, register: Int): RegisterOccupancy =
        occupancy(graph.executedPile(address).map(ExecutionNode::frame), register)

    /** The reflected method [register] holds after [address]. */
    internal fun reflectedMethod(address: Int, register: Int): Agreement<ReflectedMethodMetadata> =
        reflectedMethod(intArrayOf(address), register)

    /** The reflected method [register] holds over the union of nodes at [addresses]. */
    public fun reflectedMethod(addresses: IntArray, register: Int): Agreement<ReflectedMethodMetadata> =
        agreement(addresses) { node ->
            val ref = heapRefIn(node.frame, register) ?: return@agreement null
            (node.frame.heapObject(ref) as? HeapObject.HostObject)?.reflectedMethodMetadata
        }

    /** The reflected field [register] holds after [address]. */
    internal fun reflectedField(address: Int, register: Int): Agreement<ReflectedFieldMetadata> =
        reflectedField(intArrayOf(address), register)

    /** The reflected field [register] holds over the union of nodes at [addresses]. */
    public fun reflectedField(addresses: IntArray, register: Int): Agreement<ReflectedFieldMetadata> {
        val field = agreement(addresses) { node ->
            heapRefIn(node.frame, register)?.let { node.frame.heap.trackedFieldAt(it)?.metadata }
        }
        return when (field) {
            is Agreement.Agreed -> Agreement.Agreed(
                ReflectedFieldMetadata(
                    FieldSignature(field.value.declaringType, field.value.name, field.value.fieldType).toFieldReference(),
                    field.value.modifiers,
                ),
            )
            Agreement.Disagree -> Agreement.Disagree
            Agreement.Absent -> Agreement.Absent
            Agreement.NoPath -> Agreement.NoPath
        }
    }

    /** The length of the tracked array [register] holds over the union of nodes at [addresses]. */
    internal fun arrayLength(addresses: IntArray, register: Int): Agreement<Int> = agreement(addresses) { node ->
        heapRefIn(node.frame, register)?.let { (node.frame.heapObject(it) as? HeapObject.TrackedArray)?.length }
    }

    /** The immutable String [register] denotes after [address], without exposing any host object. */
    public fun knownString(address: Int, register: Int): Agreement<String> = agreement(intArrayOf(address)) { node ->
        when (val raw = (valueIn(node.frame, register) as? Value.Known)?.raw) {
            is String -> raw
            is HeapRef -> node.frame.heap.hostObjectAt(raw) as? String
            else -> null
        }
    }

    /** Join of [register] over the returning paths only; [Value.Top] when no path returned. */
    public fun terminatingConsensus(register: Int): Value {
        val returned = graph.returningNodes()
        if (returned.isEmpty()) return Value.Top
        return joinAll(returned.map { valueIn(it.terminalFrame, register) }, graph.superclasses) ?: Value.Top
    }

    /** [registerOccupancy] over the returning paths; [RegisterOccupancy.NO_PATH] when no path returned. */
    internal fun terminatingRegisterOccupancy(register: Int): RegisterOccupancy =
        occupancy(graph.returningNodes().map(ExecutionNode::terminalFrame), register)

    /** Whether any path returned normally. */
    internal fun didAnyPathReturn(): Boolean = graph.returningNodes().isNotEmpty()

    /**
     * Join of the static field [fieldDescriptor] (fully qualified, `Lcom/example/Foo;->bar:I`) of [type] over the
     * returning paths; [Value.Top] when no path returned or a path never initialized [type].
     */
    internal fun terminatingStaticFieldConsensus(type: VirtualType, fieldDescriptor: String): Value {
        val returned = graph.returningNodes()
        if (returned.isEmpty()) return Value.Top
        return joinAll(returned.map { it.terminalFrame.getStaticField(type, fieldDescriptor) }, graph.superclasses)
            ?: Value.Top
    }

    /**
     * The object [ref] denotes, joined over the returning paths: a surviving [Value.Known] element or field means
     * every returning path agrees about it. [Agreement.Absent] when a returning path has no object at [ref].
     * The object is synthetic and in no path's heap; read it, never write it.
     */
    public fun terminatingHeapObject(ref: HeapRef): Agreement<HeapObject> {
        val frames = graph.returningNodes().map(ExecutionNode::terminalFrame)
        if (frames.isEmpty()) return Agreement.NoPath
        val objects = frames.map { it.heapObject(ref) ?: return Agreement.Absent }
        val graphToken = frames.first().hostGraphVersionToken()
        val sameGraph = frames.all { it.hostGraphVersionToken() === graphToken }
        return Agreement.Agreed(HeapStore.mergeObject(objects, graph.superclasses, sameGraph))
    }

    /**
     * The most any op at [address] declared, or `STRONG` when a path aborted there for a reason that
     * [AbortReason.impliesStrongEffect]. `NONE` for an address no op ran at, so `NONE` is evidence of harmlessness
     * only where [ExecutionGraph.wasReached] confirms an op ran.
     */
    public fun sideEffectLevel(address: Int): SideEffectLevel {
        val pile = graph.nodePile(address)
        val abortedStrong = pile.any { (it.outcome as? Outcome.Aborted)?.reason?.impliesStrongEffect == true }
        if (abortedStrong) return SideEffectLevel.STRONG
        return pile.fold(SideEffectLevel.NONE) { acc, node -> maxOf(acc, node.sideEffectLevel) }
    }

    /** The highest [sideEffectLevel] over the method's instructions. */
    internal fun methodSideEffectLevel(): SideEffectLevel =
        methodView.instructionAddresses.maxOfOrNull(::sideEffectLevel) ?: SideEffectLevel.NONE

    /**
     * True only when every node attempted at [address] ran and continued normally, with no handled, escaping or
     * aborted exceptional partition. False for an unattempted address.
     */
    public fun didEveryExecutionContinueNormally(address: Int): Boolean {
        val pile = graph.nodePile(address)
        return pile.isNotEmpty() && pile.all { node ->
            val progress = node.progress
            progress is OpProgress.Executed &&
                progress.continuedNormally &&
                !progress.continuedExceptionally &&
                node.outcome !is Outcome.Aborted &&
                node.outcome !is Outcome.Threw
        }
    }

    /**
     * [valueOf] at every node whose op ran at [addresses]: agreed when all give one value, absent as soon as one
     * gives null.
     */
    private inline fun <T : Any> agreement(addresses: IntArray, valueOf: (ExecutionNode) -> T?): Agreement<T> {
        var agreed: T? = null
        var disagree = false
        for (address in addresses) {
            for (node in graph.executedPile(address)) {
                val value = valueOf(node) ?: return Agreement.Absent
                if (agreed == null) agreed = value else if (agreed != value) disagree = true
            }
        }
        return when {
            agreed == null -> Agreement.NoPath
            disagree -> Agreement.Disagree
            else -> Agreement.Agreed(agreed)
        }
    }

    private fun occupancy(frames: List<Frame>, register: Int): RegisterOccupancy = when {
        frames.isEmpty() -> RegisterOccupancy.NO_PATH
        frames.any { register in 0 until it.registerCount && it.peek(register) != null } -> RegisterOccupancy.HELD
        else -> RegisterOccupancy.NEVER_ASSIGNED
    }

    /** [register] in [frame]; [Value.Top] when unassigned or outside the frame's method, so this never throws. */
    private fun valueIn(frame: Frame, register: Int): Value =
        if (register in 0 until frame.registerCount) frame.peek(register) ?: Value.Top else Value.Top

    private fun heapRefIn(frame: Frame, register: Int): HeapRef? =
        (valueIn(frame, register) as? Value.Known)?.raw as? HeapRef

    override fun toString(): String = "${javaClass.simpleName}(${methodView.signature})"
}

/**
 * [ExploredPaths] of a run whose completion is `canTrustConsensus`: every feasible path was walked, so its
 * answers may be acted on. Only [ExecutionGraph.trusted] hands one out.
 */
public class TrustedGraphView internal constructor(graph: ExecutionGraph) : ExploredPaths(graph) {
    /**
     * True when [address] holds an executable instruction that no path reached, which on a complete run proves it
     * dead. False for a payload pseudo-instruction, which control flow never reaches on any run, and for an
     * address outside the method.
     */
    public fun isDead(address: Int): Boolean =
        methodView.hasAddress(address) && !methodView.isPayload(address) && !graph.wasReached(address)
}
