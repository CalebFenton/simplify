package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet
import com.android.tools.smali.dexlib2.immutable.ImmutableAnnotation

/** Complete immutable declaration of one application class. */
class ApplicationClassDefinition internal constructor(
    val type: String,
    val accessFlags: Int,
    val superclass: String?,
    interfaces: List<String>,
    val sourceFile: String?,
    annotations: Set<ImmutableAnnotation>,
    staticFields: List<ApplicationFieldDefinition>,
    instanceFields: List<ApplicationFieldDefinition>,
    methods: List<ApplicationMethodDefinition>,
) {
    init {
        require((staticFields + instanceFields).all { it.declaration.definingClass == type }) {
            "Application field owner does not match $type"
        }
        require(staticFields.all { java.lang.reflect.Modifier.isStatic(it.declaration.accessFlags) }) {
            "Application static field list contains an instance field"
        }
        require(instanceFields.none { java.lang.reflect.Modifier.isStatic(it.declaration.accessFlags) }) {
            "Application instance field list contains a static field"
        }
        require((staticFields + instanceFields).map { fieldDescriptor(it) }.distinct().size ==
            staticFields.size + instanceFields.size) {
            "Application class contains duplicate field descriptors: $type"
        }
        require(methods.all { it.declaration.definingClass == type }) {
            "Application method owner does not match $type"
        }
        require(methods.map(::methodDescriptor).distinct().size == methods.size) {
            "Application class contains duplicate method descriptors: $type"
        }
    }

    val interfaces: PersistentList<String> = interfaces.toPersistentList()
    val annotations: PersistentSet<ImmutableAnnotation> = annotations.toPersistentSet()
    val staticFields: PersistentList<ApplicationFieldDefinition> = staticFields.toPersistentList()
    val instanceFields: PersistentList<ApplicationFieldDefinition> = instanceFields.toPersistentList()
    internal val methods: PersistentList<ApplicationMethodDefinition> = methods.toPersistentList()
    internal val methodsByDescriptor: PersistentMap<String, ApplicationMethodDefinition> =
        methods.associateBy(::methodDescriptor).toPersistentMap()

    internal fun withStringEncoding(encoding: StringInstructionEncoding): ApplicationClassDefinition =
        ApplicationClassDefinition(
            type,
            accessFlags,
            superclass,
            interfaces,
            sourceFile,
            annotations,
            staticFields,
            instanceFields,
            methods.map { ApplicationMethodDefinition.withStringEncoding(it, encoding) },
        )

    internal fun withMethod(replacement: ApplicationMethodDefinition): ApplicationClassDefinition {
        return withMethods(mapOf(replacement.methodSignature to replacement))
    }

    internal fun withMethods(
        replacementsBySignature: Map<String, ApplicationMethodDefinition>,
    ): ApplicationClassDefinition {
        require(replacementsBySignature.isNotEmpty()) { "At least one replacement method is required" }
        replacementsBySignature.values.forEach { replacement ->
            require(replacement.declaration.definingClass == type) { "Replacement method belongs to another class" }
            require(methodsByDescriptor.containsKey(methodDescriptor(replacement))) {
                "Replacement method is not declared by $type"
            }
        }
        require(replacementsBySignature.all { (signature, replacement) -> signature == replacement.methodSignature }) {
            "Replacement method map key does not match its method signature"
        }
        return ApplicationClassDefinition(
            type,
            accessFlags,
            superclass,
            interfaces,
            sourceFile,
            annotations,
            staticFields,
            instanceFields,
            methods.map { method ->
                replacementsBySignature[method.methodSignature] ?: method
            },
        )
    }

    internal fun hasSameStructureAs(other: ApplicationClassDefinition): Boolean =
        type == other.type &&
            accessFlags == other.accessFlags &&
            superclass == other.superclass &&
            interfaces == other.interfaces &&
            sourceFile == other.sourceFile &&
            annotations == other.annotations &&
            fieldsMatch(staticFields, other.staticFields) &&
            fieldsMatch(instanceFields, other.instanceFields) &&
            methods.size == other.methods.size &&
            methods.zip(other.methods).all { (left, right) -> declarationsMatch(left, right) }

    private fun methodDescriptor(method: ApplicationMethodDefinition): String =
        method.declaration.name +
            method.declaration.parameterTypes.joinToString(separator = "", prefix = "(", postfix = ")") +
            method.declaration.returnType

    private fun fieldDescriptor(field: ApplicationFieldDefinition): String =
        "${field.declaration.name}:${field.declaration.type}"

    private fun fieldsMatch(
        left: List<ApplicationFieldDefinition>,
        right: List<ApplicationFieldDefinition>,
    ): Boolean = left.size == right.size && left.zip(right).all { (first, second) ->
        val a = first.declaration
        val b = second.declaration
        a.definingClass == b.definingClass &&
            a.name == b.name &&
            a.type == b.type &&
            a.accessFlags == b.accessFlags &&
            a.initialValue == b.initialValue &&
            a.annotations == b.annotations &&
            a.hiddenApiRestrictions == b.hiddenApiRestrictions
    }

    private fun declarationsMatch(
        left: ApplicationMethodDefinition,
        right: ApplicationMethodDefinition,
    ): Boolean {
        val a = left.declaration
        val b = right.declaration
        return a.definingClass == b.definingClass &&
            a.name == b.name &&
            a.parameters == b.parameters &&
            a.returnType == b.returnType &&
            a.accessFlags == b.accessFlags &&
            a.annotations == b.annotations &&
            a.hiddenApiRestrictions == b.hiddenApiRestrictions
    }
}
