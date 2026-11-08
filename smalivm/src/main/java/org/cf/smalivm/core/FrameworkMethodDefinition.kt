package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.immutable.ImmutableMethod

/** Immutable bundled-framework method declaration and optional modeled body. */
internal class FrameworkMethodDefinition internal constructor(
    internal val identity: FrameworkMethodIdentity,
    internal val declaration: ImmutableMethod,
    internal val body: MethodBody?,
    internal val dynamicLoadingEvidence: DynamicLoadingEvidence,
) {
    internal val methodSignature: MethodSignature = MethodSignature.of(declaration)
}
