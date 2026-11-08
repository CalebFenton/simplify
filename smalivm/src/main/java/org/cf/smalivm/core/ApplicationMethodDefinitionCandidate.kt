package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.immutable.ImmutableMethod

/** Complete edited method content before revision allocation and publication. */
internal class ApplicationMethodDefinitionCandidate private constructor(
    val declaration: ImmutableMethod,
    val body: MethodBody,
    val dynamicLoadingEvidence: DynamicLoadingEvidence,
    val dexReferenceClosure: DexReferenceClosure,
    val baselineRegisterCount: Int,
) {
    val methodSignature: MethodSignature = MethodSignature.of(declaration)

    companion object {
        fun from(
            baseline: ApplicationMethodDefinition,
            body: MethodBody,
            hierarchy: HierarchySnapshot,
        ): ApplicationMethodDefinitionCandidate = ApplicationMethodDefinitionCandidate(
            baseline.declaration,
            body,
            DynamicLoadingEvidence.fromBody(baseline.methodSignature, body, hierarchy),
            DexReferenceClosure.fromMethod(baseline.declaration, body),
            requireNotNull(baseline.body).registerCount,
        )

        fun fromDefinition(definition: ApplicationMethodDefinition): ApplicationMethodDefinitionCandidate {
            val body = requireNotNull(definition.body) { "Bodyless definitions have no editable candidate" }
            return ApplicationMethodDefinitionCandidate(
                definition.declaration,
                body,
                definition.dynamicLoadingEvidence,
                definition.dexReferenceClosure,
                body.registerCount,
            )
        }
    }
}
