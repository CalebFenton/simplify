package org.cf.smalivm2

import org.cf.smalivm.type.VirtualMethod
import org.jf.dexlib2.builder.MethodLocation

open class UnresolvedChild {
    companion object {
        fun build(method: VirtualMethod) = MethodInvocationChild(method)
        fun build(location: MethodLocation) = LocationChild(location)
        fun build(exceptionClass: Class<out Throwable>, message: String? = null) = ExceptionChild(exceptionClass, message)
    }
}

class MethodInvocationChild(val method: VirtualMethod) : UnresolvedChild()
class LocationChild(val location: MethodLocation) : UnresolvedChild()
class ExceptionChild(val exceptionClass: Class<out Throwable>, val message: String? = null) : UnresolvedChild()
