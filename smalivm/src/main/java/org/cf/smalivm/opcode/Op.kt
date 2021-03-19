package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import java.util.*

abstract class Op internal constructor(val location: MethodLocation, val children: Array<MethodLocation>) {
    internal constructor(location: MethodLocation) : this(location, arrayOf<MethodLocation>())
    internal constructor(location: MethodLocation, child: MethodLocation) : this(location, arrayOf<MethodLocation>(child))

    val exceptions: MutableSet<Throwable> = HashSet(0)
    val address = location.codeAddress
    val index = location.index
    val instruction = location.instruction as BuilderInstruction?
    val name: String = if (instruction != null) {
        instruction.opcode.name
    } else {
        // Happens during instruction removal
        "*null instr*"
    }
    open val sideEffectLevel = SideEffect.Level.NONE

    abstract fun execute(node: ExecutionNode) : Array<OpChild>
    abstract fun getRegistersReadCount(): Int
    abstract fun getRegistersAssignedCount(): Int
    abstract override fun toString(): String

    fun asdf() {

    }
}
