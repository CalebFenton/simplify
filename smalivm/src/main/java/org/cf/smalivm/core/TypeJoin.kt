package org.cf.smalivm.core

/** Result of joining two types: a common upper bound, or proof there is none. */
sealed interface JoinedType {
    data class Joined(val type: VirtualType) : JoinedType
    data object Incompatible : JoinedType
}

/**
 * The class hierarchy [joinTypes] is parameterized over: one link of a **superclass chain**.
 *
 * Mirrors [TypeHierarchy] deliberately -- a `fun interface` in `core/`, implemented for real by
 * [ClassIndex] and by a lambda in tests -- so the algebra in this file never imports an oracle type and
 * survives the oracle's deletion in Plan 16 (spec §12). It is a SECOND interface rather than a second
 * method on [TypeHierarchy] for two reasons: [TypeHierarchy] is a `fun interface` whose SAM conversion
 * several tests use, and its KDoc's non-totality contract is load-bearing for its own two consumers, so
 * widening it would change decisions this file has nothing to do with.
 *
 * **Why the superclass chain and not the ancestor set.** `VirtualClass.getImmediateAncestors()` puts the
 * superclass and every interface into one `HashSet` whose "order is unspecified"
 * (`docs/architecture/type-and-dex.md:93`, restated at `exceptions.md:164`: "Ancestor order is not an
 * explicit distance ordering ... Branching ancestry can therefore depend on collection iteration
 * order"), and `getAncestors()` flattens that into a single mutable cache it returns **by reference**,
 * with no revision and no invalidation (`type-and-dex.md:161`, `:216`). "Prefer the first common
 * ancestor" over that set is therefore not a defined rule: it picks by descriptor-string hash and
 * insertion history, so two runs of one input could disagree about what a register's type is -- and
 * `joinTypes` feeds every consensus query in the engine. A superclass chain is a **total order**, so
 * "the deepest type both share" has exactly one answer.
 *
 * **The implementation must therefore never read `getAncestors()` and never retain any set it did not
 * build itself.** [ClassIndex.superclassOf] reads `BuilderClassDef.getSuperclass()` -- one string, no
 * collection, no cache -- which removes the hazard at the source rather than by copying around it. A
 * different implementation that has to read an ancestor set must **copy on read**. And because
 * `VirtualType.equals()` "ignores the owning `ClassManager`" (`type-and-dex.md:79`), no result here may
 * be cached against object identity alone.
 *
 * **Not total, exactly like [TypeHierarchy], and for the same reason: an obfuscated APK routinely names
 * a superclass it does not ship.** An implementation reports that by throwing [ClassLoadFailure] and
 * must NOT invent an answer -- `null` means "this type genuinely has no superclass" (`Ljava/lang/Object;`,
 * a primitive, `void`), which is a completely different fact and one [joinTypes] treats as the end of a
 * complete chain. What the two callers DO with the failure differs, and that difference is only
 * available because it is reported: see [joinTypes].
 */
fun interface Superclasses {
    /**
     * The direct superclass of [type], or `null` if it has none. Throws [ClassLoadFailure] when the class
     * model cannot answer for a well-formed descriptor.
     */
    fun superclassOf(type: VirtualType): VirtualType?
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
 * **A failed walk degrades to `Object` instead of propagating**, unlike [ClassIndex.isSubtypeOf]. For a
 * subtype question there is no safe default, so [ClassLoadFailure] propagates there. For a join, `Object` is
 * still a true upper bound, so a missing superclass costs precision only; the chain is kept as far as it
 * resolved. Missing ancestors can only remove candidates from the two chains' intersection, so the answer
 * moves up, never down. That holds while **one descriptor resolves to at most one class def in the loaded
 * set**, which the class model guarantees by resolving each descriptor through one `ClassManager` with
 * last-write-wins insertion.
 *
 * **Interfaces are excluded** (spec §3: full interface lattices are not a deobfuscation requirement). Two
 * types sharing only interfaces join to `Object`. `instance-of` and `check-cast` do consult interfaces --
 * see [ClassIndex.isInstanceOf].
 */
fun joinTypes(a: VirtualType, b: VirtualType, superclasses: Superclasses): JoinedType {
    if (a == b) return JoinedType.Joined(a)
    if (!a.isReference || !b.isReference) return JoinedType.Incompatible
    val sharedDimensions = minOf(a.arrayDimensions(), b.arrayDimensions())
    val sharedPrefix = a.descriptor.substring(0, sharedDimensions)
    val componentA = VirtualType.of(a.descriptor.substring(sharedDimensions))
    val componentB = VirtualType.of(b.descriptor.substring(sharedDimensions))
    // The components differ and at most one is an array. A primitive component leaves the arrays sharing only
    // `Object` one dimension up; sharedDimensions is then at least 1 because a and b are references.
    val joined = if (componentA.isReference && componentB.isReference) {
        sharedPrefix + nearestCommonSuperclass(componentA, componentB, superclasses).descriptor
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
private fun nearestCommonSuperclass(a: VirtualType, b: VirtualType, superclasses: Superclasses): VirtualType {
    val ofA = superclassChain(a, superclasses) ?: return VirtualType.OBJECT
    val ofB = superclassChain(b, superclasses) ?: return VirtualType.OBJECT
    return ofA.firstOrNull { it in ofB } ?: VirtualType.OBJECT
}

/**
 * `[type, superclass, ..., Ljava/lang/Object;]`, or as much of it as resolves -- and `null` when the
 * declared hierarchy is **not a partial order at all**, which is a different answer and not a degenerate
 * chain (see the cycle case below).
 *
 * TRUNCATES, keeping the resolvable prefix, on two things -- both ordinary in obfuscated input, both sound
 * in the same direction (fewer candidates in the common set can only push the join UP), and both harmless
 * to commutativity because a prefix of one chain is still consistent with the other's:
 *  - [ClassLoadFailure]: the superclass is not in the loaded set.
 *  - A superclass descriptor that is **not a reference**. A malformed class def naming a primitive would
 *    otherwise let two reference operands join to a primitive -- narrower than any true upper bound, i.e.
 *    the unsound direction.
 *
 * REFUSES, answering `null` so [nearestCommonSuperclass] falls back to `Ljava/lang/Object;`, on one thing:
 *  - A **cycle**. Nothing in a dex file prevents `A extends B extends A`; dexlib parses each class def
 *    independently, and the oracle's own flattening survives it only because `getAncestors0` tests set
 *    membership first (`type/VirtualClass.java:39-48`). Truncating a cycle is NOT enough here, and the
 *    reason is the one this whole file exists for: under `A extends B extends A` both chains contain the
 *    same two types in OPPOSITE orders, so "the first of `a`'s chain that `b`'s also contains" answers `A`
 *    for `join(A, B)` and `B` for `join(B, A)`. A non-commutative join makes `joinAll`'s left fold
 *    order-sensitive, i.e. reintroduces exactly the nondeterminism the superclass chain was chosen to
 *    remove -- through hostile input instead of through a `HashSet`. An inconsistent hierarchy therefore
 *    gets no sharpening at all. **Measured, not theorised:** before this refusal
 *    `TypeJoinTest.aCyclicSuperclassChainTerminatesInsteadOfHangingTheEngine` failed with
 *    `Lchild_class;` where `Lparent_class;` was expected, i.e. with the answer that is not commutative.
 *
 * Only [ClassLoadFailure] is caught; anything else escapes untranslated, so a bug in this file or in a
 * [Superclasses] implementation still reaches the caller as itself (the discipline
 * [ClassIndex.isSubtypeOf] documents).
 */
private fun superclassChain(type: VirtualType, superclasses: Superclasses): Set<VirtualType>? {
    val chain = LinkedHashSet<VirtualType>()
    var current: VirtualType? = type
    while (current != null && current.isReference) {
        if (!chain.add(current)) return null // a cycle: the declared hierarchy is not a partial order
        current = try {
            superclasses.superclassOf(current)
        } catch (e: ClassLoadFailure) {
            null
        }
    }
    return chain
}
