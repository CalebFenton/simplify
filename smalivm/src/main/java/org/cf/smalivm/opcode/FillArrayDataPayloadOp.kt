package org.cf.smalivm.opcode

import org.apache.commons.lang3.ClassUtils
import org.cf.smalivm.context.*
import org.cf.smalivm.opcode.FillArrayDataPayloadOp
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.lang.reflect.Array

class FillArrayDataPayloadOp  // childAddress / returnAddress not known until runtime
internal constructor(location: MethodLocation, private val elementWidth: Int, private val arrayElements: List<Number>) : MethodStateOp(location) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val parent = mState.parent
        val targetRegister = parent.registersAssigned[0]
        // Peek rather than read. This pseudo-instruction shouldn't count as an actual usage for the optimizer.
        val arrayItem = mState.peekRegister(targetRegister)
        if (!arrayItem!!.isUnknown) {
            val array = arrayItem.value
            val expectedClass = array!!.javaClass.componentType
            for (i in arrayElements.indices) {
                val number = arrayElements[i]
                val value = getProperValue(number, expectedClass)
                Array.set(array, i, value)
            }
            // Poke rather than assign for the optimizer.
            mState.pokeRegister(targetRegister, arrayItem)
        }
        val returnLocation = mState.parent.pseudoInstructionReturnInstruction
        node.setChildLocations(returnLocation)
    }

    override fun toString(): String {
        val sb = StringBuilder(".array-data ")
        sb.append(elementWidth)
        for (number in arrayElements) {
            sb.append("\n\t").append(number.toString())
        }
        sb.append("\n.end array-data")
        return sb.toString()
    }

    companion object {
        private val log = LoggerFactory.getLogger(FillArrayDataPayloadOp::class.java.simpleName)
        private fun getProperValue(number: Number, expectedClass: Class<*>): Any? {
            val klazz = ClassUtils.wrapperToPrimitive(number.javaClass)
            var value: Any? = null

            // Dexlib will only ever make byte (t), int, long (l), or short (s)
            if (klazz == Byte::class.javaPrimitiveType) {
                value = number.toByte()
            } else if (klazz == Short::class.javaPrimitiveType) {
                value = number.toShort()
            } else if (klazz == Int::class.javaPrimitiveType) {
                value = number.toInt()
            } else if (klazz == Long::class.javaPrimitiveType) {
                value = number.toLong()
            }
            if (expectedClass == Boolean::class.javaPrimitiveType) {
                value = value as Byte.toInt() == 1
            } else if (expectedClass == Char::class.javaPrimitiveType) {
                value = number.toShort().toChar()
            } else if (expectedClass == Short::class.javaPrimitiveType) {
                value = number.toShort()
            } else if (expectedClass == Int::class.javaPrimitiveType) {
                value = number.toInt()
            } else if (expectedClass == Long::class.javaPrimitiveType) {
                value = number.toLong()
            } else if (expectedClass == Float::class.javaPrimitiveType) {
                value = java.lang.Float.intBitsToFloat(number.toInt())
            } else if (expectedClass == Double::class.javaPrimitiveType) {
                value = java.lang.Double.longBitsToDouble(number.toLong())
            }
            return value
        }
    }
}
