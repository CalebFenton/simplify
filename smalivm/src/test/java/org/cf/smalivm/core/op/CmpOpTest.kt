package org.cf.smalivm.core.op

import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `cmp-long`, `cmpl/g-float`, `cmpl/g-double` over `ops/cmp_test.smali`, where every fixture writes its result
 * into its lhs register `v0`. Expected values follow `docs/DALVIK-SEMANTICS.md`, "`cmp-long`, `cmpl-*`, `cmpg-*`".
 */
class CmpOpTest {

    companion object {
        private const val CLASS_NAME = "Lcmp_test;"
    }

    private fun cmp(
        name: String,
        method: String,
        lhs: Any,
        rhs: Any,
        type: String,
        expected: Any,
    ): DynamicTest = DynamicTest.dynamicTest("$method $name") {
        val rhsRegister = if (type == "F") 1 else 2
        val initial = CoreState().apply { setRegisters(0, lhs, type, rhsRegister, rhs, type) }
        val expectedState = CoreState().apply { setRegisters(0, expected, "I") }
        CoreTester.test(CLASS_NAME, method, initial, expectedState)
    }

    /** The rhs register differs from the destination, so only `registersRead` shows it is live. */
    @Test
    fun cmplFloatReportsBothOperandRegistersAsRead() {
        val graph = CoreTester.execute(CLASS_NAME, "cmplFloat()V")
        assertEquals(setOf(0, 1), graph.registersRead(0))
    }

    /** [BC] `cmpkind`: 0 if equal, 1 if `b > c`, -1 if `b < c`, for both biases. */
    @TestFactory
    fun orderedOperandsCompareByValue(): List<DynamicTest> = buildList {
        for (method in listOf("cmplFloat()V", "cmpgFloat()V")) {
            add(cmp("equal", method, 4.5f, 4.5f, "F", 0))
            add(cmp("greater", method, 20.5f, 0.5f, "F", 1))
            add(cmp("less", method, 0.5f, 20.5f, "F", -1))
        }
        for (method in listOf("cmplDouble()V", "cmpgDouble()V")) {
            add(cmp("equal", method, 4.5, 4.5, "D", 0))
            add(cmp("greater", method, 20.5, 0.5, "D", 1))
            add(cmp("less", method, 0.5, 20.5, "D", -1))
        }
        add(cmp("equal", "cmpLong()V", 0x100L, 0x100L, "J", 0))
        add(cmp("greater", "cmpLong()V", 0x100000L, 0x10L, "J", 1))
        add(cmp("less", "cmpLong()V", 0x10L, 0x100000L, "J", -1))
    }

    /**
     * [BC]: a NaN operand gives 1 for `cmpg` and -1 for `cmpl`. An lhs NaN under `cmpl` is the only row that
     * distinguishes an explicit NaN check from a `Float.compare`/`Double.compare` fallback, which sorts NaN highest.
     */
    @TestFactory
    fun aNaNOperandGivesTheBiasResult(): List<DynamicTest> = listOf(
        cmp("rhs NaN", "cmpgFloat()V", 0.5f, Float.NaN, "F", 1),
        cmp("rhs NaN", "cmplFloat()V", 0.5f, Float.NaN, "F", -1),
        cmp("lhs NaN", "cmplFloat()V", Float.NaN, 20.5f, "F", -1),
        cmp("rhs NaN", "cmpgDouble()V", 0.5, Double.NaN, "D", 1),
        cmp("rhs NaN", "cmplDouble()V", 0.5, Double.NaN, "D", -1),
        cmp("lhs NaN", "cmplDouble()V", Double.NaN, 20.5, "D", -1),
    )

    /** IEEE-754 `==` treats `-0.0` and `0.0` as equal (ART `HandleCmpl`/`HandleCmpg`); a total-order compare does not. */
    @TestFactory
    fun signedZerosCompareEqualUnderBothBiases(): List<DynamicTest> = buildList {
        for (method in listOf("cmplFloat()V", "cmpgFloat()V")) {
            add(cmp("-0.0 vs 0.0", method, -0.0f, 0.0f, "F", 0))
            add(cmp("0.0 vs -0.0", method, 0.0f, -0.0f, "F", 0))
        }
        for (method in listOf("cmplDouble()V", "cmpgDouble()V")) {
            add(cmp("-0.0 vs 0.0", method, -0.0, 0.0, "D", 0))
            add(cmp("0.0 vs -0.0", method, 0.0, -0.0, "D", 0))
        }
    }

    @TestFactory
    fun anUnknownOperandGivesUnknownInt(): List<DynamicTest> = listOf(
        cmp("unknown rhs", "cmpgFloat()V", 0.5f, CoreState.UNKNOWN, "F", CoreState.UNKNOWN),
        cmp("unknown rhs", "cmplFloat()V", 0.5f, CoreState.UNKNOWN, "F", CoreState.UNKNOWN),
        cmp("unknown rhs", "cmpgDouble()V", 0.5, CoreState.UNKNOWN, "D", CoreState.UNKNOWN),
        cmp("unknown rhs", "cmplDouble()V", 0.5, CoreState.UNKNOWN, "D", CoreState.UNKNOWN),
        cmp("unknown rhs", "cmpLong()V", 0x100L, CoreState.UNKNOWN, "J", CoreState.UNKNOWN),
        cmp("unknown lhs", "cmpLong()V", CoreState.UNKNOWN, 0x100L, "J", CoreState.UNKNOWN),
    )

    /**
     * `const-wide` literals in a double comparison are read as their bits. `-2.0` and `-4.0` order oppositely as
     * bits and as long values, so a by-value read answers -1 where the correct answer is 1.
     */
    @Test
    fun aCmpDoubleOnConstWideLiteralBitsOrdersTheDoublesNotTheirBitPatterns() {
        val initial = CoreState().apply {
            setRegisters(0, java.lang.Double.doubleToRawLongBits(-2.0), "J", 2, java.lang.Double.doubleToRawLongBits(-4.0), "J")
        }
        val expected = CoreState().apply { setRegisters(0, 1, "I") }
        CoreTester.test(CLASS_NAME, "cmpgDouble()V", initial, expected)
    }

    /** A 32-bit `Float` in v0 supplies only half of the 64 bits `cmp-long` reads, so the result is unprovable. */
    @Test
    fun aCmpLongReadingAComputedFloatRawIsNotProvable() {
        val initial = CoreState().apply { setRegisters(0, 5.9f, "F", 2, 7L, "J") }
        val expected = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "I") }
        CoreTester.test(CLASS_NAME, "cmpLong()V", initial, expected)
    }
}
