package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

/** What a session assumes about every class its entry path has not triggered. */
public enum class EntryClassState {
    /**
     * The entry is reached from arbitrary program state, as Simplify analyses every method: a class the path has
     * not triggered may be initialized, with any value in each non-final static, or not yet initialized.
     */
    ARBITRARY,

    /** The entry is the first code a fresh process runs, so no class is initialized yet. */
    FRESH_PROCESS,
}

/** Immutable class-initialization status supplied to a selected emulator. */
internal class ClassInitStatusSnapshot internal constructor(private val classes: ClassStore) {
    internal fun stateOf(type: VirtualType): ClassInitState = classes.initState(type)

    internal fun actionFor(type: VirtualType): ClassInitAction = classes.actionFor(type)
}

/** The only decisions a class-init trigger can receive from path state. */
internal sealed interface ClassInitAction {
    data object Proceed : ClassInitAction
    data object StartInitialization : ClassInitAction

    /** [type] failed initialization earlier on this path with [failure]; every trigger throws [recipe]. */
    data class ThrowFailed(val type: VirtualType, val failure: ThrownSubject) : ClassInitAction {
        val recipe: ThrowableRecipe get() = GuestThrowables.earlierClassFailure(type, failure)
    }
}

/** Returns the pre-access arm for one opcode trigger, or null when the opcode can proceed. */
internal fun Frame.classInitTrigger(type: VirtualType): OpExecution? = when (val action = classInitAction(type)) {
    ClassInitAction.Proceed -> null
    ClassInitAction.StartInitialization -> OpExecution.single(OpArm.InitClass(type))
    is ClassInitAction.ThrowFailed -> OpExecution.single(GuestThrowables.throwing(action.recipe))
}

/**
 * JLS 12.4.2 steps 11-12 for one exit by throw from [this] context's initializer. An `Error` leaves as itself;
 * anything else leaves as the cause of a fresh `ExceptionInInitializerError` (ART `WrapExceptionInInitializer`).
 * A domain that may be either forks into one outward path per partition. The initializing class and every waiting
 * dependent store the outward throwable as their failure, written only after every outward path is valid.
 */
internal fun ClassInitContext.failInitializer(
    exit: ActivationExit.Threw,
    hierarchy: ThrowableHierarchy,
): List<ActivationExit.Threw> {
    val partition = partitionThrowableDomain(exit.subject.domain, GuestThrowables.ERROR, hierarchy)
    val outward = listOfNotNull(partition.matching, partition.residual).map { domain ->
        val frame = exit.frame.copy()
        val source = exit.subject.refinedTo(domain, hierarchy)
        val thrown = if (domain === partition.matching) {
            source
        } else {
            GuestThrowables.exceptionInInitializer(source).materialize(frame, hierarchy)
        }
        ActivationExit.Threw(ThrowPath.create(frame, thrown, hierarchy))
    }
    outward.forEach { it.frame.failClassInit(this, it.subject, hierarchy) }
    return outward
}

/** Internal lifecycle data. Map absence is the only Uninitialized representation. */
private sealed interface ClassInitRecord {
    data object Initializing : ClassInitRecord
    data object Initialized : ClassInitRecord
    data class Failed(val firstOutwardFailure: ThrownSubject) : ClassInitRecord
}

/** Opaque exact key. The private lifecycle record never enters another file's signature. */
internal class ClassInitCompatibilityKey(private val value: Any) {
    override fun equals(other: Any?): Boolean =
        this === other || other is ClassInitCompatibilityKey && value == other.value

    override fun hashCode(): Int = value.hashCode()
}

/**
 * One path's class state (the [ClassState] contract): each class's initialization lifecycle and every
 * static field cell. Both are per class and per path, threaded across both call boundaries and shared per fork: a
 * class initialized on only some callee paths must stay uninitialized on the others, and a fork must not see its
 * sibling's static writes. [copy] shares immutable roots in constant time.
 *
 * There is no cross-path join of incompatible lifecycle state. Callers group callee terminals by
 * [compatibilityKey] before merging values, so INITIALIZED and absent/UNINITIALIZED stay on separate caller
 * paths, and [merge] rejects different keys.
 *
 * Lifecycle records are keyed by type descriptor. Static cells are keyed by the fully-qualified descriptor from
 * [ClassIndex.fieldDescriptorOf] (`Lcom/example/Foo;->bar:I`), so a shadowed static, or two same-named statics of
 * different types in one hierarchy, never share a cell. Each cell keeps its provenance, so a host projection never
 * silently becomes guest storage. [initializerOnlyStatics] names every prepared static that only its class's
 * `<clinit>` writes ([ProgramSnapshotResolver.initializerOnlyStatics]).
 */
internal class ClassStore private constructor(
    private var states: CompatibilityMapKey<String, ClassInitRecord>,
    private var statics: PersistentMap<String, StaticCell>,
    private var initializerOnlyStatics: PersistentSet<String>,
    private var entryState: EntryClassState,
) : ClassState {
    constructor() : this(CompatibilityMapKey.empty(), persistentMapOf(), persistentSetOf(), EntryClassState.ARBITRARY)

    /** A class this path has never triggered reads by the session's [EntryClassState]; total, never throws. */
    override fun initState(type: VirtualType): ClassInitState = when (states[type.descriptor]) {
        null -> when (entryState) {
            EntryClassState.ARBITRARY -> ClassInitState.UNKNOWN
            EntryClassState.FRESH_PROCESS -> ClassInitState.UNINITIALIZED
        }
        ClassInitRecord.Initializing -> ClassInitState.INITIALIZING
        ClassInitRecord.Initialized -> ClassInitState.INITIALIZED
        is ClassInitRecord.Failed -> ClassInitState.FAILED
    }

    /** Sets what the run assumes about untriggered classes; a session calls this once on its entry frame. */
    fun enter(state: EntryClassState) {
        entryState = state
    }

    fun actionFor(type: VirtualType): ClassInitAction = when (val record = states[type.descriptor]) {
        null -> ClassInitAction.StartInitialization
        ClassInitRecord.Initializing, ClassInitRecord.Initialized -> ClassInitAction.Proceed
        is ClassInitRecord.Failed -> ClassInitAction.ThrowFailed(type, record.firstOutwardFailure)
    }

    /**
     * [fieldDescriptor]'s value on this path, or [Value.Top] when this path has not triggered the field's class:
     * [prepare] seeds every declared static when the class is triggered, so an op that consults [actionFor]
     * first never reads `Top`. `Top` rather than `Unknown(T)`, because the cell does not exist and has no type.
     * [type] repeats the declaring class in [fieldDescriptor] to mirror [ExecutionState.getField]'s ref-keyed form.
     */
    override fun getStaticField(type: VirtualType, fieldDescriptor: String): Value =
        statics[fieldDescriptor]?.value ?: Value.Top

    internal fun staticCell(fieldDescriptor: String): StaticCell? = statics[fieldDescriptor]

    /**
     * Writes [value] into [fieldDescriptor] on this path as a guest write, with no join or coercion, as a Dalvik
     * static slot holds the bits written to it. Interpreting them under the field's declared type is the read's
     * job, as in ART.
     */
    fun setStaticField(fieldDescriptor: String, value: Value) {
        setStaticCell(fieldDescriptor, StaticCell(value, StaticCellProvenance.GUEST_WRITE))
    }

    internal fun setHostProjection(fieldDescriptor: String, value: Value) {
        val current = statics[fieldDescriptor]
        if (current?.provenance?.blocksHostProjection == true) return
        setStaticCell(fieldDescriptor, StaticCell(value, StaticCellProvenance.HOST_PROJECTION))
    }

    private fun setStaticCell(fieldDescriptor: String, cell: StaticCell) {
        statics = statics.putting(fieldDescriptor, cell)
    }

    /** Writes each of [descriptors] joined over [arms] under the same value and owner rule as [merge]. */
    internal fun reconcileStatics(descriptors: Iterable<String>, arms: List<ClassStore>, superclasses: TypeHierarchy) {
        for (descriptor in descriptors) {
            setStaticCell(descriptor, mergeStaticCells(arms.map { it.statics[descriptor] }, superclasses))
        }
    }

    /**
     * JVMS §5.4.2 preparation: gives every declared static of a class its pre-`<clinit>` value when this path
     * first triggers the class, and records which only `<clinit>` writes. [ClassInitPlanner] decides the values.
     * It runs before the `<clinit>`, so an initializer reading a static of its own class reads the prepared value.
     *
     * A prepared seed replaces a host projection; a guest write stays authoritative, so a retried guest
     * initializer or an explicit seed never merges silently with host-owned state.
     */
    fun prepare(seeds: Map<String, StaticCell>, initializerOnly: Set<String>) {
        var updated = statics
        for ((descriptor, cell) in seeds) {
            val current = updated[descriptor]
            if (current == null || current.provenance == StaticCellProvenance.HOST_PROJECTION) {
                updated = updated.putting(descriptor, cell)
            }
        }
        statics = updated
        if (initializerOnly.isNotEmpty()) initializerOnlyStatics = initializerOnlyStatics.addAll(initializerOnly)
    }

    fun start(type: VirtualType) {
        require(states[type.descriptor] == null) {
            "Cannot start class initialization for ${type.descriptor} from ${initState(type)}"
        }
        replace(type, ClassInitRecord.Initializing)
    }

    fun complete(type: VirtualType) {
        val current = states[type.descriptor]
        require(current == null || current == ClassInitRecord.Initializing) {
            "Cannot complete class initialization for ${type.descriptor} from ${initState(type)}"
        }
        replace(type, ClassInitRecord.Initialized)
    }

    /**
     * Marks [type] initialized after its `<clinit>` returned. [before] is this path's state at the trigger when
     * the trigger found the lifecycle [ClassInitState.UNKNOWN], else null. In that case the class may instead
     * have been initialized before the entry, so every static the initializer changed that other code can also
     * write may hold another value: it reads `Unknown` with guest provenance. An initializer-only static keeps the
     * initializer's value, because nothing else writes it.
     */
    fun completeInitializer(type: VirtualType, before: ClassStore?) {
        complete(type)
        if (before == null) return
        var updated = statics
        for ((descriptor, cell) in statics) {
            if (descriptor in initializerOnlyStatics || before.statics[descriptor] == cell) continue
            val unknown = Value.Unknown(FieldSignature.parse(descriptor).type)
            updated = updated.putting(descriptor, StaticCell(unknown, StaticCellProvenance.GUEST_WRITE))
        }
        statics = updated
    }

    fun fail(type: VirtualType, failure: ThrownSubject) {
        require(states[type.descriptor] == ClassInitRecord.Initializing) {
            "Cannot fail class initialization for ${type.descriptor} from ${initState(type)}"
        }
        replace(type, ClassInitRecord.Failed(failure))
    }

    /** Only superclass dependency failure may move an uninitialized class directly to Failed. */
    fun failDependency(type: VirtualType, failure: ThrownSubject) {
        val current = states[type.descriptor]
        require(current == null || current == ClassInitRecord.Initializing) {
            "Cannot dependency-fail class initialization for ${type.descriptor} from ${initState(type)}"
        }
        replace(type, ClassInitRecord.Failed(failure))
    }

    private fun replace(type: VirtualType, record: ClassInitRecord) {
        states = states.put(type.descriptor, record)
    }

    /** A new wrapper over the persistent roots. A later write replaces only the writing path's root. */
    fun copy(): ClassStore = ClassStore(states, statics, initializerOnlyStatics, entryState)

    /** Exact lifecycle partition key. Missing means UNINITIALIZED or UNKNOWN and is distinct from INITIALIZED. */
    fun compatibilityKey(): ClassInitCompatibilityKey = ClassInitCompatibilityKey(states)

    internal fun guestRootValues(): List<Value> = buildList {
        addAll(statics.values.map(StaticCell::value))
        states.values.mapNotNullTo(this) { record ->
            (record as? ClassInitRecord.Failed)?.firstOutwardFailure?.value
        }
    }

    internal fun statusSnapshot(): ClassInitStatusSnapshot = ClassInitStatusSnapshot(copy())

    override fun toString(): String = "ClassStore($entryState, $states, statics=$statics)"

    companion object {
        /** Joins one exact-lifecycle call partition. Callers must partition incompatible states first. */
        fun merge(stores: List<ClassStore>, superclasses: TypeHierarchy): ClassStore {
            require(stores.isNotEmpty())
            val first = stores.first()
            val shared = stores.all {
                it.states === first.states && it.statics === first.statics &&
                    it.initializerOnlyStatics === first.initializerOnlyStatics
            }
            if (shared) return first.copy()
            val stateKey = first.states
            require(stores.all { it.states == stateKey }) {
                "Class-init states cannot be joined. Partition incompatible lifecycle maps before merging."
            }
            val mergedStates = first.states
            var mergedStatics = if (stores.all { it.statics === first.statics }) first.statics else persistentMapOf()
            if (mergedStatics !== first.statics) {
                for (key in stores.flatMap { it.statics.keys }.toSortedSet()) {
                    mergedStatics = mergedStatics.putting(
                        key,
                        mergeStaticCells(stores.map { it.statics[key] }, superclasses),
                    )
                }
            }
            val initializerOnly = stores.fold(first.initializerOnlyStatics) { union, store ->
                union.addAll(store.initializerOnlyStatics)
            }
            return ClassStore(mergedStates, mergedStatics, initializerOnly, first.entryState)
        }

        private fun mergeStaticCells(cells: List<StaticCell?>, superclasses: TypeHierarchy): StaticCell {
            val present = cells.filterNotNull()
            if (present.isEmpty()) return StaticCell(Value.Top, StaticCellProvenance.GUEST_WRITE)
            if (present.size != cells.size) {
                return StaticCell(Value.Top, StaticCellProvenance.GUEST_WRITE)
            }
            val values = present.map(StaticCell::value)
            val firstProvenance = present.first().provenance
            if (present.all { it.provenance == firstProvenance }) {
                return StaticCell(joinAll(values, superclasses) ?: Value.Top, firstProvenance)
            }
            val provenance = conservativeMixedProvenance(present)
            val firstValue = values.first()
            if (firstValue is Value.Known && values.drop(1).all(firstValue::agreesWith)) {
                return StaticCell(firstValue, provenance)
            }
            val joined = joinAll(values, superclasses) ?: Value.Top
            val type = joined.type ?: present.firstNotNullOfOrNull { it.value.type }
            return StaticCell(
                type?.let(Value::Unknown) ?: Value.Top,
                provenance,
            )
        }

        /** Mixed ownership keeps exact agreement and otherwise selects the least permissive valid owner. */
        private fun conservativeMixedProvenance(cells: List<StaticCell>): StaticCellProvenance {
            val provenances = cells.mapTo(linkedSetOf(), StaticCell::provenance)
            return when {
                StaticCellProvenance.GUEST_WRITE in provenances -> StaticCellProvenance.GUEST_WRITE
                StaticCellProvenance.ENCODED_LITERAL in provenances -> StaticCellProvenance.ENCODED_LITERAL
                cells.map(StaticCell::value).let { values ->
                    values.first() is Value.Known && values.drop(1).all(values.first()::agreesWith)
                } -> StaticCellProvenance.HOST_PROJECTION
                else -> StaticCellProvenance.SEEDED_DEFAULT
            }
        }
    }
}

/**
 * The owner of one path-local static cell. Host and guest domains never share an unmarked value. A
 * [GUEST_WRITE] cell holding `Unknown` stands for a write by code outside the analysed path, such as code that
 * ran before an arbitrary entry.
 */
internal enum class StaticCellProvenance {
    ENCODED_LITERAL,
    SEEDED_DEFAULT,
    GUEST_WRITE,
    HOST_PROJECTION,
    ;

    internal val blocksHostProjection: Boolean get() = this == ENCODED_LITERAL || this == GUEST_WRITE
}

/** One static value and the domain that supplied it. */
internal data class StaticCell(val value: Value, val provenance: StaticCellProvenance)

/**
 * The value a field of [type] holds before anything assigns it: [JVMS] §2.3/§2.4's default values, applied at
 * §5.4.2 preparation. A [Value.Known], because after initialization a static nothing assigned is definitely its
 * default. The raw's boxed type matches the declared type exactly (`Byte` under `B`), as [Value.Known] requires.
 * `V` is not a field type, so the `else` asserts a programming error rather than handling input.
 */
internal fun defaultValueOf(type: VirtualType): Value.Known = Value.Known(
    when (type.descriptor) {
        "Z" -> false
        "B" -> 0.toByte()
        "S" -> 0.toShort()
        "C" -> '\u0000'
        "I" -> 0
        "J" -> 0L
        "F" -> 0f
        "D" -> 0.0
        else -> {
            require(type.isReference) { "No default value for type ${type.descriptor}: not a field type" }
            null
        }
    },
    type,
)

/** The runtime class-init trigger carried by one prepared invocation, or null when this call adds none. */
internal val PreparedInvocation.classInitTarget: VirtualType?
    get() = if (kind == InvokeKind.STATIC && target.name != "<clinit>") target.definingClass else null

/**
 * The one class whose guest `<clinit>` a trigger must run now, as a nested activation. [entryAddress] is a claim
 * about [view], checked by `MethodView.hasAddress` before anything is decoded, as for [BackendProbe.Schedule].
 */
internal class GuestInitializer(
    val type: VirtualType,
    val view: MethodView,
    val entryAddress: Int,
)

/**
 * One visible initializer and every class waiting on its successful completion. [triggerState] is the path's
 * class state at the trigger when the trigger found the initializer's class [ClassInitState.UNKNOWN], else null;
 * a successful return forgets the non-final static writes made since ([ClassStore.completeInitializer]).
 *
 * [ownInitializerOnlyStatics] and [allocationMark] decide which writes stay confined to this initialization
 * ([confines]): writes of the initializer's own class's initializer-only statics, and of objects whose heap id is
 * at least [allocationMark], the next id the run's [HeapIds] handed out when the trigger planned it. Ids are
 * monotonic and a path's heap holds only objects its own history allocated, so such an object was allocated on
 * this path by this initialization or code it called.
 */
internal class ClassInitContext(
    val requestedClass: VirtualType,
    val activeInitializer: GuestInitializer,
    val waitingDependents: List<VirtualType>,
    val ownInitializerOnlyStatics: Set<String>,
    val allocationMark: Int,
    val triggerState: ClassStore? = null,
) {
    /** Whether [ref] was allocated after this initialization started. */
    fun allocatedDuring(ref: HeapRef): Boolean = ref.id >= allocationMark

    /**
     * Whether an update with [footprint] writes something, and writes only cells this initialization owns: its own
     * class's initializer-only statics and objects it allocated.
     */
    fun confines(footprint: OpUpdateFootprint): Boolean {
        val statics = footprint.staticDescriptors
        val arrays = footprint.arrayRanges.keys
        val objects = footprint.objectFields.keys
        if (statics.isEmpty() && arrays.isEmpty() && objects.isEmpty()) return false
        return statics.all { it in ownInitializerOnlyStatics } && arrays.all(::allocatedDuring) &&
            objects.all(::allocatedDuring)
    }

    init {
        require(activeInitializer.type !in waitingDependents) {
            "The active initializer cannot also be a waiting dependent"
        }
        require(waitingDependents.distinct() == waitingDependents) {
            "Class-init dependents must be unique and deterministic"
        }
        require(requestedClass == activeInitializer.type || requestedClass in waitingDependents) {
            "The requested class must be active or wait on the active initializer"
        }
    }
}

/**
 * An erroneous ancestor blocks initialization before another initializer can start. Every trigger of the
 * requested class throws [recipe], the ancestor's own earlier-failure error, and the still-uninitialized
 * [waitingDependents] store that throwable as their failure (ART `ClassLinker::InitializeClass` marks a class
 * erroneous with the exception its superclass's initialization raised).
 */
internal class ClassInitDependencyFailure(
    val recipe: ThrowableRecipe,
    val waitingDependents: List<VirtualType>,
)

/**
 * What must happen before a class-init trigger may do its own work. A plan applies nothing; [ExecutionSession]
 * is the only writer of node, frame and frontier state.
 *
 * A plan carries at most one guest `<clinit>`: [context] owns the topmost uninitialized class in the chain that
 * has one, and the trigger asks again when it resumes. A superclass therefore runs strictly before its subclass,
 * and the scheduler never chooses an order.
 */
internal class ClassInitPlan(
    /**
     * Classes that become [ClassInitState.INITIALIZED] with no nested run, ancestors first: a class with no
     * declared `<clinit>`, or a framework class whose `<clinit>` is never run.
     */
    val settled: List<VirtualType>,
    /**
     * Classes in [settled] whose declared `<clinit>` did not run, ancestors first. A class with no initializer skips
     * nothing. The session decides from the reflection policy which of them leave unmodelled state behind.
     */
    val skippedInitializers: List<VirtualType>,
    /** The guest `<clinit>` to run now and the requested classes waiting on it. */
    internal val context: ClassInitContext?,
    internal val dependencyFailure: ClassInitDependencyFailure? = null,
    /**
     * A class on the chain whose declared `<clinit>` has no code. ART's dex verifier rejects such a file
     * (`DexFileVerifier::CheckMethodAccessFlags`: "Constructor ... must not be abstract or native"), so the
     * trigger's path ends as [AbortReason.MALFORMED_INPUT].
     */
    internal val malformedInitializer: VirtualType? = null,
    /**
     * JVMS §5.4.2 preparation: the pre-`<clinit>` value of every static of every class this plan settles or
     * starts, keyed by fully-qualified descriptor, ancestors first. Applied by [Frame.applyClassInit] before any
     * initializer runs, so a `<clinit>` reading its own class's static sees the prepared value.
     */
    internal val staticSeeds: Map<String, StaticCell> = emptyMap(),
    /** The descriptors in [staticSeeds] that only their class's `<clinit>` writes. */
    internal val initializerOnlyStatics: Set<String> = emptySet(),
) {
    /**
     * Every class in the chain is already triggered on this path. A correct trigger never asks for
     * this plan, and the scheduler rejects it: re-executing the trigger would ask again forever.
     */
    val isNoOp: Boolean
        get() = settled.isEmpty() && context == null && dependencyFailure == null && malformedInitializer == null

    override fun toString(): String =
        "ClassInitPlan(settled=$settled, skippedInitializers=$skippedInitializers, " +
            "context=${context?.activeInitializer?.type}, dependencyFailure=${dependencyFailure?.recipe?.message}, " +
            "malformedInitializer=$malformedInitializer, staticSeeds=$staticSeeds)"
}

/**
 * Decides what one class-init trigger needs, reading the triggering path's [ClassState] and the loaded class set
 * and changing neither. A `<clinit>` runs as an ordinary nested call, so this planner only chooses it.
 */
internal class ClassInitPlanner(private val resolver: ProgramSnapshotResolver) {
    constructor(classIndex: ClassIndex) : this(classIndex.resolver(classIndex.currentSnapshot))

    /**
     * The plan for initializing [type] on the path [state] describes.
     *
     * Walks [type]'s superclass chain and answers about the topmost class still needing work, so repeated calls
     * initialize a hierarchy from the top down. A failed ancestor nearest to [type] fails the request, so each
     * descendant stores its own superclass's outward failure. Superinterfaces are not walked: JLS §12.4.1
     * initializes one only when it declares a default method.
     *
     * Throws [ClassLoadFailure] when an ancestor is absent from the loaded set; callers end only the path.
     */
    fun plan(state: Frame, type: VirtualType): ClassInitPlan {
        val chain = chainFromTheTop(type)
        val settled = ArrayList<VirtualType>(2)
        val seeds = LinkedHashMap<String, StaticCell>()
        val initializerOnly = HashSet<String>()
        val skippedInitializers = ArrayList<VirtualType>(1)
        val nearestFailedAncestorIndex = chain.indexOfLast { ancestor ->
            state.classInitAction(ancestor) is ClassInitAction.ThrowFailed
        }
        for ((index, ancestor) in chain.withIndex()) {
            when (val action = state.classInitAction(ancestor)) {
                ClassInitAction.Proceed -> continue
                is ClassInitAction.ThrowFailed -> {
                    if (index != nearestFailedAncestorIndex) continue
                    return ClassInitPlan(
                        settled,
                        skippedInitializers,
                        context = null,
                        dependencyFailure = ClassInitDependencyFailure(
                            action.recipe,
                            uninitializedBelow(state, chain, index),
                        ),
                        staticSeeds = seeds,
                        initializerOnlyStatics = initializerOnly,
                    )
                }
                ClassInitAction.StartInitialization -> Unit
            }
            val lifecycleUnknown = state.initState(ancestor) == ClassInitState.UNKNOWN
            // A framework class's `<clinit>` never runs, and is not even resolved. `Ljava/lang/Object;` declares
            // none and is on every chain, so only a framework class that declares one counts as skipped.
            if (resolver.hierarchy.isFrameworkClass(ancestor)) {
                settled.add(ancestor)
                if (resolver.hasClassInitializer(ancestor)) skippedInitializers.add(ancestor)
                prepare(ancestor, initializerRuns = false, lifecycleUnknown, seeds, initializerOnly)
                continue
            }
            when (val lookup = resolver.lookupClassInitializer(ancestor)) {
                MethodLookup.NoSuchMethod -> {
                    settled.add(ancestor)
                    prepare(ancestor, initializerRuns = false, lifecycleUnknown, seeds, initializerOnly)
                }
                is MethodLookup.Runnable -> {
                    prepare(ancestor, initializerRuns = true, lifecycleUnknown, seeds, initializerOnly)
                    return ClassInitPlan(
                        settled,
                        skippedInitializers,
                        ClassInitContext(
                            type,
                            GuestInitializer(ancestor, lookup.view, ENTRY_ADDRESS),
                            uninitializedBelow(state, chain, index),
                            ownInitializerOnlyStatics = resolver.initializerOnlyStatics(ancestor),
                            allocationMark = state.heap.allocationMark(),
                            triggerState = if (lifecycleUnknown) state.classes.copy() else null,
                        ),
                        staticSeeds = seeds,
                        initializerOnlyStatics = initializerOnly,
                    )
                }
                is MethodLookup.NoBody -> return ClassInitPlan(
                    settled,
                    skippedInitializers,
                    context = null,
                    malformedInitializer = ancestor,
                )
                is MethodLookup.DefaultConflict, is MethodLookup.AbstractSelection, MethodLookup.NoSuchClass ->
                    error("The declared <clinit> of loaded class ${ancestor.descriptor} resolved to $lookup")
            }
        }
        return ClassInitPlan(
            settled,
            skippedInitializers,
            context = null,
            staticSeeds = seeds,
            initializerOnlyStatics = initializerOnly,
        )
    }

    /**
     * The preparation of [type] for a run whose entry is [type]'s own `<clinit>`: nothing can run before it, so
     * its statics hold their prepared values whatever the session's [EntryClassState].
     */
    fun ownInitializerPreparation(type: VirtualType): ClassInitPlan {
        val seeds = LinkedHashMap<String, StaticCell>()
        val initializerOnly = HashSet<String>()
        prepare(type, initializerRuns = true, lifecycleUnknown = false, seeds, initializerOnly)
        return ClassInitPlan(
            emptyList(),
            emptyList(),
            context = null,
            staticSeeds = seeds,
            initializerOnlyStatics = initializerOnly,
        )
    }

    /** The classes after [index] in the top-first [chain] that are still uninitialized on [state]'s path. */
    private fun uninitializedBelow(state: Frame, chain: List<VirtualType>, index: Int): List<VirtualType> =
        chain.subList(index + 1, chain.size).filter {
            state.classInitAction(it) == ClassInitAction.StartInitialization
        }

    /**
     * Adds [type]'s statics after JVMS §5.4.2 preparation and before its `<clinit>` to [seeds], and the ones only
     * its `<clinit>` writes ([ProgramSnapshotResolver.initializerOnlyStatics]) to [initializerOnly]:
     *  - a static other code can write, of a class whose lifecycle is unknown and whose initializer does not run,
     *    is [Value.Unknown] with guest provenance, literal or not: code before the entry may have written it;
     *  - otherwise a dex-encoded literal is that literal, whether or not the class has an initializer, because
     *    a compiler emits no `<clinit>` assignment for a `static final` constant;
     *  - otherwise the type default, when the class has no initializer or its initializer is about to run;
     *  - otherwise [Value.Unknown]: a framework initializer that does not run may compute anything.
     */
    private fun prepare(
        type: VirtualType,
        initializerRuns: Boolean,
        lifecycleUnknown: Boolean,
        seeds: MutableMap<String, StaticCell>,
        initializerOnly: MutableSet<String>,
    ) {
        val declarations = resolver.declaredStaticFields(type)
        if (declarations.isEmpty()) return
        val initializerAccountedFor = initializerRuns || !resolver.hasClassInitializer(type)
        val ownInitializerOnly = resolver.initializerOnlyStatics(type)
        initializerOnly.addAll(ownInitializerOnly)
        for (declaration in declarations) {
            seeds[declaration.descriptor] = when {
                lifecycleUnknown && declaration.descriptor !in ownInitializerOnly && !initializerRuns ->
                    StaticCell(Value.Unknown(declaration.type), StaticCellProvenance.GUEST_WRITE)
                declaration.literal != null -> StaticCell(declaration.literal, StaticCellProvenance.ENCODED_LITERAL)
                initializerAccountedFor -> StaticCell(defaultValueOf(declaration.type), StaticCellProvenance.SEEDED_DEFAULT)
                else -> StaticCell(Value.Unknown(declaration.type), StaticCellProvenance.SEEDED_DEFAULT)
            }
        }
    }

    /**
     * [type] and its superclasses, most ancestral first, ending at the first class with no superclass. A cyclic
     * declaration stops at the first repeat; the set keeps the walk linear in the chain's length.
     */
    private fun chainFromTheTop(type: VirtualType): List<VirtualType> {
        val seen = HashSet<VirtualType>()
        val chain = ArrayList<VirtualType>()
        var current: VirtualType? = type
        while (current != null && seen.add(current)) {
            chain.add(current)
            current = resolver.hierarchy.superclassOf(current)
        }
        return chain.asReversed()
    }

    private companion object {
        /** A DEX method's code starts at address 0. */
        const val ENTRY_ADDRESS = 0
    }
}
