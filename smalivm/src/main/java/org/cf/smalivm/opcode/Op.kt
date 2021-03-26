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
//    childAddresses: Array<Int> = arrayOf(),
    exceptions: Array<Pair<Class<out Throwable>, String?>> = arrayOf()
) {
    constructor(
        location: MethodLocation,
        vararg defaultExceptions: Class<out Throwable> = arrayOf()
    ) : this(location, defaultExceptions.map { Pair(it, null) }.toTypedArray())

//    constructor(
//        location: MethodLocation,
//        vararg defaultExceptions: Pair<Class<out Throwable>, String?>
//    ) : this(location, arrayOf(*defaultExceptions))

    abstract val registersReadCount: Int
    abstract val registersAssignedCount: Int

    //    val children = childAddresses.map { UnresolvedChild.build(it) }.toTypedArray()
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

    fun callMethod(methodCall: VirtualMethod): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(methodCall))
    }

//    fun finishOp(mayThrow: Boolean = false, includeChildren: Boolean = true): Array<out UnresolvedChild> {
//        return if (mayThrow) {
//            if (includeChildren) {
//                arrayOf(*children, *exceptions)
//            } else {
//                exceptions
//            }
//        } else {
//            if (includeChildren) {
//                arrayOf(*children)
//            } else {
//                arrayOf()
//            }
//        }
//    }

    fun finishOp(mayThrow: Boolean = false): Array<out UnresolvedChild> {
        val children: MutableList<UnresolvedChild> = LinkedList()
        val opcode = location.instruction!!.opcode
        if (opcode.canContinue() && this !is SwitchOp) {
            val child = UnresolvedChild.build(nextAddress)
            children.add(child)
        }
        if (mayThrow) {
            children.addAll(exceptions)
        }
        return children.toTypedArray()
    }

    fun finishOp(addresses: Array<Int>): Array<out UnresolvedChild> {
        return addresses.map { UnresolvedChild.build(it) }.toTypedArray()
    }

    fun finishOp(address: Int): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(address))
    }

//    fun finishOp(child: UnresolvedChild): Array<UnresolvedChild> {
//        return arrayOf(child)
//    }
}
