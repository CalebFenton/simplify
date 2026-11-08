package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * The value join behind consensus: paths that disagree must not report a constant, and knowledge lost earlier
 * in a fold must not come back. Oracle: the join table below plus the lattice laws (commutative, idempotent,
 * associative, upper bound).
 */
class ValueJoinTest {
    private val int = VirtualType.of("I")
    private val long = VirtualType.of("J")
    private val byte = VirtualType.of("B")
    private val str = VirtualType.of("Ljava/lang/String;")
    private val sb = VirtualType.of("Ljava/lang/StringBuilder;")
    private val num = VirtualType.of("Ljava/lang/Number;")
    private val integerType = VirtualType.of("Ljava/lang/Integer;")
    private val longType = VirtualType.of("Ljava/lang/Long;")

    /** Passed explicitly because `join` takes no default hierarchy. */
    private val hierarchy: TypeHierarchy = CoreTester.classIndex

    private class Row(val name: String, val a: Value, val b: Value, val expected: Value)

    /**
     * Each row is checked in both operand orders. The int-zero rows follow the Dalvik bytecode reference,
     * "General design": `(Object) null == (int) 0`, so `const/4 vA, 0` is how bytecode writes null and merges
     * with a reference into that reference. Only a `Known` int zero is that idiom.
     */
    @TestFactory
    fun joinTable(): List<DynamicTest> {
        val zero = Value.Known(0, int)
        return listOf(
            Row("agreeing knowns", Value.Known(5, int), Value.Known(5, int), Value.Known(5, int)),
            Row("agreeing references", Value.Known(HeapRef(3), str), Value.Known(HeapRef(3), str), Value.Known(HeapRef(3), str)),
            Row("disagreeing knowns of one type", Value.Known(5, int), Value.Known(6, int), Value.Unknown(int)),
            Row("disagreeing references", Value.Known(HeapRef(1), str), Value.Known(HeapRef(2), sb), Value.Unknown(VirtualType.OBJECT)),
            Row("incompatible knowns", Value.Known(5, int), Value.Known(5L, long), Value.Top),
            Row("incompatible unknowns", Value.Unknown(int), Value.Unknown(long), Value.Top),
            Row("known and unknown", Value.Known(5, int), Value.Unknown(int), Value.Unknown(int)),
            Row("known and unknown reference", Value.Known(HeapRef(1), str), Value.Unknown(sb), Value.Unknown(VirtualType.OBJECT)),
            Row("distinct reference unknowns", Value.Unknown(str), Value.Unknown(sb), Value.Unknown(VirtualType.OBJECT)),
            Row("hierarchy reaches join", Value.Known(HeapRef(1), integerType), Value.Unknown(longType), Value.Unknown(num)),
            Row("top and known", Value.Top, Value.Known(5, int), Value.Top),
            Row("top and unknown", Value.Unknown(int), Value.Top, Value.Top),
            Row("top and top", Value.Top, Value.Top, Value.Top),
            Row("int zero and unknown reference", zero, Value.Unknown(str), Value.Unknown(str)),
            Row("int zero and null", zero, Value.Known(null, str), Value.Known(null, str)),
            Row("int zero and tracked object", zero, Value.Known(HeapRef(1), str), Value.Unknown(str)),
            Row("non-zero int and reference", Value.Known(1, int), Value.Unknown(str), Value.Top),
            Row("unknown int and reference", Value.Unknown(int), Value.Unknown(str), Value.Top),
            // No `const` opcode produces a wide or narrow zero.
            Row("long zero and reference", Value.Known(0L, long), Value.Unknown(str), Value.Top),
            Row("byte zero and reference", Value.Known(0.toByte(), byte), Value.Unknown(str), Value.Top),
        ).map { row ->
            DynamicTest.dynamicTest(row.name) {
                assertEquals(row.expected, join(row.a, row.b, hierarchy))
                assertEquals(row.expected, join(row.b, row.a, hierarchy))
            }
        }
    }

    @Test
    fun joinAllFoldsToConsensus() {
        // One surviving Known means all paths agree.
        assertEquals(Value.Known(5, int), joinAll(listOf(Value.Known(5, int), Value.Known(5, int)), hierarchy))
        assertEquals(Value.Unknown(int), joinAll(listOf(Value.Known(5, int), Value.Known(6, int), Value.Known(5, int)), hierarchy))
        assertEquals(Value.Top, joinAll(listOf(Value.Known(5, int), Value.Known(5L, long)), hierarchy))
        assertEquals(Value.Unknown(num), joinAll(listOf(Value.Unknown(integerType), Value.Unknown(longType)), hierarchy))
        assertNull(joinAll(emptyList(), hierarchy))
    }

    @Test
    fun joinAllIsPoisonedByAnIncompatiblePairMidSequence() {
        // A later agreeing Known must not heal a fold that already reached Top.
        assertEquals(
            Value.Top,
            joinAll(listOf(Value.Known(5, int), Value.Known(5L, long), Value.Known(5, int)), hierarchy),
        )
    }

    @Test
    fun theFoldIsInsensitiveToOperandOrderWithTheHierarchyEngaged() {
        // `joinAll` is a left fold over a node pile whose order is a scheduling artefact, so every permutation
        // must agree. The law tests below use one reference type and never engage the hierarchy; this one does.
        fun permutations(values: List<Value>): List<List<Value>> =
            if (values.size <= 1) listOf(values)
            else values.flatMapIndexed { i, v ->
                permutations(values.filterIndexed { j, _ -> j != i }).map { listOf(v) + it }
            }

        fun everyOrderAgrees(expected: Value, operands: List<Value>) {
            for (order in permutations(operands)) {
                assertEquals(expected, joinAll(order, hierarchy), "fold order ${order.map { it.type }}")
            }
        }

        val integer = Value.Unknown(integerType)
        val long = Value.Unknown(longType)
        val number = Value.Unknown(num)
        val string = Value.Unknown(str)

        everyOrderAgrees(Value.Unknown(num), listOf(integer, long, number))
        // The intermediate joins differ by order (Integer ⊔ Long is Number, Integer ⊔ String is Object); the
        // fold's answer must not.
        assertEquals(Value.Unknown(num), join(integer, long, hierarchy))
        assertEquals(Value.Unknown(VirtualType.OBJECT), join(integer, string, hierarchy))
        everyOrderAgrees(Value.Unknown(VirtualType.OBJECT), listOf(integer, long, string))

        val child = Value.Unknown(VirtualType.of("Lchild_class;"))
        val parent = Value.Unknown(VirtualType.of("Lparent_class;"))
        val grandparent = Value.Unknown(VirtualType.of("Lgrandparent_class;"))
        everyOrderAgrees(grandparent, listOf(child, parent, grandparent))
        everyOrderAgrees(Value.Unknown(VirtualType.OBJECT), listOf(child, parent, grandparent, string))
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

    /**
     * Lattice laws over every pair and triple of a set that reaches each row of the join table: Top, Unknown
     * of a primitive and of a reference type, two int constants (one the null idiom's zero), null, and a
     * tracked object. A non-commutative or non-associative join makes consensus depend on exploration order;
     * a non-idempotent one or one that is not an upper bound changes a value every path agreed on.
     */
    private val lawValues: List<Value> = listOf(
        Value.Top,
        Value.Unknown(int),
        Value.Unknown(str),
        Value.Known(0, int),
        Value.Known(1, int),
        Value.Known(null, str),
        Value.Known(HeapRef(1), str),
    )

    private val lawPairs = lawValues.flatMap { a -> lawValues.map { b -> a to b } }

    @Test
    fun joinIsCommutativeOverEveryPair() {
        val violations = lawPairs.filter { (a, b) -> join(a, b, hierarchy) != join(b, a, hierarchy) }
        assertEquals(emptyList<Pair<Value, Value>>(), violations)
    }

    @Test
    fun joinIsIdempotentOnEveryValue() {
        assertEquals(emptyList<Value>(), lawValues.filter { join(it, it, hierarchy) != it })
    }

    @Test
    fun joinIsAssociativeOverEveryTriple() {
        val triples = lawValues.flatMap { a -> lawValues.flatMap { b -> lawValues.map { c -> Triple(a, b, c) } } }
        val violations = triples.filter { (a, b, c) ->
            join(join(a, b, hierarchy), c, hierarchy) != join(a, join(b, c, hierarchy), hierarchy)
        }
        assertEquals(emptyList<Triple<Value, Value, Value>>(), violations)
    }

    /** Absorption: `a ⊔ b` is an upper bound of `a`, so joining `a` into it again changes nothing. */
    @Test
    fun joinIsAnUpperBoundOfEachOperand() {
        val violations = lawPairs.filter { (a, b) ->
            val joined = join(a, b, hierarchy)
            join(joined, a, hierarchy) != joined
        }
        assertEquals(emptyList<Pair<Value, Value>>(), violations)
    }
}
