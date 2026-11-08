package org.cf.smalivm.core

/** Per-class initialization lifecycle (spec §4.2): partial and failed init are representable. */
public enum class ClassInitState { UNINITIALIZED, INITIALIZING, INITIALIZED, FAILED }

/** One path's class state (spec §4.2): lifecycle and statics, implemented by `ClassInit.kt`'s `ClassStore`. */
public interface ClassState {
    /**
     * Where [type] sits in the lifecycle **on this path** (resolved decision 9), and total: a class this
     * path never triggered is [ClassInitState.UNINITIALIZED] rather than an error.
     *
     * Read this rather than [ExecutionState.isClassInitialized] when the difference between "not yet"
     * and "in progress" or "failed" matters — which is most of the time, since only
     * [ClassInitAction.StartInitialization] means a trigger has work to do.
     */
    public fun initState(type: VirtualType): ClassInitState

    /**
     * [fieldDescriptor] is a FULLY-QUALIFIED field descriptor that INCLUDES the defining
     * class, e.g. `Lcom/example/Foo;->bar:I` — required so a shadowed static field never
     * collapses into the same state cell as another class's field of the same name.
     */
    public fun getStaticField(type: VirtualType, fieldDescriptor: String): Value
}

/**
 * One path's registers, heap and class state as a consumer reads them (spec §4.2). Reads are pure:
 * observing a register or an object never mutates state.
 */
public interface ExecutionState : ClassState {
    /** Number of registers in the method this state belongs to. */
    public val registerCount: Int
    /**
     * Null if [register] was never assigned on this path. This read does not itself record
     * liveness: `registersRead` liveness (spec §9) is recorded during op execution, not by
     * consumers calling this accessor.
     */
    public fun peekRegister(register: Int): Value?
    /**
     * [Value.Top] if [register] was never assigned on this path — total, never throws. Like
     * [peekRegister], this read does not record liveness.
     */
    public fun readRegister(register: Int): Value
    /** Registers written in this frame, in ascending order. */
    public fun registersAssigned(): Set<Int>
    /**
     * [fieldDescriptor] is a FULLY-QUALIFIED field descriptor that INCLUDES the defining
     * class, e.g. `Lcom/example/Foo;->bar:I` — never just a bare field name — so that a
     * shadowed field (same name, different defining class) never collapses into one
     * state cell.
     */
    public fun getField(ref: HeapRef, fieldDescriptor: String): Value
    /**
     * Whether [type]'s initialization **completed** on this path: `true` for
     * [ClassInitState.INITIALIZED] and for nothing else. A class whose `<clinit>` is running has not
     * finished writing its statics, and a failed initializer does not read back as initialized (spec §7).
     * A trigger decides whether to initialize through [ClassInitAction], never through this predicate.
     */
    public fun isClassInitialized(type: VirtualType): Boolean

    /**
     * The tracked object behind a [Value.Known] whose raw is a [HeapRef], or null if this path has no
     * such object: a ref that reached this path from a join or from a sibling's allocation denotes
     * nothing here.
     */
    public fun heapObject(ref: HeapRef): HeapObject?
}
