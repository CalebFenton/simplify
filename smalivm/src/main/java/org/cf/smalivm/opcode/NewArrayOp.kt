package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.opcode.NewArrayOp
import org.cf.smalivm.type.UnknownValue
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class NewArrayOp internal constructor(
    location: MethodLocation, child: MethodLocation?, val destRegister: Int, val lengthRegister: Int, val arrayType: String?,
    private val classLoader: ClassLoader
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val lengthItem = mState.readRegister(lengthRegister)
        val instance = buildInstance(lengthItem)
        if (instance is Throwable) {
            node.setException(instance)
            node.clearChildren()
            return
        } else {
            node.clearExceptions()
        }
        mState.assignRegister(destRegister, instance, arrayType)
    }

    override fun toString(): String {
        return "$name r$destRegister, r$lengthRegister, $arrayType"
    }

    private fun buildInstance(lengthItem: HeapItem): Any {
        val instance: Any
        instance = if (lengthItem.isUnknown) {
            UnknownValue()
        } else {
            val length = lengthItem.asInteger()
            try {
                // Dalvik does not statically initialize classes with new-array
                Utils.buildArray(arrayType, length, classLoader)
            } catch (e: ClassNotFoundException) {
                return e
            }
        }
        return instance
    }

    companion object {
        private val log = LoggerFactory.getLogger(NewArrayOp::class.java.simpleName)
    }
}
