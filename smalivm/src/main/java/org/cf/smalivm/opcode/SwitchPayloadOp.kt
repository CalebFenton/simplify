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
    private val addressToLocation: Map<Int, MethodLocation>,
    private val targetKeyToOffset: Map<Int, Int>
) : Op(location) {

    override val registersReadCount = 2
    override val registersAssignedCount = 0

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        // Pseudo points to instruction *after* switch op.
        // Don't know children until we know the pseudo return instruction, only branch offsets
        val returnLocation = node.state.getPsuedoInstructionReturnLocation()
        val branchFromAddress = returnLocation.codeAddress - SWITCH_OP_CODE_UNITS
        val targetItem = node.state.readResultRegister()
        if (targetItem.isUnknown) {
            val childList = getTargets(branchFromAddress, targetKeyToOffset)
            childList.add(returnLocation)
            val children = childList.toTypedArray()
            return finishOp(*children)
        }
        val targetKey = Utils.getIntegerValue(targetItem.value)
        if (targetKeyToOffset.containsKey(targetKey)) {
            val targetOffset = branchFromAddress + targetKeyToOffset[targetKey]!!
            val child = addressToLocation[targetOffset]!!
            return finishOp(child)
        }

        // Branch target is unspecified. Continue to next op.
        return finishOp(returnLocation)
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

    private fun getTargets(branchFromAddress: Int, targetKeyToOffset: Map<Int, Int>): MutableList<MethodLocation> {
        val offsets = targetKeyToOffset.values
        val targets: MutableList<MethodLocation> = LinkedList()
        for (offset in offsets) {
            val targetOffset = branchFromAddress + offset
            targets.add(addressToLocation[targetOffset]!!)
        }
        return targets
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(SwitchPayloadOp::class.java.simpleName)
        private const val SWITCH_OP_CODE_UNITS = 3

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instr = location.instruction as SwitchPayload
            val targetKeyToOffset: HashMap<Int, Int> = HashMap()
            for (element in instr.switchElements) {
                targetKeyToOffset[element.key] = element.offset
            }
            return SwitchPayloadOp(location, addressToLocation, targetKeyToOffset)
        }
    }
}
