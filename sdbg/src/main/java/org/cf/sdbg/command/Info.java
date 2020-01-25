package org.cf.sdbg.command;

import org.cf.sdbg.Main;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import picocli.CommandLine;

@CommandLine.Command(name = "info", aliases = {"i"}, mixinStandardHelpOptions = true, version = "1.0",
    description = "Info")
public class Info implements Runnable {

  @CommandLine.ParentCommand
  CliCommands parent;

  public void run() {
    ExecutionNode currentNode = Main.debugger.getCurrentNode();
    ExecutionContext context = currentNode.getContext();
    String methodState = context.getMethodState().toString(false);
    parent.out.println(methodState);
  }
}
