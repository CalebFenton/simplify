package org.cf.util

import org.cf.smalivm.opcode.ConstOp
import org.cf.smalivm.opcode.SPutOp
import org.cf.smalivm.VirtualMachine

class EnumAnalyzer(val vm: VirtualMachine) {
    private val originalNameToObfuscatedName: MutableMap<String, String> = HashMap()

    fun analyze(enumClass: Class<out Enum<*>>) {
        /*
         * If an enum is obfuscated, say with Proguard, the field names change. This is a problem
         * because it's not possible to instantiate Enums using reflection. Instead, you must use
         * Enum.valueOf(Class, String) where the String is the Enum name and also must be the name
         * of the *field*. If the field name changes, the actual string used in the enum's <clinit>
         * to initialize the field doesn't change. So we end up getting the valueOf() something
         * that doesn't exist. To deal with this, we "analyze" the enum to figure out which original
         * string maps to which field name. Then, when the code asks for the original, we look up
         * the mapped, obfuscated field name and use that instead.
         */
        val classManager = vm.classManager
        val virtualClass = classManager.getVirtualClass(enumClass)
        val method = virtualClass.getMethod(METHOD_DESCRIPTOR)!!
        val graph = vm.getExecutionGraph(method)
        //val fields: List<VirtualField> = ArrayList(virtualClass.getFields())
        var lookingForField = false
        var name: String? = null
        for (address in graph.getAddresses()) {
            // Would be smarter, though significantly more work, to analyze semantics and know for
            // sure which literal was put into which field.
            val op = graph.getOp(address)
            if (op is ConstOp) {
                if (op.constantType === ConstOp.ConstantType.STRING) {
                    name = op.literal as String
                    lookingForField = true
                }
            } else if (op is SPutOp && lookingForField) {
                val obfuscatedName: String = op.field.name
                originalNameToObfuscatedName[name!!] = obfuscatedName
                lookingForField = false
            }
        }
    }

    fun getObfuscatedName(name: String): String? {
        return originalNameToObfuscatedName[name]
    }

    companion object {
        private const val METHOD_DESCRIPTOR = "<clinit>()V"
    }
}
