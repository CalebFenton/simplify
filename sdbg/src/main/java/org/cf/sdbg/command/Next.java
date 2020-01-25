package org.cf.sdbg.command;

import org.cf.sdbg.Main;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.opcode.Op;
import picocli.CommandLine;

@CommandLine.Command(name = "next", aliases = {"n"}, mixinStandardHelpOptions = true, version = "1.0",
    description = "Execute next line of code without entering methods")
public class Next implements Runnable {

  @CommandLine.ParentCommand
  CliCommands parent;

  public void run() {
    try {
      Op currentOp = Main.debugger.getCurrentOp();
      parent.out.println((currentOp.getIndex() + 1) + ":>\t" + currentOp.toString());
      Main.debugger.step();
    } catch (UnhandledVirtualException e) {
      e.printStackTrace(parent.out);
    }
  }
}
