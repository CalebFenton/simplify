package org.cf.smalivm.core

import java.lang.reflect.Method
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

/**
 * One immutable version of a VM-tracked object (spec §4.2). A [Value.Known] whose raw is a [HeapRef] denotes one of
 * these; the raw is **never** the live host object, so identity is the heap id and never host `==`.
 *
 * Reads are public because consumers need them (spec §9: a strategy folding `new int[]{1,2,3}` must
 * see the contents). Guest writes create a replacement through [HeapStore]; an object returned by a read
 * has no mutation surface. This keeps earlier frames and sibling paths unchanged without eager copies.
 */
sealed class HeapObject(val type: VirtualType) {
    /** One path-local reflection field identity. Accessibility changes replace this immutable version. */
    internal class TrackedField internal constructor(
        val metadata: TrackedFieldValue,
        val accessibility: FieldAccessibility,
    ) : HeapObject(FIELD_TYPE) {
        internal fun withAccessibility(next: FieldAccessibility): TrackedField =
            if (next == accessibility) this else TrackedField(metadata, next)

        override fun toString(): String = "$type(${metadata.descriptor}, $accessibility)"

        private companion object {
            val FIELD_TYPE: VirtualType = VirtualType.of("Ljava/lang/reflect/Field;")
        }
    }

    /** A direct JVM object plus immutable method metadata captured before the host graph can clone it. */
    class HostObject internal constructor(
        type: VirtualType,
        val contents: HostGraphContents,
        internal val reflectedMethodMetadata: ReflectedMethodMetadata?,
    ) : HeapObject(type) {
        internal fun withContents(next: HostGraphContents): HostObject =
            if (next == contents) this else HostObject(type, next, reflectedMethodMetadata)

        override fun toString(): String = "$type($contents)"
    }

    /**
     * A tracked array. **Each element is its own lattice point**, which is the whole reason this type
     * exists rather than a host array in the raw: the engine being replaced marks the ENTIRE array
     * `Unknown` when one element or one index is unknown (`opcode/APutOp.kt`'s
     * `if (store.isUnknown || index.isUnknown) array = Value.unknown(array.type)`), and **its own test called
     * that non-ideal**: *"TODO: Ideally, setting an element unknown shouldn't set entire array unknown"*. With
     * elements as [Value]s the honest answer is available -- one unknown element, every other element still
     * known.
     *
     * **No file reference for that admission on purpose: the test carrying it belonged to the engine being
     * replaced and was deleted with it in Plan 7 Task 9.** The quotation is preserved here because it is the
     * strongest evidence for the divergence -- the old engine's own author agreeing -- and a citation to a
     * deleted file is worse than none: it resolves against whatever now answers to that name. The surviving
     * tests are `core/op/APutOpTest.anUnknownValueMakesOnlyItsOwnElementUnknown` and, for the unknown-INDEX
     * half, `…anUnknownIndexJoinsEveryElementWithTheStoredValue`; both are REPLACED migrations, keeping the
     * corpus's input and question while the answer sharpens. Cited by NAME, never by line range.
     *
     * [type] is **this object's own array type and is always a real array descriptor**, so
     * [componentType] is never null and no caller needs a fallback. It is established at allocation from
     * something that cannot lie: `new-array`'s type operand, or -- when the harness seeds a host array --
     * that array's own class.
     *
     * **The REGISTER's tag is a separate thing and is allowed to disagree.** The corpus tags array
     * registers loosely on purpose (`core/op/AGetOpTest.canGetObject` tags a `String[]` as
     * `Ljava/lang/String;`, `core/op/ArrayLengthOpTest.canGetLengthForStringArray` puts Strings in a `[I`),
     * because a Dalvik register carries no type at all -- the tag is smalivm's own metadata. So the rule is
     * **object over tag**: where an object
     * exists it is the authority for component type and length, and a register tag is consulted only when
     * there is no object (an `Unknown` array). Deriving a fact from the tag when an object was available
     * is how a test passes for the wrong reason -- it is what makes the oracle's
     * `array.type.replaceFirst("\\[", "")` accidentally right on these fixtures.
     *
     * Element raws are still not required to match [componentType]; that half is a genuine parity
     * requirement.
     */
    class TrackedArray private constructor(
        type: VirtualType,
        private val elements: PersistentList<Value>,
        val mirror: HostGraphContents?,
        internal val mirrorDirty: Boolean,
        @Suppress("UNUSED_PARAMETER") persistent: Unit,
    ) : HeapObject(type) {
        internal constructor(
            type: VirtualType,
            elements: Array<Value>,
            mirror: HostGraphContents? = null,
        ) : this(type, elements.toList().toPersistentList(), mirror, mirrorDirty = false, Unit)

        val length: Int get() = elements.size

        /** The element at [index]. Callers must bounds-check first: an out-of-range read is an AIOOBE the OP decides about. */
        fun element(index: Int): Value = elements[index]

        /** Every element, in order -- for consensus and for rendering. */
        fun elements(): List<Value> = elements

        internal fun elementsRoot(): PersistentList<Value> = elements

        internal fun withElement(index: Int, value: Value): TrackedArray =
            TrackedArray(type, elements.replacingAt(index, value), mirror, mirror is HostGraphContents.Known, Unit)

        internal fun withElements(startIndex: Int, values: List<Value>): TrackedArray {
            require(startIndex >= 0 && startIndex + values.size <= length) {
                "Array update [$startIndex, ${startIndex + values.size}) is outside length $length"
            }
            var updated = elements
            for ((offset, value) in values.withIndex()) updated = updated.replacingAt(startIndex + offset, value)
            return TrackedArray(type, updated, mirror, mirror is HostGraphContents.Known, Unit)
        }

        internal fun withMirror(contents: HostGraphContents?): TrackedArray =
            TrackedArray(type, elements, contents, mirrorDirty && contents is HostGraphContents.Known, Unit)

        internal fun withSynchronizedMirror(contents: HostGraphContents?): TrackedArray =
            if (contents == mirror && !mirrorDirty) this
            else TrackedArray(type, elements, contents, mirrorDirty = false, Unit)

        internal fun withElementsRoot(
            values: PersistentList<Value>,
            contents: HostGraphContents?,
            dirty: Boolean = mirrorDirty,
        ): TrackedArray = TrackedArray(type, values, contents, dirty && contents is HostGraphContents.Known, Unit)

        internal fun contentsRootToken(): Any = elements

        override fun toString(): String = "$type$elements"

        internal companion object {
            fun fromPersistent(type: VirtualType, elements: PersistentList<Value>): TrackedArray =
                TrackedArray(type, elements, mirror = null, mirrorDirty = false, Unit)
        }
    }

    /**
     * An ordinary tracked guest object -- what `new-instance` allocates (Plan 9 Task 17, the case Task
     * 13 surfaced as the hierarchy's unnamed prerequisite: arrays and throwables existed, a plain
     * object did not).
     *
     * [uninitialized] is **resolved decision 8's flag**: `true` from allocation until an `<init>` call
     * whose receiver is this object's ref returns. Constructor completion creates a replacement object
     * version with this value false; it never mutates an object held by an earlier frame.
     *
     * [fields] are keyed by the fully-qualified descriptor of the class that declares each field. Forks
     * share the persistent root; one field write creates one replacement object version.
     *
     * Identity is the heap id, NEVER the type -- adjudication 8. The oracle's
     * `type/UninitializedInstance.java` compares type only, so two unrelated `new-instance Lfoo;`
     * read as one value there and initializing one rewrites every register holding the other
     * (`ExecutionState.update` matches by `equals`). Registered as an intended divergence
     * (`Fixtures.intendedDivergences`, `ratchet-alias-unrelated-allocation`); pinned at the store by
     * `HeapStoreTest.twoPathsAllocatingObjectsOfOneTypeGetDistinctIdsAndDoNotAgree` and end-to-end by
     * `NewInstanceOpTest.twoAllocationsOfOneTypeAreDistinctUnrelatedObjects`.
     */
    class TrackedObject private constructor(
        type: VirtualType,
        val uninitialized: Boolean,
        private val fields: PersistentMap<String, Value>,
        @Suppress("UNUSED_PARAMETER") persistent: Unit,
    ) : HeapObject(type) {
        internal constructor(
            type: VirtualType,
            uninitialized: Boolean,
            fields: Map<String, Value> = emptyMap(),
        ) : this(type, uninitialized, fields.toPersistentMap(), Unit)

        /** The value in one fully-qualified declaring-class field cell, or null if this object has no such cell. */
        fun field(fieldDescriptor: String): Value? = fields[fieldDescriptor]

        /** A stable snapshot used by heap consensus. */
        internal fun fields(): Map<String, Value> = fields

        internal fun withField(fieldDescriptor: String, value: Value): TrackedObject? =
            if (fieldDescriptor in fields) TrackedObject(type, uninitialized, fields.putting(fieldDescriptor, value), Unit)
            else null

        internal fun withFields(values: Map<String, Value>): TrackedObject =
            TrackedObject(type, uninitialized, values.toPersistentMap(), Unit)

        internal fun initialized(): TrackedObject =
            if (uninitialized) TrackedObject(type, false, fields, Unit) else this

        internal fun contentsRootToken(): Any = fields

        override fun toString(): String = "${if (uninitialized) "uninitialized " else ""}$type$fields"
    }

    /**
     * A guest throwable the engine synthesized (divide by zero, NPE, AIOOBE, ArrayStore, ...).
     *
     * The Java instance lives only in [HostGraphState] and [contents] identifies its graph node. It is
     * never reconstructed from class/message metadata, so guest identity survives throw and routing.
     *
     * Identity is the heap id, so two synthesized instances never agree. That is CORRECT rather than
     * conservative: two paths that each throw a fresh NPE do not throw the same object in Dalvik
     * either.
     */
    class GuestThrowable internal constructor(
        type: VirtualType,
        val contents: HostGraphContents,
        val cause: Value? = null,
    ) : HeapObject(type) {
        internal fun withContents(next: HostGraphContents): GuestThrowable =
            if (next == contents) this else GuestThrowable(type, next, cause)

        internal fun withCause(next: Value): GuestThrowable =
            if (next == cause) this else GuestThrowable(type, contents, next)

        override fun toString(): String = "$type($contents, cause=$cause)"
    }
}

internal fun Method.toReflectedMethodMetadataOrNull(): ReflectedMethodMetadata? = try {
    ReflectedMethodMetadata(fullDescriptor(), modifiers, declaringClass.modifiers)
} catch (fatal: VirtualMachineError) {
    throw fatal
} catch (_: Throwable) {
    null
}

private fun Method.fullDescriptor(): String = buildString {
    append(virtualTypeOfHostClass(declaringClass).descriptor)
    append("->")
    append(name)
    append('(')
    parameterTypes.forEach { append(virtualTypeOfHostClass(it).descriptor) }
    append(')')
    append(virtualTypeOfHostClass(returnType).descriptor)
}
