package org.cf.smalivm.core

/**
 * Method-local physical registers (spec §4.2). One per Frame; lookup structurally cannot
 * escape the frame. Wide values occupy two slots; reading the high half of a wide pair as
 * an independent value is a contract violation the implementation must prevent.
 *
 * The result/return/throw/caught-exception slots have a consume/clear lifecycle: produced
 * by one op, moved at most once (move-result / move-exception), then dead.
 */
interface RegisterFile {
    val registerCount: Int
    fun peek(register: Int): Value?
    fun registersAssigned(): Set<Int>
}

/**
 * Instance-field state and object identity (spec §4.2). Reference values the VM tracks
 * get an explicit [HeapRef]; aliasing is "same heap id", never host reference identity.
 * Configured-immutable types use value semantics and are not id-tracked.
 */
interface Heap {
    /**
     * [fieldDescriptor] is a FULLY-QUALIFIED field descriptor that INCLUDES the defining
     * class, e.g. `Lcom/example/Foo;->bar:I` — never just a bare field name — so that a
     * shadowed field (same name, different defining class) never collapses into one
     * state cell.
     */
    fun getField(ref: HeapRef, fieldDescriptor: String): Value
    fun objectType(ref: HeapRef): VirtualType?

    /**
     * The object [ref] denotes **on this path**, or null when this heap has no such object -- which is
     * a real answer, not an error: a `HeapRef` that reached this path from a join, or from a sibling's
     * allocation, denotes nothing here. Callers must treat null as "nothing known", never fabricate.
     */
    fun objectAt(ref: HeapRef): HeapObject?
}

/**
 * Per-class initialization lifecycle (spec §4.2): partial and failed init are representable.
 *
 * An `enum` and not a sealed class, having been reconsidered when it gained its implementor
 * (`ClassInit.kt`, Plan 9 Task 16). A sealed class forces an exhaustive `when` over the four cases,
 * which is the usual argument for one here — but nothing about a state carries data, the two decisions
 * that matter (`actionFor`, and which transitions are legal) are each written **once** rather
 * than as a `when` scattered across consumers, and Kotlin makes an `enum` `when` exhaustive too when the
 * result is used. So the sealed version would buy nothing this design does not already have and would
 * cost the `LEGAL_STEPS` table its map key.
 */
enum class ClassInitState { UNINITIALIZED, INITIALIZING, INITIALIZED, FAILED }

/**
 * The third of the three stores (spec §4.2), implemented by `ClassInit.kt`'s `ClassInitStore` and
 * surfaced on [ExecutionState], which extends it.
 */
interface ClassState {
    /**
     * Where [type] sits in the lifecycle **on this path** (resolved decision 9), and total: a class this
     * path never triggered is [ClassInitState.UNINITIALIZED] rather than an error.
     *
     * Read this rather than [ExecutionState.isClassInitialized] when the difference between "not yet"
     * and "in progress" or "failed" matters — which is most of the time, since only
     * [ClassInitAction.StartInitialization] means a trigger has work to do.
     */
    fun initState(type: VirtualType): ClassInitState

    /**
     * [fieldDescriptor] is a FULLY-QUALIFIED field descriptor that INCLUDES the defining
     * class, e.g. `Lcom/example/Foo;->bar:I` (see [Heap.getField]) — required so a shadowed
     * static field never collapses into the same state cell as another class's field of
     * the same name.
     */
    fun getStaticField(type: VirtualType, fieldDescriptor: String): Value
}

/** How much a computation may have perturbed observable state (spec §4.5). Aggregation errs upward. */
enum class SideEffectLevel { NONE, WEAK, STRONG }

/**
 * The consumer-facing typed facade over the three stores (spec §4.2). Keeps the
 * `ExecutionState` name the 513 tests and the debugger already speak, drops the old
 * god-object identity: no raw string map is ever exposed, and reads are pure —
 * observing a register MUST NOT mutate state (the old clone-on-read did).
 */
interface ExecutionState : ClassState {
    val registers: RegisterFile
    /**
     * Null if [register] was never assigned on this path. This read does not itself record
     * liveness: `registersRead` liveness (spec §9) is recorded during op execution, not by
     * consumers calling this accessor.
     */
    fun peekRegister(register: Int): Value?
    /**
     * [Value.Top] if [register] was never assigned on this path — total, never throws. Like
     * [peekRegister], this read does not record liveness; `registersRead` liveness is
     * recorded during op execution, not on consumer reads.
     */
    fun readRegister(register: Int): Value
    fun registersAssigned(): Set<Int>
    /**
     * [fieldDescriptor] is a FULLY-QUALIFIED field descriptor that INCLUDES the defining
     * class, e.g. `Lcom/example/Foo;->bar:I` (see [Heap.getField]).
     */
    fun getField(ref: HeapRef, fieldDescriptor: String): Value
    /**
     * Whether [type]'s initialization **completed** on this path: `true` for
     * [ClassInitState.INITIALIZED] and for nothing else.
     *
     * Deliberately `false` for [ClassInitState.INITIALIZING] and [ClassInitState.FAILED], and both
     * halves are load-bearing rather than incidental. A class whose `<clinit>` is *running* has not
     * finished writing its statics, so reading it as initialized is the "maybe initialized across paths"
     * conflation `state-model.md:160` records; and *"a failed initializer does not read back as
     * `Initialized`"* is spec §7 verbatim. **A trigger must not consult this predicate to decide whether
     * to initialize** — [ClassInitAction] is that question and retains failed-state cause data. Read
     * [ClassState.initState] when the distinction matters.
     */
    fun isClassInitialized(type: VirtualType): Boolean

    /**
     * The tracked object behind a [Value.Known] whose raw is a [HeapRef], or null if this path has no
     * such object. Pure, like every other read here: observing an object MUST NOT mutate it, which is
     * why [HeapObject]'s mutators are `internal`.
     */
    fun heapObject(ref: HeapRef): HeapObject?
}
