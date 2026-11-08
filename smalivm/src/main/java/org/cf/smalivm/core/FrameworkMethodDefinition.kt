package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.immutable.ImmutableMethod

/** Immutable bundled-framework method declaration and optional modeled body. */
class FrameworkMethodDefinition internal constructor(
    val identity: FrameworkMethodIdentity,
    internal val declaration: ImmutableMethod,
    val body: MethodBody?,
    val dynamicLoadingEvidence: DynamicLoadingEvidence,
) {
    val methodSignature: MethodSignature = MethodSignature.of(declaration)
}
