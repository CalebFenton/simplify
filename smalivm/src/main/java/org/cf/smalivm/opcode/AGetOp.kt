package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.smalivm2.Value
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction23x
import org.slf4j.LoggerFactory
import java.lang.reflect.Array

class AGetOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val destRegister: Int,
    private val arrayRegister: Int,
    private val indexRegister: Int,
    private val exceptionFactory: ExceptionFactory
) : Op(location, child) {

    init {
        exceptions.add(exceptionFactory.build(this, NullPointerException::class.java))
        exceptions.add(exceptionFactory.build(this, ArrayIndexOutOfBoundsException::class.java))
    }

    override fun execute(node: ExecutionNode) : Array<OpChild> {
        val array = node.state.readRegister(arrayRegister)
        val index = node.state.readRegister(indexRegister)
        val indexed: Value
        if (array.isUnknown()) {
            val innerType = getUnknownArrayInnerType(array)
            indexed = Value.unknown(innerType)
        } else {
            if (index.isUnknown()) {
                val innerType = array.type.replaceFirst("\\[".toRegex(), "")
                indexed = Value.unknown(innerType)
            } else {
                // All values are known so it's possible to know for sure what exceptions are thrown, if any
                node.clearExceptions()
                if (array.value == null) {
                    val exception = exceptionFactory.build(this, NullPointerException::class.java)
                    node.addException(exception)
                    node.clearChildren()
                    return
                }
                val innerType = array.type.replaceFirst("\\[".toRegex(), "")
                if (index.asInteger() >= Array.getLength(array.value)) {
                    val exception = exceptionFactory.build(this, ArrayIndexOutOfBoundsException::class.java)
                    node.addException(exception)
                    node.clearChildren()
                    return
                } else {
                    indexed = Value.wrap(Array.get(array.value, index.asInteger()), innerType)
                    node.clearExceptions()
                }
            }
        }
        node.state.assignRegister(destRegister, indexed)
    }

    override fun getRegistersReadCount(): Int {
        return 2
    }

    override fun getRegistersAssignedCount(): Int {
        return 1
    }

    override fun toString(): String {
        return "$name r$destRegister, r$arrayRegister, r$indexRegister"
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(AGetOp::class.java.simpleName)

        private fun getUnknownArrayInnerType(array: Value): String {
            val outerType = array.type
            val result: String = if (CommonTypes.UNKNOWN == outerType) {
                CommonTypes.UNKNOWN
            } else {
                outerType.replaceFirst("\\[".toRegex(), "")
            }
            return result
        }

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            exceptionFactory: ExceptionFactory,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction23x
            val valueRegister = instr.registerA
            val arrayRegister = instr.registerB
            val indexRegister = instr.registerC
            return AGetOp(location, child, valueRegister, arrayRegister, indexRegister, exceptionFactory)
        }
    }
}
