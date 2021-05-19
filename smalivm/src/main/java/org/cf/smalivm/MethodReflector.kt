package org.cf.smalivm

import org.apache.commons.beanutils.ConstructorUtils
import org.apache.commons.beanutils.MethodUtils
import org.cf.smalivm.type.VirtualMethod
import org.cf.util.ClassNameUtils
import org.cf.util.Utils
import org.slf4j.LoggerFactory
import java.util.*

class MethodReflector {

    private data class ReflectionArguments constructor(val args: Array<Any?>, val parameterTypes: Array<Class<*>?>)

    companion object {
        private val log = LoggerFactory.getLogger(MethodReflector::class.java.simpleName)
        private val childProducer = UnresolvedChildProducer()

        fun reflect(method: VirtualMethod, state: ExecutionState): Array<out UnresolvedChild> {
            log.debug("Reflecting {} with state:\n{}", method, state)
            val returnRaw = try {
                invoke(method, state)
            } catch (e: Exception) {
                log.warn("Failed to reflect {}: ", method, e)
                log.debug("Stack trace: ", e)
                return childProducer.throwException(e)
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

            return childProducer.finishMethod()
        }

        @Throws(ClassNotFoundException::class)
        private fun buildReflectionArguments(method: VirtualMethod, state: ExecutionState): ReflectionArguments {
            var registerOffset = 0
            val parameterTypeNames = LinkedList(method.parameterTypeNames)
            if (!method.isStatic) {
                // First parameter for non-static methods is the object instance and is not needed for JVM reflection invocation.
                registerOffset = 1
                parameterTypeNames.removeFirst()
            }
            val args = arrayOfNulls<Any>(parameterTypeNames.size)
            val parameterTypes = arrayOfNulls<Class<*>>(parameterTypeNames.size)
            var i = 0
            while (i < parameterTypeNames.size) {
                val argItem = state.peekParameterOffset(registerOffset)!!
                args[i] = argItem.raw
                val parameterTypeName = parameterTypeNames[i]
                val parameterType: Class<*> = if (argItem.isPrimitive) {
                    ClassNameUtils.getPrimitiveClass(parameterTypeName)
                } else {
                    // Shouldn't need a VM class loader since these are all safe to reflect on the JVM
                    // Also, some classes are arrays and loadClass only works for non-array, non-primitive types
                    Class.forName(ClassNameUtils.internalToBinary(parameterTypeName))
                }
                parameterTypes[i] = parameterType
                registerOffset += Utils.getRegisterSize(parameterTypeName)
                i++
            }
            return ReflectionArguments(args, parameterTypes)
        }

        private operator fun invoke(method: VirtualMethod, state: ExecutionState): Any? {
            val klazz = Class.forName(method.binaryClassName)
            val invocationArgs = buildReflectionArguments(method, state)
            val args = invocationArgs.args
            return if (method.isStatic) {
                log.debug("Reflecting static method {}, clazz={} args={}", method, klazz, args.contentToString())
                MethodUtils.invokeStaticMethod(klazz, method.name, args, invocationArgs.parameterTypes)
            } else {
                if (method.name == "<init>") {
                    log.debug("Reflecting constructor {}, class={} args={}", method, klazz, args.contentToString())
                    val rawInstance = ConstructorUtils.invokeConstructor(klazz, args)
                    val instance = Value.wrap(rawInstance)
                    state.assignRegister(state.firstParameterRegister, instance, updateIdentities = true)
                    rawInstance
                } else {
                    val instance = state.peekParameterOffset(0)!!
                    log.debug("Reflecting virtual method {}, target={} args={}", method, instance, args.contentToString())
                    MethodUtils.invokeMethod(instance.raw!!, method.name, args, invocationArgs.parameterTypes)
                }
            }
        }
    }
}
