package org.cf.smalivm.differential

import java.io.File

/**
 * The device fixture corpus under `tools/art-runner/fixtures`, read directly by the current core.
 * **The same files, not a copy**: this object is the whole of Global Constraint 4's "one source, two
 * consumers", and `DeviceFixturesTest` is what makes that a fact rather than an intention.
 *
 * **Why the corpus lives outside `src/test/resources/smali`.** It is also a `docker build` input, and
 * `tools/art-runner/{assemble.sh,run-batch.sh}` must be able to read it with no Gradle and no classpath.
 * So the test tree has to reach UP out of the module to `tools/art-runner/fixtures`, which is brittle
 * under a changed working directory -- Gradle runs tests with the module directory as `user.dir`, an IDE
 * or a `--project-dir` invocation need not. That is resolved ONCE, here, in [repoRoot]: no consumer may
 * spell its own path to the device side. `DeviceFixturesTest` is the deliberate exception -- it spells the
 * device side's paths itself, because a check that re-used this object's answer as its own reference could
 * not fail.
 *
 * **What a device fixture looks like, and why none of them look like the in-JVM ones.** A device cannot
 * seed registers (Constraint 5), so every fixture builds its own operands in bytecode and exposes
 * [ENTRY_POINT] -- `public static run()Ljava/lang/Object;` -- which the ART-side dispatcher reaches by
 * `Class.forName`. Roughly half of this repo's in-JVM fixtures rely on [Fixture.seededRegisters] and are
 * unportable by construction; this corpus was written for the device, not ported to it.
 *
 * **Two class-name prefixes, one of them never invoked.** [INVOCABLE_PREFIX] (`Fx_`) is a fixture;
 * everything else is a SUPPORT class ([supportClassNames]), today the `Probe_*` `<clinit>` witnesses.
 * `Class.forName` initializes the class it resolves ([JLS] 12.4.1), so invoking a witness directly
 * initializes the very class whose silence another fixture is measuring -- which is why `run-batch.sh`
 * auto-invokes `Fx_*` only, and why [classNames] applies the same filter with the same rule.
 *
 * **Nothing here decides what to OBSERVE, on purpose.** [fixtures] carries no observed registers: a device
 * reports a return value and never register state, so `NormalizedResult`'s register map is not the axis
 * these fixtures are compared on. Choosing that projection is `DeviceVm`'s task, and inheriting a default
 * `{0}` from [Fixture] would have handed it a silent, wrong answer.
 */
object DeviceFixtures {

    /**
     * The contract every device fixture satisfies, and the dispatcher's only assumption about them:
     * `public static Object run()`. Boxing the result is the fixture's job, which is why the descriptor is
     * `Ljava/lang/Object;` rather than a primitive.
     */
    const val ENTRY_POINT: String = "run()Ljava/lang/Object;"

    /** `run-batch.sh`'s auto-invoke filter. See the class KDoc for why it is load-bearing. */
    const val INVOCABLE_PREFIX: String = "Fx_"

    /**
     * Where `assemble.sh` reads [org.cf.smalivm.dex.SmaliParser.DEX_API_LEVEL] from, relative to
     * [repoRoot]. Public because the API-level agreement test replays that read (Constraint 6), and a
     * repo-root marker for the same reason it is worth replaying: if this file moves, the device side
     * assembles at a level nobody chose.
     */
    const val SMALI_PARSER_SOURCE: String = "smalivm/src/main/java/org/cf/smalivm/dex/SmaliParser.kt"

    /** The corpus, relative to [repoRoot]. The device side spells it `$HERE/fixtures`. */
    private const val CORPUS_PATH: String = "tools/art-runner/fixtures"

    /**
     * What makes a directory THIS repository's root. Three files rather than one, and none of them the
     * corpus itself: `settings.gradle` says "a Gradle build", the batch script says "the device side lives
     * here", and the parser source says "the engine side lives here". A directory satisfying all three is
     * not plausibly anything but this repo, and deriving the corpus from it afterwards leaves the corpus
     * FALSIFIABLE -- a marker set that included `tools/art-runner/fixtures` would make "the corpus is
     * where the corpus is" unfalsifiable, which is the one thing Constraint 4 needs to be able to fail.
     */
    private val REPO_MARKERS: List<String> = listOf(
        "settings.gradle",
        "tools/art-runner/run-batch.sh",
        SMALI_PARSER_SOURCE,
    )

    /**
     * The repository root, resolved once and loudly.
     *
     * Walks up from this class's OWN code location first and from `user.dir` second, so the answer does not
     * depend on the working directory a runner happens to choose: the compiled test class sits under
     * `smalivm/build/classes/...`, which is inside the repo no matter who launched the JVM. `user.dir` is
     * kept as a second root rather than dropped, for the case where classes are loaded from somewhere
     * outside the checkout.
     *
     * Fails with every directory it examined and the marker set it wanted, because the failure this
     * replaces -- a `FileNotFoundException` on a relative path, thrown later, from a parser -- names
     * neither.
     */
    val repoRoot: File by lazy { locateRepoRoot() }

    /**
     * The corpus directory: canonical, so a message about it is unambiguous and so identity comparisons
     * against the device side's `$HERE/fixtures` mean what they say.
     */
    val directory: File by lazy {
        val resolved = repoRoot.resolve(CORPUS_PATH).canonicalFile
        check(resolved.isDirectory) {
            "DeviceFixtures: the device fixture corpus is not a directory: ${resolved.absolutePath}\n" +
                "  (repository root ${repoRoot.absolutePath}, corpus path $CORPUS_PATH)\n" +
                "  This is the directory tools/art-runner/{assemble.sh,run-batch.sh} assemble and ship to " +
                "ART. The in-JVM engines must read those same files -- see this class's KDoc."
        }
        resolved
    }

    /** Every `.smali` file in the corpus, ordered by file name. */
    val files: List<File> by lazy {
        val found = (directory.listFiles() ?: emptyArray())
            .filter { it.isFile && it.name.endsWith(SMALI_EXTENSION) }
            .sortedBy { it.name }
        check(found.isNotEmpty()) {
            "DeviceFixtures: no *$SMALI_EXTENSION files in ${directory.absolutePath}, so the device corpus " +
                "is empty and every device comparison would pass vacuously"
        }
        found
    }

    /**
     * Every class in the corpus, from the `.class` directives and never from file names, sorted.
     *
     * The directive is the source because `run-batch.sh` reads the directive: a file renamed away from its
     * class would drop the fixture on one side only, which is precisely the silent divergence this object
     * exists to prevent.
     */
    val allClassNames: List<String> by lazy {
        files.flatMap { file ->
            val names = file.readLines().mapNotNull(::classNameIn)
            check(names.size == 1) {
                "DeviceFixtures: expected exactly one `.class` directive in ${file.absolutePath}, found " +
                    "${names.size}: $names"
            }
            names
        }.sorted()
        // `.sorted()` is `String.compareTo`, i.e. UTF-16 code-unit order, which for these ASCII
        // descriptors is byte order -- the SAME order `run-batch.sh` produces with `LC_ALL=C sort`. That
        // alignment is deliberate and load-bearing: `classNames`' order is asserted equal to the device
        // golden's dispatch order, and the two enumerations must not drift apart on a locale boundary. See
        // `run-batch.sh`'s comment on why the C locale is forced there.
    }

    /**
     * The fixtures `run-batch.sh` auto-invokes, in the order it dispatches them: [CLASS_DIRECTIVE] over
     * every corpus file, filtered to [INVOCABLE_PREFIX], sorted. That is the script's `sed | sort`
     * pipeline, and the `sed` expression is quoted verbatim beside [CLASS_DIRECTIVE] for comparison.
     *
     * Lifting the `Fx_` literal out of the pattern into a filter selects the same set: the pattern can
     * only match at the FIRST `L` on the line either way, because a `[^L]` run cannot cross one.
     */
    val classNames: List<String> by lazy {
        allClassNames.filter { it.startsWith(INVOCABLE_PREFIX) }
    }

    /**
     * The rest: loadable, resolvable, and never auto-invoked. Today the `Probe_*` `<clinit>` witnesses.
     * Kept as a first-class list rather than derived at each use, because "which classes must NOT be
     * invoked" is a claim about the corpus that a test can then hold to.
     */
    val supportClassNames: List<String> by lazy {
        allClassNames.filterNot { it.startsWith(INVOCABLE_PREFIX) }
    }

    /**
     * The corpus as [Fixture]s, one per invocable class, derived from [classNames] rather than listed:
     * a hand-maintained list is a second source, and a second source is the thing Constraint 4 forbids.
     *
     * [Fixture.id] is the bare class name -- `Fx_DivOverflow`, not `device-div-overflow` -- so one string
     * greps across the `.smali` file, `run-batch.sh`'s argument, the golden's `FIXTURE` record, the ART
     * dispatcher's output line and any differential report. [Fixture.observeRegisters] is empty; see the
     * class KDoc for why that is a decision and not an omission.
     */
    val fixtures: List<Fixture> by lazy {
        classNames.map { name ->
            Fixture(
                id = name,
                className = "L$name;",
                methodDescriptor = ENTRY_POINT,
                observeRegisters = emptySet(),
            )
        }
    }

    /**
     * The corpus class path for [NewCoreVm]. One expression prevents consumers from drifting to a copy.
     */
    val classPath: String get() = directory.path

    /**
     * `.class public LFx_DivOverflow;` -> `Fx_DivOverflow`, by `run-batch.sh`'s rule with the prefix
     * filter lifted out. `null` for any other line, including a comment that mentions a descriptor: the
     * directive must start the line, exactly as `sed`'s `^\.class` requires.
     */
    private fun classNameIn(line: String): String? =
        CLASS_DIRECTIVE.find(line)?.groupValues?.get(1)

    private fun locateRepoRoot(): File {
        val roots = linkedMapOf(
            "this class's code location" to codeLocation(),
            "the working directory" to File(System.getProperty("user.dir")).absoluteFile,
        )
        val examined = mutableListOf<String>()
        for ((description, start) in roots) {
            if (start == null) {
                examined.add("  $description: unavailable")
                continue
            }
            examined.add("  $description: ${start.absolutePath}")
            for (candidate in generateSequence(start.canonicalFile) { it.parentFile }) {
                if (REPO_MARKERS.all { candidate.resolve(it).exists() }) return candidate
                examined.add("    ${candidate.absolutePath}")
            }
        }
        throw IllegalStateException(
            "DeviceFixtures: cannot locate the repository root, so the device fixture corpus " +
                "($CORPUS_PATH) cannot be read.\n" +
                "  A root is a directory containing ALL of: ${REPO_MARKERS.joinToString(", ")}\n" +
                "Searched upward from:\n" + examined.joinToString("\n"),
        )
    }

    /** The directory holding this class's own compiled form, or null if the JVM will not say. */
    private fun codeLocation(): File? = runCatching {
        val location = DeviceFixtures::class.java.protectionDomain?.codeSource?.location
        val file = location?.let { File(it.toURI()).absoluteFile }
        when {
            file == null -> null
            file.isDirectory -> file
            else -> file.parentFile
        }
    }.getOrNull()

    private const val SMALI_EXTENSION: String = ".smali"

    // `run-batch.sh`'s enumeration, verbatim, with the `Fx_` filter lifted out into [INVOCABLE_PREFIX]:
    //     sed -n 's/^\.class[^L]*L\(Fx_[^;]*\);.*/\1/p' "$HERE"/fixtures/*.smali | sort
    // Quoted in a line comment rather than in the KDoc above on purpose: Kotlin block comments NEST, so a
    // glob or a `sed` replacement inside one silently swallows the rest of the file.
    private val CLASS_DIRECTIVE: Regex = Regex("""^\.class[^L]*L([^;]*);""")
}
