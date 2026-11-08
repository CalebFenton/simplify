package org.cf.smalivm.core

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/** Resolves Class field lookup into scheduler allocation of one tracked field identity. */
internal class FieldLookupEmulator(
    private val classIndex: ClassIndex,
    private val reflectionPolicy: ReflectionPolicy,
) : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val classToken = (call.receiver as? EmulatorValue.Known)?.javaValue as? Class<*>
            ?: return returnUnknown(call.returnType)
        val nameValue = call.arguments.singleOrNull() as? EmulatorValue.Known
            ?: return returnUnknown(call.returnType)
        val name = nameValue.javaValue as? String ?: return targetThrew(NullPointerException())
        val declaredOnly = call.target.name == "getDeclaredField"
        val requestedType = virtualTypeOfHostClass(classToken)

        val tracked = try {
            val indexedToken = classIndex.loadClassConstant(requestedType.descriptor)
            if (
                indexedToken === classToken &&
                classIndex.isLoadable(requestedType) &&
                !classIndex.isFrameworkClass(requestedType)
            ) {
                virtualField(requestedType, name, declaredOnly)
            } else {
                hostField(classToken, name, declaredOnly)
            }
        } catch (target: SecurityException) {
            return targetThrew(target)
        } ?: return targetThrew(NoSuchFieldException(name))

        if (
            tracked.domain is TrackedFieldDomain.Host &&
            !hostFieldReadIsAuthorized(tracked, classIndex, reflectionPolicy)
        ) {
            return returnUnknown(call.returnType)
        }
        return IntrinsicCallResult(IntrinsicOperation.AllocateTrackedField(tracked))
    }

    private fun virtualField(
        requestedType: VirtualType,
        name: String,
        declaredOnly: Boolean,
    ): TrackedFieldValue? {
        if (declaredOnly) {
            return classIndex.declaredFields(requestedType)
                .firstOrNull { it.name == name }
                ?.toTrackedField()
        }
        return publicVirtualField(requestedType, name, mutableSetOf())
    }

    private fun publicVirtualField(
        type: VirtualType,
        name: String,
        visited: MutableSet<VirtualType>,
    ): TrackedFieldValue? {
        if (!visited.add(type)) return null
        classIndex.declaredFields(type)
            .firstOrNull { it.name == name && Modifier.isPublic(it.modifiers) }
            ?.let { return it.toTrackedField() }
        for (interfaceType in classIndex.directInterfaces(type)) {
            publicVirtualField(interfaceType, name, visited)?.let { return it }
        }
        if (classIndex.isInterface(type)) return null
        val superclass = classIndex.superclassOf(type) ?: return null
        return publicVirtualField(superclass, name, visited)
    }

    private fun hostField(classToken: Class<*>, name: String, declaredOnly: Boolean): TrackedFieldValue? {
        val field = try {
            if (declaredOnly) classToken.getDeclaredField(name) else classToken.getField(name)
        } catch (_: NoSuchFieldException) {
            return null
        }
        return field.toTrackedField()
    }
}

private fun FieldDeclaration.toTrackedField(): TrackedFieldValue = TrackedFieldValue(
    declaringType,
    name,
    descriptor,
    type,
    modifiers,
    visibilityOf(modifiers),
    if (Modifier.isStatic(modifiers)) FieldStorage.STATIC else FieldStorage.INSTANCE,
    TrackedFieldDomain.Virtual,
)

private fun Field.toTrackedField(): TrackedFieldValue {
    val declaringType = virtualTypeOfHostClass(declaringClass)
    val fieldType = virtualTypeOfHostClass(type)
    return TrackedFieldValue(
        declaringType,
        name,
        ClassIndex.fieldDescriptorOf(declaringType, name, fieldType.descriptor),
        fieldType,
        modifiers,
        visibilityOf(modifiers),
        if (Modifier.isStatic(modifiers)) FieldStorage.STATIC else FieldStorage.INSTANCE,
        TrackedFieldDomain.Host(declaringClass),
    )
}

internal fun visibilityOf(modifiers: Int): FieldVisibility = when {
    Modifier.isPublic(modifiers) -> FieldVisibility.PUBLIC
    Modifier.isProtected(modifiers) -> FieldVisibility.PROTECTED
    Modifier.isPrivate(modifiers) -> FieldVisibility.PRIVATE
    else -> FieldVisibility.PACKAGE
}
