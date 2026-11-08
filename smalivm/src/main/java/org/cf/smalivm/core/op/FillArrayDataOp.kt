package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * `fill-array-data` with its payload decoded at build time, so the fill happens in one step and the payload
 * address is never executed (payloads must not be reachable by control flow, [CON] B22).
 *
 * ART's `FillArrayData` checks null, then that the payload fits, then copies every element at once, so a
 * payload longer than the array stores nothing and an array longer than the payload keeps its tail. Only an
 * arm that fills declares an effect: `STRONG` into an array the engine cannot name, `WEAK` into a tracked one.
 */
internal class FillArrayDataOp(
    address: Int,
    override val name: String,
    private val arrayRegister: Int,
    /** The payload's element width in bytes, which fixes how each element's bits are read. */
    private val elementWidth: Int,
    private val payloadElements: List<Number>,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(arrayRegister)

    override fun execute(frame: Frame): OpExecution = when (val array = frame.readArrayOperand(arrayRegister)) {
        ArrayOperand.Null -> OpExecution.single(GuestThrowables.throwing(NULL_ARRAY))
        is ArrayOperand.Unprovable -> OpExecution.of(
            buildList<OpArm> {
                add(OpArm.Continue(OpUpdate.effect(SideEffectLevel.STRONG)))
                add(GuestThrowables.throwing(NULL_ARRAY))
                // An empty payload fits every array, so only a non-empty one can be too long.
                if (payloadElements.isNotEmpty()) add(GuestThrowables.throwing(overrun(length = null)))
            },
        )
        is ArrayOperand.NotTracked -> OpExecution.single(OpArm.Continue(OpUpdate.effect(SideEffectLevel.STRONG)))
        is ArrayOperand.Tracked -> {
            if (payloadElements.size > array.array.length) {
                OpExecution.single(GuestThrowables.throwing(overrun(array.array.length)))
            } else {
                val write = OpUpdate.writeArrayElements(
                    array.ref,
                    0,
                    payloadElements.map { elementValue(it, array.component) },
                )
                val update = if (payloadElements.isEmpty()) write else write.then(OpUpdate.effect(SideEffectLevel.WEAK))
                OpExecution.single(OpArm.Continue(update))
            }
        }
    }

    /**
     * The payload element as it lands in the slot: read at [elementWidth], then as the component holds those
     * bits. ART copies the payload's bytes, so `F`/`D` reinterpret them and a `Z`/`B`/`C`/`S` element keeps
     * them as [Coerce.narrowStore] does. A width the verifier rejects leaves the element as decoded.
     */
    private fun elementValue(element: Number, component: VirtualType): Value {
        val bits = when (elementWidth) {
            1 -> element.toByte()
            2 -> element.toShort()
            4 -> element.toInt()
            8 -> element.toLong()
            else -> element
        }
        val raw: Any = when (component.descriptor) {
            "Z", "B", "C", "S" -> return Coerce.narrowStore(Value.Known(bits.toInt(), INT), component)
            "I" -> bits.toInt()
            "J" -> bits.toLong()
            "F" -> Float.fromBits(bits.toInt())
            "D" -> Double.fromBits(bits.toLong())
            else -> bits
        }
        return Value.Known(raw, component)
    }

    private fun overrun(length: Int?): ThrowableRecipe = GuestThrowables.fillArrayDataOverrun(length, payloadElements.size)

    /** The payload is decoded away at build time, so its address is not rendered. */
    override fun toString(): String = "$name r$arrayRegister, :payload"

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
        val NULL_ARRAY = GuestThrowables.nullPointer(GuestThrowables.NullOperand.FILL_ARRAY_DATA)
    }
}
