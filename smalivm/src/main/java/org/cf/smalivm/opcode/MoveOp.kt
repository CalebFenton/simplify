package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction

class MoveOp internal constructor(location: MethodLocation, child: MethodLocation, val toRegister: Int, val moveType: MoveType) :
    Op(location, child) {
    var targetRegister = 0
        private set

    internal constructor(location: MethodLocation, child: MethodLocation, toRegister: Int, targetRegister: Int) : this(
        location,
        child,
        toRegister,
        MoveType.REGISTER
    ) {
        this.targetRegister = targetRegister
    }

    override val registersReadCount = 1
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        when (moveType) {
            MoveType.EXCEPTION -> moveException(node.state, toRegister)
            MoveType.RESULT -> moveResult(node.state, toRegister)
            MoveType.REGISTER -> moveRegister(node.state, toRegister, targetRegister)
        }
        return finishOp()
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

    companion object : OpFactory {
        private fun getMoveType(opName: String): MoveType {
            return when {
                opName.contains("-result") -> MoveType.RESULT
                opName.contains("-exception") -> MoveType.EXCEPTION
                else -> MoveType.REGISTER
            }
        }

        private fun moveException(state: ExecutionState, toRegister: Int) {
            val exception = state.peekExceptionRegister()!!
            state.assignRegister(toRegister, exception)
        }

        private fun moveRegister(state: ExecutionState, toRegister: Int, fromRegister: Int) {
            val value = state.readRegister(fromRegister)
            state.assignRegister(toRegister, value)
        }

        private fun moveResult(state: ExecutionState, toRegister: Int) {
            val value = state.readResultRegister()
            state.assignRegister(toRegister, value)
        }

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instruction = location.instruction as BuilderInstruction
            val opName = instruction.opcode.name
            val toRegister = (instruction as OneRegisterInstruction).registerA
            return when (val moveType = getMoveType(opName)) {
                MoveType.RESULT, MoveType.EXCEPTION -> MoveOp(location, child, toRegister, moveType)
                MoveType.REGISTER -> {
                    val targetRegister = (instruction as TwoRegisterInstruction).registerB
                    MoveOp(location, child, toRegister, targetRegister)
                }
            }
        }
    }
}
