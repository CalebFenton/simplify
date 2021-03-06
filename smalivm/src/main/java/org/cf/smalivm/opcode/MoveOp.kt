package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.jf.dexlib2.builder.MethodLocation

class MoveOp internal constructor(location: MethodLocation, child: MethodLocation?, val toRegister: Int, val moveType: MoveType) :
    MethodStateOp(location, child) {
    var targetRegister = 0
        private set

    internal constructor(location: MethodLocation, child: MethodLocation?, toRegister: Int, targetRegister: Int) : this(
        location,
        child,
        toRegister,
        MoveType.REGISTER
    ) {
        this.targetRegister = targetRegister
    }

    override fun execute(node: ExecutionNode, mState: MethodState) {
        when (moveType) {
            MoveType.EXCEPTION -> moveException(mState, toRegister)
            MoveType.RESULT -> moveResult(mState, toRegister)
            MoveType.REGISTER -> moveRegister(mState, toRegister, targetRegister)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" r").append(toRegister)
        if (MoveType.REGISTER == moveType) {
            sb.append(", r").append(targetRegister)
        }
        return sb.toString()
    }

    enum class MoveType {
        EXCEPTION, REGISTER, RESULT
    }

    companion object {
        private fun moveException(mState: MethodState, toRegister: Int) {
            val exception = mState.peekExceptionRegister()
            mState.assignRegister(toRegister, exception)
        }

        private fun moveRegister(mState: MethodState, toRegister: Int, fromRegister: Int) {
            val item = mState.readRegister(fromRegister)
            mState.assignRegister(toRegister, item)
        }

        private fun moveResult(mState: MethodState, toRegister: Int) {
            val item = mState.readResultRegister()
            mState.assignRegister(toRegister, item)
        }
    }
}
