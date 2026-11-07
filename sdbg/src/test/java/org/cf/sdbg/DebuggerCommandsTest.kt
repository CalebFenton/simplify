package org.cf.sdbg

import org.cf.sdbg.command.CliCommands
import org.cf.smalivm.core.Breakpoint
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class DebuggerCommandsTest {
    private lateinit var commands: CommandLine
    private lateinit var output: StringWriter

    @BeforeEach
    fun initialize() {
        val input = File(requireNotNull(javaClass.getResource("/debugger")).toURI())
        assertEquals(0, Main.commandLine(Initialize()).execute(input.path, EXERCISE))
        output = StringWriter()
        commands = Main.commandLine(CliCommands().apply { out = PrintWriter(output, true) })
            .setOut(PrintWriter(output, true)).setErr(PrintWriter(output, true))
    }

    @Test
    fun commandsShareOneSessionAndInspectTheActualCalleeFrame() {
        val session = Main.session
        execute("break", "add(II)I")
        execute("continue")
        assertSame(session, Main.session)
        assertEquals(ADD, Main.debugger.cursor!!.view.signature)
        assertEquals(0, Main.debugger.currentInstructionIndex)
        val frame = Main.debugger.cursor!!.frame
        val reads = frame.registersRead()
        execute("print", "p1")
        assertTrue(output.toString().contains("3"), output.toString())
        assertEquals(3, (frame.readRegister(2) as Value.Known).raw)
        assertEquals(reads, frame.registersRead(), "Inspecting parameters must not record VM reads")
        execute("step")
        assertEquals(1, Main.debugger.currentInstructionIndex)
        execute("next")
        assertEquals(EXERCISE, Main.debugger.cursor!!.view.signature)
        assertEquals(3, Main.debugger.currentInstructionIndex)
        execute("next")
        execute("next")
        execute("print", "LDebugFixture;->total:I")
        assertTrue(output.toString().contains("5"), output.toString())
        assertEquals(5, (Main.debugger.currentState!!.getStaticField(
            VirtualType.of("LDebugFixture;"), "LDebugFixture;->total:I") as Value.Known).raw)
        execute("continue")
        assertTrue(Main.debugger.isFinished)
        execute("step")
        execute("next")
        execute("where")
        execute("info")
        execute("print", "v0")
        assertNull(Main.debugger.cursor)
    }

    @Test
    fun nextSkipsARealCallButHonorsAnInnerBreakpoint() {
        execute("next")
        execute("next")
        execute("next")
        assertEquals(EXERCISE, Main.debugger.cursor!!.view.signature)
        assertEquals(3, Main.debugger.currentInstructionIndex)
        initialize()
        execute("break", "add(II)I")
        execute("next")
        execute("next")
        execute("next")
        assertEquals(ADD, Main.debugger.cursor!!.view.signature)
        assertEquals(0, Main.debugger.currentInstructionIndex)
        execute("continue")
        assertTrue(Main.debugger.isFinished, "Continue must leave the breakpoint it just stopped at")
    }

    @Test
    fun invalidBoundariesDoNotCreateBreakpointsOrAdvanceExecution() {
        val cursor = Main.debugger.cursor
        val count = Main.session.methodView.instructionCount
        for (target in listOf("-1", count.toString(), "2147483648", "bodyless()V")) {
            execute("break", target)
            assertTrue(Main.debugger.breakpoints.isEmpty(), target)
        }
        for (target in listOf("-1", "nonsense", "2147483648")) execute("continue", target)
        for (target in listOf("p2", "v2", "p2147483648", "LDebugFixture;")) execute("print", target)
        assertSame(cursor, Main.debugger.cursor)
        execute("break", "0")
        execute("continue")
        assertSame(cursor, Main.debugger.cursor)
        assertEquals(setOf(Breakpoint(EXERCISE, 0)), Main.debugger.breakpoints)
        execute("continue")
        assertTrue(Main.debugger.isFinished)
    }

    @Test
    fun signedOffsetsAndContinueCountsUseExecutableIndexes() {
        execute("next")
        execute("break", "-1")
        execute("break", "+1")
        assertEquals(setOf(Breakpoint(EXERCISE, 0), Breakpoint(EXERCISE, 2)), Main.debugger.breakpoints)
        execute("break", "1")
        execute("continue", "1")
        assertEquals(2, Main.debugger.currentInstructionIndex)
        execute("continue")
        assertTrue(Main.debugger.isFinished)
    }

    @Test
    fun helpAndInvalidEntryDoNotStartAConsoleSession() {
        val help = Initialize()
        assertEquals(0, Main.commandLine(help).setOut(PrintWriter(StringWriter())).execute("--help"))
        assertFalse(help.started)
        val invalid = Initialize()
        val input = File(requireNotNull(javaClass.getResource("/debugger")).toURI())
        assertNotEquals(0, Main.commandLine(invalid).setErr(PrintWriter(StringWriter()))
            .execute(input.path, "LDebugFixture;->bodyless()V"))
        assertFalse(invalid.started)
    }

    @Test
    fun steppingIntoFrameworkCodeKeepsListingAndMethodBreakpointsUsable() {
        val input = File(requireNotNull(javaClass.getResource("/debugger")).toURI())
        assertEquals(0, Main.commandLine(Initialize()).execute(
            input.path, "LDebugFixture;->frameworkCall()Ljava/lang/StringBuilder;"))
        execute("break", "3")
        execute("continue")
        execute("step")
        val signature = "Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;"
        val cursor = requireNotNull(Main.debugger.cursor)
        assertEquals(signature, cursor.view.signature)
        assertEquals(0, Main.debugger.currentInstructionIndex)

        execute("list")
        val indexes = Regex("^(\\d+):[b>]*\\s+", RegexOption.MULTILINE)
        assertEquals((0 until cursor.view.instructionCount).toList(),
            indexes.findAll(output.toString()).map { it.groupValues[1].toInt() }.toList(), output.toString())
        execute("list", "0,0")
        assertEquals(listOf(0),
            indexes.findAll(output.toString()).map { it.groupValues[1].toInt() }.toList(), output.toString())

        execute("break", "append(J)Ljava/lang/StringBuilder;")
        assertTrue(Breakpoint(signature, 0) in Main.debugger.breakpoints)
        execute("break", "+1")
        assertTrue(Breakpoint(signature, 1) in Main.debugger.breakpoints)
        execute("list", "Ljava/lang/StringBuilder;")
        assertTrue(output.toString().contains(signature), output.toString())
        assertTrue(Regex("^0:b>\\s+", RegexOption.MULTILINE).containsMatchIn(output.toString()),
            output.toString())
        assertSame(cursor, Main.debugger.cursor, "Listing and setting breakpoints must not advance the framework frame")
    }

    private fun execute(vararg args: String) {
        output.buffer.setLength(0)
        assertEquals(0, commands.execute(*args), output.toString())
    }

    private companion object {
        const val EXERCISE = "LDebugFixture;->exercise()I"
        const val ADD = "LDebugFixture;->add(II)I"
    }
}
