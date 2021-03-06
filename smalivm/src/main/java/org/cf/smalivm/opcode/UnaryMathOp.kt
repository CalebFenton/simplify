package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation

class UnaryMathOp internal constructor(location: MethodLocation, child: MethodLocation?, val destRegister: Int, val srcRegister: Int) :
    MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val item = mState.readRegister(srcRegister)
        val resultItem: HeapItem
        val type = getResultTypeName(name)
        resultItem = if (item.isUnknown) {
            HeapItem.newUnknown(type)
        } else {
            val resultValue = perform(item.value, name)
            HeapItem(resultValue, type)
        }
        mState.assignRegister(destRegister, resultItem)
    }

    override fun toString(): String {
        return "$name r$destRegister, r$srcRegister"
    }

    private fun perform(value: Any?, opName: String?): Any? {
        // Stupid, but simple. Deals.
        var resultValue: Any? = null
        if (opName!!.startsWith("double")) {
            val typedValue = Utils.getDoubleValue(value)
            if (opName.endsWith("float")) {
                resultValue = typedValue.toFloat()
            } else if (opName.endsWith("int")) {
                resultValue = typedValue.toInt()
            } else if (opName.endsWith("long")) {
                resultValue = typedValue.toLong()
            }
        } else if (opName.startsWith("float")) {
            val typedValue = Utils.getFloatValue(value)
            if (opName.endsWith("double")) {
                resultValue = typedValue.toDouble()
            } else if (opName.endsWith("int")) {
                resultValue = typedValue.toInt()
            } else if (opName.endsWith("long")) {
                resultValue = typedValue.toLong()
            }
        } else if (opName.startsWith("long")) {
            val typedValue = Utils.getLongValue(value)
            if (opName.endsWith("double")) {
                resultValue = typedValue.toDouble()
            } else if (opName.endsWith("int")) {
                resultValue = typedValue.toInt()
            } else if (opName.endsWith("float")) {
                resultValue = typedValue.toFloat()
            }
        } else if (opName.startsWith("int")) {
            // Could be something other than an int, such as short
            val typedValue = Utils.getIntegerValue(value)
            if (opName.endsWith("byte")) {
                resultValue = typedValue.toByte()
            } else if (opName.endsWith("char")) {
                resultValue = typedValue.toInt().toChar()
            } else if (opName.endsWith("short")) {
                resultValue = typedValue.toShort()
            } else if (opName.endsWith("double")) {
                resultValue = typedValue.toDouble()
            } else if (opName.endsWith("long")) {
                resultValue = typedValue.toLong()
            } else if (opName.endsWith("float")) {
                resultValue = typedValue.toFloat()
            }
        } else if (opName.startsWith("neg")) {
            if (opName.endsWith("double")) {
                val typedValue = Utils.getDoubleValue(value)
                resultValue = -typedValue
            } else if (opName.endsWith("float")) {
                val typedValue = Utils.getFloatValue(value)
                resultValue = -typedValue
            } else if (opName.endsWith("int")) {
                val typedValue = Utils.getIntegerValue(value)
                resultValue = -typedValue
            } else if (opName.endsWith("long")) {
                val typedValue = Utils.getLongValue(value)
                resultValue = -typedValue
            }
        } else if (opName.startsWith("not")) {
            if (opName.endsWith("int")) {
                val typedValue = Utils.getIntegerValue(value)
                resultValue = typedValue.inv()
            } else if (opName.endsWith("long")) {
                val typedValue = Utils.getLongValue(value)
                resultValue = typedValue.inv()
            }
        }
        return resultValue
    }

    companion object {
        private fun getResultTypeName(opName: String?): String? {
            val parts = opName!!.split("-").toTypedArray()
            val type = parts[parts.size - 1]
            var resultType: String? = null
            if ("int" == type) {
                resultType = "I"
            } else if ("long" == type) {
                resultType = "J"
            } else if ("float" == type) {
                resultType = "F"
            } else if ("byte" == type) {
                resultType = "B"
            } else if ("char" == type) {
                resultType = "C"
            } else if ("double" == type) {
                resultType = "D"
            } else if ("short" == type) {
                resultType = "S"
            }
            return resultType
        }
    }
}
