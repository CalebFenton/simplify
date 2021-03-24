package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.opcode.SGetOp
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

class SGetOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val destRegister: Int,
    private val fieldReference: FieldReference,
    private val actualField: VirtualField
) : Op(location, child) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        val item = node.state.peekField(actualField)
        node.state.assignRegister(destRegister, item)
        return collectChildren()
    }

    override fun toString() = "$name r$destRegister, ${ReferenceUtil.getFieldDescriptor(fieldReference)}"

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(SGetOp::class.java.simpleName)

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction21c
            val destRegister = instr.registerA
            val fieldReference = instr.reference as FieldReference
            val fieldClass = classManager.getVirtualClass(fieldReference.definingClass)
            val actualField = fieldClass.getField(fieldReference.name)!!
            return SGetOp(location, child, destRegister, fieldReference, actualField)
        }

    }
}
