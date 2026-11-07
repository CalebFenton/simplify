package org.cf.smalivm.core

/** Stable identity of one method in a frozen framework catalog. */
class FrameworkMethodIdentity internal constructor(
    private val catalogIdentity: Any,
    private val methodSignature: String,
) {
    override fun equals(other: Any?): Boolean =
        other is FrameworkMethodIdentity &&
            other.catalogIdentity === catalogIdentity &&
            other.methodSignature == methodSignature

    override fun hashCode(): Int = 31 * System.identityHashCode(catalogIdentity) + methodSignature.hashCode()
}
