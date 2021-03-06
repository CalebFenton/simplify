package org.cf.smalivm.opcode

import org.cf.smalivm2.ExecutionState
import org.cf.smalivm.SideEffect
import org.cf.smalivm.context.ExecutionNode
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import java.util.*

abstract class Op internal constructor(val location: MethodLocation, val children: Array<MethodLocation>) {
    internal constructor(location: MethodLocation) : this(location, arrayOf<MethodLocation>())
    internal constructor(location: MethodLocation, child: MethodLocation) : this(location, arrayOf<MethodLocation>(child))

    val exceptions: MutableSet<Throwable> = HashSet()
    val address: Int
        get() = location.codeAddress
    val index: Int
        get() = location.index
    val instruction: BuilderInstruction?
        get() = location.instruction as BuilderInstruction?
    val name: String
        get() = if (instruction != null) {
            instruction!!.opcode.name
        } else {
            // Happens during instruction removal
            "*null instr*"
        }
    open val sideEffectLevel: SideEffect.Level
        get() = SideEffect.Level.NONE

    abstract fun execute(node: ExecutionNode, state: ExecutionState)

    abstract override fun toString(): String
}
