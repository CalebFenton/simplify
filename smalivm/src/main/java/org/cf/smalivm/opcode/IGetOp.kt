package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.opcode.IGetOp
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.Value
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory

class IGetOp(
    location: MethodLocation,
    child: MethodLocation,
    private val destRegister: Int,
    private val instanceRegister: Int,
    private val fieldDescriptor: String
) : Op(location, child) {
    override fun execute(node: ExecutionNode, state: ExecutionState) {
        // TODO: https://github.com/CalebFenton/simplify/issues/22
        val instance = state.readRegister(instanceRegister)
        val type = fieldDescriptor.split(":").toTypedArray()[1]
        state.assignRegister(destRegister, Value.unknown(type))
    }

    override fun getRegistersReadCount(): Int {
        return 1
    }

    override fun getRegistersAssignedCount(): Int {
        return 1
    }

    override fun toString(): String {
        return "$name r$destRegister, r$instanceRegister, $fieldDescriptor"
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(IGetOp::class.java.simpleName)
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
            val destRegister = instr.registerA
            val instanceRegister = instr.registerB
            val reference = instr.reference as FieldReference
            val fieldDescriptor = ReferenceUtil.getFieldDescriptor(reference)
            return IGetOp(location, child, destRegister, instanceRegister, fieldDescriptor)
        }
    }
}
