package org.cf.smalivm.differential

import java.io.File

/**
 * ART's answers from the committed Tier A recording; this adapter executes no VM code. Each record is one
 * fixture's final answer, so fixtures must not depend on batch order. Missing or malformed records are harness
 * failures, never VM aborts.
 */
class DeviceVm(private val golden: File = DeviceFixtures.golden) {

    sealed interface Outcome {
        /** [type] is a dotted binary name and [repr] the value's text. */
        data class Returned(val type: String, val repr: String) : Outcome

        /** [throwableClass] is a dotted binary name. */
        data class Threw(val throwableClass: String, val message: String?) : Outcome

        /** The core does not verify, so a `VerifyError` describes a rejected program with no execution to compare. */
        val isVerifyError: Boolean get() = this is Threw && throwableClass == VERIFY_ERROR
    }

    private val outcomes: Map<String, Outcome> by lazy { parse(golden) }

    fun run(fixture: Fixture): NormalizedResult = when (val outcome = outcomeOf(fixture)) {
        is Outcome.Returned -> NormalizedResult(
            registers = emptyMap(),
            reachedAddresses = emptySet(),
            unhandledExceptions = emptyList(),
            aborted = false,
            // The recording uses runtime binary names; deviceComparable reconciles the core's descriptors.
            returnValue = NormalizedValue(ValueKind.KNOWN, outcome.type, outcome.repr),
        )
        is Outcome.Threw -> NormalizedResult(
            registers = emptyMap(),
            reachedAddresses = emptySet(),
            unhandledExceptions = listOf(ResultProjection.exceptionEntry(outcome.throwableClass, outcome.message)),
            aborted = false,
            returnValue = null,
        )
    }

    fun outcomeOf(fixture: Fixture): Outcome = outcomes[fixture.id]
        ?: throw DeviceGoldenException(
            "the device golden (${golden.absolutePath}) has no record for '${fixture.id}': a HARNESS failure " +
                "(stale golden or renamed fixture), not an engine abort. Known records: ${outcomes.keys.sorted()}",
        )

    private fun parse(file: File): Map<String, Outcome> {
        if (!file.isFile) {
            throw DeviceGoldenException("the device golden is not a file: ${file.absolutePath}")
        }
        val result = LinkedHashMap<String, Outcome>()
        // A class-init print can interleave into a fixture's FIXTURE line, leaving it without OK/THREW; the
        // answer is then the next OK/THREW line. DISPATCH_END clears a pending class.
        var pendingClass: String? = null
        for (raw in file.readLines()) {
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) continue
            val fields = line.split('\t')
            when (fields.first()) {
                "RUNTIME" -> Unit
                "DISPATCH_END" -> pendingClass = null
                "FIXTURE" -> {
                    val className = fields.getOrNull(1)
                        ?: throw DeviceGoldenException("malformed FIXTURE line in golden: '$line'")
                    val outcome = outcomeFrom(fields, from = 2)
                    if (outcome != null) {
                        result[className] = outcome
                        pendingClass = null
                    } else {
                        pendingClass = className
                    }
                }
                "OK", "THREW" -> {
                    val className = pendingClass
                        ?: throw DeviceGoldenException("golden continuation line with no pending FIXTURE: '$line'")
                    result[className] = outcomeFrom(fields, from = 0)
                        ?: throw DeviceGoldenException("unparseable golden continuation line: '$line'")
                    pendingClass = null
                }
                // Any other interleaved print while a fixture is pending is output, not an answer.
                else -> Unit
            }
        }
        return result
    }

    /** The [Outcome] named by an `OK`/`THREW` marker at or after [from], or null if the line carries none. */
    private fun outcomeFrom(fields: List<String>, from: Int): Outcome? {
        val marker = (from until fields.size).firstOrNull { fields[it] == "OK" || fields[it] == "THREW" }
            ?: return null
        return when (fields[marker]) {
            "OK" -> {
                val describe = fields.getOrNull(marker + 1)
                    ?: throw DeviceGoldenException("golden OK with no value: $fields")
                val (type, repr) = parseDescribe(describe)
                Outcome.Returned(type, repr)
            }
            else -> { // THREW
                val throwableClass = fields.getOrNull(marker + 1)
                    ?: throw DeviceGoldenException("golden THREW with no throwable class: $fields")
                // The message is the rest of the line, rejoined on the tabs it was split on.
                val message = fields.drop(marker + 2).joinToString("\t").ifEmpty { null }
                Outcome.Threw(throwableClass, message)
            }
        }
    }

    /** `"java.lang.Integer(-1)"` -> `("java.lang.Integer", "-1")`: type before the first `(`, repr to the last `)`. */
    private fun parseDescribe(describe: String): Pair<String, String> {
        val open = describe.indexOf('(')
        val close = describe.lastIndexOf(')')
        if (open < 0 || close <= open) {
            throw DeviceGoldenException("unparseable golden OK describe (want 'type(value)'): '$describe'")
        }
        return describe.substring(0, open) to describe.substring(open + 1, close)
    }

    companion object {
        const val VERIFY_ERROR: String = "java.lang.VerifyError"
    }
}

/** The recording cannot answer for a fixture (no record or an unparseable line). */
class DeviceGoldenException(message: String) : RuntimeException(message)
