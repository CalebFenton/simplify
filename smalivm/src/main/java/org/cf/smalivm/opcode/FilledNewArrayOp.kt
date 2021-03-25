package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.Value
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.VariableRegisterInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory

class FilledNewArrayOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val dimensionRegisters: IntArray,
    private val typeReference: String
) : Op(location, child) {

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
            val item = node.state.readRegister(register)
            if (item.value is Number) {
                dimensions[i] = item.value.toInt()
            } else {
                if (item.isKnown) {
                    log.warn("Unexpected dimension argument type for {}: {}", toString(), item)
                }
                foundUnknown = true
                break
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
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instruction = location.instruction as BuilderInstruction
            val reference = (instruction as ReferenceInstruction).reference
            val typeReference = ReferenceUtil.getReferenceString(reference)!!
            val registerCount = (instruction as VariableRegisterInstruction).registerCount
            val opName = instruction.getOpcode().name
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
            return FilledNewArrayOp(location, child, dimensionRegisters, typeReference)
        }
    }
}
