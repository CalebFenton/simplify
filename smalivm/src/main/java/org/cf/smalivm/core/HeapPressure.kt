package org.cf.smalivm.core

import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean
import java.lang.management.MemoryType

/**
 * The signal [ExecutionSession] consults to stop a run before it exhausts the JVM heap. The session reads the
 * cheap [collectionsOverThreshold] once per scheduler step, and only when it has grown asks the expensive
 * [liveDataOverThreshold], whose answer decides the abort.
 */
internal interface HeapPressure {
    /** Collections so far that ended with the tenured heap at or above its threshold. Never decreases. */
    fun collectionsOverThreshold(): Long

    /**
     * Collects the whole heap and answers whether the data still live afterwards is at or above the threshold.
     * A collection that reports pressure can count garbage it has not reclaimed yet, such as the graph of a run
     * that already ended; only a whole-heap collection measures live data.
     */
    fun liveDataOverThreshold(): Boolean
}

/**
 * [HeapPressure] read from the JVM's own collection-usage threshold on every tenured heap pool: the heap pools
 * that support a usage threshold, which excludes eden and survivor spaces. The JVM measures a pool's collection
 * usage only after a collection that collected that pool (a G1 mixed, full or concurrent-cycle collection; a
 * Parallel or Serial full collection), so young-only collections never report pressure. A heap with no such pool
 * never reports pressure at all.
 *
 * The threshold is a JVM-wide setting, so it is configured once, on first use, for the whole process.
 */
internal object TenuredHeapPressure : HeapPressure {
    /**
     * Live tenured data, as a fraction of the pool's maximum, at which a run stops. G1 keeps a tenth of the heap
     * in reserve for evacuation and needs a young generation of at least a twentieth; with live data above about
     * nine tenths it falls back to back-to-back full collections and then throws [OutOfMemoryError]. Stopping at
     * eight tenths leaves the rest as headroom for what the run allocates before its next step.
     */
    internal const val OCCUPANCY_THRESHOLD: Double = 0.8

    private val tenuredPools: List<MemoryPoolMXBean> = ManagementFactory.getMemoryPoolMXBeans()
        .filter { it.type == MemoryType.HEAP && it.isUsageThresholdSupported && it.isCollectionUsageThresholdSupported }
        .onEach { pool ->
            val max = pool.usage.max.takeIf { it > 0 } ?: Runtime.getRuntime().maxMemory()
            pool.collectionUsageThreshold = (max * OCCUPANCY_THRESHOLD).toLong()
        }

    override fun collectionsOverThreshold(): Long = tenuredPools.sumOf { it.collectionUsageThresholdCount }

    /**
     * Compares the collection usage itself: [MemoryPoolMXBean.isCollectionUsageThresholdExceeded] also answers
     * true while the threshold sensor is still on from an earlier report, which the JVM clears only later, on its
     * service thread. A JVM that ignores explicit collections (`-XX:+DisableExplicitGC`) keeps the reading that
     * reported pressure, so the answer stays conservative: the run stops.
     */
    override fun liveDataOverThreshold(): Boolean {
        System.gc()
        return tenuredPools.any { it.collectionUsage.used >= it.collectionUsageThreshold }
    }
}
