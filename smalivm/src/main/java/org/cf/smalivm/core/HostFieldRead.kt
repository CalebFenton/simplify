package org.cf.smalivm.core

import java.lang.reflect.Modifier

/** Exact identity of one resolved static field. */
data class StaticFieldDescriptor(
    val definingClass: VirtualType,
    val name: String,
    val type: VirtualType,
) {
    val descriptor: String get() = ClassIndex.fieldDescriptorOf(definingClass, name, type.descriptor)
}

internal fun staticFieldDescriptor(
    definingClass: VirtualType,
    descriptor: String,
    type: VirtualType,
): StaticFieldDescriptor {
    val member = descriptor.substringAfter("->")
    return StaticFieldDescriptor(definingClass, member.substringBefore(':'), type)
}

/** Result of one authorized host field read. */
internal sealed interface HostFieldRead {
    data class Returned(val value: HostValue) : HostFieldRead
    data class TargetThrew(val throwable: HostThrowable) : HostFieldRead
    data class Unsupported(val diagnostic: String) : HostFieldRead
    data class InfrastructureFailure(val cause: Throwable) : HostFieldRead
}

/**
 * Whether [policy] authorizes the guest to read the host field [field] names, decided before any host class
 * initializes. A static read is checked against the initialization chain the class model gives its declaring
 * class, the same chain a static host call is checked against; a class the model cannot describe is not
 * authorized.
 */
internal fun hostFieldReadIsAuthorized(
    field: TrackedFieldValue,
    classIndex: ClassIndex,
    policy: ReflectionPolicy,
): Boolean {
    if (!hostDeclaringClassMatchesVm(field, classIndex)) return false
    val chain = if (field.storage == FieldStorage.STATIC) {
        try {
            classIndex.hostInitializationChain(field.declaringType)
        } catch (_: ClassLoadFailure) {
            return false
        }
    } else {
        emptyList()
    }
    return policy.decideFieldRead(field, chain) is ReflectionDecision.Allowed
}

/**
 * Reads the framework static an authorized `sget` names. The VM models no access check for `sget`, so only a
 * public field of a public class, which every caller may read, is read.
 */
internal fun readHostStatic(classLoader: ClassLoader, field: StaticFieldDescriptor): HostFieldRead {
    val hostClass = hostBoundary({ return HostFieldRead.InfrastructureFailure(it) }) {
        try {
            field.definingClass.hostClass(classLoader)
        } catch (_: ClassNotFoundException) {
            return HostFieldRead.Unsupported("No host class matches ${field.definingClass}")
        }
    }
    if (!Modifier.isPublic(hostClass.modifiers)) {
        return HostFieldRead.Unsupported("Host class ${hostClass.name} is not public")
    }
    return readHostField(hostClass, field.name, field.type, receiver = null) { modifiers ->
        Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)
    }
}

/**
 * Reads the field [name] that [declaringClass] itself declares, for a guest whose access the caller has already
 * established. The guest, not this analyser, is the accessing class, so the host's own language check is
 * suppressed; a field the host does not open to reflection is unsupported. Reading a static may initialize
 * [declaringClass]; an [Error] other than a [VirtualMachineError] can only come from that initializer, which
 * rethrows an Error unchanged and wraps an exception in [ExceptionInInitializerError] (JLS 12.4.2), so it is the
 * target's throw.
 */
internal fun readHostField(
    declaringClass: Class<*>,
    name: String,
    type: VirtualType,
    receiver: Any?,
    acceptsModifiers: (Int) -> Boolean,
): HostFieldRead {
    val reflected = hostBoundary({ return HostFieldRead.InfrastructureFailure(it) }) {
        try {
            declaringClass.getDeclaredField(name)
        } catch (_: NoSuchFieldException) {
            return HostFieldRead.Unsupported("${declaringClass.name} declares no host field $name")
        }
    }
    if (!acceptsModifiers(reflected.modifiers) || virtualTypeOfHostClass(reflected.type) != type) {
        return HostFieldRead.Unsupported("Host field ${declaringClass.name}.$name does not match the analysed declaration")
    }
    if (!hostBoundary({ false }) { reflected.trySetAccessible() }) {
        return HostFieldRead.Unsupported("The host does not open ${declaringClass.name}.$name to reflection")
    }
    return hostBoundary({ failure ->
        when (failure) {
            is IllegalArgumentException, is Error -> HostFieldRead.TargetThrew(targetThrew(failure).throwable)
            else -> HostFieldRead.InfrastructureFailure(failure)
        }
    }) {
        HostFieldRead.Returned(reflected.get(receiver).toHostValue(type))
    }
}
