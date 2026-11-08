package org.cf.smalivm.core

import java.lang.management.ManagementFactory
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Asserts that [measured] on a [large] input costs at most [maxRatio] times [measured] on a [small] one. A cost
 * claim picks [maxRatio] well above the ratio its algorithm reaches and well below the ratio of the algorithm it
 * rules out, so the assertion depends on how cost grows, not on how fast the host is.
 *
 * Cost is the calling thread's CPU time, which excludes time the host gives to other processes and to collector
 * and compiler threads. [small] and [large] build a fresh input for each run outside the measurement. After one
 * warm-up run of each, the runs alternate and each side keeps its cheapest, so a burst of load, a collection, or a
 * compilation during one run does not decide the ratio.
 */
internal fun <T> assertCostRatio(maxRatio: Double, small: () -> T, large: () -> T, measured: (T) -> Unit) {
    val threads = ManagementFactory.getThreadMXBean()
    check(threads.isCurrentThreadCpuTimeSupported) { "Cost ratios need per-thread CPU time" }
    fun cost(input: () -> T): Long {
        val prepared = input()
        val start = threads.currentThreadCpuTime
        measured(prepared)
        return threads.currentThreadCpuTime - start
    }

    cost(small)
    cost(large)
    var smallCost = Long.MAX_VALUE
    var largeCost = Long.MAX_VALUE
    repeat(COST_RATIO_ROUNDS) {
        smallCost = minOf(smallCost, cost(small))
        largeCost = minOf(largeCost, cost(large))
    }
    val ratio = largeCost.toDouble() / maxOf(smallCost, 1L)
    assertTrue(ratio <= maxRatio) {
        "large/small cost ratio %.1f exceeds %.1f (small %.1f ms, large %.1f ms of CPU)"
            .format(ratio, maxRatio, smallCost / 1e6, largeCost / 1e6)
    }
}

private const val COST_RATIO_ROUNDS = 5
