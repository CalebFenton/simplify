package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation

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

    val children = childLocations.map { OpChild.build(it) }.toTypedArray()
    val exceptions = defaultExceptions.map { OpChild.build(it.first, it.second) }.toTypedArray()

    val address= location.codeAddress
    val index = location.index

    // May get null instructions when modifying implementations
    val instruction= location.instruction as BuilderInstruction?
    val name: String = instruction?.opcode?.name ?: "*null instr*"

    open val sideEffectLevel = SideEffect.Level.NONE

    abstract fun execute(node: ExecutionNode): Array<out OpChild>

    open fun resume(node: ExecutionNode): Array<out OpChild> {
        return collectChildren()
    }

    abstract override fun toString(): String

    fun throwChild(t: Throwable): Array<out OpChild> {
        return throwChild(t.javaClass, t.message)
    }

    fun throwChild(klazz: Class<out Throwable>, message: String? = null): Array<out OpChild> {
        return arrayOf(OpChild.build(klazz, message))
    }

    fun collectChildren(mayThrow: Boolean = false, includeChildren: Boolean = true): Array<out OpChild> {
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

    fun collectChildren(vararg locations: MethodLocation): Array<out OpChild> {
        return locations.map { OpChild.build(it) }.toTypedArray()
    }

    fun collectChildren(child: OpChild): Array<OpChild> {
        return arrayOf(child)
    }

//    fun collectChildren(child: OpChild, mayThrow: Boolean) : Array<OpChild> {
//
//    }

}
