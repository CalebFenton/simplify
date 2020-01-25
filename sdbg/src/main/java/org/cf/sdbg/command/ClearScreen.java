package org.cf.sdbg.command;

import java.io.IOException;
import java.util.concurrent.Callable;
import picocli.CommandLine;

/**
 * Command that clears the screen.
 */
@CommandLine.Command(name = "cls", aliases = "clear", mixinStandardHelpOptions = true, version = "1.0",
    description = "Clears the screen")
public class ClearScreen implements Callable<Void> {

  @CommandLine.ParentCommand
  CliCommands parent;

  public Void call() throws IOException {
    parent.reader.clearScreen();
    return null;
  }
}
