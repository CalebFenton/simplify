package org.cf.smalivm.differential

import java.io.File

/**
 * Independent ART answers from the committed Tier-A recording; this adapter executes no VM code.
 * Fixtures must not depend on batch order or state carried from another fixture, because the recording
 * contains only each final answer. VerifyError records are exposed separately: rejected bytecode has
 * no execution to compare. Missing or malformed records are loud harness failures, never VM aborts.
 */
class DeviceVm(
    private val golden: File = DeviceFixtures.repoRoot.resolve(GOLDEN_PATH),
) : SmaliVmUnderTest {

    override val name: String = "device"

    /** One recorded device answer. A device does exactly one of these per fixture, then dies or returns. */
    sealed interface Outcome {
        /** The device returned a (boxed) value. [type] is a DOTTED binary name, [repr] the value's text. */
        data class Returned(val type: String, val repr: String) : Outcome

        /** The device threw and the throwable escaped `run`. [throwableClass] is a DOTTED binary name. */
        data class Threw(val throwableClass: String, val message: String?) : Outcome

        /**
         * A `VerifyError` is a [Threw] like any other in the recording, but the differential must not treat
         * it as one: the engine does not verify at all -- it deliberately executes unverified, hostile input
         * -- so a device `VerifyError` cannot be compared against anything the engine did. The sweep asks this
         * to route the fixture to its own path.
         */
        val isVerifyError: Boolean get() = this is Threw && throwableClass == VERIFY_ERROR
    }

    /** The whole recording, class name -> answer, parsed once. [Fixture.id] IS the class name. */
    private val outcomes: Map<String, Outcome> by lazy { parse(golden) }

    override fun run(fixture: Fixture): NormalizedResult = when (val outcome = outcomeOf(fixture)) {
        is Outcome.Returned -> NormalizedResult(
            registers = emptyMap(),
            reachedAddresses = emptySet(),
            unhandledExceptions = emptyList(),
            aborted = false,
            // The recording uses runtime binary names; deviceComparable reconciles core descriptors.
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

    /**
     * The recorded answer for [fixture], or a LOUD failure. Exposed so the sweep can route a `VerifyError`
     * ([Outcome.isVerifyError]) to its own path before it ever reaches the diff.
     */
    fun outcomeOf(fixture: Fixture): Outcome = outcomes[fixture.id]
        ?: throw DeviceGoldenException(
            "the device golden (${golden.absolutePath}) has no record for '${fixture.id}', so the device " +
                "could not have run it -- this is a HARNESS failure (a stale golden, a renamed fixture, or a " +
                "corpus/golden drift `check-golden.sh` should have caught), NOT an engine abort. Known " +
                "records: ${outcomes.keys.sorted()}",
        )

    private fun parse(file: File): Map<String, Outcome> {
        if (!file.isFile) {
            throw DeviceGoldenException("the device golden is not a file: ${file.absolutePath}")
        }
        val result = LinkedHashMap<String, Outcome>()
        // `pendingClass` is the ONE trap the recording sets (Task 4 parse trap 1): a class-init print can
        // interleave into a fixture's dispatcher line, so `Fx_StaticFieldDoesInitialize` reads
        // `FIXTURE ... CLINIT_RAN Probe_ClinitWitnessB` with no OK/THREW, and its real answer `OK ...(9)` is
        // the NEXT physical line. So a FIXTURE with no marker leaves its class pending, and the next OK/THREW
        // line resolves it. Reset at DISPATCH_END so a pending class can never leak across the batch boundary.
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
                // Any other interleaved print (a witness line, a marker like CLINIT_RAN standing alone)
                // while a fixture is pending: ignored, exactly as the dispatcher's own output is noise
                // between the FIXTURE line and its answer.
                else -> Unit
            }
        }
        return result
    }

    /**
     * The [Outcome] named by an `OK`/`THREW` marker at or after [from] in [fields], or null if this line
     * carries no marker (a bare `CLINIT_RAN`/witness line, which leaves its FIXTURE pending).
     */
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
                // The message is the remainder, rejoined on the tab it was split on so a tab inside a message
                // survives; `null` when absent. Only the type is ever compared (deviceComparable strips the
                // message), so this is kept for a readable report, not for the diff.
                val message = fields.drop(marker + 2).joinToString("\t").ifEmpty { null }
                Outcome.Threw(throwableClass, message)
            }
        }
    }

    /**
     * `"java.lang.Integer(-2147483648)"` -> `("java.lang.Integer", "-2147483648")`. The type is everything
     * before the FIRST `(`; the repr is the text inside the OUTERMOST parens (first `(` to LAST `)`), so a
     * value whose own text contains parentheses survives intact.
     */
    private fun parseDescribe(describe: String): Pair<String, String> {
        val open = describe.indexOf('(')
        val close = describe.lastIndexOf(')')
        if (open < 0 || close <= open) {
            throw DeviceGoldenException("unparseable golden OK describe (want 'type(value)'): '$describe'")
        }
        return describe.substring(0, open) to describe.substring(open + 1, close)
    }

    companion object {
        /** Relative to [DeviceFixtures.repoRoot] -- the ONE resolved root, so no consumer spells its own. */
        const val GOLDEN_PATH: String = "tools/art-runner/golden/tier-a-batch.txt"

        /** The one throwable the differential refuses to treat as an observation. See [Outcome.isVerifyError]. */
        const val VERIFY_ERROR: String = "java.lang.VerifyError"
    }
}

/**
 * The device could not have run a fixture (no record, a missing marker, an unparseable line). A HARNESS
 * failure, deliberately LOUD and deliberately NOT an engine `aborted` -- see [DeviceVm]'s KDoc.
 */
class DeviceGoldenException(message: String) : RuntimeException(message)
