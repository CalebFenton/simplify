package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Bug class: unsound consensus — reporting a constant when explored paths disagree, or
 * resurrecting knowledge after it was lost (join going DOWN the lattice). Oracle: the
 * spec §4.1 join table plus lattice laws (commutative, idempotent, Top absorbing,
 * associative on the table's cases).
 */
class ValueJoinTest {
    private val int = VirtualType.of("I")
    private val long = VirtualType.of("J")
    private val byte = VirtualType.of("B")
    private val str = VirtualType.of("Ljava/lang/String;")
    private val sb = VirtualType.of("Ljava/lang/StringBuilder;")
    private val num = VirtualType.of("Ljava/lang/Number;")

    /**
     * The real hierarchy (Plan 8 Task 7). Threaded rather than defaulted at the source, so these tests
     * pass it explicitly -- which is the point: a default would let a production call site lose precision
     * engine-wide with nothing failing.
     */
    private val hierarchy: TypeHierarchy = CoreTester.classIndex

    @Test
    fun agreeingKnownsJoinToTheKnown() {
        assertEquals(Value.Known(5, int), join(Value.Known(5, int), Value.Known(5, int), hierarchy))
        val ref = Value.Known(HeapRef(3), str)
        assertEquals(ref, join(ref, Value.Known(HeapRef(3), str), hierarchy))
    }

    @Test
    fun disagreeingKnownsSameTypeJoinToUnknownOfThatType() {
        assertEquals(Value.Unknown(int), join(Value.Known(5, int), Value.Known(6, int), hierarchy))
    }

    @Test
    fun disagreeingKnownsCompatibleTypesJoinToUnknownOfJoinedType() {
        // Two different reference types: value gone, type generalizes to Object.
        assertEquals(
            Value.Unknown(VirtualType.OBJECT),
            join(Value.Known(HeapRef(1), str), Value.Known(HeapRef(2), sb), hierarchy),
        )
    }

    @Test
    fun incompatibleTypesJoinToTop() {
        assertEquals(Value.Top, join(Value.Known(5, int), Value.Known(5L, long), hierarchy))
        assertEquals(Value.Top, join(Value.Unknown(int), Value.Unknown(long), hierarchy))
    }

    @Test
    fun knownJoinUnknownLosesTheValueKeepsJoinedType() {
        assertEquals(Value.Unknown(int), join(Value.Known(5, int), Value.Unknown(int), hierarchy))
        assertEquals(Value.Unknown(VirtualType.OBJECT), join(Value.Known(HeapRef(1), str), Value.Unknown(sb), hierarchy))
    }

    @Test
    fun topIsAbsorbing() {
        assertEquals(Value.Top, join(Value.Top, Value.Known(5, int), hierarchy))
        assertEquals(Value.Top, join(Value.Unknown(int), Value.Top, hierarchy))
        assertEquals(Value.Top, join(Value.Top, Value.Top, hierarchy))
    }

    @Test
    fun joinIsCommutativeOnRepresentativeCases() {
        val cases = listOf(
            Value.Known(5, int) to Value.Known(6, int),
            Value.Known(5, int) to Value.Unknown(int),
            Value.Known(HeapRef(1), str) to Value.Known(HeapRef(2), sb),
            Value.Known(5, int) to Value.Top,
            Value.Unknown(int) to Value.Unknown(long),
        )
        for ((a, b) in cases) assertEquals(join(a, b, hierarchy), join(b, a, hierarchy), "join($a,$b) not commutative")
    }

    @Test
    fun joinAllFoldsToConsensus() {
        // The consensus read: one surviving Known means all paths agree (spec §6.3).
        assertEquals(Value.Known(5, int), joinAll(listOf(Value.Known(5, int), Value.Known(5, int)), hierarchy))
        assertEquals(Value.Unknown(int), joinAll(listOf(Value.Known(5, int), Value.Known(6, int), Value.Known(5, int)), hierarchy))
        assertEquals(Value.Top, joinAll(listOf(Value.Known(5, int), Value.Known(5L, long)), hierarchy))
        assertNull(joinAll(emptyList(), hierarchy))
    }

    @Test
    fun sameTypeUnknownJoinIsIdempotent() {
        // Bug class: a mutant collapsing the Unknown⊔Unknown case to "always Top" — a
        // surviving mutant this pins down. Same-type Unknown must be a fixed point of
        // join, not poison to Top.
        assertEquals(Value.Unknown(int), join(Value.Unknown(int), Value.Unknown(int), hierarchy))
    }

    @Test
    fun theHierarchyReachesJoinAndNotOnlyJoinTypes() {
        // Plan 8 Task 7's threading, pinned at the ⊔ level rather than only at `joinTypes`. Without this
        // the hierarchy could be correct in `TypeJoinTest` while `join` -- the function every consensus
        // query actually calls -- still passed a hierarchy-free stub, and nothing would fail.
        // `Integer` and `Long` are both `Number`s, so the old `Object` answer was a true upper bound but
        // not the tightest one.
        assertEquals(
            Value.Unknown(num),
            join(Value.Known(HeapRef(1), VirtualType.of("Ljava/lang/Integer;")), Value.Unknown(VirtualType.of("Ljava/lang/Long;")), hierarchy),
        )
        assertEquals(
            Value.Unknown(num),
            joinAll(
                listOf(Value.Unknown(VirtualType.of("Ljava/lang/Integer;")), Value.Unknown(VirtualType.of("Ljava/lang/Long;"))),
                hierarchy,
            ),
        )
    }

    @Test
    fun distinctReferenceUnknownsGeneralizeToObject() {
        // Law: Unknown⊔Unknown across distinct-but-compatible reference types still
        // generalizes the type (spec §4.1 type join) instead of poisoning to Top.
        assertEquals(Value.Unknown(VirtualType.OBJECT), join(Value.Unknown(str), Value.Unknown(sb), hierarchy))
    }

    @Test
    fun theFoldOverThreeOrMoreTypesIsInsensitiveToTheirORDERWithTheHierarchYEngaged() {
        // THE PROPERTY CRITICAL B IS ACTUALLY ABOUT, and the one a 2-ary commutativity test cannot state.
        // `joinAll` is a LEFT FOLD (`ValueJoin.kt`) over a node pile whose order is a scheduling artefact
        // (`ExecutionGraph.consensusOverExploredPaths` maps over `executedPile`), so a register's consensus
        // must not depend on which arm the scheduler happened to run first. Every existing associativity
        // test lands on `Value.Top` and therefore never reaches the hierarchy at all -- so with a
        // hierarchy-aware join this was the one place the determinism story had no witness.
        //
        // Asserted over EVERY permutation, not a representative pair of groupings: with n operands there
        // are n! fold orders and the claim is about all of them.
        fun permutations(values: List<Value>): List<List<Value>> =
            if (values.size <= 1) listOf(values)
            else values.flatMapIndexed { i, v ->
                permutations(values.filterIndexed { j, _ -> j != i }).map { listOf(v) + it }
            }

        fun everyOrderAgrees(expected: Value, operands: List<Value>, expectedOrders: Int) {
            val orders = permutations(operands)
            assertEquals(expectedOrders, orders.size, "the permutation generator produced the wrong count")
            for (order in orders) {
                assertEquals(expected, joinAll(order, hierarchy), "fold order ${order.map { it.type }}")
            }
        }

        val integer = Value.Unknown(VirtualType.of("Ljava/lang/Integer;"))
        val long = Value.Unknown(VirtualType.of("Ljava/lang/Long;"))
        val number = Value.Unknown(num)
        val string = Value.Unknown(str)

        // Three types whose join is a REAL nearest common ancestor, so the hierarchy is engaged in every
        // order rather than short-circuited to Object.
        everyOrderAgrees(Value.Unknown(num), listOf(integer, long, number), expectedOrders = 6)
        // The discriminating case: the INTERMEDIATE values genuinely differ by order -- `Integer ⊔ Long` is
        // `Ljava/lang/Number;` while `Integer ⊔ String` is `Ljava/lang/Object;` -- and the fold's answer
        // must not. Both intermediates are asserted below so this is a stated fact rather than a hope.
        assertEquals(Value.Unknown(num), join(integer, long, hierarchy))
        assertEquals(Value.Unknown(VirtualType.OBJECT), join(integer, string, hierarchy))
        everyOrderAgrees(Value.Unknown(VirtualType.OBJECT), listOf(integer, long, string), expectedOrders = 6)
        // FOUR operands, a three-deep chain, and one type off the chain entirely: 24 orders.
        val child = Value.Unknown(VirtualType.of("Lchild_class;"))
        val parent = Value.Unknown(VirtualType.of("Lparent_class;"))
        val grandparent = Value.Unknown(VirtualType.of("Lgrandparent_class;"))
        everyOrderAgrees(
            Value.Unknown(VirtualType.of("Lgrandparent_class;")),
            listOf(child, parent, grandparent),
            expectedOrders = 6,
        )
        everyOrderAgrees(
            Value.Unknown(VirtualType.OBJECT),
            listOf(child, parent, grandparent, string),
            expectedOrders = 24,
        )
    }

    @Test
    fun joinIsAssociativeOnARepresentativeIncompatibleCase() {
        // Law: associativity. Bug class: join going DOWN the lattice depending on
        // grouping order, which would make a left-to-right joinAll fold order-sensitive.
        // **This case lands on `Value.Top` and therefore never reaches the hierarchy** -- the n-ary,
        // hierarchy-engaged property is pinned by
        // `theFoldOverThreeOrMoreTypesIsInsensitiveToTheirORDERWithTheHierarchYEngaged` above, which is a
        // different claim and not a duplicate of this one.
        val a = Value.Known(HeapRef(1), str)
        val b = Value.Known(HeapRef(2), sb)
        val c = Value.Known(5, int)
        val leftGrouped = join(join(a, b, hierarchy), c, hierarchy)
        val rightGrouped = join(a, join(b, c, hierarchy), hierarchy)
        assertEquals(leftGrouped, rightGrouped)
        assertEquals(Value.Top, leftGrouped)
    }

    @Test
    fun joinAllIsPoisonedByAnIncompatiblePairMidSequence() {
        // Bug class: resurrecting knowledge after Top — a later agreeing Known must not
        // "heal" a fold that already hit an incompatible pair earlier in the sequence.
        assertEquals(
            Value.Top,
            joinAll(listOf(Value.Known(5, int), Value.Known(5L, long), Value.Known(5, int)), hierarchy),
        )
    }

    @Test
    fun anIntZeroJoinedWithAReferenceIsThatReferencesNull() {
        // Dalvik bytecode reference, "General design": `(Object) null == (int) 0`. `const/4 vA, 0` is how
        // bytecode writes null, and ART's verifier types that constant Zero, which `IsReferenceTypes()` admits,
        // so it merges with a reference into that reference.
        val zero = Value.Known(0, int)
        assertEquals(Value.Unknown(str), join(zero, Value.Unknown(str), hierarchy))
        assertEquals(Value.Unknown(str), join(Value.Unknown(str), zero, hierarchy))
        assertEquals(Value.Known(null, str), join(zero, Value.Known(null, str), hierarchy))
        assertEquals(Value.Known(null, str), join(Value.Known(null, str), zero, hierarchy))
        assertEquals(Value.Unknown(str), join(zero, Value.Known(HeapRef(1), str), hierarchy))
    }

    @Test
    fun onlyAKnownIntZeroIsTheNullIdiom() {
        // A non-zero int, an unproven int, and a zero no `const` opcode produces (wide `J`, narrow `B`) are
        // not null; each still disagrees with a reference.
        assertEquals(Value.Top, join(Value.Known(1, int), Value.Unknown(str), hierarchy))
        assertEquals(Value.Top, join(Value.Unknown(int), Value.Unknown(str), hierarchy))
        assertEquals(Value.Top, join(Value.Known(0L, long), Value.Unknown(str), hierarchy))
        assertEquals(Value.Top, join(Value.Known(0.toByte(), byte), Value.Unknown(str), hierarchy))
    }

    @Test
    fun theNullIdiomKeepsTheJoinAssociative() {
        // Two differently tagged zeros meet a reference: both groupings must agree.
        val intZero = Value.Known(0, int)
        val byteZero = Value.Known(0.toByte(), byte)
        val nullString = Value.Known(null, str)
        assertEquals(
            join(join(intZero, byteZero, hierarchy), nullString, hierarchy),
            join(intZero, join(byteZero, nullString, hierarchy), hierarchy),
        )
        assertEquals(
            join(join(nullString, intZero, hierarchy), Value.Known(5, int), hierarchy),
            join(nullString, join(intZero, Value.Known(5, int), hierarchy), hierarchy),
        )
    }
}
