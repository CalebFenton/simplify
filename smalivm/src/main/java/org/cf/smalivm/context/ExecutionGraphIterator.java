package org.cf.smalivm.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

class ExecutionGraphIterator implements Iterator<ExecutionNode> {

    private final Deque<ExecutionNode> stack;

    ExecutionGraphIterator(ExecutionGraph graph) {
        stack = new ArrayDeque<>();
        ExecutionNode rootNode = graph.getRoot();

        if (rootNode != null) {
            stack.push(rootNode);
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public ExecutionNode next() {
        ExecutionNode result = stack.poll();
        stack.addAll(result.getChildren());

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
