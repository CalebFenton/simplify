package org.cf.smalivm.debug;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.cf.smalivm.MethodExecutor;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualMethod;

public class Debugger {

  private final VirtualMachine vm;
  private final Stack<MethodExecutor> steppedMethodExecutors;
  private final Stack<InvokeOp> steppedInvokeOps;
  private final Set<Breakpoint> breakpoints;
  private ExecutionNode currentNode;

  public Debugger(File smaliPath, String methodSignature) throws IOException {
    this(new VirtualMachineFactory().build(smaliPath), methodSignature);
  }

  public Debugger(VirtualMachine vm, String methodSignature) {
    this.vm = vm;
    this.vm.getMethodExecutorFactory().setInteractive();

    steppedMethodExecutors = new Stack<>();
    steppedInvokeOps = new Stack<>();
    breakpoints = new HashSet<>();

    ClassManager classManager = vm.getClassManager();
    VirtualMethod virtualMethod = classManager.getMethod(methodSignature);
    if (virtualMethod == null) {
      throw new IllegalArgumentException("Method signature not found: " + methodSignature);
    }
    MethodExecutor methodExecutor = vm.getMethodExecutorFactory().build(virtualMethod);
    currentNode = methodExecutor.getCurrentNode();
    steppedMethodExecutors.push(methodExecutor);
  }

  private ExecutionNode stepIntoInvoke(InvokeOp invokeOp) throws UnhandledVirtualException {
    invokeOp.setDebugMode(true);
    ExecutionNode node = getMethodExecutor().step();
    invokeOp.setDebugMode(false);

    MethodExecutor invokedMethodExecutor = invokeOp.getDebuggedMethodExecutor();
    steppedMethodExecutors.push(invokedMethodExecutor);
    steppedInvokeOps.push(invokeOp);
    return node;
  }

  private void stepOutOfInvoke() {
    MethodExecutor methodExecutor = steppedMethodExecutors.pop();
    InvokeOp invokeOp = steppedInvokeOps.pop();
    invokeOp.finishDebugLocalMethod(methodExecutor.getExecutionGraph());
  }

  public VirtualMachine getVirtualMachine() {
    return vm;
  }

  public ClassManager getClassManager() {
    return vm.getClassManager();
  }

  public ExecutionGraph getExecutionGraph() {
    return getMethodExecutor().getExecutionGraph();
  }

  public MethodExecutor getMethodExecutor() {
    return steppedMethodExecutors.peek();
  }

  public VirtualMethod getCurrentMethod() {
    return getExecutionGraph().getMethod();
  }

  public String getCurrentMethodSignature() {
    return getCurrentMethod().getSignature();
  }

  @Nonnull
  public ExecutionNode getCurrentNode() {
    return currentNode;
  }

  public Op getCurrentOp() {
    return getCurrentNode().getOp();
  }

  public int getCurrentIndex() {
    return getCurrentOp().getIndex();
  }

  public boolean addBreakpoint(String methodSignature, int instructionIndex) {
    Breakpoint breakpoint = new Breakpoint(methodSignature, instructionIndex);
    return breakpoints.add(breakpoint);
  }

  public boolean removeBreakpoint(String methodSignature, int instructionIndex) {
    Breakpoint breakpoint = new Breakpoint(methodSignature, instructionIndex);
    return breakpoints.remove(breakpoint);
  }

  public void clearBreakpoints() {
    breakpoints.clear();
  }

  public boolean isBreakpoint(String methodSignature, int instructionIndex) {
    Breakpoint breakpoint = new Breakpoint(methodSignature, instructionIndex);
    return breakpoints.contains(breakpoint);
  }

  public boolean isBreakpoint(ExecutionNode node) {
    String methodSignature = node.getMethod().getSignature();
    int index = node.getIndex();
    return isBreakpoint(methodSignature, index);
  }

  public boolean isAtBreakpoint() {
    return isBreakpoint(currentNode);
  }

  public Set<Breakpoint> getBreakpoints() {
    return breakpoints;
  }

  public boolean isFinished() {
    return getMethodExecutor().isFinished();
  }

  @Nullable
  public ExecutionNode step() throws UnhandledVirtualException {
    return step(true);
  }

  @Nullable
  public ExecutionNode step(boolean stepIntoInvokes) throws UnhandledVirtualException {
    if (isFinished()) {
      return null;
    }

    Op currentOp = getCurrentOp();
    if (stepIntoInvokes && currentOp instanceof InvokeOp) {
      currentNode = stepIntoInvoke(((InvokeOp) currentOp));
    } else {
      currentNode = getMethodExecutor().step();
    }
    boolean insideDebuggedInvoke = currentNode.getCallDepth() > 0;
    if (isFinished() && insideDebuggedInvoke) {
      stepOutOfInvoke();
    }

    if (isFinished()) {
      return null;
    } else {
      currentNode = getMethodExecutor().getCurrentNode();
      return currentNode;
    }
  }

  public void run() throws UnhandledVirtualException {
    while (true) {
      ExecutionNode lastNode = step();
      if (lastNode == null || isAtBreakpoint()) {
        break;
      }
    }
  }

}
