package org.cf.smalivm.core

/** Result of joining two types: a common upper bound, or proof there is none. */
internal sealed interface JoinedType {
    data class Joined(val type: VirtualType) : JoinedType
    data object Incompatible : JoinedType
}

/**
 * The type half of the reconciliation operator ⊔ (spec §4.1). Rules, in order:
 *  - equal types join to themselves;
 *  - anything that is not a reference on both sides is [JoinedType.Incompatible] -- differing
 *    primitives, primitive vs reference, `void` vs anything;
 *  - two arrays join component-wise over their shared dimensions and re-wrap, falling back to `Object` one
 *    dimension up when the components do not join;
 *  - two other references join to their **nearest common superclass**, or `Ljava/lang/Object;` when the
 *    chains share nothing else.
 *
 * **Every answer is a true upper bound (spec §2.2).** A nearest common superclass is a supertype of both
 * operands by construction, and array covariance keeps that property (JLS §4.10.3). Going too high costs
 * precision; going too low would claim a type relationship a device does not agree with, so every
 * degradation goes up.
 *
 * [JoinedType.Incompatible] is reserved for non-reference operands, because it produces [Value.Top]. Two
 * arrays with incompatible components are still two references and join to `Object`.
 *
 * **A failed walk degrades to `Object` instead of propagating**, unlike [TypeHierarchy.isSubtypeOf]. For a
 * subtype question there is no safe default, so [ClassLoadFailure] propagates there. For a join, `Object` is
 * still a true upper bound, so a missing superclass costs precision only; the chain is kept as far as it
 * resolved. Missing ancestors can only remove candidates from the two chains' intersection, so the answer
 * moves up, never down. That holds while **one descriptor resolves to at most one class def in the loaded
 * set**, which the importer guarantees by defining the first application class of each name and none that
 * a framework class already defines.
 *
 * **Interfaces are excluded** (spec §3: full interface lattices are not a deobfuscation requirement). Two
 * types sharing only interfaces join to `Object`. `instance-of` and `check-cast` do consult interfaces --
 * see [TypeHierarchy.isInstanceOf].
 */
internal fun joinTypes(a: VirtualType, b: VirtualType, hierarchy: TypeHierarchy): JoinedType {
    if (a == b) return JoinedType.Joined(a)
    if (!a.isReference || !b.isReference) return JoinedType.Incompatible
    val sharedDimensions = minOf(a.arrayDimensions(), b.arrayDimensions())
    val sharedPrefix = a.descriptor.substring(0, sharedDimensions)
    val componentA = VirtualType.of(a.descriptor.substring(sharedDimensions))
    val componentB = VirtualType.of(b.descriptor.substring(sharedDimensions))
    // The components differ and at most one is an array. A primitive component leaves the arrays sharing only
    // `Object` one dimension up; sharedDimensions is then at least 1 because a and b are references.
    val joined = if (componentA.isReference && componentB.isReference) {
        sharedPrefix + nearestCommonSuperclass(componentA, componentB, hierarchy).descriptor
    } else {
        sharedPrefix.drop(1) + VirtualType.OBJECT.descriptor
    }
    return JoinedType.Joined(VirtualType.of(joined))
}

private fun VirtualType.arrayDimensions(): Int = descriptor.indexOfFirst { it != '[' }

/**
 * The deepest type present in both superclass chains, or `Ljava/lang/Object;` when they share nothing
 * else. [superclassChain] runs specific -> general, so the FIRST of `a`'s chain that `b`'s also contains
 * is the deepest common one -- a single defined answer, because each chain is a total order.
 */
private fun nearestCommonSuperclass(a: VirtualType, b: VirtualType, hierarchy: TypeHierarchy): VirtualType {
    val ofA = superclassChain(a, hierarchy) ?: return VirtualType.OBJECT
    val ofB = superclassChain(b, hierarchy) ?: return VirtualType.OBJECT
    return ofA.firstOrNull { it in ofB } ?: VirtualType.OBJECT
}

/**
 * `[type, superclass, ..., Ljava/lang/Object;]`, or as much of it as resolves.
 *
 * Truncates, keeping the resolvable prefix, at a [ClassLoadFailure] and at a superclass that is not a reference
 * (a malformed class def naming a primitive would otherwise join two references to a primitive). Both are sound:
 * fewer common candidates can only push the join up, and a prefix of one chain stays consistent with the other.
 *
 * Answers `null`, so the join falls back to `Ljava/lang/Object;`, when the chain cycles. Under
 * `A extends B extends A` both chains hold the same types in opposite orders, so taking the first common one
 * would make `join(A, B) != join(B, A)` and `joinAll`'s fold order-sensitive.
 *
 * Only [ClassLoadFailure] is caught; any other failure is a defect and reaches the caller as itself.
 */
private fun superclassChain(type: VirtualType, hierarchy: TypeHierarchy): Set<VirtualType>? {
    val chain = LinkedHashSet<VirtualType>()
    var current: VirtualType? = type
    while (current != null && current.isReference) {
        if (!chain.add(current)) return null
        current = try {
            hierarchy.superclassOf(current)
        } catch (_: ClassLoadFailure) {
            null
        }
    }
    return chain
}
