package org.cf.sdbg.command;

import java.util.concurrent.TimeUnit;
import org.cf.sdbg.Main;
import picocli.CommandLine;

/**
 * A command with some options to demonstrate completion.
 */
@CommandLine.Command(name = "cmd", mixinStandardHelpOptions = true, version = "1.0",
    description = "Command with some options to demonstrate TAB-completion" +
        " (note that enum values also get completed)",
    subcommands = CommandLine.HelpCommand.class)
public class MyCommand implements Runnable {

  @CommandLine.Option(names = {"-v", "--verbose"},
      description = {
          "Specify multiple -v options to increase verbosity.",
          "For example, `-v -v -v` or `-vvv`"
      })
  private boolean[] verbosity = {};

  @CommandLine.ArgGroup(exclusive = false)
  private MyDuration myDuration = new MyDuration();

  static class MyDuration {

    @CommandLine.Option(names = {"-d", "--duration"},
        description = "The duration quantity.",
        required = true)
    private int amount;

    @CommandLine.Option(names = {"-u", "--timeUnit"},
        description = "The duration time unit.",
        required = true)
    private TimeUnit unit;
  }

  @CommandLine.ParentCommand
  CliCommands parent;

  public void run() {
    if (verbosity.length > 0) {
      parent.out.printf("Hi there. You asked for %d %s.%n",
          myDuration.amount, myDuration.unit);
    } else {
      parent.out.println("hi!");
    }
  }
}
