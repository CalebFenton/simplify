package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.Value
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory

class NewArrayOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    val destRegister: Int,
    val lengthRegister: Int,
    val arrayType: String
) : Op(location, child) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        val lengthItem = node.state.readRegister(lengthRegister)
        val instance = buildInstance(lengthItem, node.classLoader)
        if (instance is Throwable) {
            return finishOp(instance)
        }
        node.state.assignRegister(destRegister, instance, arrayType)
        return finishOp()
    }

    override fun toString() ="$name r$destRegister, r$lengthRegister, $arrayType"

    private fun buildInstance(lengthItem: Value, classLoader: SmaliClassLoader): Any {
        return if (lengthItem.isUnknown) {
            UnknownValue()
        } else {
            val length = lengthItem.toInteger()
            try {
                // Dalvik does not statically initialize classes with new-array
                Utils.buildArray(arrayType, length, classLoader)
            } catch (e: ClassNotFoundException) {
                return e
            }
        }
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(NewArrayOp::class.java.simpleName)

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction22c
            val destRegister = instr.registerA
            val sizeRegister = instr.registerB
            val arrayType = ReferenceUtil.getReferenceString(instr.reference)!!
            return NewArrayOp(location, child, destRegister, sizeRegister, arrayType)
        }
    }
}
