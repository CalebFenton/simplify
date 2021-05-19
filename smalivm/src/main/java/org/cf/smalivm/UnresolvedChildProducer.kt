package org.cf.smalivm

import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import java.util.*

open class UnresolvedChildProducer(
    defaultExceptions: Array<Pair<Class<out Throwable>, String?>> = arrayOf()
) {
    val defaultExceptions = defaultExceptions.map { UnresolvedChild.build(it.first, it.second, false) }

    fun throwException(t: Throwable, emulatedOrReflected: Boolean = false): Array<out UnresolvedChild> {
        return throwException(t.javaClass, t.message, emulatedOrReflected)
    }

    fun throwException(klazz: Class<out Throwable>, message: String? = null, emulatedOrReflected: Boolean = false): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(klazz, message, emulatedOrReflected))
    }

    fun staticInitClass(virtualClass: VirtualType): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(virtualClass))
    }

    fun callMethod(methodCall: VirtualMethod, state: ExecutionState): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(methodCall, state))
    }

    fun finishMethod(): Array<out UnresolvedChild> {
        return arrayOf()
    }

    fun finishOpWithoutChildren(): Array<out UnresolvedChild> {
        return arrayOf()
    }

    fun finishOp(mayThrow: Boolean = false): Array<out UnresolvedChild> {
        val children: MutableList<UnresolvedChild> = LinkedList()
        children.add(UnresolvedChild.build())
        if (mayThrow) {
            children.addAll(defaultExceptions)
        }
        return children.toTypedArray()
    }

    fun finishOp(addresses: Array<Int>): Array<out UnresolvedChild> {
        return addresses.map { UnresolvedChild.build(it) }.toTypedArray()
    }

    fun finishOp(address: Int): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(address))
    }
}
