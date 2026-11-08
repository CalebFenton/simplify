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
        counts: Map<String, Int>,
    ) {
        /** Distinct edits the method editor refused; each one is a strategy defect. */
        val rejections: List<Rejection> = Collections.unmodifiableList(rejections.toList())
        val optimizationCounts: Map<String, Int> = Collections.unmodifiableMap(LinkedHashMap(counts))
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
            val trusted = graph.trusted() ?: return Result(commits, graph.completion(), false, rejections, counts)
            if (commits >= maxPasses) return Result(commits, graph.completion(), true, rejections, counts)
            val controlFlow = MethodControlFlow.of(trusted.methodView)
            var committed = false
            for (strategy in strategies) {
                val transaction = vmServices.methodEditor.beginEdit(trusted)
                val input = StrategyInput(trusted, controlFlow, transaction)
                val commitResult = try {
                    strategy.perform(input)
                    transaction.commit()
                } finally {
                    if (transaction.isOpen) transaction.discard()
                }
                when (commitResult) {
                    is MethodEditCommitResult.Committed -> {
                        counts.addCounts(input.optimizationCounts)
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

internal fun emptyCounts(strategies: List<OptimizationStrategy>): MutableMap<String, Int> =
    linkedMapOf<String, Int>().apply {
        strategies.forEach { strategy -> strategy.optimizationCategories.forEach { putIfAbsent(it, 0) } }
    }

internal fun MutableMap<String, Int>.addCounts(counts: Map<String, Int>) =
    counts.forEach { (category, value) -> merge(category, value, Int::plus) }
