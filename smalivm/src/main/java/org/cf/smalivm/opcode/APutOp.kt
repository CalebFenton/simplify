package org.cf.smalivm.opcode

import org.cf.smalivm.ExceptionFactory
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.opcode.APutOp
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualArray
import org.cf.smalivm.type.VirtualClass
import org.cf.util.ClassNameUtils
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.lang.reflect.Array

class APutOp internal constructor(
    location: MethodLocation, child: MethodLocation?, private val valueRegister: Int, private val arrayRegister: Int, private val indexRegister: Int,
    private val classManager: ClassManager, private val exceptionFactory: ExceptionFactory
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val valueItem = mState.readRegister(valueRegister)
        var arrayItem = mState.readRegister(arrayRegister)
        val indexItem = mState.readRegister(indexRegister)
        val throwsStoreException = throwsArrayStoreException(arrayItem, valueItem, classManager)
        if (throwsStoreException) {
            val storeType = ClassNameUtils.internalToBinary(valueItem.type)
            val exception = exceptionFactory.build(this, ArrayStoreException::class.java, storeType)
            node.setException(exception)
            node.clearChildren()
            return
        }
        if (arrayItem.isUnknown) {
            // Do nothing.
        } else {
            if (valueItem.isUnknown || indexItem.isUnknown) {
                val type = arrayItem.type
                arrayItem = HeapItem.newUnknown(type)
            } else {
                val array = arrayItem.value
                if (null == array) {
                    val exception = exceptionFactory.build(this, NullPointerException::class.java)
                    node.setException(exception)
                    node.clearChildren()
                    return
                }
                val index = indexItem.asInteger()
                if (index >= Array.getLength(array)) {
                    val exception = exceptionFactory.build(this, ArrayIndexOutOfBoundsException::class.java)
                    node.setException(exception)
                    node.clearChildren()
                    return
                } else {
                    val value = castValue(name, valueItem.value)
                    Array.set(array, index, value)
                    node.clearExceptions()
                }
            }
        }
        mState.assignRegister(arrayRegister, arrayItem)
    }

    override fun toString(): String {
        return "$name r$valueRegister, r$arrayRegister, r$indexRegister"
    }

    companion object {
        private val log = LoggerFactory.getLogger(APutOp::class.java.simpleName)
        private fun castValue(opName: String?, value: Any?): Any? {
            var value = value
            if (value is Number) {
                if (opName!!.endsWith("-boolean")) {
                    // Booleans are represented by integer literals; need to convert
                    val intValue = Utils.getIntegerValue(value)
                    value = intValue == 1
                } else {
                    val intValue = Utils.getIntegerValue(value)
                    if (opName.endsWith("-byte")) {
                        value = intValue.toByte()
                    } else if (opName.endsWith("-char")) {
                        // Characters, like boolean, are represented by integers
                        value = intValue.toInt().toChar()
                    } else if (opName.endsWith("-short")) {
                        value = intValue.toShort()
                    } else if (opName.endsWith("-object") && intValue == 0) {
                        // const/4 v0, 0x0 is null
                        value = null
                    }
                }
            }
            return value
        }

        private fun isOverloadedPrimitiveType(type: String): Boolean {
            return ClassNameUtils.isPrimitive(type) &&
                    !(CommonTypes.FLOAT == type || CommonTypes.DOUBLE == type || CommonTypes.LONG == type)
        }

        private fun throwsArrayStoreException(
            arrayItem: HeapItem, valueItem: HeapItem,
            classManager: ClassManager
        ): Boolean {
            val valueType = classManager.getVirtualType(valueItem.type)
            val arrayTypeType = classManager.getVirtualType(arrayItem.type)
            if (arrayTypeType is VirtualClass && arrayTypeType.getName() == CommonTypes.OBJECT) {
                val e = Exception("APutOp")
                log.warn(
                    "Attempting to store item in array of type java.lang.Object. Not enough type information to know if this may throw an exception.",
                    e
                )
                return false
            }
            val arrayType = arrayTypeType as VirtualArray
            val arrayComponentType = arrayType.componentType
            if (valueType.instanceOf(arrayComponentType)) {
                return false
            }

            // Types: Z B C S I are all represented identically in bytecode (e.g. const/4)
            val valueTypeName = valueType.name
            val arrayComponentTypeName = arrayComponentType.name
            if (isOverloadedPrimitiveType(valueTypeName) && isOverloadedPrimitiveType(arrayComponentTypeName)) {
                // Trying to store something that looks like one type into another type, but they're compatible.
                // For example, a method returns Z, which is actually either 0x1 or 0x0 and is then storing it in an int
                // array.
                // TODO: figure out what dalvik actually does when you try to aput 0x2 into [B
                // also try to find other edge cases, like Integer.MAX_VALUE in [S
                return false
            }

            // Trying to store a known value of 0 and a primitive type of integer into an array of object reference.
            // This is how Dalvik does null.
            // TODO: see what happens when Dalvik makes a Z and uses as null, might want to consider other types
            val storingNull = !valueItem.isUnknown && valueTypeName == CommonTypes.INTEGER && valueItem.asInteger() == 0 &&
                    !ClassNameUtils.isPrimitive(arrayComponentTypeName)
            return !storingNull
        }
    }

    init {
        addException(exceptionFactory.build(this, NullPointerException::class.java))
        addException(exceptionFactory.build(this, ArrayIndexOutOfBoundsException::class.java))
    }
}
