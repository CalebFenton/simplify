package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * A session analyses its entry method as reached from arbitrary program state: any class may already be
 * initialized, and any code may have written its non-final statics since. Expected values come from the
 * fixtures in `entry_class_state/` read under that model, not from a run.
 */
class EntryClassStateTest {
    private val int = VirtualType.of("I")

    @Test
    fun aMutableStaticReadsUnknownSoBothBranchesAreReachable() {
        // Before entry, mutable may be anything: 1 if only <clinit> wrote it, 0 or any other value otherwise.
        assertEquals(
            setOf(Outcome.Returned(Value.Known(10, int)), Outcome.Returned(Value.Known(20, int))),
            run(HOLDER, "branchOnMutable()I").outcomes.toSet(),
        )
    }

    @Test
    fun aFreshProcessReadsTheValueTheInitializerLeaves() {
        // Nothing runs before the entry, so mutable holds <clinit>'s 1.
        val view = CoreTester.classIndex.requireMethodView(HOLDER, "branchOnMutable()I")
        val graph = CoreTester.vmServices.newSession(view, entryClassState = EntryClassState.FRESH_PROCESS).run()
        assertEquals(listOf(Outcome.Returned(Value.Known(20, int))), graph.outcomes)
    }

    @Test
    fun aFinalStaticReadsTheValueItsInitializationProduces() {
        // Only <clinit> writes a final static: 5 from the initializer plus the encoded 42.
        assertEquals(listOf(Outcome.Returned(Value.Known(47, int))), run(HOLDER, "sumOfFinals()I").outcomes)
    }

    @Test
    fun aStaticThePathWroteReadsTheWrittenValue() {
        assertEquals(listOf(Outcome.Returned(Value.Known(9, int))), run(HOLDER, "writeThenRead()I").outcomes)
    }

    @Test
    fun aTriggeredInitializersWriteToAnotherClassIsNotKnownAfterTheTrigger() {
        // writer's <clinit> writes 3 only if writer was not initialized before entry; otherwise the 9 stays.
        assertEquals(
            listOf(Outcome.Returned(Value.Unknown(int))),
            run(HOLDER, "writeThenTriggerAnotherInitializer()I").outcomes,
        )
    }

    @Test
    fun aNonFinalFrameworkStaticWithAnEncodedLiteralReadsUnknown() {
        // The literal is only the value before any code writes the field.
        assertEquals(
            listOf(Outcome.Returned(Value.Unknown(int))),
            run(HOLDER, "frameworkMutableLiteral()I").outcomes,
        )
    }

    @Test
    fun anEntryClassInitializerRunsOnceOverPreparedStatics() {
        // The class is initializing while its own <clinit> runs, so reading count sees the prepared 0, not a
        // nested run's result.
        val graph = run(COUNTER, "<clinit>()V")
        val returning = graph.addresses().flatMap { graph.nodePile(it) }.filter { it.outcome is Outcome.Returned }
        assertEquals(
            listOf(Value.Known(1, int)),
            returning.map { it.frame.getStaticField(VirtualType.of(COUNTER), "$COUNTER->count:I") }.distinct(),
        )
    }

    private fun run(className: String, descriptor: String): ExecutionGraph =
        CoreTester.vmServices.newSession(CoreTester.classIndex.requireMethodView(className, descriptor)).run()

    private companion object {
        const val HOLDER = "Lentry_class_state/holder;"
        const val COUNTER = "Lentry_class_state/counter;"
    }
}
