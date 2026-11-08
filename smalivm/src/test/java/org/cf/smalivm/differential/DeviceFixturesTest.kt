package org.cf.smalivm.differential

import java.io.File
import java.security.MessageDigest
import java.util.HexFormat
import org.cf.smalivm.core.ClassIndex
import org.cf.smalivm.dex.SmaliParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeviceFixturesTest {
    private val goldenHeader = DeviceFixtures.golden.readLines().takeWhile { it.startsWith("#") }

    @Test
    fun theCoreLoadsEveryCorpusClassAndResolvesEveryFixtureEntryPoint() {
        val index = ClassIndex.open(DeviceFixtures.directory)
        val loaded = index.currentSnapshot.applicationClasses.keys.map { it.removePrefix("L").removeSuffix(";") }
        assertEquals(
            DeviceFixtures.allClassNames.toSet(), loaded.toSet(),
            "the .class-directive enumeration run-batch.sh uses and the core's class model disagree",
        )
        assertTrue(
            DeviceFixtures.supportClassNames.isNotEmpty(),
            "no class is excluded by the ${DeviceFixtures.INVOCABLE_PREFIX} filter, so nothing exercises it",
        )
        DeviceFixtures.fixtures.forEach { index.requireMethodView(it.className, it.methodDescriptor) }
    }

    @Test
    fun theGoldenRecordsExactlyTheDispatchedFixtures() {
        val recorded = DeviceFixtures.golden.readLines()
            .filter { it.startsWith("FIXTURE\t") }
            .map { it.split('\t')[1] }
        assertEquals(
            DeviceFixtures.classNames, recorded,
            "the golden and the corpus list different fixtures; record the corpus with " +
                "tools/art-runner/check-golden.sh --update",
        )
    }

    @Test
    fun theGoldenWasRecordedFromTheCurrentFixtureSources() {
        val recorded = goldenHeader.mapNotNull { SOURCE_HASH.matchEntire(it) }
            .associate { it.groupValues[2] to it.groupValues[1] }
        val sources = DeviceFixtures.artRunner.resolve("dispatcher").listFiles().orEmpty().toList() + DeviceFixtures.files
        val current = sources.filter { it.name.endsWith(".smali") }
            .associate { it.relativeTo(DeviceFixtures.artRunner).invariantSeparatorsPath to sha256(it) }
        val stale = (recorded.keys + current.keys).sorted().filter { recorded[it] != current[it] }
        assertTrue(stale.isEmpty()) {
            "${DeviceFixtures.golden.path} was recorded from different sources, so its ART answers may not " +
                "hold for these programs:\n" +
                stale.joinToString("\n") { "  $it: recorded ${recorded[it] ?: "absent"}, now ${current[it] ?: "absent"}" } +
                "\nRevert the source change, or re-record with tools/art-runner/check-golden.sh --update."
        }
    }

    @Test
    fun theGoldenWasRecordedFromDexAssembledAtTheEnginesApiLevel() {
        val recorded = goldenHeader.firstNotNullOfOrNull { API_LEVEL.matchEntire(it)?.groupValues?.get(1) }
        assertEquals(
            SmaliParser.DEX_API_LEVEL.toString(), recorded,
            "the ART recording was assembled at a different API level than the core parses; re-record with " +
                "tools/art-runner/check-golden.sh --update",
        )
    }

    private companion object {
        private val SOURCE_HASH = Regex("""^# source-sha256: ([0-9a-f]{64}) {2}(\S+)$""")
        private val API_LEVEL = Regex("""^# dex-api-level: (\d+)$""")

        private fun sha256(file: File): String =
            HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(file.readBytes()))
    }
}
