package org.cf.smalivm.emulate

import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.ExecutionState
import org.cf.smalivm.UnresolvedChild
import org.cf.smalivm.VirtualMachine
import org.slf4j.LoggerFactory

internal class java_lang_Object_clone : EmulatedMethodCall(true) {
    // This method is used when cloning arrays
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine): Array<out UnresolvedChild> {
        val obj = state.peekParameterOffset(0)!!
        if (obj.isNull) {
            // This operation would have thrown a null pointer exception and nothing else.
            return throwException(NullPointerException::class.java, null, true)
        }
        val clone = when {
            obj.isUnknown -> UnknownValue()
            else -> {
                try {
                    val m = Any::class.java.getDeclaredMethod("clone")
                    m.isAccessible = true
                    m.invoke(obj.raw)
                } catch (e: Exception) {
                    log.error("Real exception initializing Object; returning unknown", e)
                    UnknownValue()
                }
            }
        }
        state.assignReturnRegister(clone, obj.type)

        return finishMethod()
    }

    companion object {
        private val log = LoggerFactory.getLogger(java_lang_Object_clone::class.java.simpleName)
    }
}
