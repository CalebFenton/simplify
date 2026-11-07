package org.cf.smalivm.core

import java.util.Collections
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

/** One validated, non-empty result from an op execution. */
internal class OpExecution private constructor(arms: List<OpArm>) {
    val arms: List<OpArm> = Collections.unmodifiableList(arms.toList())
    val footprint: OpUpdateFootprint = arms.fold(OpUpdateFootprint.EMPTY) { footprint, arm ->
        footprint.combine(arm.reconciliationFootprint)
    }

    init {
        require(arms.isNotEmpty()) { "An op execution must contain at least one arm" }
    }

    companion object {
        fun of(vararg arms: OpArm): OpExecution = OpExecution(arms.toList())
        fun of(arms: List<OpArm>): OpExecution = OpExecution(arms)
        fun single(arm: OpArm): OpExecution = OpExecution(listOf(arm))
    }
}

/** Which instruction address a throw uses for handler lookup. */
internal enum class ExceptionPc { AT_INSTRUCTION, AFTER_INSTRUCTION }

/** One feasible result of an op. Each arm receives its own persistent frame copy. */
internal sealed interface OpArm {
    val update: OpUpdate get() = OpUpdate.NONE
    val reconciliationFootprint: OpUpdateFootprint get() = if (this is MoveFromSlot) {
        update.footprint.withRegisterWrite(RegisterWrite(register, isWide))
    } else {
        update.footprint
    }

    data class Continue(override val update: OpUpdate = OpUpdate.NONE) : OpArm
    data class Jump(val address: Int, override val update: OpUpdate = OpUpdate.NONE) : OpArm
    data class Throw(
        override val update: OpUpdate = OpUpdate.NONE,
        val evidence: ThrowEvidence,
        val lookupPc: ExceptionPc = ExceptionPc.AT_INSTRUCTION,
    ) : OpArm
    data class Return(val frameValue: FrameValue?) : OpArm {
        constructor(value: Value) : this(FrameValue.plain(value))
        val value: Value? get() = frameValue?.value

        companion object {
            fun fromValue(value: Value?): Return = Return(value?.let(FrameValue::plain))
        }
    }
    data class MoveFromSlot(
        val register: Int,
        val source: SpecialSlotSource,
        val isWide: Boolean,
    ) : OpArm {
        init {
            require(register >= 0) { "Move destination must be non-negative: $register" }
        }
    }
    data class Invoke(
        val target: MethodDescriptor,
        val argumentRegisters: IntArray,
        val kind: InvokeKind,
    ) : OpArm {
        override fun equals(other: Any?): Boolean = other is Invoke &&
            other.target == target && other.kind == kind &&
            other.argumentRegisters.contentEquals(argumentRegisters)

        override fun hashCode(): Int {
            var result = target.hashCode()
            result = 31 * result + argumentRegisters.contentHashCode()
            result = 31 * result + kind.hashCode()
            return result
        }
    }
    data class InitClass(val type: VirtualType) : OpArm
    data class ProjectStatic(
        val register: Int,
        val definingClass: VirtualType,
        val fieldDescriptor: String,
        val fieldType: VirtualType,
    ) : OpArm
    data class Abort(val reason: AbortReason) : OpArm
}

/** A scheduler-owned consume slot used by move-result or move-exception. */
internal enum class SpecialSlotSource { RESULT, CAUGHT }

/** A typed destination for an allocation requested by an op. */
internal sealed interface AllocationTarget {
    data class Register(val register: Int) : AllocationTarget {
        init {
            require(register >= 0) { "Allocation register must be non-negative: $register" }
        }
    }

    data object ResultSlot : AllocationTarget
}

/** Scheduler result from applying an [OpUpdate]. */
internal data class AppliedOpUpdate(
    val result: FrameValue? = null,
    val effect: SideEffectLevel = SideEffectLevel.NONE,
)

internal data class RegisterWrite(val register: Int, val isWide: Boolean)

/** Immutable, cached cells an update can change. */
internal data class OpUpdateFootprint(
    val registerWrites: PersistentList<RegisterWrite>,
    val arrayRanges: PersistentMap<HeapRef, PersistentList<IntRange>>,
    val objectFields: PersistentMap<HeapRef, PersistentSet<String>>,
    val staticDescriptors: PersistentSet<String>,
) {
    fun combine(other: OpUpdateFootprint): OpUpdateFootprint {
        if (this === EMPTY) return other
        if (other === EMPTY) return this
        var fields = objectFields
        for ((ref, descriptors) in other.objectFields) {
            fields = fields.putting(ref, (fields[ref] ?: persistentSetOf()).addingAll(descriptors))
        }
        var arrays = arrayRanges
        for ((ref, ranges) in other.arrayRanges) {
            var merged = arrays[ref] ?: persistentListOf()
            for (range in ranges) merged = mergeRange(merged, range)
            arrays = arrays.putting(ref, merged)
        }
        return OpUpdateFootprint(
            registerWrites.addingAll(other.registerWrites),
            arrays,
            fields,
            staticDescriptors.addingAll(other.staticDescriptors),
        )
    }

    fun withRegisterWrite(write: RegisterWrite): OpUpdateFootprint =
        copy(registerWrites = registerWrites.adding(write))

    fun withArrayRange(ref: HeapRef, range: IntRange): OpUpdateFootprint {
        require(!range.isEmpty()) { "An array update footprint must not be empty" }
        return copy(arrayRanges = arrayRanges.putting(ref, mergeRange(arrayRanges[ref] ?: persistentListOf(), range)))
    }

    private fun mergeRange(ranges: PersistentList<IntRange>, added: IntRange): PersistentList<IntRange> {
        var first = added.first
        var last = added.last
        var inserted = false
        val merged = persistentListOf<IntRange>().builder()
        for (range in ranges) {
            when {
                range.last.toLong() + 1 < first.toLong() -> merged.add(range)
                last.toLong() + 1 < range.first.toLong() -> {
                    if (!inserted) {
                        merged.add(first..last)
                        inserted = true
                    }
                    merged.add(range)
                }
                else -> {
                    first = minOf(first, range.first)
                    last = maxOf(last, range.last)
                }
            }
        }
        if (!inserted) merged.add(first..last)
        return merged.build()
    }

    companion object {
        val EMPTY = OpUpdateFootprint(
            persistentListOf(),
            persistentMapOf(),
            persistentMapOf(),
            persistentSetOf(),
        )
    }
}

/** Frame-resolved cells, including implicit wide-register partner changes. */
internal data class ReconciliationFootprint(
    val registerCells: PersistentSet<Int>,
    val arrayRanges: PersistentMap<HeapRef, PersistentList<IntRange>>,
    val objectFields: PersistentMap<HeapRef, PersistentSet<String>>,
    val staticDescriptors: PersistentSet<String>,
)

/**
 * Ordered, typed state changes an op may request.
 *
 * It can change registers, heap objects, statics, and effects only. The scheduler owns class lifecycle
 * and all special slots. An allocation can target the result slot, but only the scheduler writes that slot.
 */
internal class OpUpdate private constructor(
    private val changes: PersistentList<Change>,
    val footprint: OpUpdateFootprint,
) {
    internal sealed interface ArrayData {
        data class Defaults(val length: Int) : ArrayData {
            init {
                require(length >= 0) { "Array length must be non-negative: $length" }
            }
        }

        data class Values(val elements: PersistentList<Value>) : ArrayData
    }

    internal sealed interface Change {
        data class Assign(
            val register: Int,
            val value: FrameValue,
        ) : Change
        data class AllocateArray(
            val target: AllocationTarget,
            val type: VirtualType,
            val data: ArrayData,
        ) : Change
        data class AllocateObject(
            val target: AllocationTarget.Register,
            val type: VirtualType,
            val fields: PersistentMap<String, Value>,
        ) : Change
        data class WriteArrayElements(
            val ref: HeapRef,
            val startIndex: Int,
            val values: PersistentList<Value>,
        ) : Change
        data class WriteField(val ref: HeapRef, val descriptor: String, val value: Value) : Change
        data class WriteStatic(val descriptor: String, val value: Value) : Change
        data class Effect(val level: SideEffectLevel) : Change
    }

    fun then(other: OpUpdate): OpUpdate = when {
        changes.isEmpty() -> other
        other.changes.isEmpty() -> this
        else -> OpUpdate(changes.addingAll(other.changes), footprint.combine(other.footprint))
    }

    internal fun applyTo(frame: Frame): AppliedOpUpdate {
        var result: FrameValue? = null
        var effect = SideEffectLevel.NONE
        for (change in changes) {
            when (change) {
                is Change.Assign -> frame.assignFrameValue(change.register, change.value)
                is Change.AllocateArray -> {
                    val allocated = when (val data = change.data) {
                        is ArrayData.Defaults -> frame.allocateArray(change.type, data.length)
                        is ArrayData.Values -> frame.allocateArray(change.type, data.elements)
                    }
                    when (val target = change.target) {
                        is AllocationTarget.Register -> frame.assign(target.register, allocated)
                        AllocationTarget.ResultSlot -> {
                            check(result == null) { "One op update cannot produce more than one result value" }
                            result = FrameValue.plain(allocated)
                        }
                    }
                }
                is Change.AllocateObject -> frame.assign(
                    change.target.register,
                    frame.allocateObject(change.type, change.fields),
                )
                is Change.WriteArrayElements -> check(
                    frame.writeArrayElements(change.ref, change.startIndex, change.values),
                ) { "No tracked array exists at ${change.ref}" }
                is Change.WriteField -> check(frame.writeField(change.ref, change.descriptor, change.value)) {
                    "No tracked field ${change.descriptor} exists at ${change.ref}"
                }
                is Change.WriteStatic -> frame.writeStaticField(change.descriptor, change.value)
                is Change.Effect -> effect = maxOf(effect, change.level)
            }
        }
        return AppliedOpUpdate(result, effect)
    }

    companion object {
        val NONE: OpUpdate = OpUpdate(persistentListOf(), OpUpdateFootprint.EMPTY)

        fun assign(
            register: Int,
            value: Value,
        ): OpUpdate = assign(register, FrameValue.plain(value))

        internal fun assign(register: Int, value: FrameValue): OpUpdate {
            require(register >= 0) { "Assigned register must be non-negative: $register" }
            return OpUpdate(
                persistentListOf(Change.Assign(register, value)),
                OpUpdateFootprint.EMPTY.withRegisterWrite(RegisterWrite(register, value.value.type?.isWide == true)),
            )
        }

        fun allocateArray(target: AllocationTarget, type: VirtualType, length: Int): OpUpdate =
            allocationUpdate(target, type, ArrayData.Defaults(length))

        fun allocateArray(target: AllocationTarget, type: VirtualType, elements: Iterable<Value>): OpUpdate =
            allocationUpdate(target, type, ArrayData.Values(elements.toPersistentList()))

        private fun allocationUpdate(target: AllocationTarget, type: VirtualType, data: ArrayData): OpUpdate {
            val footprint = if (target is AllocationTarget.Register) {
                OpUpdateFootprint.EMPTY.withRegisterWrite(RegisterWrite(target.register, isWide = false))
            } else {
                OpUpdateFootprint.EMPTY
            }
            return OpUpdate(persistentListOf(Change.AllocateArray(target, type, data)), footprint)
        }

        fun allocateObject(
            register: Int,
            type: VirtualType,
            fields: Map<String, Value>,
        ): OpUpdate = OpUpdate(
            persistentListOf(
                Change.AllocateObject(
                    AllocationTarget.Register(register),
                    type,
                    fields.toPersistentMap(),
                ),
            ),
            OpUpdateFootprint.EMPTY.withRegisterWrite(RegisterWrite(register, isWide = false)),
        )

        fun writeArrayElement(ref: HeapRef, index: Int, value: Value): OpUpdate =
            writeArrayElements(ref, index, listOf(value))

        fun writeArrayElements(ref: HeapRef, startIndex: Int, values: List<Value>): OpUpdate {
            require(startIndex >= 0) { "Array update index must be non-negative: $startIndex" }
            val persistentValues = values.toPersistentList()
            if (persistentValues.isEmpty()) return NONE
            val endExclusive = Math.addExact(startIndex, persistentValues.size)
            return OpUpdate(
                persistentListOf(Change.WriteArrayElements(ref, startIndex, persistentValues)),
                OpUpdateFootprint.EMPTY.withArrayRange(ref, startIndex until endExclusive),
            )
        }

        fun writeField(ref: HeapRef, descriptor: String, value: Value): OpUpdate = OpUpdate(
            persistentListOf(Change.WriteField(ref, descriptor, value)),
            OpUpdateFootprint.EMPTY.copy(
                objectFields = persistentMapOf(ref to persistentSetOf(descriptor)),
            ),
        )

        fun writeStatic(descriptor: String, value: Value): OpUpdate = OpUpdate(
            persistentListOf(Change.WriteStatic(descriptor, value)),
            OpUpdateFootprint.EMPTY.copy(staticDescriptors = persistentSetOf(descriptor)),
        )

        fun effect(level: SideEffectLevel): OpUpdate =
            if (level == SideEffectLevel.NONE) NONE
            else OpUpdate(persistentListOf(Change.Effect(level)), OpUpdateFootprint.EMPTY)
    }
}
