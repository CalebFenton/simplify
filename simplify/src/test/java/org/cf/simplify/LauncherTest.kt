package org.cf.simplify

import org.apache.commons.cli.ParseException
import org.cf.simplify.strategy.OptimizationStrategy
import org.cf.simplify.strategy.StrategyInput
import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class LauncherTest {
    @TempDir lateinit var temporaryDirectory: Path
    private val fixture = "src/test/resources/task64"
    private fun launcher() = Launcher(listOf(OptimizationStrategy {}))
    private fun run(input: String, output: Path, vararg options: String) = launcher().run(arrayOf(input, "--output", output.toString(), "--quiet", *options))
    private fun returned(file: File): Any? = (OptimizerTester.execute(OptimizerTester.services(file), OptimizerTest.CHANGED).trusted()!!.terminatingConsensus(0) as Value.Known).raw
    private fun Launcher.run(args: Array<String>) = run(SimplifyOptionsParser.parse(args))

    @Test fun directoryAndDexInputsProduceExecutableDex() {
        val input = temporaryDirectory.resolve("input.dex")
        val output = temporaryDirectory.resolve("output.dex")
        run(fixture, input, "--include-types", "aNoOp|changed")
        run(input.toString(), output, "--include-types", "changed")
        assertEquals(0, returned(output.toFile()))
        assertEquals(3, OptimizerTester.services(output.toFile()).currentSnapshot.concreteApplicationMethodSignatures().size)
    }

    @Test fun archiveResourcesSurviveWhileCommittedDexReplacesOriginalEvenInPlace() {
        val dex = temporaryDirectory.resolve("classes.dex")
        run(fixture, dex, "--include-types", "aNoOp")
        val apk = temporaryDirectory.resolve("input.apk")
        val marker = "kept".toByteArray()
        ZipOutputStream(Files.newOutputStream(apk)).use { zip ->
            zip.setComment("archive metadata")
            zip.putNextEntry(ZipEntry("classes.dex")); Files.copy(dex, zip); zip.closeEntry()
            val entry = ZipEntry("assets/marker.txt").apply {
                method = ZipEntry.STORED
                size = marker.size.toLong()
                crc = CRC32().apply { update(marker) }.value
                time = 1_600_000_000_000L
                comment = "resource"
            }
            zip.putNextEntry(entry); zip.write(marker); zip.closeEntry()
        }
        var attempts = 0
        val strategy = OptimizationStrategy {
            if (attempts++ == 0) {
                it.transaction.replaceInstruction(0, listOf(InstructionTemplate.Encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 3))))
                it.recordOptimization("constant")
            }
        }
        val report = Launcher(listOf(strategy)).run(arrayOf(apk.toString(), "--output", apk.toString(), "--include-types", "changed", "--quiet"))
        assertEquals(1, report.optimizationCounts["constant"])
        ZipFile(apk.toFile()).use { zip ->
            assertEquals("archive metadata", zip.comment)
            val entry = zip.getEntry("assets/marker.txt")
            assertArrayEquals(marker, zip.getInputStream(entry).use { it.readBytes() })
            assertEquals(ZipEntry.STORED, entry.method)
            assertEquals(1_600_000_000_000L, entry.time)
            assertEquals("resource", entry.comment)
            val extracted = temporaryDirectory.resolve("reloaded.dex")
            zip.getInputStream(zip.getEntry("classes.dex")).use { Files.copy(it, extracted) }
            assertEquals(3, returned(extracted.toFile()))
        }
    }

    @Test fun includeExcludeAndZeroPassLimitControlActualExecution() {
        val visited = mutableListOf<String>()
        val strategy = OptimizationStrategy { visited += it.graph.methodView.signature }
        val output = temporaryDirectory.resolve("filtered.dex")
        Launcher(listOf(strategy)).run(arrayOf(fixture, "--include-types", "aNoOp|changed", "--exclude-types", "changed", "--output", output.toString(), "--quiet"))
        assertEquals(listOf(OptimizerTest.NO_OP), visited)
        visited.clear()
        val result = Launcher(listOf(strategy)).run(arrayOf(fixture, "--include-types", "changed", "--max-passes", "0", "--output", output.toString(), "--quiet"))
        assertTrue(visited.isEmpty())
        assertEquals(listOf(OptimizerTest.CHANGED), result.passLimitReachedMethods)
        assertEquals(0, returned(output.toFile()))
    }

    @Test fun incompleteAnalysisSkipsTheMethodWithoutFailingTheCommandLine() {
        var attempts = 0
        val launcher = Launcher(listOf(OptimizationStrategy { attempts++ }))
        val args = arrayOf(fixture, "--include-types", "loop", "--max-address-visits", "1", "--output", temporaryDirectory.resolve("partial.dex").toString(), "--quiet")
        val result = launcher.run(args)
        assertEquals(0, attempts)
        assertEquals(listOf(OptimizerTest.LOOP), result.skippedMethods)
        assertEquals(emptyList<String>(), result.failedMethods)
        assertEquals(0, result.optimizedMethodCount)
        assertEquals(0, Main.run(args, launcher, PrintStream(ByteArrayOutputStream())))
    }

    @Test fun rejectedStrategyEditFailsTheMethodAndTheCommandLineUnlessErrorsAreIgnored() {
        val invalidEdit = object : OptimizationStrategy {
            override val name = "InvalidEdit"
            override fun perform(input: StrategyInput) = input.transaction.removeInstruction(99)
        }
        val errors = ByteArrayOutputStream()
        val launcher = Launcher(listOf(invalidEdit), PrintStream(ByteArrayOutputStream()), PrintStream(errors))
        val args = arrayOf(fixture, "--include-types", "changed", "--output", temporaryDirectory.resolve("rejected.dex").toString())
        val result = launcher.run(args)
        assertEquals(listOf(OptimizerTest.CHANGED), result.failedMethods)
        assertEquals(0, result.optimizedMethodCount)
        // The fixture's changed() is two code units long, so code address 99 names no instruction.
        assertTrue(errors.toString().contains("InvalidEdit"), errors.toString())
        assertTrue(errors.toString().contains(MethodEditRejectionReason.InvalidCodeAddress(99).describe()), errors.toString())
        assertEquals(1, Main.run(args, launcher, PrintStream(ByteArrayOutputStream())))
        assertEquals(0, Main.run(args + "--ignore-errors", launcher, PrintStream(ByteArrayOutputStream())))
    }

    @Test fun mainReportsParseErrorsWithStatusOne() {
        val errors = ByteArrayOutputStream()
        assertEquals(1, Main.run(emptyArray(), launcher(), PrintStream(errors)))
        assertTrue(errors.toString().startsWith("Error: Missing input file."), errors.toString())
    }

    @Test fun ignoredFailureContinuesOtherMethodsWhileFatalFailurePublishesNoOutput() {
        val errors = ByteArrayOutputStream()
        val visited = mutableListOf<String>()
        val strategy = OptimizationStrategy {
            val signature = it.graph.methodView.signature
            if (signature == OptimizerTest.NO_OP) throw IllegalStateException("method failure")
            visited += signature
        }
        val output = temporaryDirectory.resolve("ignored.dex")
        val result = Launcher(listOf(strategy), PrintStream(ByteArrayOutputStream()), PrintStream(errors)).run(arrayOf(fixture, "--ignore-errors", "--include-types", "aNoOp|changed", "--output", output.toString(), "--quiet"))
        assertEquals(listOf(OptimizerTest.NO_OP), result.failedMethods)
        assertEquals(listOf(OptimizerTest.CHANGED), visited)
        assertTrue(errors.toString().contains("method failure"))
        assertEquals(0, returned(output.toFile()))
        val fatal = temporaryDirectory.resolve("fatal.dex")
        assertThrows(IllegalStateException::class.java) { Launcher(listOf(strategy)).run(arrayOf(fixture, "--output", fatal.toString(), "--quiet")) }
        assertFalse(Files.exists(fatal))
    }

    @Test fun ignoreErrorsDoesNotSuppressVirtualMachineErrors() {
        val output = temporaryDirectory.resolve("fatal-vm.dex")
        val failure = StackOverflowError("fatal VM failure")
        val launcher = Launcher(
            listOf(OptimizationStrategy { throw failure }),
            PrintStream(ByteArrayOutputStream()),
            PrintStream(ByteArrayOutputStream()),
        )
        assertSame(failure, assertThrows(StackOverflowError::class.java) {
            launcher.run(arrayOf(fixture, "--ignore-errors", "--quiet", "--output", output.toString()))
        })
        assertFalse(Files.exists(output))
    }

    @Test fun helpAndLimitsAreParsedAndUnsafeWeakRemovalIsRejected() {
        assertTrue(SimplifyOptionsParser.parse(arrayOf("--help")).help)
        assertThrows(ParseException::class.java) { SimplifyOptionsParser.parse(emptyArray()) }
        assertThrows(ParseException::class.java) { SimplifyOptionsParser.parse(arrayOf(fixture, "--remove-weak")) }
        for (option in listOf("max-execution-time", "max-address-visits", "max-call-depth", "max-method-visits", "max-passes", "output-api-level")) {
            for (invalid in listOf("-1", "not-an-int", "2147483648")) {
                assertThrows(ParseException::class.java) { SimplifyOptionsParser.parse(arrayOf(fixture, "--$option", invalid)) }
            }
        }
        val options = SimplifyOptionsParser.parse(arrayOf(fixture, "--max-execution-time", "11", "--max-address-visits", "12", "--max-call-depth", "13", "--max-method-visits", "14", "--max-passes", "15", "--output-api-level", "19", "--ignore-errors"))
        assertEquals(listOf(11, 12, 13, 14, 15, 19), listOf(options.maxExecutionTime, options.maxAddressVisits, options.maxCallDepth, options.maxMethodVisits, options.maxOptimizationPasses, options.outputAPILevel))
        assertTrue(options.ignoreErrors)
    }

    @Test fun unusableInputsAndPatternsAreParseErrors() {
        val missing = temporaryDirectory.resolve("missing.dex").toString()
        val elf = temporaryDirectory.resolve("program.so")
        Files.write(elf, byteArrayOf(0x7f, 0x45, 0x4c, 0x46))
        for (args in listOf(arrayOf(missing), arrayOf(elf.toString()), arrayOf(fixture, "--include-types", "["), arrayOf(fixture, "--exclude-types", "("))) {
            assertThrows(ParseException::class.java, { SimplifyOptionsParser.parse(args) }, args.joinToString(" "))
        }
        val errors = ByteArrayOutputStream()
        assertEquals(1, Main.run(arrayOf(missing), launcher(), PrintStream(errors)))
        assertTrue(errors.toString().startsWith("Error: "), errors.toString())
    }

    @Test fun multiDexArchiveIsAnInputErrorNamingItsSecondaryDex() {
        val dex = temporaryDirectory.resolve("classes.dex")
        run(fixture, dex, "--include-types", "aNoOp")
        val apk = temporaryDirectory.resolve("multidex.apk")
        ZipOutputStream(Files.newOutputStream(apk)).use { zip ->
            for (name in listOf("classes.dex", "classes2.dex")) {
                zip.putNextEntry(ZipEntry(name)); Files.copy(dex, zip); zip.closeEntry()
            }
        }
        val output = temporaryDirectory.resolve("multidex_simple.apk")
        val errors = ByteArrayOutputStream()

        assertEquals(1, Main.run(arrayOf(apk.toString(), "--output", output.toString()), launcher(), PrintStream(errors)))
        val message = errors.toString()
        assertTrue(message.startsWith("Error: Multi-DEX archives are not supported: $apk also has classes2.dex"), message)
        assertFalse("\tat " in message, "an input error is reported without a stack trace: $message")
        assertFalse(Files.exists(output))
    }

    @Test fun verboseIsACountingFlagThatNeverConsumesTheInputPath() {
        val single = SimplifyOptionsParser.parse(arrayOf("-v", fixture))
        assertEquals(File(fixture), single.inFile)
        assertEquals(1, single.verbosity)
        assertEquals(2, SimplifyOptionsParser.parse(arrayOf("-vv", fixture)).verbosity)
        assertEquals(3, SimplifyOptionsParser.parse(arrayOf(fixture, "-v", "--verbose", "-v")).verbosity)
        assertThrows(ParseException::class.java) { SimplifyOptionsParser.parse(arrayOf(fixture, "-v", "2")) }
    }

    @Test fun quietAndVerboseReportsDoNotLeakBetweenRuns() {
        val quiet = ByteArrayOutputStream()
        Launcher(listOf(OptimizationStrategy {}), PrintStream(quiet)).run(arrayOf(fixture, "--include-types", "aNoOp", "--quiet", "--output", temporaryDirectory.resolve("quiet.dex").toString()))
        assertEquals("", quiet.toString())
        val verbose = ByteArrayOutputStream()
        Launcher(listOf(OptimizationStrategy {}), PrintStream(verbose)).run(arrayOf(fixture, "--include-types", "aNoOp", "-vv", "--output", temporaryDirectory.resolve("verbose.dex").toString()))
        assertTrue(verbose.toString().contains("Executing ${OptimizerTest.NO_OP}"))
        assertTrue(verbose.toString().contains("Optimizations for ${OptimizerTest.NO_OP}"))
    }

    @Test fun supportLibraryFilterRequiresExplicitOptIn() {
        val visited = mutableListOf<String>()
        val strategy = OptimizationStrategy { visited += it.graph.methodView.signature }
        val output = temporaryDirectory.resolve("support.dex")
        val args = arrayOf("src/test/resources/task69", "--output", output.toString(), "--quiet")
        Launcher(listOf(strategy)).run(args)
        assertEquals(listOf("Ltask69/Application;->value()I"), visited)
        visited.clear()
        Launcher(listOf(strategy)).run(args + "--include-support")
        assertEquals(listOf("Landroid/support/v7/Support;->value()I", "Ltask69/Application;->value()I"), visited)
    }

    @Test fun outputNamesPreserveInputTypeConventions() {
        val directory = Files.createDirectory(temporaryDirectory.resolve("smali.input"))
        val dex = temporaryDirectory.resolve("sample.dex")
        Files.write(dex, byteArrayOf(0x64, 0x65, 0x78))
        val archive = temporaryDirectory.resolve("sample.apk")
        ZipOutputStream(Files.newOutputStream(archive)).use {
            it.putNextEntry(ZipEntry("classes.dex"))
            it.write(byteArrayOf(0x64, 0x65, 0x78))
            it.closeEntry()
        }
        for ((input, expected) in listOf(directory to "smali_simple.dex", dex to "sample_simple.dex", archive to "sample_simple.apk")) {
            assertEquals(File(expected), SimplifyOptionsParser.parse(arrayOf(input.toString())).outFile)
        }
    }

    @Test fun mainExecutesRealPipeline() {
        val output = temporaryDirectory.resolve("main.dex")
        Main.main(arrayOf(fixture, "--include-types", "changed", "--quiet", "--output", output.toString()))
        assertEquals(0, returned(output.toFile()))
    }
}
