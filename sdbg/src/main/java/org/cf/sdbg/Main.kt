package org.cf.sdbg

import org.cf.sdbg.command.CliCommands
import org.cf.smalivm.core.Debugger
import org.cf.smalivm.core.ExecutionSession
import org.cf.smalivm.core.VmServices
import org.fusesource.jansi.AnsiConsole
import org.jline.builtins.ConfigurationPath
import org.jline.console.impl.Builtins
import org.jline.builtins.Options.HelpException
import org.jline.console.CmdDesc
import org.jline.console.CmdLine
import org.jline.console.CmdLine.DescriptionType
import org.jline.console.CommandRegistry.CommandSession
import org.jline.widget.TailTipWidgets
import org.jline.reader.*
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import picocli.CommandLine
import picocli.shell.jline3.PicocliCommands
import java.nio.file.Paths
import java.util.function.Function
import kotlin.system.exitProcess

object Main {
    lateinit var services: VmServices
    lateinit var session: ExecutionSession
    lateinit var debugger: Debugger
    lateinit var listing: SmaliListing
    const val version: String = "2.0.0-SNAPSHOT"

    internal fun commandLine(command: Any): CommandLine = CommandLine(command).setExecutionExceptionHandler {
        exception, commandLine, _ ->
        var cause: Throwable? = exception
        while (cause != null) {
            if (cause is Error) throw cause
            cause = cause.cause
        }
        commandLine.err.println(exception.message ?: exception.javaClass.simpleName)
        commandLine.commandSpec.exitCodeOnExecutionException()
    }.also { line ->
        if (command is CliCommands) {
            for (name in listOf("break", "list", "continue")) {
                line.subcommands.getValue(name).commandSpec.parser().unmatchedOptionsArePositionalParams(true)
            }
        }
    }

    private fun startConsoleLoop() {
        val workDir = Paths.get("")
        val parser = DefaultParser()
        val builtins = Builtins(workDir, ConfigurationPath(null, null), null)
        builtins.alias("zle", "widget")
        builtins.alias("bindkey", "keymap")
        val systemCompleter = builtins.compileCompleters()
        val commands = CliCommands()
        val cmd = commandLine(commands)
        val picocliCommands = PicocliCommands(cmd)
        systemCompleter.add(picocliCommands.compileCompleters())
        systemCompleter.compile()
        TerminalBuilder.builder().build().use { terminal ->
            val commandSession = CommandSession(terminal, System.`in`, System.out, System.err)
            val reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(systemCompleter)
                .parser(parser)
                .variable(LineReader.LIST_MAX, 50)
                .build()
            builtins.setLineReader(reader)
            commands.configureReader(reader)
            cmd.setOut(terminal.writer()).setErr(terminal.writer())
            val descriptions = DescriptionGenerator(builtins, picocliCommands, parser)
            TailTipWidgets(reader, Function { line: CmdLine -> descriptions.commandDescription(line) }, 5,
                TailTipWidgets.TipType.COMPLETER)
            while (true) {
                try {
                    val line = reader.readLine("(sdbg) ", null, null as MaskingCallback?, null)
                    if (line.isBlank() || line.trimStart().startsWith('#')) continue
                    val parsed = reader.parser.parse(line, 0)
                    val arguments = parsed.words().toTypedArray()
                    val command = parser.getCommand(arguments[0])
                    if (builtins.hasCommand(command)) {
                        builtins.invoke(commandSession, command, *arguments.copyOfRange(1, arguments.size))
                    } else {
                        cmd.execute(*arguments)
                    }
                } catch (e: HelpException) {
                    HelpException.highlight(e.message, HelpException.defaultStyle()).print(terminal)
                } catch (e: UserInterruptException) {
                    // Ctrl-C cancels the current input; Ctrl-D closes the debugger.
                } catch (e: EndOfFileException) {
                    return
                } catch (e: Exception) {
                    AttributedStringBuilder()
                        .append(e.message ?: e.javaClass.simpleName, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                        .toAttributedString().println(terminal)
                } finally {
                    terminal.flush()
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        AnsiConsole.systemInstall()
        val status = try {
            val initialize = Initialize()
            val result = commandLine(initialize).execute(*args)
            if (result == 0 && initialize.started) startConsoleLoop()
            result
        } catch (e: Exception) {
            System.err.println(e.message ?: e.javaClass.simpleName)
            1
        } finally {
            AnsiConsole.systemUninstall()
        }
        if (status != 0) exitProcess(status)
    }

    private class DescriptionGenerator(val builtins: Builtins, val picocli: PicocliCommands, val parser: Parser) {
        fun commandDescription(line: CmdLine): CmdDesc? {
            if (line.descriptionType != DescriptionType.COMMAND || line.args.isEmpty()) return null
            val command = parser.getCommand(line.args[0])
            return when {
                builtins.hasCommand(command) -> builtins.commandDescription(line.args)
                picocli.hasCommand(command) -> picocli.commandDescription(line.args)
                else -> null
            }
        }
    }
}
