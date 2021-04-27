package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.Value
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.jf.dexlib2.formatter.DexFormatter
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc
import org.slf4j.LoggerFactory

class FilledNewArrayOp internal constructor(
    location: MethodLocation,
    private val dimensionRegisters: IntArray,
    private val typeReference: String
) : Op(location) {

    override val registersReadCount = dimensionRegisters.size
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        /*
         * This populates a 1-dimensional integer array with values from the parameters. It does NOT create
         * n-dimensional arrays. It's usually used to create parameter for Arrays.newInstance(). If you use anything but
         * [I as the type, the code fails verification and a few decompilers choke (disassemblers are usually fine).
         */
        val dimensions = IntArray(dimensionRegisters.size)
        var foundUnknown = false
        for (i in dimensionRegisters.indices) {
            val register = dimensionRegisters[i]
            val value = node.state.readRegister(register)
            if (value.raw is Number) {
                dimensions[i] = value.raw.toInt()
            } else {
                if (value.isKnown) {
                    log.warn("Unexpected dimension argument type for {}: {}", toString(), value)
                }
                foundUnknown = true
                // Not going to break here since we want to signify that all registers are read here!
            }
        }
        if (foundUnknown) {
            node.state.assignResultRegister(Value.unknown("[I"))
        } else {
            node.state.assignResultRegister(dimensions, "[I")
        }

        return finishOp()
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" {")
        if (dimensionRegisters.size > 5) {
            sb.append('r').append(dimensionRegisters[0]).append(" .. r")
                .append(dimensionRegisters[dimensionRegisters.size - 1])
        } else {
            for (dimensionRegister in dimensionRegisters) {
                sb.append('r').append(dimensionRegister).append(", ")
            }
            sb.setLength(sb.length - 2)
        }
        sb.append("}, ").append(typeReference)
        return sb.toString()
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(FilledNewArrayOp::class.java.simpleName)
        override fun build(
            location: MethodLocation,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): FilledNewArrayOp {
            val opName = location.instruction!!.opcode.name
            val instruction = if (opName.endsWith("/range")) {
                location.instruction as Instruction3rc
            } else {
                location.instruction as BuilderInstruction35c
            }
            val reference = instruction.reference
            val typeReference = DexFormatter.INSTANCE.getReference(reference)
            val registerCount = instruction.registerCount
            val dimensionRegisters = IntArray(registerCount)
            if (opName.endsWith("/range")) {
                val instr = location.instruction as Instruction3rc
                val startRegister = instr.startRegister
                for (i in dimensionRegisters.indices) {
                    dimensionRegisters[i] = startRegister + i
                }
            } else {
                val instr = location.instruction as Instruction35c
                when (dimensionRegisters.size) {
                    5 -> {
                        dimensionRegisters[4] = instr.registerG
                        dimensionRegisters[3] = instr.registerF
                        dimensionRegisters[2] = instr.registerE
                        dimensionRegisters[1] = instr.registerD
                        dimensionRegisters[0] = instr.registerC
                    }
                    4 -> {
                        dimensionRegisters[3] = instr.registerF
                        dimensionRegisters[2] = instr.registerE
                        dimensionRegisters[1] = instr.registerD
                        dimensionRegisters[0] = instr.registerC
                    }
                    3 -> {
                        dimensionRegisters[2] = instr.registerE
                        dimensionRegisters[1] = instr.registerD
                        dimensionRegisters[0] = instr.registerC
                    }
                    2 -> {
                        dimensionRegisters[1] = instr.registerD
                        dimensionRegisters[0] = instr.registerC
                    }
                    1 -> dimensionRegisters[0] = instr.registerC
                    else -> throw IllegalArgumentException("Unexpected filled new array dimension register size: ${dimensionRegisters.size}")
                }
            }
            return FilledNewArrayOp(location, dimensionRegisters, typeReference)
        }
    }
}
