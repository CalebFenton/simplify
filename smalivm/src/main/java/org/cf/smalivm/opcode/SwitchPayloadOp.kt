package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.opcode.SwitchPayloadOp
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory
import java.util.*

class SwitchPayloadOp  // Don't know children until we know the pseudo return instruction, only branch offsets
internal constructor(
    location: MethodLocation, private val addressToLocation: Map<Int, MethodLocation>,
    private val targetKeyToOffset: Map<Int, Int>
) : MethodStateOp(location) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        // Pseudo points to instruction *after* switch op.
        val returnLocation = mState.pseudoInstructionReturnInstruction
        val branchFromAddress = returnLocation.codeAddress - SWITCH_OP_CODE_UNITS
        val targetItem = mState.readResultRegister()
        if (targetItem.isUnknown) {
            val childList = getTargets(branchFromAddress, targetKeyToOffset)
            childList.add(returnLocation)
            val children = childList.toTypedArray()
            node.setChildLocations(*children)
            return
        }
        val targetKey = Utils.getIntegerValue(targetItem.value)
        if (targetKeyToOffset.containsKey(targetKey)) {
            val targetOffset = branchFromAddress + targetKeyToOffset[targetKey]!!
            val child = addressToLocation[targetOffset]
            node.setChildLocations(child)
            return
        }

        // Branch target is unspecified. Continue to next op.
        node.setChildLocations(returnLocation)
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

    companion object {
        private val log = LoggerFactory
            .getLogger(SwitchPayloadOp::class.java.simpleName)
        private const val SWITCH_OP_CODE_UNITS = 3
    }
}
