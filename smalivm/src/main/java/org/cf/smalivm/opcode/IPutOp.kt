package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.SideEffect
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory

class IPutOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val valueRegister: Int,
    private val instanceRegister: Int,
    private val fieldDescriptor: String
) : Op(location, child) {
    override val registersReadCount = 2
    override val registersAssignedCount = 1
    override val sideEffectLevel = SideEffect.Level.WEAK

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        val value = node.state.readRegister(valueRegister)
        val instance = node.state.readRegister(instanceRegister)
        node.state.assignRegister(instanceRegister, instance)
        return collectChildren()
    }

    override fun toString() = "$name r$valueRegister, r$instanceRegister, $fieldDescriptor"

    private companion object : OpFactory {
        private val log = LoggerFactory.getLogger(IPutOp::class.java.simpleName)
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            exceptionFactory: ExceptionFactory,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction22c
            val valueRegister = instr.registerA
            val instanceRegister = instr.registerB
            val reference = instr.reference as FieldReference
            val fieldDescriptor = ReferenceUtil.getFieldDescriptor(reference)
            return IPutOp(location, child, valueRegister, instanceRegister, fieldDescriptor)
        }
    }
}
