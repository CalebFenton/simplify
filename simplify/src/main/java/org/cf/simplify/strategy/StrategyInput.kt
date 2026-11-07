package org.cf.simplify.strategy

import org.cf.smalivm.core.ExecutionGraphView
import org.cf.smalivm.core.MethodEditor
import java.util.Collections

class StrategyInput(val structureGraph: ExecutionGraphView, methodEditor: MethodEditor) {
    val graph = requireNotNull(structureGraph.trusted()) {
        "Strategy input requires complete analysis: ${structureGraph.completion()}"
    }
    val transaction = methodEditor.beginEdit(graph)
    private val counts = linkedMapOf<String, Int>()
    val optimizationCounts: Map<String, Int> get() = Collections.unmodifiableMap(counts)

    fun recordOptimization(category: String) {
        counts[category] = (counts[category] ?: 0) + 1
    }
}
