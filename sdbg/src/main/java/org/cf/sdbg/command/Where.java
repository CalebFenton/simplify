package org.cf.sdbg.command;

import org.cf.sdbg.Main;
import org.cf.smalivm.context.ExecutionNode;
import picocli.CommandLine;

@CommandLine.Command(name = "where", aliases = {"w"}, mixinStandardHelpOptions = true, version = "1.0",
    description = "Shows current instruction index and which function you are in")
public class Where implements Runnable {

  @CommandLine.ParentCommand
  CliCommands parent;

  public void run() {
    int instructionIndex = Main.debugger.getCurrentIndex() + 1;
    String methodSignature = Main.debugger.getCurrentMethodSignature();

    parent.out.println("#" + instructionIndex + " in " + methodSignature);
  }
}
