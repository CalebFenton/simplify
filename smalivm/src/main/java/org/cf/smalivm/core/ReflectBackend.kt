package org.cf.smalivm.core

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/** Executes policy-authorized host methods with exact JVM descriptor lookup. */
internal class ReflectBackend(
    private val policy: ReflectionPolicy,
    private val resolver: ProgramSnapshotResolver,
) : SynchronousBackend {
    constructor(policy: ReflectionPolicy, classIndex: ClassIndex) :
        this(policy, classIndex.resolver(classIndex.currentSnapshot))

    override val name: String get() = "reflection"

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
        val constructor = call.kind == InvokeKind.DIRECT && call.target.name == "<init>"
        if (call.kind == InvokeKind.SUPER || (call.kind == InvokeKind.DIRECT && !constructor)) {
            return BackendProbe.Declined("reflection does not implement ${call.kind} special invocation")
        }
        val initializationChain = if (call.kind == InvokeKind.STATIC || constructor) {
            try {
                resolver.hostInitializationChain(call.target.definingClass)
            } catch (_: ClassLoadFailure) {
                return BackendProbe.Denied("reflection cannot prove the host initialization chain for ${call.target}")
            }
        } else {
            emptyList()
        }
        val decision = policy.decide(
            call.target,
            hostInitializationChain = initializationChain,
        )
        if (decision is ReflectionDecision.Denied) {
            return BackendProbe.Denied("reflection policy denied ${call.target} by ${decision.rule}")
        }

        val receiverAvailable = when {
            call.kind == InvokeKind.STATIC -> true
            constructor -> call.receiver?.let { it.isLatticeKnown && it.isTrackedReference } == true
            else -> call.receiver?.isHostMaterializable == true
        }
        if (!receiverAvailable || call.arguments.any { !it.isHostMaterializable }) {
            return BackendProbe.Declined("${call.target} has an unknown or unmaterializable argument")
        }
        return BackendProbe.Execute(policy.effectOf(call.target))
    }

    override fun execute(call: SelectedHostCall): SynchronousCallResult {
        val declaringClass: Class<*>
        val parameterClasses: Array<Class<*>>
        val returnClass: Class<*>
        try {
            declaringClass = hostClass(call.target.definingClass)
            parameterClasses = call.parameterTypes.map(::hostClass).toTypedArray()
            returnClass = hostClass(call.returnType)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return SynchronousCallResult.InfrastructureFailure(failure)
        }

        return if (call.kind == InvokeKind.DIRECT && call.target.name == "<init>") {
            invokeConstructor(declaringClass, parameterClasses, call)
        } else {
            invokeMethod(declaringClass, parameterClasses, returnClass, call)
        }
    }

    private fun invokeConstructor(
        declaringClass: Class<*>,
        parameterClasses: Array<Class<*>>,
        call: SelectedHostCall,
    ): SynchronousCallResult {
        val constructor = try {
            declaringClass.getDeclaredConstructor(*parameterClasses)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return SynchronousCallResult.InfrastructureFailure(failure)
        }
        return invokeTarget {
            ConstructedHostObjectResult(constructor.newInstance(*call.arguments.toTypedArray()))
        }
    }

    private fun invokeMethod(
        declaringClass: Class<*>,
        parameterClasses: Array<Class<*>>,
        returnClass: Class<*>,
        call: SelectedHostCall,
    ): SynchronousCallResult {
        val matches = try {
            declaringClass.declaredMethods.filter { method ->
                method.name == call.target.name &&
                    method.parameterTypes.contentEquals(parameterClasses) &&
                    method.returnType == returnClass
            }
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return SynchronousCallResult.InfrastructureFailure(failure)
        }
        val method = when (matches.size) {
            0 -> return SynchronousCallResult.InfrastructureFailure(
                NoSuchMethodException("No exact JVM method matches ${call.target}"),
            )
            1 -> matches.single()
            else -> return SynchronousCallResult.InfrastructureFailure(
                IllegalStateException("Multiple exact JVM methods match ${call.target}"),
            )
        }
        val isStatic = Modifier.isStatic(method.modifiers)
        if ((call.kind == InvokeKind.STATIC) != isStatic) {
            return SynchronousCallResult.InfrastructureFailure(
                IllegalArgumentException("${call.target} static shape does not match ${call.kind}"),
            )
        }
        if (declaringClass == Method::class.java && method.name == "invoke") return invokeReflectedMethod(call)
        return invokeTarget {
            val raw = method.invoke(if (isStatic) null else call.receiver, *call.arguments.toTypedArray())
            SynchronousCallResult.Returned(importReturn(raw, call.returnType))
        }
    }

    /**
     * Runs a guest `Method.invoke` only when the policy also authorizes the reflected method, because
     * authorizing `Method.invoke` alone would run any host method the guest can name. The reflected method
     * is called directly so that `Method.invoke`'s own failures keep their types: nested JDK 21 reflection
     * reports the null-receiver NullPointerException as an IllegalArgumentException of the outer call.
     */
    private fun invokeReflectedMethod(call: SelectedHostCall): SynchronousCallResult {
        val target = call.receiver as? Method ?: return SynchronousCallResult.InfrastructureFailure(
            IllegalArgumentException("Method.invoke requires a reflected Method receiver"),
        )
        reflectedTargetDenial(target)?.let { return SynchronousCallResult.InfrastructureFailure(SecurityException(it)) }
        val arguments = when (val raw = call.arguments[1]) {
            null -> emptyArray<Any?>()
            is Array<*> -> raw
            else -> return SynchronousCallResult.InfrastructureFailure(
                IllegalArgumentException("Method.invoke requires an Object[] argument"),
            )
        }
        return try {
            SynchronousCallResult.Returned(importReturn(target.invoke(call.arguments[0], *arguments), call.returnType))
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (targetFailure: Throwable) {
            targetResult(targetFailure)
        }
    }

    /** Applies the probe's policy rule to the method a `Method.invoke` receiver reflects. */
    private fun reflectedTargetDenial(target: Method): String? {
        val descriptor = target.toReflectedMethodMetadataOrNull()?.descriptor
            ?: return "reflection cannot describe $target"
        val reflected = ReflectionPolicy.parseMethod(descriptor)
        val initializationChain = if (Modifier.isStatic(target.modifiers)) {
            try {
                resolver.hostInitializationChain(reflected.definingClass)
            } catch (_: ClassLoadFailure) {
                return "reflection cannot prove the host initialization chain for $reflected"
            }
        } else {
            emptyList()
        }
        val decision = policy.decide(reflected, initializationChain)
        return if (decision is ReflectionDecision.Denied) "reflection policy denied $reflected by ${decision.rule}" else null
    }

    private inline fun invokeTarget(block: () -> SynchronousCallResult): SynchronousCallResult = try {
        block()
    } catch (wrapper: InvocationTargetException) {
        val target = wrapper.targetException ?: return SynchronousCallResult.InfrastructureFailure(wrapper)
        if (target is VirtualMachineError) throw target
        targetResult(target)
    } catch (fatal: VirtualMachineError) {
        throw fatal
    } catch (target: LinkageError) {
        targetResult(target)
    } catch (failure: Throwable) {
        SynchronousCallResult.InfrastructureFailure(failure)
    }

    private fun targetResult(target: Throwable): SynchronousCallResult.TargetThrew =
        SynchronousCallResult.TargetThrew(HostThrowable(virtualTypeOfHostClass(target.javaClass), target))

    private fun importReturn(raw: Any?, declaredType: VirtualType): HostReturn {
        if (declaredType.isVoid) return HostReturn.Void
        if (declaredType.isPrimitive) {
            return HostReturn.Value(HostValue.Primitive(requireNotNull(raw), declaredType))
        }
        if (raw == null) return HostReturn.Value(HostValue.TypedNull(declaredType))
        return HostReturn.Value(HostValue.Reference(raw, virtualTypeOfHostClass(raw.javaClass)))
    }

    private fun hostClass(type: VirtualType): Class<*> = when (type.descriptor) {
        "Z" -> Boolean::class.javaPrimitiveType!!
        "B" -> Byte::class.javaPrimitiveType!!
        "C" -> Char::class.javaPrimitiveType!!
        "S" -> Short::class.javaPrimitiveType!!
        "I" -> Int::class.javaPrimitiveType!!
        "J" -> Long::class.javaPrimitiveType!!
        "F" -> Float::class.javaPrimitiveType!!
        "D" -> Double::class.javaPrimitiveType!!
        "V" -> Void.TYPE
        else -> Class.forName(type.hostClassName(), false, resolver.classLoader)
    }
}

private fun VirtualType.hostClassName(): String = when {
    descriptor.startsWith("[") -> descriptor.replace('/', '.')
    else -> descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')
}
