package org.cf.smalivm.emulate

import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.VirtualMachine2
import org.slf4j.LoggerFactory

class MethodEmulator {
    companion object {
        private val log = LoggerFactory.getLogger(MethodEmulator::class.java.simpleName)
        private val EMULATED_METHOD_CALLS: MutableMap<String, Class<out EmulatedMethodCall>> = HashMap()

        fun addMethod(methodDescriptor: String, methodClass: Class<out EmulatedMethodCall>) {
            EMULATED_METHOD_CALLS[methodDescriptor] = methodClass
        }

        fun canEmulate(methodDescriptor: String): Boolean {
            return EMULATED_METHOD_CALLS.containsKey(methodDescriptor)
        }

        fun canHandleUnknownValues(methodDescriptor: String): Boolean {
            val methodClass = EMULATED_METHOD_CALLS[methodDescriptor]
            return methodClass != null && UnknownValuesMethod::class.java.isAssignableFrom(methodClass)
        }

        fun clearMethods() {
            EMULATED_METHOD_CALLS.clear()
        }

        private fun getMethod(methodSignature: String): EmulatedMethodCall? {
            val methodClass = EMULATED_METHOD_CALLS[methodSignature]!!
            var em: EmulatedMethodCall? = null
            try {
                em = methodClass.newInstance()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return em
        }

        init {
            addMethod("Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z", android_text_TextUtils_isEmpty::class.java)
            addMethod("Lorg/cf/simplify/Utils;->breakpoint()V", org_cf_simplify_Utils_breakpoint::class.java)
            addMethod("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;", java_lang_Class_forName::class.java)
            addMethod("Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;", java_lang_reflect_Field_get::class.java)
            addMethod("Ljava/lang/Object;->getClass()Ljava/lang/Class;", java_lang_Object_getClass::class.java)
        }
    }

    fun emulate(methodSignature: String, callerNode: ExecutionNode, vm: VirtualMachine2) {
        try {
            val methodCall = getMethod(methodSignature)
            // TODO: make a child node from callerNode and pass the state
            methodCall.execute(null, callerNode, vm)
        } catch (e: Exception) {
            log.warn("Real exception raised emulating {}: {}", methodSignature, e)
        }
    }
}
