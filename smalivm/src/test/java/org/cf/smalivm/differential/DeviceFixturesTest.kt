package org.cf.smalivm.differential

import java.io.File
import java.nio.file.Files
import org.cf.smalivm.core.ClassIndex
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.dex.SmaliParser
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.cf.smalivm.core.requireMethodView

class DeviceFixturesTest {

    @Test
    fun theCorpusTheEnginesReadIsTheSameDirectoryTheDeviceScriptsAssemble() {
        val runBatch = DeviceFixtures.repoRoot.resolve("tools/art-runner/run-batch.sh")
        assertTrue(
            runBatch.isFile,
            "the device side's batch script is missing at ${runBatch.absolutePath}, so this test cannot " +
                "say where the device looks for its fixtures",
        )
        val deviceSideCorpus = runBatch.parentFile.resolve("fixtures")
        assertEquals(
            deviceSideCorpus.canonicalFile,
            DeviceFixtures.directory.canonicalFile,
            "the in-JVM engines are NOT reading the directory the device runs. Global Constraint 4: a " +
                "divergence in a copied fixture is indistinguishable from a divergence in the runtimes",
        )
        assertTrue(
            Files.isSameFile(deviceSideCorpus.toPath(), DeviceFixtures.directory.toPath()),
            "${DeviceFixtures.directory.absolutePath} is not the same directory as " +
                "${deviceSideCorpus.absolutePath} (same path text, different file)",
        )
    }

    @Test
    fun theInvocableEnumerationIsRunBatchsClassDirectiveRuleAndExcludesTheProbes() {
        val invocable = DeviceFixtures.classNames
        assertTrue(invocable.isNotEmpty(), "an empty device corpus makes every device test vacuous")
        assertEquals(invocable.sorted(), invocable, "run-batch.sh pipes its enumeration through `sort`")
        assertEquals(invocable.distinct(), invocable, "a class name enumerated twice would run twice")
        assertTrue(
            invocable.all { it.startsWith(DeviceFixtures.INVOCABLE_PREFIX) },
            "run-batch.sh only auto-invokes ${DeviceFixtures.INVOCABLE_PREFIX}*: $invocable",
        )

        val engineSees = deviceIndex.currentSnapshot.applicationClasses.keys
            .map { it.removePrefix("L").removeSuffix(";") }
        assertEquals(
            DeviceFixtures.allClassNames.toSet(),
            engineSees.toSet(),
            "the engine's class model and the `.class`-directive enumeration disagree about which classes " +
                "the corpus holds",
        )
        assertEquals(
            invocable.toSet(),
            engineSees.filter { it.startsWith(DeviceFixtures.INVOCABLE_PREFIX) }.toSet(),
            "the invocable set the engines would sweep is not the invocable set run-batch.sh dispatches",
        )

        assertTrue(
            DeviceFixtures.supportClassNames.isNotEmpty(),
            "no support classes left in the corpus, so the ${DeviceFixtures.INVOCABLE_PREFIX} filter is " +
                "untested: an over-broad enumeration would pass every assertion above",
        )
        assertTrue(
            DeviceFixtures.supportClassNames.none { it.startsWith(DeviceFixtures.INVOCABLE_PREFIX) },
            "a support class named ${DeviceFixtures.INVOCABLE_PREFIX}* would be auto-invoked by " +
                "run-batch.sh and initialized by Class.forName: ${DeviceFixtures.supportClassNames}",
        )
        assertEquals(
            DeviceFixtures.allClassNames.sorted(),
            (invocable + DeviceFixtures.supportClassNames).sorted(),
            "every class in the corpus is either invocable or support, with no third category",
        )
    }

    @Test
    fun theEnumeratedFixturesAreExactlyTheOnesTheDeviceGoldenRecords() {
        val golden = DeviceFixtures.repoRoot.resolve("tools/art-runner/golden/tier-a-batch.txt")
        assertTrue(golden.isFile, "the device golden is missing at ${golden.absolutePath}")
        val recorded = golden.readLines()
            .filter { it.startsWith("FIXTURE\t") }
            .map { it.split('\t')[1] }

        assertEquals(
            recorded,
            DeviceFixtures.classNames,
            "the corpus the engines enumerate and the corpus the device ran are not the same set. If a " +
                "fixture was added, re-record with tools/art-runner/check-golden.sh --update",
        )
    }

    @Test
    fun theCoreParsesTheCorpusAndResolvesEveryFixtureEntryPoint() {
        val dexBuilder = DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL))
        val parsed = DeviceFixtures.files.map { file ->
            val classDef = file.inputStream().use { SmaliParser.parse(file.path, it, dexBuilder, SmaliParser.DEX_API_LEVEL) }
            classDef.type.removePrefix("L").removeSuffix(";")
        }
        assertEquals(
            DeviceFixtures.allClassNames.sorted(),
            parsed.sorted(),
            "the classes SmaliParser actually parsed out of the corpus are not the classes the " +
                "enumeration claims are in it",
        )

        val coreIndex = deviceIndex
        for (fixture in DeviceFixtures.fixtures) {
            assertEquals(
                DeviceFixtures.ENTRY_POINT,
                fixture.methodDescriptor,
                "every device fixture is reached through the dispatcher's one entry point",
            )

            coreIndex.requireMethodView(fixture.className, fixture.methodDescriptor)
        }
        for (name in DeviceFixtures.supportClassNames) {
            assertTrue(
                coreIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("L$name;")),
                "support class $name must be loadable -- the fixture that measures it needs it resolvable, " +
                    "just never invoked",
            )
        }
    }

    @Test
    fun theAssembledFixtureDexIsStampedAtTheEnginesApiLevel() {
        val fixturesDex = DeviceFixtures.repoRoot.resolve("build/art-runner/art-fixtures.dex")
        assumeTrue(
            fixturesDex.isFile,
            "no assembled device dex on this machine (tools/art-runner/assemble.sh has never run here), so " +
                "there is nothing built to check the API-level stamp against -- skipped, not silently passed",
        )
        val stamp = DeviceFixtures.repoRoot.resolve("build/art-runner/api-level.txt")
        assertTrue(
            stamp.isFile,
            "${fixturesDex.absolutePath} exists but its api-level.txt stamp does not. assemble.sh writes the " +
                "stamp on every run, so a dex with no stamp is a broken build: re-run assemble.sh --force",
        )
        assertEquals(
            SmaliParser.DEX_API_LEVEL.toString(),
            stamp.readText().trim(),
            "the assembled device dex is stamped at a different API level than the engines parse at. " +
                "assemble.sh now keys staleness on the API level, so this means DEX_API_LEVEL changed and the " +
                "dexes were not rebuilt: re-run tools/art-runner/assemble.sh --force (plan Global Constraint 6)",
        )
    }

    private companion object {

        private val deviceIndex: ClassIndex by lazy { ClassIndex.open(File(DeviceFixtures.classPath)) }
    }
}
