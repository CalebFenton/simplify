package org.cf.sdbg

import org.cf.sdbg.command.CliCommands
import org.cf.smalivm.core.Breakpoint
import org.jline.terminal.impl.DumbTerminal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class DebuggerCommandsTest {
    private val terminalOutput = ByteArrayOutputStream()
    private lateinit var debugSession: DebugSession
    private lateinit var cli: CliCommands
    private lateinit var commands: CommandLine

    private val debugger get() = debugSession.debugger

    @Test
    fun breakpointStepAndPrintInspectTheActualCalleeFrame() {
        start(FIXTURE, EXERCISE)
        assertEquals(listOf("breakpoint set for $ADD"), execute("break", "add(II)I"))
        assertEquals(listOf("hit breakpoint $ADD @ 0"), execute("continue"))
        val frame = debugger.cursor!!.frame
        val reads = frame.registersRead()
        // add(II)I has one local, so p0 is r1 and p1 is r2; exercise() passes 2 and 3.
        assertEquals(listOf("p1(r2): Known(raw=3, type=I)"), execute("print", "p1"))
        assertEquals(listOf("v0: Top", "v1(p0): Known(raw=2, type=I)", "v2(p1): Known(raw=3, type=I)"),
            execute("info", "registers"))
        assertEquals(reads, frame.registersRead(), "Inspecting parameters must not record VM reads")

        // Math, invoke and field ops render operands; the others render name@codeAddress.
        // add(II)I: add-int (two code units) @0, return @2.
        assertEquals(listOf("0:>\tadd-int r0, r1, r2"), execute("step"))
        assertEquals(listOf("#1 in $ADD"), execute("where"))
        assertEquals(listOf("1:>\treturn@2"), execute("next"))
        // exercise(): const/4 @0, const/4 @1, invoke-static (three code units) @2, move-result @5, sput @6.
        assertEquals(listOf("#3 in $EXERCISE"), execute("where"))
        assertEquals(listOf("3:>\tmove-result@5"), execute("next"))
        assertEquals(listOf("4:>\tsput r0, LDebugFixture;->total:I"), execute("next"))
        assertEquals(listOf("LDebugFixture;->total:I: Known(raw=5, type=I)"),
            execute("print", "LDebugFixture;->total:I"))

        val finished = listOf("execution finished: Complete", "Returned(value=Known(raw=5, type=I))")
        assertEquals(finished, execute("continue"))
        assertTrue(debugger.isFinished)
        assertEquals(finished, execute("step"))
        assertEquals(finished, execute("next"))
        assertEquals(listOf("no current instruction; execution finished"), execute("where"))
        assertEquals(listOf("no current frame; execution finished"), execute("info"))
        assertEquals(listOf("no current frame; execution finished"), execute("print", "v0"))
        assertNull(debugger.cursor)
    }

    @Test
    fun nextStepsOverARealCall() {
        start(FIXTURE, EXERCISE)
        assertEquals(listOf("0:>\tconst/4@0"), execute("next"))
        assertEquals(listOf("1:>\tconst/4@1"), execute("next"))
        assertEquals(listOf("2:>\tinvoke-static {v0, v1}, $ADD"), execute("next"))
        assertEquals(EXERCISE, debugger.cursor!!.view.signature)
        assertEquals(3, debugger.currentInstructionIndex)
    }

    @Test
    fun nextStopsAtABreakpointInsideTheCall() {
        start(FIXTURE, EXERCISE)
        execute("break", "add(II)I")
        execute("next")
        execute("next")
        assertEquals(listOf("2:>\tinvoke-static {v0, v1}, $ADD"), execute("next"))
        assertEquals(ADD, debugger.cursor!!.view.signature)
        assertEquals(0, debugger.currentInstructionIndex)
        execute("continue")
        assertTrue(debugger.isFinished, "Continue must leave the breakpoint it just stopped at")
    }

    @Test
    fun malformedOrMistypedTargetsAreRejectedWithoutSideEffects() {
        start(FIXTURE, EXERCISE)
        val cursor = debugger.cursor
        for ((target, reason) in listOf(
            "add(II" to "malformed method 'add(II'; expected name(params)return",
            "add(IX)I" to "malformed method 'add(IX)I'; expected name(params)return",
            "add(IV)I" to "malformed method 'add(IV)I'; expected name(params)return",
            "add(II)" to "malformed method 'add(II)'; expected name(params)return",
            "LDebugFixture" to "malformed class descriptor 'LDebugFixture'; expected Lpkg/Name;",
            "LDebug.Fixture;->add(II)I" to "malformed owner 'LDebug.Fixture;'; expected Lpkg/Name;",
            "LDebugFixture;->total" to "malformed member 'total'; expected name(params)return or name:type",
            "nonsense" to "'nonsense' is not a class descriptor, method, or Lpkg/Name;->member reference",
            "LDebugFixture;" to "expected a method, got class LDebugFixture;",
            "LDebugFixture;->total:I" to "expected a method, got field LDebugFixture;->total",
            "-1" to "index out of range",
            "6" to "index out of range",
            "2147483648" to "index out of range",
            "LDebugFixture;->bodyless()V" to "concrete method not found",
            "missing()V" to "concrete method not found",
        )) {
            assertEquals(listOf("invalid target; $reason"), execute("break", target), target)
        }
        assertTrue(debugger.breakpoints.isEmpty())

        for ((target, reason) in listOf(
            "1,x" to "'1,x' is not a class descriptor, method, or Lpkg/Name;->member reference",
            "3,1" to "instruction range out of bounds",
            "6" to "instruction range out of bounds",
            "0,2147483648" to "instruction range out of bounds",
            "LDebugFixture" to "malformed class descriptor 'LDebugFixture'; expected Lpkg/Name;",
            "LDebugFixture;->total:I" to "expected a class or method, got field LDebugFixture;->total",
            "LMissing;" to "class descriptor not found",
            "missing()V" to "method signature not found",
        )) {
            assertEquals(listOf("invalid target; $reason"), execute("list", target), target)
        }

        for ((target, reason) in listOf(
            "p2" to "register out of range",
            "v2" to "register out of range",
            "p2147483648" to "register out of range",
            "LDebugFixture;" to "expected a register, parameter, or Lpkg/Name;->field:type",
            "LDebugFixture;->total:V" to "malformed field 'total:V'; expected name:type",
            "LDebugFixture;->missing:I" to "static field not found",
        )) {
            assertEquals(listOf("invalid target; $reason"), execute("print", target), target)
        }
        for (target in listOf("-1", "nonsense", "2147483648")) {
            assertEquals(listOf("invalid target; expected a nonnegative breakpoint count"),
                execute("continue", target), target)
        }
        assertEquals(listOf("invalid target; expected break, breakpoints, or registers"), execute("info", "stack"))
        assertSame(cursor, debugger.cursor)

        assertEquals(listOf("breakpoint set for $EXERCISE @ 0"), execute("break", "0"))
        assertEquals(listOf("hit breakpoint $EXERCISE @ 0"), execute("continue"))
        assertSame(cursor, debugger.cursor, "A breakpoint on the starting instruction stops before it runs")
        assertEquals(setOf(Breakpoint(EXERCISE, 0)), debugger.breakpoints)
    }

    @Test
    fun signedOffsetsAndContinueCountsUseExecutableIndexes() {
        start(FIXTURE, EXERCISE)
        execute("next")
        assertEquals(listOf("breakpoint set for $EXERCISE @ 0"), execute("break", "-1"))
        assertEquals(listOf("breakpoint set for $EXERCISE @ 2"), execute("break", "+1"))
        assertEquals(listOf("breakpoint set for $EXERCISE @ 1"), execute("break", "1"))
        assertEquals(listOf("breakpoint count=3", "$EXERCISE @ 0", "$EXERCISE @ 1", "$EXERCISE @ 2"),
            execute("info", "breakpoints"))
        assertEquals(listOf("hit breakpoint $EXERCISE @ 1", "hit breakpoint $EXERCISE @ 2"), execute("continue", "1"))
        assertEquals(2, debugger.currentInstructionIndex)
        execute("continue")
        assertTrue(debugger.isFinished)
    }

    @Test
    fun readmeWalkthroughShowsTheDocumentedValues() {
        start(File("resources"), "LTest;->addNumbers()V")
        val caller = "LTest;->addNumbers()V"
        // addNumbers(): v0 = 2 + 3 + 3 before invoke-static, the instruction at index 4.
        assertEquals(listOf("breakpoint set for $caller @ 4"), execute("break", "4"))
        assertEquals(listOf("hit breakpoint $caller @ 4"), execute("continue"))
        assertEquals(listOf("v0: Known(raw=8, type=I)"), execute("print", "v0"))
        assertEquals(listOf("4:>\tinvoke-static {v0, v1}, LTest;->addParameters(II)I"), execute("step"))
        assertEquals(listOf("#0 in LTest;->addParameters(II)I"), execute("where"))
        assertEquals(listOf("p0(r0): Known(raw=8, type=I)"), execute("print", "p0"))
        // addParameters(II)I has no locals, so p0 is r0 and p1 is r1.
        assertEquals(listOf("0:>\tadd-int r0, r0, r1"), execute("next"))
        assertEquals(listOf("p0(r0): Known(raw=11, type=I)"), execute("print", "p0"))
    }

    @Test
    fun helpVersionAndInvalidEntryDoNotStartASession() {
        val help = Initialize()
        assertEquals(0, Main.commandLine(help).setOut(PrintWriter(StringWriter())).execute("--help"))
        assertNull(help.debugSession)

        val version = Initialize()
        val versionOutput = StringWriter()
        assertEquals(0, Main.commandLine(version).setOut(PrintWriter(versionOutput)).execute("--version"))
        assertEquals("unknown", versionOutput.toString().trim(), "Test classes are not loaded from a jar")
        assertNull(version.debugSession)

        for ((signature, message) in listOf(
            "LDebugFixture;->bodyless()V" to "No concrete method: LDebugFixture;->bodyless()V",
            "LDebugFixture;exercise()I" to
                "Expected a method signature Lpkg/Name;->name(params)return: LDebugFixture;exercise()I",
            "exercise()I" to "Expected a method signature Lpkg/Name;->name(params)return: exercise()I",
        )) {
            val invalid = Initialize()
            val errors = StringWriter()
            assertNotEquals(0, Main.commandLine(invalid).setErr(PrintWriter(errors)).execute(FIXTURE.path, signature))
            assertEquals(message, errors.toString().trim())
            assertNull(invalid.debugSession)
        }
    }

    @Test
    fun steppingIntoFrameworkCodeKeepsListingAndMethodBreakpointsUsable() {
        start(FIXTURE, "LDebugFixture;->frameworkCall()Ljava/lang/StringBuilder;")
        execute("break", "3")
        execute("continue")
        execute("step")
        val signature = "Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;"
        val cursor = requireNotNull(debugger.cursor)
        assertEquals(signature, cursor.view.signature)
        assertEquals(0, debugger.currentInstructionIndex)

        val indexes = Regex("^(\\d+):[b>]*\\s+")
        val listed = execute("list").mapNotNull { indexes.find(it)?.groupValues?.get(1)?.toInt() }
        assertEquals((0 until cursor.view.instructionCount).toList(), listed)
        assertEquals(listOf(0), execute("list", "0,0").mapNotNull { indexes.find(it)?.groupValues?.get(1)?.toInt() })

        assertEquals(listOf("breakpoint set for $signature"), execute("break", "append(J)Ljava/lang/StringBuilder;"))
        assertEquals(listOf("breakpoint set for $signature @ 1"), execute("break", "+1"))
        val classListing = execute("list", "Ljava/lang/StringBuilder;")
        assertTrue(classListing.any { it.endsWith(signature) }, classListing.joinToString("\n"))
        assertTrue(classListing.any { it.startsWith("0:b>") }, classListing.joinToString("\n"))
        assertSame(cursor, debugger.cursor, "Listing and setting breakpoints must not advance the framework frame")
    }

    private fun start(input: File, signature: String) {
        val initialize = Initialize()
        assertEquals(0, Main.commandLine(initialize).execute(input.path, signature))
        debugSession = requireNotNull(initialize.debugSession)
        cli = CliCommands(debugSession, DumbTerminal(ByteArrayInputStream(ByteArray(0)), terminalOutput))
        commands = Main.commandLine(cli).setOut(cli.out).setErr(cli.out)
    }

    private fun execute(vararg args: String): List<String> {
        terminalOutput.reset()
        val status = commands.execute(*args)
        cli.out.flush()
        val lines = terminalOutput.toString(Charsets.UTF_8).lines().dropLastWhile { it.isEmpty() }
        assertEquals(0, status, lines.joinToString("\n"))
        return lines
    }

    private companion object {
        val FIXTURE = File(requireNotNull(DebuggerCommandsTest::class.java.getResource("/debugger")).toURI())
        const val EXERCISE = "LDebugFixture;->exercise()I"
        const val ADD = "LDebugFixture;->add(II)I"
    }
}
