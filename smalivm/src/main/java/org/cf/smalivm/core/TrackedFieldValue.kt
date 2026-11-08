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

/** Whether the VM's class loader resolves [field]'s declaring type to the exact host class the field came from. */
internal fun hostDeclaringClassMatchesVm(field: TrackedFieldValue, resolver: ProgramSnapshotResolver): Boolean {
    val domain = field.domain as? TrackedFieldDomain.Host ?: return false
    return hostBoundary({ false }) {
        resolver.loadClassConstant(field.declaringType.descriptor) === domain.declaringClass
    }
}
