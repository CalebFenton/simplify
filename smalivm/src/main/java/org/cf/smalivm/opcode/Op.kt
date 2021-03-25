package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import java.util.*

abstract class Op internal constructor(
    val location: MethodLocation,
    childLocations: Array<MethodLocation> = arrayOf(),
    defaultExceptions: Array<Pair<Class<out Throwable>, String?>> = arrayOf()
) {
    constructor(
        location: MethodLocation,
        childLocation: MethodLocation,
        vararg defaultExceptions: Class<out Throwable> = arrayOf()
    ) : this(location, arrayOf(childLocation), defaultExceptions.map { Pair(it, null) }.toTypedArray())

    constructor(
        location: MethodLocation,
        childLocation: MethodLocation,
        vararg defaultExceptions: Pair<Class<out Throwable>, String?>
    ) : this(location, arrayOf(childLocation), arrayOf(*defaultExceptions))

    abstract val registersReadCount: Int
    abstract val registersAssignedCount: Int

    val children = childLocations.map { UnresolvedChild.build(it) }.toTypedArray()
    val exceptions = defaultExceptions.map { UnresolvedChild.build(it.first, it.second) }.toTypedArray()

    val address = location.codeAddress
    val index = location.index

    // May get null instructions when modifying implementations
    val instruction = location.instruction as BuilderInstruction?
    val name: String = instruction?.opcode?.name ?: "*null instr*"

    open val sideEffectLevel = SideEffect.Level.NONE

    abstract fun execute(node: ExecutionNode): Array<out UnresolvedChild>

    open fun resume(node: ExecutionNode): Array<out UnresolvedChild> {
        var s: Stack<ExecutionNode>
        return finishOp()
    }

    abstract override fun toString(): String

    fun throwException(t: Throwable): Array<out UnresolvedChild> {
        return throwException(t.javaClass, t.message)
    }

    fun throwException(klazz: Class<out Throwable>, message: String? = null): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(klazz, message))
    }

    fun finishOp(mayThrow: Boolean = false, includeChildren: Boolean = true): Array<out UnresolvedChild> {
        return if (mayThrow) {
            if (includeChildren) {
                arrayOf(*children, *exceptions)
            } else {
                exceptions
            }
        } else {
            if (includeChildren) {
                arrayOf(*children)
            } else {
                arrayOf()
            }
        }
    }

    fun finishOp(vararg locations: MethodLocation): Array<out UnresolvedChild> {
        return locations.map { UnresolvedChild.build(it) }.toTypedArray()
    }

    fun finishOp(methodCall: VirtualMethod): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(methodCall))
    }

    fun finishOp(child: UnresolvedChild): Array<UnresolvedChild> {
        return arrayOf(child)
    }

//    fun collectChildren(child: OpChild, mayThrow: Boolean) : Array<OpChild> {
//
//    }

}
