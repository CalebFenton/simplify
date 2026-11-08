package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.StaticCellProvenance
import org.cf.smalivm.core.StaticFieldLookup
import org.cf.smalivm.core.classInitTrigger
import org.cf.smalivm.core.noSuchFieldFailure
import org.cf.smalivm.core.resolutionFailure
import org.cf.smalivm.core.Value

/**
 * What must happen instead of a static access, in a device's order: resolution, then initialization of the
 * field's declaring class ([JVMS] §5.5), or null when the access may proceed. An absent class is not asked
 * about initialization, because it cannot be initialized. The trigger is a prelude the scheduler runs before
 * resuming at this address.
 */
private fun preamble(frame: Frame, field: StaticFieldLookup): OpExecution? = when (field) {
    is StaticFieldLookup.NoSuchClass -> resolutionFailure(field.type)
    is StaticFieldLookup.NoSuchField -> noSuchFieldFailure(field.descriptor)
    is StaticFieldLookup.Resolved -> frame.classInitTrigger(field.definingClass)
}

/**
 * Reads a static field into a register: all `sget*` forms. A prepared framework static whose value is
 * unknown is projected from the host by the scheduler.
 */
internal class SGetOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val field: StaticFieldLookup,
) : Op(address) {

    /**
     * The width comes from the resolved field's type, which keeps the declaration in agreement with what
     * `Frame.assign` reserves. An unresolved field throws before assigning, so it declares the narrow set.
     */
    override val registersAssigned: Set<Int> =
        destinationRegisters(destRegister, isWide = (field as? StaticFieldLookup.Resolved)?.type?.isWide == true)

    override fun execute(frame: Frame): OpExecution {
        preamble(frame, field)?.let { return it }
        val resolved = field as StaticFieldLookup.Resolved
        val cell = frame.classes.staticCell(resolved.descriptor)
        if (cell?.provenance == StaticCellProvenance.SEEDED_DEFAULT && cell.value is Value.Unknown) {
            return OpExecution.single(
                OpArm.ProjectStatic(destRegister, resolved.definingClass, resolved.descriptor, resolved.type),
            )
        }
        val stored = frame.getStaticField(resolved.definingClass, resolved.descriptor)
        return OpExecution.single(OpArm.Continue(fieldRead(destRegister, stored, resolved.type, resolved.isVolatile)))
    }

    override fun toString(): String = "$name r$destRegister, $field"
}

/**
 * Writes a register into a static field: all `sput*` forms. A static write outlives the frame, so it records
 * `WEAK`; the cell is named and tracked per path, so it is never `STRONG`.
 */
internal class SPutOp(
    address: Int,
    override val name: String,
    private val valueRegister: Int,
    private val field: StaticFieldLookup,
    private val storesAReference: Boolean,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(valueRegister)

    override fun execute(frame: Frame): OpExecution {
        preamble(frame, field)?.let { return it }
        val resolved = field as StaticFieldLookup.Resolved
        val stored = slotWrite(frame.read(valueRegister), resolved.type, storesAReference)
        val update = OpUpdate.writeStatic(resolved.descriptor, stored).then(OpUpdate.effect(SideEffectLevel.WEAK))
        return OpExecution.single(OpArm.Continue(update))
    }

    override fun toString(): String = "$name r$valueRegister, $field"
}
