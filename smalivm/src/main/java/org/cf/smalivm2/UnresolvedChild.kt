package org.cf.smalivm2

import org.cf.smalivm.SideEffect
import org.cf.smalivm.type.VirtualMethod

abstract class UnresolvedChild(val sideEffectLevel: SideEffect.Level) {
    companion object {
        fun build(
            method: VirtualMethod,
            state: ExecutionState?,
            analyzedParameterTypes: Array<String>?,
            sideEffectLevel: SideEffect.Level = SideEffect.Level.NONE
        ) = UnresolvedMethodInvocationChild(method, state, analyzedParameterTypes, sideEffectLevel)

        fun build(address: Int, sideEffectLevel: SideEffect.Level = SideEffect.Level.NONE) =
            UnresolvedAddressChild(address, sideEffectLevel)

        fun build(exceptionClass: Class<out Throwable>, message: String? = null, sideEffectLevel: SideEffect.Level = SideEffect.Level.NONE) =
            UnresolvedExceptionChild(exceptionClass, message, sideEffectLevel)

        fun build(exception: Throwable, sideEffectLevel: SideEffect.Level = SideEffect.Level.NONE) =
            UnresolvedExceptionChild(exception.javaClass, exception.message, sideEffectLevel)

        fun build(sideEffectLevel: SideEffect.Level = SideEffect.Level.NONE) = UnresolvedNopChild(sideEffectLevel)
    }
}

class UnresolvedMethodInvocationChild(
    val method: VirtualMethod,
    val state: ExecutionState?,
    val analyzedParameterTypes: Array<String>?,
    sideEffectLevel: SideEffect.Level
) : UnresolvedChild(sideEffectLevel)

class UnresolvedExceptionChild(
    val exceptionClass: Class<out Throwable>,
    val message: String?,
    sideEffectLevel: SideEffect.Level
) : UnresolvedChild(sideEffectLevel)

class UnresolvedAddressChild(
    val address: Int,
    sideEffectLevel: SideEffect.Level
) : UnresolvedChild(sideEffectLevel)

class UnresolvedNopChild(
    sideEffectLevel: SideEffect.Level
) : UnresolvedChild(sideEffectLevel)
