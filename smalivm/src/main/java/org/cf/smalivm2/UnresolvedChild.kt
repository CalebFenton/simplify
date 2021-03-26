package org.cf.smalivm2

import org.cf.smalivm.type.VirtualMethod

open class UnresolvedChild {
    companion object {
        fun build(method: VirtualMethod) = UnresolvedMethodInvocationChild(method)
        fun build(address: Int) = UnresolvedAddressChild(address)
        fun build(exceptionClass: Class<out Throwable>, message: String? = null) = UnresolvedExceptionChild(exceptionClass, message)
    }
}

class UnresolvedMethodInvocationChild(val method: VirtualMethod) : UnresolvedChild()
class UnresolvedAddressChild(val address: Int) : UnresolvedChild()
class UnresolvedExceptionChild(val exceptionClass: Class<out Throwable>, val message: String? = null) : UnresolvedChild()
