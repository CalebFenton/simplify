package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.ExecutionNode
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.agreed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IFieldClassInitTest {

    @Test
    fun allocationInitializesOnceAndInstanceAccessUsesDeclaredCellsWithoutRetriggering() {
        val graph = CoreTester.execute(CLASS_NAME, "exerciseAllocationAndFields()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(TARGET), initializersRun(graph), "the target <clinit> runs exactly once")

        val trusted = graph.trusted()!!
        assertEquals(Value.Known(0, type("I")), trusted.terminatingConsensus(1), "fresh primitive default")
        assertEquals(
            Value.Known(null, type("Ljava/lang/String;")),
            trusted.terminatingConsensus(2),
            "fresh reference default",
        )
        assertEquals(Value.Known(7, type("I")), trusted.terminatingConsensus(4), "iput/iget round trip")
        assertEquals(
            Value.Known(1, type("I")),
            trusted.terminatingStaticFieldConsensus(type(TARGET), "$TARGET->runs:I"),
            "allocation initialized the class; no later static access can hide a missed trigger",
        )

        val receiver = trusted.terminatingConsensus(0) as Value.Known
        val objectState = trusted.terminatingHeapObject(receiver.raw as HeapRef).agreed() as HeapObject.TrackedObject
        assertEquals(Value.Known(7, type("I")), objectState.field("$TARGET->number:I"))
        assertEquals(Value.Known(null, type("Ljava/lang/String;")), objectState.field("$TARGET->text:Ljava/lang/String;"))
    }

    private fun allNodes(graph: ExecutionGraph): List<ExecutionNode> {
        val out = mutableListOf<ExecutionNode>()
        fun walk(node: ExecutionNode) {
            out += node
            node.children.forEach(::walk)
        }
        walk(graph.nodePile(0).first())
        return out
    }

    private fun initializersRun(graph: ExecutionGraph): List<String> {
        val seen = mutableListOf<org.cf.smalivm.core.Activation>()
        for (node in allNodes(graph)) if (seen.none { it === node.activation }) seen += node.activation
        return seen.mapNotNull { it.classInitContext?.activeInitializer?.type?.descriptor }
    }

    private fun type(descriptor: String) = VirtualType.of(descriptor)

    private companion object {
        private const val CLASS_NAME = "Lifield_clinit_test;"
        private const val TARGET = "Lifield_clinit_target;"
    }
}
