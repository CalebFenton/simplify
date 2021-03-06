package org.cf.smalivm.opcode

import org.cf.smalivm.ExceptionFactory
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.opcode.CheckCastOp
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualType
import org.cf.util.ClassNameUtils
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class CheckCastOp internal constructor(
    location: MethodLocation, child: MethodLocation?, private val targetRegister: Int, private val castType: VirtualType,
    private val classManager: ClassManager, private val exceptionFactory: ExceptionFactory
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val item = mState.readRegister(targetRegister)
        val isInstance = isInstance(item, castType, classManager)
        if (isInstance) {
            node.clearExceptions()
            mState.assignRegister(targetRegister, item.value, castType.name)
        } else {
            // E.g. java.lang.ClassCastException: java.lang.String cannot be cast to java.io.File
            val error = ClassNameUtils.internalToBinary(item.type) + " cannot be cast to " + castType.binaryName
            val exception = exceptionFactory.build(this, ClassCastException::class.java, error)
            node.setException(exception)
            if (!item.isUnknown) {
                // Exception is certain to happen since we had all class information
                // exception is certain.
                node.clearChildren()
            }
        }
    }

    override fun toString(): String {
        return "$name r$targetRegister, $castType"
    }

    companion object {
        private val log = LoggerFactory.getLogger(CheckCastOp::class.java.simpleName)
        private fun isInstance(item: HeapItem, referenceType: VirtualType, classManager: ClassManager): Boolean {
            /*
         * This covers cases where class type cannot be implied from value. E.g.
         * Object o = null;
         * String s = (String) o; // check-cast o as null passes
         */
            val itemType = classManager.getVirtualType(item.type)
            if (!itemType.isPrimitive && item.isNull) {
                return true
            }
            for (typeName in Utils.getDeclaredAndValueTypeNames(item)) {
                val type = classManager.getVirtualType(typeName)
                if (type.instanceOf(referenceType)) {
                    return true
                }
            }
            return false
        }
    }
}
