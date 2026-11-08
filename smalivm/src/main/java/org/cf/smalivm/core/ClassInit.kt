package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/** Immutable class-initialization status supplied to a selected emulator. */
class ClassInitStatusSnapshot internal constructor(private val classes: ClassInitStore) {
    fun stateOf(type: VirtualType): ClassInitState = classes.initState(type)

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
 * One path's class-initialization lifecycle: the third of spec §4.2's three stores, alongside [RegisterFile]
 * and [Heap], implementing the [ClassState] contract.
 *
 * Per path, never session-global: a class initialized on only some callee paths must stay uninitialized on
 * the others, or later accesses skip initialization on paths where it never ran. [copy] shares immutable
 * roots in constant time.
 *
 * There is deliberately no cross-path join of incompatible lifecycle state. Callers group callee terminals by
 * [compatibilityKey] before merging values, so INITIALIZED and absent/UNINITIALIZED stay on separate caller
 * paths, and [merge] rejects different keys.
 *
 * Keyed by descriptor string: [VirtualType] is structural on its descriptor, so the two are equivalent keys.
 */
internal class ClassInitStore private constructor(
    private var states: CompatibilityMapKey<String, ClassInitRecord>,
    /**
     * This path's static field cells, keyed by the fully-qualified descriptor from
     * [ClassIndex.fieldDescriptorOf] (`Lcom/example/Foo;->bar:I`), so a shadowed static, or two same-named
     * statics of different types in one hierarchy, never share a cell.
     *
     * Statics live beside [states] because they have the same scoping: per class, per path, threaded across
     * both call boundaries ([Frame.calleeEntryFrame] in, [Frame.resumedWith] back) and shared per fork. A
     * `<clinit>`'s caller must see its writes; a fork must not see its sibling's. Each cell keeps its value
     * and provenance, so a host projection never silently becomes guest storage.
     */
    private var statics: PersistentMap<String, StaticCell>,
) : ClassState {
    constructor() : this(CompatibilityMapKey.empty(), persistentMapOf())

    /** [ClassInitState.UNINITIALIZED] for a class this path has never triggered -- total, never throws. */
    override fun initState(type: VirtualType): ClassInitState = when (states[type.descriptor]) {
        null -> ClassInitState.UNINITIALIZED
        ClassInitRecord.Initializing -> ClassInitState.INITIALIZING
        ClassInitRecord.Initialized -> ClassInitState.INITIALIZED
        is ClassInitRecord.Failed -> ClassInitState.FAILED
    }

    fun actionFor(type: VirtualType): ClassInitAction = when (val record = states[type.descriptor]) {
        null -> ClassInitAction.StartInitialization
        ClassInitRecord.Initializing, ClassInitRecord.Initialized -> ClassInitAction.Proceed
        is ClassInitRecord.Failed -> ClassInitAction.ThrowFailed(type, record.firstOutwardFailure)
    }

    /**
     * [fieldDescriptor]'s value on this path, or [Value.Top] when this path holds none.
     *
     * **A [Value.Top] here means the field's class has not left [ClassInitState.UNINITIALIZED] on this path**,
     * because [prepare] seeds EVERY declared static of a class at the moment it does (JVMS §5.4.2's
     * preparation, which precedes initialization). So an op that consults [actionFor] first -- which
     * every trigger must, or it re-executes forever -- never sees it, and a `Top` reaching a consumer is a
     * scheduler bug rather than an imprecision. It stays `Top` rather than `Unknown(fieldType)` for the reason
     * it always did: `Top` is *"this path knows nothing at all"*, while `Unknown(T)` would assert a declared
     * type for a cell that does not exist.
     *
     * [type] is redundant with [fieldDescriptor], which already names the declaring class -- kept because
     * [ClassState.getStaticField]'s contract is the class-keyed mirror of [Heap.getField]'s ref-keyed one, and
     * because a caller passing a type that disagrees with the descriptor has a resolution bug worth being able
     * to see in a signature.
     */
    override fun getStaticField(type: VirtualType, fieldDescriptor: String): Value =
        statics[fieldDescriptor]?.value ?: Value.Top

    internal fun staticCell(fieldDescriptor: String): StaticCell? = statics[fieldDescriptor]

    /**
     * Writes [value] into [fieldDescriptor] on this path -- `sput*`'s whole effect, and the test harness's
     * field seeding.
     *
     * No merge, no join, no coercion: the cell holds what it was given and becomes guest-owned, exactly as a
     * Dalvik static slot holds the bits written to it. Interpreting those bits under the field's declared type is the READ's business
     * (`core/op/SGetOp`'s reference row), which is where ART puts it too -- the reference half of a frame is
     * consulted by the fetch, not by the store.
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
    internal fun reconcileStatics(descriptors: Iterable<String>, arms: List<ClassInitStore>, superclasses: Superclasses) {
        for (descriptor in descriptors) {
            setStaticCell(descriptor, mergeStaticCells(arms.map { it.statics[descriptor] }, superclasses))
        }
    }

    /**
     * **JVMS §5.4.2 preparation**: gives every declared static of a class its pre-`<clinit>` value, at the
     * moment the class leaves [ClassInitState.UNINITIALIZED] on this path.
     *
     * Separate from lifecycle transitions and called by [Frame.applyClassInit], because *what* the values
     * are is a class-model question this file may not ask (the two-tier rule) -- [ClassInitPlanner] decides
     * them and [ClassInitPlan.staticSeeds] carries them.
     *
     * **BEFORE the `<clinit>` runs, which is the ordering that matters.** Preparation precedes initialization,
     * so a `<clinit>` reading a static of its own class reads the prepared value (typically the type default)
     * and may then overwrite it -- which is exactly what `Lstatic_fields_test;-><clinit>()V` does
     * (`sget alwaysZero` -> `sput testInt1`) and what the ported `canGetFieldsWhileInitializing` asserts. A
     * build that seeded on COMPLETION instead would read `Top` inside the initializer and then clobber
     * whatever it wrote.
     *
     * A prepared seed replaces a prior host projection. A guest write remains authoritative. This makes a
     * retried guest initializer or explicit test seed unable to merge silently with host-owned static state.
     */
    fun prepare(seeds: Map<String, StaticCell>) {
        var updated = statics
        for ((descriptor, cell) in seeds) {
            val current = updated[descriptor]
            if (current == null || current.provenance == StaticCellProvenance.HOST_PROJECTION) {
                updated = updated.putting(descriptor, cell)
            }
        }
        statics = updated
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

    /** A new wrapper over both persistent roots. A later write replaces only the writing path's root. */
    fun copy(): ClassInitStore = ClassInitStore(states, statics)

    /** Exact lifecycle partition key. Missing means UNINITIALIZED and is therefore distinct from INITIALIZED. */
    fun compatibilityKey(): ClassInitCompatibilityKey = ClassInitCompatibilityKey(states)

    internal fun guestRootValues(): List<Value> = buildList {
        addAll(statics.values.map(StaticCell::value))
        states.values.mapNotNullTo(this) { record ->
            (record as? ClassInitRecord.Failed)?.firstOutwardFailure?.value
        }
    }

    internal fun statusSnapshot(): ClassInitStatusSnapshot = ClassInitStatusSnapshot(copy())

    override fun toString(): String = "ClassInitStore($states, statics=$statics)"

    companion object {
        /** Joins one exact-lifecycle call partition. Callers must partition incompatible states first. */
        fun merge(stores: List<ClassInitStore>, superclasses: Superclasses): ClassInitStore {
            require(stores.isNotEmpty())
            val first = stores.first()
            if (stores.all { it.states === first.states && it.statics === first.statics }) return first.copy()
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
            return ClassInitStore(mergedStates, mergedStatics)
        }

        private fun mergeStaticCells(cells: List<StaticCell?>, superclasses: Superclasses): StaticCell {
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

/** The owner of one path-local static cell. Host and guest domains never share an unmarked value. */
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
 * The value a field of [type] holds before anything assigns it -- **SPEC**, [JVMS] §2.3/§2.4's default values,
 * applied at §5.4.2 preparation: `0` for every integral type, `0.0` for the floating ones, `false` for
 * `boolean`, `'\u0000'` (NUL, not a space) for `char`, and the null reference for every class or array type.
 *
 * A **[Value.Known]**, not an [Value.Unknown], and that is the whole content of the deliverable's *"an
 * untouched static of an INITIALIZED class is its type default, which is a provable fact"*: after a class is
 * initialized, a static nothing assigned is definitely its default, and answering `Unknown` there would refuse
 * to fold a constant Dalvik guarantees. The oracle agrees for a class with no `<clinit>`
 * (`type/VirtualField.kt:43-54` is the same table), which is why the *Uninitialized* trio of migrated
 * `SGetOpTest` expectations (`false`, `0`, `'\u0000'`, `null`, ...) ports byte-identically.
 *
 * The raw's boxed runtime type matches the declared type exactly (`Byte` under `B`, `Short` under `S`), which
 * [Value.Known]'s own precondition asks for and which the migrated expectations assert -- `0.toByte()` is not
 * `0`, and `agreesWith` compares raws with host `equals`.
 *
 * `V` cannot reach here: `void` is not a field type, and [VirtualType]'s grammar forbids it as an array
 * component, so the `else` is a programming-error assertion rather than an input path.
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
 * The one class whose guest `<clinit>` a trigger must run **now**, as a nested activation.
 *
 * [view] and [entryAddress] are the same pair [BackendProbe.Schedule] carries, and for the same reason: the
 * address is a *claim about the callee's method*, checked by `MethodView.hasAddress` before anything is
 * decoded (plan design decision 5).
 */
internal class GuestInitializer(
    val type: VirtualType,
    val view: MethodView,
    val entryAddress: Int,
)

/** One visible initializer and every class waiting on its successful completion. */
internal class ClassInitContext(
    val requestedClass: VirtualType,
    val activeInitializer: GuestInitializer,
    val waitingDependents: List<VirtualType>,
) {
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
     * True when a class in [settled] has a `<clinit>` that did not run. Whatever it would have changed is
     * unmodelled, so the trigger declares [SideEffectLevel.STRONG]; a class with no initializer skips nothing.
     */
    val skippedAnInitializer: Boolean,
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
) {
    /**
     * Every class in the chain is already past [ClassInitState.UNINITIALIZED]. A correct trigger never asks for
     * this plan, and the scheduler rejects it: re-executing the trigger would ask again forever.
     */
    val isNoOp: Boolean
        get() = settled.isEmpty() && context == null && dependencyFailure == null && malformedInitializer == null

    override fun toString(): String =
        "ClassInitPlan(settled=$settled, skippedAnInitializer=$skippedAnInitializer, " +
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
        var skippedAnInitializer = false
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
                        skippedAnInitializer,
                        context = null,
                        dependencyFailure = ClassInitDependencyFailure(
                            action.recipe,
                            uninitializedBelow(state, chain, index),
                        ),
                        staticSeeds = seeds,
                    )
                }
                ClassInitAction.StartInitialization -> Unit
            }
            // A framework class's `<clinit>` never runs, and is not even resolved. `Ljava/lang/Object;` declares
            // none and is on every chain, so only a framework class that declares one counts as skipped.
            if (resolver.isFrameworkClass(ancestor)) {
                settled.add(ancestor)
                if (resolver.hasClassInitializer(ancestor)) skippedAnInitializer = true
                seeds.putAll(prepare(ancestor, initializerRuns = false))
                continue
            }
            when (val lookup = resolver.lookupClassInitializer(ancestor)) {
                MethodLookup.NoSuchMethod -> {
                    settled.add(ancestor)
                    seeds.putAll(prepare(ancestor, initializerRuns = false))
                }
                is MethodLookup.Runnable -> {
                    seeds.putAll(prepare(ancestor, initializerRuns = true))
                    return ClassInitPlan(
                        settled,
                        skippedAnInitializer,
                        ClassInitContext(
                            type,
                            GuestInitializer(ancestor, lookup.view, ENTRY_ADDRESS),
                            uninitializedBelow(state, chain, index),
                        ),
                        staticSeeds = seeds,
                    )
                }
                is MethodLookup.NoBody -> return ClassInitPlan(
                    settled,
                    skippedAnInitializer,
                    context = null,
                    malformedInitializer = ancestor,
                )
                is MethodLookup.DefaultConflict, MethodLookup.NoSuchClass ->
                    error("The declared <clinit> of loaded class ${ancestor.descriptor} resolved to $lookup")
            }
        }
        return ClassInitPlan(settled, skippedAnInitializer, context = null, staticSeeds = seeds)
    }

    /** The classes after [index] in the top-first [chain] that are still uninitialized on [state]'s path. */
    private fun uninitializedBelow(state: Frame, chain: List<VirtualType>, index: Int): List<VirtualType> =
        chain.subList(index + 1, chain.size).filter {
            state.classInitAction(it) == ClassInitAction.StartInitialization
        }

    /**
     * [type]'s statics after JVMS §5.4.2 preparation and before its `<clinit>`:
     *  - a dex-encoded literal is that literal, whether or not the class has an initializer, because a compiler
     *    emits no `<clinit>` assignment for a `static final` constant;
     *  - otherwise the type default, when the class has no initializer or its initializer is about to run;
     *  - otherwise [Value.Unknown]: a framework initializer that does not run may compute anything.
     */
    private fun prepare(type: VirtualType, initializerRuns: Boolean): Map<String, StaticCell> {
        val declarations = resolver.declaredStaticFields(type)
        if (declarations.isEmpty()) return emptyMap()
        val initializerAccountedFor = initializerRuns || !resolver.hasClassInitializer(type)
        val seeds = LinkedHashMap<String, StaticCell>(declarations.size)
        for (declaration in declarations) {
            seeds[declaration.descriptor] = if (declaration.literal != null) {
                StaticCell(declaration.literal, StaticCellProvenance.ENCODED_LITERAL)
            } else {
                StaticCell(
                    if (initializerAccountedFor) defaultValueOf(declaration.type) else Value.Unknown(declaration.type),
                    StaticCellProvenance.SEEDED_DEFAULT,
                )
            }
        }
        return seeds
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
            current = resolver.superclassOf(current)
        }
        return chain.asReversed()
    }

    private companion object {
        /** A DEX method's code starts at address 0. */
        const val ENTRY_ADDRESS = 0
    }
}
