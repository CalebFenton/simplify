package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffectLevel
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualField
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.FieldReference
import org.slf4j.LoggerFactory

class SPutOp internal constructor(
    location: MethodLocation,
    val valueRegister: Int,
    val field: VirtualField
) : Op(location) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        return if (node.shouldInitializeClass(field.definingClass)) {
            staticInitClass(field.definingClass)
        } else {
            resume(node)
        }
    }

    override fun resume(node: ExecutionNode): Array<out UnresolvedChild> {
        val item = node.state.readRegister(valueRegister)
        // TODO: check if this is <clinit> and only allow static final fields to be initialized here
        node.state.assignField(field, item)
        node.sideEffectLevel = SideEffectLevel.WEAK
        return finishOp()
    }

    override fun toString() = "$name r$valueRegister, $field"

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(SPutOp::class.java.simpleName)

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
            return SPutOp(location, destRegister, field)
        }
    }
}
