package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Field
import java.lang.reflect.Executable
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Deque
import java.util.IdentityHashMap
import java.util.LinkedHashMap
import java.util.SortedMap
import java.util.SortedSet

/** Exact result of comparing two detached Java identity graphs. */
internal sealed interface HostGraphComparison {
    data object Equal : HostGraphComparison
    data object Different : HostGraphComparison

    /**
     * The graphs could not be compared exactly. [cause] is the failure that stopped the comparison; null when the
     * graphs hold state no comparison can see.
     */
    data class Unprovable(val cause: Throwable? = null) : HostGraphComparison
}

/** Compares mutable Java graph state without calling user equality methods. */
internal class HostGraphComparator {
    fun compare(
        left: HostGraphState,
        right: HostGraphState,
        expectedLeftArrays: Map<Int, List<Any?>> = emptyMap(),
    ): HostGraphComparison {
        if (!left.isComplete || !right.isComplete) return HostGraphComparison.Unprovable()
        val leftNodes = left.objectsById()
        val rightNodes = right.objectsById()
        if (left.nodeCount != right.nodeCount) return HostGraphComparison.Different
        return hostBoundary({ HostGraphComparison.Unprovable(it) }) {
            val expectedByIdentity = IdentityHashMap<Any, List<Any?>>()
            expectedLeftArrays.forEach { (id, values) -> expectedByIdentity[leftNodes.getValue(id)] = values }
            val walk = Walk(expectedByIdentity)
            for (id in 0 until left.nodeCount) {
                val result = walk.compare(leftNodes.getValue(id), rightNodes.getValue(id))
                if (result != HostGraphComparison.Equal) return result
            }
            HostGraphComparison.Equal
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

    /**
     * One comparison: identity bindings plus a depth-first work stack.
     *
     * Pending work lives on [stack], never the host call stack, because a guest can build graphs deeper than
     * the host stack. [visit] only pushes work; each [Step] visits at most one pair when executed. While an
     * unordered match tries a candidate pairing, the steps above that match's base belong to the attempt.
     * After a result other than Equal the walk must not be reused.
     */
    private inner class Walk(private val expectedLeftArrays: IdentityHashMap<Any, List<Any?>>) {
        private val leftToRight = IdentityHashMap<Any, Any>()
        private val rightToLeft = IdentityHashMap<Any, Any>()
        private val boundLeft = ArrayList<Any>()
        private val boundRight = ArrayList<Any>()
        private val stack = ArrayList<Step>()
        private val matches = ArrayList<UnorderedMatch>()
        private var steps = 0L
        private var discardedSteps = 0L

        /** Compares one pair; the bindings behind an Equal result constrain every later pair. */
        fun compare(left: Any?, right: Any?): HostGraphComparison {
            var verdict = visit(left, right)
            while (true) {
                if (discardedSteps > MAX_DISCARDED_STEPS) return HostGraphComparison.Unprovable()
                val match = matches.lastOrNull()
                if (verdict == HostGraphComparison.Equal && stack.size > (match?.base ?: 0)) {
                    steps++
                    verdict = execute(stack.removeAt(stack.lastIndex))
                    continue
                }
                if (match == null) return verdict
                while (stack.size > match.base) stack.removeAt(stack.lastIndex)
                verdict = resume(match, verdict)
            }
        }

        @Suppress("DEPRECATION")
        private fun visit(left: Any?, right: Any?): HostGraphComparison {
            if (left == null || right == null) return exact(left == null && right == null)
            if (left.javaClass != right.javaClass) return HostGraphComparison.Different

            scalarComparison(left, right)?.let { return it }
            leftToRight[left]?.let { mapped -> return exact(mapped === right) }
            rightToLeft[right]?.let { mapped -> return exact(mapped === left) }
            leftToRight[left] = right
            rightToLeft[right] = left
            boundLeft += left
            boundRight += right

            return when {
                left.javaClass.isArray -> visitArray(left, right)
                left is Executable -> exact(left == right && left.isAccessible == (right as Executable).isAccessible)
                left is StringBuilder -> compareCharacterSequence(left, right as StringBuilder)
                left is StringBuffer -> compareCharacterSequence(left, right as StringBuffer)
                left is Map.Entry<*, *> -> {
                    pushCustomFields(left, right)
                    stack += EntryPair(left, right as Map.Entry<*, *>)
                    HostGraphComparison.Equal
                }
                left is List<*> -> {
                    pushCustomFields(left, right)
                    visitOrdered(left, right as List<*>, entries = false)
                }
                left is Deque<*> -> {
                    pushCustomFields(left, right)
                    visitOrdered(left, right as Deque<*>, entries = false)
                }
                left is SortedSet<*> -> {
                    right as SortedSet<*>
                    pushCustomFields(left, right)
                    stack += if (left.size == right.size) Ordered(left, right, entries = false) else SizeMismatch
                    visitComparator(left.comparator(), right.comparator())
                }
                left is Set<*> -> {
                    pushCustomFields(left, right)
                    visitUnordered(left.toList(), (right as Set<*>).toList(), entries = false)
                }
                left is SortedMap<*, *> -> {
                    right as SortedMap<*, *>
                    pushCustomFields(left, right)
                    stack += if (left.size == right.size) {
                        Ordered(left.entries, right.entries, entries = true)
                    } else {
                        SizeMismatch
                    }
                    visitComparator(left.comparator(), right.comparator())
                }
                left is LinkedHashMap<*, *> -> {
                    pushCustomFields(left, right)
                    visitOrdered(left.entries, (right as Map<*, *>).entries, entries = true)
                }
                left is Map<*, *> -> {
                    pushCustomFields(left, right)
                    visitUnordered(left.entries.toList(), (right as Map<*, *>).entries.toList(), entries = true)
                }
                left is Collection<*> -> HostGraphComparison.Unprovable()
                JdkClasses.isJdk(left.javaClass) -> HostGraphComparison.Unprovable()
                else -> {
                    stack += FieldValues(left, right, instanceFields(left.javaClass))
                    HostGraphComparison.Equal
                }
            }
        }

        private fun execute(step: Step): HostGraphComparison = when (step) {
            is ValuePair -> visit(step.left, step.right)
            is EntryPair -> visitEntry(step.left, step.right)
            is Ordered -> if (step.left.hasNext()) {
                val left = step.left.next()
                val right = step.right.next()
                stack += step
                if (step.entries) {
                    visitEntry(left as Map.Entry<*, *>, right as Map.Entry<*, *>)
                } else {
                    visit(left, right)
                }
            } else {
                HostGraphComparison.Equal
            }
            is ArrayElements -> if (step.index < step.length) {
                val index = step.index++
                stack += step
                val left = step.expectedLeft?.get(index) ?: ReflectArray.get(step.left, index)
                visit(left, ReflectArray.get(step.right, index))
            } else {
                HostGraphComparison.Equal
            }
            is FieldValues -> if (step.index < step.fields.size) {
                val field = step.fields[step.index++]
                stack += step
                visitField(field, step.left, step.right)
            } else {
                HostGraphComparison.Equal
            }
            SizeMismatch -> HostGraphComparison.Different
        }

        private fun visitEntry(left: Map.Entry<*, *>, right: Map.Entry<*, *>): HostGraphComparison {
            stack += ValuePair(left.value, right.value)
            return visit(left.key, right.key)
        }

        private fun visitField(field: Field, left: Any, right: Any): HostGraphComparison {
            if (!field.trySetAccessible()) return HostGraphComparison.Unprovable()
            val leftValue: Any?
            val rightValue: Any?
            try {
                leftValue = field.get(left)
                rightValue = field.get(right)
            } catch (_: IllegalAccessException) {
                return HostGraphComparison.Unprovable()
            }
            return visit(leftValue, rightValue)
        }

        private fun visitArray(left: Any, right: Any): HostGraphComparison {
            val expectedLeft = expectedLeftArrays[left]
            val length = expectedLeft?.size ?: ReflectArray.getLength(left)
            if (length != ReflectArray.getLength(right)) return HostGraphComparison.Different
            stack += ArrayElements(left, right, expectedLeft, length)
            return HostGraphComparison.Equal
        }

        private fun visitOrdered(left: Collection<*>, right: Collection<*>, entries: Boolean): HostGraphComparison {
            if (left.size != right.size) return HostGraphComparison.Different
            stack += Ordered(left, right, entries)
            return HostGraphComparison.Equal
        }

        private fun visitComparator(left: Comparator<*>?, right: Comparator<*>?): HostGraphComparison {
            if (left == null || right == null) return exact(left == null && right == null)
            if (left !== right) stack += ValuePair(left, right)
            return HostGraphComparison.Equal
        }

        private fun pushCustomFields(left: Any, right: Any) {
            val fields = customInstanceFields(left.javaClass)
            if (fields.isNotEmpty()) stack += FieldValues(left, right, fields)
        }

        private fun visitUnordered(left: List<Any?>, right: List<Any?>, entries: Boolean): HostGraphComparison {
            if (left.size != right.size) return HostGraphComparison.Different
            val fingerprint: (Any?) -> String = if (entries) ::entryFingerprint else ::safeFingerprint
            val leftFingerprints = left.map(fingerprint)
            val rightFingerprints = right.map(fingerprint)
            if (leftFingerprints.groupingBy { it }.eachCount() != rightFingerprints.groupingBy { it }.eachCount()) {
                return HostGraphComparison.Different
            }
            if (left.isEmpty()) return HostGraphComparison.Equal
            val rightBuckets = right.indices.groupBy { rightFingerprints[it] }
            val match = UnorderedMatch(stack.size, left, right, entries, leftFingerprints.map(rightBuckets::getValue))
            matches += match
            return nextCandidate(match)
        }

        /**
         * Records the attempt at [match]'s current level: Equal descends a level, anything else undoes the
         * attempt's bindings. Returns Equal while the match is still searching or once every element is paired.
         */
        private fun resume(match: UnorderedMatch, attempt: HostGraphComparison): HostGraphComparison {
            val level = match.level
            if (attempt == HostGraphComparison.Equal) {
                match.used[match.chosen[level]] = true
                match.level = level + 1
                if (match.level == match.left.size) {
                    matches.removeAt(matches.lastIndex)
                    return HostGraphComparison.Equal
                }
                match.cursor[match.level] = 0
                match.sawUnprovable[match.level] = false
            } else {
                if (attempt is HostGraphComparison.Unprovable) match.sawUnprovable[level] = true
                undo(match, level)
            }
            return nextCandidate(match)
        }

        /** Pushes the next unused candidate at the current level, backtracking out of exhausted levels. */
        private fun nextCandidate(match: UnorderedMatch): HostGraphComparison {
            while (true) {
                val level = match.level
                val bucket = match.buckets[level]
                while (match.cursor[level] < bucket.size) {
                    val candidate = bucket[match.cursor[level]++]
                    if (match.used[candidate]) continue
                    match.chosen[level] = candidate
                    match.markBindings[level] = boundLeft.size
                    match.markSteps[level] = steps
                    match.markDiscardedSteps[level] = discardedSteps
                    stack += if (match.entries) {
                        EntryPair(match.left[level] as Map.Entry<*, *>, match.right[candidate] as Map.Entry<*, *>)
                    } else {
                        ValuePair(match.left[level], match.right[candidate])
                    }
                    return HostGraphComparison.Equal
                }
                val exhausted = if (match.sawUnprovable[level]) {
                    HostGraphComparison.Unprovable()
                } else {
                    HostGraphComparison.Different
                }
                if (level == 0) {
                    matches.removeAt(matches.lastIndex)
                    return exhausted
                }
                match.level = level - 1
                match.used[match.chosen[level - 1]] = false
                if (exhausted is HostGraphComparison.Unprovable) match.sawUnprovable[level - 1] = true
                undo(match, level - 1)
            }
        }

        /** Removes the bindings made since [level]'s attempt began and charges its steps as discarded. */
        private fun undo(match: UnorderedMatch, level: Int) {
            while (boundLeft.size > match.markBindings[level]) {
                leftToRight.remove(boundLeft.removeAt(boundLeft.lastIndex))
                rightToLeft.remove(boundRight.removeAt(boundRight.lastIndex))
            }
            discardedSteps = match.markDiscardedSteps[level] + (steps - match.markSteps[level])
        }
    }

    /** Pending work. Cursor steps go back on the stack before visiting their next pair. */
    private sealed interface Step

    private class ValuePair(val left: Any?, val right: Any?) : Step

    private class EntryPair(val left: Map.Entry<*, *>, val right: Map.Entry<*, *>) : Step

    private class Ordered(left: Iterable<*>, right: Iterable<*>, val entries: Boolean) : Step {
        val left: Iterator<*> = left.iterator()
        val right: Iterator<*> = right.iterator()
    }

    private class ArrayElements(
        val left: Any,
        val right: Any,
        val expectedLeft: List<Any?>?,
        val length: Int,
    ) : Step {
        var index = 0
    }

    private class FieldValues(val left: Any, val right: Any, val fields: List<Field>) : Step {
        var index = 0
    }

    /** A sorted container's size differs; reported only after its comparator compares Equal. */
    private data object SizeMismatch : Step

    /**
     * Backtracking search pairing each left element with an unused right element of equal fingerprint.
     * Arrays are indexed by level, the position in [left] being paired.
     */
    private class UnorderedMatch(
        val base: Int,
        val left: List<Any?>,
        val right: List<Any?>,
        val entries: Boolean,
        val buckets: List<List<Int>>,
    ) {
        var level = 0
        val used = BooleanArray(right.size)
        val cursor = IntArray(left.size)
        val chosen = IntArray(left.size)
        val sawUnprovable = BooleanArray(left.size)
        val markBindings = IntArray(left.size)
        val markSteps = LongArray(left.size)
        val markDiscardedSteps = LongArray(left.size)
    }

    private fun entryFingerprint(entry: Any?): String {
        entry as Map.Entry<*, *>
        return "${safeFingerprint(entry.key)}=>${safeFingerprint(entry.value)}"
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

    private companion object {
        /**
         * Steps a comparison may spend on unordered-match attempts it later undoes. Members with equal
         * fingerprints can force the search through every pairing, so past this the answer is Unprovable.
         */
        const val MAX_DISCARDED_STEPS = 1_000_000L
    }
}
