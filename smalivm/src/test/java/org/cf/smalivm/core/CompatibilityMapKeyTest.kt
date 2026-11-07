package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CompatibilityMapKeyTest {
    @Test
    fun cachedHashAndRootFastPathDoNotReadElements() {
        // Bug class: every compatibility hash or shared-root equality walks all map entries.
        // Oracle: writes compute the structural hash; repeated reads and identical roots are O(1).
        val counts = Counts()
        val key = CompatibilityMapKey.empty<CountingElement, CountingElement>()
            .put(CountingElement(1, 11, counts), CountingElement(2, 22, counts))
        counts.clear()

        repeat(100_000) { key.hashCode() }
        assertEquals(key, key)

        assertEquals(0, counts.hashCalls)
        assertEquals(0, counts.equalsCalls)
    }

    @Test
    fun unequalHashesRejectWithoutStructuralTraversal() {
        // Bug class: different compatibility hashes still trigger full map equality.
        // Oracle: unequal Map hashes prove inequality; no key/value equality is required.
        val counts = Counts()
        val left = CompatibilityMapKey.empty<CountingElement, CountingElement>()
            .put(CountingElement(1, 1, counts), CountingElement(10, 10, counts))
        val right = CompatibilityMapKey.empty<CountingElement, CountingElement>()
            .put(CountingElement(2, 2, counts), CountingElement(20, 20, counts))
        counts.clear()

        assertNotEquals(left, right)

        assertEquals(0, counts.equalsCalls)
    }

    @Test
    fun equalHashCollisionFallsBackToExactMapEquality() {
        // Bug class: cached-hash equality merges different compatibility maps after a collision.
        // Oracle: Map hash is only a filter; equal hashes require exact structural equality.
        val counts = Counts()
        val left = CompatibilityMapKey.empty<CountingElement, CountingElement>()
            .put(CountingElement(1, 7, counts), CountingElement(10, 9, counts))
        val right = CompatibilityMapKey.empty<CountingElement, CountingElement>()
            .put(CountingElement(2, 7, counts), CountingElement(20, 9, counts))
        counts.clear()
        assertEquals(left.hashCode(), right.hashCode())
        counts.clear()

        assertNotEquals(left, right)

        assertTrue(counts.equalsCalls > 0)
    }

    @Test
    fun overwriteAndRemoveKeepTheMapContractHash() {
        // Bug class: incremental hash maintenance forgets to retract an overwritten or removed entry, so equal
        // maps hash differently and land in different call partitions.
        // Oracle: java.util.Map's contract -- hashCode is the sum of key^value entry hashes, and equals compares
        // mappings regardless of implementation or insertion history.
        val key = CompatibilityMapKey.empty<String, Int>()
            .put("a", 1)
            .put("b", 2)
            .put("a", 3)
            .put("a", 3)
            .remove("b")
            .remove("missing")
            .put("c", 4)
        val expected = hashMapOf("a" to 3, "c" to 4)
        val rebuilt = CompatibilityMapKey.empty<String, Int>().put("c", 4).put("a", 3)

        assertEquals(expected.hashCode(), key.hashCode())
        assertEquals(expected, key)
        assertEquals(key, expected)
        assertEquals(rebuilt, key)
        assertEquals(rebuilt.hashCode(), key.hashCode())
        assertNotEquals(hashMapOf("a" to 1, "c" to 4), key)

        val emptied = key.remove("a").remove("c")
        assertEquals(CompatibilityMapKey.empty<String, Int>(), emptied)
        assertEquals(emptyMap<String, Int>().hashCode(), emptied.hashCode())
    }

    @Test
    fun independentlyBuiltEqualMapsConvergeIntoOnePartition() {
        // Bug class: root-only equality keeps semantically identical call states on exponential duplicate paths.
        // Oracle: independently built maps with equal entries compare exactly equal after the cached-hash filter.
        val counts = Counts()
        val left = CompatibilityMapKey.empty<CountingElement, CountingElement>()
            .put(CountingElement(1, 7, counts), CountingElement(10, 9, counts))
        val right = CompatibilityMapKey.empty<CountingElement, CountingElement>()
            .put(CountingElement(1, 7, counts), CountingElement(10, 9, counts))
        counts.clear()

        assertEquals(left, right)
        assertEquals(1, listOf(left, right).groupBy { it }.size)
        assertTrue(counts.equalsCalls > 0)
    }

    private class Counts {
        var hashCalls = 0
        var equalsCalls = 0

        fun clear() {
            hashCalls = 0
            equalsCalls = 0
        }
    }

    private class CountingElement(
        private val id: Int,
        private val hash: Int,
        private val counts: Counts,
    ) {
        override fun hashCode(): Int {
            counts.hashCalls++
            return hash
        }

        override fun equals(other: Any?): Boolean {
            counts.equalsCalls++
            return other is CountingElement && id == other.id
        }
    }
}
