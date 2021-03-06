package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.opcode.CmpOp
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class CmpOp internal constructor(
    location: MethodLocation, child: MethodLocation?, val destRegister: Int, val lhsRegister: Int,
    val rhsRegister: Int
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val lhsItem = mState.readRegister(lhsRegister)
        val rhsItem = mState.readRegister(rhsRegister)
        val item: HeapItem
        item = if (lhsItem.isUnknown || rhsItem.isUnknown) {
            HeapItem.newUnknown("I")
        } else {
            val lhs = lhsItem.value as Number?
            val rhs = rhsItem.value as Number?
            assert(lhs!!.javaClass == rhs!!.javaClass)
            assert(lhsItem.type == rhsItem.type)
            val cmp = cmp(lhs, rhs)
            HeapItem(cmp, "I")
        }
        mState.assignRegister(destRegister, item)
    }

    override fun toString(): String {
        return "$name r$destRegister, r$lhsRegister, r$rhsRegister"
    }

    private fun cmp(val1: Number?, val2: Number?): Int {
        val arg1IsNan = val1 is Float && val1
            .isNaN() || val1 is Double && val1.isNaN()
        val arg2IsNan = val2 is Float && val2
            .isNaN() || val2 is Double && val2.isNaN()
        var value = 0
        value = if (arg1IsNan || arg2IsNan) {
            if (name.startsWith("cmpg")) {
                1
            } else { // cmpl
                -1
            }
        } else {
            if (name.endsWith("float")) {
                val castVal1 = Utils.getFloatValue(val1)
                val castVal2 = Utils.getFloatValue(val2)
                // The docs say "b == c" but I don't think they mean identity.
                java.lang.Float.compare(castVal1, castVal2)
            } else if (name.endsWith("double")) {
                val castVal1 = Utils.getDoubleValue(val1)
                val castVal2 = Utils.getDoubleValue(val2)
                // The docs say "b == c" but I don't think they mean identity.
                java.lang.Double.compare(castVal1, castVal2)
            } else {
                val castVal1 = Utils.getLongValue(val1)
                val castVal2 = Utils.getLongValue(val2)
                java.lang.Long.compare(castVal1, castVal2)
            }
        }
        return value
    }

    companion object {
        private val log = LoggerFactory.getLogger(CmpOp::class.java.simpleName)
    }
}
