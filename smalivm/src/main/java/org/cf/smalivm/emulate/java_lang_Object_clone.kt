package org.cf.smalivm.emulate

import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.VirtualMachine2
import org.slf4j.LoggerFactory

internal class java_lang_Object_clone : EmulatedMethodCall(true) {
    // This method is used when cloning arrays
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine2): Array<out UnresolvedChild> {
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
