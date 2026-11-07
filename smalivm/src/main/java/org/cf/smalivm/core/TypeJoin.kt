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
 *  - two ARRAYS join component-wise and re-wrap ([JoinedType.Joined] `[C` where `C` is the component
 *    join), falling back to `Ljava/lang/Object;` when the components do not join;
 *  - two other references join to their **nearest common superclass**, or `Ljava/lang/Object;` when the
 *    chains share nothing else.
 *
 * **Soundness direction (spec §2.2): every answer here is a TRUE upper bound, and that is what makes the
 * hierarchy safe to consult.** A nearest common superclass is a supertype of both operands by
 * construction, and array covariance keeps that property (`A <: C` implies `[A <: [C`, JLS §4.10.3, quoted
 * at `docs/DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`). Going too HIGH costs a fold a
 * deobfuscator wanted; going too LOW would claim a type relationship a device does not agree with, which
 * is the direction that corrupts a consumer's decision, so every degradation below goes up.
 *
 * **[JoinedType.Incompatible] still means what it meant.** It is what produces [Value.Top], which Task 5
 * gave a distinguishable meaning at the query surface, so a hierarchy-aware join must not quietly turn a
 * primitive-vs-reference pair into `Object`. Two arrays with incompatible *components* are still two
 * references and still join to `Object`; only the operands themselves being non-references produces
 * `Incompatible`.
 *
 * **A failed walk degrades to `Object` instead of propagating, which is the OPPOSITE of what
 * [ClassIndex.isSubtypeOf] does with the identical failure -- stated here because the difference is not
 * obvious.** For a subtype question there is no safe default (answering `false` would silently un-catch a
 * handler), so [ClassLoadFailure] propagates. For a JOIN the pre-hierarchy answer -- `Object` for two
 * references -- is still a true upper bound, so a missing superclass costs precision and nothing else;
 * propagating instead would turn an obfuscated APK's absent class into a host exception out of every
 * consensus query in the engine. The chain is kept as far as it resolved, so the loss is proportional to
 * how much of the hierarchy is absent rather than total.
 *
 * **And the sharpening stays sound under an INCOMPLETE hierarchy, which is a property with a stated premise
 * rather than an assumption.** Missing ancestors can only *remove* candidates from the two chains'
 * intersection, so the surviving answer is at least as general -- the answer moves UP, never down, and total
 * absence lands on the pre-hierarchy `Object`. The unsound direction would need an **over**-reported subtype
 * relation, which requires the premise below to fail:
 *
 * > **One descriptor resolves to at most one class def in the loaded set.**
 *
 * The class model resolves each descriptor through one `ClassManager` with last-write-wins insertion.
 * This uniqueness premise matters independently of packaging: duplicate input definitions or a collision
 * with a framework descriptor must not be interpreted as two simultaneous class identities.
 *
 * **Interfaces are deliberately excluded, not picked arbitrarily** (spec §3: full interface lattices are
 * not a deobfuscation requirement). Two types sharing only interfaces join to `Object`. `instance-of` and
 * `check-cast` DO consult interfaces -- see [ClassIndex.isInstanceOf] -- so this is an exclusion in the
 * join alone.
 */
fun joinTypes(a: VirtualType, b: VirtualType, superclasses: Superclasses): JoinedType = when {
    a == b -> JoinedType.Joined(a)
    !a.isReference || !b.isReference -> JoinedType.Incompatible
    a.isArray && b.isArray -> when (val component = joinTypes(a.componentType!!, b.componentType!!, superclasses)) {
        is JoinedType.Joined -> JoinedType.Joined(VirtualType.of("[" + component.type.descriptor))
        JoinedType.Incompatible -> JoinedType.Joined(VirtualType.OBJECT)
    }
    else -> JoinedType.Joined(nearestCommonSuperclass(a, b, superclasses))
}

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
