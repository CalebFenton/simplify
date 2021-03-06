package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.opcode.FilledNewArrayOp
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class FilledNewArrayOp internal constructor(
    location: MethodLocation,
    child: MethodLocation?,
    private val dimensionRegisters: IntArray,
    private val typeReference: String?
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        /*
         * This populates a 1-dimensional integer array with values from the parameters. It does NOT create
         * n-dimensional arrays. It's usually used to create parameter for Arrays.newInstance(). If you use anything but
         * [I as the virtual parse, the code fails verification and a few decompilers (not disassemblers) choke.
         */
        val dimensions = IntArray(dimensionRegisters.size)
        var foundUnknown = false
        for (i in dimensionRegisters.indices) {
            val register = dimensionRegisters[i]
            val item = mState.readRegister(register)
            if (foundUnknown) {
                continue
            }
            val value = item.value
            if (value is Number) {
                dimensions[i] = value.toInt()
            } else {
                if (!item.isUnknown) {
                    if (log.isWarnEnabled) {
                        log.warn("Unexpected value virtual for {}: {}", toString(), item)
                    }
                }

                // At least one value is unknown. Give up.
                foundUnknown = true
            }
        }
        if (foundUnknown) {
            mState.assignResultRegister(HeapItem.newUnknown("[I"))
        } else {
            mState.assignResultRegister(dimensions, "[I")
        }
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

    companion object {
        private val log = LoggerFactory.getLogger(FilledNewArrayOp::class.java.simpleName)
    }
}
