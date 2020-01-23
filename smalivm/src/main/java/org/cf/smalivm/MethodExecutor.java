package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
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

    private final ClassManager classManager;
    private final Deque<ExecutionNode> stack;
    private final NodeExecutor nodeExecutor;
    private final ExecutionGraph graph;

    public MethodExecutor(ClassManager classManager, ExecutionGraph graph) {
        this.classManager = classManager;
        this.graph = graph;

        nodeExecutor = new NodeExecutor(graph, classManager);
        stack = new ArrayDeque<>();
        ExecutionNode rootNode = graph.getRoot();
        stack.push(rootNode);
    }

    public boolean finished() {
        return getStack().isEmpty();
    }

    public ExecutionGraph execute() throws VirtualMachineException {
        ExecutionNode rootNode = getGraph().getRoot();
        VirtualMethod method = getGraph().getMethod();
        int callDepth = rootNode.getCallDepth();
        log.info("Executing {}, depth={}", method, callDepth);

        while (!finished()) {
            step();
        }

        return getGraph();
    }

    public ExecutionNode step() throws UnhandledVirtualException {
        ExecutionNode node = getStack().poll();
        nodeExecutor.execute(node);
        getStack().addAll(node.getChildren());

        return node;
    }

    public ExecutionGraph getGraph() {
        return graph;
    }

    public Deque<ExecutionNode> getStack() {
        return stack;
    }

}
