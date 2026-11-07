package org.cf.simplify

import org.cf.simplify.strategy.OptimizationStrategy
import org.cf.simplify.strategy.StrategyInput
import org.cf.smalivm.core.*
import java.util.Collections

class Optimizer(
    private val vmServices: VmServices,
    private val limits: Limits,
    private val maxPasses: Int,
    strategies: List<OptimizationStrategy>,
) {
    private val strategies = strategies.toList()
    init {
        require(maxPasses >= 0) { "maxPasses must be non-negative: $maxPasses" }
        require(strategies.isNotEmpty()) { "At least one optimization strategy is required" }
    }

    class Result internal constructor(
        val committedEditCount: Int,
        val completionStatus: CompletionStatus,
        val passLimitReached: Boolean,
        rejections: Collection<Rejection>,
        counts: Map<String, Map<String, Int>>,
    ) {
        /** Distinct edits the method editor refused; each one is a strategy defect. */
        val rejections: List<Rejection> = Collections.unmodifiableList(rejections.toList())
        val strategyOptimizationCounts = immutableCounts(counts)
        val optimizationCounts: Map<String, Int> get() = flattenCounts(strategyOptimizationCounts)
    }

    data class Rejection(val strategy: String, val reason: MethodEditRejectionReason)

    fun optimize(methodSignature: String): Result {
        var commits = 0
        val counts = emptyCounts(strategies)
        val rejections = linkedSetOf<Rejection>()
        while (true) {
            val method = requireNotNull(vmServices.classIndex.methodView(methodSignature)) {
                "No concrete application method: $methodSignature"
            }
            val graph = vmServices.newSession(method, limits).run()
            if (graph.trusted() == null) return Result(commits, graph.completion(), false, rejections, counts)
            if (commits >= maxPasses) return Result(commits, graph.completion(), true, rejections, counts)
            var committed = false
            for (strategy in strategies) {
                val input = StrategyInput(graph, vmServices.methodEditor)
                val transaction = input.transaction
                val commitResult = try {
                    strategy.perform(input)
                    transaction.commit()
                } finally {
                    if (transaction.state == MethodEditTransactionState.OPEN) transaction.discard()
                }
                when (commitResult) {
                    is MethodEditCommitResult.Committed -> {
                        val categories = counts.getValue(strategy.name)
                        input.optimizationCounts.forEach { (category, value) ->
                            categories[category] = (categories[category] ?: 0) + value
                        }
                        commits++
                        committed = true
                        break
                    }
                    is MethodEditCommitResult.Rejected -> rejections += Rejection(strategy.name, commitResult.reason)
                    MethodEditCommitResult.Unchanged -> Unit
                }
            }
            if (!committed) return Result(commits, graph.completion(), false, rejections, counts)
        }
    }
}

internal fun emptyCounts(strategies: List<OptimizationStrategy>): MutableMap<String, MutableMap<String, Int>> =
    linkedMapOf<String, MutableMap<String, Int>>().apply {
        for (strategy in strategies) {
            val categories = getOrPut(strategy.name) { linkedMapOf() }
            strategy.optimizationCategories.forEach { categories.putIfAbsent(it, 0) }
        }
    }

internal fun immutableCounts(counts: Map<String, Map<String, Int>>): Map<String, Map<String, Int>> =
    Collections.unmodifiableMap(counts.mapValuesTo(linkedMapOf()) { Collections.unmodifiableMap(LinkedHashMap(it.value)) })

internal fun flattenCounts(counts: Map<String, Map<String, Int>>): Map<String, Int> {
    val flattened = linkedMapOf<String, Int>()
    counts.values.forEach { categories -> categories.forEach { (key, value) -> flattened[key] = (flattened[key] ?: 0) + value } }
    return Collections.unmodifiableMap(flattened)
}
