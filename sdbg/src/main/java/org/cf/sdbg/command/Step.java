package org.cf.sdbg.command;

import org.cf.sdbg.Main;
import org.cf.smalivm.MethodExecutor;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.Op;
import picocli.CommandLine;

@CommandLine.Command(name = "step", aliases = {"s"}, mixinStandardHelpOptions = true, version = "1.0",
    description = "Step to next line of code including stepping into functions")
public class Step implements Runnable {

  @CommandLine.ParentCommand
  CliCommands parent;

  public void run() {
    try {
      Op currentOp = Main.debugger.getCurrentOp();
      parent.out.println((currentOp.getIndex() + 1) + ":>\t" + currentOp.toString());

      if (currentOp instanceof InvokeOp) {
        InvokeOp invokeOp = (InvokeOp) currentOp;
        invokeOp.setDebugMode(true);
        Main.debugger.step();

        MethodExecutor invokedMethodExecutor = invokeOp.getDebuggedMethodExecutor();
        Main.debugger.stepIntoInvoke(invokedMethodExecutor, invokeOp);
      } else {
        Main.debugger.step();
      }
    } catch (UnhandledVirtualException e) {
      e.printStackTrace(parent.out);
    }
  }
}
