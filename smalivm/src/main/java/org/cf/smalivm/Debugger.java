package org.cf.smalivm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualMethod;

public class Debugger {

  private static VirtualMachine vm = null;

  private final Stack<MethodExecutor> steppedMethodExecutors;
  private final Stack<InvokeOp> steppedInvokeOps;
  private final Set<String> breakpoints;

  public Debugger(String methodSignature) throws IOException {
    if (vm == null) {
      VirtualMachineFactory vmFactory = new VirtualMachineFactory();
      vm = vmFactory.build("resources/");
      vm.getMethodExecutorFactory().setInteractive();
    }

    steppedMethodExecutors = new Stack<>();
    steppedInvokeOps = new Stack<>();
    breakpoints = new HashSet<>();

    ClassManager classManager = vm.getClassManager();
    VirtualMethod virtualMethod = classManager.getMethod(methodSignature);
    MethodExecutor methodExecutor = vm.getMethodExecutorFactory().build(virtualMethod);
    steppedMethodExecutors.push(methodExecutor);
  }

  public void stepIntoInvoke(MethodExecutor methodExecutor, InvokeOp invokeOp) {
    steppedMethodExecutors.push(methodExecutor);
    steppedInvokeOps.push(invokeOp);
  }

  public void stepOutOfInvoke() {
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

  public ExecutionNode getCurrentNode() {
    return getMethodExecutor().getCurrentNode();
  }

  public Op getCurrentOp() {
    return getCurrentNode().getOp();
  }

  public int getCurrentIndex() {
    return getCurrentOp().getIndex();
  }

  public boolean addBreakpoint(String methodSignature, int index) {
    String key = makeBreakpointKey(methodSignature, index);
    return breakpoints.add(key);
  }

  public boolean removeBreakpoint(String methodSignature, int index) {
    String key = makeBreakpointKey(methodSignature, index);
    return breakpoints.remove(key);
  }

  public boolean isBreakpoint(String methodSignature, int index) {
    String key = makeBreakpointKey(methodSignature, index);
    return breakpoints.contains(key);
  }

  private static String makeBreakpointKey(String methodSignature, int index) {
    return methodSignature + ":" + index;
  }

  public boolean isFinished() {
    return getMethodExecutor().isFinished();
  }

  public ExecutionNode step() throws UnhandledVirtualException {
    ExecutionNode node = getMethodExecutor().step();
    boolean steppedIntoInvoke = node.getCallDepth() > 0;
    if (isFinished() && steppedIntoInvoke) {
      stepOutOfInvoke();
    }

    return node;
  }

}
