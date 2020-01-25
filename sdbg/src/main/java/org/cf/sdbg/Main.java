package org.cf.sdbg;


import ch.qos.logback.classic.Level;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.cf.sdbg.command.CliCommands;
import org.cf.smalivm.Debugger;
import org.fusesource.jansi.AnsiConsole;
import org.jline.builtins.Builtins;
import org.jline.builtins.Completers;
import org.jline.builtins.Options;
import org.jline.builtins.Widgets;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliCommands;

public class Main {

  public static Debugger debugger;

  /**
   * Provide command descriptions for JLine TailTipWidgets to be displayed in the status bar.
   */
  private static class DescriptionGenerator {

    Builtins builtins;
    PicocliCommands picocli;

    public DescriptionGenerator(Builtins builtins, PicocliCommands picocli) {
      this.builtins = builtins;
      this.picocli = picocli;
    }

    Widgets.CmdDesc commandDescription(Widgets.CmdLine line) {
      Widgets.CmdDesc out = null;
      switch (line.getDescriptionType()) {
        case COMMAND:
          String cmd = Parser.getCommand(line.getArgs().get(0));
          if (builtins.hasCommand(cmd)) {
            out = builtins.commandDescription(cmd);
          } else if (picocli.hasCommand(cmd)) {
            out = picocli.commandDescription(cmd);
          }
          break;
        default:
          break;
      }
      return out;
    }
  }

  private static void startConsoleLoop() {
    try {
      // set up JLine built-in commands
      Path workDir = Paths.get("");
      Builtins builtins = new Builtins(workDir, null, null);
      builtins.rename(org.jline.builtins.Builtins.Command.TTOP, "top");
      builtins.alias("zle", "widget");
      builtins.alias("bindkey", "keymap");
      Completers.SystemCompleter systemCompleter = builtins.compileCompleters();

      // set up picocli commands
      CliCommands commands = new CliCommands();
      CommandLine cmd = new CommandLine(commands);
      PicocliCommands picocliCommands = new PicocliCommands(workDir, cmd);
      systemCompleter.add(picocliCommands.compileCompleters());
      systemCompleter.compile();
      Terminal terminal = TerminalBuilder.builder().build();
      LineReader reader = LineReaderBuilder.builder()
          .terminal(terminal)
          .completer(systemCompleter)
          .parser(new DefaultParser())
          .variable(LineReader.LIST_MAX, 50)   // max tab completion candidates
          .build();
      builtins.setLineReader(reader);
      commands.setReader(reader);
      DescriptionGenerator descriptionGenerator = new DescriptionGenerator(builtins,
          picocliCommands);
      new Widgets.TailTipWidgets(reader, descriptionGenerator::commandDescription, 5,
          Widgets.TailTipWidgets.TipType.COMPLETER);

      String prompt = "(sdbg) ";
      String rightPrompt = null;

      String line;
      while (true) {
        try {
          line = reader.readLine(prompt, rightPrompt, (MaskingCallback) null, null);
          if (line.matches("^\\s*#.*")) {
            continue;
          }
          ParsedLine pl = reader.getParser().parse(line, 0);
          String[] arguments = pl.words().toArray(new String[0]);
          String command = Parser.getCommand(pl.word());
          if (builtins.hasCommand(command)) {
            builtins.execute(command, Arrays.copyOfRange(arguments, 1, arguments.length)
                , System.in, System.out, System.err);
          } else {
            new CommandLine(commands).execute(arguments);
          }
        } catch (Options.HelpException e) {
          Options.HelpException.highlight(e.getMessage(), Options.HelpException.defaultStyle())
              .print(terminal);
        } catch (UserInterruptException e) {
          // Ignore
        } catch (EndOfFileException e) {
          return;
        } catch (Exception e) {
          AttributedStringBuilder asb = new AttributedStringBuilder();
          asb.append(e.getMessage(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
          asb.toAttributedString().println(terminal);
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static void setLogLevel(int verbosity) {
    if (verbosity == 0) {
      ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      rootLogger.setLevel(Level.OFF);
      return;
    }

    if (verbosity == 1) {
      ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      rootLogger.setLevel(Level.INFO);
    } else if (verbosity == 2) {
      ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      rootLogger.setLevel(Level.DEBUG);
    } else if (verbosity == 3) {
      // Ok, you asked for it.
      ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      rootLogger.setLevel(Level.TRACE);
    }
  }
  public static void main(String[] args) throws Exception {
    AnsiConsole.systemInstall();

    setLogLevel(0);

    String target;
    if (args.length > 0) {
      target = args[0];
    } else {
      target = "LTest;->addNumbers()V";
    }

//    debugger = new SmaliDebugger("LTest;->main([Ljava/lang/String;)V");
    System.out.println("Starting debugger for " + target);
    debugger = new Debugger("LTest;->addNumbers()V");

    startConsoleLoop();
  }
}
