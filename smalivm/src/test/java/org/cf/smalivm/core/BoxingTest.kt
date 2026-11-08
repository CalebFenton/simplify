package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Boxing an unknown primitive completes and returns a non-null box. Oracle: each `valueOf` returns a cached or new
 * instance of its box class for every argument (Java SE API, `Integer.valueOf(int)` and siblings).
 */
class BoxingTest {
    @TestFactory
    fun boxingAnUnknownPrimitiveCompletesWithANonNullBox(): List<DynamicTest> = listOf(
        "boxInt(I)Ljava/lang/Integer;" to "Ljava/lang/Integer;",
        "boxLong(J)Ljava/lang/Long;" to "Ljava/lang/Long;",
        "boxShort(S)Ljava/lang/Short;" to "Ljava/lang/Short;",
        "boxByte(B)Ljava/lang/Byte;" to "Ljava/lang/Byte;",
        "boxChar(C)Ljava/lang/Character;" to "Ljava/lang/Character;",
        "boxBoolean(Z)Ljava/lang/Boolean;" to "Ljava/lang/Boolean;",
        "boxFloat(F)Ljava/lang/Float;" to "Ljava/lang/Float;",
        "boxDouble(D)Ljava/lang/Double;" to "Ljava/lang/Double;",
    ).map { (descriptor, boxType) ->
        DynamicTest.dynamicTest(descriptor) {
            val graph = CoreTester.execute(BOXER, descriptor)

            assertEquals(CompletionStatus.Complete, graph.completion(), "outcomes: ${graph.outcomes}")
            val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value!! }
            assertTrue(returned.isNotEmpty(), "outcomes: ${graph.outcomes}")
            returned.forEach { value ->
                assertEquals(VirtualType.of(boxType), value.type)
                assertFalse(value.isNull, "valueOf never returns null")
            }
        }
    }

    private companion object {
        const val BOXER = "Lboxing/boxer;"
    }
}
