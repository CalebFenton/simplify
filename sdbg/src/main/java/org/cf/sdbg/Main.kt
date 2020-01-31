package org.cf.sdbg

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.cf.sdbg.command.CliCommands
import org.cf.smalivm.Debugger
import org.fusesource.jansi.AnsiConsole
import org.jline.builtins.Builtins
import org.jline.builtins.Options.HelpException
import org.jline.builtins.Widgets.*
import org.jline.builtins.Widgets.CmdLine.DescriptionType
import org.jline.reader.*
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.shell.jline3.PicocliCommands
import java.nio.file.Paths
import java.util.*
import java.util.function.Function

object Main {
    lateinit var debugger: Debugger

    private fun startConsoleLoop() {
        try {
            // set up JLine built-in commands
            val workDir = Paths.get("")
            val builtins = Builtins(workDir, null, null)
            builtins.rename(Builtins.Command.TTOP, "top")
            builtins.alias("zle", "widget")
            builtins.alias("bindkey", "keymap")
            val systemCompleter = builtins.compileCompleters()

            // set up picocli commands
            val commands = CliCommands()
            val cmd = CommandLine(commands)
            val picocliCommands = PicocliCommands(workDir, cmd)
            // TODO: figure out how to add classes, methods, and fields to auto-complete
            systemCompleter.add(picocliCommands.compileCompleters())
            systemCompleter.compile()
            val terminal = TerminalBuilder.builder().build()
            val reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(systemCompleter)
                    .parser(DefaultParser())
                    .variable(LineReader.LIST_MAX, 50) // max tab completion candidates
                    .build()
            builtins.setLineReader(reader)
            commands.setReader(reader)
            val descriptionGenerator = DescriptionGenerator(builtins, picocliCommands)
            TailTipWidgets(reader, Function { line: CmdLine -> descriptionGenerator.commandDescription(line) }, 5,
                    TailTipWidgets.TipType.COMPLETER)
            val prompt = "(sdbg) "
            val rightPrompt: String? = null
            var line: String

            while (true) {
                try {
                    line = reader.readLine(prompt, rightPrompt, null as MaskingCallback?, null)
                    if (line.matches("^\\s*#.*".toRegex())) {
                        continue
                    }
                    val pl = reader.parser.parse(line, 0)
                    val arguments = pl.words().toTypedArray()
                    val command = Parser.getCommand(pl.word())
                    if (builtins.hasCommand(command)) {
                        builtins.execute(command, Arrays.copyOfRange(arguments, 1, arguments.size)
                                , System.`in`, System.out, System.err)
                    } else {
                        CommandLine(commands).execute(*arguments)
                    }
                } catch (e: HelpException) {
                    HelpException.highlight(e.message, HelpException.defaultStyle())
                            .print(terminal)
                } catch (e: UserInterruptException) { // Ignore
                } catch (e: EndOfFileException) {
                    return
                } catch (e: Exception) {
                    val asb = AttributedStringBuilder()
                    asb.append(e.message, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                    asb.toAttributedString().println(terminal)
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun setLogLevel(verbosity: Int) {
        val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        when (verbosity) {
            0 -> { rootLogger.level = Level.OFF }
            1 -> { rootLogger.level = Level.INFO }
            2 -> { rootLogger.level = Level.DEBUG }
            3 -> { rootLogger.level = Level.TRACE }
        }
    }

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        AnsiConsole.systemInstall()
        setLogLevel(0)
        val target: String = if (args.isNotEmpty()) {
            args[0]
        } else {
            "LTest;->addNumbers()V"
        }
        //    debugger = new SmaliDebugger("LTest;->main([Ljava/lang/String;)V");
        println("Starting debugger for $target")
        debugger = Debugger("LTest;->addNumbers()V")
        startConsoleLoop()
    }

    /**
     * Provide command descriptions for JLine TailTipWidgets to be displayed in the status bar.
     */
    private class DescriptionGenerator(var builtins: Builtins, var picocli: PicocliCommands) {
        fun commandDescription(line: CmdLine): CmdDesc? {
            var out: CmdDesc? = null
            when (line.descriptionType) {
                DescriptionType.COMMAND -> {
                    val cmd = Parser.getCommand(line.args[0])
                    when {
                        builtins.hasCommand(cmd) -> {
                            out = builtins.commandDescription(cmd)
                        }
                        picocli.hasCommand(cmd) -> {
                            out = picocli.commandDescription(cmd)
                        }
                    }
                }
                else -> {
                }
            }
            return out
        }

    }
}
