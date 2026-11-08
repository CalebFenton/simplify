package org.cf.smalivm.core

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/** Executes policy-authorized host methods with exact JVM descriptor lookup. */
internal class ReflectBackend(
    private val policy: ReflectionPolicy,
    private val resolver: ProgramSnapshotResolver,
) : SynchronousBackend {
    override val name: String get() = "reflection"

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
        val constructor = call.kind == InvokeKind.DIRECT && call.target.isConstructor
        if (call.kind == InvokeKind.SUPER || (call.kind == InvokeKind.DIRECT && !constructor)) {
            return BackendProbe.Declined("reflection does not implement ${call.kind} special invocation")
        }
        val initializesClass = call.kind == InvokeKind.STATIC || constructor
        authorization(call.target, initializesClass)?.let { return BackendProbe.Denied(it) }

        val receiverAvailable = when {
            call.kind == InvokeKind.STATIC -> true
            constructor -> call.receiver?.let { it.isLatticeKnown && it.isTrackedReference } == true
            else -> call.receiver?.isHostMaterializable == true
        }
        if (!receiverAvailable || call.arguments.any { !it.isHostMaterializable }) {
            return BackendProbe.Declined("${call.target} has an unknown or unmaterializable argument")
        }
        if (call.target !in REFLECTIVE_INVOCATIONS) return BackendProbe.Execute(policy.effectOf(call.target))
        // A reflective invocation runs whatever its receiver names, so that target's policy and effect decide.
        val reflected = call.receiver?.reflectedTarget
            ?: return BackendProbe.Denied("reflection cannot describe the target of ${call.target}")
        authorization(reflected.method, reflected.initializesClass)?.let { return BackendProbe.Denied(it) }
        return BackendProbe.Execute(policy.effectOf(reflected.method))
    }

    /** The policy's denial of [target], or null when it is authorized. */
    private fun authorization(target: MethodSignature, initializesClass: Boolean): String? {
        val initializationChain = if (initializesClass) {
            try {
                resolver.hostInitializationChain(target.definingClass)
            } catch (_: ClassLoadFailure) {
                return "reflection cannot prove the host initialization chain for $target"
            }
        } else {
            emptyList()
        }
        val decision = policy.decide(target, initializationChain)
        return if (decision is ReflectionDecision.Denied) "reflection policy denied $target by ${decision.rule}" else null
    }

    override fun execute(call: SelectedHostCall): SynchronousCallResult {
        val classLoader = resolver.classLoader
        val classes = hostBoundary({ return SynchronousCallResult.InfrastructureFailure(it) }) {
            listOf(call.target.definingClass, *call.parameterTypes.toTypedArray(), call.returnType)
                .map { it.hostClass(classLoader) }
        }
        val declaringClass = classes.first()
        val parameterClasses = classes.subList(1, classes.lastIndex).toTypedArray()
        val returnClass = classes.last()

        return if (call.kind == InvokeKind.DIRECT && call.target.isConstructor) {
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
        val constructor = hostBoundary({ return SynchronousCallResult.InfrastructureFailure(it) }) {
            declaringClass.getDeclaredConstructor(*parameterClasses)
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
        val method = hostBoundary({ return SynchronousCallResult.InfrastructureFailure(it) }) {
            exactMethod(declaringClass, call.target.name, parameterClasses, returnClass)
                ?: return SynchronousCallResult.InfrastructureFailure(
                    NoSuchMethodException("No exact JVM method matches ${call.target}"),
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
     * The method [declaringClass] declares with exactly this JVM descriptor. `getDeclaredMethod` picks the most
     * specific return type among same-parameter bridge methods, so only a different return type scans the rest.
     */
    private fun exactMethod(
        declaringClass: Class<*>,
        name: String,
        parameterClasses: Array<Class<*>>,
        returnClass: Class<*>,
    ): Method? {
        val candidate = try {
            declaringClass.getDeclaredMethod(name, *parameterClasses)
        } catch (_: NoSuchMethodException) {
            return null
        }
        if (candidate.returnType == returnClass) return candidate
        return declaringClass.declaredMethods.firstOrNull { method ->
            method.name == name && method.returnType == returnClass && method.parameterTypes.contentEquals(parameterClasses)
        }
    }

    /**
     * Runs a guest `Method.invoke`, whose reflected method the probe authorized. The reflected method is
     * called directly so that `Method.invoke`'s own failures keep their types: nested JDK 21 reflection
     * reports the null-receiver NullPointerException as an IllegalArgumentException of the outer call.
     */
    private fun invokeReflectedMethod(call: SelectedHostCall): SynchronousCallResult {
        val target = call.receiver as? Method ?: return SynchronousCallResult.InfrastructureFailure(
            IllegalArgumentException("Method.invoke requires a reflected Method receiver"),
        )
        val arguments = when (val raw = call.arguments[1]) {
            null -> emptyArray<Any?>()
            is Array<*> -> raw
            else -> return SynchronousCallResult.InfrastructureFailure(
                IllegalArgumentException("Method.invoke requires an Object[] argument"),
            )
        }
        return hostBoundary({ targetThrew(it) }) {
            SynchronousCallResult.Returned(importReturn(target.invoke(call.arguments[0], *arguments), call.returnType))
        }
    }

    private inline fun invokeTarget(block: () -> SynchronousCallResult): SynchronousCallResult =
        hostBoundary({ failure ->
            when (failure) {
                is InvocationTargetException -> {
                    val target = failure.targetException ?: return SynchronousCallResult.InfrastructureFailure(failure)
                    if (target is VirtualMachineError) throw target
                    targetThrew(target)
                }
                is LinkageError -> targetThrew(failure)
                else -> SynchronousCallResult.InfrastructureFailure(failure)
            }
        }, block)

    private fun importReturn(raw: Any?, declaredType: VirtualType): HostReturn =
        if (declaredType.isVoid) HostReturn.Void else HostReturn.Value(raw.toHostValue(declaredType))

    private companion object {
        val REFLECTIVE_INVOCATIONS = setOf(
            MethodSignature(
                VirtualType.of("Ljava/lang/reflect/Method;"),
                "invoke",
                "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
            ),
            MethodSignature(
                VirtualType.of("Ljava/lang/reflect/Constructor;"),
                "newInstance",
                "([Ljava/lang/Object;)Ljava/lang/Object;",
            ),
        )
    }
}
