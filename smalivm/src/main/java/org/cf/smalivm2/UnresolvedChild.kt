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

        fun build(exceptionClass: Class<out Throwable>, message: String? = null) =
            UnresolvedExceptionChild(exceptionClass, message)

        fun build(exception: Throwable) =
            UnresolvedExceptionChild(exception.javaClass, exception.message)

        fun build() = UnresolvedContinueChild()
    }
}

class UnresolvedMethodInvocationChild(
    val method: VirtualMethod,
    val state: ExecutionState,
    val analyzedParameterTypes: Array<String>
) : UnresolvedChild()

class UnresolvedExceptionChild(
    val exceptionClass: Class<out Throwable>,
    val message: String?
) : UnresolvedChild()

class UnresolvedAddressChild(
    val address: Int
) : UnresolvedChild()

class UnresolvedContinueChild : UnresolvedChild()
