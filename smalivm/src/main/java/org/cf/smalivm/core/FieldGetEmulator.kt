package org.cf.smalivm.core

import org.cf.util.ClassNameUtils

/** Applies Java field access law before virtual or reauthorized host reads. */
internal class FieldGetEmulator(
    private val resolver: ProgramSnapshotResolver,
    private val reflectionPolicy: ReflectionPolicy,
) : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val field = call.receiver as? TrackedFieldEmulatorValue
            ?: return returnUnknown(call.returnType)
        val argument = call.arguments.singleOrNull()
            ?: return SynchronousCallResult.InfrastructureFailure(
                IllegalArgumentException("Field.get requires one receiver argument"),
            )
        val metadata = field.metadata
        if (metadata.storage == FieldStorage.INSTANCE && argument is EmulatorValue.Known && argument.javaValue == null) {
            return targetThrew(NullPointerException())
        }
        if (metadata.domain is TrackedFieldDomain.Host && !hostFieldReadIsAuthorized(metadata, resolver, reflectionPolicy)) {
            return returnUnknown(reflectionBoxedType(metadata.fieldType))
        }
        val receiverType = receiverType(argument)
        val receiverClass = (argument as? EmulatorValue.Known)?.javaValue?.javaClass
        val access = hostBoundary({ return SynchronousCallResult.InfrastructureFailure(it) }) {
            permitsAccess(metadata, field.accessibility, call.callerClass, receiverType, receiverClass)
        }
        if (!access) return targetThrew(IllegalAccessException())
        if (
            metadata.storage == FieldStorage.INSTANCE &&
            receiverType != null &&
            !isSubtype(receiverType, metadata.declaringType, metadata.domain, receiverClass)
        ) {
            return targetThrew(IllegalArgumentException())
        }
        if (
            metadata.domain == TrackedFieldDomain.Virtual &&
            metadata.storage == FieldStorage.STATIC &&
            call.classInitStatus.actionFor(metadata.declaringType) != ClassInitAction.Proceed
        ) {
            return SynchronousCallResult.NeedsClassInit(metadata.declaringType)
        }

        return when (metadata.domain) {
            TrackedFieldDomain.Virtual -> when {
                metadata.storage == FieldStorage.INSTANCE && argument is EmulatorValue.Unknown ->
                    returnUnknown(reflectionBoxedType(metadata.fieldType))
                else -> IntrinsicCallResult(IntrinsicOperation.ReadVirtualField(metadata))
            }
            is TrackedFieldDomain.Host -> readAuthorizedHostField(metadata, argument)
        }
    }

    private fun receiverType(argument: EmulatorValue): VirtualType? = when (argument) {
        is EmulatorValue.Known -> argument.javaValue?.let { virtualTypeOfHostClass(it.javaClass) }
        is EmulatorValue.TrackedReference -> argument.type
        is TrackedFieldEmulatorValue -> argument.type
        is EmulatorValue.Unknown -> null
    }

    private fun permitsAccess(
        field: TrackedFieldValue,
        accessibility: FieldAccessibility,
        caller: VirtualType,
        receiverType: VirtualType?,
        receiverClass: Class<*>?,
    ): Boolean {
        if (accessibility == FieldAccessibility.OVERRIDDEN) return true
        val samePackage = samePackage(caller, field)
        return when (field.visibility) {
            FieldVisibility.PUBLIC -> true
            FieldVisibility.PRIVATE -> isDeclaringClass(caller, field)
            FieldVisibility.PACKAGE -> samePackage
            FieldVisibility.PROTECTED -> when {
                samePackage -> true
                !isSubtype(caller, field.declaringType, field.domain) -> false
                field.storage == FieldStorage.STATIC -> true
                receiverType == null -> false
                else -> isSubtype(receiverType, caller, field.domain, receiverClass)
            }
        }
    }

    private fun samePackage(caller: VirtualType, field: TrackedFieldValue): Boolean = when (val domain = field.domain) {
        TrackedFieldDomain.Virtual -> packageName(caller) == packageName(field.declaringType)
        is TrackedFieldDomain.Host -> {
            val callerClass = resolver.loadClassConstant(caller.descriptor) ?: return false
            callerClass.packageName == domain.declaringClass.packageName &&
                callerClass.classLoader === domain.declaringClass.classLoader
        }
    }

    /**
     * ART's reflective access check (`reflection.cc` `VerifyAccess`) admits a private member only to its declaring
     * class: unlike the JVM, it grants nestmates nothing.
     */
    private fun isDeclaringClass(caller: VirtualType, field: TrackedFieldValue): Boolean = when (val domain = field.domain) {
        TrackedFieldDomain.Virtual -> caller == field.declaringType
        is TrackedFieldDomain.Host -> resolver.loadClassConstant(caller.descriptor) === domain.declaringClass
    }

    private fun isSubtype(
        candidate: VirtualType,
        ancestor: VirtualType,
        domain: TrackedFieldDomain,
        candidateClass: Class<*>? = null,
    ): Boolean = when (domain) {
        TrackedFieldDomain.Virtual -> resolver.hierarchy.isSubtypeOf(candidate, ancestor)
        is TrackedFieldDomain.Host -> {
            val actualCandidate = candidateClass ?: hostClass(candidate, domain) ?: return false
            val ancestorClass = hostClass(ancestor, domain) ?: return false
            ancestorClass.isAssignableFrom(actualCandidate)
        }
    }

    private fun hostClass(type: VirtualType, domain: TrackedFieldDomain.Host): Class<*>? =
        if (type == virtualTypeOfHostClass(domain.declaringClass)) domain.declaringClass
        else resolver.loadClassConstant(type.descriptor)

    private fun readAuthorizedHostField(field: TrackedFieldValue, argument: EmulatorValue): SynchronousCallResult {
        val receiver = if (field.storage == FieldStorage.STATIC) {
            null
        } else {
            (argument as? EmulatorValue.Known)?.javaValue ?: return returnUnknown(reflectionBoxedType(field.fieldType))
        }
        val declaringClass = (field.domain as TrackedFieldDomain.Host).declaringClass
        return when (
            val read = readHostField(declaringClass, field.name, field.fieldType, receiver) { it == field.modifiers }
        ) {
            is HostFieldRead.Returned -> {
                val value = when (val raw = read.value) {
                    is HostValue.Primitive -> HostValue.Reference(raw.value, virtualTypeOfHostClass(raw.value.javaClass))
                    else -> raw
                }
                SynchronousCallResult.Returned(HostReturn.Value(value))
            }
            is HostFieldRead.TargetThrew -> SynchronousCallResult.TargetThrew(read.throwable)
            is HostFieldRead.Unsupported -> returnUnknown(reflectionBoxedType(field.fieldType))
            is HostFieldRead.InfrastructureFailure -> SynchronousCallResult.InfrastructureFailure(read.cause)
        }
    }
}

/** The type reflection (`Field.get`, `Method.invoke`) returns for a value of [type]: its wrapper class when primitive. */
internal fun reflectionBoxedType(type: VirtualType): VirtualType =
    if (type.isPrimitive) {
        virtualTypeOfHostClass(ClassNameUtils.getPrimitiveClass(type.descriptor).kotlin.javaObjectType)
    } else {
        type
    }

/**
 * What reflection returns for [value] of [type]. `Field.get` and `Method.invoke` box a primitive into its wrapper
 * as `valueOf` does (ART `BoxPrimitive`), so the result is always one reference, never a wide register pair.
 */
internal fun reflectionBoxed(value: Value, type: VirtualType): Value {
    if (!type.isPrimitive) return value
    val wrapper = reflectionBoxedType(type)
    val known = value as? Value.Known ?: return Value.Unknown(wrapper)
    val boxed: Any? = when (type.descriptor) {
        "I" -> Coerce.toInt(known.raw)
        "J" -> Coerce.toLong(known.raw)
        "F" -> Coerce.toFloat(known.raw)
        "D" -> Coerce.toDouble(known.raw)
        else -> (Coerce.narrowStore(known, type) as? Value.Known)?.raw
    }
    return if (boxed == null) Value.Unknown(wrapper) else Value.Known(boxed, wrapper)
}

private fun packageName(type: VirtualType): String =
    type.descriptor.substringBeforeLast('/', missingDelimiterValue = "")
