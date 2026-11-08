package org.cf.smalivm.core

import java.util.IdentityHashMap
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList

internal data class CallCompatibilityKey(
    val classInit: ClassInitCompatibilityKey,
    val trackedFields: CompatibilityMapKey<Int, TrackedFieldCompatibility>,
)

/**
 * One method activation's mutable state: physical registers plus the four special slots
 * (spec §4.2).
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
 * `readRegister`) is that observer: per its own contract (`StateContracts.kt`) neither of its
 * accessors records liveness -- liveness is recorded only when an op drives this Frame directly
 * through [read], never when a consumer reads through the facade.
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
    private val heap: HeapStore,
    /**
     * This path's class-initialization lifecycle, a store separate from [heap] (spec §4.2 names three
     * stores). It must be threaded across both call boundaries, [calleeEntryFrame] and [resumedWith];
     * the private constructor makes the compiler require it at each.
     */
    private val classes: ClassInitStore,
) : ExecutionState {

    /** A register either holds a value, or is the reserved high half of the wide value below it. */
    private sealed interface RegisterSlot {
        data class Held(val value: FrameValue) : RegisterSlot
        data class WideHigh(val lowRegister: Int) : RegisterSlot
    }

    override val registerCount: Int get() = view.registerCount

    // --- registers ---

    /** Assigns [value] to [register], reserving the following register when the value is wide. */
    fun assign(register: Int, value: Value) = assignFrameValue(register, FrameValue.plain(value))

    /** Assigns one atomic internal value. Public writes deliberately create [FrameValue.Plain]. */
    internal fun assignFrameValue(register: Int, value: FrameValue) {
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
        assigned = assigned.adding(register)
    }

    /** Null for a register never assigned on this path and for the high half of a wide pair. */
    fun peek(register: Int): Value? {
        return peekFrameValue(register)?.value
    }

    /** Internal non-liveness read that preserves throwable refinement. */
    internal fun peekFrameValue(register: Int): FrameValue? {
        requireInRange(register)
        return (slots[register] as? RegisterSlot.Held)?.value
    }

    /** Total read: [Value.Top] for an unassigned register or the high half of a wide pair. */
    fun read(register: Int): Value {
        return readFrameValue(register).value
    }

    /** Internal total read that preserves throwable refinement. */
    internal fun readFrameValue(register: Int): FrameValue {
        requireInRange(register)
        read = read.adding(register)
        return peekFrameValue(register) ?: FrameValue.plain(Value.Top)
    }

    /**
     * Removes throwable refinements at an external-call boundary without changing register liveness or
     * assignment history. The underlying [Value], including a concrete [HeapRef], stays identical.
     */
    internal fun forgetRefinements(registers: Iterable<Int>) {
        for (register in registers) {
            requireInRange(register)
            val held = slots[register] as? RegisterSlot.Held ?: continue
            if (held.value !is FrameValue.RefinedThrowable) continue
            slots = slots.replacingAt(register, RegisterSlot.Held(FrameValue.plain(held.value.value)))
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

    fun setResult(value: Value) { result = FrameValue.plain(value) }
    internal fun setResult(value: FrameValue) { result = value }
    fun takeResult(): Value? = takeResultFrameValue()?.value
    internal fun takeResultFrameValue(): FrameValue? = result.also { result = null }
    fun setCaught(value: Value) { caught = FrameValue.plain(value) }
    internal fun setCaught(subject: ThrownSubject) { caught = subject.frameValue }
    internal fun setCaught(value: FrameValue.RefinedThrowable) { caught = value }
    fun takeCaught(): Value? = takeCaughtFrameValue()?.value
    internal fun takeCaughtFrameValue(): FrameValue? = caught.also { caught = null }
    fun setReturnValue(value: Value?) { returnValue = value?.let(FrameValue::plain) }
    internal fun setReturnFrameValue(value: FrameValue?) { returnValue = value }
    fun returnValueOrNull(): Value? = returnValue?.value
    fun setThrown(value: Value) { thrown = value }

    fun thrownOrNull(): Value? = thrown

    // --- ExecutionState facade ---
    // peekRegister/readRegister are conservative public projections: they expose the exact Value and
    // HeapRef identity but intentionally omit internal throwable refinements. They are also the read-only,
    // consumer-facing accessors (debugger, Task 6
    // ExecutionGraph queries): per StateContracts.kt's contract on ExecutionState, NEITHER records
    // liveness. That means readRegister must NOT delegate to read() -- only an op driving this
    // Frame directly through read() may record a register as live.

    override fun peekRegister(register: Int): Value? = peek(register)
    override fun readRegister(register: Int): Value = peek(register) ?: Value.Top

    /** Reads one fully-qualified instance-field cell from this path's heap. */
    override fun getField(ref: HeapRef, fieldDescriptor: String): Value = heap.getField(ref, fieldDescriptor)

    // Heap cells store Value by design. Array elements, instance fields, and statics are explicit
    // conservative-forgetting boundaries; no parallel refinement map is attached to those stores.
    internal fun writeField(ref: HeapRef, fieldDescriptor: String, value: Value): Boolean =
        heap.setField(ref, fieldDescriptor, value)

    internal fun writeArrayElement(ref: HeapRef, index: Int, value: Value): Boolean =
        heap.setArrayElement(ref, index, value)

    internal fun writeArrayElements(ref: HeapRef, startIndex: Int, values: List<Value>): Boolean =
        heap.setArrayElements(ref, startIndex, values)

    /** This path's static storage; see [ClassInitStore.getStaticField], which is the one place it lands. */
    override fun getStaticField(type: VirtualType, fieldDescriptor: String): Value =
        classes.getStaticField(type, fieldDescriptor)

    internal fun staticCell(fieldDescriptor: String): StaticCell? = classes.staticCell(fieldDescriptor)

    override fun initState(type: VirtualType): ClassInitState = classes.initState(type)

    override fun isClassInitialized(type: VirtualType): Boolean =
        classes.initState(type) == ClassInitState.INITIALIZED

    internal fun classInitAction(type: VirtualType): ClassInitAction = classes.actionFor(type).also { action ->
        if (action is ClassInitAction.ThrowFailed) {
            action.failure.validateReferenceInFrame(this)
        }
    }

    // --- class init (spec §4.2's third store) ---
    // Mutators are `internal` for the same reason [HeapObject]'s are: a read must not perturb what it
    // observes, and the SCHEDULER is the only thing allowed to move a class along the lifecycle. An op
    // asks for a [ClassInitAction] and returns the corresponding arm; it never marks anything itself.

    /**
     * Applies everything [plan] settled without running anything, then marks the class whose guest
     * `<clinit>` is about to run [ClassInitState.INITIALIZING].
     *
     * The `INITIALIZING` mark lands on the **callee's own entry frame**, which is what makes the re-entry
     * guard work at all: a `<clinit>` that transitively triggers its own class has to *see* `INITIALIZING`
     * from inside the nested call, and [calleeEntryFrame] is the only thing that carries state in there.
     */
    internal fun applyClassInit(plan: ClassInitPlan) {
        // PREPARATION FIRST (JVMS §5.4.2): every static of every class this plan moves gets its
        // pre-`<clinit>` value before any mark is applied and before the initializer runs, so a `<clinit>`
        // reading a static of its own class sees the prepared value rather than nothing. See
        // [ClassInitStore.prepare].
        classes.prepare(plan.staticSeeds)
        for (settled in plan.settled) classes.complete(settled)
        plan.context?.activeInitializer?.type?.let(classes::start)
    }

    internal fun completeClassInit(context: ClassInitContext) {
        classes.complete(context.activeInitializer.type)
    }

    internal fun startClassInit(type: VirtualType) = classes.start(type)

    internal fun completeClassInit(type: VirtualType) = classes.complete(type)

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

    /**
     * Writes a static field on this path -- `sput*`'s whole effect, reached only through the op (and through
     * the test harness's field seeding, which is why this is `internal` rather than private).
     *
     * `internal` for [HeapObject]'s reason: a read must not be able to perturb what it observes, so the write
     * side is not on the [ExecutionState] facade at all. [fieldDescriptor] is the FULLY-QUALIFIED descriptor
     * `ClassIndex.fieldDescriptorOf` builds; passing anything else stores into a cell no read will find.
     */
    internal fun writeStaticField(fieldDescriptor: String, value: Value) =
        classes.setStaticField(fieldDescriptor, value)

    internal fun writeHostProjection(fieldDescriptor: String, value: Value) =
        classes.setHostProjection(fieldDescriptor, value)

    // --- heap (spec §4.2) ---
    // These are the ONLY way an op reaches the heap. The store stays private so every write replaces the
    // current frame's persistent root instead of trying to mutate an object returned by a read.

    internal fun allocateArray(type: VirtualType, length: Int): Value.Known =
        Value.Known(heap.allocateArray(type, length), type)

    internal fun allocateArray(type: VirtualType, elements: Array<Value>): Value.Known =
        Value.Known(heap.allocateArray(type, elements), type)

    internal fun allocateArray(type: VirtualType, elements: PersistentList<Value>): Value.Known =
        Value.Known(heap.allocateArray(type, elements), type)

    internal fun allocateThrowable(type: VirtualType, hostThrowable: Throwable, cause: Value? = null): Value.Known =
        Value.Known(heap.allocateThrowable(type, hostThrowable, cause), type)

    internal fun throwableCause(ref: HeapRef): Value =
        (heap.objectAt(ref) as? HeapObject.GuestThrowable)?.cause
            ?: Value.Unknown(VirtualType.of("Ljava/lang/Throwable;"))

    internal fun setThrowableCause(ref: HeapRef, cause: Value) = heap.setThrowableCause(ref, cause)

    internal fun allocateHostObject(type: VirtualType, raw: Any): HeapRef = heap.allocateHostObject(type, raw)

    internal fun findHostObject(raw: Any): HeapRef? = heap.findHostObject(raw)

    internal fun admitHostSeeds(seeds: List<HostSeed>): List<Value> = heap.admitHostSeeds(seeds)

    internal fun admitReturnedHostValue(raw: Any, type: VirtualType): Value.Known =
        Value.Known(heap.importHostObject(raw, type), type)

    internal fun admitReturnedHostArray(raw: Any, type: VirtualType): Value.Known =
        Value.Known(heap.importReturnedArray(raw), type)

    internal fun admitReturnedHostValues(raws: List<Any>): IdentityHashMap<Any, HeapRef> =
        heap.importHostObjects(raws)

    internal fun hostObjectAt(ref: HeapRef): Any? = heap.hostObjectAt(ref)

    internal fun hostObjectAt(nodeId: HostGraphNodeId): Any? = heap.hostObjectAt(nodeId)

    internal fun hostNodeId(raw: Any): HostGraphNodeId? = heap.hostNodeId(raw)

    internal fun hostGraphObjects(): Map<Int, Any> = heap.hostGraphObjects()

    internal fun refForHostNode(nodeId: HostGraphNodeId): HeapRef? = heap.refForNode(nodeId)

    internal fun mirroredArrays(): List<HostArrayMirror> = heap.mirroredArrays()

    internal fun isHostMaterializable(value: Value): Boolean = isHostMaterializable(value, mutableSetOf())

    internal fun isTrackedReference(value: Value): Boolean {
        val ref = (value as? Value.Known)?.raw as? HeapRef ?: return false
        return heapObject(ref) != null
    }

    internal fun trackedFieldAt(ref: HeapRef): HeapObject.TrackedField? = heap.trackedFieldAt(ref)

    internal fun setTrackedFieldAccessibility(ref: HeapRef, accessibility: FieldAccessibility): Boolean =
        heap.setTrackedFieldAccessibility(ref, accessibility)

    private fun isHostMaterializable(value: Value, visiting: MutableSet<Int>): Boolean {
        if (value !is Value.Known) return false
        val raw = value.raw
        if (raw !is HeapRef) return raw == null || raw.isDirectHostImmutable()
        if (!visiting.add(raw.id)) return true
        val materializable = when (val obj = heapObject(raw)) {
            is HeapObject.HostObject -> obj.contents is HostGraphContents.Known && hostObjectAt(raw) != null
            is HeapObject.GuestThrowable ->
                obj.contents is HostGraphContents.Known &&
                    hostObjectAt(raw) != null &&
                    obj.cause?.let { isHostMaterializable(it, visiting) } == true
            is HeapObject.TrackedField, is HeapObject.TrackedObject, null -> false
            is HeapObject.TrackedArray -> when (obj.mirror) {
                is HostGraphContents.Known -> hostObjectAt(raw) != null
                HostGraphContents.Unknown -> false
                null -> obj.elements().all { isHostMaterializable(it, visiting) }
            }
        }
        visiting.remove(raw.id)
        return materializable
    }

    internal fun importHostNode(nodeId: HostGraphNodeId, type: VirtualType, detachedRaw: Any): HeapRef =
        heap.importHostNode(nodeId, type, detachedRaw)

    internal fun setArrayMirror(ref: HeapRef, raw: Any): HostGraphNodeId = heap.setArrayMirror(ref, raw)

    internal fun heapTransactionCheckpoint(): HeapTransactionCheckpoint = heap.transactionCheckpoint()

    internal fun verifyHostGraphCheckpoint(checkpoint: HostGraphCheckpoint) =
        heap.verifyHostGraphCheckpoint(checkpoint)

    internal fun applyArrayReplacementsAtomically(replacements: List<ArrayReplacement>) =
        heap.applyArrayReplacementsAtomically(replacements)

    internal fun rollbackFailedCopyback(checkpoint: HeapTransactionCheckpoint, affectedArrays: Set<HeapRef>) =
        heap.rollbackFailedCopyback(checkpoint, affectedArrays)

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

    internal fun restoreHostGraph(checkpoint: HostGraphCheckpoint) = heap.restoreHostGraph(checkpoint)

    /**
     * A fresh, uninitialized tracked object of [type] -- `new-instance`'s allocation, mirroring
     * [allocateArray]: the object's type and the register's tag are the same thing here, because both
     * come from the instruction's type operand, which cannot lie.
     */
    internal fun allocateObject(type: VirtualType, fields: Map<String, Value>): Value.Known =
        Value.Known(heap.allocateObject(type, fields), type)

    internal fun allocateTrackedField(metadata: TrackedFieldValue): Value.Known =
        Value.Known(heap.allocateTrackedField(metadata), VirtualType.of("Ljava/lang/reflect/Field;"))

    internal fun arrayAt(ref: HeapRef): HeapObject.TrackedArray? = heap.arrayAt(ref)

    internal fun collapseReachable(values: List<Value>, authoritativeTypes: List<VirtualType>) =
        heap.collapseReachable(values, authoritativeTypes)

    internal fun reachableTrackedFields(values: Iterable<Value>): List<HeapRef> = heap.reachableTrackedFields(
        values.mapNotNull { (it as? Value.Known)?.raw as? HeapRef },
    )

    internal fun markInitialized(ref: HeapRef): Boolean = heap.markInitialized(ref)

    internal fun installConstructedHostObject(ref: HeapRef, raw: Any): Boolean =
        heap.installConstructedHostObject(ref, raw)

    internal fun classInitCompatibilityKey(): ClassInitCompatibilityKey = classes.compatibilityKey()

    internal fun callCompatibilityKey(): CallCompatibilityKey = CallCompatibilityKey(
        classes.compatibilityKey(),
        heap.trackedFieldCompatibilityKey(),
    )

    internal fun classInitStatusSnapshot(): ClassInitStatusSnapshot = classes.statusSnapshot()

    override fun heapObject(ref: HeapRef): HeapObject? = heap.objectAt(ref)

    /** A constant-time snapshot over the same immutable roots. Later writes replace only one frame's roots. */
    fun copy(): Frame = Frame(
        view = view,
        slots = slots,
        assigned = assigned,
        read = read,
        addressVisits = addressVisits,
        result = result,
        returnValue = returnValue,
        thrown = thrown,
        caught = caught,
        heap = heap.copy(),
        classes = classes.copy(),
    )

    /** Copies a caller-supplied public entry frame and removes all internal throwable refinements. */
    internal fun sanitizedCopyForPublicEntry(): Frame {
        var sanitizedSlots = slots
        for (index in slots.indices) {
            val held = slots[index] as? RegisterSlot.Held ?: continue
            if (held.value !is FrameValue.RefinedThrowable) continue
            sanitizedSlots = sanitizedSlots.replacingAt(index, RegisterSlot.Held(FrameValue.plain(held.value.value)))
        }
        return Frame(
            view = view,
            slots = sanitizedSlots,
            assigned = assigned,
            read = read,
            addressVisits = addressVisits,
            result = result?.value?.let(FrameValue::plain),
            returnValue = returnValue?.value?.let(FrameValue::plain),
            thrown = thrown,
            caught = caught?.value?.let(FrameValue::plain),
            heap = heap.copy(),
            classes = classes.copy(),
        )
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
     * Reconciles only state entries named by op updates. Unchanged roots stay shared and are not visited.
     */
    internal fun reconcileOpArms(
        arms: List<Frame>,
        footprint: ReconciliationFootprint,
        superclasses: TypeHierarchy,
    ): Frame {
        require(arms.size > 1)
        val staticDescriptors = footprint.staticDescriptors.sorted()
        val reconciledClasses = classes.copy()
        reconciledClasses.reconcileStatics(staticDescriptors, arms.map { it.classes }, superclasses)
        val reconciled = Frame(
            view = view,
            slots = slots,
            assigned = assigned,
            read = read,
            addressVisits = addressVisits,
            result = result,
            returnValue = returnValue,
            thrown = thrown,
            caught = caught,
            heap = heap.copy(),
            classes = reconciledClasses,
        )
        for ((ref, ranges) in footprint.arrayRanges.entries.sortedBy { it.key.id }) {
            for (range in ranges) {
                for (index in range) {
                    var value: Value? = null
                    for (arm in arms) {
                        val armValue = arm.arrayAt(ref)?.element(index) ?: Value.Top
                        value = value?.let { join(it, armValue, superclasses) } ?: armValue
                    }
                    check(reconciled.writeArrayElement(ref, index, value ?: Value.Top)) {
                        "Op arm reconciliation lost tracked array $ref"
                    }
                }
            }
        }
        for ((ref, descriptors) in footprint.objectFields.entries.sortedBy { it.key.id }) {
            for (descriptor in descriptors.sorted()) {
                var value: Value? = null
                for (arm in arms) {
                    val armValue = arm.heap.objectAt(ref).let { obj ->
                        (obj as? HeapObject.TrackedObject)?.field(descriptor) ?: Value.Top
                    }
                    value = value?.let { join(it, armValue, superclasses) } ?: armValue
                }
                check(reconciled.writeField(ref, descriptor, value ?: Value.Top)) {
                    "Op arm reconciliation lost tracked field $descriptor at $ref"
                }
            }
        }
        for (register in footprint.registerCells.sorted()) {
            var allEmpty = true
            var allHeld = true
            var allWideHigh = true
            var wideLow: Int? = null
            var sameWideLow = true
            var heldValue: FrameValue? = null
            for (arm in arms) {
                when (val slot = arm.slots[register]) {
                    is RegisterSlot.Held -> {
                        allEmpty = false
                        allWideHigh = false
                        heldValue = heldValue?.let { joinFrameValues(it, slot.value, superclasses) } ?: slot.value
                    }
                    is RegisterSlot.WideHigh -> {
                        allEmpty = false
                        allHeld = false
                        if (wideLow == null) wideLow = slot.lowRegister
                        else if (wideLow != slot.lowRegister) sameWideLow = false
                    }
                    null -> {
                        allHeld = false
                        allWideHigh = false
                    }
                }
            }
            when {
                allEmpty -> reconciled.clearRegisterCell(register)
                allWideHigh && sameWideLow -> Unit
                allHeld -> reconciled.assignFrameValue(register, heldValue ?: FrameValue.plain(Value.Top))
                else -> reconciled.assign(register, Value.Top)
            }
        }
        return reconciled
    }

    private fun clearRegisterCell(register: Int) {
        slots = clearWidePartner(slots, register).replacingAt(register, null)
    }

    /**
     * A fresh entry frame for [callee], **sharing this frame's heap id space** -- the one way a callee frame
     * may be built (`CARRY-FORWARD.md` Plan 9 item 10).
     *
     * This exists so the rule is enforced rather than documented. [HeapStore] is private to this class
     * precisely so no op or scheduler can hold one across a fork, which also means the [Invoker] cannot
     * reach `entryFrame(view, heap)` itself -- it has to come through here, and here there is no way to pass
     * a fresh [HeapIds]. Building a callee frame through the PUBLIC [Companion.entryFrame] instead mints a
     * new id space starting at 0, and because [HeapRef] is a structural data class the caller's `HeapRef(0)`
     * and the callee's would compare EQUAL: heap consensus silently treats two distinct objects as one.
     *
     * The object-map root is shared because it and every heap object are immutable versions. The callee sees
     * the caller's objects and shares its [HeapIds], while a later write replaces only the callee's map path.
     *
     * ⚠️ **THIS AND [resumedWith] ARE THE ONLY TWO PLACES STATE CROSSES A CALL BOUNDARY.** This is a
     * **fresh** [entryFrame]: registers, slots and liveness are new, and the only things carried in are the
     * [HeapStore] and the [ClassInitStore]. [resumedWith] is the mirror -- it copies the caller's frame and
     * takes both stores from the callee. So a field added to `Frame` and nowhere else is **silently dropped
     * entering a callee and silently dropped again on return**, which is a wrong answer rather than a loud
     * failure. Add it to both, or put it inside a store that is already threaded.
     *
     * Task 16 threaded the [ClassInitStore] explicitly here rather than hiding it in the [HeapStore], which
     * this KDoc used to recommend as the cheaper option. Spec §4.2 names **three** stores and [ClassState] is
     * one of them, so a class-init map inside the object heap would leave one of the three with no type of its
     * own; the explicit threading costs the two lines below and above, and the private constructor makes the
     * compiler demand them. What the old advice was right about is the requirement: per-path is not enough on
     * its own, because a `<clinit>` whose class is already `Initializing` must still SEE `Initializing` from
     * inside the nested call, or the re-entry guard design decision 2 exists to provide is lost and a
     * `<clinit>` recurses until [Limits.maxCallDepth] truncates the run.
     */
    internal fun calleeEntryFrame(callee: MethodView): Frame = entryFrame(callee, heap.copy(), classes.copy())

    /**
     * The frame a caller resumes on when a call returns: this frame's registers with [callee]'s heap and
     * class-init store.
     *
     * Registers are method-local, so a callee cannot change which object a caller register denotes. The heap
     * and class-init state are per path: dropping the callee's heap would lose its allocations and field
     * writes, and dropping its class-init store would re-run every `<clinit>` the callee already ran.
     *
     * Together with [calleeEntryFrame] these are the only two places state crosses a call boundary.
     */
    internal fun resumedWith(callee: Frame): Frame = Frame(
        view = view,
        slots = slots,
        assigned = assigned,
        read = read,
        addressVisits = addressVisits,
        result = result,
        returnValue = returnValue,
        thrown = thrown,
        caught = caught,
        heap = callee.heap.copy(),
        classes = callee.classes.copy(),
    )

    /** Caller registers plus stores joined over exactly one callee outcome partition. */
    internal fun mergedCalleeState(callees: List<Frame>, superclasses: TypeHierarchy): Frame {
        require(callees.isNotEmpty())
        return Frame(
            view = view,
            slots = slots,
            assigned = assigned,
            read = read,
            addressVisits = addressVisits,
            result = result,
            returnValue = returnValue,
            thrown = thrown,
            caught = caught,
            heap = HeapStore.merge(callees.map { it.heap }, superclasses),
            classes = ClassInitStore.merge(callees.map { it.classes }, superclasses),
        )
    }

    internal fun hostGraphVersionToken(): Any = heap.hostGraphVersionToken()

    override fun toString(): String =
        "Frame(${view.signature}, assigned=${registersAssigned()})"

    companion object {
        /**
         * @see entryFrame
         *
         * Mints a FRESH [HeapIds], i.e. a NEW id space starting at 0. Correct for a run's own entry
         * frame (one session, one id space), but a callee frame must NOT be built through this
         * overload: [HeapRef] is a structural data class, so a caller's `HeapRef(0)` and an
         * independently-minted callee `HeapRef(0)` compare EQUAL, and heap consensus would silently
         * treat two distinct objects as one.
         *
         * **A callee frame comes from [calleeEntryFrame] instead**, which is the caller's own frame handing
         * over its id space -- the enforcement, not just the instruction, since [HeapStore] is private to
         * this class so nothing outside can build the shared-heap frame any other way.
         */
        fun entryFrame(view: MethodView): Frame = entryFrame(view, HeapStore(HeapIds()), ClassInitStore())

        /** Default session input; constructor receivers own fresh fields when their layout is known. */
        internal fun entryFrame(view: MethodView, resolver: ProgramSnapshotResolver): Frame {
            val frame = entryFrame(view)
            if (!view.isStatic && view.signature.isConstructor) {
                // An unresolved superclass leaves the receiver unknown, rather than inventing its fields.
                resolver.instanceFieldDefaults(view.definingClass)?.let { fields ->
                    frame.assign(view.firstParameterRegister, frame.allocateObject(view.definingClass, fields))
                    frame.assigned = persistentSetOf()
                }
            }
            return frame
        }

        /**
         * The state a method starts in: locals unassigned, parameters seeded [Value.Unknown] of
         * their declared type (a caller's argument is not known to the method being analyzed in
         * isolation). Seeding is not an assignment *by the method*, so it does not appear in
         * [registersAssigned].
         *
         * [heap] is the run's heap. One [HeapIds] per entry frame therefore means one id space per run,
         * which is all uniqueness needs; a caller that starts two sessions from ONE seeded frame gives
         * both runs the same id space, which is harmless -- ids stay unique, they are simply not dense.
         *
         * [classes] starts EMPTY for a run's own entry frame, i.e. **the entry method's own defining class
         * is not initialized** -- which matches the old engine, whose `spawnEntrypointState()` seeds that
         * class's static defaults *without* marking it initialized (`state-model.md:160`,
         * `VirtualMachine.kt:516-525`). The first trigger inside the method initializes it, exactly as one
         * inside a callee would.
         */
        internal fun entryFrame(view: MethodView, heap: HeapStore, classes: ClassInitStore): Frame {
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
                frame.assign(parameter.register, Value.Unknown(parameter.type))
            }
            frame.assigned = persistentSetOf()
            return frame
        }
    }
}
