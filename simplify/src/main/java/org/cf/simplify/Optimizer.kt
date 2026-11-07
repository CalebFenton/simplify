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
        val executionCount: Int,
        val committedEditCount: Int,
        val completionStatus: CompletionStatus,
        val terminalCommitResult: MethodEditCommitResult?,
        val passLimitReached: Boolean,
        counts: Map<String, Map<String, Int>>,
    ) {
        val strategyOptimizationCounts = immutableCounts(counts)
        val optimizationCounts: Map<String, Int> get() = flattenCounts(strategyOptimizationCounts)
    }

    fun optimize(methodSignature: String): Result {
        var executions = 0
        var commits = 0
        val counts = emptyCounts(strategies)
        while (true) {
            val method = requireNotNull(vmServices.classIndex.methodView(methodSignature)) {
                "No concrete application method: $methodSignature"
            }
            val graph = vmServices.newSession(method, limits).run()
            executions++
            if (graph.trusted() == null) return Result(executions, commits, graph.completion(), null, false, counts)
            if (commits >= maxPasses) return Result(executions, commits, graph.completion(), null, true, counts)
            var committed = false
            var lastResult: MethodEditCommitResult? = null
            var rejection: MethodEditCommitResult.Rejected? = null
            for (strategy in strategies) {
                val input = StrategyInput(graph, vmServices.methodEditor)
                val transaction = input.transaction
                try {
                    strategy.perform(input)
                    lastResult = transaction.commit()
                } finally {
                    if (transaction.state == MethodEditTransactionState.OPEN) transaction.discard()
                }
                when (lastResult) {
                    is MethodEditCommitResult.Committed -> {
                        val categories = counts.getValue(strategy.name)
                        input.optimizationCounts.forEach { (category, value) ->
                            categories[category] = (categories[category] ?: 0) + value
                        }
                        commits++
                        committed = true
                        break
                    }
                    is MethodEditCommitResult.Rejected -> if (rejection == null) rejection = lastResult
                    else -> Unit
                }
            }
            if (!committed) return Result(executions, commits, graph.completion(), rejection ?: lastResult, false, counts)
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
