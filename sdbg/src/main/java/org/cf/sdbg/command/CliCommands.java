package org.cf.sdbg.command;

import java.io.PrintWriter;
import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;
import picocli.CommandLine;

@CommandLine.Command(name = "",
    description = {
        "Smali DeBuGger (SDBG) Hit @|magenta <TAB>|@ to see available commands.",
        "Type `@|bold,yellow keymap ^[s tailtip-toggle|@`, then hit @|magenta ALT-S|@ to toggle tailtips.",
        ""
    },
    footer = {"", "Press Ctl-D to exit."},
    subcommands = {
        CommandLine.HelpCommand.class,
//        MyCommand.class,
        ClearScreen.class,
        List.class,
        Where.class,
        Step.class,
        Info.class,
    })
public class CliCommands implements Runnable {

  LineReaderImpl reader;
  protected PrintWriter out;

  public CliCommands() {
  }

  public void setReader(LineReader reader) {
    this.reader = (LineReaderImpl) reader;
    out = reader.getTerminal().writer();
  }

  public void run() {
    out.println(new CommandLine(this).getUsageMessage());
  }
}
