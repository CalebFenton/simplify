package org.cf.smalivm

import org.apache.commons.beanutils.ConstructorUtils
import org.apache.commons.beanutils.MethodUtils
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.Value
import org.cf.util.ClassNameUtils
import org.cf.util.EnumAnalyzer
import org.cf.util.Utils
import org.slf4j.LoggerFactory
import java.util.*

class MethodReflector() {

    private class InvocationArguments constructor(val args: Array<Any?>, val parameterTypes: Array<Class<*>>)

    companion object {
        private val log = LoggerFactory.getLogger(MethodReflector::class.java.simpleName)
        private const val ENUM_INIT_SIGNATURE_PREFIX = "Ljava/lang/Enum;-><init>(Ljava/lang/String;"

        fun reflect(method: VirtualMethod, state: ExecutionState, classLoader: ClassLoader, enumAnalyzer: EnumAnalyzer): UnresolvedChild {
            log.debug("Reflecting {} with state:\n{}", method, state)
            val returnRaw = try {
                invoke(method, state, classLoader, enumAnalyzer)
            } catch (e: Exception) {
                log.warn("Failed to reflect {}: ", method, e)
                log.debug("Stack trace: ", e)
                return UnresolvedChild.build(e)
            }

            if (!method.returnsVoid()) {
                // If returning a primitive type, don't infer Value type from the actual value, i.e. an int may be boxed as Integer.
                // You shouldn't always use the method return type because it may be less specific than the actual value type.
                val returnValue = if (returnRaw != null && !ClassNameUtils.isPrimitive(method.returnType)) {
                    Value.wrap(returnRaw)
                } else {
                    Value.wrap(returnRaw, method.returnType)
                }
                state.assignReturnRegister(returnValue)
            }

            return UnresolvedChild.build()
        }


        @Throws(ClassNotFoundException::class)
        private fun buildInvocationArguments(method: VirtualMethod, state: ExecutionState): InvocationArguments {
            var registerOffset = 0
            val parameterTypeNames = LinkedList(method.parameterTypeNames)
            if (!method.isStatic) {
                // First parameter for non-static methods is the object instance and is not needed for JVM reflection invocation.
                registerOffset = 1
                parameterTypeNames.removeFirst()
            }
            val args = arrayOfNulls<Any>(parameterTypeNames.size)
            val parameterTypes: MutableList<Class<*>> = LinkedList()
            var i = 0
            var register = registerOffset
            while (i < parameterTypeNames.size) {
                val argItem = state.peekParameter(register)!!
                args[i] = argItem.value
                val parameterTypeName = parameterTypeNames[i]
                val parameterType: Class<*> = if (argItem.isPrimitive) {
                    ClassNameUtils.getPrimitiveClass(parameterTypeName)
                } else {
                    // Shouldn't need a VM class loader since these are all safe to reflect on the JVM
                    // Also, some classes are arrays and loadClass only works for non-array, non-primitive types
                    Class.forName(ClassNameUtils.internalToBinary(parameterTypeName))
                }
                parameterTypes[i] = parameterType
                register += Utils.getRegisterSize(parameterTypeName)
                i++
            }
            return InvocationArguments(args, parameterTypes.toTypedArray())
        }

        private operator fun invoke(method: VirtualMethod, state: ExecutionState, classLoader: ClassLoader, enumAnalyzer: EnumAnalyzer): Any? {
            val klazz = Class.forName(method.binaryClassName)
            val invocationArgs = buildInvocationArguments(method, state)
            val args = invocationArgs.args
            return if (method.isStatic) {
                log.debug("Reflecting static method {}, clazz={} args={}", method, klazz, args.contentToString())
                MethodUtils.invokeStaticMethod(klazz, method.name, args, invocationArgs.parameterTypes)
            } else {
                if ("<init>" == method.name) {
                    log.debug("Reflecting constructor {}, class={} args={}", method, klazz, args.contentToString())
                    val instance = if (method.signature.startsWith(ENUM_INIT_SIGNATURE_PREFIX)) {
                        invokeEnumInit(state, args[0] as String, classLoader, enumAnalyzer)
                    } else {
                        ConstructorUtils.invokeConstructor(klazz, args)
                    }
                    val instanceItem = Value.wrap(instance)
                    state.assignRegister(0, instanceItem)
                    instance
                } else {
                    val targetItem = state.peekRegister(0)!!
                    log.debug("Reflecting virtual method {}, target={} args={}", method, targetItem, args.contentToString())
                    MethodUtils.invokeMethod(targetItem.value!!, method.name, args, invocationArgs.parameterTypes)
                }
            }
        }

        @Throws(ClassNotFoundException::class)
        private fun invokeEnumInit(state: ExecutionState, name: String, classLoader: ClassLoader, enumAnalyzer: EnumAnalyzer): Any {
            /*
             * Enums can't be instantiated by calling newInstance() on the constructor,
             * even with setAccessible(true). It fails with InstantiationException.
             * http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.9
             */
            var name = name
            val instance = state.peekParameter(0)!!
            val enumType: String = ClassNameUtils.internalToSource(instance.type)
            val enumClass = classLoader.loadClass(enumType) as Class<out Enum<*>?>
            return try {
                java.lang.Enum.valueOf(enumClass, name)!!
            } catch (e: IllegalArgumentException) {
                enumAnalyzer.analyze(enumClass)
                name = enumAnalyzer.getObfuscatedName(name)!!
                java.lang.Enum.valueOf(enumClass, name)!!
            }
        }
    }
}
