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
    data class ThrowFailed(val failure: ThrownSubject) : ClassInitAction
}

/** Returns the pre-access arm for one opcode trigger, or null when the opcode can proceed. */
internal fun Frame.classInitTrigger(type: VirtualType): OpExecution? = when (val action = classInitAction(type)) {
    ClassInitAction.Proceed -> null
    ClassInitAction.StartInitialization -> OpExecution.single(OpArm.InitClass(type))
    is ClassInitAction.ThrowFailed -> OpExecution.single(
        OpArm.Throw(
            evidence = ThrowEvidence.Fresh(
                ThrowableRecipe.of(
                    VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
                    type.descriptor,
                    action.failure,
                ),
            ),
        ),
    )
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
        is ClassInitRecord.Failed -> ClassInitAction.ThrowFailed(record.firstOutwardFailure)
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

/** A prior failed ancestor blocks initialization before another initializer can start. */
internal class ClassInitDependencyFailure(
    val failedAncestor: VirtualType,
    val storedFailure: ThrownSubject,
    val waitingDependents: List<VirtualType>,
)

/**
 * What must happen before a class-init trigger may do its own work (spec §7's lifecycle, design decisions 1
 * and 2). A **plan**: it applies nothing, exactly as [InvocationPlan] applies nothing. [ExecutionSession]
 * remains the only writer of node, frame and frontier state. It applies settled invoke marks before probing
 * a backend when no guest initializer needs scheduling; otherwise it applies the plan to a successor frame.
 *
 * **One nested `<clinit>` per plan, deliberately, and that is what makes divergence 4's ordering fall out
 * rather than be arranged.** [settled] is ordered ancestors-first and [context] owns the *topmost*
 * uninitialized class in the chain that has a guest `<clinit>` -- so a superclass runs strictly before its
 * subclass, and the trigger simply asks again when it resumes. The old engine gets this backwards: it
 * recurses over ancestors first (`VirtualMachine.kt:457-491`, the intent plainly correct) and then a LIFO
 * deque reverses it (`add` is `addLast`, `step()` takes `peekLast()`, `:145`), so a subclass `<clinit>` runs
 * before its superclass's and reads a static the superclass has not written yet -- a wrong constant in
 * exactly the constant-recovery case this engine exists for (adjudication 4). A plan carrying a LIST of
 * initializers would have had to be consumed in some order by the scheduler, i.e. it would have re-created
 * the choice the oracle got wrong; carrying one and re-asking cannot be ordered incorrectly.
 */
internal class ClassInitPlan(
    /**
     * Classes that become [ClassInitState.INITIALIZED] with **no** nested run, ancestors first: a class with
     * no declared `<clinit>`, or a framework class whose `<clinit>` this build declines to run (decision 7).
     */
    val settled: List<VirtualType>,
    /**
     * True when at least one class in [settled] **has** a `<clinit>` that was not run -- decision 7's
     * framework rule, and the reason the triggering address's side effect is raised to
     * [SideEffectLevel.STRONG]: whatever that initializer would have perturbed is unmodelled, so spec §2.2's
     * err-upward rule applies. False when every settled class simply has no initializer, because then nothing
     * was skipped and a spurious `STRONG` would stop every fold in the triggering method for no reason.
     */
    val skippedAnInitializer: Boolean,
    /** The guest `<clinit>` to run now and the requested classes waiting on it. */
    internal val context: ClassInitContext?,
    /** A prior erroneous ancestor that must fail the still-uninitialized descendants. */
    internal val dependencyFailure: ClassInitDependencyFailure? = null,
    /**
     * **JVMS §5.4.2 preparation**: the pre-`<clinit>` value of every static declared by every class this plan
     * moves out of [ClassInitState.UNINITIALIZED] -- [settled], the active initializer, and waiting
     * dependents -- keyed by
     * fully-qualified descriptor. Applied by [Frame.applyClassInit] through [ClassInitStore.prepare].
     *
     * Carried on the plan rather than computed by the store, for the two-tier reason: only
     * [ClassInitPlanner] may ask [ClassIndex] what a class declares. Carried for the initializer's class TOO,
     * not just the settled ones, because preparation precedes initialization -- see
     * [ClassInitStore.prepare].
     *
     * Ancestors-first insertion order, matching [settled], so a re-run is reproducible. Empty for a plan
     * that moves nothing.
     */
    internal val staticSeeds: Map<String, StaticCell> = emptyMap(),
) {
    /**
     * Nothing to do: every class in the chain is already past [ClassInitState.UNINITIALIZED].
     *
     * Unreachable from a correct trigger -- an op asks [ClassInitStore.actionFor] first and a callee entry is planned
     * only for an uninitialized defining class -- and the scheduler treats it as a loud invariant rather than
     * a tolerated no-op, because the *tolerant* handling of a no-op plan is an infinite loop: the trigger
     * would re-execute at its own address, ask again, and get the same answer.
     */
    val isNoOp: Boolean get() = settled.isEmpty() && context == null && dependencyFailure == null

    override fun toString(): String =
        "ClassInitPlan(settled=$settled, skippedAnInitializer=$skippedAnInitializer, " +
        "context=${context?.activeInitializer?.type}, dependencyFailure=${dependencyFailure?.failedAncestor}, " +
            "staticSeeds=$staticSeeds)"
}

/**
 * Decides what one class-init trigger needs, reading the triggering path's [ClassState] and the loaded class
 * set -- and touching neither. Reached through [Invoker.planClassInit], so "everything a call needs decided
 * before any code runs" stays one call site (spec §5.3): a `<clinit>` **is** nested method execution
 * (spec §11.3), so it must not get a private mini-interpreter or a private scheduling path.
 *
 * Lives in `core/` rather than `core/op/` for the two-tier reason [Invoker]'s KDoc gives: this is the other
 * operation that most wants to reach for `ClassManager`, and it reaches through [ClassIndex] only.
 */
internal class ClassInitPlanner(private val resolver: ProgramSnapshotResolver) {
    constructor(classIndex: ClassIndex) : this(classIndex.resolver(classIndex.currentSnapshot))


    /**
     * The plan for initializing [type] on the path [state] describes.
     *
     * Selects a failed superclass nearest to [type], so each descendant stores its superclass's outward
     * failure.
     *
     * Walks [type]'s superclass chain and answers about the **topmost** class still needing work, so
     * repeated calls initialize a hierarchy from the top down (see [ClassInitPlan]). Interfaces are
     * deliberately not walked: [ClassIndex.superclassOf] reads `BuilderClassDef.getSuperclass()`, which is
     * one string, and [JLS] §12.4.1 requires a superINTERFACE's initialization only for an interface
     * declaring a default method -- a shape dex-era smali rarely has. The oracle omits interfaces *and*
     * abstract ancestors (`VirtualMachine.kt:469-477`); abstract ancestors are ordinary links in this chain
     * and are therefore fixed here, which is the half of that defect that matters for statics. Full JLS 12.4
     * ordering remains a non-goal (spec §7).
     *
     * **Can throw**, and every caller is inside `ExecutionSession.contained` for it: [ClassIndex.superclassOf]
     * raises [ClassLoadFailure] for a class whose ancestors are not in the loaded set, which is routine in an
     * obfuscated APK and must abort the path rather than the run.
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
                    val dependents = chain.drop(index + 1).filter {
                        state.classInitAction(it) == ClassInitAction.StartInitialization
                    }
                    dependents.forEach { seeds.putAll(prepareForFutureInitialization(it)) }
                    return ClassInitPlan(
                        settled,
                        skippedAnInitializer,
                        context = null,
                        dependencyFailure = ClassInitDependencyFailure(ancestor, action.failure, dependents),
                        staticSeeds = seeds,
                    )
                }
                ClassInitAction.StartInitialization -> Unit
            }
            // A framework class is marked initialized and its `<clinit>` is NEVER run (decision 7). The
            // framework smali is 21,088 ordinary loaded classes, so without this row a `new-instance
            // Ljava/lang/Integer;` (Task 17's own corpus) would pull the real `Integer.<clinit>` into virtual
            // execution. Asked of `ClassIndex`, never of a live `type.*` handle -- the two-tier rule.
            //
            // Ordered BEFORE the `<clinit>` lookup so a framework class's initializer is never even resolved,
            // and the `hasClassInitializer` call below is what decides whether anything was actually skipped:
            // `Ljava/lang/Object;` declares no `<clinit>` at all, and it is on EVERY app class's chain, so
            // treating "framework" as "unmodelled" unconditionally would make every trigger in the system
            // report STRONG and stop every fold downstream of one.
            if (resolver.isFrameworkClass(ancestor)) {
                settled.add(ancestor)
                if (resolver.hasClassInitializer(ancestor)) skippedAnInitializer = true
                seeds.putAll(prepare(ancestor, initializerRuns = false))
                continue
            }
            when (val lookup = resolver.lookupClassInitializer(ancestor)) {
                // The common case, and it settles with no nested run at all (design decision 1).
                MethodLookup.NoSuchMethod -> {
                    settled.add(ancestor)
                    seeds.putAll(prepare(ancestor, initializerRuns = false))
                }
                is MethodLookup.Runnable -> {
                    seeds.putAll(prepare(ancestor, initializerRuns = true))
                    val dependents = chain.drop(index + 1).filter {
                        state.classInitAction(it) == ClassInitAction.StartInitialization
                    }
                    dependents.forEach { seeds.putAll(prepareForFutureInitialization(it)) }
                    return ClassInitPlan(
                        settled, skippedAnInitializer,
                        ClassInitContext(
                            type,
                            GuestInitializer(ancestor, lookup.view, ENTRY_ADDRESS),
                            dependents,
                        ),
                        staticSeeds = seeds,
                    )
                }
                // A DECLARED `<clinit>` with no code (`.method static constructor <clinit>()V` and nothing
                // else) or a class the model cannot load at all. Neither is reachable through a well-formed
                // input on a chain `superclassOf` just walked successfully, and both are settled
                // CONSERVATIVELY -- marked initialized so the trigger can proceed, with `STRONG` declared
                // because an initializer that exists and did not run is exactly the unmodelled case.
                is MethodLookup.NoBody, is MethodLookup.DefaultConflict, MethodLookup.NoSuchClass -> {
                    settled.add(ancestor)
                    skippedAnInitializer = true
                    seeds.putAll(prepare(ancestor, initializerRuns = false))
                }
            }
        }
        return ClassInitPlan(settled, skippedAnInitializer, context = null, staticSeeds = seeds)
    }

    private fun prepareForFutureInitialization(type: VirtualType): Map<String, StaticCell> {
        if (resolver.isFrameworkClass(type)) return prepare(type, initializerRuns = false)
        return when (resolver.lookupClassInitializer(type)) {
            is MethodLookup.Runnable -> prepare(type, initializerRuns = true)
            MethodLookup.NoSuchMethod -> prepare(type, initializerRuns = false)
            is MethodLookup.NoBody, is MethodLookup.DefaultConflict, MethodLookup.NoSuchClass ->
                prepare(type, initializerRuns = false)
        }
    }

    /**
     * [type]'s statics as they stand **after preparation and before its `<clinit>`** -- JVMS §5.4.2, and
     * adjudication 5's fix in three rows.
     *
     *  1. **A dex-encoded literal is that literal, always** -- app class or framework class, and this is
     *     adjudication 5: *"dex encodes static initial values in the class definition, and for `static final`
     *     constants the compiler emits NO `<clinit>` assignment at all"*, so the oracle's rule of seeding
     *     literals only for a class *without* a `<clinit>` (`VirtualMachine.kt:479-487` vs `:549-555`)
     *     **discards constants that are sitting right there in the dex** and reads them back as `Unknown` with
     *     an *"undefined field"* log (`ExecutionState.kt:258-262`). Seeded for every class here. It is also
     *     what recovers `Ljava/lang/Integer;->MAX_VALUE:I` without any host reflection -- decision 7's
     *     *"cheap part"*.
     *  2. **Otherwise the JVM type DEFAULT, when the class's own initializer either does not exist or is about
     *     to run** ([initializerRuns]). Both cases make the default a PROVABLE fact rather than a guess: with
     *     no `<clinit>` nothing will ever assign the field, and with one about to run the default is genuinely
     *     what it observes on entry. This is what `SGetOpTest.staticBooleanUninitialized` asserts (`false`, not
     *     `Unknown`) and it is the oracle's answer too, for a class with no `<clinit>`.
     *  3. **Otherwise [Value.Unknown]** -- a **framework** class whose `<clinit>` this build declines to run
     *     (resolved decision 7), or an app class whose declared `<clinit>` has no body. An initializer that
     *     exists and did not run may have computed anything, so the default would be a fabricated constant.
     *     This is the *only* row that answers `Unknown`, and the reason [StaticFieldDeclaration] keeps a
     *     field's literal separate from its type default: with the oracle's merged accessor this row would
     *     fold `0` for every framework static a real initializer computes.
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
     * [type] and its superclasses, **most ancestral first**. Terminates at the first class with no
     * superclass, which [ClassIndex.superclassOf] answers for `Ljava/lang/Object;`, for arrays (which get
     * `Ljava/lang/Object;`, so the chain is two long and neither link has a `<clinit>`) and for primitives.
     */
    private fun chainFromTheTop(type: VirtualType): List<VirtualType> {
        val chain = ArrayList<VirtualType>(3)
        var current: VirtualType? = type
        while (current != null && current !in chain) {
            chain.add(current)
            current = resolver.superclassOf(current)
        }
        return chain.asReversed()
    }

    private companion object {
        /** A DEX method's code starts at address 0; see the bytecode backends' matching constant. */
        const val ENTRY_ADDRESS = 0
    }
}
