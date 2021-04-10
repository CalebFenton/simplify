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
    val defaultExceptions = defaultExceptions.map { UnresolvedChild.build(it.first, it.second) }

    fun throwException(t: Throwable): Array<out UnresolvedChild> {
        return throwException(t.javaClass, t.message)
    }

    fun throwException(klazz: Class<out Throwable>, message: String? = null): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(klazz, message))
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

    fun finish(mayThrow: Boolean = false): Array<out UnresolvedChild> {
        val children: MutableList<UnresolvedChild> = LinkedList()
        children.add(UnresolvedChild.build())
        if (mayThrow) {
            children.addAll(defaultExceptions)
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
