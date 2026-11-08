package org.cf.smalivm.core

import java.io.File
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.dex.SmaliParser

/** Resolved identity and declared signature, available whether the method has executable bytecode or not. */
class ResolvedMethod internal constructor(
    val target: MethodDescriptor,
    val returnType: VirtualType,
    val parameterTypes: List<VirtualType>,
    val isSynchronized: Boolean,
)

/**
 * What looking a method reference up in the program found -- see [ProgramSnapshotResolver.lookupMethod]. Sealed,
 * so a consumer cannot answer different guest outcomes with one branch.
 */
sealed interface MethodLookup {
    /** Present, with a body. [view] is what a nested activation runs. */
    class Runnable(val view: MethodView, val method: ResolvedMethod) : MethodLookup

    /**
     * The defining class is not in the program: a guest `NoClassDefFoundError`, a `LinkageError` that a guest
     * `catch (Exception)` does not catch (`docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once`).
     */
    data object NoSuchClass : MethodLookup

    /**
     * The class is present and declares no such member: a guest `NoSuchMethodError`, in the same `LinkageError`
     * family as [NoSuchClass]. Resolution precedes the null-receiver check, as for `check-cast`/`instance-of`.
     */
    data object NoSuchMethod : MethodLookup

    /**
     * Declared, but with no code: abstract, native, a framework declaration, or an array pseudo-method
     * (`[I->clone()`), a runtime intrinsic with no dex body. A backend executes these; a device raises no linking
     * error for them.
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
 * What looking a static field reference up in the program found -- see
 * [ProgramSnapshotResolver.resolveStaticField]. Sealed for [MethodLookup]'s reason.
 */
sealed interface StaticFieldLookup {
    /**
     * Present. [descriptor] names the class that declares the field, which may differ from the class the
     * instruction named: `sget v0, Lchild;->parentField:I` resolves to `Lparent;->parentField:I`. [definingClass]
     * is that declaring class, the one a trigger initializes (JVMS 5.5).
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
     * A class the search needed is absent: the referenced class, or an ancestor reached while walking for an
     * inherited static. A guest `NoClassDefFoundError`.
     */
    class NoSuchClass internal constructor(val type: VirtualType) : StaticFieldLookup

    /**
     * Every class and interface the search reaches is present, and none declares a static field of this name and
     * type: a guest `NoSuchFieldError` (JVMS 5.4.3.2). [descriptor] is what the instruction named.
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
 * One declared static field of one class: its fully-qualified [descriptor], its declared [type], and its
 * compile-time constant [literal] if the dex encodes one (`.field public static myInt:I = 0x42`), else null.
 *
 * [literal] is kept apart from the type default: for a framework class whose `<clinit>` does not run, only the
 * literal is knowable and every other static reads [Value.Unknown], so folding the default there would invent a
 * value the real initializer computes.
 */
class StaticFieldDeclaration internal constructor(
    val descriptor: String,
    val type: VirtualType,
    val literal: Value?,
    val modifiers: Int,
) {
    override fun toString(): String = "$descriptor${literal?.let { " = $it" } ?: ""}"
}

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
) {
    companion object {
        fun of(declaringType: VirtualType, descriptor: String, type: VirtualType, modifiers: Int) =
            FieldDeclaration(declaringType, descriptor.substringAfter("->").substringBefore(':'), descriptor, type, modifiers)
    }
}

/**
 * The program's live class model: every query answers from the workspace's current snapshot. A session resolves
 * through [resolver] over the snapshot it captured instead, so publications after it starts do not reach it.
 * Hierarchy questions answer from the snapshot's [HierarchySnapshot], which publication never changes.
 */
class ClassIndex internal constructor(internal val programWorkspace: ProgramWorkspace) : TypeHierarchy {
    internal val currentSnapshot: ProgramSnapshot get() = programWorkspace.currentSnapshot

    /**
     * The one host class loader for this program, shared by every resolver: a second loader would define a
     * distinct `Class` for one guest class, and `const-class` identity would differ between them. Class shapes come
     * from the snapshot current when the loader is first used; edits never change a class's structure.
     */
    internal val classLoader: SmaliClassLoader by lazy { SmaliClassLoader(programWorkspace.currentSnapshot) }

    internal fun resolver(snapshot: ProgramSnapshot): ProgramSnapshotResolver {
        require(snapshot.programIdentity === programWorkspace.programIdentity) {
            "Cannot resolve a snapshot from another program workspace"
        }
        return ProgramSnapshotResolver(snapshot, this)
    }

    override fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean =
        currentSnapshot.hierarchy.isSubtypeOf(candidate, ancestor)

    override fun isInstanceOf(candidate: VirtualType, target: VirtualType): Boolean =
        currentSnapshot.hierarchy.isInstanceOf(candidate, target)

    override fun superclassOf(type: VirtualType): VirtualType? = currentSnapshot.hierarchy.superclassOf(type)

    /** [ProgramSnapshotResolver.lookupMethod] over the current snapshot. */
    fun lookupMethod(definingClass: VirtualType, methodDescriptor: String): MethodLookup =
        resolver(currentSnapshot).lookupMethod(definingClass, methodDescriptor)

    /** [ProgramSnapshotResolver.resolveStaticField] over the current snapshot. */
    fun resolveStaticField(referencedClass: VirtualType, name: String, fieldTypeDescriptor: String): StaticFieldLookup =
        resolver(currentSnapshot).resolveStaticField(referencedClass, name, fieldTypeDescriptor)

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


    companion object {
        /**
         * Imports the program at [input]: a Smali file, a directory of Smali files, a DEX file, or an archive whose
         * only class path DEX is `classes.dex`. [outputApiLevel] selects the Smali opcode table and the output DEX.
         *
         * @throws ProgramInputException when [input] is missing, unreadable, malformed, or multi-DEX
         */
        fun open(input: File, outputApiLevel: Int = SmaliParser.DEX_API_LEVEL): ClassIndex =
            ClassIndex(InitialProgramImporter.import(ProgramInput.read(input, outputApiLevel)))

        /** A static initializer's member descriptor. */
        internal const val CLASS_INITIALIZER_DESCRIPTOR = "<clinit>()V"

        /**
         * The one spelling of a fully-qualified field descriptor, `Lcom/example/Foo;->bar:I`, the key form of
         * [ExecutionState.getField] and [ClassState.getStaticField]. A storage key and a lookup key that differ by one
         * character miss silently, so every field key is built here.
         */
        internal fun fieldDescriptorOf(definingClass: VirtualType, name: String, fieldTypeDescriptor: String): String =
            "${definingClass.descriptor}->$name:$fieldTypeDescriptor"
    }
}
