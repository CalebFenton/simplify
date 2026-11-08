package org.cf.simplify.strategy

import org.cf.smalivm.core.MethodControlFlow
import org.cf.smalivm.core.MethodEditTransaction
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.TrustedGraphView
import java.util.Collections

/**
 * One strategy's view of one completed analysis. [controlFlow] is null when the method's bytecode is malformed; a
 * strategy then makes no edit that depends on control flow.
 */
class StrategyInput internal constructor(
    val graph: TrustedGraphView,
    val controlFlow: MethodControlFlow?,
    val transaction: MethodEditTransaction,
) {
    val method: MethodView get() = graph.methodView
    private val counts = linkedMapOf<String, Int>()
    val optimizationCounts: Map<String, Int> get() = Collections.unmodifiableMap(counts)

    fun recordOptimization(category: String) {
        counts[category] = (counts[category] ?: 0) + 1
    }
}
