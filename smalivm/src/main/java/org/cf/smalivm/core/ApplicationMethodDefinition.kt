package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.formatter.DexFormatter
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod

/** Immutable application method declaration and optional assembled body. */
internal class ApplicationMethodDefinition private constructor(
    internal val declaration: ImmutableMethod,
    val body: MethodBody?,
    val methodRevision: ApplicationMethodRevision,
    val dynamicLoadingEvidence: DynamicLoadingEvidence,
    internal val dexReferenceClosure: DexReferenceClosure,
) {
    val methodSignature: String get() = DexFormatter.INSTANCE.getMethodDescriptor(declaration)

    init {
        require(methodRevision.identifies(methodSignature)) {
            "Application method revision belongs to another method: $methodSignature"
        }
        val bodyForbidden = java.lang.reflect.Modifier.isAbstract(declaration.accessFlags) ||
            java.lang.reflect.Modifier.isNative(declaration.accessFlags)
        require(!bodyForbidden || body == null) { "Native or abstract method has a body: $methodSignature" }
        require(bodyForbidden || body != null) { "Concrete method has no body: $methodSignature" }
    }

    internal companion object {
        fun withStringEncoding(
            method: ApplicationMethodDefinition,
            encoding: StringInstructionEncoding,
        ): ApplicationMethodDefinition {
            val body = method.body
                ?.let { MethodLayoutSolver.solve(it, encoding) }
                ?.let(MethodBodyAssembler::assemble)
            return ApplicationMethodDefinition(
                method.declaration,
                body,
                method.methodRevision,
                method.dynamicLoadingEvidence,
                DexReferenceClosure.fromMethod(method.declaration, body),
            )
        }

        fun import(
            method: Method,
            methodRevision: ApplicationMethodRevision,
        ): ApplicationMethodDefinition {
            val immutable = ImmutableMethod.of(method)
            val body = method.implementation?.let(MethodBody::import)?.let(MethodLayoutSolver::solve)
                ?.let(MethodBodyAssembler::assemble)
            val declaration = ImmutableMethod(
                immutable.definingClass,
                immutable.name,
                immutable.parameters,
                immutable.returnType,
                immutable.accessFlags,
                immutable.annotations,
                immutable.hiddenApiRestrictions,
                null,
            )
            val footprint = DexReferenceClosure.fromMethod(declaration, body)
            val evidence = body?.let { DynamicLoadingEvidence.fromBody(methodSignatureOf(immutable), it) }
                ?: DynamicLoadingEvidence.EMPTY
            return ApplicationMethodDefinition(declaration, body, methodRevision, evidence, footprint)
        }

        fun withBody(
            method: ApplicationMethodDefinition,
            body: MethodBody,
            methodRevision: ApplicationMethodRevision,
        ): ApplicationMethodDefinition = ApplicationMethodDefinition(
            method.declaration,
            body,
            methodRevision,
            DynamicLoadingEvidence.fromBody(method.methodSignature, body),
            DexReferenceClosure.fromMethod(method.declaration, body),
        )

        fun withHierarchyEvidence(
            method: ApplicationMethodDefinition,
            hierarchy: HierarchySnapshot,
        ): ApplicationMethodDefinition = ApplicationMethodDefinition(
            method.declaration,
            method.body,
            method.methodRevision,
            method.body?.let { DynamicLoadingEvidence.fromBody(method.methodSignature, it, hierarchy) }
                ?: DynamicLoadingEvidence.EMPTY,
            method.dexReferenceClosure,
        )

        fun fromCandidate(
            candidate: ApplicationMethodDefinitionCandidate,
            methodRevision: ApplicationMethodRevision,
        ): ApplicationMethodDefinition = ApplicationMethodDefinition(
            candidate.declaration,
            candidate.body,
            methodRevision,
            candidate.dynamicLoadingEvidence,
            candidate.dexReferenceClosure,
        )

        private fun methodSignatureOf(method: Method): String =
            DexFormatter.INSTANCE.getMethodDescriptor(method)
    }
}
