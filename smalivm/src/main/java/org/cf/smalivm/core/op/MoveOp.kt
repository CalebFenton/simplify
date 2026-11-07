package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SpecialSlotSource

/**
 * Moves a value into a register, from another register or from one of the frame's slots. A slot read
 * consumes the slot, and an empty slot yields `Top`.
 */
internal class MoveOp(
    address: Int,
    override val name: String,
    private val toRegister: Int,
    private val isWide: Boolean,
    private val source: Source,
) : Op(address) {

    internal sealed interface Source {
        /** `move*`: another register. */
        data class Register(val register: Int) : Source

        /** `move-exception` and `move-result*`, which name only a destination. */
        data class Slot(val slot: SpecialSlotSource) : Source
    }

    override val registersRead: Set<Int> = when (source) {
        is Source.Register -> setOf(source.register)
        is Source.Slot -> emptySet()
    }

    override val registersAssigned: Set<Int> = destinationRegisters(toRegister, isWide)

    override fun execute(frame: Frame): OpExecution = OpExecution.single(
        when (source) {
            is Source.Register -> OpArm.Continue(OpUpdate.assign(toRegister, frame.readFrameValue(source.register)))
            is Source.Slot -> OpArm.MoveFromSlot(toRegister, source.slot, isWide)
        },
    )
}
