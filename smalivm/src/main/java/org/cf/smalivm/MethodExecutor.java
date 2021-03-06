package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionGraphImpl;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.exception.VirtualMachineException;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

public class MethodExecutor {

    private static Logger log = LoggerFactory.getLogger(MethodExecutor.class.getSimpleName());

    private final Deque<ExecutionNode> stack;
    private final NodeExecutor nodeExecutor;
    private final ExecutionGraphImpl graph;

    public MethodExecutor(ClassManager classManager, ExecutionGraphImpl graph) {
        this.graph = graph;

        nodeExecutor = new NodeExecutor(graph, classManager);
        stack = new ArrayDeque<>();
        ExecutionNode rootNode = graph.getRoot();
        stack.push(rootNode);
    }

    public boolean isFinished() {
        return getStack().isEmpty();
    }

    public ExecutionGraph execute() throws VirtualMachineException {
        VirtualMethod method = getExecutionGraph().getMethod();
        int callDepth = getCurrentNode().getCallDepth();
        log.info("Executing {}, depth={}", method, callDepth);

        while (!isFinished()) {
            step();
        }

        return getExecutionGraph();
    }

    public ExecutionNode step() throws UnhandledVirtualException {
        ExecutionNode node = getStack().poll();
        nodeExecutor.execute(node);
        getStack().addAll(node.getChildren());

        return node;
    }

    public ExecutionNode getCurrentNode() {
        return getStack().peek();
    }

    public VirtualMethod getVirtualMethod() {
        return getExecutionGraph().getMethod();
    }

    public ExecutionGraphImpl getExecutionGraph() {
        return graph;
    }

    public Deque<ExecutionNode> getStack() {
        return stack;
    }

}
