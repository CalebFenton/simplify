package org.cf.smalivm.opcode

import org.cf.smalivm2.ExecutionState
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class IfOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val ifType: IfType?,
    private val target: MethodLocation,
    private val register1: Int
) : Op(location, arrayOf(child, target)) {
    private var compareToZero = true
    private var register2 = 0

    internal constructor(
        location: MethodLocation, child: MethodLocation, ifType: IfType, target: MethodLocation, register1: Int,
        register2: Int
    ) : this(location, child, ifType, target, register1) {
        this.register2 = register2
        compareToZero = false
    }

    override fun execute(node: ExecutionNode, state: ExecutionState) {
        val lhsItem = state.readRegister(register1)
        val rhsItem = if (compareToZero) HeapItem(0, "I") else state.readRegister(register2)

        // Ambiguous predicate. Return to add both possible branches as children.
        if (lhsItem.isUnknown || rhsItem.isUnknown) {
            return
        }
        val lhs = lhsItem.value
        val rhs = rhsItem.value
        val cmp: Int = if (compareToZero) {
            if (lhs == null) {
                // if-*z ops are used to check for null refs
                0
            } else if ((lhs is Number || lhs is Boolean || lhs is Char) &&
                (rhs is Number || rhs is Boolean || rhs is Char)
            ) {
                val aIntValue = Utils.getIntegerValue(lhs)
                aIntValue.compareTo((rhs as Int?)!!)
            } else {
                if (lhs === rhs) 0 else 1
            }
        } else if ((lhs is Number || lhs is Boolean || lhs is Char) &&
            (rhs is Number || rhs is Boolean || rhs is Char)
        ) {
            val aIntValue = Utils.getIntegerValue(lhs)
            val bIntValue = Utils.getIntegerValue(rhs)
            aIntValue.compareTo(bIntValue)
        } else {
            if (lhs === rhs) 0 else 1
        }
        if (log.isTraceEnabled) {
            log.trace("IF compare: {} vs {} = {}", lhs, rhs, cmp)
        }
        val childIndex = if (isTrue(ifType, cmp)) 1 else 0
        node.setChildLocations(children[childIndex])
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" r").append(register1)
        if (!compareToZero) {
            sb.append(", r").append(register2)
        }
        sb.append(", :addr_").append(target.codeAddress)
        return sb.toString()
    }

    internal enum class IfType {
        EQUAL, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL, NOT_EQUAL
    }

    companion object {
        private val log = LoggerFactory.getLogger(IfOp::class.java.simpleName)
        private fun isTrue(ifType: IfType?, cmp: Int): Boolean {
            var isTrue = false
            when (ifType) {
                IfType.EQUAL -> isTrue = cmp == 0
                IfType.GREATER -> isTrue = cmp == 1
                IfType.GREATER_OR_EQUAL -> isTrue = cmp >= 0
                IfType.LESS -> isTrue = cmp == -1
                IfType.LESS_OR_EQUAL -> isTrue = cmp <= 0
                IfType.NOT_EQUAL -> isTrue = cmp != 0
            }
            return isTrue
        }
    }
}
