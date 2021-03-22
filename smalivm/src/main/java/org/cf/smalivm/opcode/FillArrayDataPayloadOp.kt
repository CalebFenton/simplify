package org.cf.smalivm.opcode

import ExceptionFactory
import org.apache.commons.lang3.ClassUtils
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.ArrayPayload
import org.slf4j.LoggerFactory
import java.lang.reflect.Array

class FillArrayDataPayloadOp internal constructor(
    location: MethodLocation,
    private val elementWidth: Int,
    private val arrayElements: List<Number>
) :
    Op(location) {

    override val registersReadCount = 2
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): kotlin.Array<out OpChild> {
        val parent = node.state.getParent()!!
        val targetRegister = parent.registersAssigned.toList()[0]
        val array = node.state.peekRegister(targetRegister)!!
        if (array.isKnown) {
            val expectedClass = array.value!!.javaClass.componentType
            for (i in arrayElements.indices) {
                val number = arrayElements[i]
                val value = getProperValue(number, expectedClass)
                Array.set(array.value, i, value)
            }
            node.state.pokeRegister(targetRegister, array)
        }
        // This is a strange pseudo-op which should return to the next op after the FillArrayDataOp.
        val returnLocation = parent.getPsuedoInstructionReturnLocation()
        return collectChildren(returnLocation)
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

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(FillArrayDataPayloadOp::class.java.simpleName)

        private fun getProperValue(number: Number, expectedClass: Class<*>): Any {
            val klazz = ClassUtils.wrapperToPrimitive(number.javaClass)

            // Dexlib will only ever make byte (t), int, long (l), or short (s)
            val classValue = when (klazz) {
                Byte::class.javaPrimitiveType -> number.toByte()
                Short::class.javaPrimitiveType -> number.toShort()
                Int::class.javaPrimitiveType -> number.toInt()
                Long::class.javaPrimitiveType -> number.toLong()
                else -> throw IllegalArgumentException("Unexpected array data payload type: $klazz")
            }

            return when (expectedClass) {
                Boolean::class.javaPrimitiveType -> classValue.toInt() == 1
                Char::class.javaPrimitiveType -> number.toShort().toChar()
                Short::class.javaPrimitiveType -> number.toShort()
                Int::class.javaPrimitiveType -> number.toInt()
                Long::class.javaPrimitiveType -> number.toLong()
                Float::class.javaPrimitiveType -> java.lang.Float.intBitsToFloat(number.toInt())
                Double::class.javaPrimitiveType -> java.lang.Double.longBitsToDouble(number.toLong())
                else -> throw IllegalArgumentException("Unexpected array data payload expected type: $expectedClass")
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
            val instr = location.instruction as ArrayPayload
            val elementWidth = instr.elementWidth
            val arrayElements = instr.arrayElements
            return FillArrayDataPayloadOp(location, elementWidth, arrayElements)
        }
    }
}
