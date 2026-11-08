package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/**
 * Why a register reads [Value.Top]: whether any path in scope **held** a value there at all.
 *
 * [Value.Top] is correctly the top of the lattice, and it is reached by **three** different facts about
 * a run. A consumer deciding whether to fold a constant or delete a method needs them apart, because
 * they license different actions, and until this type existed the answer to all three was one
 * [Value.Top]:
 *
 *  1. **A path genuinely holds a [Value.Top].** `MoveOp`'s register arm reads through [Frame.read],
 *     which is `peek(r) ?: Value.Top`, so `move v1, v9` from an untouched v9 assigns a real
 *     [Value.Top]; `move-exception` on a path with no caught value does the same. → [HELD].
 *  2. **The paths returned and disagreed incompatibly**, so the join is [Value.Top]. → also [HELD],
 *     deliberately: both mean "live and unknowable", and *how many* values disagreed is what
 *     [ExecutionGraphView.registerValuesOverExploredPaths] is for.
 *  3. **Nothing ever assigned the register**, so `ExecutionGraph.valueIn` turned [Frame.peek]'s null
 *     into [Value.Top]. → [NEVER_ASSIGNED].
 *
 * plus [NO_PATH] for the two cases where there was nothing to ask at all.
 *
 * **Why this is an enum and not a nullable, and the reason is a production post-mortem rather than
 * taste.** The engine being replaced *did* separate "the paths disagreed" from "never assigned", and
 * said so in its own contract (`org.cf.smalivm.ExecutionGraph.getRegisterConsensus`'s KDoc: *"`UnknownValue`
 * if `register` was assigned but there was no consensus, null if the register was never set"*). The
 * separation collapsed because absence was signalled **in band**, as a nullable return: that `return null`
 * is nested inside an `if (log.isWarnEnabled)`, so with WARN disabled execution falls through into
 * unknown-type construction and a *semantic* answer depends on a *logging level*. And because the signal
 * was in band, the consumer that wanted the value force-unwrapped it -- `getRegisterConsensusValue` is
 * `getRegisterConsensus(...)!!.raw` and can throw. Both are still in the tree; the audit files the pair at
 * `docs/architecture/state-model.md:141` and `docs/architecture/graph-optimizer-contract.md:68-78`, and
 * `docs/architecture/exceptions.md:142` records the WARN-dependent path as the mechanism of a host crash.
 *
 * **And the shape of THIS api is confirmed by the old engine's one real consumer, which needed exactly
 * these two facts and could only pair them by hand.** `org.cf.smalivm.opcode.InvokeOp` reads a callee's
 * return value like this (`opcode/InvokeOp.kt:148-153`, verified in the source tree):
 *
 * ```
 * val hasOneReturnPath = calleeGraph.hasAtLeastOneReturnPath()
 * if (hasOneReturnPath) {
 *     if (!method.returnsVoid()) {
 *         // Terminating addresses may include throw ops which may not have a return register set
 *         val result = calleeGraph.getRegisterConsensus(..., RETURN_REGISTER)!!
 * ```
 *
 * -- i.e. "did any path return" is asked FIRST, as a separate query, because the consensus read alone
 * cannot answer it; and the consensus read is then force-unwrapped anyway, with a comment admitting the
 * case that survives the guard. `docs/architecture/exceptions.md:142` records that this exact `!!` is a
 * host crash whose reachability depends on the log level. So [ExecutionGraphView.didAnyPathReturnOverExploredPaths]
 * is not a new idea -- it is `hasAtLeastOneReturnPath` promoted onto the query surface and *paired* with a
 * register answer that is total, so no caller has to write the `!!` that the pairing was supposed to make
 * safe. A third site of the same shape is `ExecutionState.kt:288`, where `readRegister` is
 * `peekRegister(register)!!` under a TODO asking what happens for an uninitialized register; it kills the
 * engine on `move v0, v2`, which the differential registry now pins as
 * `branch-move-from-untouched-register`.
 *
 * So the rule this type enforces is not "don't widen [Value]" -- it is **never signal absence in band**.
 * A `Value?` or a `Boolean?` here would be the same mistake with a new name, and a reviewer proposing one
 * as "simpler" is proposing the shape that shipped the crash. A total answer cannot be force-unwrapped and
 * cannot be made to depend on a log level.
 *
 * **Widening [Value] with a "paths disagree" case was considered and rejected**, and not for the same
 * reason: these are not two lattice *values*, they are one value reached by two facts about the RUN, so
 * they belong on the graph query. A case on [Value] would push a run-shape fact into every `when` over the
 * lattice, including the ops, which have no business knowing it.
 */
enum class RegisterOccupancy {
    /**
     * At least one path in scope holds a value in this register -- possibly a [Value.Known], possibly an
     * [Value.Unknown], possibly a genuine [Value.Top] a `move` put there. With a [Value.Top] consensus this
     * means **live and unknowable**: do not fold, do not treat the register as free.
     */
    HELD,

    /**
     * Paths in scope exist and **none** of them holds a value in this register. With a [Value.Top]
     * consensus, and [ExecutionGraphView.wasReached] confirming the address, this means **dead**.
     *
     * It does **not** mean "free". Two cases answer this without the register being available:
     * the **high half of a live wide (`J`/`D`) pair**, which [Frame.assign] records as an internal
     * wide-partner slot that [Frame.peek] answers null for; and a register out of the method's range at
     * all. A consumer hunting scratch space must use [ExecutionGraphView.availableRegisters], which is
     * static, accounts for wide pairs on both sides, and is already correct --
     * `AssignmentDistinctionTest.theHighHalfOfALiveWidePairReadsAsNeverAssignedAndAvailableRegistersIsWhatKnowsBetter`
     * pins exactly that pair of answers.
     */
    NEVER_ASSIGNED,

    /**
     * There is no path in scope to ask. For the per-address query that means no node ever *ran an op* at
     * the address, i.e. [ExecutionGraphView.wasReached] is false; for the terminating query it means **no
     * path returned normally at all**.
     *
     * **It does not separate "never attempted" from "attempted, then stopped by a bound"** -- the same
     * shape [ExecutionGraphView.sideEffectLevel]'s KDoc records for its own `NONE`. A caller that needs
     * those apart pairs this with [ExecutionGraphView.wasAttempted] (was a node ever dequeued here?).
     * And on a run whose [ExecutionGraphView.completion] is not `canTrustConsensus`, this may mean
     * nothing more than "the run stopped early" -- which is why the gated twins on [TrustedGraphView]
     * exist and why the ungated ones are named for being ungated.
     */
    NO_PATH,
}

/** Immutable identity data needed to replace one exact reflected method invocation with Dalvik bytecode. */
data class ReflectedMethodMetadata(
    val method: MethodReference,
    val modifiers: Int,
    val declaringClassModifiers: Int,
)

/** Immutable identity data needed to replace one exact reflected field read with Dalvik bytecode. */
data class ReflectedFieldMetadata(val field: FieldReference, val modifiers: Int)

/**
 * The queries whose answers a consumer would **act on** — fold a constant, delete a method — and
 * which are therefore meaningful only when exploration actually finished (spec §9 completion-gated
 * trust). Reachable only through [ExecutionGraphView.trusted], which is null for a `Partial` run —
 * and, just as importantly, for a [CompletionStatus.InProgress] one. "Actually finished" is enforced
 * in **time** as well as in kind: `ExecutionSession.graph()` may be called mid-run (Plan 10's
 * debugger), and such a snapshot has an empty abort set, so a gate keyed on aborts alone would hand
 * it a fully actionable view of a walk that had not finished. See [CompletionStatus.InProgress].
 *
 * **Why this type exists.** The trust rule used to be KDoc only, and the incentives ran the wrong
 * way: on a `Partial` run `consensus(addr, r)` handed back a confident [Value.Known] and
 * `methodSideEffectLevel()` handed back `NONE`, so a strategy could fold a constant, or delete a
 * method, out of an exploration that stopped at a bound — and the *safe* path (consult
 * [CompletionStatus] first) was strictly longer to type than the unsafe one. Splitting the
 * trust-bearing queries onto a view you can only get from a completed run makes the compiler enforce what the
 * documentation previously only requested: `graph.trusted()?.consensus(a, r)` is now the SHORT
 * spelling, and reaching a confident answer from a truncated run costs a deliberately longer,
 * differently-named call.
 *
 * **What it does not do.** It changes who can reach a query, never what a query computes: every
 * member here is the same pure read as its `...OverExploredPaths` twin on [ExecutionGraphView], and
 * both stay total (never throwing on ordinary traversal). A consumer that legitimately wants the
 * truncated picture — Plan 10's debugger, Plan 11's differential — calls the twin directly and says so
 * by name.
 */
interface TrustedGraphView {
    /** Immutable application method baseline captured by this trusted graph. */
    val methodView: MethodView
    /** Program revision whose whole-program facts made this graph trustworthy. */
    val programRevision: ProgramRevision
    /** Application method revision executed by this graph. */
    val methodRevision: ApplicationMethodRevision?

    /** See [ExecutionGraphView.consensusOverExploredPaths]. */
    fun consensus(address: Int, register: Int): Value
    /** See [ExecutionGraphView.consensusOverExploredPaths]. */
    fun consensus(addresses: IntArray, register: Int): Value
    /** See [ExecutionGraphView.reflectedMethodOverExploredPaths]. */
    fun reflectedMethod(address: Int, register: Int): ReflectedMethodMetadata?
    /** See [ExecutionGraphView.reflectedMethodOverExploredPaths]. */
    fun reflectedMethod(addresses: IntArray, register: Int): ReflectedMethodMetadata?
    /** See [ExecutionGraphView.reflectedFieldOverExploredPaths]. */
    fun reflectedField(address: Int, register: Int): ReflectedFieldMetadata?
    /** See [ExecutionGraphView.reflectedFieldOverExploredPaths]. */
    fun reflectedField(addresses: IntArray, register: Int): ReflectedFieldMetadata?
    /** See [ExecutionGraphView.arrayLengthOverExploredPaths]. */
    fun arrayLength(addresses: IntArray, register: Int): Int?
    /**
     * Consensus over **return** paths only (spec §6.5); [Value.Top] when no path returned — which is a
     * DIFFERENT fact from "the returning paths agreed on nothing", and [terminatingRegisterOccupancy] plus
     * [didAnyPathReturn] are what tell them apart.
     */
    fun terminatingConsensus(register: Int): Value
    /** See [ExecutionGraphView.registerValuesOverExploredPaths]. */
    fun registerValues(address: Int, register: Int): Set<Value>
    /** See [ExecutionGraphView.knownStringOverExploredPaths]. */
    fun knownString(address: Int, register: Int): String?
    /** See [ExecutionGraphView.registerOccupancyOverExploredPaths]. */
    fun registerOccupancy(address: Int, register: Int): RegisterOccupancy
    /** See [ExecutionGraphView.terminatingRegisterOccupancyOverExploredPaths]. */
    fun terminatingRegisterOccupancy(register: Int): RegisterOccupancy
    /** See [ExecutionGraphView.didAnyPathReturnOverExploredPaths]. */
    fun didAnyPathReturn(): Boolean
    /** See [ExecutionGraphView.methodSideEffectLevelOverExploredPaths]. */
    fun methodSideEffectLevel(): SideEffectLevel
    /** See [ExecutionGraphView.didEveryExecutionContinueNormallyOverExploredPaths]. */
    fun didEveryExecutionContinueNormally(address: Int): Boolean
    /**
     * True when [address] holds an executable instruction that no explored path reached. The run is complete, so
     * this proves the instruction dead. Payload data and addresses outside the method are never dead.
     */
    fun isDead(address: Int): Boolean
    /** See [ExecutionGraphView.parentAddresses]. */
    fun parentAddresses(address: Int): IntArray
    /** See [ExecutionGraphView.children]. */
    fun children(address: Int): IntArray
    /** See [ExecutionGraphView.sideEffectLevel]. */
    fun sideEffectLevel(address: Int): SideEffectLevel
    /** See [ExecutionGraphView.terminatingStaticFieldConsensusOverExploredPaths]. */
    fun terminatingStaticFieldConsensus(type: VirtualType, fieldDescriptor: String): Value
    /**
     * The tracked object [ref] denotes, reconciled over **return** paths only -- the heap counterpart of
     * [terminatingConsensus], and required rather than convenient.
     *
     * [terminatingConsensus] answering `Known(HeapRef(n), T)` proves the returning paths agree about
     * WHICH object the register denotes. It proves nothing about that object's CONTENTS: an array
     * allocated before a fork carries the same id on every arm while each arm's heap holds its own
     * elements, so a consumer reading one path's heap would report a value another path contradicts.
     * This joins them element by element, so a surviving [Value.Known] element means every returning
     * path agrees about that element.
     *
     * Null when no returning path has an object at [ref], or when the paths disagree about its **shape**
     * (different runtime kind, declared type, or array length) -- three things a join cannot
     * over-approximate, so the honest answer is "nothing known" rather than an invented shape.
     *
     * The returned array is **synthetic**: it exists in no path's heap and its elements are joins. Read
     * it, never write it (the mutators are `internal` for that reason).
     */
    fun terminatingHeapObject(ref: HeapRef): HeapObject?
}

/**
 * The §9 consumer/analysis query surface, sketched in Phase 1 so the core cannot grow
 * optimizer-hostile. All queries are PURE: no clone-on-read, no mutation, ever.
 *
 * Two load-bearing semantics every implementation and consumer must honor (spec §9):
 *
 *  1. **Post-op state.** The state at an address is the state *after* that address's op
 *     executed; consensus at an op's own address is the value it *produced*. When the op
 *     destroys or never computes the needed value (a check-cast recast; an unemulated
 *     `Method.invoke`/`Field.get` yielding `Unknown`), the strategy reads the op's PARENT
 *     addresses — hence [parentAddresses] and the multi-address
 *     [consensusOverExploredPaths] overload are load-bearing, not polish.
 *  2. **Completion-gated trust.** A strategy may act on consensus only from a graph whose
 *     [completion] is `canTrustConsensus` (spec §6.4). Agreement across explored paths
 *     proves nothing when some relevant path was never walked. This is **type-enforced**, not
 *     merely documented: the trust-bearing queries live on [TrustedGraphView] and are reached
 *     through [trusted], which is null for a `Partial` run. The `...OverExploredPaths` members
 *     below are the same reads with no gate, named so that using one is a visible decision.
 *
 * [getOp], [nodePile] and [wasAttempted] describe execution. Static instructions belong to [MethodView],
 * not the execution graph's query interface. [TrustedGraphView.methodView] binds an optimizer's
 * instruction reads to the same revision as its completed analysis.
 */
interface ExecutionGraphView {
    // --- Structure ---
    /** Every code address in the method, ascending (code-unit offsets). */
    fun addresses(): IntArray
    /**
     * True if at least one explored path reached [address].
     *
     * **A `false` is a dead-code PROOF only when [completion] is `canTrustConsensus`.** Ungated on
     * purpose — a debugger showing coverage on a truncated run needs it, and gating it would break
     * that — but it carries the same ambiguity the trust gate exists to close, in the one query a
     * dead-code strategy reaches for FIRST. On a `Partial` run `false` means either "genuinely
     * unreachable" or "not explored yet, because a bound stopped the run", and this query cannot tell
     * them apart: a method with a genuinely dead branch plus a loop elsewhere that trips an
     * address-visit or step bound produces exactly the same `false` as one whose branch simply was
     * never walked. On a [CompletionStatus.InProgress] run — a mid-run snapshot — every address the
     * walk has not reached YET answers `false` too, which is the same ambiguity arriving even earlier.
     * Deleting code on the strength of either is a wrong optimization. A caller that acts on `false`
     * MUST check [completion] itself; the compiler will not do it here.
     *
     * (`true` needs no such caveat: a node at [address] demonstrably ran its op, and no amount of
     * further exploration can un-run it.)
     *
     * **THIRD CASE, and it survives the completion check: a PAYLOAD address answers `false` on EVERY
     * run, including a `Complete` one.** `.array-data` and the two switch tables are data, not code
     * ([CON] B22: *"The `packed-switch-data`, `sparse-switch-data`, and `fill-array-data`
     * pseudo-instructions must not be reachable by control flow."*), so this engine decodes them at
     * build time and no op ever runs at their addresses. There is no exploration that could change that,
     * which is exactly why the completion gate does not help here: a dead-code strategy that does what
     * the paragraph above prescribes -- check [completion], find `Complete`, treat `false` as proof --
     * deletes a switch's table and breaks every switch referencing it. **So a consumer acting on `false`
     * must also exclude [isPayload].** Not hypothetical: `docs/architecture/graph-optimizer-contract.md:136`
     * records that the OLD dead-removal strategy needed exactly this carve-out **in production**, and it
     * got there the ugly way -- the old engine *executed* payloads, so their addresses were reached, and
     * Simplify still needed the special case.
     */
    fun wasReached(address: Int): Boolean
    /**
     * Whether [address] holds a payload pseudo-instruction -- `.array-data`, `packed-switch-data` or
     * `sparse-switch-data`.
     *
     * **The companion [wasReached] needs and the reason this is on THIS interface.** The data existed
     * already, on [MethodView] (`isPayload`/`payloadAddresses`, whose KDoc names Plan 14's dead-code
     * strategy as the consumer that would need it), but a decode-time view is not what a consumer reads:
     * [ExecutionGraphView] is, and it had no payload query at all. A projection convention in the
     * differential harness protects no real consumer either. So this delegates to the [MethodView] the
     * graph already holds -- one answer, one owner, `OpcodeGroups.PAYLOAD` -- rather than
     * introducing a second notion of "which addresses are data".
     *
     * **Static, and deliberately ungated.** It reads the decoded instruction and no node, so it is
     * equally true of a `Partial` run, an [CompletionStatus.InProgress] snapshot and a `Complete` one --
     * which is what makes it usable as the guard on an ungated [wasReached] negative. Answers `false` for
     * an address the method does not contain, exactly as it does for ordinary code.
     */
    fun isPayload(address: Int): Boolean
    /** The method's try blocks (handler routing, spec §6/§7). */
    fun tryBlocks(): List<TryBlock>
    /** Addresses whose op flows into [address] on some explored path (spec §9 parent reads). */
    fun parentAddresses(address: Int): IntArray
    /** Addresses [address]'s op flows into on some explored path. */
    fun children(address: Int): IntArray
    /**
     * The op decoded at [address]. Throws only if no path ever *attempted* [address] -- check
     * [wasAttempted] first. A node a bound (address-visit/step/timeout limit) stopped before it ran
     * still records the op, so this can succeed even where [wasReached] is `false`: reaching and
     * merely attempting are different questions, and this answers the weaker one.
     */
    fun getOp(address: Int): Op
    /**
     * Every node the scheduler produced at [address] across all explored paths -- the address's
     * "pile", and the raw material [consensusOverExploredPaths] reconciles. Empty for an address no path ever
     * attempted. May include a node a bound (address-visit/step/timeout limit) stopped before its
     * op ran: such a node's frame is not this address's post-op state, so [wasReached] and every
     * value-bearing query below do not count it, even though it still appears here.
     */
    fun nodePile(address: Int): List<ExecutionNode>
    /**
     * True if the scheduler ever dequeued a node at [address], whether or not that node's op went
     * on to run -- the actual precondition [getOp] needs. Deliberately weaker than [wasReached],
     * which additionally requires the op to have executed.
     */
    fun wasAttempted(address: Int): Boolean = nodePile(address).isNotEmpty()

    // --- Consensus: the reconciliation read (spec §6) ---
    // UNGATED, and named for it. Each of these is the exact computation of the same-named member on
    // TrustedGraphView, minus the completion gate: the answer describes only the paths this run
    // actually walked, which on a Partial run is a subset of the feasible ones. Sound to READ, unsafe
    // to ACT on — get a TrustedGraphView from `trusted()` for that.
    /**
     * Join (⊔) of [register] over [address]'s live nodes; a surviving [Value.Known] means every explored path agrees.
     * Total: an unreached [address], or a [register] never assigned on any live node there, yields [Value.Top] —
     * mirroring [joinAll]'s empty-input "nothing known" — this query never throws on ordinary traversal.
     */
    fun consensusOverExploredPaths(address: Int, register: Int): Value
    /**
     * Consensus of [register] over the union of nodes at all [addresses]. Unreflection reads
     * the reflected `Method`/`Field` from an invoke's PARENT addresses because the invoke op
     * itself produces `Unknown` (spec §9) — this overload is required, not convenience.
     * Total: if none of [addresses] were reached, or [register] was never assigned on any live
     * node among them, the result is [Value.Top]; this query never throws on ordinary traversal.
     */
    fun consensusOverExploredPaths(addresses: IntArray, register: Int): Value
    /**
     * The exact reflected method held by [register] on every executed node at [address], or null when
     * the register is absent, is not a reflected method, or the paths disagree.
     */
    fun reflectedMethodOverExploredPaths(address: Int, register: Int): ReflectedMethodMetadata?
    /** [reflectedMethodOverExploredPaths] over the union of executed nodes at [addresses]. */
    fun reflectedMethodOverExploredPaths(addresses: IntArray, register: Int): ReflectedMethodMetadata?
    /**
     * The exact reflected field held by [register] on every executed node at [address], or null when
     * the register is absent, is not a reflected field, or the paths disagree.
     */
    fun reflectedFieldOverExploredPaths(address: Int, register: Int): ReflectedFieldMetadata?
    /** [reflectedFieldOverExploredPaths] over the union of executed nodes at [addresses]. */
    fun reflectedFieldOverExploredPaths(addresses: IntArray, register: Int): ReflectedFieldMetadata?
    /**
     * The length of the tracked array held by [register] on every executed node at [addresses], or null
     * when the register is absent, is not a tracked array, or the paths disagree about the length.
     */
    fun arrayLengthOverExploredPaths(addresses: IntArray, register: Int): Int?
    /**
     * The distinct values [register] holds across [address]'s live nodes — the type-set check-cast peephole
     * needs (spec §9).
     *
     * **What this already tells a consumer about [RegisterOccupancy], so the newer query is not credited
     * with it — and one thing it does NOT tell them, which is why that query exists.** An **empty** set is
     * exactly [RegisterOccupancy.NO_PATH]: no live node, so nothing to read. A set of **size two or more**
     * proves the paths disagreed. But a **singleton `{Top}` is ambiguous**, and it is the whole subject of
     * this task: `ExecutionGraph.valueIn` maps [Frame.peek]'s null onto the same [Value.Top] a frame can
     * genuinely hold, so `{Top}` means "a path holds a real `Top`", OR "no path assigned it", OR any
     * mixture of the two across several nodes, which a `Set` collapses.
     * `AssignmentDistinctionTest.aFrameHeldTopAndAnUnassignedRegisterHaveIDENTICALValuesAndDifferentOccupancy`
     * exhibits three registers whose sets are all `{Top}` and whose occupancies are not all equal.
     */
    fun registerValuesOverExploredPaths(address: Int, register: Int): Set<Value>
    /**
     * The exact immutable String denoted by [register] after [address], or null when the address was not
     * executed, the register is not a known String, or explored paths disagree. This resolves a tracked
     * constructor receiver without exposing its host object or any mutable host state.
     */
    fun knownStringOverExploredPaths(address: Int, register: Int): String?
    /**
     * Whether any path in scope at [address] **holds** a value in [register] — i.e. which of the three
     * facts behind a [Value.Top] this one is. See [RegisterOccupancy], which carries the whole contract
     * and the reason the answer is a total tri-state rather than a nullable.
     *
     * **Scope is [nodePile] restricted to nodes whose op actually ran**, exactly as
     * [consensusOverExploredPaths] restricts it, so the two are always about the same nodes. The pairings a
     * consumer acts on:
     *
     * | consensus | this query | also | means |
     * |---|---|---|---|
     * | [Value.Top] | [RegisterOccupancy.HELD] | — | live and unknowable: a real `Top`, or paths disagreed |
     * | [Value.Top] | [RegisterOccupancy.NEVER_ASSIGNED] | [wasReached] is true | **no path holds a READABLE value here** — read the caveat below before turning this into "dead" |
     * | [Value.Top] | [RegisterOccupancy.NO_PATH] | [wasAttempted] is false | never explored: nothing ran here |
     *
     * **The middle row is NOT a licence to delete the code that would have written the register, nor to
     * reuse it — and the counterexample is a case this query made observable rather than a hypothetical.**
     * Measured on `Lspine/spine;->instanceWideParams(JLjava/lang/String;I)I`: v5 answers
     * [RegisterOccupancy.NEVER_ASSIGNED] with [wasReached] true and a [Value.Top] consensus, i.e. all three
     * antecedents hold — and v5 is the **high half of a live `J` parameter**. [Frame.assign] records it as
     * an internal wide-partner slot and [Frame.peek] answers `null` for it, so "holds no readable value" and
     * "holds nothing" are different facts for exactly two registers: a wide pair's high half, and a register
     * outside the method's range. A strategy asking *"may I reuse this register"* must ask
     * [availableRegisters], which is static and names both halves of every wide pair; a strategy asking
     * *"is this register's VALUE unavailable here"* is answered by the row.
     *
     * That distinction is stated here rather than left to [RegisterOccupancy]'s own KDoc because this table
     * is what a consumer reads before acting, and this file's opening sentence says the actions on the table
     * include deleting a method. `AssignmentDistinctionTest.theHighHalfOfALiveWidePairReadsAsNeverAssignedAndAvailableRegistersIsWhatKnowsBetter`
     * is the pin.
     *
     * **Deliberately not [wasRegisterAssigned], and not an overload of it.** That query is *static*: it is
     * `register in registersAssigned(address)`, the decoded op's DECLARED destination set, reading no node,
     * no path and no [Frame] — so it answers `true` for a register in a provably dead arm, and it has no
     * per-path answer inside it that a terminating twin could reuse.
     *
     * **And `Frame.registersAssigned()` is not the raw material either**, though it looks like the obvious
     * one: [Frame.entryFrame] calls `assigned.clear()` after seeding, deliberately (seeding is a caller's
     * argument, not an assignment *by the method*), so a parameter register holding a perfectly live
     * [Value.Unknown] would report "not assigned" — the commonest live register in any real method.
     * [Frame.peek] returning non-null is the only predicate that answers the question actually asked,
     * because `ExecutionGraph.valueIn` is `peek(register) ?: Value.Top` and is therefore the sole producer
     * of the "never assigned" [Value.Top] this query explains.
     */
    fun registerOccupancyOverExploredPaths(address: Int, register: Int): RegisterOccupancy
    /**
     * [registerOccupancyOverExploredPaths] over **return** paths only — the companion to
     * `terminatingConsensusOverExploredPaths`, which answers [Value.Top] for two unrelated reasons.
     * [RegisterOccupancy.NO_PATH] here means *no path returned normally at all*, so the method has no
     * normal result to be unknowable about. [RegisterOccupancy.HELD] means only that at least one returning
     * path held a value — **it says nothing about the join**, which may be a perfectly good [Value.Known];
     * it is the pairing `Top` + [RegisterOccupancy.HELD] that means "the returning paths had values and
     * nothing survived their join".
     *
     * A register can be [RegisterOccupancy.HELD] at an address and [RegisterOccupancy.NO_PATH] here at the
     * same time — a method whose only path throws holds its parameter right up to the `throw`. That is not
     * a contradiction: "did any path return" is a fact about the RUN, which is why it also has the
     * register-free spelling [didAnyPathReturnOverExploredPaths].
     */
    fun terminatingRegisterOccupancyOverExploredPaths(register: Int): RegisterOccupancy
    /**
     * Whether any explored path returned normally.
     *
     * **Disclosed as strictly DERIVABLE, and kept anyway for legibility rather than necessity.**
     * `terminatingRegisterOccupancyOverExploredPaths(r) == RegisterOccupancy.NO_PATH` holds for *every* `r`
     * exactly when this is `false` — the emptiness of the returning-node set is tested before the register is
     * looked at at all, so even an out-of-range `r` in a method with no registers answers `NO_PATH`. So this
     * is not load-bearing and no soundness argument rests on it.
     *
     * What it buys is that a caller asking "does this method have a normal result" does not have to invent a
     * register number and know that the number is irrelevant. That implicit knowledge is exactly what the old
     * engine's one consumer needed and got right — `opcode/InvokeOp.kt:148` asks
     * `calleeGraph.hasAtLeastOneReturnPath()` as its own register-free query before reading consensus — and
     * this is that query, promoted onto the query surface. It is also the only spelling reachable from
     * [TrustedGraphView] for a caller that has no register in hand.
     *
     * `false` on a run whose [completion] is `canTrustConsensus` means the method **never returns
     * normally** — every path throws or aborts. On a truncated run it may mean only that the run stopped
     * before any path got there, which is why this is the ungated spelling and
     * [TrustedGraphView.didAnyPathReturn] is the one to act on.
     */
    fun didAnyPathReturnOverExploredPaths(): Boolean
    /** See [TrustedGraphView.terminatingHeapObject]. Ungated: the truncated picture, for a debugger or the differential. */
    fun terminatingHeapObjectOverExploredPaths(ref: HeapRef): HeapObject?

    /**
     * The value every **returning** path agrees a static field holds -- the class-state counterpart of
     * [terminatingConsensusOverExploredPaths], and the replacement for the oracle's
     * `ExecutionGraph.getTerminatingFieldConsensus(VirtualField)`.
     *
     * [fieldDescriptor] is the FULLY-QUALIFIED descriptor `ClassIndex.fieldDescriptorOf` builds
     * (`Lcom/example/Foo;->bar:I`), which INCLUDES the declaring class -- never a bare name. The oracle keys
     * this query by a `VirtualField` its own name-only lookup produced, so two same-named statics of different
     * types in one hierarchy answer for one cell (`docs/architecture/type-and-dex.md:116`); here they cannot.
     *
     * [type] is that declaring class, redundant with the descriptor and kept because
     * [ClassState.getStaticField]'s contract has the same pair -- see its KDoc.
     *
     * Total: [Value.Top] when no path returned, and equally when the field's class was never initialized on
     * the returning paths (its cell does not exist, so there is nothing to agree about). Those are different
     * facts and this query does not separate them, exactly as [terminatingConsensusOverExploredPaths] does not
     * separate its own two -- pair it with [didAnyPathReturnOverExploredPaths] if the difference matters. It
     * never throws on ordinary traversal.
     */
    fun terminatingStaticFieldConsensusOverExploredPaths(type: VirtualType, fieldDescriptor: String): Value

    // --- Liveness ---
    fun registersAssigned(address: Int): Set<Int>
    fun registersRead(address: Int): Set<Int>
    fun wasRegisterAssigned(address: Int, register: Int): Boolean
    fun wasRegisterRead(address: Int, register: Int): Boolean
    /**
     * Registers free for scratch use at [address]. Unreflection needs these, and this MUST
     * NOT crash on ordinary non-leaf traversal — the old engine's equivalent did (a
     * confirmed audit defect, spec §9).
     *
     * **Sound for wide values.** The high half of a live `J`/`D` pair is never reported as free,
     * whether the pair came from a parameter or from an op: `Op.destinationRegisters` names both halves
     * of every wide result. This was once false in the op case -- see
     * `ExecutionGraph.availableRegisters` for the demonstrated case and the test that now pins it.
     *
     * **Residual caveat, and it is on [registersRead] rather than on this query:** a wide *source* is
     * reported by its low half only, so [wasRegisterRead] answers `false` for the high half of a pair
     * an op reads. That cannot affect this query -- a wide value reaches a register only via a wide
     * parameter or a wide-result assignment, and both name both halves.
     */
    fun availableRegisters(address: Int): IntArray

    // --- Effects & completion (spec §4.5, §6.4) ---
    /**
     * The effect level recorded at [address] itself. Deliberately NOT behind the trust gate: it is a
     * statement about this one address, not a whole-method claim, and a consumer that turns
     * per-address levels into a method-wide decision must aggregate them over a complete run — which
     * is what [methodSideEffectLevelOverExploredPaths]' gated twin is for.
     *
     * **`NONE` alone does not mean "harmless".** This query is not tri-state: it returns `NONE`
     * identically for an address no path ever attempted and for one that ran a demonstrably
     * effect-free op. A caller that needs those apart must pair it with [wasAttempted] (was a node
     * ever dequeued here?) or [wasReached] (did an op actually run here?) — `NONE` is only evidence
     * of harmlessness for an address one of those confirms. Left as-is rather than widened to a
     * tri-state because the gated aggregate already carries the soundness weight: aggregating over a
     * complete run visits every address, so an unattempted one cannot exist to be misread.
     */
    fun sideEffectLevel(address: Int): SideEffectLevel
    /**
     * True only when every attempted node at [address] ran and produced normal control flow, with no
     * handled, escaping, or aborted exceptional partition. False for an unattempted address and for a
     * node that a limit stopped before execution. Ungated: act on the trusted twin.
     */
    fun didEveryExecutionContinueNormallyOverExploredPaths(address: Int): Boolean
    /**
     * The method's aggregate effect level over the paths this run walked. UNGATED, and the reason the
     * gate exists at all: on a `Partial` run this happily reports `NONE` for a method whose unwalked
     * paths could do anything, which is precisely the answer that would authorize deleting it.
     */
    fun methodSideEffectLevelOverExploredPaths(): SideEffectLevel
    /**
     * Whether exploration completed, aborted, or has not finished yet; gates consensus trust via
     * [CompletionStatus.canTrustConsensus].
     */
    fun completion(): CompletionStatus

    // --- The trust gate (spec §9) ---
    /**
     * The trust-bearing queries, or **null when [completion] is not `canTrustConsensus`** — i.e. on
     * any run that stopped at a bound, hit an unimplemented opcode, suffered a host failure, or has
     * simply **not finished yet** ([CompletionStatus.InProgress], the case that makes this gate
     * unbypassable in time rather than only in kind). Every query on a non-null result is safe to act
     * on.
     *
     * Null rather than a reason-carrying refusal on purpose: [completion] already returns
     * `Partial(reasons)` on the same object, so a consumer that wants to report *why* trust was
     * refused writes `graph.trusted() ?: return explain(graph.completion())`. Duplicating those
     * reasons into this return type would be a second representation of one fact, and this API's job
     * is to make the unsafe path harder to type by accident, not to become a reporting framework.
     */
    fun trusted(): TrustedGraphView?
}
