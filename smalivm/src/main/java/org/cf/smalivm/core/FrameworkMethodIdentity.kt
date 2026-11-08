package org.cf.smalivm.core

/**
 * Identity of one method in a frozen framework catalog, compared by reference: the catalog creates exactly one per
 * method, so two views name the same framework method only when their identities are the same object.
 */
internal class FrameworkMethodIdentity internal constructor(private val methodSignature: MethodSignature) {
    override fun toString(): String = methodSignature.toString()
}
