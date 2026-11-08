package org.cf.smalivm.core

/** Applies Java field access law before virtual or reauthorized host reads. */
internal class FieldGetEmulator(
    private val classIndex: ClassIndex,
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
        if (metadata.domain is TrackedFieldDomain.Host && !hostFieldReadIsAuthorized(metadata, classIndex, reflectionPolicy)) {
            return returnUnknown(metadata.fieldType)
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
        if (metadata.domain == TrackedFieldDomain.Virtual && metadata.storage == FieldStorage.STATIC) {
            when (call.classInitStatus.actionFor(metadata.declaringType)) {
                ClassInitAction.Proceed -> Unit
                ClassInitAction.StartInitialization ->
                    return SynchronousCallResult.NeedsClassInit(metadata.declaringType)
                is ClassInitAction.ThrowFailed ->
                    return IntrinsicCallResult(IntrinsicOperation.RouteFailedClassInit(metadata.declaringType))
            }
        }

        return when (metadata.domain) {
            TrackedFieldDomain.Virtual -> when {
                metadata.storage == FieldStorage.INSTANCE && argument is EmulatorValue.Unknown ->
                    returnUnknown(metadata.fieldType)
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
            FieldVisibility.PRIVATE -> areNestmates(caller, field)
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
            val callerClass = classIndex.loadClassConstant(caller.descriptor) ?: return false
            callerClass.packageName == domain.declaringClass.packageName &&
                callerClass.classLoader === domain.declaringClass.classLoader
        }
    }

    private fun areNestmates(caller: VirtualType, field: TrackedFieldValue): Boolean = when (val domain = field.domain) {
        TrackedFieldDomain.Virtual -> classIndex.areNestmates(caller, field.declaringType)
        is TrackedFieldDomain.Host ->
            classIndex.loadClassConstant(caller.descriptor)?.isNestmateOf(domain.declaringClass) == true
    }

    private fun isSubtype(
        candidate: VirtualType,
        ancestor: VirtualType,
        domain: TrackedFieldDomain,
        candidateClass: Class<*>? = null,
    ): Boolean = when (domain) {
        TrackedFieldDomain.Virtual -> classIndex.isSubtypeOf(candidate, ancestor)
        is TrackedFieldDomain.Host -> {
            val actualCandidate = candidateClass ?: hostClass(candidate, domain) ?: return false
            val ancestorClass = hostClass(ancestor, domain) ?: return false
            ancestorClass.isAssignableFrom(actualCandidate)
        }
    }

    private fun hostClass(type: VirtualType, domain: TrackedFieldDomain.Host): Class<*>? =
        if (type == virtualTypeOfHostClass(domain.declaringClass)) domain.declaringClass
        else classIndex.loadClassConstant(type.descriptor)

    private fun readAuthorizedHostField(field: TrackedFieldValue, argument: EmulatorValue): SynchronousCallResult {
        val receiver = if (field.storage == FieldStorage.STATIC) {
            null
        } else {
            (argument as? EmulatorValue.Known)?.javaValue ?: return returnUnknown(field.fieldType)
        }
        val declaringClass = (field.domain as TrackedFieldDomain.Host).declaringClass
        return when (
            val read = readHostField(declaringClass, field.name, field.fieldType, receiver) { it == field.modifiers }
        ) {
            is HostFieldRead.Returned -> SynchronousCallResult.Returned(HostReturn.Value(read.value))
            is HostFieldRead.TargetThrew -> SynchronousCallResult.TargetThrew(read.throwable)
            is HostFieldRead.Unsupported -> returnUnknown(field.fieldType)
            is HostFieldRead.InfrastructureFailure -> SynchronousCallResult.InfrastructureFailure(read.cause)
        }
    }
}

private fun packageName(type: VirtualType): String =
    type.descriptor.substringBeforeLast('/', missingDelimiterValue = "")
