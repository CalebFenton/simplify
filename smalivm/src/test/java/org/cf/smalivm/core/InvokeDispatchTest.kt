package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class InvokeDispatchTest {
    private val int = VirtualType.of("I")

    @Test
    fun invokeStaticNeverInheritsAParentClinit() {
        val graph = CoreTester.execute(REVIEW, "invokeInheritedClinit()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchMethodError;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun invokeSuperResolvesAnUnambiguousInterfaceDefault() {
        CoreTester.assertReturns(REVIEW, "callInterfaceDefault()I", Value.Known(11, int))
    }

    @Test
    fun incompleteDispatchWorldDegradesOneCandidateWithoutSampleSpecificPolicy(@TempDir root: Path) {
        Files.writeString(root.resolve("orphan.smali"), ORPHAN_SOURCE.trimIndent())
        val index = classIndexFromSource(root, INCOMPLETE_WORLD_SOURCE)
        val view = index.requireMethodView(INCOMPLETE_WORLD, "incompleteWorld(Lincomplete/base;)I")
        val graph = VmServices(index).newSession(view).run()
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Unknown(int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        val invoke = graph.addresses().flatMap { graph.nodePile(it) }.single { it.dispatchCandidates.isNotEmpty() }
        assertEquals(setOf("Lincomplete/base;->value()I"), invoke.dispatchCandidates)
    }

    @Test
    fun ambiguousDispatchCandidatesAreOrderedCopiedAndImmutable() {
        val lookup = CoreTester.classIndex.currentResolver().resolveInvocation(
            MethodSignature(VirtualType.of("Linvoke_dispatch_base;"), "value", "()I"),
            InvokeKind.VIRTUAL,
            VirtualType.of(REVIEW),
            receiverType = null,
        ) as InvocationLookup.Ambiguous
        assertEquals(
            listOf("Linvoke_dispatch_base;->value()I", "Linvoke_dispatch_child;->value()I"),
            lookup.candidates.toList(),
        )
        assertFalse(lookup.candidates.javaClass.name.contains("LinkedHashMap"), lookup.candidates.javaClass.name)
        @Suppress("UNCHECKED_CAST")
        val mutable = lookup.candidates as MutableSet<String>
        assertThrows(UnsupportedOperationException::class.java) { mutable.add("Lleak;->value()I") }
    }

    private companion object {
        const val REVIEW = "Linvoke_call;"
        const val INCOMPLETE_WORLD = "Lincomplete/base;"

        const val INCOMPLETE_WORLD_SOURCE = """
            .class public Lincomplete/base;
            .super Ljava/lang/Object;

            .method public value()I
                .locals 1
                const/4 v0, 0x7
                return v0
            .end method

            .method public static incompleteWorld(Lincomplete/base;)I
                .locals 0
                invoke-virtual {p0}, Lincomplete/base;->value()I
                move-result p0
                return p0
            .end method
        """

        /** Its superclass is absent, so the program's hierarchy is incomplete. */
        const val ORPHAN_SOURCE = """
            .class public Lincomplete/orphan;
            .super Lincomplete/absent;
        """
    }
}
