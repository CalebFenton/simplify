package org.cf.simplify

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.apache.commons.cli.help.HelpFormatter
import org.apache.commons.cli.help.TextHelpAppendable
import org.apache.commons.cli.Options
import org.cf.simplify.strategy.*
import org.cf.smalivm.core.*
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.util.ConsoleUtil
import java.io.IOException
import java.io.PrintStream
import java.io.PrintWriter
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.Collections
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class Launcher(
    strategies: List<OptimizationStrategy> = listOf(
        ConstantPropagationStrategy(), DeadRemovalStrategy(), PeepholeStrategy(), UnreflectionStrategy(),
    ),
    private val output: PrintStream = System.out,
    private val error: PrintStream = System.err,
) {
    private val strategies = strategies.toList()
    init { require(strategies.isNotEmpty()) { "At least one optimization strategy is required" } }

    class Result internal constructor(
        val targetMethodCount: Int,
        val optimizedMethodCount: Int,
        val initialInstructionCount: Int,
        val finalInstructionCount: Int,
        val runTimeMillis: Long,
        failedMethods: List<String>,
        skippedMethods: List<String>,
        passLimitReachedMethods: List<String>,
        counts: Map<String, Int>,
    ) {
        /** Methods that threw or whose strategies produced a rejected edit; each one is a defect. */
        val failedMethods: List<String> = Collections.unmodifiableList(failedMethods.toList())
        /** Methods left unoptimized because their analysis was partial. */
        val skippedMethods: List<String> = Collections.unmodifiableList(skippedMethods.toList())
        val failedMethodCount: Int get() = failedMethods.size
        val skippedMethodCount: Int get() = skippedMethods.size
        val passLimitReachedMethods: List<String> = Collections.unmodifiableList(passLimitReachedMethods.toList())
        val optimizationCounts: Map<String, Int> = Collections.unmodifiableMap(LinkedHashMap(counts))
    }

    fun run(options: SimplifyOptions): Result {
        if (options.help) {
            usage(SimplifyOptionsParser.options, output)
            return Result(0, 0, 0, 0, 0, emptyList(), emptyList(), emptyList(), emptyMap())
        }
        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        val previousLevel = root.level
        root.level = when {
            options.quiet -> Level.OFF
            options.verbosity >= 3 -> Level.TRACE
            options.verbosity >= 2 -> Level.DEBUG
            options.verbosity >= 1 -> Level.INFO
            else -> Level.WARN
        }
        try {
            return simplify(options)
        } finally {
            root.level = previousLevel
        }
    }

    private fun simplify(options: SimplifyOptions): Result {
        val reporter = Reporter(options, output, error)
        reporter.verbose(1, "Options:\n$options")
        val start = System.currentTimeMillis()
        val classIndex = ClassIndex(ClassManagerFactory().build(options.inFile, options.outputAPILevel))
        val services = VmServices(classIndex)
        val limits = Limits(options.maxAddressVisits, options.maxMethodVisits, options.maxExecutionTime * 1000L, options.maxCallDepth)
        val optimizer = Optimizer(services, limits, options.maxOptimizationPasses, strategies)
        val targets = services.currentSnapshot.concreteApplicationMethodSignatures().filter { matches(it, options) }
        val initialCount = instructionCount(classIndex, targets)
        var optimized = 0
        val failures = mutableListOf<String>()
        val skips = mutableListOf<String>()
        val passLimits = mutableListOf<String>()
        val counts = emptyCounts(strategies)
        for ((index, signature) in targets.withIndex()) {
            reporter.verbose(1, "(${index + 1} / ${targets.size}) Executing $signature")
            val result = try {
                optimizer.optimize(signature)
            } catch (failure: Exception) {
                if (!options.ignoreErrors) throw failure
                failures += signature
                reporter.methodFailure(signature, failure)
                continue
            }
            counts.addCounts(result.optimizationCounts)
            if (result.passLimitReached) passLimits += signature
            result.rejections.forEach { reporter.rejectedEdit(signature, it) }
            val complete = result.completionStatus is CompletionStatus.Complete
            if (!complete) reporter.verbose(1, "Skipping optimization after partial execution: $signature (${result.completionStatus})")
            when {
                result.rejections.isNotEmpty() -> failures += signature
                !complete -> skips += signature
                else -> optimized++
            }
            reporter.verbose(2, formatCounts("Optimizations for $signature", result.optimizationCounts))
        }
        val finalCount = instructionCount(classIndex, targets)
        reporter.normal("Writing output to ${options.outFile}")
        val writer = ProgramDexWriter()
        if (options.isZip) writeArchive(options.inFile.toPath(), options.outFile.toPath(), writer.writeToBytes(services.currentSnapshot))
        else writer.write(services.currentSnapshot, options.outFile)
        val elapsed = System.currentTimeMillis() - start
        val result = Result(targets.size, optimized, initialCount, finalCount, elapsed, failures, skips, passLimits, counts)
        reporter.normal("Simplification complete:\n\ttotal methods = ${targets.size}\n\toptimized methods = $optimized\n\tfailed methods = ${failures.size}\n\tskipped methods = ${skips.size}\n\tpass-limit methods = ${passLimits.size}\n\tinstructions = $initialCount -> $finalCount\n\trun time = $elapsed ms")
        reporter.normal(formatCounts("Total optimizations", result.optimizationCounts))
        return result
    }

    private fun instructionCount(index: ClassIndex, signatures: List<String>) = signatures.sumOf { index.methodView(it)?.instructionCount ?: 0 }

    private fun matches(signature: String, options: SimplifyOptions): Boolean {
        if (!options.includeSupportLibrary && SUPPORT_LIBRARY_PATTERN.containsMatchIn(signature)) return false
        if (options.includeFilter?.matcher(signature)?.find() == false) return false
        return options.excludeFilter?.matcher(signature)?.find() != true
    }

    private fun writeArchive(input: Path, output: Path, dex: ByteArray) {
        val destination = output.toAbsolutePath()
        val temporary = Files.createTempFile(destination.parent, "simplify-${destination.fileName}-", ".tmp")
        var replaced = false
        try {
            ZipFile(input.toFile()).use { source ->
                ZipOutputStream(Files.newOutputStream(temporary)).use { target ->
                    target.setComment(source.comment)
                    val buffer = ByteArray(8192)
                    val entries = source.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        val replacement = ZipEntry(entry.name).apply {
                            if (entry.time >= 0) time = entry.time
                            comment = entry.comment
                            extra = entry.extra
                            if (entry.name != "classes.dex" && entry.method == ZipEntry.STORED) {
                                method = ZipEntry.STORED
                                size = entry.size
                                crc = entry.crc
                            }
                        }
                        target.putNextEntry(replacement)
                        if (entry.name == "classes.dex") {
                            target.write(dex)
                            replaced = true
                        } else {
                            source.getInputStream(entry).use { contents ->
                                while (true) {
                                    val count = contents.read(buffer)
                                    if (count == -1) break
                                    target.write(buffer, 0, count)
                                }
                            }
                        }
                        target.closeEntry()
                    }
                }
            }
            if (!replaced) throw IOException("Input archive has no classes.dex: $input")
            try {
                Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(temporary)
        }
    }

    private class Reporter(private val options: SimplifyOptions, private val output: PrintStream, private val error: PrintStream) {
        fun normal(message: String) { if (!options.quiet) output.println(message) }
        fun verbose(level: Int, message: String) { if (!options.quiet && options.verbosity >= level) output.println(message) }
        fun methodFailure(signature: String, failure: Throwable) {
            error.println("Exception executing or optimizing $signature; skipping")
            failure.printStackTrace(error)
        }
        fun rejectedEdit(signature: String, rejection: Optimizer.Rejection) {
            error.println(
                "${rejection.strategy} produced an edit of $signature that the method editor rejected: " +
                    rejection.reason.describe(),
            )
        }
    }

    companion object {
        private val SUPPORT_LIBRARY_PATTERN = Regex("Landroid/support/(annotation|design|v\\d{1,2})/")
        internal fun usage(options: Options, output: PrintStream = System.out) {
            val writer = PrintWriter(output)
            val appendable = TextHelpAppendable(writer).apply {
                maxWidth = ConsoleUtil.getConsoleWidth().takeIf { it > 0 } ?: 80
            }
            val formatter = HelpFormatter.builder().setHelpAppendable(appendable).setShowSince(false).get()
            formatter.printHelp("java -jar simplify.jar <input> [options]", "deobfuscates a Dalvik executable", options, "", false)
            writer.flush()
        }
        private fun formatCounts(heading: String, counts: Map<String, Int>) = buildString {
            append(heading).append(':')
            counts.toSortedMap().forEach { (key, value) -> append("\n\t$key = $value") }
        }
    }
}
