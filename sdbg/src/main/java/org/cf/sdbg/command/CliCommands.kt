package org.cf.sdbg.command

import org.jline.reader.LineReader
import org.jline.reader.impl.LineReaderImpl
import picocli.CommandLine
import java.io.PrintWriter

@CommandLine.Command(name = "", description = ["Smali DeBuGger (SDBG) Hit @|magenta <TAB>|@ to see available commands.", "Type `@|bold,yellow keymap ^[s tailtip-toggle|@`, then hit @|magenta ALT-S|@ to toggle tailtips.", ""], footer = ["", "Press Ctl-D to exit."], subcommands = [CommandLine.HelpCommand::class,  //        MyCommand.class,
    ClearScreen::class, List::class, Where::class, Step::class, Info::class])
class CliCommands : Runnable {
    var reader: LineReaderImpl? = null
    var out: PrintWriter? = null
    fun setReader(reader: LineReader) {
        this.reader = reader as LineReaderImpl
        out = reader.getTerminal().writer()
    }

    override fun run() {
        out!!.println(CommandLine(this).usageMessage)
    }
}
