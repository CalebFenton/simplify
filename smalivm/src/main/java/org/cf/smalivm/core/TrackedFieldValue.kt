package org.cf.smalivm.core

import java.lang.reflect.Modifier

internal enum class FieldVisibility(val modifier: Int) {
    PUBLIC(Modifier.PUBLIC),
    PROTECTED(Modifier.PROTECTED),
    PACKAGE(0),
    PRIVATE(Modifier.PRIVATE),
}

internal enum class FieldStorage { STATIC, INSTANCE }

internal sealed interface TrackedFieldDomain {
    data object Virtual : TrackedFieldDomain

    /** The exact host declaration identity. [declaringClass] retains its defining class loader. */
    data class Host(val declaringClass: Class<*>) : TrackedFieldDomain
}

internal enum class FieldAccessibility {
    LANGUAGE_CHECKS,
    OVERRIDDEN,
    UNKNOWN,
}

internal data class TrackedFieldCompatibility(
    val metadata: TrackedFieldValue,
    val accessibility: FieldAccessibility,
)

/** Immutable field metadata. Scheduler-owned accessibility and identity live in [HeapObject.TrackedField]. */
internal data class TrackedFieldValue(
    val declaringType: VirtualType,
    val name: String,
    val descriptor: String,
    val fieldType: VirtualType,
    val modifiers: Int,
    val visibility: FieldVisibility,
    val storage: FieldStorage,
    val domain: TrackedFieldDomain,
) {
    init {
        require(declaringType.isReference && !declaringType.isArray) {
            "A field declaring type must be a non-array reference: $declaringType"
        }
        require(name.isNotEmpty() && ':' !in name && "->" !in name) { "Invalid field name: $name" }
        require(!fieldType.isVoid) { "A field cannot have void type" }
        require(descriptor == ClassIndex.fieldDescriptorOf(declaringType, name, fieldType.descriptor)) {
            "Field descriptor does not match its declaring type, name, and type: $descriptor"
        }
        val visibilityBits = modifiers and VISIBILITY_MASK
        require(visibilityBits == visibility.modifier) {
            "Field visibility $visibility contradicts modifiers ${Modifier.toString(modifiers)}"
        }
        require(Modifier.isStatic(modifiers) == (storage == FieldStorage.STATIC)) {
            "Field storage $storage contradicts modifiers ${Modifier.toString(modifiers)}"
        }
        if (domain is TrackedFieldDomain.Host) {
            require(virtualTypeOfHostClass(domain.declaringClass) == declaringType) {
                "Host declaring class ${domain.declaringClass.name} does not match $declaringType"
            }
        }
    }

    private companion object {
        const val VISIBILITY_MASK = Modifier.PUBLIC or Modifier.PROTECTED or Modifier.PRIVATE
    }
}

/** JVM initialization dependencies for one static field owner, without initializing any class. */
internal fun hostFieldInitializationChain(declaringClass: Class<*>): List<VirtualType> {
    val chain = linkedSetOf<Class<*>>()

    fun addDefaultInterfaces(owner: Class<*>) {
        for (interfaceClass in owner.interfaces) {
            addDefaultInterfaces(interfaceClass)
            if (interfaceClass.declaredMethods.any { it.isDefault }) chain += interfaceClass
        }
    }

    fun addClass(owner: Class<*>) {
        owner.superclass?.let(::addClass)
        addDefaultInterfaces(owner)
        chain += owner
    }

    addClass(declaringClass)
    return chain.map(::virtualTypeOfHostClass)
}

internal fun hostDeclaringClassMatchesVm(field: TrackedFieldValue, classIndex: ClassIndex): Boolean {
    val domain = field.domain as? TrackedFieldDomain.Host ?: return false
    return try {
        classIndex.loadClassConstant(field.declaringType.descriptor) === domain.declaringClass
    } catch (fatal: VirtualMachineError) {
        throw fatal
    } catch (_: Throwable) {
        false
    }
}
