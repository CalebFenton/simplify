package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.Value
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction12x

class UnaryMathOp internal constructor(location: MethodLocation, child: MethodLocation, val destRegister: Int, val srcRegister: Int) :
    Op(location, child) {

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        val item = node.state.readRegister(srcRegister)
        val result: Value
        val type = getResultTypeName(name)
        result = if (item.isUnknown) {
            Value.unknown(type)
        } else {
            val resultValue = perform(item, name)
            Value.wrap(resultValue, type)
        }
        node.state.assignRegister(destRegister, result)
        return finishOp()
    }

    override fun toString() = "$name r$destRegister, r$srcRegister"

    private fun perform(value: Value, opName: String): Any {
        return when {
            opName.startsWith("double") -> {
                val typedValue = value.toDouble()
                when {
                    opName.endsWith("float") -> typedValue.toFloat()
                    opName.endsWith("long") -> typedValue.toLong()
                    opName.endsWith("int") -> typedValue.toInt()
                    else -> throw IllegalArgumentException("Unexpected op name $opName")
                }
            }
            opName.startsWith("float") -> {
                val typedValue = value.toFloat()
                when {
                    opName.endsWith("double") -> typedValue.toDouble()
                    opName.endsWith("long") -> typedValue.toLong()
                    opName.endsWith("int") -> typedValue.toInt()
                    else -> throw IllegalArgumentException("Unexpected op name $opName")
                }
            }
            opName.startsWith("long") -> {
                val typedValue = value.toLong()
                when {
                    opName.endsWith("double") -> typedValue.toDouble()
                    opName.endsWith("float") -> typedValue.toFloat()
                    opName.endsWith("int") -> typedValue.toInt()
                    else -> throw IllegalArgumentException("Unexpected op name $opName")
                }
            }
            opName.startsWith("int") -> {
                // Could be something other than an int, such as short
                val typedValue = value.toInteger()
                when {
                    opName.endsWith("byte") -> typedValue.toByte()
                    opName.endsWith("char") -> typedValue.toChar()
                    opName.endsWith("short") -> typedValue.toShort()
                    opName.endsWith("double") -> typedValue.toDouble()
                    opName.endsWith("long") -> typedValue.toLong()
                    opName.endsWith("float") -> typedValue.toFloat()
                    else -> throw IllegalArgumentException("Unexpected op name $opName")
                }
            }
            opName.startsWith("neg") -> {
                when {
                    opName.endsWith("double") -> -value.toDouble()
                    opName.endsWith("float") -> -value.toFloat()
                    opName.endsWith("long") -> -value.toLong()
                    opName.endsWith("int") -> -value.toInteger()
                    else -> throw IllegalArgumentException("Unexpected op name $opName")
                }
            }
            opName.startsWith("not") -> {
                when {
                    opName.endsWith("int") -> value.toInteger().inv()
                    opName.endsWith("long") -> value.toLong().inv()
                    else -> throw IllegalArgumentException("Unexpected op name $opName")
                }
            }
            else -> throw IllegalArgumentException("Unexpected op name $opName")
        }
    }

    companion object : OpFactory {
        private fun getResultTypeName(opName: String): String {
            val parts = opName.split("-").toTypedArray()
            return when (parts[parts.size - 1]) {
                "int" -> CommonTypes.INTEGER
                "long" -> CommonTypes.LONG
                "float" -> CommonTypes.FLOAT
                "byte" -> CommonTypes.BYTE
                "char" -> CommonTypes.CHARACTER
                "double" -> CommonTypes.DOUBLE
                "short" -> CommonTypes.SHORT
                else -> throw IllegalArgumentException("Unexpected op name $opName")
            }
        }

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction12x
            val destRegister = instr.registerA
            val srcRegister = instr.registerB
            return UnaryMathOp(location, child, destRegister, srcRegister)
        }
    }
}
