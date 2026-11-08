package org.cf.smalivm.core.op

import org.cf.smalivm.core.ClassInitState
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.CoreTester.test
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.ExecutionNode
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * `sget*` (Plan 9 Task 18): a static field read into a register, through per-path class storage
 * ([org.cf.smalivm.core.ClassInitStore]), triggering the declaring class's initialization first --
 * MEASURED for `sget` (`Fx_StaticFieldDoesInitialize`, `[DEVICE]` both tiers; `SGetOp.initialize`'s
 * KDoc carries the full citation). Migrated from `org.cf.smalivm.opcode.SGetOpTest` -- all 27 tests --
 * plus the cases the corpus never covered: the primitive-tagged reference read (`readsAsNullReference`,
 * CARRY-FORWARD Plan 9 item 13), the missing-FIELD resolution failure, the inherited-static walk, and
 * the op's side-effect claims.
 *
 * **Migration fidelity: 26 of 27 expectations are byte-identical.** The calibration that made that
 * checkable in advance: `sget_test.smali` declares NO `<clinit>`, so adjudication 5 (the oracle
 * discarding dex literals for a class *with* one) moves nothing in this file -- the oracle seeds
 * literal initializers for a no-`<clinit>` class exactly as the core's JVMS §5.4.2 preparation does,
 * which is why every `*Literal` and `*Uninitialized` expectation ports verbatim (the `Uninitialized`
 * trio's type defaults are `Known`, not `Unknown`: after initialization an unassigned static is
 * provably its default). The ONE re-expression is [staticUnknownClassFieldThrowsException], under a
 * named `[DEVICE]` authority -- see its comment. Where adjudication 5 DOES bite is pinned
 * differentially instead: `Fixtures.intendedDivergences`' `statics-literal-untouched-by-clinit`.
 *
 * **Assertions go through `CoreTester.test`'s TERMINATING consensus, not per-address reads at the
 * `sget` address, and that is load-bearing:** the class-init trigger re-executes the op at its own
 * address, so the first pass contributes `Value.Top` to `consensusOverExploredPaths(address, register)`
 * (`ExecutionSession.planInitClass`'s ⚠️ note). Terminating consensus reads returning nodes only and is
 * what the oracle's `Tester.test` asserted through, so the migration is faithful.
 */
class SGetOpTest {
    private lateinit var expected: CoreState
    private lateinit var initial: CoreState

    @BeforeEach
    fun setUp() {
        expected = CoreState()
        initial = CoreState()
    }

    @Test
    fun staticBoolean() {
        initial.setFields(CLASS_NAME, "myBoolean:Z", true)
        expected.setRegisters(0, true, "Z")
        test(CLASS_NAME, "getStaticBoolean()V", initial, expected)
    }

    @Test
    fun staticBooleanLiteral() {
        expected.setRegisters(0, true, "Z")
        test(CLASS_NAME, "getStaticBooleanLiteral()V", initial, expected)
    }

    @Test
    fun staticBooleanUninitialized() {
        expected.setRegisters(0, false, "Z")
        test(CLASS_NAME, "getStaticBoolean()V", initial, expected)
    }

    @Test
    fun staticByte() {
        initial.setFields(CLASS_NAME, "myByte:B", 0xf.toByte())
        expected.setRegisters(0, 0xf.toByte(), "B")
        test(CLASS_NAME, "getStaticByte()V", initial, expected)
    }

    @Test
    fun staticByteLiteral() {
        expected.setRegisters(0, 0xf.toByte(), "B")
        test(CLASS_NAME, "getStaticByteLiteral()V", initial, expected)
    }

    @Test
    fun staticByteUninitialized() {
        expected.setRegisters(0, 0.toByte(), "B")
        test(CLASS_NAME, "getStaticByte()V", initial, expected)
    }

    @Test
    fun staticChar() {
        initial.setFields(CLASS_NAME, "myChar:C", 'x')
        expected.setRegisters(0, 'x', "C")
        test(CLASS_NAME, "getStaticChar()V", initial, expected)
    }

    @Test
    fun staticCharLiteral() {
        expected.setRegisters(0, 'x', "C")
        test(CLASS_NAME, "getStaticCharLiteral()V", initial, expected)
    }

    @Test
    fun staticCharUninitialized() {
        expected.setRegisters(0, '\u0000', "C")
        test(CLASS_NAME, "getStaticChar()V", initial, expected)
    }

    @Test
    fun staticDoubleLiteral() {
        expected.setRegisters(0, 10000000000.0, "D")
        test(CLASS_NAME, "getStaticDoubleLiteral()V", initial, expected)
    }

    @Test
    fun staticDoubleUninitialized() {
        expected.setRegisters(0, 0.0, "D")
        test(CLASS_NAME, "getStaticDouble()V", initial, expected)
    }

    @Test
    fun staticFloatLiteral() {
        // The fixture spells this `sget-wide` naming the narrow `F` field -- assemblable, and kept
        // verbatim from the corpus. The value ports byte-identically BECAUSE the op takes its width
        // from the resolved FIELD's type rather than the mnemonic (`SGetOp.registersAssigned`'s KDoc);
        // trusting the mnemonic would reserve a wide pair the `.locals 1` frame does not have.
        expected.setRegisters(0, 1.1f, "F")
        test(CLASS_NAME, "getStaticFloatLiteral()V", initial, expected)
    }

    @Test
    fun staticFloatUninitialized() {
        expected.setRegisters(0, 0f, "F")
        test(CLASS_NAME, "getStaticFloat()V", initial, expected)
    }

    @Test
    fun staticInt() {
        initial.setFields(CLASS_NAME, "myInt:I", 0x42)
        expected.setRegisters(0, 0x42, "I")
        test(CLASS_NAME, "getStaticInt()V", initial, expected)
    }

    @Test
    fun staticIntLiteral() {
        expected.setRegisters(0, 0x42, "I")
        test(CLASS_NAME, "getStaticIntLiteral()V", initial, expected)
    }

    @Test
    fun staticIntUninitialized() {
        expected.setRegisters(0, 0, "I")
        test(CLASS_NAME, "getStaticInt()V", initial, expected)
    }

    @Test
    fun staticLong() {
        initial.setFields(CLASS_NAME, "myLong:J", 0xFFFFFFFFFL)
        expected.setRegisters(0, 0xFFFFFFFFFL, "J")
        test(CLASS_NAME, "getStaticLong()V", initial, expected)
    }

    @Test
    fun staticLongLiteral() {
        expected.setRegisters(0, 0xFFFFFFFFFL, "J")
        test(CLASS_NAME, "getStaticLongLiteral()V", initial, expected)
    }

    @Test
    fun staticLongUninitialized() {
        expected.setRegisters(0, 0L, "J")
        test(CLASS_NAME, "getStaticLong()V", initial, expected)
    }

    @Test
    fun staticShort() {
        initial.setFields(CLASS_NAME, "myShort:S", 0x100.toShort())
        expected.setRegisters(0, 0x100.toShort(), "S")
        test(CLASS_NAME, "getStaticShort()V", initial, expected)
    }

    @Test
    fun staticShortLiteral() {
        expected.setRegisters(0, 0x100.toShort(), "S")
        test(CLASS_NAME, "getStaticShortLiteral()V", initial, expected)
    }

    @Test
    fun staticShortUninitialized() {
        expected.setRegisters(0, 0.toShort(), "S")
        test(CLASS_NAME, "getStaticShort()V", initial, expected)
    }

    @Test
    fun staticString() {
        initial.setFields(CLASS_NAME, "myString:Ljava/lang/String;", "They tried and died.")
        expected.setRegisters(0, "They tried and died.", "Ljava/lang/String;")
        test(CLASS_NAME, "getStaticString()V", initial, expected)
    }

    @Test
    fun staticStringLiteral() {
        expected.setRegisters(0, "life, what's life?", "Ljava/lang/String;")
        test(CLASS_NAME, "getStaticStringLiteral()V", initial, expected)
    }

    @Test
    fun staticStringUninitialized() {
        expected.setRegisters(0, null, "Ljava/lang/String;")
        test(CLASS_NAME, "getStaticString()V", initial, expected)
    }

    /**
     * 🔴 **The one NON-byte-identical migration in this file, re-expressed under a named authority.**
     * The old engine asserted its own HOST crash at op-BUILD time for an absent class, killing the whole
     * run for input a deobfuscator's diet routinely contains (plan adjudication
     * 7). The device answer is a definite guest `NoClassDefFoundError` on the failing path alone:
     * `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)`, fixture `Fx_SGetAbsentType` against
     * `Fx_SGetPresentType` -- the same ROW-23 family ruling that re-expressed `const-class`/
     * `instance-of`/`check-cast`/`new-instance`'s absent-type rows in Tasks 15/17, and the authority
     * `StaticFieldLookup.NoSuchClass`'s KDoc records. The message is the binary base name
     * (`NewArrayOp`'s shape) -- asserted only so it cannot drift silently, never as fidelity.
     */
    @Test
    fun staticUnknownClassFieldThrowsException() {
        CoreTester.testThrows(
            CLASS_NAME, "getStaticUnknownClassField()V",
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "some.unknown.classzzzzz",
        )
        // The negative halves, `NewInstanceOpTest.anAbsentClassThrowsNoClassDefFoundErrorWithoutFallingThrough`'s
        // shape: the fall-through is only provably unexplored on a run that explored everything, and a
        // throwing sget leaves its destination untouched. `sget` is format 21c = 2 code units.
        val graph = CoreTester.execute(CLASS_NAME, "getStaticUnknownClassField()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(2), "a definite throw must not also explore the fall-through")
        assertTrue(
            graph.nodePile(0).first().frame.peek(0) == null,
            "a throwing sget must leave its destination register untouched",
        )
    }

    @Test
    fun staticWhitelistedClassField() {
        // Byte-identical: `Ljava/lang/Integer;` is a framework class whose `<clinit>` this build
        // declines to run (resolved decision 7), and MAX_VALUE is recoverable anyway because the dex
        // encodes it as a literal -- decision 7's "cheap part", `ClassInitPlanner.prepare` row 1. The
        // STRONG that the skipped initializer costs is pinned by [sideEffectsAreTheSchedulersForASkippedInitializerNeverTheOps].
        expected.setRegisters(0, Int.MAX_VALUE, "I")
        test(CLASS_NAME, "getStaticWhitelistedClassField()V", expected)
    }

    // ------------------------------------------------------------------------------------------
    // Beyond the corpus: the cases the oracle could not express or never covered.
    // ------------------------------------------------------------------------------------------

    /**
     * **The dropped-NPE class Plan 7 shipped four times, pinned for `sget-object` specifically** (the
     * plan's mutate-to-prove row, and MB5-MB8's per-op isolation: `SPutOpTest` pins its own call site
     * separately). A static cell holds bits; `sget-object` fetches them AS A REFERENCE, so a
     * primitive-tagged `Known(5, "I")` in a reference-typed field reads as `null` -- keyed on
     * [org.cf.smalivm.core.readsAsNullReference], whose non-zero row is **VERIFIER** tier (ART's two
     * interpreter tiers contradict each other there; `SGetOp`'s KDoc carries the table). Replacing the
     * predicate with `raw == null` in `SGetOp.execute` makes this read back `Known(5, "I")` and this
     * test go red; nothing else in this file can see that mutation, because every corpus seed is
     * correctly typed.
     */
    @Test
    fun aPrimitiveTaggedCellReadsAsNullThroughSGetObject() {
        initial.setField(CLASS_NAME, "myString:Ljava/lang/String;", Value.Known(5, VirtualType.of("I")))
        expected.setRegisters(0, null, "Ljava/lang/String;")
        test(CLASS_NAME, "getStaticString()V", initial, expected)
    }

    /**
     * The class resolves and declares no such field, anywhere in its superclass chain: a guest
     * `NoSuchFieldError` -- INHERITED tier ([JVMS] §5.4.3.2 via §5.4.3.1; `getstatic`'s Linking
     * Exceptions clause), NOT measured, and `noSuchFieldFailure`'s KDoc says so rather than borrowing
     * the method twin's measurement. What it replaces is the oracle's `getField(name)!!` host NPE at
     * op-build time (`opcode/SGetOp.kt:52`). The throwable CLASS is the whole assertion -- the message
     * is CORPUS tier at best (`ResolutionFailure.kt`'s standing rule), so no message is asserted.
     */
    @Test
    fun aFieldTheClassDoesNotDeclareThrowsNoSuchFieldError() {
        CoreTester.testThrows(
            CLASS_NAME, "getStaticMissingField()V",
            exceptionType = "Ljava/lang/NoSuchFieldError;",
        )
        val graph = CoreTester.execute(CLASS_NAME, "getStaticMissingField()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(2), "a definite throw must not also explore the fall-through")
    }

    /**
     * A static declared in an ancestor, referenced through a descendant -- legal dex javac never
     * emits, and the superclass walk in `ClassIndex.resolveStaticField` is the only thing that can
     * answer it. Three claims, each a distinct wrong answer if it moves:
     *  1. the VALUE is the parent `<clinit>`'s write (1), so the walk found the declaring class;
     *  2. the CELL is keyed by the parent's descriptor (`StaticFieldLookup.Resolved.descriptor` is the
     *     declaring class's), so two instructions naming the same inherited static through different
     *     subclasses read one cell -- asserted through the field consensus under the PARENT's key;
     *  3. **the class initialized is the DECLARING one and only it** ([JVMS] §5.5; `SGetOp.initialize`'s
     *     KDoc): the parent's `<clinit>` ran, the child's did NOT, and the child is still
     *     `UNINITIALIZED` at return.
     */
    @Test
    fun anInheritedStaticResolvesToTheDeclaringClassesCellAndInitializesOnlyThatClass() {
        expected.setRegisters(0, 1, "I")
        expected.setFields(PARENT, "parentTouched:I", 1)
        test(CHILD, "getParentTouchedThroughChildReference()I", initial, expected)

        val graph = CoreTester.execute(CHILD, "getParentTouchedThroughChildReference()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(PARENT), initializersRun(graph),
            "exactly the DECLARING class's <clinit> ran -- the referenced subclass's must not",
        )
        assertEquals(ClassInitState.INITIALIZED, initStateAtReturn(graph, VirtualType.of(PARENT)))
        assertEquals(
            ClassInitState.UNINITIALIZED, initStateAtReturn(graph, VirtualType.of(CHILD)),
            "[JVMS] §5.5: a getstatic initializes the class that DECLARES the field, never the one the " +
                "instruction named",
        )
    }

    /**
     * `sget` declares NO side effect of its own -- `SGetOp` declares no effect on any arm, pinned
     * rather than assumed (the `FilledNewArrayOp`/`ThrowOp` precedent) -- and the STRONG a framework
     * read carries is the SCHEDULER's declaration for the skipped `Integer.<clinit>`
     * (`ExecutionSession.declareSkippedInitializerEffect`), not the op's. Both halves in one test
     * because the pair is the claim: an op-level effect would make BOTH methods non-NONE, and a lost
     * scheduler declaration would make both NONE.
     */
    @Test
    fun sideEffectsAreTheSchedulersForASkippedInitializerNeverTheOps() {
        val plain = CoreTester.execute(CLASS_NAME, "getStaticIntLiteral()V")
        assertEquals(CompletionStatus.Complete, plain.completion())
        assertEquals(
            SideEffectLevel.NONE, plain.methodSideEffectLevelOverExploredPaths(),
            "a read of an app class's static perturbs nothing -- NONE, or every folding pass stops",
        )
        val framework = CoreTester.execute(CLASS_NAME, "getStaticWhitelistedClassField()V")
        assertEquals(CompletionStatus.Complete, framework.completion())
        assertEquals(
            SideEffectLevel.STRONG, framework.methodSideEffectLevelOverExploredPaths(),
            "Integer.<clinit> exists and was NOT run (resolved decision 7), so whatever it would have " +
                "written is unmodelled state -- the trigger address is STRONG, declared by the scheduler",
        )
    }

    /**
     * The width of the assignment comes from the resolved FIELD's declared type, never from the
     * mnemonic -- `SGetOp.registersAssigned`'s deliberate inversion of `AGetOp`'s choice, pinned on
     * all three shapes: a genuinely wide field ({0, 1}), a narrow one ({0}), and the corpus's own
     * assemblable mismatch (`getStaticFloat()V` is `sget-wide` naming the narrow `F` field in a
     * `.locals 1` frame, where trusting the mnemonic would declare a register the method does not
     * have). This is also the pin on the `this.field` backing-field fix in that accessor: a property
     * accessor reading Kotlin's `field` keyword instead of the constructor parameter does not compile,
     * but a rewrite that dropped the qualifier AND the resolution would default every read to narrow
     * and die on the first row here.
     */
    @Test
    fun theAssignmentWidthComesFromTheFieldNotTheMnemonic() {
        val wide = CoreTester.execute(CLASS_NAME, "getStaticLong()V")
        assertEquals(setOf(0, 1), wide.registersAssigned(0), "a J field's read owns the pair")
        val narrow = CoreTester.execute(CLASS_NAME, "getStaticInt()V")
        assertEquals(setOf(0), narrow.registersAssigned(0), "an I field's read owns one register")
        val mismatch = CoreTester.execute(CLASS_NAME, "getStaticFloat()V")
        assertEquals(
            setOf(0), mismatch.registersAssigned(0),
            "sget-wide naming a narrow F field declares the FIELD's width -- the mnemonic is a " +
                "redundant re-encoding of a fact the resolved field already carries exactly",
        )
    }

    // ------------------------------------------------------------------------------------------
    // Harness -- the minimal subset of ClassInitTest's activation-walking helpers, the same copies
    // NewInstanceOpTest carries and for the same reason: a graph describes ONE activation by design,
    // so a <clinit>'s nodes are reachable only through ExecutionNode.children.
    // ------------------------------------------------------------------------------------------

    private fun allNodes(graph: ExecutionGraph): List<ExecutionNode> {
        val out = mutableListOf<ExecutionNode>()
        fun walk(node: ExecutionNode) {
            out.add(node)
            node.children.forEach(::walk)
        }
        walk(graph.nodePile(0).first())
        return out
    }

    /** Classes whose `<clinit>` RAN as a class-init prelude, in run order; see ClassInitTest.initializersRun. */
    private fun initializersRun(graph: ExecutionGraph): List<String> {
        val seen = mutableListOf<org.cf.smalivm.core.Activation>()
        for (node in allNodes(graph)) if (seen.none { it === node.activation }) seen.add(node.activation)
        return seen.mapNotNull { it.classInitContext?.activeInitializer?.type?.descriptor }
    }

    /** [type]'s lifecycle state on the entry method's returning path; see ClassInitTest.initStateAtReturn. */
    private fun initStateAtReturn(graph: ExecutionGraph, type: VirtualType): ClassInitState {
        val returning = allNodes(graph).filter { it.outcome is Outcome.Returned && it.activation.caller == null }
        assertTrue(returning.isNotEmpty(), "no returning node in the entry activation")
        return returning.map { it.frame.initState(type) }.distinct().single()
    }

    private companion object {
        private const val CLASS_NAME = "Lsget_test;"
        private const val PARENT = "Lstatic_fields_parent_test;"
        private const val CHILD = "Lstatic_fields_child_test;"
    }
}
