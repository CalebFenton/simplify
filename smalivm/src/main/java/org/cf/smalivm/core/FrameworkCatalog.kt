package org.cf.smalivm.core

import org.cf.smalivm.dex.SmaliParser
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder

/** Immutable bundled-framework meaning with catalog-owned lazy materialization. */
class FrameworkCatalog private constructor(
    private val catalogIdentity: Any,
    internal val artifact: FrameworkArtifact,
) {
    private sealed interface MaterializedClass {
        data class Loaded(val definition: ImmutableClassDef) : MaterializedClass
        data class Failed(val failure: RuntimeException) : MaterializedClass
    }

    private val materializedClasses = HashMap<String, MaterializedClass>()
    private val materializedMethods = HashMap<String, FrameworkMethodDefinition?>()

    val classCount: Int get() = artifact.classes.size

    val dynamicLoadingEvidence: DynamicLoadingEvidence = DynamicLoadingEvidence.ofAll(
        artifact.classes.values.flatMap(FrameworkClassArtifact::dynamicLoadingMethods),
    )

    fun method(methodSignature: String): FrameworkMethodDefinition? {
        if (materializedMethods.containsKey(methodSignature)) return materializedMethods[methodSignature]
        val method = declaredMethod(methodSignature.substringBefore("->"), methodSignature.substringAfter("->"))
        materializedMethods[methodSignature] = method
        return method
    }

    internal fun declaredMethod(definingClass: String, descriptor: String): FrameworkMethodDefinition? {
        val artifactEntry = artifact.classes[definingClass] ?: return null
        if (descriptor !in artifactEntry.methodDescriptors) return null
        val signature = "$definingClass->$descriptor"
        if (materializedMethods.containsKey(signature)) return materializedMethods[signature]
        val method = classDefinition(definingClass)?.methods
            ?.firstOrNull { methodDescriptor(it) == descriptor }
            ?.let { buildMethod(catalogIdentity, it) }
        materializedMethods[signature] = method
        return method
    }

    /** Immutable framework source for consumer disassembly; materialized once per catalog. */
    fun classDefinition(type: String): ImmutableClassDef? {
        when (val cached = materializedClasses[type]) {
            is MaterializedClass.Loaded -> return cached.definition
            is MaterializedClass.Failed -> throw cached.failure
            null -> Unit
        }
        val source = artifact.openClassSource(type) ?: return null
        val materialized = try {
            val builder = DexBuilder(Opcodes.forApi(artifact.apiLevel))
            val definition = source.inputStream().use { input ->
                ImmutableClassDef.of(SmaliParser.parse("$type.smali", input, builder, artifact.apiLevel))
            }
            MaterializedClass.Loaded(definition)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            MaterializedClass.Failed(RuntimeException("Failed to materialize framework class $type", failure))
        }
        materializedClasses[type] = materialized
        return when (materialized) {
            is MaterializedClass.Loaded -> materialized.definition
            is MaterializedClass.Failed -> throw materialized.failure
        }
    }

    internal companion object {
        fun fromArtifact(artifact: FrameworkArtifact): FrameworkCatalog = FrameworkCatalog(Any(), artifact)

        private fun methodDescriptor(method: com.android.tools.smali.dexlib2.iface.Method): String =
            method.name + method.parameterTypes.joinToString(separator = "", prefix = "(", postfix = ")") +
                method.returnType

        private fun isUnsupportedFrameworkStub(implementation: com.android.tools.smali.dexlib2.iface.MethodImplementation): Boolean =
            implementation.instructions.any { instruction ->
                val value = ((instruction as? ReferenceInstruction)?.reference as? StringReference)?.string
                value == "Use StringFactory instead." || value == "Stub!"
            }

        private fun buildMethod(
            catalogIdentity: Any,
            method: ImmutableMethod,
        ): FrameworkMethodDefinition {
            val signature = com.android.tools.smali.dexlib2.formatter.DexFormatter.INSTANCE.getMethodDescriptor(method)
            val body = method.implementation
                ?.takeUnless(::isUnsupportedFrameworkStub)
                ?.let(MethodBody::import)
                ?.let(MethodLayoutSolver::solve)
                ?.let(MethodBodyAssembler::assemble)
            val declaration = ImmutableMethod(
                method.definingClass,
                method.name,
                method.parameters,
                method.returnType,
                method.accessFlags,
                method.annotations,
                method.hiddenApiRestrictions,
                null,
            )
            return FrameworkMethodDefinition(
                FrameworkMethodIdentity(catalogIdentity, signature),
                declaration,
                body,
                body?.let { DynamicLoadingEvidence.fromBody(signature, it) } ?: DynamicLoadingEvidence.EMPTY,
            )
        }
    }
}
