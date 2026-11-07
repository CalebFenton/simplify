package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Method
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Deque
import java.util.IdentityHashMap
import java.util.LinkedHashMap
import java.util.SortedMap
import java.util.SortedSet

/** Exact result of comparing two detached Java identity graphs. */
sealed interface HostGraphComparison {
    data object Equal : HostGraphComparison
    data object Different : HostGraphComparison
    data object Unprovable : HostGraphComparison
}

/** Compares mutable Java graph state without calling user equality methods. */
internal class HostGraphComparator {
    fun compare(
        left: HostGraphState,
        right: HostGraphState,
        expectedLeftArrays: Map<Int, List<Any?>> = emptyMap(),
    ): HostGraphComparison {
        if (!left.isComplete || !right.isComplete) return HostGraphComparison.Unprovable
        val leftNodes = left.objectsById()
        val rightNodes = right.objectsById()
        if (left.nodeCount != right.nodeCount) return HostGraphComparison.Different
        return guarded {
            val expectedByIdentity = IdentityHashMap<Any, List<Any?>>()
            expectedLeftArrays.forEach { (id, values) -> expectedByIdentity[leftNodes.getValue(id)] = values }
            val context = Context(expectedByIdentity)
            for (id in 0 until left.nodeCount) {
                val result = compareValue(leftNodes.getValue(id), rightNodes.getValue(id), context)
                if (result != HostGraphComparison.Equal) return@guarded result
            }
            HostGraphComparison.Equal
        }
    }

    fun compare(left: Any?, right: Any?): HostGraphComparison = guarded {
        compareValue(left, right, Context(IdentityHashMap()))
    }

    private fun guarded(block: () -> HostGraphComparison): HostGraphComparison = try {
        block()
    } catch (fatal: VirtualMachineError) {
        throw fatal
    } catch (_: Throwable) {
        HostGraphComparison.Unprovable
    }

    @Suppress("DEPRECATION")
    private fun compareValue(left: Any?, right: Any?, context: Context): HostGraphComparison {
        if (left == null || right == null) {
            return if (left == null && right == null) HostGraphComparison.Equal else HostGraphComparison.Different
        }
        if (left.javaClass != right.javaClass) return HostGraphComparison.Different

        scalarComparison(left, right)?.let { return it }
        context.leftToRight[left]?.let { mapped ->
            return if (mapped === right) HostGraphComparison.Equal else HostGraphComparison.Different
        }
        context.rightToLeft[right]?.let { mapped ->
            return if (mapped === left) HostGraphComparison.Equal else HostGraphComparison.Different
        }
        context.bind(left, right)

        return when {
            left.javaClass.isArray -> compareArrays(left, right, context.expectedLeftArrays[left], context)
            left is Method -> {
                right as Method
                exact(left == right && left.isAccessible == right.isAccessible)
            }
            left is StringBuilder -> compareCharacterSequence(left, right as StringBuilder)
            left is StringBuffer -> compareCharacterSequence(left, right as StringBuffer)
            left is Map.Entry<*, *> -> compareContainer(left, right, context) {
                val rightEntry = right as Map.Entry<*, *>
                val key = compareValue(left.key, rightEntry.key, context)
                if (key != HostGraphComparison.Equal) key else compareValue(left.value, rightEntry.value, context)
            }
            left is List<*> -> compareContainer(left, right, context) {
                compareOrdered(left, right as List<*>, context)
            }
            left is Deque<*> -> compareContainer(left, right, context) {
                compareOrdered(left.toList(), (right as Deque<*>).toList(), context)
            }
            left is SortedSet<*> -> compareContainer(left, right, context) {
                compareSortedSet(left, right as SortedSet<*>, context)
            }
            left is Set<*> -> compareContainer(left, right, context) {
                compareUnordered(left.toList(), (right as Set<*>).toList(), context)
            }
            left is SortedMap<*, *> -> compareContainer(left, right, context) {
                compareSortedMap(left, right as SortedMap<*, *>, context)
            }
            left is LinkedHashMap<*, *> -> compareContainer(left, right, context) {
                compareOrderedMap(left, right as Map<*, *>, context)
            }
            left is Map<*, *> -> compareContainer(left, right, context) {
                compareMaps(left, right as Map<*, *>, context)
            }
            left is Collection<*> -> HostGraphComparison.Unprovable
            left.javaClass.name.startsWith("java.") -> HostGraphComparison.Unprovable
            else -> compareFields(left, right, context)
        }
    }

    private fun scalarComparison(left: Any, right: Any): HostGraphComparison? = when (left) {
        is Float -> exact(left.toRawBits() == (right as Float).toRawBits())
        is Double -> exact(left.toRawBits() == (right as Double).toRawBits())
        is String, is Boolean, is Byte, is Short, is Char, is Int, is Long ->
            exact(left == right)
        is BigInteger -> if (left.javaClass == BigInteger::class.java) exact(left == right) else null
        is BigDecimal -> if (left.javaClass == BigDecimal::class.java) exact(left == right) else null
        is Class<*> -> exact(left === right)
        else -> null
    }

    private fun compareCharacterSequence(left: CharSequence, right: CharSequence): HostGraphComparison {
        if (left.length != right.length) return HostGraphComparison.Different
        if ((left as? StringBuilder)?.capacity() != (right as? StringBuilder)?.capacity()) {
            return HostGraphComparison.Different
        }
        if ((left as? StringBuffer)?.capacity() != (right as? StringBuffer)?.capacity()) {
            return HostGraphComparison.Different
        }
        for (index in left.indices) if (left[index] != right[index]) return HostGraphComparison.Different
        return HostGraphComparison.Equal
    }

    private fun compareArrays(
        left: Any,
        right: Any,
        expectedLeft: List<Any?>?,
        context: Context,
    ): HostGraphComparison {
        val length = expectedLeft?.size ?: ReflectArray.getLength(left)
        if (length != ReflectArray.getLength(right)) return HostGraphComparison.Different
        for (index in 0 until length) {
            val leftValue = expectedLeft?.get(index) ?: ReflectArray.get(left, index)
            val result = compareValue(leftValue, ReflectArray.get(right, index), context)
            if (result != HostGraphComparison.Equal) return result
        }
        return HostGraphComparison.Equal
    }

    private fun compareOrdered(left: List<*>, right: List<*>, context: Context): HostGraphComparison {
        if (left.size != right.size) return HostGraphComparison.Different
        for (index in left.indices) {
            val result = compareValue(left[index], right[index], context)
            if (result != HostGraphComparison.Equal) return result
        }
        return HostGraphComparison.Equal
    }

    private fun compareUnordered(left: List<*>, right: List<*>, context: Context): HostGraphComparison {
        if (left.size != right.size) return HostGraphComparison.Different
        return matchUnordered(left, right, context, ::safeFingerprint) { a, b, candidate ->
            compareValue(a, b, candidate)
        }
    }

    private fun compareMaps(left: Map<*, *>, right: Map<*, *>, context: Context): HostGraphComparison {
        if (left.size != right.size) return HostGraphComparison.Different
        val leftEntries = left.entries.toList()
        val rightEntries = right.entries.toList()
        return matchUnordered(
            leftEntries,
            rightEntries,
            context,
            { entry -> "${safeFingerprint(entry.key)}=>${safeFingerprint(entry.value)}" },
        ) { a, b, candidate ->
            val key = compareValue(a.key, b.key, candidate)
            if (key != HostGraphComparison.Equal) key else compareValue(a.value, b.value, candidate)
        }
    }

    private fun compareOrderedMap(left: Map<*, *>, right: Map<*, *>, context: Context): HostGraphComparison {
        if (left.size != right.size) return HostGraphComparison.Different
        val rightIterator = right.entries.iterator()
        for (leftEntry in left.entries) {
            val rightEntry = rightIterator.next()
            val key = compareValue(leftEntry.key, rightEntry.key, context)
            if (key != HostGraphComparison.Equal) return key
            val value = compareValue(leftEntry.value, rightEntry.value, context)
            if (value != HostGraphComparison.Equal) return value
        }
        return HostGraphComparison.Equal
    }

    private fun compareSortedMap(
        left: SortedMap<*, *>,
        right: SortedMap<*, *>,
        context: Context,
    ): HostGraphComparison {
        val comparator = comparePublicComparator(left.comparator(), right.comparator(), context)
        return if (comparator == HostGraphComparison.Equal) compareOrderedMap(left, right, context) else comparator
    }

    private fun compareSortedSet(
        left: SortedSet<*>,
        right: SortedSet<*>,
        context: Context,
    ): HostGraphComparison {
        val comparator = comparePublicComparator(left.comparator(), right.comparator(), context)
        return if (comparator == HostGraphComparison.Equal) {
            compareOrdered(left.toList(), right.toList(), context)
        } else {
            comparator
        }
    }

    private fun comparePublicComparator(
        left: Comparator<*>?,
        right: Comparator<*>?,
        context: Context,
    ): HostGraphComparison = when {
        left == null || right == null -> exact(left == null && right == null)
        left === right -> HostGraphComparison.Equal
        else -> compareValue(left, right, context)
    }

    private fun <T> matchUnordered(
        left: List<T>,
        right: List<T>,
        context: Context,
        fingerprint: (T) -> String,
        compare: (T, T, Context) -> HostGraphComparison,
    ): HostGraphComparison {
        val leftFingerprints = left.map(fingerprint)
        val rightFingerprints = right.map(fingerprint)
        if (leftFingerprints.groupingBy { it }.eachCount() != rightFingerprints.groupingBy { it }.eachCount()) {
            return HostGraphComparison.Different
        }
        val candidateBuckets = right.indices.groupBy { rightFingerprints[it] }
        return matchUnordered(
            left,
            right,
            leftFingerprints,
            candidateBuckets,
            0,
            BooleanArray(right.size),
            context,
            compare,
        )
    }

    private fun <T> matchUnordered(
        left: List<T>,
        right: List<T>,
        leftFingerprints: List<String>,
        candidateBuckets: Map<String, List<Int>>,
        index: Int,
        used: BooleanArray,
        context: Context,
        compare: (T, T, Context) -> HostGraphComparison,
    ): HostGraphComparison {
        if (index == left.size) return HostGraphComparison.Equal
        var sawUnprovable = false
        for (candidateIndex in candidateBuckets.getValue(leftFingerprints[index])) {
            if (used[candidateIndex]) continue
            val mark = context.mark()
            when (compare(left[index], right[candidateIndex], context)) {
                HostGraphComparison.Equal -> {
                    used[candidateIndex] = true
                    when (
                        matchUnordered(
                            left,
                            right,
                            leftFingerprints,
                            candidateBuckets,
                            index + 1,
                            used,
                            context,
                            compare,
                        )
                    ) {
                        HostGraphComparison.Equal -> return HostGraphComparison.Equal
                        HostGraphComparison.Unprovable -> sawUnprovable = true
                        HostGraphComparison.Different -> Unit
                    }
                    used[candidateIndex] = false
                }
                HostGraphComparison.Unprovable -> sawUnprovable = true
                HostGraphComparison.Different -> Unit
            }
            context.rollback(mark)
        }
        return if (sawUnprovable) HostGraphComparison.Unprovable else HostGraphComparison.Different
    }

    private fun compareContainer(
        left: Any,
        right: Any,
        context: Context,
        compareContents: () -> HostGraphComparison,
    ): HostGraphComparison {
        val contents = compareContents()
        if (contents != HostGraphComparison.Equal) return contents
        return compareFields(left, right, context, customInstanceFields(left.javaClass))
    }

    private fun compareFields(
        left: Any,
        right: Any,
        context: Context,
        fields: List<java.lang.reflect.Field> = instanceFields(left.javaClass),
    ): HostGraphComparison {
        for (field in fields) {
            if (!field.trySetAccessible()) return HostGraphComparison.Unprovable
            val result = try {
                compareValue(field.get(left), field.get(right), context)
            } catch (_: IllegalAccessException) {
                HostGraphComparison.Unprovable
            }
            if (result != HostGraphComparison.Equal) return result
        }
        return HostGraphComparison.Equal
    }

    private fun exact(equal: Boolean): HostGraphComparison =
        if (equal) HostGraphComparison.Equal else HostGraphComparison.Different

    private fun safeFingerprint(value: Any?): String = when (value) {
        null -> "null"
        is Float -> "F:${value.toRawBits()}"
        is Double -> "D:${value.toRawBits()}"
        is String -> "S:$value"
        is Boolean, is Byte, is Short, is Char, is Int, is Long ->
            "${value.javaClass.name}:$value"
        is BigInteger -> if (value.javaClass == BigInteger::class.java) "BI:$value" else "O:${value.javaClass.name}"
        is BigDecimal -> if (value.javaClass == BigDecimal::class.java) "BD:$value" else "O:${value.javaClass.name}"
        is Class<*> -> "C:${value.name}:${System.identityHashCode(value)}"
        is Enum<*> -> "E:${value.javaClass.name}:${value.ordinal}"
        is StringBuilder -> "SB:${value.length}:${value.capacity()}:${value}"
        is StringBuffer -> "SF:${value.length}:${value.capacity()}:${value}"
        else -> when {
            value.javaClass.isArray -> "A:${value.javaClass.name}:${ReflectArray.getLength(value)}"
            value is Map<*, *> -> "M:${value.javaClass.name}:${value.size}"
            value is Collection<*> -> "K:${value.javaClass.name}:${value.size}"
            else -> "O:${value.javaClass.name}"
        }
    }

    private class Context(val expectedLeftArrays: IdentityHashMap<Any, List<Any?>>) {
        val leftToRight: IdentityHashMap<Any, Any> = IdentityHashMap()
        val rightToLeft: IdentityHashMap<Any, Any> = IdentityHashMap()
        private val bindings = mutableListOf<Pair<Any, Any>>()

        fun bind(left: Any, right: Any) {
            leftToRight[left] = right
            rightToLeft[right] = left
            bindings += left to right
        }

        fun mark(): Int = bindings.size

        fun rollback(mark: Int) {
            while (bindings.size > mark) {
                val (left, right) = bindings.removeAt(bindings.lastIndex)
                leftToRight.remove(left)
                rightToLeft.remove(right)
            }
        }
    }
}
