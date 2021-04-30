package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.SwitchPayload
import org.slf4j.LoggerFactory
import java.util.*

class SwitchPayloadOp internal constructor(
    location: MethodLocation,
    private val targetKeyToOffset: Map<Int, Int>
) : Op(location) {

    override val registersReadCount = 2
    override val registersAssignedCount = 0

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        // Pseudo points to instruction *after* switch op.
        // Don't know children until we know the pseudo return instruction, only branch offsets
        val returnAddress = node.state.getPsuedoInstructionReturnAddress()
        val branchFromAddress = returnAddress - SWITCH_OP_CODE_UNITS
        val result = node.state.readResultRegister()
        if (result.isUnknown) {
            val childAddresses = getTargets(branchFromAddress, targetKeyToOffset)
            childAddresses.add(returnAddress)
            return finishOp(childAddresses.toTypedArray())
        }
        val targetKey = Utils.getIntegerValue(result.raw)
        if (targetKeyToOffset.containsKey(targetKey)) {
            val targetAddress = branchFromAddress + targetKeyToOffset[targetKey]!!
            return finishOp(targetAddress)
        }

        // Branch target is unspecified. Continue to next op.
        return finishOp(returnAddress)
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" [")
        val keys = targetKeyToOffset.keys.toIntArray()
        Arrays.sort(keys)
        for (key in keys) {
            val offset = targetKeyToOffset[key]
            sb.append(key).append(" -> :addr_").append(offset).append(", ")
        }
        sb.setLength(sb.length - 2)
        sb.append(']')
        return sb.toString()
    }

    private fun getTargets(branchFromAddress: Int, targetKeyToOffset: Map<Int, Int>): MutableList<Int> {
        val offsets = targetKeyToOffset.values
        val targets: MutableList<Int> = LinkedList()
        for (offset in offsets) {
            val targetAddress = branchFromAddress + offset
            targets.add(targetAddress)
        }
        return targets
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(SwitchPayloadOp::class.java.simpleName)
        private const val SWITCH_OP_CODE_UNITS = 3

        override fun build(
            location: MethodLocation,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): SwitchPayloadOp {
            val instr = location.instruction as SwitchPayload
            val targetKeyToOffset: HashMap<Int, Int> = HashMap()
            for (element in instr.switchElements) {
                targetKeyToOffset[element.key] = element.offset
            }
            return SwitchPayloadOp(location, targetKeyToOffset)
        }
    }
}
