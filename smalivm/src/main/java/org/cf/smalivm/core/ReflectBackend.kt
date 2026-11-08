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
        val receiverClass = if (initializesClass) null else call.receiver?.trackedType
        authorization(call.target, initializesClass, receiverClass)?.let { return BackendProbe.Denied(it) }

        // A reflected constructor builds an object of exactly its own class, so it cannot run the superclass
        // constructor a subclass constructor chains to; that call runs as bytecode on the subclass object.
        if (constructor && call.receiver?.trackedType != call.target.definingClass) {
            return BackendProbe.Declined("${call.target} cannot construct a receiver of ${call.receiver?.trackedType}")
        }
        val receiverAvailable =
            call.kind == InvokeKind.STATIC || constructor || call.receiver?.isHostMaterializable == true
        // An authorized method is a host method, so its bundled framework body is not the fallback: running that
        // body on unknown input forks at every branch on the unknown, and the result is unknown anyway.
        if (!receiverAvailable || call.arguments.any { !it.isHostMaterializable }) {
            return BackendProbe.Denied("${call.target} has an unknown or unmaterializable argument")
        }
        // An array that reaches a parameter of a non-array type lets the host read its identity hash through
        // Object.hashCode or Object.toString, and the host's identity hash is not the device's.
        if (call.arguments.zip(call.parameterTypes).any { (argument, parameter) ->
                argument.trackedType?.isArray == true && !parameter.isArray
            }
        ) {
            return BackendProbe.Denied("${call.target} would expose an array's identity hash")
        }
        // Policy authorizes only methods with no effect outside the guest heap, and finalizing the call's host graph
        // raises the effect for whatever guest-visible state it changed, so the call declares none of its own.
        if (call.target !in REFLECTIVE_INVOCATIONS) return BackendProbe.Execute(SideEffectLevel.NONE)
        // A reflective invocation runs whatever its receiver names, so that target's policy decides.
        val reflected = call.receiver?.reflectedTarget
            ?: return BackendProbe.Denied("reflection cannot describe the target of ${call.target}")
        val reflectedReceiver = if (reflected.initializesClass) null else call.arguments.firstOrNull()?.trackedType
        authorization(reflected.method, reflected.initializesClass, reflectedReceiver)?.let { return BackendProbe.Denied(it) }
        return BackendProbe.Execute(SideEffectLevel.NONE)
    }

    /** The policy's denial of [target] on a receiver of [receiverClass], or null when it is authorized. */
    private fun authorization(target: MethodSignature, initializesClass: Boolean, receiverClass: VirtualType?): String? {
        val initializationChain = if (initializesClass) {
            try {
                resolver.hostInitializationChain(target.definingClass)
            } catch (_: ClassLoadFailure) {
                return "reflection cannot prove the host initialization chain for $target"
            }
        } else {
            emptyList()
        }
        val decision = policy.decide(target, initializationChain, receiverClass)
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
            exactMethod(declaringClass, call.target.name, parameterClasses, returnClass)?.let(::accessible)
                ?: return SynchronousCallResult.InfrastructureFailure(
                    NoSuchMethodException("No accessible exact JVM method matches ${call.target}"),
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
     * [method] when its class is public, else the public superclass or interface method it overrides. Virtual
     * dispatch from that method reaches [method], and reflection cannot call a member of a non-public class such
     * as `java.util.LinkedList$ListItr` directly. Null for a static method of a non-public class.
     */
    private fun accessible(method: Method): Method? {
        if (Modifier.isPublic(method.declaringClass.modifiers)) return method
        if (Modifier.isStatic(method.modifiers)) return null
        val pending = ArrayDeque<Class<*>>()
        method.declaringClass.superclass?.let(pending::addLast)
        pending.addAll(method.declaringClass.interfaces)
        while (pending.isNotEmpty()) {
            val type = pending.removeFirst()
            if (Modifier.isPublic(type.modifiers)) {
                val candidate = try {
                    type.getDeclaredMethod(method.name, *method.parameterTypes)
                } catch (_: NoSuchMethodException) {
                    null
                }
                if (candidate != null && Modifier.isPublic(candidate.modifiers)) return candidate
            }
            type.superclass?.let(pending::addLast)
            pending.addAll(type.interfaces)
        }
        return null
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
