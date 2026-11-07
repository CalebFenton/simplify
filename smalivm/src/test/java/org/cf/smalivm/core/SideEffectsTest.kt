package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Active Plan 9 portion of `TestSideEffects.java`: eleven direct behavior ports, one repaired malformed
 * instance-write fixture, and two added controls. The repaired row keeps its original `WEAK` expectation.
 */
class SideEffectsTest {
    @Test
    fun constOpsHaveNoSideEffects() = assertEffect("constOps()V", SideEffectLevel.NONE)

    @Test
    fun emptyMethodHasNoSideEffects() = assertEffect("emptyMethod()V", SideEffectLevel.NONE)

    @Test
    fun invokeMethodWithNoSideEffectsHasNoSideEffects() =
        assertEffect("invokeMethodWithNoSideEffects()V", SideEffectLevel.NONE)

    @Test
    fun invokeOfNonAnalyzableMethodHasStrongSideEffects() =
        assertEffect("invokeOfNonAnalyzableMethod()V", SideEffectLevel.STRONG, AbortReason.CALL_DEPTH)

    @Test
    fun invokeSideEffectMethodHasStrongSideEffects() =
        assertEffect("invokeSideEffectMethod(Ljava/io/OutputStream;[B)V", SideEffectLevel.STRONG)

    @Test
    fun modifyInstanceMemberHasWeakSideEffects() =
        assertEffect("modifyInstanceMember()V", SideEffectLevel.WEAK)

    @Test
    fun newInstanceFrameworkClassWithoutInitializerHasNoSideEffects() =
        assertEffect("newInstanceFrameworkClassWithoutInitializer()V", SideEffectLevel.NONE)

    @Test
    fun newInstanceOfClassWithStaticInitializerWithStrongSideEffectsHasStrongSideEffects() =
        assertEffect("newInstanceOfClassWithStaticInitializerWithStrongSideEffects()V", SideEffectLevel.STRONG)

    @Test
    fun newInstanceOfClassWithStaticInitializerWithWeakSideEffectsHasWeakSideEffects() =
        assertEffect("newInstanceOfClassWithStaticInitializerWithWeakSideEffects()V", SideEffectLevel.WEAK)

    @Test
    fun newInstanceOfMethodWithNoStaticInitializerHasNoSideEffects() =
        assertEffect("newInstanceOfMethodWithNoStaticInitializer()V", SideEffectLevel.NONE)

    @Test
    fun newInstanceOfMethodWithStaticInitializerWithNoSideEffectsHasNoSideEffects() =
        assertEffect("newInstanceOfMethodWithStaticInitializerWithNoSideEffects()V", SideEffectLevel.NONE)

    @Test
    fun writeOutputStreamHasStrongSideEffects() =
        assertEffect("writeOutputStream(Ljava/io/OutputStream;[B)V", SideEffectLevel.STRONG)

    @Test
    fun aDirectStaticWriteHasWeakSideEffects() =
        assertEffect("modifyStaticMember()V", SideEffectLevel.WEAK)

    @Test
    fun aSkippedFrameworkInitializerIsStrongButNewInstanceItselfIsNot() =
        assertEffect("newInstanceFrameworkClassWithInitializer()V", SideEffectLevel.STRONG)

    private fun assertEffect(method: String, expected: SideEffectLevel, abortReason: AbortReason? = null) {
        val graph = CoreTester.execute(CLASS_NAME, method)
        if (abortReason == null) {
            assertEquals(CompletionStatus.Complete, graph.completion(), method)
        } else {
            assertEquals(CompletionStatus.Partial(setOf(abortReason)), graph.completion(), method)
            assertNull(graph.trusted(), method)
        }
        assertEquals(expected, graph.methodSideEffectLevelOverExploredPaths(), method)
    }

    private companion object {
        const val CLASS_NAME = "Lside_effects_test;"
    }
}
