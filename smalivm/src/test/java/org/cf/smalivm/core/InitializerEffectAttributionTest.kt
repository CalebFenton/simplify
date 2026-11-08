package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * A class initialization a call triggers adds to that call's effect only what the initialization leaves behind
 * beyond itself. Each trigger is the method's first class-init trigger, so its address's pile holds the triggering
 * execution and the retried instruction.
 */
class InitializerEffectAttributionTest {
    @Test
    fun anInitializerWritingOnlyItsOwnInitializerOnlyStaticAndItsOwnArrayLeavesTheTriggerEffectFree() {
        val graph = run("ownStatic()I")

        assertEquals(listOf(Outcome.Returned(Value.Known(7, INT))), graph.outcomes)
        assertEquals(SideEffectLevel.NONE, effectAt(graph, 0))
    }

    @Test
    fun anInitializerWritingAnotherClassStaticKeepsItsWeakEffectAtTheTrigger() {
        assertEquals(SideEffectLevel.WEAK, effectAt(run("otherStatic()I"), 0))
    }

    @Test
    fun anInitializerWritingAnObjectAllocatedBeforeItKeepsItsWeakEffectAtTheTrigger() {
        assertEquals(SideEffectLevel.WEAK, effectAt(run("preexisting()I"), PREEXISTING_TRIGGER))
    }

    @Test
    fun anInitializerCallingAnOpaqueMethodKeepsItsStrongEffectAtTheTrigger() {
        assertEquals(SideEffectLevel.STRONG, effectAt(run("opaque()I"), 0))
    }

    @Test
    fun anInitializerThatThrowsKeepsItsConfinedWritesAtTheTrigger() {
        // A failed initialization is not repeated by a later trigger, so its own-static write is the trigger's.
        assertEquals(SideEffectLevel.WEAK, effectAt(run("throwing()I"), 0))
    }

    @Test
    fun aSkippedFrameworkInitializerOfASafeClassDeclaresNothing() {
        assertEquals(SideEffectLevel.NONE, effectAt(run("unobservableFramework()V"), 0))
    }

    @Test
    fun aSkippedFrameworkInitializerOutsideTheSafeClassesStaysStrong() {
        assertEquals(SideEffectLevel.STRONG, effectAt(run("observableFramework()V"), 0))
    }

    private fun run(method: String): ExecutionGraph {
        val view = CoreTester.classIndex.requireMethodView(TRIGGER, method)
        val graph = VmServices(CoreTester.classIndex, ReflectionPolicy.defaults()).newSession(view).run()
        assertEquals(CompletionStatus.Complete, graph.completion())
        return graph
    }

    private fun effectAt(graph: ExecutionGraph, address: Int): SideEffectLevel =
        requireNotNull(graph.trusted()).sideEffectLevel(address)

    private companion object {
        const val TRIGGER = "Linitializer_effects/trigger;"
        const val PREEXISTING_TRIGGER = 5
        val INT: VirtualType = VirtualType.of("I")
    }
}
