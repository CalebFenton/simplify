package org.cf.smalivm.opcode

import org.apache.commons.lang3.ClassUtils
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.util.ClassNameUtils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.formatter.DexFormatter
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction
import org.jf.dexlib2.iface.reference.StringReference
import org.slf4j.LoggerFactory

class ConstOp internal constructor(
    location: MethodLocation,
    val destRegister: Int,
    val constantType: ConstantType,
    val literal: Any,
    val classLoader: ClassLoader,
) : Op(location) {

    override val registersReadCount = 0
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        val constant = buildConstant()
        if (constant is Throwable) {
            return throwException(constant)
        }
        node.state.assignRegister(destRegister, constant, constantTypeString)
        return finishOp()
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" r").append(destRegister).append(", ")
        var value: String
        when (constantType) {
            ConstantType.CLASS -> sb.append(literal)
            ConstantType.NARROW -> {
                value = (literal as Int).toString(16)
                if (value.startsWith("-")) {
                    sb.append('-')
                    value = value.substring(1)
                }
                sb.append("0x").append(value)
            }
            ConstantType.STRING -> sb.append('"').append(literal as String).append('"')
            ConstantType.WIDE -> {
                value = java.lang.Long.toString(literal as Long, 16)
                if (value.startsWith("-")) {
                    sb.append('-')
                    value = value.substring(1)
                }
                sb.append("0x").append(value)
            }
        }
        return sb.toString()
    }

    private fun buildConstant(): Any {
        return if (ConstantType.CLASS == constantType) {
            val className = literal as String
            try {
                val binaryClassName = ClassNameUtils.internalToBinary(className)
                // TODO: does const-class really not cause class init?
                ClassUtils.getClass(classLoader, binaryClassName)
            } catch (e: ClassNotFoundException) {
                return e
            }
        } else {
            literal
        }
    }

    // Type string is ambiguous because Dalvik treats a multiple types as int (char, byte, etc.) and multiple
    // types as "wide" (long, float). Logic elsewhere must infer actual types when necessary, such as by opcode.
    private val constantTypeString: String
        get() {
            // Type string is ambiguous because Dalvik treats a multiple types as int (char, byte, etc.) and multiple
            // types as "wide" (long, float). Logic elsewhere must infer actual types when necessary, such as by opcode.
            return when (constantType) {
                ConstantType.CLASS -> CommonTypes.CLASS
                ConstantType.NARROW -> CommonTypes.INTEGER
                ConstantType.STRING -> CommonTypes.STRING
                ConstantType.WIDE -> {
                    if ("const-wide" == name) {
                        CommonTypes.DOUBLE
                    } else {
                        CommonTypes.LONG
                    }
                }
            }
        }

    enum class ConstantType {
        CLASS, NARROW, STRING, WIDE
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(ConstOp::class.java.simpleName)
        override fun build(
            location: MethodLocation,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instruction = location.instruction as BuilderInstruction
            val destRegister = (instruction as OneRegisterInstruction).registerA
            val constantType: ConstantType
            val literal: Any
            val opName = instruction.getOpcode().name
            when {
                opName.matches("const-string(?:/jumbo)?".toRegex()) -> {
                    val instr = location.instruction as ReferenceInstruction
                    literal = (instr.reference as StringReference).string
                    constantType = ConstantType.STRING
                }
                opName.endsWith("-class") -> {
                    // Don't ensure that the class exists here since we don't know what classes will be available.
                    // Defer to actual execution to handle any possible exceptions.
                    val instr = location.instruction as ReferenceInstruction
                    val classRef = instr.reference
                    literal = DexFormatter.INSTANCE.getReference(classRef)
                    constantType = ConstantType.CLASS
                }
                opName.contains("-wide") -> {
                    val instr = location.instruction as WideLiteralInstruction
                    literal = instr.wideLiteral
                    constantType = ConstantType.WIDE
                }
                else -> {
                    val instr = location.instruction as NarrowLiteralInstruction
                    literal = instr.narrowLiteral
                    constantType = ConstantType.NARROW
                }
            }
            return ConstOp(location, destRegister, constantType, literal, classLoader)
        }
    }
}
