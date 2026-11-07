package org.cf.simplify.strategy

fun interface OptimizationStrategy {
    fun perform(input: StrategyInput)
    val name: String get() = javaClass.simpleName
    val optimizationCategories: Set<String> get() = emptySet()
}
