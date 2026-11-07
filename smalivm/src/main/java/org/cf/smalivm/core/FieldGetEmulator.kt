package org.cf.smalivm.core

/** Applies Java field access law before virtual or reauthorized host reads. */
internal class FieldGetEmulator(
    private val classIndex: ClassIndex,
    private val reflectionPolicy: ReflectionPolicy,
) : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val field = call.receiver as? TrackedFieldEmulatorValue
            ?: return IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(call.returnType))
        val argument = call.arguments.singleOrNull()
            ?: return SynchronousCallResult.InfrastructureFailure(
                IllegalArgumentException("Field.get requires one receiver argument"),
            )
        val metadata = field.metadata
        if (metadata.storage == FieldStorage.INSTANCE && argument is EmulatorValue.Known && argument.javaValue == null) {
            return targetThrow(NullPointerException())
        }
        if (metadata.domain is TrackedFieldDomain.Host && !hostReadIsAuthorized(metadata)) {
            return IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(metadata.fieldType))
        }
        val receiverType = receiverType(argument)
        val receiverClass = (argument as? EmulatorValue.Known)?.javaValue?.javaClass
        val access = try {
            permitsAccess(metadata, field.accessibility, call.callerClass, receiverType, receiverClass)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return SynchronousCallResult.InfrastructureFailure(failure)
        }
        if (!access) return targetThrow(IllegalAccessException())
        if (
            metadata.storage == FieldStorage.INSTANCE &&
            receiverType != null &&
            !isSubtype(receiverType, metadata.declaringType, metadata.domain, receiverClass)
        ) {
            return targetThrow(IllegalArgumentException())
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
                    IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(metadata.fieldType))
                else -> IntrinsicCallResult(IntrinsicOperation.ReadVirtualField(metadata))
            }
            is TrackedFieldDomain.Host -> readHostField(metadata, argument)
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

    private fun hostReadIsAuthorized(field: TrackedFieldValue): Boolean {
        val domain = field.domain as TrackedFieldDomain.Host
        if (!hostDeclaringClassMatchesVm(field, classIndex)) return false
        val chain = if (field.storage == FieldStorage.STATIC) {
            hostFieldInitializationChain(domain.declaringClass)
        } else {
            emptyList()
        }
        return reflectionPolicy.decideFieldRead(field, chain) is ReflectionDecision.Allowed
    }

    private fun readHostField(
        field: TrackedFieldValue,
        argument: EmulatorValue,
    ): SynchronousCallResult {
        val declaringClass = (field.domain as TrackedFieldDomain.Host).declaringClass
        val reflected = try {
            declaringClass.getDeclaredField(field.name)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return SynchronousCallResult.InfrastructureFailure(failure)
        }
        if (
            reflected.modifiers != field.modifiers ||
            virtualTypeOfHostFieldClass(reflected.type) != field.fieldType ||
            !reflected.trySetAccessible()
        ) {
            return SynchronousCallResult.InfrastructureFailure(
                IllegalStateException("Authorized host field metadata changed for ${field.descriptor}"),
            )
        }
        val rawReceiver = if (field.storage == FieldStorage.STATIC) null else (argument as? EmulatorValue.Known)?.javaValue
            ?: return IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(field.fieldType))
        return try {
            SynchronousCallResult.Returned(HostReturn.Value(reflected.get(rawReceiver).toHostValue(field.fieldType)))
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (target: IllegalAccessException) {
            targetThrow(target)
        } catch (target: IllegalArgumentException) {
            targetThrow(target)
        } catch (target: LinkageError) {
            targetThrow(target)
        } catch (failure: Throwable) {
            SynchronousCallResult.InfrastructureFailure(failure)
        }
    }

    private fun targetThrow(throwable: Throwable): SynchronousCallResult.TargetThrew =
        SynchronousCallResult.TargetThrew(
            HostThrowable(virtualTypeOfHostClass(throwable.javaClass), throwable),
        )
}

private fun packageName(type: VirtualType): String =
    type.descriptor.substringBeforeLast('/', missingDelimiterValue = "")

private fun Any?.toHostValue(declaredType: VirtualType): HostValue = when {
    declaredType.isPrimitive -> HostValue.Primitive(requireNotNull(this), declaredType)
    this == null -> HostValue.TypedNull(declaredType)
    else -> HostValue.Reference(this, virtualTypeOfHostClass(javaClass))
}
