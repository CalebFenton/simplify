package org.cf.smalivm.opcode

import org.cf.smalivm.ExceptionFactory
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.opcode.AGetOp
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionState
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction23x
import org.slf4j.LoggerFactory
import java.lang.reflect.Array

class AGetOp internal constructor(
    location: MethodLocation, child: MethodLocation?, private val valueRegister: Int, private val arrayRegister: Int, private val indexRegister: Int,
    private val exceptionFactory: ExceptionFactory
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val arrayItem = mState.readRegister(arrayRegister)
        val indexItem = mState.readRegister(indexRegister)
        val getItem: HeapItem
        if (arrayItem.isUnknown) {
            val innerType = getUnknownArrayInnerType(arrayItem)
            getItem = HeapItem.newUnknown(innerType)
        } else {
            val array = arrayItem.value
            if (indexItem.isUnknown) {
                val innerType = arrayItem.type.replaceFirst("\\[".toRegex(), "")
                getItem = HeapItem.newUnknown(innerType)
            } else {
                // All values known, so exceptions are deterministic.
                node.clearExceptions()
                if (null == array) {
                    val exception = exceptionFactory.build(this, NullPointerException::class.java)
                    node.setException(exception)
                    node.clearChildren()
                    return
                }
                val index = indexItem.asInteger()
                val innerType = arrayItem.type.replaceFirst("\\[".toRegex(), "")
                if (index >= Array.getLength(array)) {
                    val exception = exceptionFactory.build(this, ArrayIndexOutOfBoundsException::class.java)
                    node.setException(exception)
                    node.clearChildren()
                    return
                } else {
                    val value = Array.get(array, index)
                    getItem = HeapItem(value, innerType)
                    node.clearExceptions()
                }
            }
        }
        mState.assignRegister(valueRegister, getItem)
    }

    override fun execute(node: ExecutionNode, state: ExecutionState) {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "$name r$valueRegister, r$arrayRegister, r$indexRegister"
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(AGetOp::class.java.simpleName)
        private fun getUnknownArrayInnerType(array: HeapItem): String {
            val outerType = array.type
            val result: String
            result = if (CommonTypes.UNKNOWN == outerType) {
                CommonTypes.UNKNOWN
            } else {
                outerType.replaceFirst("\\[".toRegex(), "")
            }
            return result
        }

        override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, classManager: ClassManager, exceptionFactory: ExceptionFactory): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instr = location.instruction as Instruction23x
            val valueRegister = instr.registerA
            val arrayRegister = instr.registerB
            val indexRegister = instr.registerC
            return AGetOp(location, child, valueRegister, arrayRegister, indexRegister, exceptionFactory)
        }

    }

    init {
        exceptions.add(exceptionFactory.build(this, NullPointerException::class.java))
        exceptions.add(exceptionFactory.build(this, ArrayIndexOutOfBoundsException::class.java))
    }
}
