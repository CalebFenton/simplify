package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BackendSideEffectsTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun explicitPureMethodEffectRemainsNoneWhenTheHostGraphIsUnchanged() {
        // Bug class: exact NONE declarations are ignored even when host execution changes no owned graph.
        // Oracle: the fixture reads an immutable String and the exact policy entry declares NONE.
        val target = method(HOST, "pureLength", "(Ljava/lang/String;)I")
        val run = execute(
            "pureLength(Ljava/lang/String;)I",
            ReflectionPolicy(exactSafeMethods = setOf(target), methodEffects = mapOf(target to SideEffectLevel.NONE)),
            mapOf(1 to Value.Known("four", VirtualType.of("Ljava/lang/String;"))),
        )

        assertEquals(Value.Known(4, INT), run.graph.returnedValue())
        assertEquals(SideEffectLevel.NONE, run.graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun exactMutationUnderDeclaredNoneImportsAliasesAndRemainsNone() {
        // Bug class: exact VM-owned mutation is imported but spuriously raised from declared NONE to WEAK.
        // Oracle: Plan 11 Task 42 keeps declared NONE when exact reconciliation imports all modeled mutation.
        val target = method(HOST, "mutate", "(Ljava/lang/StringBuilder;)V")
        val run = execute(
            "observedMutation(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;",
            ReflectionPolicy(exactSafeMethods = setOf(target), methodEffects = mapOf(target to SideEffectLevel.NONE)),
            mapOf(1 to Value.Known(StringBuilder("none"), BUILDER)),
        )
        val ref = (run.seeded.getValue(1) as Value.Known).raw
        val trusted = run.graph.trusted()!!

        assertEquals(ref, (run.graph.returnedValue() as Value.Known).raw)
        assertEquals(ref, (trusted.terminatingConsensus(0) as Value.Known).raw)
        assertEquals(ref, (trusted.terminatingConsensus(1) as Value.Known).raw)
        assertEquals("none-mutated", run.returnedHostObject().toString())
        assertEquals(SideEffectLevel.NONE, run.graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun unclassifiedExactMutationDefaultsToStrongWhileImportingContents() {
        // Bug class: exact mutation causes an unclassified reflected call to inherit NONE from graph precision.
        // Oracle: reflection defaults STRONG unless exact policy evidence declares a narrower effect.
        val target = method(HOST, "mutate", "(Ljava/lang/StringBuilder;)V")
        val run = execute(
            "observedMutation(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;",
            ReflectionPolicy(exactSafeMethods = setOf(target)),
            mapOf(1 to Value.Known(StringBuilder("strong"), BUILDER)),
        )

        assertEquals("strong-mutated", run.returnedHostObject().toString())
        assertEquals(SideEffectLevel.STRONG, run.graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun explicitlyWeakExactMutationRemainsWeak() {
        // Bug class: exact reconciliation incorrectly downgrades an explicit WEAK declaration to NONE.
        // Oracle: effects are monotonic and exact graph comparison never lowers the backend declaration.
        val target = method(HOST, "mutate", "(Ljava/lang/StringBuilder;)V")
        val run = execute(
            "observedMutation(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;",
            ReflectionPolicy(exactSafeMethods = setOf(target), methodEffects = mapOf(target to SideEffectLevel.WEAK)),
            mapOf(1 to Value.Known(StringBuilder("weak"), BUILDER)),
        )

        assertEquals("weak-mutated", run.returnedHostObject().toString())
        assertEquals(SideEffectLevel.WEAK, run.graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun mutationBeforeTargetThrowRetainsGraphChangeAndDeclaredNone() {
        // Bug class: target-throw routing restores changed graph state or raises reconciled NONE mutation to WEAK.
        // Oracle: Plan 11 Task 42 finalizes mutation before guest routing and preserves declared NONE.
        val target = method(HOST, "mutateThenThrow", "(Ljava/lang/StringBuilder;)V")
        val run = execute(
            "mutationBeforeTargetThrow(Ljava/lang/StringBuilder;)Ljava/lang/String;",
            ReflectionPolicy(exactSafeMethods = setOf(target), methodEffects = mapOf(target to SideEffectLevel.NONE)),
            mapOf(1 to Value.Known(StringBuilder("kept"), BUILDER)),
        )

        assertEquals("kept-before-throw", run.hostObjectInRegister(1).toString())
        assertEquals(SideEffectLevel.NONE, run.graph.methodSideEffectLevelOverExploredPaths())
    }

    private fun execute(
        descriptor: String,
        policy: ReflectionPolicy,
        initial: Map<Int, Value>,
    ): FixtureRun {
        val view = classIndex.requireMethodView(FIXTURE, descriptor)
        val vm = VmServices(classIndex, policy)
        val frame = Frame.entryFrame(view, vm.hostObjectCloner)
        val entries = initial.entries.toList()
        val admitted = frame.admitHostSeeds(entries.map { HostSeed(it.value) })
        val seeded = entries.zip(admitted).associate { (entry, value) -> entry.key to value }
        seeded.forEach(frame::assign)
        val session = vm.newSession(view, entryFrame = frame)
        val graph = session.run()
        assertEquals(CompletionStatus.Complete, graph.completion(), descriptor)
        return FixtureRun(graph, seeded)
    }

    private fun ExecutionGraph.returnedValue(): Value? = (outcomes.single() as Outcome.Returned).value

    private fun FixtureRun.returnedHostObject(): Any {
        val returned = graph.returnedValue() as Value.Known
        return requireNotNull(graph.terminatingHostObjectOverExploredPaths(returned.raw as HeapRef))
    }

    private fun FixtureRun.hostObjectInRegister(register: Int): Any {
        val value = graph.trusted()!!.terminatingConsensus(register) as Value.Known
        return requireNotNull(graph.terminatingHostObjectOverExploredPaths(value.raw as HeapRef))
    }

    private data class FixtureRun(
        val graph: ExecutionGraph,
        val seeded: Map<Int, Value>,
    )

    private fun method(owner: VirtualType, name: String, proto: String) = MethodDescriptor(owner, name, proto)

    private companion object {
        const val FIXTURE = "Lbackend_side_effect_test;"
        val INT = VirtualType.of("I")
        val BUILDER = VirtualType.of("Ljava/lang/StringBuilder;")
        val HOST = VirtualType.of("Lorg/cf/smalivm/core/BackendCorpusHostFixture;")
    }
}
