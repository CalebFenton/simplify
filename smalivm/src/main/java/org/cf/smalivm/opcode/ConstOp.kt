package org.cf.smalivm.opcode

import org.apache.commons.lang3.ClassUtils
import org.cf.smalivm.ExceptionFactory
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.opcode.ConstOp
import org.cf.util.ClassNameUtils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class ConstOp internal constructor(
    location: MethodLocation, child: MethodLocation?, val destRegister: Int,
    val constantType: ConstantType, val literal: Any, val classLoader: ClassLoader,
    exceptionFactory: ExceptionFactory
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val constant = buildConstant()
        if (constant is Throwable) {
            node.setException(constant)
            node.clearChildren()
            return
        } else {
            node.clearExceptions()
        }
        mState.assignRegister(destRegister, constant, constantTypeString)
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" r").append(destRegister).append(", ")
        var `val`: String
        when (constantType) {
            ConstantType.CLASS -> sb.append(literal)
            ConstantType.NARROW -> {
                `val` = Integer.toString(literal as Int, 16)
                if (`val`.startsWith("-")) {
                    sb.append('-')
                    `val` = `val`.substring(1)
                }
                sb.append("0x").append(`val`)
            }
            ConstantType.STRING -> sb.append('"').append(literal as String).append('"')
            ConstantType.WIDE -> {
                `val` = java.lang.Long.toString(literal as Long, 16)
                if (`val`.startsWith("-")) {
                    sb.append('-')
                    `val` = `val`.substring(1)
                }
                sb.append("0x").append(`val`)
            }
            else -> {
            }
        }
        return sb.toString()
    }

    private fun buildConstant(): Any {
        val constant: Any
        constant = if (ConstantType.CLASS == constantType) {
            val className = literal as String
            try {
                val binaryClassName = ClassNameUtils.internalToBinary(className)
                ClassUtils.getClass(classLoader, binaryClassName)
            } catch (e: ClassNotFoundException) {
                return e
            }
        } else {
            literal
        }
        return constant
    }

    // Type string is ambiguous because Dalvik treats a multiple types as int (char, byte, etc.) and multiple
    // types as "wide" (long, float). Logic elsewhere must infer actual types when necessary, such as by opcode.
    private val constantTypeString: String
        private get() {
            // Type string is ambiguous because Dalvik treats a multiple types as int (char, byte, etc.) and multiple
            // types as "wide" (long, float). Logic elsewhere must infer actual types when necessary, such as by opcode.
            val type: String
            type = when (constantType) {
                ConstantType.CLASS -> CommonTypes.CLASS
                ConstantType.NARROW -> CommonTypes.INTEGER
                ConstantType.STRING -> CommonTypes.STRING
                ConstantType.WIDE -> if ("const-wide" == name) {
                    CommonTypes.DOUBLE
                } else {
                    CommonTypes.LONG
                }
                else -> {
                    if (log.isWarnEnabled) {
                        log.warn("Unexpected constant class (should never happen): {}", constantType)
                    }
                    CommonTypes.UNKNOWN
                }
            }
            return type
        }

    enum class ConstantType {
        CLASS, NARROW, STRING, WIDE
    }

    companion object {
        private val log = LoggerFactory.getLogger(ConstOp::class.java.simpleName)
    }

    init {
        if (ConstantType.CLASS == constantType) {
            addException(
                exceptionFactory.build(this, ClassNotFoundException::class.java, literal as String)
            )
        }
    }
}
