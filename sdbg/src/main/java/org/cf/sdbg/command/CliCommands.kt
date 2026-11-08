package org.cf.sdbg.command

import org.cf.sdbg.DebugSession
import org.cf.sdbg.ManifestVersion
import org.jline.terminal.Terminal
import picocli.CommandLine
import java.io.PrintWriter

@CommandLine.Command(name = "", versionProvider = ManifestVersion::class, footer = ["", "Press Ctrl-D to exit."],
        description = [
            "Smali DeBuGger (SDBG) Hit @|magenta <TAB>|@ to see available commands.",
            "Type `@|bold,yellow keymap ^[s tailtip-toggle|@`, then hit @|magenta ALT-S|@ to toggle tailtips.",
            ""
        ],
        subcommands = [
            CommandLine.HelpCommand::class,
            ClearScreenCommand::class,
            ListCommand::class,
            WhereCommand::class,
            StepCommand::class,
            NextCommand::class,
            InfoCommand::class,
            PrintCommand::class,
            BreakCommand::class,
            ContinueCommand::class
        ])
internal class CliCommands(val debugSession: DebugSession, val terminal: Terminal) : Runnable {
    val out: PrintWriter = terminal.writer()

    override fun run() {
        out.println(CommandLine(this).usageMessage)
    }
}
