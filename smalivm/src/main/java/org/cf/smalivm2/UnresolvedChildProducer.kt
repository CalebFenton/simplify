package org.cf.smalivm2

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import java.util.*

open class UnresolvedChildProducer(
    defaultExceptions: Array<Pair<Class<out Throwable>, String?>> = arrayOf()
) {
    val defaultExceptions = defaultExceptions.map { UnresolvedChild.build(it.first, it.second, false) }

    fun throwException(t: Throwable, unhandled: Boolean = false): Array<out UnresolvedChild> {
        return throwException(t.javaClass, t.message, unhandled)
    }

    fun throwException(klazz: Class<out Throwable>, message: String? = null, unhandled: Boolean = false): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(klazz, message, unhandled))
    }

    fun staticInitClass(
        virtualClass: VirtualType,
        classManager: ClassManager,
        classLoader: SmaliClassLoader,
        configuration: Configuration
    ): Array<out UnresolvedChild> {
        // TODO: Don't some classes not have clinit? May need to just set field literals here or allow null methods OR have UnresolvedStaticInit
        val staticInit = virtualClass.getMethod("<clinit>()V")!!
        val state = ExecutionState.build(staticInit, classManager, classLoader, configuration)
        return callMethod(staticInit, state, arrayOf())
    }

    fun callMethod(methodCall: VirtualMethod, state: ExecutionState, analyzedParameterTypes: Array<String>): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(methodCall, state, analyzedParameterTypes))
    }

    fun finishMethod() : Array<out UnresolvedChild> {
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
