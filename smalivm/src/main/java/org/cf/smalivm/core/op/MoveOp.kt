package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SpecialSlotSource
import org.cf.smalivm.core.Value

/**
 * Moves a value into a register. [Source] says where the value comes from: another register, or one
 * of the [Frame]'s typed slots -- and a slot read **consumes** it (spec §4.2 slot lifecycle). A slot
 * that is empty yields [Value.Top] rather than a fabricated value: nothing produced it, so nothing is
 * known about it.
 *
 * [Source] is a sealed hierarchy rather than the nullable `fromRegister` it replaced because the
 * three sources are mutually exclusive and a nullable `Int` can encode only ONE slot. With
 * `move-result*` joining this family, `fromRegister == null` would have had to mean "caught OR
 * result" and `execute` could no longer tell them apart -- a `move-result` reading the caught slot
 * silently moves the wrong value into a register, which is exactly the class of defect an exhaustive
 * `when` over a sealed type makes unwritable.
 */
internal class MoveOp(
    address: Int,
    override val name: String,
    private val toRegister: Int,
    private val isWide: Boolean,
    private val source: Source,
) : Op(address) {

    /** Where a move reads from. */
    internal sealed interface Source {
        /** Another register: `move`/`move-object`/`move-wide` and their wider-operand forms. */
        data class Register(val register: Int) : Source

        /** The caught-exception slot: `move-exception`, which names only a destination. */
        data object Caught : Source

        /** The invoke-result slot: `move-result*`, which name only a destination. */
        data object Result : Source
    }

    override val registersRead: Set<Int> get() = when (source) {
        is Source.Register -> setOf(source.register)
        Source.Caught, Source.Result -> emptySet()
    }

    override val registersAssigned: Set<Int> get() = destinationRegisters(toRegister, isWide)

    override fun execute(frame: Frame): OpExecution {
        return when (source) {
            is Source.Register -> OpExecution.single(
                OpArm.Continue(
                    OpUpdate.assign(
                        toRegister,
                        frame.readFrameValue(source.register),
                    ),
                ),
            )
            Source.Caught -> OpExecution.single(
                OpArm.MoveFromSlot(toRegister, SpecialSlotSource.CAUGHT, isWide),
            )
            Source.Result -> OpExecution.single(
                OpArm.MoveFromSlot(toRegister, SpecialSlotSource.RESULT, isWide),
            )
        }
    }
}
