package org.cf.smalivm.core

import java.lang.reflect.Method
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
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
public sealed class HeapObject(internal val type: VirtualType) {
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
    internal class HostObject internal constructor(
        type: VirtualType,
        override val contents: HostGraphContents,
        internal val reflectedMethodMetadata: ReflectedMethodMetadata?,
    ) : HeapObject(type), HostBacked {
        internal fun withContents(next: HostGraphContents): HostObject =
            if (next == contents) this else HostObject(type, next, reflectedMethodMetadata)

        override fun toString(): String = "$type($contents)"
    }

    /**
     * A tracked array. Each element is its own lattice point, so one unknown element leaves every other element
     * known.
     *
     * [type] is this object's own array type and is always a real array descriptor, so [componentType] is never
     * null. It comes from something that cannot lie: `new-array`'s type operand, or a seeded host array's own
     * class. A register's tag is separate and may disagree, because a Dalvik register carries no type: where an
     * object exists it is the authority for component type and length, and a tag is consulted only for an
     * `Unknown` array. Element raws are not required to match [componentType].
     *
     * [contents] is the Java array mirroring these elements, or null when the array has none. [mirrorDirty]
     * marks guest writes the mirror has not yet received.
     */
    public class TrackedArray private constructor(
        type: VirtualType,
        private val elements: PersistentList<Value>,
        override val contents: HostGraphContents?,
        internal val mirrorDirty: Boolean,
        @Suppress("UNUSED_PARAMETER") persistent: Unit,
    ) : HeapObject(type), HostBacked {
        internal constructor(
            type: VirtualType,
            elements: Array<Value>,
            contents: HostGraphContents? = null,
        ) : this(type, elements.toList().toPersistentList(), contents, mirrorDirty = false, Unit)

        internal val length: Int get() = elements.size

        /** The element at [index]. Callers must bounds-check first: an out-of-range read is an AIOOBE the OP decides about. */
        internal fun element(index: Int): Value = elements[index]

        /** Every element, in order -- for consensus and for rendering. */
        public fun elements(): List<Value> = elements

        internal fun elementsRoot(): PersistentList<Value> = elements

        internal fun withElements(startIndex: Int, values: List<Value>): TrackedArray {
            require(startIndex >= 0 && startIndex + values.size <= length) {
                "Array update [$startIndex, ${startIndex + values.size}) is outside length $length"
            }
            var updated = elements
            for ((offset, value) in values.withIndex()) updated = updated.replacingAt(startIndex + offset, value)
            return TrackedArray(type, updated, contents, contents is HostGraphContents.Known, Unit)
        }

        internal fun withContents(next: HostGraphContents): TrackedArray =
            TrackedArray(type, elements, next, mirrorDirty && next is HostGraphContents.Known, Unit)

        internal fun withSynchronizedMirror(next: HostGraphContents?): TrackedArray =
            if (next == contents && !mirrorDirty) this
            else TrackedArray(type, elements, next, mirrorDirty = false, Unit)

        internal fun withElementsRoot(
            values: PersistentList<Value>,
            next: HostGraphContents?,
            dirty: Boolean = mirrorDirty,
        ): TrackedArray = TrackedArray(type, values, next, dirty && next is HostGraphContents.Known, Unit)

        internal fun contentsRootToken(): Any = elements

        override fun toString(): String = "$type$elements"

        internal companion object {
            fun fromPersistent(type: VirtualType, elements: PersistentList<Value>): TrackedArray =
                TrackedArray(type, elements, contents = null, mirrorDirty = false, Unit)
        }
    }

    /**
     * An ordinary tracked guest object, what `new-instance` allocates.
     *
     * [uninitialized] is true from allocation until the outermost `<init>` call on this object's ref returns
     * ([Activation.initializedReceiver]).
     * Constructor completion creates a replacement version; it never mutates an object an earlier frame holds.
     *
     * [fields] are keyed by the fully-qualified descriptor of the class that declares each field. Identity is the
     * heap id, never the type: two `new-instance` of one type are two unrelated objects.
     */
    internal class TrackedObject private constructor(
        type: VirtualType,
        internal val uninitialized: Boolean,
        private val fields: PersistentMap<String, Value>,
        @Suppress("UNUSED_PARAMETER") persistent: Unit,
    ) : HeapObject(type) {
        internal constructor(
            type: VirtualType,
            uninitialized: Boolean,
            fields: Map<String, Value> = emptyMap(),
        ) : this(type, uninitialized, fields.toPersistentMap(), Unit)

        /** The value in one fully-qualified declaring-class field cell, or null if this object has no such cell. */
        internal fun field(fieldDescriptor: String): Value? = fields[fieldDescriptor]

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
    internal class GuestThrowable internal constructor(
        type: VirtualType,
        override val contents: HostGraphContents,
        internal val cause: Value? = null,
    ) : HeapObject(type), HostBacked {
        internal fun withContents(next: HostGraphContents): GuestThrowable =
            if (next == contents) this else GuestThrowable(type, next, cause)

        internal fun withCause(next: Value): GuestThrowable =
            if (next == cause) this else GuestThrowable(type, contents, next)

        override fun toString(): String = "$type($contents, cause=$cause)"
    }
}

/** A heap object whose Java state can live in the path's host graph. */
internal sealed interface HostBacked {
    /** Where the host graph holds this object's Java state; null only for an array with no Java mirror. */
    val contents: HostGraphContents?
}

/** This object with its Java state at [next]; every guest-visible cell is unchanged. */
internal fun HostBacked.withContents(next: HostGraphContents): HeapObject = when (this) {
    is HeapObject.HostObject -> withContents(next)
    is HeapObject.GuestThrowable -> withContents(next)
    is HeapObject.TrackedArray -> withContents(next)
}

/** The graph node holding this object's Java state, or null when the graph holds none for it. */
internal val HostBacked.knownNodeId: HostGraphNodeId?
    get() = (contents as? HostGraphContents.Known)?.nodeId

/** Null when [Method]'s signature has no VM descriptor; the metadata is then unknown, not absent. */
internal fun Method.toReflectedMethodMetadataOrNull(): ReflectedMethodMetadata? = hostBoundary({ null }) {
    val method = ImmutableMethodReference(
        virtualTypeOfHostClass(declaringClass).descriptor,
        name,
        parameterTypes.map { virtualTypeOfHostClass(it).descriptor },
        virtualTypeOfHostClass(returnType).descriptor,
    )
    ReflectedMethodMetadata(method, modifiers, declaringClass.modifiers)
}
