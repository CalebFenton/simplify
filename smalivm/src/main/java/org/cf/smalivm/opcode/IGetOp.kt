package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.UnresolvedChild
import org.cf.smalivm.Value
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.formatter.DexFormatter
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.reference.FieldReference
import org.slf4j.LoggerFactory

class IGetOp(
    location: MethodLocation,
    private val destRegister: Int,
    private val instanceRegister: Int,
    private val fieldDescriptor: String
) : Op(location) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        // TODO: https://github.com/CalebFenton/simplify/issues/22
        val instance = node.state.readRegister(instanceRegister)
        val type = fieldDescriptor.split(":").toTypedArray()[1]
        node.state.assignRegister(destRegister, Value.unknown(type))
        return finishOp()
    }

    override fun toString() = "$name r$destRegister, r$instanceRegister, $fieldDescriptor"

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(IGetOp::class.java.simpleName)

        override fun build(
            location: MethodLocation,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instr = location.instruction as Instruction22c
            val destRegister = instr.registerA
            val instanceRegister = instr.registerB
            val reference = instr.reference as FieldReference
            val fieldDescriptor = DexFormatter.INSTANCE.getFieldDescriptor(reference)
            return IGetOp(location, destRegister, instanceRegister, fieldDescriptor)
        }
    }
}
