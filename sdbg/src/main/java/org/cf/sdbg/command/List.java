package org.cf.sdbg.command;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cf.sdbg.Main;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.type.VirtualMethod;
import picocli.CommandLine;

@CommandLine.Command(name = "list", aliases = {"l"}, mixinStandardHelpOptions = true, version = "1.0",
    description = "List source code")
public class List implements Runnable {

  @CommandLine.ParentCommand
  CliCommands parent;

  @CommandLine.Parameters(index = "0", arity = "0..1", description = "line-number | start#,end# | method | class->method", paramLabel = "TARGET")
  String target;

  public void run() {
    ExecutionGraph graph = Main.debugger.getExecutionGraph();
    int start = 0;
    int end = 0;
    if (target != null) {
      if (target.contains("(")) {
        String methodSignature;
        if (target.contains("->")) {
          methodSignature = target;
        } else {
          String className = Main.debugger.getExecutionGraph().getMethod().getClassName();
          methodSignature = className + "->" + target;
        }
        VirtualMethod virtualMethod = Main.debugger.getClassManager().getMethod(methodSignature);
        if (virtualMethod == null) {
          parent.out.println("invalid target; method signature not found");
          return;
        }
        graph = Main.debugger.getVirtualMachine().spawnInstructionGraph(virtualMethod);
      } else if (target.contains(",")) {
        int[] positions = Arrays.stream(target.split(",", 2))
            .mapToInt(Integer::parseInt)
            .toArray();
        start = positions[0];
        end = positions[1];
        if (start > end) {
          parent.out.println("invalid target; start# > end#");
          return;
        }
        if (start <= 0) {
          parent.out.println("invalid target; start# <= 0");
          return;
        }
      } else {
        try {
          start = Integer.parseInt(target);
          end = start;
          if (start <= 0) {
            parent.out.println("invalid target; line-number <= 0");
            return;
          }
        } catch (NumberFormatException ex) {
          parent.out.println("invalid target; unable to parse line-number");
          return;
        }
      }
    }
    String[] theLines = graph.toSmali(true).split("\n");
    if (start == 0) {
      end = theLines.length - 1;
    } else {
      start--;
      end--;
      if (end >= theLines.length) {
        end = theLines.length - 1;
      }
    }
    int instructionIndex = Main.debugger.getCurrentNode().getIndex();
    java.util.List<String> lines = IntStream.range(start, end + 1)
        .mapToObj(n -> (n + 1) + (n == instructionIndex ? ":>" : ":") + "\t" + theLines[n])
        .collect(Collectors.toList());
    String code = String.join("\n", lines);
    parent.out.println(code);
  }
}
