package org.cf.smalivm2

import org.cf.smalivm.type.VirtualMethod

abstract class UnresolvedChild() {
    companion object {
        fun build(
            method: VirtualMethod,
            state: ExecutionState,
            analyzedParameterTypes: Array<String>,
        ) = UnresolvedMethodInvocationChild(method, state, analyzedParameterTypes)

        fun build(address: Int) =
            UnresolvedAddressChild(address)

        fun build(exceptionClass: Class<out Throwable>, message: String?, unhandled: Boolean) =
            UnresolvedExceptionChild(exceptionClass, message, unhandled)

        fun build(exception: Throwable, unhandled: Boolean) =
            UnresolvedExceptionChild(exception.javaClass, exception.message, unhandled)

        fun build() = UnresolvedContinueChild()
    }
}

class UnresolvedMethodInvocationChild(
    val method: VirtualMethod,
    val state: ExecutionState,
    // TODO: remove this? are they not needed anymore?
    val analyzedParameterTypes: Array<String>
) : UnresolvedChild()

class UnresolvedExceptionChild(
    val exceptionClass: Class<out Throwable>,
    val message: String?,
    val unhandled: Boolean,
) : UnresolvedChild()

class UnresolvedAddressChild(
    val address: Int
) : UnresolvedChild()

class UnresolvedContinueChild : UnresolvedChild()
