package org.cf.smalivm2

import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType

abstract class UnresolvedChild() {
    companion object {
        fun build(method: VirtualMethod,state: ExecutionState) =
            UnresolvedMethodInvocationChild(method, state)

        fun build(virtualClass: VirtualType) =
            UnresolvedStaticClassInit(virtualClass)

        fun build(address: Int) =
            UnresolvedAddressChild(address)

        fun build(exceptionClass: Class<out Throwable>, message: String?, unhandled: Boolean) =
            UnresolvedExceptionChild(exceptionClass, message, unhandled)

        fun build(exception: Throwable, unhandled: Boolean) =
            UnresolvedExceptionChild(exception.javaClass, exception.message, unhandled)

        fun build() = UnresolvedContinueChild()
    }
}

data class UnresolvedMethodInvocationChild(
    val method: VirtualMethod,
    val state: ExecutionState,
) : UnresolvedChild()

data class UnresolvedStaticClassInit(
    val virtualClass: VirtualType
) : UnresolvedChild()

data class UnresolvedExceptionChild(
    val exceptionClass: Class<out Throwable>,
    val message: String?,
    val unhandled: Boolean,
) : UnresolvedChild()

data class UnresolvedAddressChild(
    val address: Int
) : UnresolvedChild()

class UnresolvedContinueChild : UnresolvedChild()
