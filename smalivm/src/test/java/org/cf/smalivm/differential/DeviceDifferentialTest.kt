package org.cf.smalivm.differential

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * The current core against the committed ART `-Xint` recording, comparing returned runtime type and value,
 * escaping exception types, and completion. Every fixture agrees except a named environment boundary, which
 * asserts both sides' answers; a `VerifyError` record has no execution to compare.
 */
class DeviceDifferentialTest {
    @TestFactory
    fun coreResultsMatchRecordedArtOrAnExplicitEnvironmentBoundary(): List<DynamicTest> =
        DeviceFixtures.fixtures.map { fixture ->
            DynamicTest.dynamicTest(fixture.id) {
                val recorded = DEVICE.outcomeOf(fixture)
                if (recorded.isVerifyError) {
                    assertEquals("Fx_VerifyErrorBadReturn", fixture.id)
                    return@dynamicTest
                }
                val actual = ResultProjection.deviceComparable(CORE.run(fixture))
                val expected = ResultProjection.deviceComparable(DEVICE.run(fixture))
                val boundary = ENVIRONMENT_BOUNDARIES[fixture.id]
                if (boundary == null) {
                    assertEquals(expected, actual, "${fixture.id}: ${expected.diff(actual).differences}")
                    return@dynamicTest
                }
                assertEquals(boundary.device, expected, "${fixture.id}: recorded boundary")
                assertEquals(boundary.core, actual, "${fixture.id}: ${boundary.reason}")
                assertFalse(expected.diff(actual).identical, "${fixture.id}: a boundary that agrees is no longer one")
            }
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
        assertTrue(failure.message!!.contains("HARNESS"), "got: ${failure.message}")
    }

    private companion object {
        private val DEVICE = DeviceVm()
        private val CORE = CoreProjection(DeviceFixtures.directory.path)

        private data class Boundary(
            val device: NormalizedResult,
            val core: NormalizedResult,
            val reason: String,
        )

        private fun returned(kind: ValueKind, repr: String?) = NormalizedResult(
            emptyMap(), emptySet(), emptyList(), false,
            NormalizedValue(kind, "java.lang.String", repr),
        )

        private fun threw(type: String) = NormalizedResult(emptyMap(), emptySet(), listOf(type), false)

        /** Standalone ART has no `android.*` classes and no `libandroid_runtime.so`, so it cannot run these programs. */
        private val ENVIRONMENT_BOUNDARIES = mapOf(
            "Fx_AndroidBase64" to Boundary(
                threw("java.lang.NoClassDefFoundError"), returned(ValueKind.KNOWN, "aGk="),
                "the core runs the bundled android.util.Base64; base64 of \"hi\" is aGk= (RFC 4648 section 4), " +
                    "which tools/emulator's Tier B recording also shows",
            ),
            "Fx_AndroidBuildModel" to Boundary(
                threw("java.lang.NoClassDefFoundError"), returned(ValueKind.UNKNOWN, null),
                "Build.MODEL depends on the device, so the core returns an unknown String",
            ),
            "Fx_AndroidBuildModelAfterLoadLibrary" to Boundary(
                threw("java.lang.UnsatisfiedLinkError"), returned(ValueKind.UNKNOWN, null),
                "the core denies System.loadLibrary, so Build.MODEL stays an unknown String",
            ),
        )
    }
}
