package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.CoreTester.test
import org.cf.smalivm.core.EntryClassState
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `sput*`: a register written into per-path static storage, through the same resolution, class-init and access
 * preamble as [SGetOp]. Field expectations go through `TrustedGraphView.terminatingStaticFieldConsensus`, keyed by
 * the fully-qualified descriptor so two same-named statics of different types never share a cell.
 */
class SPutOpTest {

    /** Each form stores its register's value unchanged when the value already fits the field. */
    @TestFactory
    fun aStoreWritesTheRegisterIntoTheField(): List<DynamicTest> = listOf(
        StoreCase("putStaticBoolean()V", "myBoolean:Z", "Z", true),
        StoreCase("putStaticByte()V", "myByte:B", "B", 0xFF.toByte()),
        StoreCase("putStaticChar()V", "myChar:C", "C", '!'),
        StoreCase("putStaticInt()V", "myInt:I", "I", 0x42),
        StoreCase("putStaticObject()V", "myString:Ljava/lang/String;", "Ljava/lang/String;", "Do not pray for an easy life"),
        StoreCase("putStaticShort()V", "myShort:S", "S", 0x4242.toShort()),
        StoreCase("putStaticWide()V", "myLong:J", "J", 0xFFFFFFFFFL),
    ).map { case ->
        dynamicTest(case.method) {
            test(
                CLASS_NAME, case.method,
                CoreState().apply { setRegisters(0, case.value, case.type) },
                CoreState().apply { setFields(CLASS_NAME, case.field, case.value) },
            )
        }
    }

    /**
     * A narrow `sput-*` stores only its field's width (`docs/DALVIK-SEMANTICS.md`, "Stores narrow to the field
     * width"): ART casts the 32-bit register to the field type, so `0x1ff` stored to a byte reads back as -1. The
     * verifier accepts any integral register for these stores. A boolean byte other than 0 or 1 is real on a
     * device but has no `Boolean` raw, so it is `Unknown(Z)`.
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

    /**
     * `sput-object` reads its value register as a reference, so a primitive-tagged `Known(5, I)` stores `null`
     * (`docs/DALVIK-SEMANTICS.md`, "Reading a register as a reference", verifier tier). Only the `-object` form's
     * operand is in a reference context. `SGetOpTest.aPrimitiveTaggedCellReadsAsNullThroughSGetObject` pins the
     * read side; neither test sees a defect in the other's call site.
     */
    @Test
    fun aPrimitiveTaggedRegisterStoresNullThroughSPutObject() {
        test(
            CLASS_NAME, "putStaticObject()V",
            CoreState().apply { setRegisters(0, 5, "I") },
            CoreState().apply {
                setField(CLASS_NAME, "myString:Ljava/lang/String;", Value.Known(null, VirtualType.of("Ljava/lang/String;")))
            },
        )
    }

    /**
     * The `<clinit>` prelude runs before the triggering write lands ([JVMS] §5.5, `putstatic`). The fixture writes
     * 0x2a into `testInt1`, which the `<clinit>` assigns, then reads it back. In order, the prelude sets
     * `testInt1 = 0` and `testInt2 = 1`, the re-executed `sput` writes 0x2a, and the read returns 0x2a. A `sput`
     * that skipped its trigger would write first, and the `sget`'s own trigger would then run the `<clinit>` over
     * it and return 0. `testInt2 = 1` shows separately that the prelude ran at all.
     */
    @Test
    fun theClinitPreludeRunsBeforeTheTriggeringWriteLands() {
        val expected = CoreState().apply {
            setRegisters(0, 0x2a, "I")
            setFields(STATIC_FIELDS, "testInt1:I", 0x2a, "testInt2:I", 1)
        }
        // From process start, so the <clinit>'s write to the non-final testInt2 is known.
        test(STATIC_FIELDS, "putThenGetTestInt1()I", CoreState(), expected, EntryClassState.FRESH_PROCESS)
    }

    /**
     * Static storage is per path: the fixture forks on an unknown parameter and each arm writes a different
     * constant, so the returning paths join to `Unknown(I)`. A store shared across paths would give whichever
     * write ran last as a definite `Known` on both.
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
     * A `sput` declares exactly [SideEffectLevel.WEAK]: `NONE` would let dead-code removal delete a method whose
     * only act is a static write that outlives the frame, and `STRONG` means unmodelled, which would stop every
     * fold in a method that writes a static. The fixture class has no `<clinit>`, so the method's level is the
     * op's. The value register is read and no register is assigned: the store goes into class state.
     */
    @Test
    fun aSPutDeclaresExactlyAWeakSideEffect() {
        val graph = CoreTester.execute(
            CLASS_NAME, "putStaticInt()V", CoreState().apply { setRegisters(0, 0x42, "I") },
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.WEAK, graph.explored.methodSideEffectLevel())
        assertEquals(setOf(0), graph.registersRead(0), "the value register is read")
        assertEquals(
            emptySet<Int>(), graph.registersAssigned(0),
            "a sput assigns no register; claiming a destination would offer the value register as scratch",
        )
    }

    private class StoreCase(val method: String, val field: String, val type: String, val value: Any)

    private companion object {
        private const val CLASS_NAME = "Lsput_test;"
        private const val STATIC_FIELDS = "Lstatic_fields_test;"
    }
}
