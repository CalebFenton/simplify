package org.cf.smalivm.emulate

import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.ExecutionState
import org.cf.smalivm.UnresolvedChild
import org.cf.smalivm.VirtualMachine
import org.slf4j.LoggerFactory


class MethodEmulator {
    companion object {
        private val log = LoggerFactory.getLogger(MethodEmulator::class.java.simpleName)
        private val EMULATED_METHOD_SIGNATURES: MutableMap<String, EmulatedMethodCall> = HashMap()
        private val EMULATED_METHOD_MATCHES: MutableMap<(methodSignature: String) -> Boolean, EmulatedMethodCall> = HashMap()
        private val UNKNOWN_HANDLER_SIGNATURES: MutableSet<String> = HashSet()
        private val UNKNOWN_HANDLER_MATCHES: MutableSet<(methodSignature: String) -> Boolean> = HashSet()

        init {
            addMethod("Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z", android_text_TextUtils_isEmpty())
            addMethod("Lorg/cf/simplify/Utils;->breakpoint()V", org_cf_simplify_Utils_breakpoint())
            addMethod("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;", java_lang_Class_forName())
            addMethod("Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;", java_lang_reflect_Field_get())
            addMethod("Ljava/lang/Object;->getClass()Ljava/lang/Class;", java_lang_Object_getClass())
            addMethod("Ljava/lang/Enum;-><init>(Ljava/lang/String;I)V", java_lang_Enum_init())
            addMethod("Ljava/lang/Object;-><init>()V", java_lang_Object_init())
            addMethod({ signature: String -> signature.endsWith("->clone()Ljava/lang/Object;") }, java_lang_Object_clone())
        }

        private fun addMethod(match: (methodSignature: String) -> Boolean, method: EmulatedMethodCall) {
            EMULATED_METHOD_MATCHES[match] = method
            if (method.canHandleUnknownValues) {
                UNKNOWN_HANDLER_MATCHES.add(match)
            }
        }

        fun addMethod(methodSignature: String, method: EmulatedMethodCall) {
            EMULATED_METHOD_SIGNATURES[methodSignature] = method
            if (method.canHandleUnknownValues) {
                UNKNOWN_HANDLER_SIGNATURES.add(methodSignature)
            }
        }

        fun canEmulate(method: VirtualMethod): Boolean {
            return canEmulate(method.signature)
        }

        fun canEmulate(methodSignature: String): Boolean {
            if (EMULATED_METHOD_SIGNATURES.containsKey(methodSignature)) {
                return true
            }
            for ((match, method) in EMULATED_METHOD_MATCHES) {
                if (match.invoke(methodSignature)) {
                    // Cache this signature for faster lookups later
                    addMethod(methodSignature, method)
                    return true
                }
            }
            return false
        }

        fun canHandleUnknownValues(method: VirtualMethod): Boolean {
            return canHandleUnknownValues(method.signature)
        }

        fun canHandleUnknownValues(methodSignature: String): Boolean {
            return UNKNOWN_HANDLER_SIGNATURES.contains(methodSignature)
        }

        fun clearMethods() {
            EMULATED_METHOD_SIGNATURES.clear()
        }

//        private fun getMethod(methodSignature: String): EmulatedMethodCall {
//            val methodClass = EMULATED_METHOD_SIGNATURES[methodSignature]!!
//            return try {
//                methodClass.newInstance()
//            } catch (e: Exception) {
//                throw RuntimeException("Exception instantiating emulated method $methodSignature", e)
//            }
//        }

        fun emulate(method: VirtualMethod, state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine): Array<out UnresolvedChild> {
            return emulate(method.signature, state, callerNode, vm)
        }

        fun emulate(methodSignature: String, state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine): Array<out UnresolvedChild> {
            val emulatedMethodCall = EMULATED_METHOD_SIGNATURES[methodSignature]!!
            return try {
                emulatedMethodCall.execute(state, callerNode, vm)
            } catch (e: Exception) {
                throw RuntimeException("Unhandled exception thrown emulating $methodSignature", e)
            }
        }
    }
}
