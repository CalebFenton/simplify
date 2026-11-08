package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * [joinTypes] may only generalize up: an answer narrower than a true upper bound of both operands is unsound.
 * Incompatible types (a primitive and a reference, two differing primitives, `V` and anything else) join to
 * [JoinedType.Incompatible]. Two references join to their deepest common superclass, so a missing or broken
 * hierarchy can only remove candidates and push the answer up toward `Object`.
 */
class TypeJoinTest {
    private fun t(d: String) = VirtualType.of(d)

    private val hierarchy: TypeHierarchy = CoreTester.classIndex

    /** A hierarchy that answers only [TypeHierarchy.superclassOf], the one question [joinTypes] asks. */
    private fun superclasses(of: (VirtualType) -> VirtualType?) = object : TypeHierarchy {
        override fun superclassOf(type: VirtualType): VirtualType? = of(type)
        override fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean = error("joinTypes asks only for superclasses")
        override fun isInstanceOf(candidate: VirtualType, target: VirtualType): Boolean = error("joinTypes asks only for superclasses")
    }

    /** Whether both types are subtypes of [ancestor], asked through the subtype test rather than [joinTypes]. */
    private fun bothExtend(first: String, second: String, ancestor: String): Boolean =
        CoreTester.classIndex.isSubtypeOf(t(first), t(ancestor)) && CoreTester.classIndex.isSubtypeOf(t(second), t(ancestor))

    private fun joined(descriptor: String) = JoinedType.Joined(t(descriptor))

    /**
     * Each row is checked in both operand orders. Array rows follow JLS §4.10.3 (see "Subtype testing" in
     * `docs/DALVIK-SEMANTICS.md`): reference components join covariantly, and primitive components are
     * invariant, so two arrays with incompatible components still share `Object`.
     */
    @TestFactory
    fun joinOverTheRealHierarchy(): List<DynamicTest> {
        val object_ = "Ljava/lang/Object;"
        val deep = "[".repeat(255)
        return listOf(
            Triple("I", "I", "I"),
            Triple("[[I", "[[I", "[[I"),
            Triple("Ljava/lang/String;", "Ljava/lang/String;", "Ljava/lang/String;"),
            Triple("V", "V", "V"),
            Triple("I", "J", null),
            Triple("F", "D", null),
            Triple("Z", "I", null),
            // The null idiom (`const vA, 0` meeting a reference) is a value fact that `join` handles first.
            Triple("I", "Ljava/lang/Integer;", null),
            Triple("[I", "I", null),
            Triple("V", "I", null),
            Triple("V", object_, null),
            Triple("Lchild_class;", "Lparent_class;", "Lparent_class;"),
            Triple("Lchild_class;", "Lgrandparent_class;", "Lgrandparent_class;"),
            Triple("Ljava/lang/String;", "Ljava/lang/StringBuilder;", object_),
            Triple("[Ljava/lang/String;", "[Ljava/lang/StringBuilder;", "[Ljava/lang/Object;"),
            Triple("[Ljava/lang/Integer;", "[Ljava/lang/Long;", "[Ljava/lang/Number;"),
            Triple("[[Ljava/lang/String;", "[[Ljava/lang/StringBuilder;", "[[Ljava/lang/Object;"),
            Triple("[I", "[J", object_),
            Triple("[I", "[Ljava/lang/String;", object_),
            Triple("[[[I", "[[Ljava/lang/String;", "[[Ljava/lang/Object;"),
            Triple("[Ljava/lang/String;", "Ljava/lang/String;", object_),
            Triple("Ljava/lang/String;", "[I", object_),
            // Differing rank: `String[][]` is an `Object[]` (components `String[]` and `String` join to Object).
            Triple("[[Ljava/lang/String;", "[Ljava/lang/String;", "[Ljava/lang/Object;"),
            Triple(deep + "Ljava/lang/Integer;", deep + "Ljava/lang/Long;", deep + "Ljava/lang/Number;"),
            Triple(deep + "I", deep + "J", "[".repeat(254) + object_),
        ).map { (a, b, expected) ->
            DynamicTest.dynamicTest("join($a, $b)") {
                val answer = if (expected == null) JoinedType.Incompatible else joined(expected)
                assertEquals(answer, joinTypes(t(a), t(b), hierarchy))
                assertEquals(answer, joinTypes(t(b), t(a), hierarchy), "the join must be commutative")
            }
        }
    }

    @Test
    fun classAncestorsArePreferredOverSharedInterfaces() {
        // Each pair shares Comparable and Serializable, so picking from an unordered ancestor set could answer an
        // interface. The join walks the superclass chain instead: Number where a class ancestor exists, else Object.
        val integer = "Ljava/lang/Integer;"
        val long = "Ljava/lang/Long;"
        val string = "Ljava/lang/String;"
        for ((a, b) in listOf(integer to long, string to integer)) {
            assertTrue(bothExtend(a, b, "Ljava/lang/Comparable;"), "$a and $b both implement Comparable")
            assertTrue(bothExtend(a, b, "Ljava/io/Serializable;"), "$a and $b both implement Serializable")
        }
        assertEquals(joined("Ljava/lang/Number;"), joinTypes(t(integer), t(long), hierarchy))
        assertEquals(JoinedType.Joined(VirtualType.OBJECT), joinTypes(t(string), t(integer), hierarchy))
        // The shared interface is inherited rather than declared here.
        assertEquals(JoinedType.Joined(VirtualType.OBJECT), joinTypes(t(string), t("Ljava/lang/Number;"), hierarchy))
    }

    @Test
    fun theJoinIsTheDeepestCommonElementOfTwoSuperclassChains() {
        // The chains are a literal table with no interfaces, and each expected answer is computed from it by the
        // definition of "deepest common element". A pick from an unordered ancestor set fails some pair.
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
        for ((a, chainA) in chains) {
            for ((b, chainB) in chains) {
                // chainA runs specific to general, so its first element also in chainB is the deepest shared type.
                val expected = chainA.first { it in chainB }
                assertEquals(joined(expected), joinTypes(t(a), t(b), hierarchy), "join($a,$b)")
            }
        }
    }

    /**
     * For two references `Object` is always a true upper bound, so a hierarchy that is absent, raises
     * [ClassLoadFailure], or names a primitive superclass loses precision and nothing else. Rows that need no
     * hierarchy short-circuit before any walk.
     */
    @TestFactory
    fun anAbsentOrBrokenHierarchyDegradesTwoReferencesToObject(): List<DynamicTest> = listOf(
        "no hierarchy" to superclasses { null },
        "every lookup raises" to superclasses { type -> throw ClassLoadFailure("no class behind ${type.descriptor}") },
        "primitive superclass" to superclasses { type -> if (type.isReference && !type.isArray) t("I") else null },
    ).map { (name, broken) ->
        DynamicTest.dynamicTest(name) {
            assertEquals(
                JoinedType.Joined(VirtualType.OBJECT),
                joinTypes(t("Ljava/lang/Integer;"), t("Ljava/lang/Long;"), broken),
            )
            assertEquals(JoinedType.Incompatible, joinTypes(t("I"), t("Ljava/lang/Integer;"), broken))
            assertEquals(
                joined("Ljava/lang/String;"),
                joinTypes(t("Ljava/lang/String;"), t("Ljava/lang/String;"), broken),
            )
        }
    }

    @Test
    fun aChainThatFailsPartWayKeepsTheAncestorsItAlreadyResolved() {
        // A chain that dies above `Lparent_class;` still contains it, so `child ⊔ parent` stays `parent`.
        val truncating = superclasses { type ->
            when (type.descriptor) {
                "Lchild_class;" -> t("Lparent_class;")
                "Lparent_class;" -> throw ClassLoadFailure("absent superclass")
                else -> throw ClassLoadFailure("absent")
            }
        }
        assertEquals(joined("Lparent_class;"), joinTypes(t("Lchild_class;"), t("Lparent_class;"), truncating))
        // Where the resolvable prefixes share nothing, the fallback is `Object`, not a raise.
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Lchild_class;"), t("Ljava/lang/String;"), truncating),
        )
    }

    @Test
    fun aCyclicSuperclassChainJoinsToObjectInEitherOrder() {
        // Nothing in a dex file prevents `A extends B extends A`. Both chains then hold the same two types in
        // opposite orders, so "deepest shared type" would answer A one way and B the other, and `joinAll` folds a
        // node pile left to right. A cycle is therefore refused outright: the join commutes to Object.
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
        assertEquals(JoinedType.Joined(VirtualType.OBJECT), forward)
        assertEquals(
            JoinedType.Joined(VirtualType.OBJECT),
            joinTypes(t("Lchild_class;"), t("Ljava/lang/String;"), cyclic),
        )
        // A cycle further up one chain is refused the same way, so the guard is not keyed on the operands.
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
}
