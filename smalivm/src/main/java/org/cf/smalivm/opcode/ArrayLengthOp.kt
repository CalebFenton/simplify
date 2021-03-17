package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm2.ExecutionState
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction12x
import org.slf4j.LoggerFactory
import java.lang.reflect.Array

class ArrayLengthOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val destRegister: Int,
    private val arrayRegister: Int,
    exceptionFactory: ExceptionFactory
) : Op(location, child) {

    init {
        exceptions.add(exceptionFactory.build(this, NullPointerException::class.java, "Attempt to get length of null array"))
    }

    override fun execute(node: ExecutionNode, state: ExecutionState) {
        val array = state.readRegister(arrayRegister)
        if (array.value == null) {
            node.clearChildren()
            return
        }
        val length: Any = when {
            array.isUnknown() -> UnknownValue()
            array.value.javaClass.isArray -> {
                node.clearExceptions()
                Array.getLength(array.value)
            }
            else -> {
                // Won't pass DEX verifier if it's not an array class. Probably our fault, so error.
                log.error("Unexpected non-array class: {}, {}", array.value.javaClass, array.value)
                UnknownValue()
            }
        }
        state.assignRegister(destRegister, length, CommonTypes.INTEGER)
    }

    override fun getRegistersReadCount(): Int {
        return 1
    }

    override fun getRegistersAssignedCount(): Int {
        return 1
    }

    override fun toString(): String {
        return "$name r$destRegister, r$arrayRegister"
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(ArrayLengthOp::class.java.simpleName)
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            exceptionFactory: ExceptionFactory,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction12x
            val destRegister = instr.registerA
            val arrayRegister = instr.registerB
            return ArrayLengthOp(location, child, destRegister, arrayRegister, exceptionFactory)
        }
    }
}
