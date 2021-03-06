package org.cf.smalivm.opcode

import org.cf.smalivm.ExceptionFactory
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.opcode.ArrayLengthOp
import org.cf.smalivm.type.UnknownValue
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.lang.reflect.Array

class ArrayLengthOp internal constructor(
    location: MethodLocation, child: MethodLocation?, private val destRegister: Int, private val arrayRegister: Int,
    exceptionFactory: ExceptionFactory
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val arrayItem = mState.readRegister(arrayRegister)
        val array = arrayItem.value
        var lengthValue: Any? = null
        if (arrayItem.isUnknown) {
            lengthValue = UnknownValue()
        } else if (array == null) {
            node.clearChildren()
            return
        } else if (array.javaClass.isArray) {
            lengthValue = Array.getLength(array)
            node.clearExceptions()
        } else {
            // Won't pass DEX verifier if it's not an array class. Probably our fault, so error.
            if (log.isErrorEnabled) {
                log.error("Unexpected non-array class: {}, {}", array.javaClass, array)
            }
        }
        mState.assignRegister(destRegister, lengthValue, CommonTypes.INTEGER)
    }

    override fun toString(): String {
        return "$name r$destRegister, r$arrayRegister"
    }

    companion object {
        private val log = LoggerFactory.getLogger(ArrayLengthOp::class.java.simpleName)
    }

    init {
        addException(exceptionFactory.build(this, NullPointerException::class.java, "Attempt to get length of null array"))
    }
}
