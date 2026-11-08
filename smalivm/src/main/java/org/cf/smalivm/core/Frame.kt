package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import java.util.IdentityHashMap

internal data class CallCompatibilityKey(
    val classInit: ClassInitCompatibilityKey,
    val trackedFields: CompatibilityMapKey<Int, TrackedFieldCompatibility>,
)

/**
 * One method activation's state on one path: physical registers and the four special slots (spec §4.2), over the
 * path's [heap] and [classes].
 *
 * Three invariants this type exists to enforce:
 *  1. **Wide pairs.** A wide value (`J`/`D`) owns two consecutive registers. The high half is not
 *     an independent value: reading it yields [Value.Top], and writing either half invalidates the
 *     pair, so no half can ever be read as a whole.
 *  2. **Slot lifecycle.** `result` and `caught` are produced once, moved once, then dead
 *     ([takeResult]/[takeCaught] clear them).
 *  3. **Persistent isolation.** [copy] shares immutable roots in constant time. A later write replaces
 *     only the writing frame's root or heap object, so parents and siblings remain unchanged.
 *
 * Reads are pure with respect to values; [read] additionally records liveness (which the graph
 * reports as `registersRead`), while [peek] never does -- an observer (debugger, consumer query)
 * must be able to look without perturbing anything. The [ExecutionState] facade (`peekRegister`/
 * `readRegister`) is that observer: neither of its accessors records liveness.
 *
 * Registers and slots hold [FrameValue]s, so a throwable's refinement survives moves within the method; the
 * [Value] accessors project it away.
 */
class Frame private constructor(
    internal val view: MethodView,
    private var slots: PersistentList<RegisterSlot?>,
    private var assigned: PersistentSet<Int>,
    private var read: PersistentSet<Int>,
    private var addressVisits: PersistentMap<Int, Int>,
    private var result: FrameValue?,
    private var returnValue: FrameValue?,
    private var thrown: Value?,
    private var caught: FrameValue?,
    /** This path's heap. Writes through it replace only this frame's roots. */
    internal val heap: HeapStore,
    /** This path's class state. Writes through it replace only this frame's roots. */
    internal val classes: ClassStore,
) : ExecutionState {

    /** A register either holds a value, or is the reserved high half of the wide value below it. */
    private sealed interface RegisterSlot {
        data class Held(val value: FrameValue) : RegisterSlot
        data class WideHigh(val lowRegister: Int) : RegisterSlot
    }

    override val registerCount: Int get() = view.registerCount

    // --- registers ---

    /** Assigns [value] to [register], reserving the following register when the value is wide. */
    fun assign(register: Int, value: Value) = assign(register, FrameValue.plain(value))

    internal fun assign(register: Int, value: FrameValue) {
        seed(register, value)
        assigned = assigned.adding(register)
    }

    /** Places [value] in [register] without recording an assignment by the method. */
    private fun seed(register: Int, value: FrameValue) {
        requireInRange(register)
        val high = if (value.value.type?.isWide == true) register + 1 else null
        require(high == null || high < registerCount) {
            "Wide value at register $register has no high half in ${view.signature}"
        }
        var updated = clearWidePartner(slots, register).replacingAt(register, RegisterSlot.Held(value))
        if (high != null) {
            updated = clearWidePartner(updated, high).replacingAt(high, RegisterSlot.WideHigh(register))
        }
        slots = updated
    }

    /** Null for a register never assigned on this path and for the high half of a wide pair. */
    fun peek(register: Int): Value? {
        requireInRange(register)
        return (slots[register] as? RegisterSlot.Held)?.value?.value
    }

    /** Total read: [Value.Top] for an unassigned register or the high half of a wide pair. */
    fun read(register: Int): Value = readFrameValue(register).value

    /** [read] keeping the register's throwable refinement. */
    internal fun readFrameValue(register: Int): FrameValue {
        requireInRange(register)
        read = read.adding(register)
        return (slots[register] as? RegisterSlot.Held)?.value ?: FrameValue.plain(Value.Top)
    }

    /**
     * Removes throwable refinements at an external-call boundary without changing register liveness or
     * assignment history. The underlying [Value], including a concrete [HeapRef], stays identical.
     */
    internal fun forgetRefinements(registers: Iterable<Int>) {
        for (register in registers) {
            requireInRange(register)
            val held = slots[register] as? RegisterSlot.Held ?: continue
            val plain = held.value.withoutRefinements()
            if (plain !== held.value) slots = slots.replacingAt(register, RegisterSlot.Held(plain))
        }
    }

    override fun registersAssigned(): Set<Int> = assigned.toSortedSet()

    fun registersRead(): Set<Int> = read.toSortedSet()

    internal fun recordAddressVisit(address: Int): Int {
        val visits = (addressVisits[address] ?: 0) + 1
        addressVisits = addressVisits.putting(address, visits)
        return visits
    }

    internal fun addressVisitCount(address: Int): Int = addressVisits[address] ?: 0

    /** Invalidates the wide pair [register] participates in, so no half survives as a whole value. */
    private fun clearWidePartner(root: PersistentList<RegisterSlot?>, register: Int): PersistentList<RegisterSlot?> =
        when (val slot = root[register]) {
            is RegisterSlot.WideHigh -> root.replacingAt(slot.lowRegister, null)
            is RegisterSlot.Held ->
                if (slot.value.value.type?.isWide == true && register + 1 < registerCount) root.replacingAt(register + 1, null)
                else root
            null -> root
        }

    private fun requireInRange(register: Int) =
        require(register in 0 until registerCount) {
            "Register $register out of range [0, $registerCount) in ${view.signature}"
        }

    // --- special slots (produced once, consumed once) ---

    fun setResult(value: Value) = setResult(FrameValue.plain(value))
    internal fun setResult(value: FrameValue) { result = value }
    internal fun takeResult(): FrameValue? = result.also { result = null }
    fun setCaught(value: Value) = setCaught(FrameValue.plain(value))
    internal fun setCaught(value: FrameValue) { caught = value }
    internal fun takeCaught(): FrameValue? = caught.also { caught = null }
    internal fun setReturnValue(value: FrameValue?) { returnValue = value }
    fun returnValueOrNull(): Value? = returnValue?.value
    fun setThrown(value: Value) { thrown = value }

    fun thrownOrNull(): Value? = thrown

    // --- ExecutionState facade ---

    override fun peekRegister(register: Int): Value? = peek(register)
    override fun readRegister(register: Int): Value = peek(register) ?: Value.Top

    /** Reads one fully-qualified instance-field cell from this path's heap. */
    override fun getField(ref: HeapRef, fieldDescriptor: String): Value = heap.getField(ref, fieldDescriptor)

    override fun getStaticField(type: VirtualType, fieldDescriptor: String): Value =
        classes.getStaticField(type, fieldDescriptor)

    override fun initState(type: VirtualType): ClassInitState = classes.initState(type)

    override fun isClassInitialized(type: VirtualType): Boolean =
        classes.initState(type) == ClassInitState.INITIALIZED

    override fun heapObject(ref: HeapRef): HeapObject? = heap.objectAt(ref)

    // --- class initialization ---
    // The scheduler is the only thing that moves a class along the lifecycle. An op asks for a [ClassInitAction]
    // and returns the corresponding arm; it never marks anything itself.

    internal fun classInitAction(type: VirtualType): ClassInitAction = classes.actionFor(type).also { action ->
        if (action is ClassInitAction.ThrowFailed) {
            action.failure.validateReferenceInFrame(this)
        }
    }

    /**
     * Starts a run at this frame's method under [entryState]. A run whose entry is a `<clinit>` finds that class
     * [ClassInitState.INITIALIZING] over its prepared statics, because nothing can initialize a class before its
     * own initializer runs.
     */
    internal fun enterSession(entryState: EntryClassState, planner: ClassInitPlanner) {
        classes.enter(entryState)
        val type = view.definingClass
        if (view.signature.isClassInitializer && classInitAction(type) == ClassInitAction.StartInitialization) {
            val preparation = planner.ownInitializerPreparation(type)
            classes.prepare(preparation.staticSeeds, preparation.finalStatics)
            classes.start(type)
        }
    }

    /**
     * Applies everything [plan] settled without running anything, then marks the class whose guest `<clinit>` is
     * about to run [ClassInitState.INITIALIZING]. The mark lands on the callee's own entry frame, so a `<clinit>`
     * that transitively triggers its own class sees `INITIALIZING` from inside the nested call.
     */
    internal fun applyClassInit(plan: ClassInitPlan) {
        // JVMS §5.4.2: preparation precedes every mark and the initializer, so a `<clinit>` reading a static of
        // its own class sees the prepared value.
        classes.prepare(plan.staticSeeds, plan.finalStatics)
        for (settled in plan.settled) classes.complete(settled)
        plan.context?.activeInitializer?.type?.let(classes::start)
    }

    internal fun completeClassInit(context: ClassInitContext) {
        classes.completeInitializer(context.activeInitializer.type, context.triggerState)
    }

    internal fun failClassInit(context: ClassInitContext, failure: ThrownSubject, hierarchy: ThrowableHierarchy) {
        failure.validateInFrame(this, hierarchy)
        classes.fail(context.activeInitializer.type, failure)
        context.waitingDependents.forEach { classes.failDependency(it, failure) }
    }

    internal fun failClassInitDependency(
        failure: ClassInitDependencyFailure,
        outward: ThrownSubject,
        hierarchy: ThrowableHierarchy,
    ) {
        outward.validateInFrame(this, hierarchy)
        failure.waitingDependents.forEach { classes.failDependency(it, outward) }
    }

    // --- heap values ---
    // Heap cells store Value. Array elements, instance fields, and statics are conservative-forgetting
    // boundaries: a throwable's refinement does not survive a store.

    internal fun allocateArray(type: VirtualType, length: Int): Value.Known =
        Value.Known(heap.allocateArray(type, length), type)

    internal fun allocateArray(type: VirtualType, elements: Array<Value>): Value.Known =
        Value.Known(heap.allocateArray(type, elements), type)

    internal fun allocateArray(type: VirtualType, elements: PersistentList<Value>): Value.Known =
        Value.Known(heap.allocateArray(type, elements), type)

    internal fun allocateThrowable(type: VirtualType, hostThrowable: Throwable, cause: Value? = null): Value.Known =
        Value.Known(heap.allocateThrowable(type, hostThrowable, cause), type)

    /**
     * A fresh, uninitialized tracked object of [type] -- `new-instance`'s allocation. The object's type and the
     * register's tag are the same here, because both come from the instruction's type operand.
     */
    internal fun allocateObject(type: VirtualType, fields: Map<String, Value>): Value.Known =
        Value.Known(heap.allocateObject(type, fields), type)

    internal fun allocateTrackedField(metadata: TrackedFieldValue): Value.Known =
        Value.Known(heap.allocateTrackedField(metadata), VirtualType.of("Ljava/lang/reflect/Field;"))

    internal fun admitReturnedHostValue(raw: Any, type: VirtualType): Value.Known =
        Value.Known(heap.importHostObject(raw, type), type)

    internal fun admitReturnedHostArray(raw: Any, type: VirtualType): Value.Known =
        Value.Known(heap.importReturnedArray(raw), type)

    /** A host value no selected call produced, such as a framework static, as a guest value on this path. */
    internal fun importHostValue(value: HostValue): Value.Known = when (value) {
        is HostValue.Primitive -> Value.Known(value.value, value.type)
        is HostValue.TypedNull -> Value.Known(null, value.type)
        is HostValue.Reference -> {
            val raw = value.value
            requireHostOwned(raw)
            when {
                raw is Throwable -> importHostThrowable(HostThrowable(value.type, raw))
                raw.isDirectHostImmutable() -> Value.Known(raw, value.type)
                raw.javaClass.isArray -> admitReturnedHostArray(raw, value.type)
                else -> admitReturnedHostValue(raw, value.type)
            }
        }
    }

    /**
     * Imports one host throwable graph, cause chain included, preserving identity, aliases, and cycles. Each object
     * keeps the ref [selectedRef] maps it to, then the ref this heap already tracks it at, else gets a new one.
     */
    internal fun importHostThrowable(
        throwable: HostThrowable,
        selectedRef: (Throwable, VirtualType) -> HeapRef? = { _, _ -> null },
    ): Value.Known {
        val imported = IdentityHashMap<Throwable, HeapRef>()

        fun importOne(raw: Throwable, type: VirtualType): Value.Known {
            imported[raw]?.let { return Value.Known(it, type) }
            val ref = selectedRef(raw, type)
                ?: heap.findHostObject(raw)
                ?: heap.hostNodeId(raw)?.let { heap.importHostNode(it, type, raw) }
                ?: allocateThrowable(type, raw).raw as HeapRef
            imported[raw] = ref
            val cause = raw.cause?.let { importOne(it, virtualTypeOfHostClass(it.javaClass)) }
                ?: Value.Known(null, GuestThrowables.THROWABLE)
            heap.setThrowableCause(ref, cause)
            return Value.Known(ref, type)
        }

        return importOne(throwable.throwable, virtualTypeOfHostClass(throwable.throwable.javaClass))
    }

    internal fun throwableCause(ref: HeapRef): Value =
        (heap.objectAt(ref) as? HeapObject.GuestThrowable)?.cause
            ?: Value.Unknown(VirtualType.of("Ljava/lang/Throwable;"))

    internal fun reachableTrackedFields(values: Iterable<Value>): List<HeapRef> = heap.reachableTrackedFields(
        values.mapNotNull { (it as? Value.Known)?.raw as? HeapRef },
    )

    /** Whether [value] is a ref to an object this path's heap tracks. */
    internal fun isTrackedReference(value: Value): Boolean =
        ((value as? Value.Known)?.raw as? HeapRef)?.let(heap::objectAt) != null

    /** Whether [value] has an exact Java object: a host value, or guest arrays built only from such values. */
    internal fun isHostMaterializable(value: Value): Boolean = isHostMaterializable(value, mutableSetOf())

    private fun isHostMaterializable(value: Value, visiting: MutableSet<Int>): Boolean {
        if (value !is Value.Known) return false
        val raw = value.raw
        if (raw !is HeapRef) return raw == null || raw.isDirectHostImmutable()
        if (!visiting.add(raw.id)) return true
        val materializable = when (val obj = heapObject(raw)) {
            is HeapObject.TrackedArray ->
                if (obj.contents == null) obj.elements().all { isHostMaterializable(it, visiting) }
                else heap.hostObjectAt(raw) != null
            is HeapObject.GuestThrowable ->
                heap.hostObjectAt(raw) != null && obj.cause?.let { isHostMaterializable(it, visiting) } == true
            is HeapObject.HostObject -> heap.hostObjectAt(raw) != null
            is HeapObject.TrackedField, is HeapObject.TrackedObject, null -> false
        }
        visiting.remove(raw.id)
        return materializable
    }

    internal fun detachHostGraph(extraRoots: List<Value>): HostGraphCheckpoint =
        heap.detachHostGraph(guestRootValues() + extraRoots)

    internal fun finalizeHostGraph(checkpoint: HostGraphCheckpoint): HostGraphComparison =
        heap.finalizeHostGraph(checkpoint, guestRootValues())

    internal fun guestRootValues(): List<Value> = buildList {
        slots.forEach { slot -> (slot as? RegisterSlot.Held)?.value?.value?.let(::add) }
        result?.value?.let(::add)
        returnValue?.value?.let(::add)
        thrown?.let(::add)
        caught?.value?.let(::add)
        addAll(classes.guestRootValues())
    }

    internal fun callCompatibilityKey(): CallCompatibilityKey = CallCompatibilityKey(
        classes.compatibilityKey(),
        heap.trackedFieldCompatibilityKey(),
    )

    // --- forks and call boundaries ---

    /** This frame's registers, slots and history over [heap] and [classes]. */
    private fun derive(heap: HeapStore, classes: ClassStore): Frame = Frame(
        view = view,
        slots = slots,
        assigned = assigned,
        read = read,
        addressVisits = addressVisits,
        result = result,
        returnValue = returnValue,
        thrown = thrown,
        caught = caught,
        heap = heap,
        classes = classes,
    )

    /** A constant-time snapshot over the same immutable roots. Later writes replace only one frame's roots. */
    fun copy(): Frame = derive(heap.copy(), classes.copy())

    /** Copies a caller-supplied public entry frame and removes all internal throwable refinements. */
    internal fun sanitizedCopyForPublicEntry(): Frame = copy().also { sanitized ->
        sanitized.forgetRefinements(0 until registerCount)
        sanitized.result = result?.withoutRefinements()
        sanitized.returnValue = returnValue?.withoutRefinements()
        sanitized.caught = caught?.withoutRefinements()
    }

    internal fun belongsTo(expectedView: MethodView): Boolean = view.hasSameExecutionIdentity(expectedView)

    /** Resolves dynamic register partners and array ranges once for this execution. */
    internal fun resolveFootprint(footprint: OpUpdateFootprint): ReconciliationFootprint {
        var registers = persistentSetOf<Int>()

        fun includeCellAndOldPartner(register: Int) {
            requireInRange(register)
            registers = registers.adding(register)
            when (val slot = slots[register]) {
                is RegisterSlot.WideHigh -> registers = registers.adding(slot.lowRegister)
                is RegisterSlot.Held -> if (slot.value.value.type?.isWide == true && register + 1 < registerCount) {
                    registers = registers.adding(register + 1)
                }
                null -> Unit
            }
        }

        for (write in footprint.registerWrites) {
            includeCellAndOldPartner(write.register)
            if (write.isWide) includeCellAndOldPartner(write.register + 1)
        }
        return ReconciliationFootprint(
            registers,
            footprint.arrayRanges,
            footprint.objectFields,
            footprint.staticDescriptors,
        )
    }

    /**
     * Joins [arms], each started from this frame, over only the cells [footprint] names. Every other cell is
     * unchanged on every arm, so it stays shared and is not visited.
     */
    internal fun reconcileOpArms(
        arms: List<Frame>,
        footprint: ReconciliationFootprint,
        superclasses: TypeHierarchy,
    ): Frame {
        require(arms.size > 1)
        val reconciledClasses = classes.copy()
        reconciledClasses.reconcileStatics(footprint.staticDescriptors.sorted(), arms.map { it.classes }, superclasses)
        val reconciled = derive(
            heap.reconciled(arms.map { it.heap }, footprint.arrayRanges, footprint.objectFields, superclasses),
            reconciledClasses,
        )
        for (register in footprint.registerCells.sorted()) {
            val armSlots = arms.map { it.slots[register] }
            val wideLows = armSlots.filterIsInstance<RegisterSlot.WideHigh>().map(RegisterSlot.WideHigh::lowRegister)
            when {
                armSlots.all { it == null } -> reconciled.clearRegisterCell(register)
                wideLows.size == arms.size && wideLows.distinct().size == 1 -> Unit
                armSlots.all { it is RegisterSlot.Held } -> reconciled.assign(
                    register,
                    requireNotNull(joinAllFrameValues(armSlots.map { (it as RegisterSlot.Held).value }, superclasses)),
                )
                else -> reconciled.assign(register, Value.Top)
            }
        }
        return reconciled
    }

    private fun clearRegisterCell(register: Int) {
        slots = clearWidePartner(slots, register).replacingAt(register, null)
    }

    /**
     * A fresh entry frame for [callee] on this path. It shares this frame's heap ids, so the callee's refs and
     * the caller's never collide. Registers, slots and liveness are new; the heap and class state are carried in.
     * With [mergedCalleeState] these are the only places state crosses a call boundary, so a field added to
     * `Frame` and to neither store is dropped at both.
     */
    internal fun calleeEntryFrame(callee: MethodView): Frame = entryFrame(callee, heap.copy(), classes.copy())

    /**
     * The frame a caller resumes on when a call returns: this frame's registers over the heap and class state of
     * [callees], one outcome partition of callee paths, joined. Registers are method-local, so a callee cannot
     * change which object a caller register denotes; dropping its heap would lose its allocations and writes, and
     * dropping its class state would re-run every `<clinit>` it ran.
     */
    internal fun mergedCalleeState(callees: List<Frame>, superclasses: TypeHierarchy): Frame {
        require(callees.isNotEmpty())
        return derive(
            heap.merged(callees.map { it.heap }, superclasses),
            ClassStore.merge(callees.map { it.classes }, superclasses),
        )
    }

    internal fun hostGraphVersionToken(): Any = heap.hostGraphVersionToken()

    override fun toString(): String =
        "Frame(${view.signature}, assigned=${registersAssigned()})"

    companion object {
        /**
         * A run's own entry frame: a new heap id space and empty class state. A callee frame comes from
         * [calleeEntryFrame], which shares its caller's.
         */
        fun entryFrame(view: MethodView): Frame = entryFrame(view, HeapStore(HeapIds()), ClassStore())

        /** Default session input; a constructor's receiver is a fresh object when its field layout is known. */
        internal fun entryFrame(view: MethodView, resolver: ProgramSnapshotResolver): Frame {
            val frame = entryFrame(view)
            if (!view.isStatic && view.signature.isConstructor) {
                // An unresolved superclass leaves the receiver unknown, rather than inventing its fields.
                resolver.instanceFieldDefaults(view.definingClass)?.let { fields ->
                    frame.seed(view.firstParameterRegister, FrameValue.plain(frame.allocateObject(view.definingClass, fields)))
                }
            }
            return frame
        }

        /**
         * The state a method starts in: locals unassigned, parameters seeded [Value.Unknown] of their declared
         * type (a caller's argument is not known to the method being analyzed in isolation). Seeding is not an
         * assignment by the method, so it does not appear in [registersAssigned].
         *
         * [classes] starts empty for a run's own entry frame: no class is triggered yet, and [enterSession] sets
         * what an untriggered class reads as. The first trigger inside the method handles the entry method's own
         * class, as one inside a callee would.
         */
        private fun entryFrame(view: MethodView, heap: HeapStore, classes: ClassStore): Frame {
            val frame = Frame(
                view = view,
                slots = List<RegisterSlot?>(view.registerCount) { null }.toPersistentList(),
                assigned = persistentSetOf(),
                read = persistentSetOf(),
                addressVisits = persistentMapOf(),
                result = null,
                returnValue = null,
                thrown = null,
                caught = null,
                heap = heap,
                classes = classes,
            )
            for (parameter in view.parameters) {
                frame.seed(parameter.register, FrameValue.plain(Value.Unknown(parameter.type)))
            }
            return frame
        }
    }
}

/** [this] as a plain value: the same [Value], without a throwable refinement. */
internal fun FrameValue.withoutRefinements(): FrameValue =
    if (this is FrameValue.RefinedThrowable) FrameValue.plain(value) else this
