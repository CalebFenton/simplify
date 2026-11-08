package org.cf.smalivm.core.op

import org.cf.smalivm.core.AbortReason
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.requireMethodView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * The twelve `if-*` opcodes over `ops/if_test.smali`. Each single-branch fixture branches at 0, falls through
 * to a `nop` at 2 and jumps to `return-void` at 3. Assertions read the branch node's successors, because the
 * set of reached addresses is `{0, 2, 3}` both for a fall-through and for a fork.
 */
class IfOpTest {
    private enum class Branch(val descriptor: String, val zeroForm: Boolean, val holds: (Int, Int) -> Boolean) {
        EQ("ifEqual()V", false, { a, b -> a == b }),
        NE("ifNotEqual()V", false, { a, b -> a != b }),
        LT("ifLessThan()V", false, { a, b -> a < b }),
        GE("ifGreaterOrEqual()V", false, { a, b -> a >= b }),
        GT("ifGreaterThan()V", false, { a, b -> a > b }),
        LE("ifLessOrEqual()V", false, { a, b -> a <= b }),
        EQZ("ifEqualZero()V", true, { a, b -> a == b }),
        NEZ("ifNotEqualZero()V", true, { a, b -> a != b }),
        LTZ("ifLessThanZero()V", true, { a, b -> a < b }),
        GEZ("ifGreaterOrEqualZero()V", true, { a, b -> a >= b }),
        GTZ("ifGreaterThanZero()V", true, { a, b -> a > b }),
        LEZ("ifLessOrEqualZero()V", true, { a, b -> a <= b }),
    }

    /** One register seed, as `CoreState.setRegister` takes it. */
    private data class Seed(val raw: Any?, val type: String) {
        val wide: Boolean get() = type == "J" || type == "D"

        override fun toString(): String = "$raw:$type"
    }

    private data class Case(val branch: Branch, val lhs: Seed, val rhs: Seed?, val expected: List<Int>) {
        fun run(): DynamicTest = DynamicTest.dynamicTest("$branch $lhs${rhs?.let { ", $it" } ?: ""} -> $expected") {
            assertEquals(expected, successorsOfBranch(branch, lhs, rhs))
        }
    }

    /**
     * [BC]: every `if` compares raw 32-bit register contents, signed, so each expected arm is Kotlin's `Int`
     * comparison of [bits]. `Z`/`B`/`S`/`C`/`I` share one 32-bit register domain. A `J` raw is compared
     * through the low half `if-eq` reads, which only verifier-rejected input can produce. The `1000` row
     * guards against deciding equality by box identity, since `Integer.valueOf` caches only -128..127.
     */
    @TestFactory
    fun integralOperandsCompareAsSignedInts(): List<DynamicTest> {
        val twoRegister = Branch.entries.filterNot { it.zeroForm }.flatMap { branch ->
            listOf(0 to 0, 0 to 1, 1 to 0).map { (a, b) -> Triple(branch, Seed(a, "I"), Seed(b, "I")) }
        }
        val zeroForm = Branch.entries.filter { it.zeroForm }.flatMap { branch ->
            listOf(-1, 0, 1).map { a -> Triple<Branch, Seed, Seed?>(branch, Seed(a, "I"), null) }
        }
        val one = 1.toByte()
        val mixedWidths = listOf(
            Seed(true, "Z") to Seed(1.toChar(), "C"),
            Seed(one, "B") to Seed(true, "Z"),
            Seed(one, "B") to Seed(1.toChar(), "C"),
            Seed(one, "B") to Seed(1.toShort(), "S"),
            Seed(1, "I") to Seed(true, "Z"),
            Seed(1, "I") to Seed(one, "B"),
            Seed(1, "I") to Seed(1.toChar(), "C"),
            Seed(1, "I") to Seed(1.toShort(), "S"),
            Seed(1.toShort(), "S") to Seed(true, "Z"),
            Seed(1.toShort(), "S") to Seed(1.toChar(), "C"),
            Seed(one, "B") to Seed(1L, "J"),
            Seed(1, "I") to Seed(1L, "J"),
            Seed(1L, "J") to Seed(true, "Z"),
            Seed(1L, "J") to Seed(1.toChar(), "C"),
            Seed(1.toShort(), "S") to Seed(1L, "J"),
            Seed(1000, "I") to Seed(1000, "I"),
        ).map { (lhs, rhs) -> Triple(Branch.EQ, lhs, rhs) }
        val zeroFormWidths = listOf(
            Branch.EQZ to Seed(false, "Z"),
            Branch.NEZ to Seed(true, "Z"),
            Branch.EQZ to Seed(0.toByte(), "B"),
            Branch.NEZ to Seed(7.toByte(), "B"),
            Branch.EQZ to Seed(null, "I"),
        ).map { (branch, lhs) -> Triple<Branch, Seed, Seed?>(branch, lhs, null) }

        return (twoRegister + zeroForm + mixedWidths + zeroFormWidths).map { (branch, lhs, rhs) ->
            val taken = branch.holds(bits(lhs.raw), rhs?.let { bits(it.raw) } ?: 0)
            Case(branch, lhs, rhs, if (taken) TAKEN else NOT_TAKEN).run()
        }
    }

    /**
     * [BC]: "`(Object) null == (int) 0`". So a null reference and an integral zero have equal bits, a live
     * pointer is never zero, and two registers holding one pointer hold equal bits under every comparison.
     * Pointer equality is identity, not `equals`: the two-string row holds equal content in distinct objects.
     */
    @TestFactory
    fun referencesCompareByIdentityAndNullIsZero(): List<DynamicTest> {
        val shared = "one object, two registers"
        val live = Seed("live", "Ljava/lang/String;")
        val nullRef = Seed(null, "Ljava/lang/String;")
        val zero = Seed(0, "I")
        val identical = Branch.entries.filterNot { it.zeroForm }.map { branch ->
            val seed = Seed(shared, "Ljava/lang/String;")
            Case(branch, seed, seed, if (branch.holds(0, 0)) TAKEN else NOT_TAKEN)
        }
        val decided = listOf(
            Case(Branch.NE, Seed("object string", "Ljava/lang/String;"), Seed(IntArray(0), "[I"), TAKEN),
            Case(
                Branch.NE, Seed("object string", "Ljava/lang/String;"),
                Seed(StringBuilder("object string").toString(), "Ljava/lang/String;"), TAKEN,
            ),
            Case(Branch.NEZ, Seed("object string", "Ljava/lang/String;"), null, TAKEN),
            Case(Branch.NEZ, Seed(byteArrayOf(0x1), "[B"), null, TAKEN),
            Case(Branch.EQ, nullRef, zero, TAKEN),
            Case(Branch.EQ, zero, nullRef, TAKEN),
            Case(Branch.NE, zero, live, TAKEN),
            Case(Branch.NE, live, zero, TAKEN),
            Case(Branch.NE, nullRef, live, TAKEN),
            Case(Branch.NE, live, nullRef, TAKEN),
        )
        return (identical + decided).map { it.run() }
    }

    /**
     * An unprovable predicate explores both arms, fall-through first. The rows are the engine choices in
     * `docs/DALVIK-SEMANTICS.md`'s `if-*` section: an unknown, a `Float`/`Double` raw (a host box's bits need
     * not be the register's), an ordering over distinct pointers, and a pointer against a non-zero integer.
     * Only the unknown row passes ART's verifier.
     */
    @TestFactory
    fun unprovableOperandsForkFallThroughFirst(): List<DynamicTest> {
        val one = 1.toByte()
        val floatRaws = listOf(
            Seed(one, "B") to Seed(1.0, "D"),
            Seed(one, "B") to Seed(1f, "F"),
            Seed(1.0, "D") to Seed(true, "Z"),
            Seed(1.0, "D") to Seed(1.toChar(), "C"),
            Seed(1f, "F") to Seed(true, "Z"),
            Seed(1f, "F") to Seed(1.toChar(), "C"),
            Seed(3.2f, "F") to Seed(3.2, "D"),
            Seed(1, "I") to Seed(1.0, "D"),
            Seed(1, "I") to Seed(1f, "F"),
            Seed(1L, "J") to Seed(1.0, "D"),
            Seed(1L, "J") to Seed(1f, "F"),
            Seed(1.toShort(), "S") to Seed(1.0, "D"),
            Seed(1.toShort(), "S") to Seed(1f, "F"),
            Seed(1f, "F") to Seed(1f, "F"),
        ).map { (lhs, rhs) -> Case(Branch.EQ, lhs, rhs, FORK) }
        val pointers = listOf(
            Case(
                Branch.LT, Seed("a", "Ljava/lang/String;"),
                Seed(StringBuilder("b").toString(), "Ljava/lang/String;"), FORK,
            ),
            Case(Branch.EQ, Seed("a", "Ljava/lang/String;"), Seed(5, "I"), FORK),
        )
        val unknown = Case(Branch.EQZ, Seed(CoreState.UNKNOWN, "I"), null, FORK)
        return (floatRaws + pointers + unknown).map { it.run() }
    }

    /**
     * `ExecutionGraph.availableRegisters` offers as scratch every register no op reads or assigns, so an
     * `if` must name the registers it compares. The `21t` form's right-hand side is the literal zero.
     */
    @Test
    fun branchesReadTheirOperandRegistersAndAssignNone() {
        val twoRegister = CoreTester.execute(CLASS_NAME, "ifEqual()V", CoreState().apply {
            setRegisters(0, 1, "I", 1, 1, "I")
        })
        assertEquals(setOf(0, 1), twoRegister.registersRead(ADDRESS_IF))
        assertEquals(emptySet<Int>(), twoRegister.registersAssigned(ADDRESS_IF))

        val compareToZero = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", CoreState().apply {
            setRegisters(0, 1, "I")
        })
        assertEquals(setOf(0), compareToZero.registersRead(ADDRESS_IF))
        assertEquals(emptySet<Int>(), compareToZero.registersAssigned(ADDRESS_IF))
    }

    /**
     * A branch writes no register and touches nothing outside its frame. `methodSideEffectLevel` is the
     * maximum over addresses, so a spurious effect on a branch would block folding in every method with one.
     */
    @Test
    fun decidedForkedAndPointerBranchesDeclareNoSideEffect() {
        val definite = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", CoreState().apply {
            setRegisters(0, 0, "I")
        })
        assertEquals(SideEffectLevel.NONE, definite.explored.sideEffectLevel(ADDRESS_IF))
        val trusted = definite.trusted()
        assertNotNull(trusted)
        assertEquals(SideEffectLevel.NONE, trusted!!.methodSideEffectLevel())

        val forked = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", CoreState().apply {
            setRegister(0, Value.Unknown(INT))
        })
        assertEquals(SideEffectLevel.NONE, forked.explored.sideEffectLevel(ADDRESS_IF))

        val reference = CoreTester.execute(CLASS_NAME, "ifNotEqualZero()V", CoreState().apply {
            setRegisters(0, byteArrayOf(0x1), "[B")
        })
        assertEquals(SideEffectLevel.NONE, reference.explored.sideEffectLevel(ADDRESS_IF))
    }

    /**
     * `if-eqz v1, :next` whose target is the next instruction: both transitions name address 2, and each
     * builds its own node with its own frame copy.
     */
    @Test
    fun aBranchWhoseTargetIsItsFallThroughBuildsTwoNodesAtThatOneAddress() {
        val graph = CoreTester.execute(CLASS_NAME, "ifEqualZeroToFallThrough(I)V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(2, 2), graph.nodePile(0).single().children.map { it.address })
        assertEquals(2, graph.nodePile(2).size)
        assertEquals(2, graph.outcomes.size)
        assertTrue(
            graph.nodePile(2).let { it[0] !== it[1] && it[0].frame !== it[1].frame },
            "forked siblings must not share a mutable frame",
        )
    }

    /**
     * A consumer may fold a value only when every explored path agrees and exploration completed.
     * `ifEqualZeroSkippingAnAssignment` assigns v0 on one arm only; `ifEqualZeroAssigningOneOnBothArms`
     * assigns `1` on both.
     */
    @Test
    fun aForkIsReconciledByPerAddressConsensusAndNotByEitherArm() {
        val disagree = CoreTester.execute(CLASS_NAME, "ifEqualZeroSkippingAnAssignment(I)V")
        assertEquals(CompletionStatus.Complete, disagree.completion())
        assertEquals(2, disagree.nodePile(3).size)
        assertEquals(setOf(Value.Known(1, INT), Value.Top), disagree.explored.registerValues(3, 0))
        assertEquals(Value.Top, disagree.explored.consensus(3, 0))
        assertEquals(Value.Top, disagree.trusted()!!.terminatingConsensus(0))

        val agree = CoreTester.execute(CLASS_NAME, "ifEqualZeroAssigningOneOnBothArms(I)V")
        assertEquals(CompletionStatus.Complete, agree.completion())
        assertEquals(Value.Known(1, INT), agree.trusted()!!.terminatingConsensus(0))
    }

    /**
     * Address visits are counted per path, so 2^9 sibling paths reaching one join under the default
     * 500-visit limit are not a loop.
     */
    @Test
    fun nineUnknownDiamondsKeepAllLoopFreeSiblingPaths() {
        val graph = CoreTester.execute(CLASS_NAME, "nineUnknownDiamonds(I)V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertNotNull(graph.trusted())
        assertEquals(512, graph.outcomes.size)
        assertEquals(0, graph.outcomes.count { it == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) })
        assertEquals(512, graph.nodePile(27).size, "every path reaches the final return-void")
    }

    /**
     * [VER], on monitor verification: "the result values from two `const-class` instructions on the same
     * class will be the same reference". So the `if-eq` at 4 is taken, and the `nop` at 6 is dead on a
     * completed run.
     */
    @Test
    fun twoConstClassResultsForOneClassAreOneReference() {
        val graph = CoreTester.execute(CLASS_NAME, "ifEqualTwoConstClassesOfOneClass()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(7), graph.nodePile(4).single().children.map { it.address })
        assertFalse(graph.wasReached(6))
    }

    /**
     * Every pair of lattice representatives under all six comparisons, executed on the op directly. The
     * verdict matrix states which decision row applies to each pair, so a new or widened row that shadows
     * another changes a cell. The verdict-to-arms expansion is written out rather than computed through
     * [IfComparison.holdsFor]. Representatives: a null reference and an integral zero (equal bits, [BC]),
     * a non-zero integer, one host object under two tags (tags are ignored), a distinct object with equal
     * content (identity, not `equals`), one heap id under two tags, another heap id, a `Float` raw, an unknown.
     */
    @Test
    fun everyPairOfLatticeRepresentativesDecidesTheSameWayForAllSixComparisons() {
        val sameHostObject = StringBuilder("content").toString()
        val equalContentButDistinct = StringBuilder("content").toString()
        val string = VirtualType.of("Ljava/lang/String;")
        val obj = VirtualType.of("Ljava/lang/Object;")
        val representatives = listOf(
            "NULLREF" to Value.Known(null, string),
            "ZERO" to Value.Known(0, INT),
            "ONE" to Value.Known(1, INT),
            "STR_A" to Value.Known(sameHostObject, string),
            "STR_A_OTHER_TAG" to Value.Known(sameHostObject, obj),
            "STR_B_EQUAL_CONTENT" to Value.Known(equalContentButDistinct, string),
            "HEAP1" to Value.Known(HeapRef(1), VirtualType.of("[B")),
            "HEAP1_OTHER_TAG" to Value.Known(HeapRef(1), obj),
            "HEAP2" to Value.Known(HeapRef(2), VirtualType.of("[B")),
            "FLOAT" to Value.Known(1f, VirtualType.of("F")),
            "UNKNOWN" to Value.Unknown(INT),
        )
        // E = equal (cmp 0)   N = unequal but unordered   L = less   G = greater   F = unprovable, fork
        //           NULLREF ZERO ONE STR_A A_TAG STR_B HEAP1 H1_TAG HEAP2 FLOAT UNKNOWN
        val matrix = listOf(
            /* NULLREF         */ "EELNNNNNNFF",
            /* ZERO            */ "EELNNNNNNFF",
            /* ONE             */ "GGEFFFFFFFF",
            /* STR_A           */ "NNFEENNNNFF",
            /* STR_A_OTHER_TAG */ "NNFEENNNNFF",
            /* STR_B_EQUAL     */ "NNFNNENNNFF",
            /* HEAP1           */ "NNFNNNEENFF",
            /* HEAP1_OTHER_TAG */ "NNFNNNEENFF",
            /* HEAP2           */ "NNFNNNNNEFF",
            /* FLOAT           */ "FFFFFFFFFFF",
            /* UNKNOWN         */ "FFFFFFFFFFF",
        )
        require(matrix.size == representatives.size && matrix.all { it.length == representatives.size })

        val target = ADDRESS_RETURN
        val jump = listOf(OpArm.Jump(target))
        val fallThrough = listOf(OpArm.Continue())
        val fork = listOf(OpArm.Continue(), OpArm.Jump(target))
        val expansion = mapOf(
            'E' to mapOf(
                IfComparison.EQ to jump, IfComparison.NE to fallThrough,
                IfComparison.LT to fallThrough, IfComparison.GE to jump,
                IfComparison.GT to fallThrough, IfComparison.LE to jump,
            ),
            'N' to mapOf(
                IfComparison.EQ to fallThrough, IfComparison.NE to jump,
                IfComparison.LT to fork, IfComparison.GE to fork,
                IfComparison.GT to fork, IfComparison.LE to fork,
            ),
            'L' to mapOf(
                IfComparison.EQ to fallThrough, IfComparison.NE to jump,
                IfComparison.LT to jump, IfComparison.GE to fallThrough,
                IfComparison.GT to fallThrough, IfComparison.LE to jump,
            ),
            'G' to mapOf(
                IfComparison.EQ to fallThrough, IfComparison.NE to jump,
                IfComparison.LT to fallThrough, IfComparison.GE to jump,
                IfComparison.GT to jump, IfComparison.LE to fallThrough,
            ),
            'F' to IfComparison.entries.associateWith { fork },
        )

        val view = CoreTester.classIndex.requireMethodView(CLASS_NAME, "ifEqual()V")
        for ((lhsIndex, lhsEntry) in representatives.withIndex()) {
            for ((rhsIndex, rhsEntry) in representatives.withIndex()) {
                val verdict = matrix[lhsIndex][rhsIndex]
                val expectedPerComparison = expansion.getValue(verdict)
                for (comparison in IfComparison.entries) {
                    val frame = Frame.entryFrame(view)
                    frame.assign(0, lhsEntry.second)
                    frame.assign(1, rhsEntry.second)
                    val op = IfOp(
                        address = ADDRESS_IF, name = "if-${comparison.name.lowercase()}",
                        comparison = comparison, targetAddress = target, lhsRegister = 0, rhsRegister = 1,
                    )
                    assertEquals(
                        expectedPerComparison.getValue(comparison), op.execute(frame).arms,
                        "${lhsEntry.first} ${comparison.name} ${rhsEntry.first} (verdict '$verdict')",
                    )
                }
            }
        }
    }

    companion object {
        private const val CLASS_NAME = "Lif_test;"
        private const val ADDRESS_IF = 0
        private const val ADDRESS_NOP = 2
        private const val ADDRESS_RETURN = 3

        private val TAKEN = listOf(ADDRESS_RETURN)
        private val NOT_TAKEN = listOf(ADDRESS_NOP)
        private val FORK = listOf(ADDRESS_NOP, ADDRESS_RETURN)

        private val INT = VirtualType.of("I")

        /** The 32 bits a device register holds for an integral [raw]: `Z` is 0 or 1, a `J` its low half, null 0. */
        private fun bits(raw: Any?): Int = when (raw) {
            null -> 0
            is Boolean -> if (raw) 1 else 0
            is Char -> raw.code
            is Number -> raw.toInt()
            else -> error("not an integral raw: $raw")
        }

        /**
         * The addresses the branch node's transitions name, on a completed run. A `J`/`D` seed occupies a
         * register pair, so wide rows run `ifEqualWithRoomForWidePairs()V` (`if-eq v0, v2`, same addresses).
         */
        private fun successorsOfBranch(branch: Branch, lhs: Seed, rhs: Seed?): List<Int> {
            require(branch.zeroForm == (rhs == null))
            val wide = lhs.wide || rhs?.wide == true
            require(!wide || branch == Branch.EQ) { "only if-eq has a wide-pair fixture" }
            val descriptor = if (wide) "ifEqualWithRoomForWidePairs()V" else branch.descriptor
            val initial = CoreState().apply {
                setRegister(0, lhs.raw, lhs.type)
                if (rhs != null) setRegister(if (wide) 2 else 1, rhs.raw, rhs.type)
            }
            val graph = CoreTester.execute(CLASS_NAME, descriptor, initial)
            assertEquals(CompletionStatus.Complete, graph.completion())
            return graph.nodePile(ADDRESS_IF).single().children.map { it.address }
        }
    }
}
