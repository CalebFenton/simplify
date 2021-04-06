package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import java.util.*

abstract class Op internal constructor(
    val location: MethodLocation,
    exceptions: Array<Pair<Class<out Throwable>, String?>> = arrayOf(),
) {
    constructor(
        location: MethodLocation,
        vararg defaultExceptions: Class<out Throwable> = arrayOf()
    ) : this(location, defaultExceptions.map { Pair(it, null) }.toTypedArray())

    abstract val registersReadCount: Int
    abstract val registersAssignedCount: Int

    val exceptions = exceptions.map { UnresolvedChild.build(it.first, it.second) }
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
        var s: Stack<ExecutionNode>
        return finish()
    }

    abstract override fun toString(): String

    fun throwException(t: Throwable): Array<out UnresolvedChild> {
        return throwException(t.javaClass, t.message)
    }

    fun throwException(klazz: Class<out Throwable>, message: String? = null): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(klazz, message))
    }

    fun callMethod(methodCall: VirtualMethod, state: ExecutionState? = null, analyzedParameterTypes: Array<String>? = null): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(methodCall, state, analyzedParameterTypes))
    }

    fun finish(mayThrow: Boolean = false, sideEffectLevel: SideEffect.Level = SideEffect.Level.NONE): Array<out UnresolvedChild> {
        val children: MutableList<UnresolvedChild> = LinkedList()
        val opcode = location.instruction!!.opcode
        if (opcode.canContinue() && this !is SwitchOp) {
            val child = UnresolvedChild.build(nextAddress, sideEffectLevel = sideEffectLevel)
            children.add(child)
        }
        if (mayThrow) {
            children.addAll(exceptions)
        }
        return children.toTypedArray()
    }

    fun finish(addresses: Array<Int>): Array<out UnresolvedChild> {
        return addresses.map { UnresolvedChild.build(it) }.toTypedArray()
    }

    fun finish(address: Int): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(address))
    }
}
