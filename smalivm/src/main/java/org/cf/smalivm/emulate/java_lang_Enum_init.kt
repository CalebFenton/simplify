package org.cf.smalivm.emulate

import org.cf.smalivm2.*
import org.cf.util.ClassNameUtils
import org.slf4j.LoggerFactory

internal class java_lang_Enum_init : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine2): Array<out UnresolvedChild> {
        /*
         * Enums can't be instantiated by calling newInstance() on the constructor,
         * even with setAccessible(true). It fails with InstantiationException.
         * http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.9
         */
        val instance = state.peekParameterOffset(0)!!
        var name = state.peekParameterOffset(1)!!.raw as String
//        val ordinal = state.peekParameterOffset(2)!!
        val enumType = ClassNameUtils.internalToSource(instance.type)
        val enumClass = vm.classLoader.loadClass(enumType) as Class<out Enum<*>?>
        val rawInstance = try {
            java.lang.Enum.valueOf(enumClass, name)!!
        } catch (e: IllegalArgumentException) {
            vm.enumAnalyzer.analyze(enumClass)
            name = vm.enumAnalyzer.getObfuscatedName(name)!!
            java.lang.Enum.valueOf(enumClass, name)!!

        }
        val newInstance = Value.wrap(rawInstance)
        state.assignRegister(state.firstParameterRegister, newInstance, updateIdentities = true)
        return finishMethod()
    }

    companion object {
        private val log = LoggerFactory.getLogger(java_lang_Enum_init::class.java.simpleName)
    }
}
