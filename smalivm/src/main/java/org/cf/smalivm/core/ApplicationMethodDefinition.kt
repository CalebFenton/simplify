package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.formatter.DexFormatter
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation

/** Code ART's method verifier rejects. It is written to output unchanged and never executes. */
internal class RejectedMethodCode(val rejection: VerifierRejection, val implementation: ImmutableMethodImplementation)

/** Immutable application method declaration and its code: an assembled body, or code the verifier rejects. */
internal class ApplicationMethodDefinition private constructor(
    internal val declaration: ImmutableMethod,
    val body: MethodBody?,
    val rejectedCode: RejectedMethodCode?,
    val methodRevision: ApplicationMethodRevision,
    val dynamicLoadingEvidence: DynamicLoadingEvidence,
    internal val dexReferenceClosure: DexReferenceClosure,
) {
    val methodSignature: String get() = DexFormatter.INSTANCE.getMethodDescriptor(declaration)

    init {
        require(methodRevision.identifies(methodSignature)) {
            "Application method revision belongs to another method: $methodSignature"
        }
        val codeForbidden = java.lang.reflect.Modifier.isAbstract(declaration.accessFlags) ||
            java.lang.reflect.Modifier.isNative(declaration.accessFlags)
        require(!codeForbidden || body == null && rejectedCode == null) {
            "Native or abstract method has code: $methodSignature"
        }
        require(codeForbidden || (body == null) != (rejectedCode == null)) {
            "Concrete method needs exactly one of a body and rejected code: $methodSignature"
        }
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
                method.rejectedCode,
                method.methodRevision,
                method.dynamicLoadingEvidence,
                body?.let { DexReferenceClosure.fromMethod(method.declaration, it) } ?: method.dexReferenceClosure,
            )
        }

        /**
         * Code that fails [MethodBody.verifierRejection] becomes [RejectedMethodCode]; the importer then keeps
         * its class out of the loaded set.
         */
        fun import(
            method: Method,
            methodRevision: ApplicationMethodRevision,
        ): ApplicationMethodDefinition {
            val immutable = ImmutableMethod.of(method)
            val implementation = immutable.implementation
            val rejection = implementation?.let(MethodBody::verifierRejection)
            val rejectedCode = rejection?.let { RejectedMethodCode(it, requireNotNull(implementation)) }
            val body = implementation?.takeIf { rejection == null }
                ?.let(MethodBody::import)
                ?.let(MethodLayoutSolver::solve)
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
            val footprint = rejectedCode?.let { DexReferenceClosure.fromRejectedMethod(declaration, it.implementation) }
                ?: DexReferenceClosure.fromMethod(declaration, body)
            val evidence = body?.let { DynamicLoadingEvidence.fromBody(methodSignatureOf(immutable), it) }
                ?: DynamicLoadingEvidence.EMPTY
            return ApplicationMethodDefinition(declaration, body, rejectedCode, methodRevision, evidence, footprint)
        }

        fun withBody(
            method: ApplicationMethodDefinition,
            body: MethodBody,
            methodRevision: ApplicationMethodRevision,
        ): ApplicationMethodDefinition = ApplicationMethodDefinition(
            method.declaration,
            body,
            null,
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
            method.rejectedCode,
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
            null,
            methodRevision,
            candidate.dynamicLoadingEvidence,
            candidate.dexReferenceClosure,
        )

        private fun methodSignatureOf(method: Method): String =
            DexFormatter.INSTANCE.getMethodDescriptor(method)
    }
}
