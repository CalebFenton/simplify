package org.cf.smalivm.opcode

import org.cf.smalivm.ExecutionGraph2
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.UnresolvedChild
import org.cf.smalivm.UnresolvedChildProducer
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation

abstract class Op internal constructor(
    val location: MethodLocation,
    defaultExceptions: Array<Pair<Class<out Throwable>, String?>> = arrayOf(),
) : UnresolvedChildProducer(defaultExceptions) {
    constructor(
        location: MethodLocation,
        vararg defaultExceptions: Class<out Throwable> = arrayOf()
    ) : this(location, defaultExceptions.map { Pair(it, null) }.toTypedArray())

    abstract val registersReadCount: Int
    abstract val registersAssignedCount: Int

    val address: Int
        get() = location.codeAddress
    val index: Int
        get() = location.index
    val nextAddress: Int
        get() = location.codeAddress + location.instruction!!.codeUnits

    // Allow nulls here because may get null instructions when modifying implementations
    val instruction: BuilderInstruction?
        get() = location.instruction as BuilderInstruction?
    val name: String
        get() = instruction?.opcode?.name ?: "*null instr*"

    abstract fun execute(node: ExecutionNode): Array<out UnresolvedChild>

    open fun resume(node: ExecutionNode): Array<out UnresolvedChild> {
        return finishOp()
    }

    open fun resume(node: ExecutionNode, calleeGraph: ExecutionGraph2): Array<out UnresolvedChild> {
        return finishOp()
    }

    abstract override fun toString(): String
}
