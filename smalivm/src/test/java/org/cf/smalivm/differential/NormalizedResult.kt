package org.cf.smalivm.differential

enum class ValueKind {
    KNOWN,
    NULL,
    UNKNOWN,

    /** No returning path held this register. */
    ABSENT,

    /** A held register or return has incompatible joined values. */
    CONFLICTED,
}

data class NormalizedValue(val kind: ValueKind, val type: String?, val repr: String?)

/** Register consensus and outcome evidence; device comparisons restrict this to their shared observables. */
data class NormalizedResult(
    val registers: Map<Int, NormalizedValue>,
    val reachedAddresses: Set<Int>,
    val unhandledExceptions: List<String>,
    val aborted: Boolean,
    /** Null means no value returned, not a known-null return (which has ValueKind.NULL). */
    val returnValue: NormalizedValue? = null,
)

data class ResultDiff(val differences: List<String>) {
    val identical: Boolean get() = differences.isEmpty()
}

fun NormalizedResult.diff(other: NormalizedResult): ResultDiff {
    val diffs = mutableListOf<String>()
    for (r in (registers.keys + other.registers.keys).sorted()) {
        val a = registers[r]
        val b = other.registers[r]
        if (a != b) diffs.add("register $r: $a != $b")
    }
    if (reachedAddresses != other.reachedAddresses) {
        diffs.add("reachedAddresses: ${reachedAddresses.sorted()} != ${other.reachedAddresses.sorted()}")
    }
    if (unhandledExceptions != other.unhandledExceptions) {
        diffs.add("unhandledExceptions: $unhandledExceptions != ${other.unhandledExceptions}")
    }
    if (aborted != other.aborted) {
        diffs.add("aborted: $aborted != ${other.aborted}")
    }

    if (returnValue != other.returnValue) {
        diffs.add("returnValue: $returnValue != ${other.returnValue}")
    }
    return ResultDiff(diffs)
}
