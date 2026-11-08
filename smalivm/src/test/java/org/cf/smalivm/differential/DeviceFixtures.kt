package org.cf.smalivm.differential

import java.io.File

/**
 * The ART runner's fixture corpus, read in place: `run-batch.sh` assembles these same files for ART.
 * Paths are relative to the module directory, which is the test working directory for every suite here.
 */
object DeviceFixtures {
    /** The dispatcher calls `public static Object run()` on each fixture; boxing the result is the fixture's job. */
    const val ENTRY_POINT: String = "run()Ljava/lang/Object;"

    /**
     * `run-batch.sh` invokes only `Fx_` classes. Other classes are support code, such as `<clinit>` witnesses that
     * must not be initialized by being invoked.
     */
    const val INVOCABLE_PREFIX: String = "Fx_"

    private val CLASS_DIRECTIVE = Regex("""^\.class[^L]*L([^;]*);""")

    val artRunner: File = File("../tools/art-runner").canonicalFile
    val directory: File = artRunner.resolve("fixtures")
    val golden: File = artRunner.resolve("golden/tier-a-batch.txt")

    val files: List<File> = directory.listFiles { file -> file.isFile && file.name.endsWith(".smali") }
        ?.sortedBy { it.name }
        ?.takeIf { it.isNotEmpty() }
        ?: error("no .smali fixtures in ${directory.path}; the device comparison would be vacuous")

    /** Class names from the `.class` directives, as `run-batch.sh` reads them, in `LC_ALL=C sort` order. */
    val allClassNames: List<String> = files.map { file ->
        file.readLines().mapNotNull { CLASS_DIRECTIVE.find(it)?.groupValues?.get(1) }.singleOrNull()
            ?: error("expected exactly one .class directive in ${file.path}")
    }.sorted()

    /** The fixtures `run-batch.sh` dispatches, in dispatch order. */
    val classNames: List<String> = allClassNames.filter { it.startsWith(INVOCABLE_PREFIX) }

    val supportClassNames: List<String> = allClassNames.filterNot { it.startsWith(INVOCABLE_PREFIX) }

    /** The device reports a return value, never registers, so no register is observed. */
    val fixtures: List<Fixture> = classNames.map { name ->
        Fixture(id = name, className = "L$name;", methodDescriptor = ENTRY_POINT, observeRegisters = emptySet())
    }
}
