package org.cf.smalivm.differential

import org.cf.smalivm.core.MethodDescriptor
import org.cf.smalivm.core.ReflectionDecision
import org.cf.smalivm.core.ReflectionPolicy
import org.cf.smalivm.core.ReflectionRule
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * Current core against committed Tier-A ART -Xint recordings, without a device dependency at test time.
 * Compare returned runtime type/value, escaping exception types, and completion. Registers, address
 * coverage, exception messages, and interleaved class-initializer prints are not device observables.
 *
 * VerifyError records describe rejected programs, not executions; they are asserted separately.
 * Named environment and policy probes are asserted on BOTH sides rather than called agreements:
 * standalone ART lacks Android classes/native libraries, while V1 bundles Base64 and deliberately
 * declines environment-dependent Build/System operations and non-authorized String.valueOf(I).
 * Unknown is never equal to a concrete answer. Every other fixture must agree.
 * Host failures and semantic disagreements fail their own dynamic test;
 * they cannot drain into a non-compared bucket. The committed recordings are never regenerated here.
 */
class DeviceDifferentialTest {
    @TestFactory
    fun coreResultsMatchRecordedArtOrAnExplicitEnvironmentOrPolicyBoundary(): List<DynamicTest> =
        Fixtures.device.map { fixture ->
            DynamicTest.dynamicTest(fixture.id) {
                val recorded = DEVICE.outcomeOf(fixture)
                if (recorded.isVerifyError) {
                    assertEquals("Fx_VerifyErrorBadReturn", fixture.id)
                    assertEquals("java.lang.VerifyError", (recorded as DeviceVm.Outcome.Threw).throwableClass)
                } else {
                    val actual = observations.getValue(fixture.id).getOrThrow()
                    val expected = ResultProjection.deviceComparable(DEVICE.run(fixture))
                    val boundary = EXECUTION_BOUNDARIES[fixture.id]
                    if (boundary == null) {
                        assertEquals(expected, actual, "${fixture.id}: ${expected.diff(actual).differences}")
                    } else {
                        boundary.deniedHostMethod?.let { target ->
                            assertEquals(
                                ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY),
                                ReflectionPolicy.defaults().decide(target),
                                "${fixture.id}: the policy boundary requires an independently denied host method",
                            )
                        }
                        assertEquals(boundary.device, expected, "${fixture.id}: recorded boundary")
                        assertEquals(boundary.core, actual, "${fixture.id}: ${boundary.reason}")
                        assertFalse(expected.diff(actual).identical, "a boundary is not runtime agreement")
                    }
                }
            }
        }


    @Test
    fun theRestrictionStripsExceptionMessagesSymmetricallySoOnlyTheTypeIsCompared() {

        val deviceSide = NormalizedResult(
            registers = emptyMap(),
            reachedAddresses = emptySet(),
            unhandledExceptions = listOf(
                ResultProjection.exceptionEntry("java.lang.NullPointerException", "Attempt to write to null array"),
            ),
            aborted = false,
        )
        val coreSide = deviceSide.copy(
            unhandledExceptions = listOf(ResultProjection.exceptionEntry("java.lang.NullPointerException", null)),
        )
        val device = ResultProjection.deviceComparable(deviceSide)
        val core = ResultProjection.deviceComparable(coreSide)
        assertEquals(listOf("java.lang.NullPointerException"), device.unhandledExceptions, "the message must be stripped")
        assertEquals(
            listOf("java.lang.NullPointerException"), core.unhandledExceptions,
            "the message must be stripped SYMMETRICALLY -- the same projection on both sides",
        )
        assertTrue(
            core.diff(device).identical,
            "same exception type, different messages must agree once stripped",
        )

        assertFalse(
            coreSide.diff(deviceSide).identical,
            "unstripped, two identical-type throws with different messages read as a divergence",
        )
    }

    @Test
    fun theReturnValueTypeIsReconciledFromDescriptorToDottedSoTypeAndValueBothCompare() {

        val coreSide = NormalizedResult(
            registers = emptyMap(), reachedAddresses = emptySet(), unhandledExceptions = emptyList(), aborted = false,
            returnValue = NormalizedValue(ValueKind.KNOWN, "Ljava/lang/Integer;", "1"),
        )
        val deviceSide = coreSide.copy(returnValue = NormalizedValue(ValueKind.KNOWN, "java.lang.Integer", "1"))
        assertTrue(
            ResultProjection.deviceComparable(coreSide).diff(ResultProjection.deviceComparable(deviceSide)).identical,
            "descriptor and dotted spellings of the same type and value must agree after reconciliation",
        )

        val longOne = coreSide.copy(returnValue = NormalizedValue(ValueKind.KNOWN, "Ljava/lang/Long;", "1"))
        assertFalse(
            ResultProjection.deviceComparable(longOne).diff(ResultProjection.deviceComparable(deviceSide)).identical,
            "Integer(1) and Long(1) share a repr but not a type -- comparing repr only would falsely agree",
        )
    }

    @Test
    fun aFixtureTheDeviceNeverReportedIsALoudHarnessFailureNotAnAbort() {

        val neverReported = Fixture(
            id = "Fx_NotInTheGoldenAtAll",
            className = "LFx_NotInTheGoldenAtAll;",
            methodDescriptor = DeviceFixtures.ENTRY_POINT,
            observeRegisters = emptySet(),
        )
        val failure = assertThrows(DeviceGoldenException::class.java) { DEVICE.run(neverReported) }
        assertTrue(
            failure.message!!.contains("HARNESS"),
            "the failure must announce itself as a harness failure, got: ${failure.message}",
        )
    }

    private companion object {
        private val DEVICE = DeviceVm()
        private val CORE = NewCoreVm(DeviceFixtures.classPath)
        private val observations: Map<String, Result<NormalizedResult>> by lazy {
            Fixtures.device.filterNot { DEVICE.outcomeOf(it).isVerifyError }.associate { fixture ->
                fixture.id to runCatching { ResultProjection.deviceComparable(CORE.run(fixture)) }
            }
        }

        private data class ExecutionBoundary(
            val device: NormalizedResult,
            val core: NormalizedResult,
            val reason: String,
            val deniedHostMethod: MethodDescriptor? = null,
        )

        private fun returned(kind: ValueKind, repr: String?) = NormalizedResult(
            emptyMap(), emptySet(), emptyList(), false,
            NormalizedValue(kind, "java.lang.String", repr),
        )

        private fun threw(type: String) = NormalizedResult(emptyMap(), emptySet(), listOf(type), false)

        private val EXECUTION_BOUNDARIES = mapOf(
            "Fx_AndroidBase64" to ExecutionBoundary(
                threw("java.lang.NoClassDefFoundError"), returned(ValueKind.KNOWN, "aGk="),
                "Pinned AOSP source supplies real android.util.Base64.encodeToString([BI): encode the bytes, " +
                    "then construct a US-ASCII String. safe_classes.cfg authorizes Base64, so 'hi' with " +
                    "NO_WRAP returns 'aGk='; standalone ART cannot resolve android.util.Base64.",
            ),
            "Fx_AndroidBuildModel" to ExecutionBoundary(
                threw("java.lang.NoClassDefFoundError"), returned(ValueKind.UNKNOWN, null),
                "Build.MODEL is environment-dependent and is not an authorized host static read; V1 must not fabricate a model.",
            ),
            "Fx_AndroidBuildModelAfterLoadLibrary" to ExecutionBoundary(
                threw("java.lang.UnsatisfiedLinkError"), returned(ValueKind.UNKNOWN, null),
                "System initialization/loadLibrary is denied by policy; opaque continuation cannot make Build.MODEL known.",
            ),
            "Fx_InvokePresentType" to ExecutionBoundary(
                returned(ValueKind.KNOWN, "1"), returned(ValueKind.UNKNOWN, null),
                "ART executes String.valueOf(I) for integer 1. ReflectionPolicy.defaults removes String via " +
                    "unsafe_classes.cfg, and safe_methods.cfg allows only String.<init>([B) and length(), not " +
                    "valueOf(I). DEFAULT_DENY stops backend fallback, so V1 returns Unknown(String).",
                deniedHostMethod = MethodDescriptor(
                    VirtualType.of("Ljava/lang/String;"), "valueOf", "(I)Ljava/lang/String;",
                ),
            ),
        )
    }
}
