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
) : Op(location, child, NullPointerException::class.java, ArrayIndexOutOfBoundsException::class.java) {

    override val registersReadCount = 2
    override val registersAssignedCount = 2

    override fun execute(node: ExecutionNode): kotlin.Array<out OpChild> {
        val array = node.state.readRegister(arrayRegister)
        val index = node.state.readRegister(indexRegister)
        val indexed: Value

        var mayThrow = true
        if (array.isUnknown) {
            val innerType = getUnknownArrayInnerType(array)
            indexed = Value.unknown(innerType)
        } else {
            if (index.isUnknown) {
                val innerType = array.type.replaceFirst("\\[".toRegex(), "")
                indexed = Value.unknown(innerType)
            } else {
                if (array.isNull) {
                    return throwChild(NullPointerException::class.java)
                }
                val innerType = array.type.replaceFirst("\\[".toRegex(), "")
                if (index.toInteger() >= Array.getLength(array.value)) {
                    return throwChild(ArrayIndexOutOfBoundsException::class.java)
                } else {
                    val raw = Array.get(array.value, index.toInteger())
                    indexed = Value.wrap(raw, innerType)
                    mayThrow = false
                }
            }
        }
        node.state.assignRegister(destRegister, indexed)
        return collectChildren(mayThrow)
    }

    override fun toString() = "$name r$destRegister, r$arrayRegister, r$indexRegister"

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(AGetOp::class.java.simpleName)

        private fun getUnknownArrayInnerType(array: Value): String {
            val outerType = array.type
            return if (CommonTypes.UNKNOWN == outerType) {
                CommonTypes.UNKNOWN
            } else {
                outerType.replaceFirst("\\[".toRegex(), "")
            }
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
            return AGetOp(location, child, valueRegister, arrayRegister, indexRegister)
        }
    }
}
