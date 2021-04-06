package org.cf.smalivm.emulate

import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.VirtualMachine2
import org.slf4j.LoggerFactory

class MethodEmulator {
    companion object {
        private val log = LoggerFactory.getLogger(MethodEmulator::class.java.simpleName)
        private val EMULATED_METHOD_CALLS: MutableMap<String, Class<out EmulatedMethodCall>> = HashMap()
        private val UNKNOWN_HANDLERS: MutableSet<String> = HashSet()

        init {
            addMethod("Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z", android_text_TextUtils_isEmpty::class.java)
            addMethod("Lorg/cf/simplify/Utils;->breakpoint()V", org_cf_simplify_Utils_breakpoint::class.java)
            addMethod("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;", java_lang_Class_forName::class.java)
            addMethod("Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;", java_lang_reflect_Field_get::class.java)
            addMethod("Ljava/lang/Object;->getClass()Ljava/lang/Class;", java_lang_Object_getClass::class.java, true)
        }

        fun addMethod(methodSignature: String, methodClass: Class<out EmulatedMethodCall>, canHandleUnknownValues: Boolean = false) {
            EMULATED_METHOD_CALLS[methodSignature] = methodClass
            if (canHandleUnknownValues) {
                UNKNOWN_HANDLERS.add(methodSignature)
            }
        }

        fun canEmulate(method: VirtualMethod): Boolean {
            return canEmulate(method.signature)
        }

        fun canEmulate(methodSignature: String): Boolean {
            return EMULATED_METHOD_CALLS.containsKey(methodSignature)
        }

        fun canHandleUnknownValues(method: VirtualMethod): Boolean {
            return canHandleUnknownValues(method.signature)
        }

        fun canHandleUnknownValues(methodSignature: String): Boolean {
            return UNKNOWN_HANDLERS.contains(methodSignature)
        }

        fun clearMethods() {
            EMULATED_METHOD_CALLS.clear()
        }

        private fun getMethod(methodSignature: String): EmulatedMethodCall {
            val methodClass = EMULATED_METHOD_CALLS[methodSignature]!!
            return try {
                methodClass.newInstance()
            } catch (e: Exception) {
                throw RuntimeException("Exception instantiating emulated method $methodSignature", e)
            }
        }

        fun emulate(method: VirtualMethod, state: ExecutionState, callerNode: ExecutionNode, vm: VirtualMachine2): UnresolvedChild {
            return emulate(method.signature, state, callerNode, vm)
        }

        fun emulate(methodSignature: String, state: ExecutionState, callerNode: ExecutionNode, vm: VirtualMachine2): UnresolvedChild {
            val emulatedMethodCall = getMethod(methodSignature)
            return try {
                emulatedMethodCall.execute(state, callerNode, vm)
            } catch (e: Exception) {
                throw RuntimeException("Unhandled exception thrown emulating $methodSignature", e)
            }
        }
    }
}
