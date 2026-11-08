package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Bug class: joining incompatible types to a fabricated "common" type, letting the
 * optimizer constant-fold across a primitive/reference boundary (spec §4.1: incompatible
 * types join to Top). Oracle: lattice laws + Dalvik type shapes, and -- since Plan 8 Task 7 --
 * the real class hierarchy `ClassIndex` reaches.
 *
 * **The join may only ever generalize UP (spec §2 principle 2).** A join that lands on a NARROWER type
 * than a true upper bound is unsound, not merely imprecise: it would let the optimizer trust a type
 * relationship a device does not agree with. Every expectation below is checked against that rule, which
 * is also why the hierarchy-aware answer is safe -- a nearest common *superclass* is a true supertype of
 * both operands by construction, and an incomplete hierarchy can only remove candidates from the common
 * set, i.e. push the answer up.
 */
class TypeJoinTest {
    private fun t(d: String) = VirtualType.of(d)

    /** The real hierarchy, shared with every other core test. */
    private val hierarchy: TypeHierarchy = CoreTester.classIndex

    /** No hierarchy at all: every chain is the type alone, the sound fallback. */
    private val noHierarchy = superclasses { null }

    /** A hierarchy that answers only [TypeHierarchy.superclassOf], the one question [joinTypes] asks. */
    private fun superclasses(of: (VirtualType) -> VirtualType?) = object : TypeHierarchy {
        override fun superclassOf(type: VirtualType): VirtualType? = of(type)
        override fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean = error("joinTypes asks only for superclasses")
        override fun isInstanceOf(candidate: VirtualType, target: VirtualType): Boolean = error("joinTypes asks only for superclasses")
    }

    /**
     * Whether both types are subtypes of [ancestor], asked through the subtype test rather than [joinTypes].
     * Used only to prove that the ambiguity `joinTypes` refuses to resolve arbitrarily is present in the data.
     */
    private fun bothExtend(first: String, second: String, ancestor: String): Boolean =
        CoreTester.classIndex.isSubtypeOf(t(first), t(ancestor)) && CoreTester.classIndex.isSubtypeOf(t(second), t(ancestor))

    @Test
    fun equalTypesJoinToThemselves() {
        assertEquals(JoinedType.Joined(t("I")), joinTypes(t("I"), t("I"), hierarchy))
        assertEquals(JoinedType.Joined(t("[[I")), joinTypes(t("[[I"), t("[[I"), hierarchy))
        assertEquals(
            JoinedType.Joined(t("Ljava/lang/String;")),
            joinTypes(t("Ljava/lang/String;"), t("Ljava/lang/String;"), hierarchy),
        )
    }

    @Test
    fun differingPrimitivesAreIncompatible() {
        assertEquals(JoinedType.Incompatible, joinTypes(t("I"), t("J"), hierarchy))
        assertEquals(JoinedType.Incompatible, joinTypes(t("F"), t("D"), hierarchy))
        assertEquals(JoinedType.Incompatible, joinTypes(t("Z"), t("I"), hierarchy))
    }

    @Test
    fun primitiveAndReferenceAreIncompatible() {
        // A primitive type and a reference type have no common upper bound, so the join produces `Value.Top`
        // rather than `Object`. The Dalvik null idiom (`const vA, 0` meeting a reference) is a VALUE fact, not a
        // type fact, and `join` handles it before this function; see `ValueJoinTest`.
        assertEquals(JoinedType.Incompatible, joinTypes(t("I"), t("Ljava/lang/Integer;"), hierarchy))
        assertEquals(JoinedType.Incompatible, joinTypes(t("[I"), t("I"), hierarchy))
    }

    @Test
    fun arraysAtTheDexDimensionLimitJoinComponentWise() {
        // Nearest common superclass of Integer and Long is Number; incompatible components join to Object
        // one dimension up.
        val deep = "[".repeat(255)
        assertEquals(
            JoinedType.Joined(t(deep + "Ljava/lang/Number;")),
            joinTypes(t(deep + "Ljava/lang/Integer;"), t(deep + "Ljava/lang/Long;"), hierarchy),
        )
        assertEquals(
            JoinedType.Joined(t("[".repeat(254) + "Ljava/lang/Object;")),
            joinTypes(t(deep + "I"), t(deep + "J"), hierarchy),
        )
        assertEquals(
            JoinedType.Joined(t("[[Ljava/lang/Object;")),
            joinTypes(t("[[[I"), t("[[Ljava/lang/String;"), hierarchy),
        )
    }

    @Test
    fun voidJoinsWithNothing() {
        assertEquals(JoinedType.Incompatible, joinTypes(t("V"), t("I"), hierarchy))
        assertEquals(JoinedType.Incompatible, joinTypes(t("V"), t("Ljava/lang/Object;"), hierarchy))
        assertEquals(JoinedType.Joined(t("V")), joinTypes(t("V"), t("V"), hierarchy))
    }

    @Test
    fun joinIsCommutative() {
        val pairs = listOf(
            t("I") to t("J"),
            t("Ljava/lang/String;") to t("[I"),
            t("[[I") to t("[[I"),
            t("Ljava/lang/String;") to t("Ljava/lang/StringBuilder;"),
            t("Ljava/lang/Integer;") to t("Ljava/lang/Long;"),
            t("[Ljava/lang/String;") to t("[Ljava/lang/StringBuilder;"),
            t("Lchild_class;") to t("Lgrandparent_class;"),
        )
        for ((a, b) in pairs) {
            assertEquals(joinTypes(a, b, hierarchy), joinTypes(b, a, hierarchy), "join($a,$b) not commutative")
        }
    }

    // ---- the hierarchy (Plan 8 Task 7) ---------------------------------------------------------

    @Test
    fun twoReferencesJoinToTheirNearestCommonSuperclass() {
        // Closes `CARRY-FORWARD.md` Plan 8 item 2, open since Plan 2. `Integer` and `Long` are both
        // `Number`s, so `Object` -- the old hierarchy-free answer -- was a true upper bound but not the
        // tightest one, and the lost precision is a fold a deobfuscator wanted.
        //
        // **THE AMBIGUITY IS ASSERTED HERE TOO, and this is the pair where it matters most.** `Integer` and
        // `Long` share `Comparable` (declared on both) and `Serializable` (through `Number`) as well as the
        // class `Number`, so an implementation picking from an unordered ancestor set
        // could answer an interface -- which for THIS pair is a strictly WRONG answer rather than a merely
        // coarser one, because a defined nearest common ancestor exists and is a class. Without these two
        // assertions the expectation below would hold just as well against a hierarchy with no shared
        // interface at all, and would be pinning less than it appears to.
        val integer = "Ljava/lang/Integer;"
        val long = "Ljava/lang/Long;"
        assertTrue(bothExtend(integer, long, "Ljava/lang/Comparable;"), "both implement Comparable")
        assertTrue(bothExtend(integer, long, "Ljava/io/Serializable;"), "both inherit Serializable via Number")
        assertTrue(bothExtend(integer, long, "Ljava/lang/Number;"), "and a real class ancestor is available")
        assertEquals(
            JoinedType.Joined(t("Ljava/lang/Number;")),
            joinTypes(t("Ljava/lang/Integer;"), t("Ljava/lang/Long;"), hierarchy),
        )
        // A subclass and its ancestor join to the ANCESTOR, exactly (not to Object).
        assertEquals(
            JoinedType.Joined(t("Lparent_class;")),
            joinTypes(t("Lchild_class;"), t("Lparent_class;"), hierarchy),
        )
        assertEquals(
            JoinedType.Joined(t("Lgrandparent_class;")),
            joinTypes(t("Lchild_class;"), t("Lgrandparent_class;"), hierarchy),
        )
        // And the old answer is still the answer when it is the right one.
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Ljava/lang/String;"), t("Ljava/lang/StringBuilder;"), hierarchy),
        )
    }

    @Test
    fun theHierarchyFreeRuleIsStillTheAnswerWhenThereIsNoHierarchy() {
        // The pre-Task-7 behaviour, kept reachable and pinned: two references generalize to Object. This
        // is the SOUND fallback every degradation path below lands on, so it needs its own witness --
        // otherwise "degrades to the old rule" would be a claim about code nothing exercises.
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Ljava/lang/Integer;"), t("Ljava/lang/Long;"), noHierarchy),
        )
        assertEquals(JoinedType.Incompatible, joinTypes(t("I"), t("Ljava/lang/Integer;"), noHierarchy))
    }

    @Test
    fun theJoinIsTheDeepestCommonElementOfTwoSuperclassChains() {
        // CAUTION 4's determinism rule, pinned as the RULE and not as a set of answers. The chains are a
        // literal table, so this assertion cannot agree with the implementation by construction (the
        // shape Task 4's disjointness matrix used); `expected` is computed from the table by the
        // definition of "deepest common element", and the table contains no interface at all, so every
        // one of the 81 answers is structurally forced to be a class on a total-ordered chain.
        //
        // Why this and not a single-answer assertion: "prefer the first common class ancestor" over an
        // unordered ancestor set has no defined answer, so a test that pins one answer cannot tell a defined
        // rule from an arbitrary pick that happened to land there. A cross product against an
        // independently-computed total order can: an implementation choosing from an unordered set fails
        // some pair.
        val chains = mapOf(
            "Ljava/lang/Object;" to listOf("Ljava/lang/Object;"),
            "Ljava/lang/String;" to listOf("Ljava/lang/String;", "Ljava/lang/Object;"),
            "Ljava/lang/Number;" to listOf("Ljava/lang/Number;", "Ljava/lang/Object;"),
            "Ljava/lang/Integer;" to listOf("Ljava/lang/Integer;", "Ljava/lang/Number;", "Ljava/lang/Object;"),
            "Ljava/lang/Long;" to listOf("Ljava/lang/Long;", "Ljava/lang/Number;", "Ljava/lang/Object;"),
            "Ljava/lang/StringBuilder;" to
                listOf("Ljava/lang/StringBuilder;", "Ljava/lang/AbstractStringBuilder;", "Ljava/lang/Object;"),
            "Lchild_class;" to
                listOf("Lchild_class;", "Lparent_class;", "Lgrandparent_class;", "Ljava/lang/Object;"),
            "Lparent_class;" to listOf("Lparent_class;", "Lgrandparent_class;", "Ljava/lang/Object;"),
            "Lgrandparent_class;" to listOf("Lgrandparent_class;", "Ljava/lang/Object;"),
        )
        var checks = 0
        for ((a, chainA) in chains) {
            for ((b, chainB) in chains) {
                // chainA runs specific -> general, so its FIRST element also in chainB is the deepest
                // type both share. Every pair shares Object, so this never falls off the end.
                val expected = chainA.first { it in chainB }
                assertEquals(JoinedType.Joined(t(expected)), joinTypes(t(a), t(b), hierarchy), "join($a,$b)")
                checks++
            }
        }
        // The LITERAL, not `chains.size * chains.size` -- which restates the table's own size and so leaves
        // the breadth of this pin unasserted. Deleting a row from the table above would silently shrink the
        // cross product to 64 and the tautological form would stay green, which is the same defect class as
        // the `assertTrue(ADDRESS_SWITCH > 0)` Task 6 shipped where the constant was `1`.
        assertEquals(81, checks, "the whole 9x9 cross product ran")
    }

    @Test
    fun interfacesAreDeliberatelyExcludedEvenWhenTwoAreShared() {
        // The determinism decision, stated where it is falsifiable. `String` and `Integer` share TWO
        // interfaces -- `Comparable` directly on both, `Serializable` directly on String and through
        // `Number` on Integer -- and no class but `Object`. "Prefer the first common ancestor" over an
        // unordered ancestor set therefore has THREE admissible answers here, which is why `joinTypes`
        // walks the superclass chain instead and answers `Object`.
        //
        // The ambiguity is ASSERTED, not assumed: without the two `assertTrue`s below this test would
        // pass just as well against a hierarchy where no interface was shared, and would then be pinning
        // nothing.
        val string = "Ljava/lang/String;"
        val integer = "Ljava/lang/Integer;"
        assertTrue(bothExtend(string, integer, "Ljava/lang/Comparable;"), "both implement Comparable, so the set is ambiguous")
        assertTrue(bothExtend(string, integer, "Ljava/io/Serializable;"), "both implement Serializable, so the set is ambiguous")
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Ljava/lang/String;"), t("Ljava/lang/Integer;"), hierarchy),
            "spec §3: full interface lattices are not a deobfuscation requirement, so the join takes " +
                "the class chain and answers Object rather than picking one of two shared interfaces",
        )
        // Same shape one level down, where the shared interface is inherited rather than declared.
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Ljava/lang/String;"), t("Ljava/lang/Number;"), hierarchy),
        )
    }

    @Test
    fun arrayComponentsJoinCovariantlyAndPrimitiveArraysStayInvariant() {
        // JLS §4.10.3, quoted at `DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`: `S[] >1 T[]`
        // iff `S >1 T` for reference components, so the array join is the COMPONENT join re-wrapped --
        // deterministic for exactly the reason the class rule is, because it reduces to it. Primitive
        // components are invariant (`int[]` and `long[]` are unrelated), so their join can only be
        // `Object`.
        assertEquals(
            JoinedType.Joined(t("[Ljava/lang/Object;")),
            joinTypes(t("[Ljava/lang/String;"), t("[Ljava/lang/StringBuilder;"), hierarchy),
        )
        assertEquals(
            JoinedType.Joined(t("[Ljava/lang/Number;")),
            joinTypes(t("[Ljava/lang/Integer;"), t("[Ljava/lang/Long;"), hierarchy),
        )
        assertEquals(
            JoinedType.Joined(t("[[Ljava/lang/Object;")),
            joinTypes(t("[[Ljava/lang/String;"), t("[[Ljava/lang/StringBuilder;"), hierarchy),
        )
        // Incompatible COMPONENTS do not make the arrays incompatible -- both are still references, and
        // `Object` is a true upper bound of any two arrays. Turning this into `Incompatible` would claim
        // two live references have no common type, i.e. would produce a `Value.Top` that means "the paths
        // disagree about a primitive and a reference" when they do not.
        assertEquals(JoinedType.Joined(VirtualType.OBJECT), joinTypes(t("[I"), t("[J"), hierarchy))
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("[I"), t("[Ljava/lang/String;"), hierarchy),
        )
        // An array and a non-array share nothing but Object, whatever the component is.
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("[Ljava/lang/String;"), t("Ljava/lang/String;"), hierarchy),
        )
        // DIFFERING RANK still joins covariantly, and this expectation was written wrong the first time --
        // recorded rather than quietly corrected, because getting it wrong in the other direction would be
        // unsound. `String[][]` IS an `Object[]`: apply the covariance rule ONCE and the components are
        // `String[]` and `String`, which join to `Object`, so the arrays join to `[Ljava/lang/Object;` --
        // a TIGHTER true upper bound than `Ljava/lang/Object;`, and legal Java (`Object[] o = new
        // String[2][2]` compiles). The first draft expected `Ljava/lang/Object;` and the implementation
        // was right.
        assertEquals(
            JoinedType.Joined(t("[Ljava/lang/Object;")),
            joinTypes(t("[[Ljava/lang/String;"), t("[Ljava/lang/String;"), hierarchy),
        )
    }

    @Test
    fun anUnloadableAncestorDegradesToObjectRatherThanRaising() {
        // CAUTION 3, and it is the OPPOSITE of what `isSubtypeOf` does with the same failure. For a
        // subtype question there is no safe default, so `ClassLoadFailure` propagates; for a JOIN the
        // pre-hierarchy answer (`Object` for two references) is still a true upper bound, so degrading
        // loses precision and nothing else. Propagating instead would turn an obfuscated APK's missing
        // superclass into a host exception out of every consensus query in the engine.
        val raises = superclasses { type ->
            throw ClassLoadFailure("no class behind ${type.descriptor}")
        }
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Ljava/lang/Integer;"), t("Ljava/lang/Long;"), raises),
        )
        // The lattice rows that need no hierarchy still short-circuit BEFORE any walk, so a failing class
        // model cannot change them.
        assertEquals(JoinedType.Incompatible, joinTypes(t("I"), t("Ljava/lang/Integer;"), raises))
        assertEquals(
            JoinedType.Joined(t("Ljava/lang/String;")),
            joinTypes(t("Ljava/lang/String;"), t("Ljava/lang/String;"), raises),
        )
    }

    @Test
    fun aChainThatFailsPartWayKeepsTheAncestorsItAlreadyResolved() {
        // Truncation, not abandonment: a chain that dies at `Lgrandparent_class;` still contains
        // `Lparent_class;`, so `child ⊔ parent` is still exactly `parent` rather than collapsing to
        // `Object`. Sound in the same direction as everything else here -- a missing ancestor can only
        // remove candidates from the common set, i.e. push the answer UP -- and it is what makes the
        // degradation a precision loss proportional to how much of the hierarchy is absent.
        val truncating = superclasses { type ->
            when (type.descriptor) {
                "Lchild_class;" -> t("Lparent_class;")
                "Lparent_class;" -> throw ClassLoadFailure("absent superclass")
                else -> throw ClassLoadFailure("absent")
            }
        }
        assertEquals(
            JoinedType.Joined(t("Lparent_class;")),
            joinTypes(t("Lchild_class;"), t("Lparent_class;"), truncating),
        )
        // ...and where the resolvable prefixes share nothing, the fallback is `Object`, not a raise.
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Lchild_class;"), t("Ljava/lang/String;"), truncating),
        )
    }

    @Test
    fun aCyclicSuperclassChainTerminatesInsteadOfHangingTheEngine() {
        // Obfuscated input is the EXPECTED input (spec §3), and nothing in a dex file prevents
        // `A extends B extends A`: dexlib parses each class def independently and the oracle's own
        // flattening only survives it because `getAncestors0` tests set membership first
        // (`type/VirtualClass.java:39-48`).
        //
        // TERMINATING IS THE EASY HALF. The load-bearing assertion is the COMMUTATIVITY one below, and it
        // is the reason a cycle is refused outright rather than truncated: under `A extends B extends A`
        // both chains contain the same two types in OPPOSITE orders, so "the deepest type both chains
        // share" answers `A` one way round and `B` the other. `joinAll` folds left to right over a node
        // pile, so a non-commutative join makes a register's consensus depend on node ORDER -- which is
        // precisely the nondeterminism the superclass chain was chosen to remove, arriving through hostile
        // input instead of through a `HashSet`. This test failed with `Lchild_class;` before the refusal
        // landed, which is what "measured, not theorised" means here.
        val cyclic = superclasses { type ->
            when (type.descriptor) {
                "Lchild_class;" -> t("Lparent_class;")
                "Lparent_class;" -> t("Lchild_class;")
                else -> null
            }
        }
        val forward = joinTypes(t("Lchild_class;"), t("Lparent_class;"), cyclic)
        val backward = joinTypes(t("Lparent_class;"), t("Lchild_class;"), cyclic)
        assertEquals(forward, backward, "an inconsistent hierarchy must not make the join order-sensitive")
        // ...and the answer it commutes to is the hierarchy-free one: an inconsistent hierarchy gets no
        // sharpening at all, which is a true upper bound for any two references.
        assertEquals(JoinedType.Joined(VirtualType.OBJECT), forward)
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Lchild_class;"), t("Ljava/lang/String;"), cyclic),
        )
        // A cycle further UP one chain is refused the same way, so the guard is not keyed on the operands.
        val deepCycle = superclasses { type ->
            when (type.descriptor) {
                "Lchild_class;" -> t("Lparent_class;")
                "Lparent_class;" -> t("Lgrandparent_class;")
                "Lgrandparent_class;" -> t("Lparent_class;")
                else -> null
            }
        }
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Lchild_class;"), t("Lparent_class;"), deepCycle),
        )
    }

    @Test
    fun aNonReferenceSuperclassIsRefusedRatherThanJoinedTo() {
        // A malformed class def whose superclass descriptor is a primitive would otherwise let the join
        // answer a PRIMITIVE for two reference operands -- a narrower answer than any true upper bound,
        // i.e. the unsound direction, from input this tool is built to accept. The chain stops there.
        val primitiveSuperclass = superclasses { type ->
            if (type.isReference && !type.isArray) t("I") else null
        }
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Ljava/lang/Integer;"), t("Ljava/lang/Long;"), primitiveSuperclass),
        )
    }
}
