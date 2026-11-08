package org.cf.smalivm.core.op

import org.cf.smalivm.core.CastOutcome
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.TypeHierarchy
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.isDalvikNull
import org.cf.smalivm.core.castOutcome
import org.cf.smalivm.core.isNull
import org.cf.smalivm.core.join

/**
 * Stores one element into a tracked array: all seven `aput*` opcodes. The store goes into the array object,
 * so no register is assigned.
 *
 * Exception order is `NullPointerException`, then `ArrayIndexOutOfBoundsException`, then the `aastore`
 * assignability `ArrayStoreException` ([JVMS] §6.5). A store whose value and component disagree in shape
 * (one primitive, one reference) is verifier-rejected input; it is refused first, with an
 * `ArrayStoreException`, so a type lie never lands in an element.
 *
 * The stored element is the value as the array's component holds it ([slotWrite]), typed with the
 * component. An unknown value or index loses precision only where the write may have landed: an unknown
 * index joins every element with the stored value. A store into an array the engine cannot name (unprovable
 * or untracked) records `STRONG`; a store into a tracked array records `WEAK`.
 */
internal class APutOp(
    address: Int,
    override val name: String,
    private val valueRegister: Int,
    private val arrayRegister: Int,
    private val indexRegister: Int,
    /** True for `aput-object`, the one form whose value operand is read as a reference. */
    private val storesAReference: Boolean,
    /** Answers `aastore`'s instance test, and joins an element with the stored value when the index is unprovable. */
    private val hierarchy: TypeHierarchy,
) : Op(address) {

    /** Whether the store throws `ArrayStoreException`, and if it refuses, which rule refused it. */
    private enum class StoreCheck {
        PERMITTED,

        /** Exactly one of value and component is primitive: verifier-rejected input, refused before every other check. */
        REFUSED_BY_SHAPE,

        /** Both references and provably not assignable: `aastore`'s refusal, after the null and bounds checks. */
        REFUSED_BY_ASSIGNABILITY,

        /** The assignability question has no answer, so the store and the throw are both feasible. */
        UNANSWERABLE;

        val storeCanProceed: Boolean get() = when (this) {
            PERMITTED, UNANSWERABLE -> true
            REFUSED_BY_SHAPE, REFUSED_BY_ASSIGNABILITY -> false
        }

        val arrayStoreIsFeasible: Boolean get() = when (this) {
            PERMITTED -> false
            REFUSED_BY_SHAPE, REFUSED_BY_ASSIGNABILITY, UNANSWERABLE -> true
        }
    }

    override val registersRead: Set<Int> = setOf(valueRegister, arrayRegister, indexRegister)

    override fun execute(frame: Frame): OpExecution {
        val value = frame.read(valueRegister)
        val array = frame.readArrayOperand(arrayRegister)
        val index = frame.read(indexRegister)

        val check = storeCheck(value, componentOf(array)) { frame.heapObject(it)?.type }
        val arrayStore = { update: OpUpdate -> GuestThrowables.throwing(GuestThrowables.arrayStore(value.type), update) }
        if (check == StoreCheck.REFUSED_BY_SHAPE) return OpExecution.single(arrayStore(OpUpdate.NONE))

        return when (array) {
            is ArrayOperand.Unprovable -> {
                val effect = OpUpdate.effect(SideEffectLevel.STRONG)
                OpExecution.of(
                    buildList<OpArm> {
                        if (check.storeCanProceed) add(OpArm.Continue(effect))
                        add(GuestThrowables.throwing(GuestThrowables.nullPointer(), effect))
                        add(GuestThrowables.throwing(GuestThrowables.arrayIndexOutOfBounds(null), effect))
                        if (check.arrayStoreIsFeasible) add(arrayStore(effect))
                    },
                )
            }
            ArrayOperand.Null -> OpExecution.single(GuestThrowables.throwing(GuestThrowables.nullPointer()))
            is ArrayOperand.NotTracked -> {
                val effect = OpUpdate.effect(SideEffectLevel.STRONG)
                OpExecution.of(
                    buildList<OpArm> {
                        if (check.storeCanProceed) add(OpArm.Continue(effect))
                        if (check.arrayStoreIsFeasible) add(arrayStore(effect))
                    },
                )
            }
            is ArrayOperand.Tracked -> storeInto(array, index, storedValue(value, array.component), check) {
                arrayStore(OpUpdate.NONE)
            }
        }
    }

    private fun storeInto(
        array: ArrayOperand.Tracked,
        indexOperand: Value,
        stored: Value,
        check: StoreCheck,
        arrayStore: () -> OpArm.Throw,
    ): OpExecution {
        val weak = OpUpdate.effect(SideEffectLevel.WEAK)
        return when (val index = array.array.indexOf(indexOperand)) {
            is ArrayIndex.OutOfBounds ->
                OpExecution.single(GuestThrowables.throwing(GuestThrowables.arrayIndexOutOfBounds(index.index)))
            // The write landed on exactly one element, so each element is its old value or the stored one.
            ArrayIndex.Unprovable -> OpExecution.of(
                buildList<OpArm> {
                    if (check.storeCanProceed) {
                        val joined = List(array.array.length) { join(array.array.element(it), stored, hierarchy) }
                        add(OpArm.Continue(OpUpdate.writeArrayElements(array.ref, 0, joined).then(weak)))
                    }
                    add(GuestThrowables.throwing(GuestThrowables.arrayIndexOutOfBounds(null)))
                    if (check.arrayStoreIsFeasible) add(arrayStore())
                },
            )
            is ArrayIndex.InBounds -> {
                if (!check.storeCanProceed) return OpExecution.single(arrayStore())
                val store = OpArm.Continue(OpUpdate.writeArrayElement(array.ref, index.index, stored).then(weak))
                if (check.arrayStoreIsFeasible) OpExecution.of(store, arrayStore()) else OpExecution.single(store)
            }
        }
    }

    /**
     * The component the store check tests against: the tracked array's own, else the register tag's. A tag
     * is a supertype of the real array type, so a refusal against its component is sound.
     */
    private fun componentOf(array: ArrayOperand): VirtualType? = when (array) {
        is ArrayOperand.Tracked -> array.component
        is ArrayOperand.Unprovable -> array.tag?.componentType
        is ArrayOperand.NotTracked -> array.tag.componentType
        ArrayOperand.Null -> null
    }

    /**
     * The `ArrayStoreException` decision, first match wins. [component] is null when neither an array object
     * nor the register tag names one. Assignability is `aastore`'s instance test of the value against the
     * component, so it is [castOutcome]'s.
     */
    private fun storeCheck(
        value: Value,
        component: VirtualType?,
        heapObjectTypeOf: (HeapRef) -> VirtualType?,
    ): StoreCheck {
        // Storing null never throws. Dalvik's integral zero is null only in a reference context, which among
        // the array stores is `aput-object`'s value operand alone.
        if ((value.isNull || (value.isDalvikNull && storesAReference)) &&
            (component == null || component.isReference)
        ) {
            return StoreCheck.PERMITTED
        }
        // `Top` carries no type, so the opcode stands in for the shape the verifier requires of the value.
        val valueIsPrimitive = value.type?.isPrimitive ?: !storesAReference
        if (component == null) return if (valueIsPrimitive) StoreCheck.PERMITTED else StoreCheck.UNANSWERABLE
        // Only `aput-object` type-checks; no store of a primitive into a primitive array throws.
        if (valueIsPrimitive && component.isPrimitive) return StoreCheck.PERMITTED
        if (valueIsPrimitive != component.isPrimitive) return StoreCheck.REFUSED_BY_SHAPE
        return when (castOutcome(value, component, hierarchy, heapObjectTypeOf)) {
            CastOutcome.ReadsAsNull, CastOutcome.Definitely, CastOutcome.NullOrInstance -> StoreCheck.PERMITTED
            CastOutcome.Never -> StoreCheck.REFUSED_BY_ASSIGNABILITY
            CastOutcome.Unprovable -> StoreCheck.UNANSWERABLE
        }
    }

    /** The element that lands in the slot ([slotWrite]), typed with the array's component. */
    private fun storedValue(value: Value, component: VirtualType): Value =
        when (val stored = slotWrite(value, component, storesAReference && component.isReference)) {
            is Value.Known -> Value.Known(stored.raw, component)
            else -> Value.Unknown(component)
        }

    override fun toString(): String = "$name r$valueRegister, r$arrayRegister, r$indexRegister"
}
