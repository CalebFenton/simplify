package org.cf.smalivm.core

import java.lang.reflect.Modifier
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.util.ClassNameUtils
import com.android.tools.smali.dexlib2.ValueType
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.Annotation
import com.android.tools.smali.dexlib2.iface.Field
import com.android.tools.smali.dexlib2.iface.value.BooleanEncodedValue
import com.android.tools.smali.dexlib2.iface.value.ByteEncodedValue
import com.android.tools.smali.dexlib2.iface.value.CharEncodedValue
import com.android.tools.smali.dexlib2.iface.value.DoubleEncodedValue
import com.android.tools.smali.dexlib2.iface.value.FloatEncodedValue
import com.android.tools.smali.dexlib2.iface.value.IntEncodedValue
import com.android.tools.smali.dexlib2.iface.value.LongEncodedValue
import com.android.tools.smali.dexlib2.iface.value.MethodEncodedValue
import com.android.tools.smali.dexlib2.iface.value.ShortEncodedValue
import com.android.tools.smali.dexlib2.iface.value.StringEncodedValue
import com.android.tools.smali.dexlib2.iface.value.TypeEncodedValue

/**
 * The class model could not answer a question about a **well-formed** descriptor, because the class
 * behind it is not in the loaded set (or its own ancestors are not) -- the normal case in a real
 * obfuscated APK, where a catch clause or a field type routinely names a class the input never
 * shipped. This is a statement about the *input*, not about the engine.
 *
 * It exists so that callers can tell "the class model cannot answer" apart from "something in the
 * engine is broken". Without it, both arrive as a bare `RuntimeException` (`ClassManager.parseClass`
 * wraps every load failure in one) and a caller that wants to answer the first conservatively is
 * forced to swallow the second too -- turning a genuine bug into silent over-approximation with a
 * `Complete` completion status and nothing recording why precision was lost. [ExecutionSession]'s
 * handler search is the caller this was added for (Task 7's ambiguity decision).
 */
class ClassLoadFailure internal constructor(message: String, cause: Throwable) : RuntimeException(message, cause)

/** Resolved identity and declared signature, available whether the method has executable bytecode or not. */
class ResolvedMethod internal constructor(
    val target: MethodDescriptor,
    val returnType: VirtualType,
    val parameterTypes: List<VirtualType>,
    val isSynchronized: Boolean,
)

/**
 * What looking a method reference up in the loaded class set found -- see [ClassIndex.lookupMethod].
 *
 * Sealed rather than a nullable [MethodView], so a consumer cannot answer different guest outcomes with one
 * branch.
 */
sealed interface MethodLookup {
    /** Present, with a body. [view] is what a nested activation runs. */
    class Runnable(val view: MethodView, val method: ResolvedMethod) : MethodLookup

    /**
     * The defining class is not in the loaded set. A guest `NoClassDefFoundError` -- a `LinkageError`, so an
     * **`Error`** and not catchable by a guest `catch (Exception)`. Measured across the whole family
     * (`docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once`), including `invoke`
     * specifically: `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)`, `tools/art-runner/fixtures/Fx_InvokeAbsentType.smali`.
     */
    data object NoSuchClass : MethodLookup

    /**
     * The class is loaded and declares no such member: **plan adjudication 7b**, and a guest
     * `NoSuchMethodError` -- also a `LinkageError`/`Error`, so the same family and the same handler
     * consequence as [NoSuchClass]. Measured, not inherited: `[DEVICE](Tier A, -Xint, art_standalone
     * 0_git20251009-r2)` cross-validated `[DEVICE](Tier B, nterp default, API 36)`,
     * `tools/art-runner/fixtures/Fx_InvokeMissingMethod.smali`. **And it precedes the null-receiver NPE**
     * (`Fx_InvokeMissingMethodNullReceiver.smali`, same run) -- resolution first, exactly as for
     * `check-cast`/`instance-of`, so Task 20 checks the target before the receiver.
     *
     * The oracle instead crashes the HOST at op-build time (`opcode/InvokeOp.kt:311`), receiver-independent,
     * which is why nothing in the corpus pins a guest answer here.
     */
    data object NoSuchMethod : MethodLookup

    /**
     * Declared, but with no code: abstract, native, a framework declaration -- **or an array pseudo-method**
     * (`[I->clone()`), which is a runtime intrinsic with no dex body anywhere (adjudication 7b(d); see
     * [ClassIndex.lookupMethod]'s array row). Not a resolution failure -- Plan 11's
     * `EmulateBackend`/`ReflectBackend` are what run these, and answering [NoSuchMethod] instead would invent
     * a linking error a device never raises.
     */
    class NoBody(val method: ResolvedMethod) : MethodLookup

    /**
     * Selection found more than one maximally-specific non-abstract interface method (JVMS 5.4.3.3). ART links
     * a conflict method in its place, and invoking it throws `IncompatibleClassChangeError` after the receiver
     * null check (`ArtMethod::ThrowInvocationTimeError`). [method] is named on the selecting class.
     */
    class DefaultConflict(val method: ResolvedMethod) : MethodLookup
}

/** Kind-correct invocation resolution, before backend selection or receiver-null handling. */
sealed interface InvocationLookup {
    class Resolved(val lookup: MethodLookup, val candidates: Set<String> = emptySet()) : InvocationLookup
    class Ambiguous(val candidates: Set<String>) : InvocationLookup
}

/**
 * What looking a **static field** reference up in the loaded class set found -- see
 * [ClassIndex.resolveStaticField] for the walk and for why the key is the DEFINING class's descriptor
 * rather than a bare name.
 *
 * Sealed for [MethodLookup]'s reason: the two failures are different guest errors with different tiers, so a
 * caller must not be able to answer both with one branch. There is deliberately **no** `NoBody`
 * twin -- a field has no body to be missing.
 */
sealed interface StaticFieldLookup {
    /**
     * Present. [descriptor] is the FULLY-QUALIFIED descriptor of the class that **declares** the field
     * (`Lcom/example/Foo;->bar:I`), which may differ from the class the instruction named: `sget v0,
     * Lchild;->parentField:I` resolves to `Lparent;->parentField:I`. [definingClass] is that declaring
     * class, and it is the class a trigger must initialize ([JVMS] §5.5).
     */
    class Resolved internal constructor(
        val definingClass: VirtualType,
        val descriptor: String,
        val type: VirtualType,
        val isVolatile: Boolean,
    ) : StaticFieldLookup {
        override fun toString(): String = descriptor
    }

    /**
     * A class the resolution needed is not in the loaded set -- the referenced class itself, or an ancestor
     * reached while walking for an inherited static. A guest `NoClassDefFoundError`, and **measured for
     * `sget` specifically**: `Fx_SGetAbsentType` against `Fx_SGetPresentType`, `[DEVICE](Tier A, -Xint,
     * art_standalone 0_git20251009-r2)` cross-validated `[DEVICE](Tier B, nterp default, API 36)`
     * (`docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once`). `sput` rides on that same
     * measurement -- same symbolic field reference, same resolution path -- which that document records
     * rather than glosses.
     */
    class NoSuchClass internal constructor(val type: VirtualType) : StaticFieldLookup

    /**
     * Every class and interface the search reaches is loaded, and none declares a static field of this name
     * AND type.
     *
     * A guest `NoSuchFieldError` -- a `LinkageError`, so an **`Error`** outside a guest `catch (Exception)`,
     * exactly like [MethodLookup.NoSuchMethod]'s `NoSuchMethodError`. **Tier: INHERITED** ([JVMS] §5.4.3.2
     * field resolution, whose failure is §5.4.3.1's `LinkageError`; `getstatic`'s own Linking Exceptions
     * clause names `NoSuchFieldError`). **NOT measured** -- the method twin is
     * (`Fx_InvokeMissingMethod`), this one has no fixture, and the honest tier is the weaker one. What it
     * replaces is the oracle's `fieldClass.getField(name)!!` (`opcode/SGetOp.kt:52`), a host `NullPointerException`
     * at op-build time that kills the whole run.
     *
     * [descriptor] is what the INSTRUCTION named, not a resolved field: there is none.
     */
    class NoSuchField internal constructor(val descriptor: String) : StaticFieldLookup
}

/** Result of resolving an instance field reference by owner, name, and type. */
sealed interface InstanceFieldLookup {
    class Resolved internal constructor(
        val definingClass: VirtualType,
        val descriptor: String,
        val type: VirtualType,
        val isVolatile: Boolean,
    ) : InstanceFieldLookup {
        override fun toString(): String = descriptor
    }

    class NoSuchClass internal constructor(val type: VirtualType) : InstanceFieldLookup

    class NoSuchField internal constructor(val descriptor: String) : InstanceFieldLookup
}

/**
 * One **declared** static field of one class: its fully-qualified [descriptor], its declared [type], and its
 * compile-time constant [literal] if the dex encodes one (`.field public static myInt:I = 0x42`), else null.
 *
 * **[literal] is deliberately NOT pre-merged with the type default, and that is the whole point of this
 * type.** The oracle's `type/VirtualField.initialValue` returns the literal *or* the JVM type default from one
 * accessor, so a caller cannot tell "the dex says 0x42" from "nothing says anything, so 0". Plan 9 needs them
 * apart: for an application class both become a definite value, but for a **framework** class whose `<clinit>`
 * this build declines to run (resolved decision 7) only the literal is knowable and everything else must read
 * [Value.Unknown]. Conflating them there would fold `0` for a framework static a real `<clinit>` computes.
 */
class StaticFieldDeclaration internal constructor(
    val descriptor: String,
    val type: VirtualType,
    val literal: Value?,
    val modifiers: Int,
) {
    override fun toString(): String = "$descriptor${literal?.let { " = $it" } ?: ""}"
}

private fun StaticFieldDeclaration.toFieldDeclaration(declaringType: VirtualType): FieldDeclaration =
    FieldDeclaration(
        declaringType,
        descriptor.substringAfter("->").substringBefore(':'),
        descriptor,
        type,
        modifiers,
    )

private fun InstanceFieldDeclaration.toFieldDeclaration(declaringType: VirtualType): FieldDeclaration =
    FieldDeclaration(
        declaringType,
        descriptor.substringAfter("->").substringBefore(':'),
        descriptor,
        type,
        modifiers,
    )

/** One declared instance field. Instance fields have no dex-encoded initial value. */
class InstanceFieldDeclaration internal constructor(
    val descriptor: String,
    val type: VirtualType,
    val modifiers: Int,
)

/** Common immutable metadata used by reflective field lookup. */
internal data class FieldDeclaration(
    val declaringType: VirtualType,
    val name: String,
    val descriptor: String,
    val type: VirtualType,
    val modifiers: Int,
)

/**
 * The core's single bridge to the class/type model (spec §12 "as built"). Everything the engine
 * needs to know about classes -- descriptor validation, subtyping, method lookup -- comes through
 * here, so the algebra in [Value]/[ValueJoin]/[HandlerResolution] never imports an oracle type and
 * survives the oracle's deletion in Plan 16 (only this file's internals change).
 *
 * Implements Boolean [TypeHierarchy] for ordinary value operations. [throwableHierarchy] is a
 * separate non-throwing capability for handler resolution. [Superclasses] serves [joinTypes], and
 * [InstanceOfTest] serves [castOutcome]. Each capability states one question and its failure policy.
 */
class ClassIndex private constructor(
    private val classManager: ClassManager,
    private val dynamicLoadingEvidenceOverride: Boolean?,
    hierarchyComplete: Boolean?,
    internal val programWorkspace: ProgramWorkspace,
    private val capturedSnapshot: ProgramSnapshot?,
    private val capturedClassLoader: SmaliClassLoader?,
) : TypeHierarchy, Superclasses, InstanceOfTest {

    constructor(
        classManager: ClassManager,
        dynamicLoadingEvidence: Boolean? = null,
        hierarchyComplete: Boolean? = null,
    ) : this(
        classManager,
        dynamicLoadingEvidence,
        hierarchyComplete,
        InitialProgramImporter.import(classManager),
        null,
        null,
    )

    internal constructor(
        classManager: ClassManager,
        programWorkspace: ProgramWorkspace,
        dynamicLoadingEvidence: Boolean? = null,
        hierarchyComplete: Boolean? = null,
    ) : this(
        classManager,
        dynamicLoadingEvidence,
        hierarchyComplete,
        programWorkspace,
        null,
        null,
    )

    internal val currentSnapshot: ProgramSnapshot get() = capturedSnapshot ?: programWorkspace.currentSnapshot
    private val hasDynamicLoadingEvidence: Boolean
        get() = dynamicLoadingEvidenceOverride ?: currentSnapshot.dynamicLoadingEvidence.isPresent
    private val hasCompleteHierarchy: Boolean = hierarchyComplete ?: scanHierarchyCompleteness()
    internal val throwableHierarchy: ThrowableHierarchy get() = currentSnapshot.hierarchy.throwableHierarchy

    /** Validated core type for a Dalvik descriptor. Throws if the descriptor is malformed. */
    fun typeOf(descriptor: String): VirtualType = VirtualType.of(descriptor)

    /**
     * True if [candidate] is [ancestor] or inherits from it. Primitives (and `void`) relate only
     * to themselves: there is no primitive/reference bridging, matching the value lattice, where
     * such a pair joins to `Top` (spec §4.1).
     *
     * Throws [ClassLoadFailure] when the class model cannot load a class it needs to answer -- either
     * operand, or any ancestor reached while walking (`VirtualClass.getImmediateAncestors` resolves
     * superclasses and interfaces lazily, so an absent *superclass* fails here just as an absent
     * operand does, and the translation has to cover the whole walk rather than only the two
     * lookups). Anything else escapes untranslated on purpose: only failures from inside the class
     * model become [ClassLoadFailure], so a bug in this method's own lattice logic still reaches the
     * caller as itself. Handler resolution does not use this throwing capability.
     */
    override fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean {
        if (candidate == ancestor) return true
        if (!candidate.isReference || !ancestor.isReference) return false
        if (!isLoadable(candidate) || !isLoadable(ancestor)) {
            throw ClassLoadFailure(
                "Class model cannot answer whether ${candidate.descriptor} is a subtype of ${ancestor.descriptor}",
                IllegalStateException("A type is absent from the program snapshot"),
            )
        }
        if (ancestor == VirtualType.OBJECT) return true
        if (candidate.isArray) return isArraySubtype(candidate, ancestor)
        val pending = ArrayDeque<String>()
        val seen = mutableSetOf<String>()
        pending.addLast(candidate.descriptor)
        while (pending.isNotEmpty()) {
            val descriptor = pending.removeFirst()
            if (!seen.add(descriptor)) continue
            if (descriptor == ancestor.descriptor) return true
            val declaration = currentSnapshot.hierarchy.declarations[descriptor] ?: throw ClassLoadFailure(
                "Class model cannot answer whether ${candidate.descriptor} is a subtype of ${ancestor.descriptor}",
                IllegalStateException("No declaration for $descriptor in the program snapshot"),
            )
            declaration.superclass?.let(pending::addLast)
            declaration.interfaces.forEach(pending::addLast)
        }
        return false
    }

    private fun isArraySubtype(candidate: VirtualType, ancestor: VirtualType): Boolean {
        if (ancestor.descriptor in ARRAY_SUPERINTERFACES) return true
        if (!ancestor.isArray) return false
        val candidateComponent = requireNotNull(candidate.componentType)
        val ancestorComponent = requireNotNull(ancestor.componentType)
        if (!candidateComponent.isReference || !ancestorComponent.isReference) {
            return candidateComponent == ancestorComponent
        }
        return isSubtypeOf(candidateComponent, ancestorComponent)
    }

    /** True when both loaded DEX classes have the same enclosing-class root. */
    internal fun areNestmates(left: VirtualType, right: VirtualType): Boolean {
        if (left == right) return true
        val leftHost = dexNestHost(left) ?: return false
        return leftHost == dexNestHost(right)
    }

    private fun dexNestHost(type: VirtualType): VirtualType? {
        if (!type.isReference || type.isArray || !isLoadable(type)) return null
        var current = type
        val seen = mutableSetOf<VirtualType>()
        while (seen.add(current)) {
            val annotations: Set<Annotation> = currentSnapshot.applicationClass(current.descriptor)?.annotations
                ?: currentSnapshot.frameworkCatalog.classDefinition(current.descriptor)?.annotations
                ?: return null
            val enclosing = annotations.firstNotNullOfOrNull { annotation: Annotation ->
                when (annotation.type) {
                    "Ldalvik/annotation/EnclosingClass;" ->
                        (annotation.elements.singleOrNull()?.value as? TypeEncodedValue)?.value
                    "Ldalvik/annotation/EnclosingMethod;" ->
                        (annotation.elements.singleOrNull()?.value as? MethodEncodedValue)?.value?.definingClass
                    else -> null
                }
            } ?: return current
            current = VirtualType.of(enclosing)
        }
        return null
    }

    /**
     * `instance-of`/`check-cast`'s question: **is a value of type [candidate] an instance of [target]**
     * at runtime? A different question from [isSubtypeOf] with a different contract, and the two are
     * deliberately not one method with a mode flag (plan design decision 4). Two differences are pinned by
     * `ClassIndexTest.aPrimitiveReflexivePairIsWhereTheTwoPredicatesDisagreeMostVisibly`: the reflexive
     * primitive pair, and an unloadable class against `Ljava/lang/Object;`.
     *
     * Rules, in order, and the order is load-bearing:
     *  1. **A primitive or `void` target is always `false`** -- **SPEC**, [BC]'s `instance-of` row: *"this
     *     will always result in `0` being stored if C refers to a primitive type"*. Before the equality row,
     *     so the reflexive primitive pair answers `false` where the oracle's
     *     `type/VirtualPrimitive.instanceOf` (`:35-40`, `equals`-only, and its own comment says the method
     *     exists to decide array storability) answers `true`.
     *
     *     **What Task 9 must do with this `false`, because getting it wrong is a soundness defect in the
     *     invented-live-path direction.** For `check-cast` the same `false` means **`Never`, a DEFINITE
     *     `ClassCastException`** -- not "unprovable". [BC]'s `check-cast` row is explicit, one table row
     *     above the `instance-of` note quoted here: *"Note: Since A must always be a reference (and not a
     *     primitive value), this will necessarily fail at runtime (that is, it will throw an exception) if B
     *     refers to a primitive type."* Answering `Unprovable` here would make `check-cast v0, I` fork a
     *     fall-through path Dalvik cannot take -- the mirror of the defect row 4 fixes. **SPEC, so it is not
     *     a judgement call.** One caveat that is already in the registry and does not weaken this: the note
     *     is false when the register reads as **null**, because ART's null short-circuit precedes the cast
     *     test (`DALVIK-SEMANTICS.md#check-cast-21c`, and `interpreter_switch_impl-inl.h:826-828` before
     *     `:830`) -- which is exactly why Plan 8's Global Constraints order `check-cast`'s
     *     `readsAsNullReference` row BEFORE the type test. Once that row has fired, a primitive target is
     *     `Never`.
     *  2. Equal types are instances of each other, with no class load.
     *  3. **Every reference is an instance of `Ljava/lang/Object;`, with no class load** -- INHERITED
     *     ([JVMS] `checkcast`: *"If T is a class type, then T must be `Object`"*). This is the row
     *     [isSubtypeOf] cannot serve: an obfuscated APK names classes it does not ship, and turning a
     *     cheap `true` into a [ClassLoadFailure] would make the commonest shape unanswerable. The oracle
     *     has the same short-circuit for classes (`type/VirtualClass.java:146-148`) and reaches it for
     *     arrays only via a synthesized ancestor.
     *  4. **Every array is an instance of `Ljava/lang/Cloneable;` and `Ljava/io/Serializable;` -- a
     *     DELIBERATE DIVERGENCE from the oracle, which omits both.** INHERITED: JLS §4.10.3 lists the two
     *     interfaces arrays implement and lists the row for primitive components explicitly (quoted at
     *     `docs/DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`), corroborated by Android's own
     *     toolchain -- AOSP `Merger.isPossiblyAssignableFrom` says the model *"does not assume anything
     *     about class hierarchy other than that the type `Object` is the supertype of all reference types
     *     and all arrays are assignable to `Serializable` and `Cloneable`"*, and its array branch reads
     *     *"Other than Object (handled above), array types are assignable only to Serializable and
     *     Cloneable"*. The oracle's `VirtualArray.instanceOf` (`type/VirtualArray.java:129-141`) is
     *     `equals` plus `getAncestors()` membership, and `getImmediateAncestors()` (`:52-74`) adds only
     *     `[^(rank-1)Ljava/lang/Object;` for a primitive component and arrays-of-component-ancestors for a
     *     reference one -- neither branch ever adds either interface (`type-and-dex.md:95`, `:203`, and
     *     `:234` records that `type/VirtualArrayTest` **encodes** the omission, so prior art here looks
     *     like confirmation rather than a warning). Both wrong answers are foldable `Known`s:
     *     `instance-of v1, v0, Ljava/io/Serializable;` on an `int[]` folds `Known(false, Z)` where a device
     *     answers `1`, and `check-cast v0, Ljava/lang/Cloneable;` on one invents a definite
     *     `ClassCastException`. Registered as `types-int-array-is-cloneable` in `differential/Fixtures.kt`
     *     and **never to be reconciled toward the oracle.** Placed before the delegation so an array of an
     *     absent component class still answers, which needs no load at all.
     *  5. A primitive [candidate] against a reference target is `false`: a register whose last write was
     *     primitive reads as a null reference (see `Value.readsAsNullReference`), and null is an instance
     *     of nothing. Tier: **[ART] under both interpreters** where those bits are zero, **VERIFIER** (spec
     *     §2.2, verifier-rejected input) where they are not -- that predicate's KDoc carries the split.
     *  6. Otherwise [isSubtypeOf] walks the captured hierarchy, including interfaces and array covariance.
     *     `instance-of` consults interfaces; [joinTypes] excludes them.
     *
     * **Not total, and Tasks 8/9 must treat the failure as AMBIGUITY rather than as `false`. This is a
     * SEPARATE rule from row 1's, with separate grounding -- do not merge them.** Row 6 walks the
     * candidate's ancestors, so an absent superclass raises [ClassLoadFailure] here exactly as in
     * [isSubtypeOf]. Nothing in [BC] speaks to an unanswerable subtype question, so the answer comes from
     * spec §2.2 alone: an unprovable value answers `Unknown` and unprovable feasibility explores more.
     * Absorbing the failure into `false` would fold a wrong constant for `instance-of` and invent a definite
     * `ClassCastException` for `check-cast`, so the sound consumption is `Unknown(Z)` and a FORK
     * respectively. Row 1's `false`, by contrast, is [BC]-grounded and **is** definite -- see above.
     */
    override fun isInstanceOf(candidate: VirtualType, target: VirtualType): Boolean {
        if (!target.isReference) return false
        if (candidate == target) return true
        if (target == VirtualType.OBJECT) return candidate.isReference
        if (candidate.isArray && target.descriptor in ARRAY_SUPERINTERFACES) return true
        if (!candidate.isReference) return false
        return isSubtypeOf(candidate, target)
    }

    /**
     * The direct superclass of [type], for [joinTypes]' nearest-common-ancestor walk. See [Superclasses]
     * for why the JOIN reads a chain instead of an ancestor set, and [joinTypes] for what it does with the
     * failure this can throw.
     *
     * **Read from `BuilderClassDef.getSuperclass()`, which is one string.** That is the whole determinism
     * argument, at its source: there is no `HashSet` to iterate, no flattened set mixing interfaces in with
     * the superclass, and no live mutable cache to retain or reorder
     * (`docs/architecture/type-and-dex.md:93`, `:161`, `:216`). `VirtualClass.getImmediateAncestors()`
     * builds exactly this string plus the interfaces into an unordered set; this method takes the one link
     * the set was built from.
     *
     * `null` for `Ljava/lang/Object;`, for primitives and for `void` -- all three genuinely have no
     * superclass, which [joinTypes] reads as a complete chain. Arrays answer `Ljava/lang/Object;`
     * (JLS §4.10.3's *"Object >₁ P[]"*, and the same for a reference component): an array's array-typed
     * supertypes are a **covariance relation, not a chain**, and [joinTypes] handles them component-wise.
     * Object and the array rows are answered without any load, so `[Lobf/absent;` still has a superclass.
     */
    override fun superclassOf(type: VirtualType): VirtualType? {
        if (!type.isReference) return null
        if (type.isArray) return VirtualType.OBJECT
        if (type == VirtualType.OBJECT) return null
        val declaration = currentSnapshot.hierarchy.declarations[type.descriptor] ?: throw ClassLoadFailure(
            "Class model cannot answer what ${type.descriptor} extends",
            IllegalStateException("No declaration for ${type.descriptor} in the program snapshot"),
        )
        val superclass = declaration.superclass
        return superclass?.let { VirtualType.of(it) }
    }

    /**
     * Whether the class model can resolve [type]'s component base -- i.e. whether the array class
     * `new-array` names actually exists in the loaded set.
     *
     * Primitives (and any array of them) are always loadable: there is no class to find. A reference base
     * is loadable iff the model can produce it without raising, which is the same `RuntimeException`
     * boundary [isSubtypeOf] translates into [ClassLoadFailure] -- and unlike there, the answer here is a
     * plain boolean rather than an exception, because the caller's decision is "does Dalvik throw" and not
     * "is the subtype question answerable".
     *
     * **A `false` is a FACT about this input, not an unknown, and that is a spec §3 consequence rather
     * than an assumption:** multidex is a non-goal, so the loaded class set is the whole world. That is
     * what lets `new-array` throw definitely instead of forking, and it is where this differs from Plan 3's
     * unloadable *catch* type, whose fork is right because there the THROWN VALUE's type was unknown.
     */
    fun isLoadable(type: VirtualType): Boolean {
        var base = type
        while (base.isArray) base = base.componentType!!
        if (!base.isReference) return true
        return currentSnapshot.hierarchy.contains(base.descriptor)
    }

    /**
     * The single class loader for host-`Class` resolution. Today its one consumer is `const-class`
     * (Plan 8 Task 10, through [loadClassConstant]); Plan 11's `ReflectBackend` -- which hands `Class`
     * objects to host methods -- is the next. **Exposed so a test seeds or expects `Class` objects through
     * the SAME loader the engine resolves through:** a second `SmaliClassLoader` instance would `defineClass`
     * a *distinct* `Class` for one guest class, so identity comparisons (`const-class`'s value equality, and
     * two `const-class` on one class yielding one reference) would spuriously fail. Lazy because most runs
     * never touch a host class at all -- a tracked array or object needs a [VirtualType], not a `Class`.
     */
    // `internal`, not `public`: its only `core/main` consumer is [loadClassConstant] below, and the test
    // source set sees `internal` main declarations (so `CoreTester` and the differential adapter still reach
    // it). Kept as narrow as the clean-core surface allows.
    internal val classLoader: SmaliClassLoader by lazy { capturedClassLoader ?: SmaliClassLoader(classManager) }

    internal fun resolver(snapshot: ProgramSnapshot): ProgramSnapshotResolver {
        require(snapshot.programIdentity === programWorkspace.programIdentity) {
            "Cannot resolve a snapshot from another program workspace"
        }
        val capturedIndex = ClassIndex(
            classManager,
            dynamicLoadingEvidenceOverride,
            hasCompleteHierarchy,
            programWorkspace,
            snapshot,
            classLoader,
        )
        return ProgramSnapshotResolver(snapshot, capturedIndex)
    }

    /**
     * The host `Class` that [descriptor] names, loaded by exact binary name with `Class.forName(name, false,
     * loader)` semantics, or `null` when [classLoader] cannot produce it.
     *
     * `null` does not mean the class is absent: a class in the model that this host loader cannot define (an
     * Android-only `java.*` class, which [SmaliClassLoader] refuses and the JVM parent lacks) also answers
     * `null`, and callers ask [isLoadable] to tell the two apart. A [LinkageError] while defining the class is
     * the same host-loader limitation and also answers `null`.
     *
     * Loading never runs `<clinit>`: ART's `const-class` handler resolves the type and stores the reference,
     * and `Class.forName` initialization is the caller's guest-visible step.
     */
    fun loadClassConstant(descriptor: String): Class<*>? {
        if (descriptor.length == 1) {
            return if (descriptor == "V") Void.TYPE else ClassNameUtils.getPrimitiveClass(descriptor)
        }
        return try {
            Class.forName(ClassNameUtils.internalToBinary(descriptor), false, classLoader)
        } catch (_: ClassNotFoundException) {
            null
        } catch (_: LinkageError) {
            null
        }
    }

    /**
     * Looks [methodDescriptor] up from [definingClass]: the class and its superclasses, then the
     * maximally-specific superinterface methods. It answers symbolic resolution and, given a receiver's class,
     * virtual selection.
     *
     * An absent class and an absent member are the guest `NoClassDefFoundError` and `NoSuchMethodError`
     * resolution failures. A declared method with no body (abstract, native, a framework declaration, an array
     * pseudo-method) is not a failure; a backend executes it. A `<clinit>` is not inherited, so class
     * initialization asks [lookupClassInitializer] instead.
     *
     * Views come from the captured immutable snapshot, so a session keeps resolving the revision it started on.
     */
    fun lookupMethod(definingClass: VirtualType, methodDescriptor: String): MethodLookup {
        if (!isLoadable(definingClass)) return MethodLookup.NoSuchClass
        // An array's members are `length`, `clone()` and Object's members (JLS 10.7), so existence is asked of
        // Object. An existing member answers NoBody rather than Object's body: array `clone()` is a runtime
        // intrinsic that Object.clone()'s framework bytecode cannot perform.
        if (definingClass.isArray) {
            val search = findSnapshotMethod(VirtualType.OBJECT, methodDescriptor)
            return if (search.method != null) {
                MethodLookup.NoBody(resolvedSnapshotMethod(search.method, definingClass))
            } else if (search.missingClass) {
                MethodLookup.NoSuchClass
            } else {
                MethodLookup.NoSuchMethod
            }
        }
        val search = findSnapshotMethod(definingClass, methodDescriptor)
        if (search.missingClass) return MethodLookup.NoSuchClass
        val method = search.method ?: return MethodLookup.NoSuchMethod
        if (search.defaultConflict) return MethodLookup.DefaultConflict(resolvedSnapshotMethod(method, definingClass))
        return lookupOf(method)
    }

    /** Current immutable application view for one full method signature, or null for bodyless/framework methods. */
    fun methodView(methodSignature: String): MethodView? {
        val method = currentSnapshot.applicationMethod(methodSignature) ?: return null
        if (method.body == null) return null
        return MethodView.of(method, currentSnapshot.programRevision)
    }

    fun requireMethodView(className: String, methodDescriptor: String): MethodView =
        requireNotNull(methodView("$className->$methodDescriptor")) {
            "No concrete application method: $className->$methodDescriptor"
        }

    private sealed interface SnapshotMethod {
        val declaration: com.android.tools.smali.dexlib2.immutable.ImmutableMethod

        class Application(val definition: ApplicationMethodDefinition) : SnapshotMethod {
            override val declaration get() = definition.declaration
        }

        class Framework(val definition: FrameworkMethodDefinition) : SnapshotMethod {
            override val declaration get() = definition.declaration
        }
    }

    private data class SnapshotMethodSearch(
        val method: SnapshotMethod?,
        val missingClass: Boolean,
        val defaultConflict: Boolean = false,
    )

    /**
     * JVMS 5.4.3.3 over the snapshot: the class and its superclasses first, then the maximally-specific
     * superinterface methods, which exclude private and static interface methods. A single non-abstract
     * maximal method is the answer; several are a [SnapshotMethodSearch.defaultConflict]; with none, an
     * abstract maximal method answers.
     */
    private fun findSnapshotMethod(definingClass: VirtualType, descriptor: String): SnapshotMethodSearch {
        val interfaces = ArrayDeque<String>()
        val visitedClasses = mutableSetOf<String>()
        var owner: String? = definingClass.descriptor
        while (owner != null && visitedClasses.add(owner)) {
            declaredSnapshotMethod(owner, descriptor)?.let { return SnapshotMethodSearch(it, false) }
            val hierarchy = currentSnapshot.hierarchy.declarations[owner]
                ?: return SnapshotMethodSearch(null, true)
            hierarchy.interfaces.forEach(interfaces::addLast)
            owner = hierarchy.superclass
        }

        val candidates = LinkedHashMap<String, SnapshotMethod>()
        val visitedInterfaces = mutableSetOf<String>()
        var missingClass = false
        while (interfaces.isNotEmpty()) {
            val current = interfaces.removeFirst()
            if (!visitedInterfaces.add(current)) continue
            declaredSnapshotMethod(current, descriptor)
                ?.takeIf { !AccessFlags.PRIVATE.isSet(it.declaration.accessFlags) }
                ?.takeIf { !AccessFlags.STATIC.isSet(it.declaration.accessFlags) }
                ?.let { candidates[current] = it }
            val hierarchy = currentSnapshot.hierarchy.declarations[current]
            if (hierarchy == null) {
                missingClass = true
            } else {
                hierarchy.interfaces.forEach(interfaces::addLast)
            }
        }
        val maximal = candidates.filterKeys { candidate ->
            candidates.keys.none { other -> other != candidate && isSubinterface(other, candidate) }
        }.values
        val nonAbstract = maximal.filterNot { AccessFlags.ABSTRACT.isSet(it.declaration.accessFlags) }
        return when (nonAbstract.size) {
            0 -> SnapshotMethodSearch(maximal.firstOrNull(), missingClass)
            1 -> SnapshotMethodSearch(nonAbstract.single(), missingClass)
            else -> SnapshotMethodSearch(nonAbstract.first(), missingClass, defaultConflict = true)
        }
    }

    private fun declaredSnapshotMethod(owner: String, descriptor: String): SnapshotMethod? {
        currentSnapshot.applicationClass(owner)?.methodsByDescriptor?.get(descriptor)?.let {
            return SnapshotMethod.Application(it)
        }
        currentSnapshot.frameworkCatalog.declaredMethod(owner, descriptor)?.let {
            return SnapshotMethod.Framework(it)
        }
        return null
    }

    private fun isSubinterface(candidate: String, ancestor: String): Boolean {
        val pending = ArrayDeque<String>()
        val seen = mutableSetOf<String>()
        pending.addLast(candidate)
        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()
            if (!seen.add(current)) continue
            if (current == ancestor) return true
            currentSnapshot.hierarchy.declarations[current]?.interfaces?.forEach(pending::addLast)
        }
        return false
    }

    private fun lookupOf(method: SnapshotMethod): MethodLookup {
        val resolved = resolvedSnapshotMethod(method)
        return when (method) {
            is SnapshotMethod.Application -> method.definition.body?.let {
                MethodLookup.Runnable(MethodView.of(method.definition, currentSnapshot.programRevision), resolved)
            } ?: MethodLookup.NoBody(resolved)
            is SnapshotMethod.Framework -> method.definition.body?.let {
                MethodLookup.Runnable(MethodView.of(method.definition, currentSnapshot.programRevision), resolved)
            } ?: MethodLookup.NoBody(resolved)
        }
    }

    private fun resolvedSnapshotMethod(
        method: SnapshotMethod,
        definingClass: VirtualType = typeOf(method.declaration.definingClass),
    ): ResolvedMethod = ResolvedMethod(
        MethodDescriptor(
            definingClass,
            method.declaration.name,
            method.declaration.parameterTypes.joinToString(separator = "", prefix = "(", postfix = ")") +
                method.declaration.returnType,
        ),
        typeOf(method.declaration.returnType),
        method.declaration.parameterTypes.map { typeOf(it.toString()) },
        Modifier.isSynchronized(method.declaration.accessFlags) ||
            AccessFlags.DECLARED_SYNCHRONIZED.isSet(method.declaration.accessFlags),
    )

    private fun runnable(view: MethodView): MethodLookup.Runnable {
        val descriptor = view.signature.substringAfter("->")
        val name = descriptor.substringBefore('(')
        val proto = descriptor.substring(name.length)
        val parameterTypes = view.parameters.drop(if (view.isStatic) 0 else 1).map(ParameterSlot::type)
        return MethodLookup.Runnable(
            view,
            ResolvedMethod(
                MethodDescriptor(view.definingClass, name, proto),
                view.returnType,
                parameterTypes,
                view.isSynchronized,
            ),
        )
    }

    /** Resolves one invoke under its opcode-specific lookup rule. */
    fun resolveInvocation(
        target: MethodDescriptor,
        kind: InvokeKind,
        callerClass: VirtualType,
        receiverType: VirtualType?,
    ): InvocationLookup {
        val referenced = if (
            kind == InvokeKind.STATIC && target.descriptor == CLASS_INITIALIZER_DESCRIPTOR
        ) {
            lookupClassInitializer(target.definingClass)
        } else {
            lookupMethod(target.definingClass, target.descriptor)
        }
        if (referenced is MethodLookup.NoSuchClass || referenced is MethodLookup.NoSuchMethod) {
            return InvocationLookup.Resolved(referenced)
        }
        return when (kind) {
            InvokeKind.STATIC -> InvocationLookup.Resolved(referenced)
            InvokeKind.DIRECT -> InvocationLookup.Resolved(lookupDeclaredMethod(target.definingClass, target.descriptor))
            InvokeKind.SUPER -> if (isInterface(target.definingClass)) {
                resolveInterfaceSuper(callerClass, target)
            } else {
                val parent = try {
                    superclassOf(callerClass)
                } catch (_: ClassLoadFailure) {
                    null
                }
                InvocationLookup.Resolved(
                    if (parent == null) MethodLookup.NoSuchMethod else lookupMethod(parent, target.descriptor),
                )
            }
            InvokeKind.VIRTUAL, InvokeKind.INTERFACE -> if (receiverType != null) {
                InvocationLookup.Resolved(lookupMethod(receiverType, target.descriptor))
            } else {
                val candidates = dispatchCandidates(target.definingClass, target.descriptor)
                val candidateNames = java.util.Collections.unmodifiableSet(LinkedHashSet(candidates.keys))
                when {
                    !dispatchWorldIsComplete(target.definingClass) -> InvocationLookup.Ambiguous(candidateNames)
                    candidates.isEmpty() -> InvocationLookup.Resolved(referenced)
                    candidates.size > 1 -> InvocationLookup.Ambiguous(candidateNames)
                    // Instances of framework subtypes are not enumerated. One application candidate is the target
                    // only when it is the referenced method, the same answer as no application candidate.
                    frameworkSubtypesCanReceive(target.definingClass) &&
                        candidates.keys.single() != selectedSignature(referenced) ->
                        InvocationLookup.Ambiguous(candidateNames)
                    else -> InvocationLookup.Resolved(candidates.values.single(), candidateNames)
                }
            }
        }
    }

    /** Immutable owner-wide evidence for whether unknown receiver dispatch is closed over the loaded set. */
    internal fun dispatchWorldIsComplete(declaredType: VirtualType): Boolean {
        if (!declaredType.isReference || declaredType.isArray || isFinalClass(declaredType)) return true
        return hasCompleteHierarchy && !hasDynamicLoadingEvidence
    }

    /**
     * Framework classes, bundled or newer on the device, can extend or implement a non-final framework type.
     * They cannot name an application type.
     */
    private fun frameworkSubtypesCanReceive(declaredType: VirtualType): Boolean =
        isFrameworkClass(declaredType) && !isFinalClass(declaredType)

    private fun isFinalClass(type: VirtualType): Boolean {
        if (!isLoadable(type)) return false
        return Modifier.isFinal(currentSnapshot.hierarchy.declarations[type.descriptor]?.accessFlags ?: return false)
    }

    /** True when every class reachable through an application class's superclass and interfaces is loaded. */
    private fun scanHierarchyCompleteness(): Boolean {
        val declarations = currentSnapshot.hierarchy.declarations
        val pending = ArrayDeque(currentSnapshot.applicationClasses.keys)
        val seen = HashSet<String>()
        while (pending.isNotEmpty()) {
            val descriptor = pending.removeLast()
            if (!seen.add(descriptor)) continue
            val declaration = declarations[descriptor] ?: return false
            declaration.superclass?.let(pending::addLast)
            pending.addAll(declaration.interfaces)
        }
        return true
    }

    internal fun isInterface(type: VirtualType): Boolean {
        if (!type.isReference || type.isArray || !isLoadable(type)) return false
        return java.lang.reflect.Modifier.isInterface(
            currentSnapshot.hierarchy.declarations[type.descriptor]?.accessFlags ?: return false,
        )
    }

    /** Direct interfaces in declaration order for Java public-field lookup. */
    internal fun directInterfaces(type: VirtualType): List<VirtualType> {
        if (!type.isReference || type.isArray || !isLoadable(type)) return emptyList()
        return currentSnapshot.hierarchy.declarations[type.descriptor]?.interfaces?.map(VirtualType::of)
            ?: throw ClassLoadFailure(
                "Class model cannot inspect interfaces of ${type.descriptor}",
                IllegalStateException("No declaration for ${type.descriptor} in the program snapshot"),
            )
    }

    /**
     * Class initialization targets checked before reflection loads any host class: [type], its superclasses,
     * and every superinterface in sorted depth-first order.
     */
    internal fun hostInitializationChain(type: VirtualType): List<VirtualType> {
        if (!type.isReference || type.isArray) return listOf(type)
        val chain = linkedSetOf<VirtualType>()
        val pendingInterfaces = ArrayDeque<String>()

        fun pushInterfaces(owner: VirtualType) {
            val interfaces = currentSnapshot.hierarchy.declarations[owner.descriptor]?.interfaces
                ?: throw ClassLoadFailure(
                    "Class model cannot inspect interfaces of ${owner.descriptor}",
                    IllegalStateException("No declaration for ${owner.descriptor} in the program snapshot"),
                )
            interfaces.sortedDescending().forEach(pendingInterfaces::addLast)
        }

        var current: VirtualType? = type
        while (current != null) {
            chain += current
            pushInterfaces(current)
            while (pendingInterfaces.isNotEmpty()) {
                val interfaceType = VirtualType.of(pendingInterfaces.removeLast())
                if (chain.add(interfaceType)) pushInterfaces(interfaceType)
            }
            if (isInterface(current)) break
            current = superclassOf(current)
        }
        return java.util.Collections.unmodifiableList(chain.toList())
    }

    /** Dex 037 `invoke-super` against an interface method selects the most specific inherited default. */
    private fun resolveInterfaceSuper(callerClass: VirtualType, target: MethodDescriptor): InvocationLookup {
        val candidates = LinkedHashMap<String, MethodView>()
        for (name in currentSnapshot.applicationClasses.keys.sorted()) {
            val type = VirtualType.of(name)
            if (!isInterface(type)) continue
            val between = try {
                isSubtypeOf(callerClass, type) && isSubtypeOf(type, target.definingClass)
            } catch (_: ClassLoadFailure) {
                false
            }
            if (!between) continue
            val lookup = lookupDeclaredMethod(type, target.descriptor)
            if (lookup is MethodLookup.Runnable) candidates[lookup.view.signature] = lookup.view
        }
        val mostSpecific = candidates.values.filter { candidate ->
            candidates.values.none { other ->
                other !== candidate && try {
                    isSubtypeOf(other.definingClass, candidate.definingClass)
                } catch (_: ClassLoadFailure) {
                    false
                }
            }
        }
        return when (mostSpecific.size) {
            1 -> InvocationLookup.Resolved(runnable(mostSpecific.single()))
            0 -> InvocationLookup.Resolved(lookupDeclaredMethod(target.definingClass, target.descriptor))
            else -> InvocationLookup.Ambiguous(mostSpecific.mapTo(linkedSetOf()) { it.signature })
        }
    }

    private fun lookupDeclaredMethod(definingClass: VirtualType, descriptor: String): MethodLookup {
        if (!isLoadable(definingClass)) return MethodLookup.NoSuchClass
        if (!definingClass.isReference || definingClass.isArray) return lookupMethod(definingClass, descriptor)
        val method = currentSnapshot.applicationClass(definingClass.descriptor)?.methodsByDescriptor?.get(descriptor)
            ?.let { SnapshotMethod.Application(it) }
            ?: currentSnapshot.frameworkCatalog.declaredMethod(definingClass.descriptor, descriptor)
                ?.let { SnapshotMethod.Framework(it) }
            ?: return MethodLookup.NoSuchMethod
        return lookupOf(method)
    }

    /**
     * The method each instantiable application subtype of [declaredType] selects, keyed by signature. Abstract
     * classes and interfaces have no instances of their own; a bodyless or conflicting selection is a target
     * like any other.
     */
    private fun dispatchCandidates(declaredType: VirtualType, descriptor: String): LinkedHashMap<String, MethodLookup> {
        val candidates = LinkedHashMap<String, MethodLookup>()
        for (name in currentSnapshot.applicationClasses.keys.sorted()) {
            val type = VirtualType.of(name)
            if (!isInstantiable(type)) continue
            val isCandidate = try {
                isSubtypeOf(type, declaredType)
            } catch (_: ClassLoadFailure) {
                false
            }
            if (!isCandidate) continue
            val lookup = lookupMethod(type, descriptor)
            selectedSignature(lookup)?.let { candidates.putIfAbsent(it, lookup) }
        }
        return candidates
    }

    private fun isInstantiable(type: VirtualType): Boolean {
        val accessFlags = currentSnapshot.hierarchy.declarations[type.descriptor]?.accessFlags ?: return false
        return !Modifier.isAbstract(accessFlags) && !Modifier.isInterface(accessFlags)
    }

    private fun selectedSignature(lookup: MethodLookup): String? {
        val method = when (lookup) {
            is MethodLookup.Runnable -> lookup.method
            is MethodLookup.NoBody -> lookup.method
            is MethodLookup.DefaultConflict -> lookup.method
            MethodLookup.NoSuchClass, MethodLookup.NoSuchMethod -> return null
        }
        return "${method.target.definingClass.descriptor}->${method.target.descriptor}"
    }

    /**
     * [type]'s **own declared** `<clinit>()V`, if it has one -- the class-init scheduler's lookup
     * ([ClassInitPlanner]), and deliberately **not** [lookupMethod].
     *
     * **A static initializer is not inherited, and [lookupMethod] cannot express that.** It routes through
     * `ClassManager.getVirtualMethod` -> `VirtualClass.getMethod`, which falls back to
     * `ancestor.getMethod0(...)` for every ancestor (`type/VirtualClass.java:112-126`) -- so asking it for
     * `<clinit>()V` on a class that declares none hands back its SUPERCLASS's initializer, and the caller
     * would run the superclass's `<clinit>` believing it had run the subclass's. That is an oracle defect
     * recorded in as many words: *"Because `VirtualClass.getMethod()` searches ancestors, a class with no
     * **declared** `<clinit>` can instead resolve an ancestor initializer ... Static initializers are not
     * inherited, so this lookup conflates ordinary method inheritance with class initialization"*
     * (`docs/architecture/execution-engine.md:159`). `getMethods()` is built from `getClassDef().getMethods()`
     * (`type/VirtualClass.java:219-227`), i.e. the class's own definition and nothing above it.
     *
     * The three failure members mean here what they mean for [lookupMethod], with one difference worth
     * stating: [MethodLookup.NoSuchMethod] is the **common, expected** answer rather than a failure -- most
     * classes declare no `<clinit>` at all, and design decision 1 has them transition
     * `Uninitialized -> Initialized` with no nested run.
     *
     * An ARRAY (or a primitive) answers [MethodLookup.NoSuchMethod] without touching the class model: an
     * array class has no initializer, and `getVirtualClass` would raise a host `ClassCastException` on one
     * (`getVirtualType("[I")` returns a `VirtualArray`, a SIBLING of `VirtualClass`) -- the same sharp edge
     * [lookupMethod]'s array row exists for.
     */
    fun lookupClassInitializer(type: VirtualType): MethodLookup {
        if (!type.isReference || type.isArray) return MethodLookup.NoSuchMethod
        if (!isLoadable(type)) return MethodLookup.NoSuchClass
        val declared = declaredClassInitializer(type) ?: return MethodLookup.NoSuchMethod
        return lookupOf(declared)
    }

    /**
     * Whether [type] declares a `<clinit>()V` at all -- the cheap half of [lookupClassInitializer], with no
     * [MethodView] built.
     *
     * Exists for decision 7's framework rule, which needs to know whether skipping a framework class's
     * initializer actually skipped anything: a framework class that declares no `<clinit>` costs no precision
     * to mark initialized, while one that does (`java/lang/Integer.smali:69`) means the trigger's address must
     * declare [SideEffectLevel.STRONG]. `Ljava/lang/Object;` declares none and is on every app class's
     * superclass chain, so conflating the two would raise every class-init trigger in the system to `STRONG`.
     */
    fun hasClassInitializer(type: VirtualType): Boolean {
        if (!type.isReference || type.isArray) return false
        if (!isLoadable(type)) return false
        return declaredClassInitializer(type) != null
    }

    private fun declaredClassInitializer(type: VirtualType): SnapshotMethod? =
        currentSnapshot.applicationClass(type.descriptor)?.methodsByDescriptor?.get(CLASS_INITIALIZER_DESCRIPTOR)
            ?.let { SnapshotMethod.Application(it) }
            ?: currentSnapshot.frameworkCatalog.declaredMethod(type.descriptor, CLASS_INITIALIZER_DESCRIPTOR)
                ?.let { SnapshotMethod.Framework(it) }

    /**
     * Whether [type] is one of the bundled **framework** classes rather than an application class --
     * resolved decision 7, and the ONE place that question is asked (the two-tier rule: `core/` outside this
     * file may not name `org.cf.smalivm.type.*`, so no caller may reach for a live `type.*` handle to ask it).
     *
     * It matters because the framework smali is 21,088 ordinary loaded classes
     * (`resources/framework_classes.cfg`, folded in at `dex/SmaliFileFactory.kt:83-98`), so nothing about
     * *presence* distinguishes `Ljava/lang/Integer;` from an app class. The old engine keeps framework classes
     * out of virtual execution by POLICY, not by absence: a `safe_classes.cfg` class runs as
     * `ExecutionType.REFLECT` (`ExecutionGraph.kt:84-88`), which short-circuits `<clinit>` and simply marks
     * the class initialized (`VirtualMachine.kt:196-203`). Plan 9 mirrors the shape of that short-circuit
     * without needing reflection; the real `safe`/`unsafe` policy surface arrives with Plan 11's backends.
     *
     * Answered off `ClassManager.getFrameworkClassNames()`, a cached set of DESCRIPTORS, so this **loads no
     * class**: it must be answerable for a class whose ancestors are missing, which is exactly the input this
     * engine is for.
     */
    fun isFrameworkClass(type: VirtualType): Boolean =
        currentSnapshot.hierarchy.declarations[type.descriptor]?.framework == true

    /**
     * The static field a `sget*`/`sput*` reference names, resolved at decode time so `core/op/` receives a
     * [VirtualType] and a fully-qualified descriptor rather than a dexlib handle.
     *
     * A dex field reference is `(class, name, type)`, so two statics differing only in type are two fields.
     * The search follows JVMS 5.4.3.2 and ART `Class::FindStaticField`: the class, then its direct
     * superinterfaces recursively, then its superclass. [StaticFieldLookup.Resolved] names the declaring class,
     * so every spelling of one inherited static reads one cell, and that class is the one a trigger initializes.
     *
     * A class absent from the loaded set anywhere in the search is [StaticFieldLookup.NoSuchClass], the guest
     * `NoClassDefFoundError`, rather than a host failure. A primitive or array [referencedClass] declares no
     * statics and answers [StaticFieldLookup.NoSuchField].
     */
    fun resolveStaticField(
        referencedClass: VirtualType,
        name: String,
        fieldTypeDescriptor: String,
    ): StaticFieldLookup {
        if (!referencedClass.isReference || referencedClass.isArray) {
            return StaticFieldLookup.NoSuchField(fieldDescriptorOf(referencedClass, name, fieldTypeDescriptor))
        }
        if (!isLoadable(referencedClass)) return StaticFieldLookup.NoSuchClass(referencedClass)
        val pending = ArrayDeque<String>()
        val searched = HashSet<String>()
        pending.addLast(referencedClass.descriptor)
        while (pending.isNotEmpty()) {
            val descriptor = pending.removeLast()
            if (!searched.add(descriptor)) continue
            val owner = VirtualType.of(descriptor)
            val declaration = currentSnapshot.hierarchy.declarations[descriptor]
                ?: return StaticFieldLookup.NoSuchClass(owner)
            val wanted = fieldDescriptorOf(owner, name, fieldTypeDescriptor)
            declaredStaticFields(owner).firstOrNull { it.descriptor == wanted }?.let { declared ->
                return StaticFieldLookup.Resolved(
                    owner,
                    declared.descriptor,
                    declared.type,
                    Modifier.isVolatile(declared.modifiers),
                )
            }
            // Popped last-in first-out: every superinterface subtree, in declaration order, before the superclass.
            declaration.superclass?.let(pending::addLast)
            declaration.interfaces.asReversed().forEach(pending::addLast)
        }
        return StaticFieldLookup.NoSuchField(fieldDescriptorOf(referencedClass, name, fieldTypeDescriptor))
    }

    /**
     * Resolves one `iget*`/`iput*` reference. The walk is the superclass chain only. Interfaces are not
     * consulted. The defining class in [InstanceFieldLookup.Resolved] becomes the heap-cell qualifier, so
     * inherited aliases share one cell and a shadowing declaration remains distinct.
     */
    fun resolveInstanceField(
        referencedClass: VirtualType,
        name: String,
        fieldTypeDescriptor: String,
    ): InstanceFieldLookup {
        val requested = fieldDescriptorOf(referencedClass, name, fieldTypeDescriptor)
        if (!referencedClass.isReference || referencedClass.isArray) {
            return InstanceFieldLookup.NoSuchField(requested)
        }
        if (!isLoadable(referencedClass)) return InstanceFieldLookup.NoSuchClass(referencedClass)
        var current: VirtualType? = referencedClass
        val seen = mutableSetOf<String>()
        while (current != null && seen.add(current.descriptor)) {
            val definingClass = current
            val declared = declaredInstanceFields(definingClass)
                .firstOrNull { it.descriptor == fieldDescriptorOf(definingClass, name, fieldTypeDescriptor) }
            if (declared != null) {
                return InstanceFieldLookup.Resolved(
                    definingClass,
                    declared.descriptor,
                    declared.type,
                    Modifier.isVolatile(declared.modifiers),
                )
            }
            current = try {
                superclassOf(definingClass)
            } catch (e: ClassLoadFailure) {
                return InstanceFieldLookup.NoSuchClass(definingClass)
            }
        }
        return InstanceFieldLookup.NoSuchField(requested)
    }

    /**
     * Every instance cell a new object owns, including inherited declarations. Each value is the declared
     * type default. Null means the class or one superclass cannot resolve.
     */
    fun instanceFieldDefaults(type: VirtualType): Map<String, Value>? {
        if (!type.isReference || type.isArray || !isLoadable(type)) return null
        val chain = mutableListOf<VirtualType>()
        val seen = mutableSetOf<String>()
        var current: VirtualType? = type
        while (current != null && seen.add(current.descriptor)) {
            chain += current
            current = try {
                superclassOf(current)
            } catch (e: ClassLoadFailure) {
                return null
            }
        }
        val fields = LinkedHashMap<String, Value>()
        for (declaringClass in chain.asReversed()) {
            for (field in declaredInstanceFields(declaringClass)) {
                fields[field.descriptor] = defaultValueOf(field.type)
            }
        }
        return fields
    }

    /**
     * [type]'s **own declared** static fields, in dex order, with each one's compile-time literal separated
     * from the type default ([StaticFieldDeclaration]).
     *
     * Declared-only, like [lookupClassInitializer] and unlike the oracle's ancestor-searching `getField` --
     * an inherited static belongs to the class that declares it, and that class's own initialization is what
     * gives it a value. Repeating an ancestor's fields here would seed one field into two cells.
     *
     * Read off `BuilderClassDef.getStaticFields()`, a `SortedSet`, so the order is **reproducible** -- the
     * same determinism argument [superclassOf] makes. `VirtualClass.getFields()` would have been the obvious
     * accessor and is the wrong one twice over: it is built from a `HashMap` (`type/VirtualClass.java:204-217`,
     * so unordered) and it mixes instance fields in.
     *
     * Empty -- never a throw -- for a primitive, an array, or a class the model cannot load: every caller is
     * seeding state for a class it is initializing, and "this class contributes no statics" is the right
     * answer for all three.
     */
    fun declaredStaticFields(type: VirtualType): List<StaticFieldDeclaration> {
        if (!type.isReference || type.isArray) return emptyList()
        return snapshotFields(type, static = true).map { field ->
            val fieldType = VirtualType.of(field.type)
            StaticFieldDeclaration(
                descriptor = fieldDescriptorOf(VirtualType.of(field.definingClass), field.name, field.type),
                type = fieldType,
                literal = literalOf(field, fieldType),
                modifiers = field.accessFlags,
            )
        }
    }

    /** This class's own instance fields, in deterministic dex order. */
    fun declaredInstanceFields(type: VirtualType): List<InstanceFieldDeclaration> {
        if (!type.isReference || type.isArray) return emptyList()
        return snapshotFields(type, static = false).map { field ->
            InstanceFieldDeclaration(
                descriptor = fieldDescriptorOf(VirtualType.of(field.definingClass), field.name, field.type),
                type = VirtualType.of(field.type),
                modifiers = field.accessFlags,
            )
        }
    }

    /** This class's declared static and instance fields with exact access metadata. */
    internal fun declaredFields(type: VirtualType): List<FieldDeclaration> =
        declaredStaticFields(type).map { it.toFieldDeclaration(type) } +
            declaredInstanceFields(type).map { it.toFieldDeclaration(type) }

    private fun snapshotFields(type: VirtualType, static: Boolean): List<Field> {
        val application = currentSnapshot.applicationClass(type.descriptor)
        if (application != null) {
            val fields = if (static) application.staticFields else application.instanceFields
            return fields.map(ApplicationFieldDefinition::declaration)
        }
        val framework = currentSnapshot.frameworkCatalog.classDefinition(type.descriptor) ?: return emptyList()
        return (if (static) framework.staticFields else framework.instanceFields).toList()
    }

    /**
     * The dex-encoded compile-time constant behind `.field public static x:I = 0x42`, or null when the field
     * has none -- adjudication 5's *"static initial values are encoded in the class definition"*, which is
     * why a `static final` constant needs no `<clinit>` assignment and why the oracle's skip-if-`<clinit>`
     * rule discards constants sitting in the dex.
     *
     * The nine encoded-value kinds are exactly the ones the oracle decodes (`type/VirtualField.kt:32-42`),
     * transcribed so a literal's raw is byte-identical to the value migrated expectations were adjudicated
     * against. **Anything else -- `ENUM`, `TYPE`, `ARRAY`, `METHOD_*`, `NULL` -- answers "no literal", which
     * is the same observable the oracle produces**: its `when` has no branch for them either, so it falls out
     * of the `if` and returns `null`, and `Value.wrap(null, T)` is a known null -- which is also this
     * build's type default for the reference type such an encoded value can only sit on. Not a narrowing;
     * measured against the oracle's own control flow.
     */
    private fun literalOf(field: Field, type: VirtualType): Value? {
        val encoded = field.initialValue ?: return null
        val raw: Any? = when (encoded.valueType) {
            ValueType.BYTE -> (encoded as ByteEncodedValue).value
            ValueType.SHORT -> (encoded as ShortEncodedValue).value
            ValueType.CHAR -> (encoded as CharEncodedValue).value
            ValueType.INT -> (encoded as IntEncodedValue).value
            ValueType.LONG -> (encoded as LongEncodedValue).value
            ValueType.FLOAT -> (encoded as FloatEncodedValue).value
            ValueType.DOUBLE -> (encoded as DoubleEncodedValue).value
            ValueType.STRING -> (encoded as StringEncodedValue).value
            ValueType.BOOLEAN -> (encoded as BooleanEncodedValue).value
            else -> return null
        }
        return Value.Known(raw, type)
    }


    internal companion object {
        /**
         * A static initializer's member descriptor. `internal` so a test naming a `<clinit>`'s signature does
         * not hand-write the string a second time.
         */
        internal const val CLASS_INITIALIZER_DESCRIPTOR = "<clinit>()V"

        /**
         * The two interfaces every array type implements, and the whole content of [isInstanceOf]'s row 4.
         * Compared as descriptor STRINGS, so neither interface has to be in the loaded set for an array to
         * answer -- which is the point: an obfuscated APK that never mentions `Cloneable` still contains
         * arrays that are `Cloneable` on a device.
         */
        private val ARRAY_SUPERINTERFACES = setOf("Ljava/lang/Cloneable;", "Ljava/io/Serializable;")

        /**
         * The ONE spelling of a fully-qualified field descriptor, `Lcom/example/Foo;->bar:I` --
         * `StateContracts.kt`'s documented key form for both [Heap.getField] and
         * [ClassState.getStaticField].
         *
         * One function rather than a string template at each site, because a **storage key** and a
         * **lookup key** that disagree by one character produce a silent miss, not an error: the read
         * answers [Value.Top] and the engine reports "nothing known" about a field it holds perfectly.
         * `resolveStaticField`, [declaredStaticFields] and every test seed go through this.
         *
         * It is also the oracle's own spelling -- `VirtualField.getSignature()` is
         * `DexFormatter.getFieldDescriptor(builderField)` (`type/VirtualField.kt:62-64`) -- which keeps a
         * migrated expectation's key comparable to the one it was adjudicated against.
         */
        internal fun fieldDescriptorOf(definingClass: VirtualType, name: String, fieldTypeDescriptor: String): String =
            "${definingClass.descriptor}->$name:$fieldTypeDescriptor"
    }
}
