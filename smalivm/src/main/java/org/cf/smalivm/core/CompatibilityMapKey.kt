package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/**
 * Immutable persistent map used as a partition key. [hashCode] is the [Map] contract's sum of entry hashes,
 * maintained incrementally on [put] and [remove], so hashing never walks the entries. [equals] follows the
 * [Map] contract: shared roots compare in constant time, and different cached hashes reject without a walk.
 */
internal class CompatibilityMapKey<K, V> private constructor(
    private val backing: PersistentMap<K, V>,
    private val structuralHash: Int,
) : Map<K, V> by backing {
    fun put(key: K, value: V): CompatibilityMapKey<K, V> {
        val hadOld = backing.containsKey(key)
        val old = backing[key]
        val updated = backing.putting(key, value)
        if (updated === backing) return this
        val withoutOld = if (hadOld) structuralHash - entryHash(key, old) else structuralHash
        return CompatibilityMapKey(updated, withoutOld + entryHash(key, value))
    }

    fun remove(key: K): CompatibilityMapKey<K, V> {
        if (!backing.containsKey(key)) return this
        return CompatibilityMapKey(backing.removing(key), structuralHash - entryHash(key, backing[key]))
    }

    override fun hashCode(): Int = structuralHash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Map<*, *>) return false
        if (other is CompatibilityMapKey<*, *>) {
            if (backing === other.backing) return true
            if (structuralHash != other.structuralHash) return false
            return backing == other.backing
        }
        return backing == other
    }

    override fun toString(): String = backing.toString()

    private fun entryHash(key: K, value: V?): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)

    companion object {
        fun <K, V> empty(): CompatibilityMapKey<K, V> = CompatibilityMapKey(persistentMapOf(), 0)
    }
}
