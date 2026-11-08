package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.CoreTester.test
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * `sput*`: a register written into per-path static storage, through the same resolution -> class-init ->
 * access preamble as [SGetOp] (one `preamble` function -- see `SFieldOp.kt`). Covers each form's store, the
 * narrow forms' truncation, the primitive-tagged `sput-object` coercion, the `<clinit>`-prelude-before-write
 * ordering, per-path write isolation, and the WEAK side-effect claim.
 *
 * A field-only expectation asserts through `TrustedGraphView.terminatingStaticFieldConsensus` -- the
 * replacement for the oracle's `getTerminatingFieldConsensus(field)`, keyed by the FULLY-QUALIFIED
 * descriptor so two same-named statics of different types can never collapse into one cell
 * (`docs/architecture/type-and-dex.md:116` is the oracle's collision).
 */
class SPutOpTest {
    private lateinit var expected: CoreState
    private lateinit var initial: CoreState

    @BeforeEach
    fun setUp() {
        expected = CoreState()
        initial = CoreState()
    }

    @Test
    fun putStaticBoolean() {
        initial.setRegisters(0, true, "Z")
        expected.setFields(CLASS_NAME, "myBoolean:Z", true)
        test(CLASS_NAME, "putStaticBoolean()V", initial, expected)
    }

    @Test
    fun putStaticByte() {
        initial.setRegisters(0, 0xFF.toByte(), "B")
        expected.setFields(CLASS_NAME, "myByte:B", 0xFF.toByte())
        test(CLASS_NAME, "putStaticByte()V", initial, expected)
    }

    @Test
    fun putStaticChar() {
        initial.setRegisters(0, '!', "C")
        expected.setFields(CLASS_NAME, "myChar:C", '!')
        test(CLASS_NAME, "putStaticChar()V", initial, expected)
    }

    @Test
    fun putStaticInt() {
        initial.setRegisters(0, 0x42, "I")
        expected.setFields(CLASS_NAME, "myInt:I", 0x42)
        test(CLASS_NAME, "putStaticInt()V", initial, expected)
    }

    @Test
    fun putStaticObject() {
        initial.setRegisters(0, "Do not pray for an easy life", "Ljava/lang/String;")
        expected.setFields(CLASS_NAME, "myString:Ljava/lang/String;", "Do not pray for an easy life")
        test(CLASS_NAME, "putStaticObject()V", initial, expected)
    }

    @Test
    fun putStaticShort() {
        initial.setRegisters(0, 0x4242.toShort(), "S")
        expected.setFields(CLASS_NAME, "myShort:S", 0x4242.toShort())
        test(CLASS_NAME, "putStaticShort()V", initial, expected)
    }

    @Test
    fun putStaticWide() {
        initial.setRegisters(0, 0xFFFFFFFFFL, "J")
        expected.setFields(CLASS_NAME, "myLong:J", 0xFFFFFFFFFL)
        test(CLASS_NAME, "putStaticWide()V", initial, expected)
    }

    /**
     * A narrow `sput-*` stores only its field's width. ART's `GetFieldValue` reads the 32-bit register and
     * casts it to the field's type -- `static_cast<int8_t>` for byte, `<int16_t>` short, `<uint16_t>` char,
     * `<uint8_t>` boolean -- so `0x1ff` stored to a byte reads back as -1. The values are what an `add-int`
     * result can hold; the verifier accepts any integral register for these stores. A boolean byte other
     * than 0 or 1 is real on a device (`iget-boolean` reads it back nonzero) but has no `Boolean` raw, so
     * it is `Unknown(Z)`.
     */
    @Test
    fun aNarrowStoreKeepsOnlyItsFieldsWidth() {
        fun assertStores(method: String, written: Int, field: String, stored: Value) = test(
            CLASS_NAME, method,
            CoreState().apply { setRegisters(0, written, "I") },
            CoreState().apply { setField(CLASS_NAME, field, stored) },
        )
        assertStores("putStaticByte()V", 0x1ff, "myByte:B", Value.Known((-1).toByte(), VirtualType.of("B")))
        assertStores("putStaticShort()V", 0x18000, "myShort:S", Value.Known((-32768).toShort(), VirtualType.of("S")))
        assertStores("putStaticChar()V", 0x10041, "myChar:C", Value.Known('A', VirtualType.of("C")))
        assertStores("putStaticBoolean()V", 0x101, "myBoolean:Z", Value.Known(true, VirtualType.of("Z")))
        assertStores("putStaticBoolean()V", 2, "myBoolean:Z", Value.Unknown(VirtualType.of("Z")))
    }

    // ------------------------------------------------------------------------------------------
    // Beyond the corpus: the cases the oracle could not express or never covered.
    // ------------------------------------------------------------------------------------------

    /**
     * **The dropped-NPE class, pinned for `sput-object`'s OWN call site** (MB5-MB8's per-op isolation:
     * `SGetOpTest.aPrimitiveTaggedCellReadsAsNullThroughSGetObject` pins the read side, and neither
     * test can see the other's mutation). `sput-object` reads its value register AS A REFERENCE, so a
     * primitive-tagged `Known(5, "I")` stores `null` -- `aput-object`'s rule 1 at a different call
     * site, gated on the mnemonic because only the `-object` form's operand sits in a reference
     * context. Tier per `SGetOp`'s table: the non-zero row is **VERIFIER** (ART's two interpreter
     * tiers contradict each other on it). Replacing `readsAsNullReference` with `raw == null` in
     * `SPutOp.execute` stores `Known(5, "I")` and this consensus assertion goes red; without this row
     * the same store would report a non-null value to `terminatingStaticFieldConsensus`, a wrong
     * answer to a public query.
     */
    @Test
    fun aPrimitiveTaggedRegisterStoresNullThroughSPutObject() {
        initial.setRegisters(0, 5, "I")
        expected.setField(
            CLASS_NAME, "myString:Ljava/lang/String;",
            Value.Known(null, VirtualType.of("Ljava/lang/String;")),
        )
        test(CLASS_NAME, "putStaticObject()V", initial, expected)
    }

    /**
     * **The `<clinit>` prelude runs BEFORE the triggering write lands** -- `SPutOp`'s ordering claim
     * ("a `sput` in a method that touches an uninitialized class does not have its value overwritten
     * by preparation"), and the mutate-to-prove pin for `sput`'s OWN class-init trigger (INHERITED,
     * [JVMS] §5.5 via `putstatic` -- `sget`'s trigger is the measured one and is pinned by the ported
     * `StaticFieldAccessTest.canGetFieldsWhileInitializing`). The fixture writes 0x2a into a field the
     * `<clinit>` assigns, then reads it back:
     *  - correct order: init prelude runs (`testInt1 = alwaysZero = 0`, `testInt2 = 1`), THEN the
     *    re-executed `sput` writes 0x2a, and the `sget` reads 0x2a;
     *  - a `sput` that skipped `OpArm.InitClass` writes 0x2a first, the `sget`'s own trigger then
     *    runs the `<clinit>` OVER it, and the method returns 0 -- red here.
     * `testInt2 = 1` is asserted alongside so "the prelude ran at all" and "it ran in the right order"
     * are separately visible.
     */
    @Test
    fun theClinitPreludeRunsBeforeTheTriggeringWriteLands() {
        expected.setRegisters(0, 0x2a, "I")
        expected.setFields(STATIC_FIELDS, "testInt1:I", 0x2a, "testInt2:I", 1)
        test(STATIC_FIELDS, "putThenGetTestInt1()I", initial, expected)
    }

    /**
     * **Per-path static storage, observed through the ops** -- the behavioural pin
     * `ClassInitStore.copy`'s ⚠️ note names (its unit twin is
     * `ClassInitTest.aCopyDoesNotShareStaticFieldValuesWithItsOriginal`): copying the lifecycle map
     * but not the VALUES map would make a `sput` on one arm of an `if` visible on the other, silently
     * -- every class-init test still passes under that mutation. The fixture forks on an unknown
     * parameter and each arm writes a different constant, so per-path stores join `Unknown(I)` over
     * the two returning paths; a shared store would answer whichever write ran last as a definite
     * `Known` on BOTH paths, and this goes red.
     */
    @Test
    fun oneBranchesStaticWriteIsInvisibleToTheOther() {
        val graph = CoreTester.execute(CLASS_NAME, "putStaticIntBranched(I)V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            Value.Unknown(VirtualType.of("I")),
            graph.trusted()!!.terminatingStaticFieldConsensus(VirtualType.of(CLASS_NAME), "$CLASS_NAME->myInt:I"),
            "two arms wrote different constants, so the returning paths must NOT agree on a value -- " +
                "a definite Known here means one arm saw the other's write (a session-global store)",
        )
    }

    /**
     * Side effect: exactly [SideEffectLevel.WEAK], asserted in both directions because each wrong
     * neighbour is a different consumer defect (`SPutOp`'s KDoc carries the three grounds):
     *  - `NONE` would let Plan 14's dead-code strategy delete a method whose only observable act is a
     *    static write that OUTLIVES the frame;
     *  - `STRONG` means *unmodelled*, and a `sput` into a resolved field is modelled exactly -- it
     *    would stop every fold in any method that writes a static.
     * Oracle parity: `opcode/SPutOp.kt:36` declares exactly this level. The fixture class has no
     * `<clinit>`, so nothing else in the run declares anything and the method level IS the op's.
     */
    @Test
    fun aSPutDeclaresExactlyAWeakSideEffect() {
        val graph = CoreTester.execute(
            CLASS_NAME, "putStaticInt()V", CoreState().apply { setRegisters(0, 0x42, "I") },
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.WEAK, graph.explored.methodSideEffectLevel())
        // The liveness declarations, pinned beside the effect because both are what
        // ExecutionGraph.availableRegisters consumers read: the value register is READ, and NOTHING is
        // assigned -- the deliberate divergence from the oracle's `registersAssignedCount = 1`
        // (`opcode/SPutOp.kt:22`), documented on `SPutOp.registersAssigned`. The address is the sput's
        // own (0); a wide source is reported by its low half only.
        assertEquals(setOf(0), graph.registersRead(0), "the value register is read")
        assertEquals(
            emptySet<Int>(), graph.registersAssigned(0),
            "a sput assigns NO register -- the store goes into class state, and claiming a destination " +
                "would offer the value register as scratch",
        )
    }

    private companion object {
        private const val CLASS_NAME = "Lsput_test;"
        private const val STATIC_FIELDS = "Lstatic_fields_test;"
    }
}
