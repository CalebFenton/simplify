package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualField
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory

class SPutOp internal constructor(
    location: MethodLocation, child: MethodLocation, val valueRegister: Int,
    val fieldReference: FieldReference, val actualField: VirtualField
) : Op(location, child) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        val item = node.state.readRegister(valueRegister)
        // TODO: check if this is <clinit> and only allow static final fields to be initialized here
        node.state.assignField(actualField, item)
        return collectChildren()
    }

    override val sideEffectLevel = SideEffect.Level.WEAK

    override fun toString() = "$name r$valueRegister, " + ReferenceUtil.getFieldDescriptor(fieldReference)

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(SPutOp::class.java.simpleName)
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction21c?
            val destRegister = instr!!.registerA
            val fieldReference = instr.reference as FieldReference
            val fieldClass = classManager.getVirtualClass(fieldReference.definingClass)
            val actualField = fieldClass.getField(fieldReference.name)!!
            return SPutOp(location, child, destRegister, fieldReference, actualField)
        }
    }
}
