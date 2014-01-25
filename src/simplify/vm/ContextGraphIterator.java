package simplify.vm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class ContextGraphIterator implements Iterator<ContextNode> {
    private final Deque<ContextNode> stack;

    ContextGraphIterator(ContextGraph graph) {
        stack = new ArrayDeque<ContextNode>();
        ContextNode rootNode = graph.getRootNode();

        if (rootNode != null) {
            stack.push(rootNode);
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public ContextNode next() {
        ContextNode result = stack.poll();

        for (ContextNode child : result.getChildren()) {
            stack.push(child);
        }

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
