package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction12x
import org.slf4j.LoggerFactory
import java.lang.reflect.Array

class ArrayLengthOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val destRegister: Int,
    private val arrayRegister: Int
) : Op(location, child, Pair(NullPointerException::class.java, "Attempt to get length of null array")) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): kotlin.Array<out OpChild> {
        val array = node.state.readRegister(arrayRegister)
        if (array.isNull) {
            return throwChild(NullPointerException::class.java, "Attempt to get length of null array")
        }
        var mayThrow = true
        val length: Any = when {
            array.isUnknown -> UnknownValue()
            array.value!!.javaClass.isArray -> {
                mayThrow = false
                Array.getLength(array.value)
            }
            else -> {
                // How'd this happen? The code won't pass DEX verifier if it's not an array class.
                log.error("Unexpected non-array class: {}, {}; assuming unknown", array.value.javaClass, array.value)
                UnknownValue()
            }
        }
        node.state.assignRegister(destRegister, length, CommonTypes.INTEGER)
        return collectChildren(mayThrow)
    }

    override fun toString() = "$name r$destRegister, r$arrayRegister"

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
            return ArrayLengthOp(location, child, destRegister, arrayRegister)
        }
    }
}
