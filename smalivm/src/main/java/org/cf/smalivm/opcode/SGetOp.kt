package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualField
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.FieldReference
import org.slf4j.LoggerFactory

class SGetOp internal constructor(
    location: MethodLocation,
    private val destRegister: Int,
    private val field: VirtualField
) : Op(location) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        return if (!node.state.isClassInitialized(field.definingClass)) {
            staticInitClass(field.definingClass, node.classManager, node.classLoader, node.configuration)
        } else {
            val item = node.state.peekField(field)
            node.state.assignRegister(destRegister, item)
            finish()
        }
    }

    override fun toString() = "$name r$destRegister, $field"

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(SGetOp::class.java.simpleName)

        override fun build(
            location: MethodLocation,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instr = location.instruction as Instruction21c
            val destRegister = instr.registerA
            val fieldReference = instr.reference as FieldReference
            val fieldClass = classManager.getVirtualClass(fieldReference.definingClass)
            val field = fieldClass.getField(fieldReference.name)!!
            return SGetOp(location, destRegister, field)
        }
    }
}
