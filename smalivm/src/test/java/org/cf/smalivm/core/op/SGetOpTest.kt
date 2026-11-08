package org.cf.smalivm.core.op

import org.cf.smalivm.core.ClassInitState
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.CoreTester.initStateAtReturn
import org.cf.smalivm.core.CoreTester.initializersRun
import org.cf.smalivm.core.EntryClassState
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `sget*`: a static field read into a register through per-path class storage, triggering the declaring
 * class's initialization first (device-measured by `Fx_StaticFieldDoesInitialize`; `docs/DALVIK-SEMANTICS.md`,
 * "Static fields").
 *
 * Assertions go through terminating consensus, not per-address reads: the class-init trigger re-executes the op
 * at its own address, so the first pass contributes `Top` to the consensus at that address. Every run starts as
 * a fresh process, because preparation and `<clinit>` values are the subject and a method entered from
 * arbitrary state cannot know them (`EntryClassStateTest`).
 */
class SGetOpTest {

    /** A static seeded on an initialized class reads back exactly the seed, at the field's declared type. */
    @TestFactory
    fun aSeededStaticReadsBackTheSeed(): List<DynamicTest> = listOf(
        "Boolean" to true,
        "Byte" to 0xf.toByte(),
        "Char" to 'x',
        "Int" to 0x42,
        "Long" to 0xFFFFFFFFFL,
        "Short" to 0x100.toShort(),
        "String" to "They tried and died.",
    ).map { (name, seed) ->
        val type = TYPES.getValue(name)
        dynamicTest("$name seeded $seed") {
            val initial = CoreState().apply { setFields(CLASS_NAME, "my$name:$type", seed) }
            val expected = CoreState().apply { setRegisters(0, seed, type) }
            test(CLASS_NAME, "getStatic$name()V", initial, expected)
        }
    }

    /**
     * A dex-encoded static value is the field's prepared value (JVMS §5.4.2; dex `static_values`). Each expected
     * value is the literal on the field's `.field` line in `ops/sget_test.smali`. `getStaticFloatLiteral()V`
     * reads the narrow `F` field with `sget-wide` in a one-register frame; the value is right only because the
     * op takes its width from the resolved field, not the mnemonic.
     */
    @TestFactory
    fun aDexEncodedLiteralIsTheReadValue(): List<DynamicTest> = listOf(
        "Boolean" to true,
        "Byte" to 0xf.toByte(),
        "Char" to 'x',
        "Double" to 10000000000.0,
        "Float" to 1.1f,
        "Int" to 0x42,
        "Long" to 0xFFFFFFFFFL,
        "Short" to 0x100.toShort(),
        "String" to "life, what's life?",
    ).map { (name, literal) ->
        dynamicTest("$name literal $literal") {
            val expected = CoreState().apply { setRegisters(0, literal, TYPES.getValue(name)) }
            test(CLASS_NAME, "getStatic${name}Literal()V", CoreState(), expected)
        }
    }

    /**
     * `Lsget_test;` declares no `<clinit>`, so a static with no encoded value holds its JVMS §2.3/§2.4 default
     * after initialization: a definite `Known`, not `Unknown`, boxed at the declared type.
     */
    @TestFactory
    fun anUnassignedStaticReadsItsTypeDefault(): List<DynamicTest> = listOf(
        "Boolean" to false,
        "Byte" to 0.toByte(),
        "Char" to '\u0000',
        "Double" to 0.0,
        "Float" to 0f,
        "Int" to 0,
        "Long" to 0L,
        "Short" to 0.toShort(),
        "String" to null,
    ).map { (name, default) ->
        dynamicTest("$name default $default") {
            val expected = CoreState().apply { setRegisters(0, default, TYPES.getValue(name)) }
            test(CLASS_NAME, "getStatic$name()V", CoreState(), expected)
        }
    }

    /**
     * An absent declaring class throws a guest `NoClassDefFoundError` on the failing path alone, with ART's
     * `DoResolveType` message (device-measured: `Fx_SGetAbsentType` against `Fx_SGetPresentType`). The throwing
     * `sget` leaves its destination untouched and does not fall through; `sget` is format 21c, two code units.
     */
    @Test
    fun anAbsentClassThrowsNoClassDefFoundErrorWithoutFallingThrough() {
        CoreTester.testThrows(
            CLASS_NAME, "getStaticUnknownClassField()V",
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "Failed resolution of: Lsome/unknown/classzzzzz;",
        )
        val graph = CoreTester.execute(CLASS_NAME, "getStaticUnknownClassField()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(2), "a definite throw must not also explore the fall-through")
        assertTrue(
            graph.nodePile(0).first().frame.peek(0) == null,
            "a throwing sget must leave its destination register untouched",
        )
    }

    /**
     * `Ljava/lang/Integer;` is a framework class whose `<clinit>` is not run, and `MAX_VALUE` is still known
     * because the dex encodes it as a literal. [aStaticReadDeclaresNoEffect] pins the read's effect.
     */
    @Test
    fun aFrameworkStaticWithAnEncodedLiteralReadsTheLiteral() {
        val expected = CoreState().apply { setRegisters(0, Int.MAX_VALUE, "I") }
        test(CLASS_NAME, "getStaticWhitelistedClassField()V", CoreState(), expected)
    }

    /**
     * A static cell holds bits, and `sget-object` reads them as a reference, so a primitive-tagged `Known(5, I)` in
     * a reference-typed field reads as `null` ([org.cf.smalivm.core.readsAsNullReference]; `docs/DALVIK-SEMANTICS.md`,
     * "Reading a register as a reference", verifier tier). Every other case here seeds a correctly typed value,
     * so only this one fails when `sget` tests `raw == null` instead.
     */
    @Test
    fun aPrimitiveTaggedCellReadsAsNullThroughSGetObject() {
        val initial = CoreState().apply {
            setField(CLASS_NAME, "myString:Ljava/lang/String;", Value.Known(5, VirtualType.of("I")))
        }
        val expected = CoreState().apply { setRegisters(0, null, "Ljava/lang/String;") }
        test(CLASS_NAME, "getStaticString()V", initial, expected)
    }

    /**
     * A resolved class that declares no such field anywhere in its superclass chain throws a guest
     * `NoSuchFieldError` ([JVMS] §5.4.3.2, `getstatic`'s linking exceptions). No device measurement fixes the
     * message text, so only the type is asserted.
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
     * A static declared in an ancestor and referenced through a descendant (legal dex that javac never emits):
     * the value is the parent `<clinit>`'s write, the cell is keyed by the declaring class's descriptor, and only
     * the declaring class is initialized ([JVMS] §5.5), so the child stays `UNINITIALIZED`.
     */
    @Test
    fun anInheritedStaticResolvesToTheDeclaringClassesCellAndInitializesOnlyThatClass() {
        val expected = CoreState().apply {
            setRegisters(0, 1, "I")
            setFields(PARENT, "parentTouched:I", 1)
        }
        test(CHILD, "getParentTouchedThroughChildReference()I", CoreState(), expected)

        val graph = CoreTester.execute(CHILD, "getParentTouchedThroughChildReference()I", entryClassState = FRESH)
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
     * `sget` declares no effect of its own, of an app class's static or of a framework class's, whose skipped
     * `Integer.<clinit>` the reflection policy shows unobservable.
     */
    @Test
    fun aStaticReadDeclaresNoEffect() {
        val plain = CoreTester.execute(CLASS_NAME, "getStaticIntLiteral()V")
        assertEquals(CompletionStatus.Complete, plain.completion())
        assertEquals(
            SideEffectLevel.NONE, plain.explored.methodSideEffectLevel(),
            "a read of an app class's static perturbs nothing -- NONE, or every folding pass stops",
        )
        val framework = CoreTester.execute(CLASS_NAME, "getStaticWhitelistedClassField()V")
        assertEquals(CompletionStatus.Complete, framework.completion())
        assertEquals(
            SideEffectLevel.NONE, framework.explored.methodSideEffectLevel(),
            "Integer is a safe class whose <clinit> is not denied, so skipping its initializer leaves nothing observable",
        )
    }

    /**
     * The assignment width comes from the resolved field's declared type, never the mnemonic: a wide field owns
     * {0, 1}, a narrow one {0}, and `getStaticFloat()V`'s `sget-wide` of the narrow `F` field in a one-register
     * frame owns {0}, where trusting the mnemonic would name a register the method does not have.
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
            "sget-wide naming a narrow F field declares the field's width",
        )
    }

    private fun test(className: String, descriptor: String, initial: CoreState, expected: CoreState) =
        CoreTester.test(className, descriptor, initial, expected, FRESH)

    private companion object {
        private const val CLASS_NAME = "Lsget_test;"
        private const val PARENT = "Lstatic_fields_parent_test;"
        private const val CHILD = "Lstatic_fields_child_test;"
        private val FRESH = EntryClassState.FRESH_PROCESS

        /** Field type per `ops/sget_test.smali` accessor name. */
        private val TYPES = mapOf(
            "Boolean" to "Z", "Byte" to "B", "Char" to "C", "Double" to "D", "Float" to "F", "Int" to "I",
            "Long" to "J", "Short" to "S", "String" to "Ljava/lang/String;",
        )
    }
}
