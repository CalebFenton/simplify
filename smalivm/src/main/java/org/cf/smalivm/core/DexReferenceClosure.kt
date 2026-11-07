package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import com.android.tools.smali.dexlib2.iface.Annotation
import com.android.tools.smali.dexlib2.iface.AnnotationElement
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.debug.SetSourceFile
import com.android.tools.smali.dexlib2.iface.debug.StartLocal
import com.android.tools.smali.dexlib2.iface.debug.EndLocal
import com.android.tools.smali.dexlib2.iface.debug.RestartLocal
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.CallSiteReference
import com.android.tools.smali.dexlib2.iface.reference.MethodHandleReference
import com.android.tools.smali.dexlib2.iface.reference.MethodProtoReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.iface.value.AnnotationEncodedValue
import com.android.tools.smali.dexlib2.iface.value.ArrayEncodedValue
import com.android.tools.smali.dexlib2.iface.value.EncodedValue
import com.android.tools.smali.dexlib2.iface.value.EnumEncodedValue
import com.android.tools.smali.dexlib2.iface.value.FieldEncodedValue
import com.android.tools.smali.dexlib2.iface.value.MethodEncodedValue
import com.android.tools.smali.dexlib2.iface.value.MethodHandleEncodedValue
import com.android.tools.smali.dexlib2.iface.value.MethodTypeEncodedValue
import com.android.tools.smali.dexlib2.iface.value.StringEncodedValue
import com.android.tools.smali.dexlib2.iface.value.TypeEncodedValue
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableCallSiteReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodHandleReference

internal data class DexFieldReference(
    val definingClass: String,
    val name: String,
    val type: String,
)

internal class DexMethodPrototype private constructor(
    val parameterTypes: PersistentList<String>,
    val returnType: String,
) {
    internal companion object {
        fun of(parameterTypes: Iterable<CharSequence>, returnType: String): DexMethodPrototype =
            DexMethodPrototype(parameterTypes.map(CharSequence::toString).toPersistentList(), returnType)
    }

    override fun equals(other: Any?): Boolean =
        other is DexMethodPrototype && parameterTypes == other.parameterTypes && returnType == other.returnType

    override fun hashCode(): Int = 31 * parameterTypes.hashCode() + returnType.hashCode()
}

internal data class DexMethodReference(
    val definingClass: String,
    val name: String,
    val prototype: DexMethodPrototype,
)

/** Complete transitive DEX reference closure stored in one method or program snapshot. */
internal class DexReferenceClosure private constructor(
    val strings: PersistentSet<String>,
    val types: PersistentSet<String>,
    val fields: PersistentSet<DexFieldReference>,
    val methods: PersistentSet<DexMethodReference>,
    val prototypes: PersistentSet<DexMethodPrototype>,
    val methodHandles: PersistentSet<ImmutableMethodHandleReference>,
    val callSites: PersistentSet<ImmutableCallSiteReference>,
) {
    internal fun plus(other: DexReferenceClosure): DexReferenceClosure = DexReferenceClosure(
        strings.addingAll(other.strings),
        types.addingAll(other.types),
        fields.addingAll(other.fields),
        methods.addingAll(other.methods),
        prototypes.addingAll(other.prototypes),
        methodHandles.addingAll(other.methodHandles),
        callSites.addingAll(other.callSites),
    )

    internal fun containsAll(other: DexReferenceClosure): Boolean =
        strings.containsAll(other.strings) &&
            types.containsAll(other.types) &&
            fields.containsAll(other.fields) &&
            methods.containsAll(other.methods) &&
            prototypes.containsAll(other.prototypes) &&
            methodHandles.containsAll(other.methodHandles) &&
            callSites.containsAll(other.callSites)

    internal companion object {
        val EMPTY = DexReferenceClosure(
            persistentSetOf(),
            persistentSetOf(),
            persistentSetOf(),
            persistentSetOf(),
            persistentSetOf(),
            persistentSetOf(),
            persistentSetOf(),
        )

        fun fromMethod(method: Method, body: MethodBody?): DexReferenceClosure {
            val collector = Collector()
            collector.method(method)
            body?.let(collector::body)
            return collector.build()
        }

        internal fun fromReferences(references: Iterable<Reference>): DexReferenceClosure {
            val collector = Collector()
            references.forEach(collector::reference)
            return collector.build()
        }

        internal fun fromStagedReferences(references: Iterable<StagedDexReference>): DexReferenceClosure =
            fromReferences(references.map(StagedDexReference::asReference))

        internal fun fromApplicationClass(definition: ApplicationClassDefinition): DexReferenceClosure {
            return fromApplicationClass(definition, null, null)
        }

        internal fun projectedProgramClosure(
            definitions: Collection<ApplicationClassDefinition>,
            replacedMethodSignature: String,
            replacement: DexReferenceClosure,
        ): DexReferenceClosure = definitions.fold(EMPTY) { result, definition ->
            result.plus(fromApplicationClass(definition, replacedMethodSignature, replacement))
        }

        private fun fromApplicationClass(
            definition: ApplicationClassDefinition,
            replacedMethodSignature: String?,
            replacement: DexReferenceClosure?,
        ): DexReferenceClosure {
            val collector = Collector()
            collector.type(definition.type)
            definition.superclass?.let(collector::type)
            definition.interfaces.forEach(collector::type)
            definition.sourceFile?.let(collector::string)
            definition.annotations.forEach(collector::annotation)
            (definition.staticFields + definition.instanceFields).forEach { field ->
                collector.field(field.declaration)
                field.declaration.initialValue?.let(collector::encodedValue)
                field.declaration.annotations.forEach(collector::annotation)
            }
            definition.methods.forEach { method ->
                collector.closure(
                    if (method.methodSignature == replacedMethodSignature) {
                        requireNotNull(replacement)
                    } else {
                        method.dexReferenceClosure
                    },
                )
            }
            return collector.build()
        }
    }

    private class Collector {
        private val strings = persistentSetOf<String>().builder()
        private val types = persistentSetOf<String>().builder()
        private val fields = persistentSetOf<DexFieldReference>().builder()
        private val methods = persistentSetOf<DexMethodReference>().builder()
        private val prototypes = persistentSetOf<DexMethodPrototype>().builder()
        private val methodHandles = persistentSetOf<ImmutableMethodHandleReference>().builder()
        private val callSites = persistentSetOf<ImmutableCallSiteReference>().builder()

        fun string(value: String) {
            strings.add(value)
        }

        fun type(value: String) {
            types.add(value)
            strings.add(value)
        }

        fun field(field: FieldReference) {
            type(field.definingClass)
            string(field.name)
            type(field.type)
            fields.add(DexFieldReference(field.definingClass, field.name, field.type))
        }

        fun method(method: MethodReference) {
            type(method.definingClass)
            string(method.name)
            val prototype = DexMethodPrototype.of(method.parameterTypes, method.returnType)
            prototype.parameterTypes.forEach(::type)
            type(prototype.returnType)
            string(shortyOf(prototype))
            prototypes.add(prototype)
            methods.add(DexMethodReference(method.definingClass, method.name, prototype))
            if (method is Method) {
                method.parameters.forEach { parameter ->
                    parameter.name?.let(::string)
                    parameter.annotations.forEach(::annotation)
                }
                method.annotations.forEach(::annotation)
            }
        }

        fun body(body: MethodBody) {
            body.instructions.forEach { record ->
                val operands = body.referenceOperandsByPosition[record.position]
                if (operands == null) {
                    val instruction = record.instruction
                    if (instruction is ReferenceInstruction) reference(instruction.reference)
                    if (instruction is DualReferenceInstruction) reference(instruction.reference2)
                } else {
                    operands.forEach { operand -> reference(operand.asReference()) }
                }
            }
            body.tryRegions.forEach { region ->
                region.handlers.forEach { handler -> handler.exceptionType?.let(::type) }
            }
            body.debugItems.forEach { imported ->
                when (val item = imported.item) {
                    is StartLocal -> {
                        item.name?.let(::string)
                        item.type?.let(::type)
                        item.signature?.let(::string)
                    }
                    is EndLocal -> {
                        item.name?.let(::string)
                        item.type?.let(::type)
                        item.signature?.let(::string)
                    }
                    is RestartLocal -> {
                        item.name?.let(::string)
                        item.type?.let(::type)
                        item.signature?.let(::string)
                    }
                    is SetSourceFile -> item.sourceFile?.let(::string)
                }
            }
        }

        fun closure(closure: DexReferenceClosure) {
            strings.addAll(closure.strings)
            types.addAll(closure.types)
            fields.addAll(closure.fields)
            methods.addAll(closure.methods)
            prototypes.addAll(closure.prototypes)
            methodHandles.addAll(closure.methodHandles)
            callSites.addAll(closure.callSites)
        }

        fun annotation(annotation: Annotation) {
            type(annotation.type)
            annotation.elements.forEach(::annotationElement)
        }

        private fun annotationElement(element: AnnotationElement) {
            string(element.name)
            encodedValue(element.value)
        }

        fun encodedValue(value: EncodedValue) {
            when (value) {
                is StringEncodedValue -> string(value.value)
                is TypeEncodedValue -> type(value.value)
                is FieldEncodedValue -> field(value.value)
                is EnumEncodedValue -> field(value.value)
                is MethodEncodedValue -> method(value.value)
                is MethodTypeEncodedValue -> prototype(value.value)
                is MethodHandleEncodedValue -> methodHandle(value.value)
                is ArrayEncodedValue -> value.value.forEach(::encodedValue)
                is AnnotationEncodedValue -> {
                    type(value.type)
                    value.elements.forEach(::annotationElement)
                }
            }
        }

        private fun prototype(reference: MethodProtoReference) {
            val prototype = DexMethodPrototype.of(reference.parameterTypes, reference.returnType)
            prototype.parameterTypes.forEach(::type)
            type(prototype.returnType)
            string(shortyOf(prototype))
            prototypes.add(prototype)
        }

        private fun methodHandle(reference: MethodHandleReference) {
            reference(reference.memberReference)
            methodHandles.add(ImmutableMethodHandleReference.of(reference))
        }

        private fun callSite(reference: CallSiteReference) {
            string(reference.methodName)
            methodHandle(reference.methodHandle)
            prototype(reference.methodProto)
            reference.extraArguments.forEach(::encodedValue)
            callSites.add(ImmutableCallSiteReference.of(reference))
        }

        fun reference(reference: Reference) {
            when (reference) {
                is StringReference -> string(reference.string)
                is TypeReference -> type(reference.type)
                is FieldReference -> field(reference)
                is MethodReference -> method(reference)
                is MethodProtoReference -> prototype(reference)
                is MethodHandleReference -> methodHandle(reference)
                is CallSiteReference -> callSite(reference)
            }
        }

        fun build(): DexReferenceClosure = DexReferenceClosure(
            strings.build(), types.build(), fields.build(), methods.build(), prototypes.build(),
            methodHandles.build(), callSites.build(),
        )

        private fun shortyOf(prototype: DexMethodPrototype): String = buildString {
            append(shortyType(prototype.returnType))
            prototype.parameterTypes.forEach { append(shortyType(it)) }
        }

        private fun shortyType(type: String): Char = if (type.startsWith('L') || type.startsWith('[')) 'L' else type[0]
    }
}
