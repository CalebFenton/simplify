package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/** Oracle: each fixture body in `side_effects_test.smali`; any write to a heap field is at least WEAK. */
class SideEffectsTest {
    @TestFactory
    fun methodSideEffectLevelMatchesTheStrongestEffectInTheBody(): List<DynamicTest> = listOf(
        Case("constOps()V", SideEffectLevel.NONE),
        Case("emptyMethod()V", SideEffectLevel.NONE),
        Case("invokeMethodWithNoSideEffects()V", SideEffectLevel.NONE),
        Case("invokeOfNonAnalyzableMethod()V", SideEffectLevel.STRONG, AbortReason.CALL_DEPTH),
        Case("invokeSideEffectMethod(Ljava/io/OutputStream;[B)V", SideEffectLevel.STRONG),
        Case("modifyInstanceMember()V", SideEffectLevel.WEAK),
        Case("modifyStaticMember()V", SideEffectLevel.WEAK),
        Case("newInstanceFrameworkClassWithoutInitializer()V", SideEffectLevel.NONE),
        Case("newInstanceFrameworkClassWithInitializer()V", SideEffectLevel.STRONG),
        Case("newInstanceOfClassWithStaticInitializerWithStrongSideEffects()V", SideEffectLevel.STRONG),
        Case("newInstanceOfClassWithStaticInitializerWithWeakSideEffects()V", SideEffectLevel.WEAK),
        Case("newInstanceOfMethodWithNoStaticInitializer()V", SideEffectLevel.NONE),
        Case("newInstanceOfMethodWithStaticInitializerWithNoSideEffects()V", SideEffectLevel.NONE),
        Case("writeOutputStream(Ljava/io/OutputStream;[B)V", SideEffectLevel.STRONG),
    ).map { case ->
        DynamicTest.dynamicTest("${case.method} is ${case.expected}") {
            val graph = CoreTester.execute(CLASS_NAME, case.method)
            if (case.abortReason == null) {
                assertEquals(CompletionStatus.Complete, graph.completion())
            } else {
                assertEquals(CompletionStatus.Partial(setOf(case.abortReason)), graph.completion())
                assertNull(graph.trusted())
            }
            assertEquals(case.expected, graph.explored.methodSideEffectLevel())
        }
    }

    private data class Case(val method: String, val expected: SideEffectLevel, val abortReason: AbortReason? = null)

    private companion object {
        const val CLASS_NAME = "Lside_effects_test;"
    }
}
