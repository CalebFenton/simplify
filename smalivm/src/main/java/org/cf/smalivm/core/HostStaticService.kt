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

/** Immutable input to [HostStaticService]. It contains no scheduler or path state. */
internal data class HostStaticRequest(val field: StaticFieldDescriptor)

/** Result of one authorized host static access. */
internal sealed interface HostStaticResult {
    data class Returned(val value: HostValue) : HostStaticResult
    data class TargetThrew(val throwable: HostThrowable) : HostStaticResult
    data class Unsupported(val diagnostic: String) : HostStaticResult
    data class InfrastructureFailure(val cause: Throwable) : HostStaticResult
    data class Fatal(val cause: VirtualMachineError) : HostStaticResult
}

/** Reads authorized host statics. The scheduler owns authorization and result import. */
internal class HostStaticService(
    private val reader: (HostStaticRequest) -> HostStaticResult,
) {
    internal fun read(request: HostStaticRequest): HostStaticResult = reader(request)
}

internal fun hostStaticService(classLoader: ClassLoader): HostStaticService =
    HostStaticService { request -> reflectStatic(classLoader, request) }

private fun reflectStatic(classLoader: ClassLoader, request: HostStaticRequest): HostStaticResult {
    val field = request.field
    val hostClass = try {
        Class.forName(field.definingClass.hostClassName(), false, classLoader)
    } catch (_: ClassNotFoundException) {
        return HostStaticResult.Unsupported("No host class matches ${field.definingClass}")
    } catch (fatal: VirtualMachineError) {
        return HostStaticResult.Fatal(fatal)
    } catch (failure: Throwable) {
        return HostStaticResult.InfrastructureFailure(failure)
    }
    val reflected = try {
        hostClass.getField(field.name)
    } catch (_: NoSuchFieldException) {
        return HostStaticResult.Unsupported("No public host field matches ${field.descriptor}")
    } catch (_: SecurityException) {
        return HostStaticResult.Unsupported("Host access is unavailable for ${field.descriptor}")
    }
    if (!Modifier.isStatic(reflected.modifiers)) {
        return HostStaticResult.Unsupported("Host field ${field.descriptor} is not static")
    }
    if (virtualTypeOfHostFieldClass(reflected.type) != field.type) {
        return HostStaticResult.Unsupported("Host field type does not match ${field.descriptor}")
    }
    val raw = try {
        reflected.get(null)
    } catch (_: IllegalAccessException) {
        return HostStaticResult.Unsupported("Host access is unavailable for ${field.descriptor}")
    } catch (_: SecurityException) {
        return HostStaticResult.Unsupported("Host access is unavailable for ${field.descriptor}")
    } catch (fatal: VirtualMachineError) {
        return HostStaticResult.Fatal(fatal)
    } catch (target: Error) {
        return HostStaticResult.TargetThrew(HostThrowable(virtualTypeOfHostClass(target.javaClass), target))
    } catch (failure: Throwable) {
        return HostStaticResult.InfrastructureFailure(failure)
    }
    return HostStaticResult.Returned(raw.toHostValue(field.type))
}

private fun Any?.toHostValue(declaredType: VirtualType): HostValue = when {
    declaredType.isPrimitive -> HostValue.Primitive(requireNotNull(this), declaredType)
    this == null -> HostValue.TypedNull(declaredType)
    else -> HostValue.Reference(this, virtualTypeOfHostClass(javaClass))
}

private fun VirtualType.hostClassName(): String = when {
    descriptor.startsWith("[") -> descriptor.replace('/', '.')
    else -> descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')
}
