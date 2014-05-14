package simplify.vm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import simplify.Main;
import simplify.vm.ops.Op;

public class ContextNode {

    private final static String DOT = "[^a-zA-Z\200-\377_0-9\\s\\p{Punct}]";

    private static Logger log = Logger.getLogger(Main.class.getSimpleName());
    private final List<ContextNode> children;
    private final Op handler;
    private MethodContext ctx;
    private ContextNode parent;

    ContextNode(ContextNode node) {
        this(node.handler);

        if (node.ctx != null) {
            ctx = new MethodContext(node.ctx);
        }
    }

    ContextNode(Op handler) {
        this.handler = handler;

        // Most nodes will only have one child.
        children = new ArrayList<ContextNode>(1);
    }

    public void addChild(ContextNode child) {
        child.setParent(this);
        children.add(child);
    }

    public int[] execute() {
        log.fine("HANDLING @" + handler.getAddress() + ": " + handler + "\nContext before: " + ctx);

        int[] result = handler.execute(ctx);

        log.fine("Context after: " + ctx);

        return result;
    }

    public int getAddress() {
        return handler.getAddress();
    }

    public List<ContextNode> getChildren() {
        return children;
    }

    public MethodContext getContext() {
        return ctx;
    }

    public Op getHandler() {
        return handler;
    }

    public ContextNode getParent() {
        return parent;
    }

    public String toGraph() {
        List<ContextNode> visitedNodes = new ArrayList<ContextNode>();

        StringBuilder sb = new StringBuilder("digraph {\n");
        getGraph(sb, visitedNodes);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return handler.toString();
    }

    private void getGraph(StringBuilder sb, List<ContextNode> visitedNodes) {
        if (visitedNodes.contains(this)) {
            return;
        }
        visitedNodes.add(this);

        for (ContextNode child : getChildren()) {
            String op = toString().replaceAll(DOT, "?").replace("\"", "\\\"");
            String ctx = getContext().toString().replaceAll(DOT, "?").replace("\"", "\\\"").trim();
            sb.append("\"").append(getAddress()).append("\n").append(op).append("\n").append(ctx).append("\"");

            sb.append(" -> ");

            op = toString().replaceAll(DOT, "?").replace("\"", "\\\"");
            ctx = getContext().toString().replaceAll(DOT, "?").replace("\"", "\\\"").trim();
            sb.append("\"").append(getAddress()).append("\n").append(op).append("\n").append(ctx).append("\"");
            sb.append("\n");
            op = null;
            ctx = null;

            child.getGraph(sb, visitedNodes);
        }
    }

    private void setParent(ContextNode parent) {
        // All nodes will have [0,1] parents since a node represents both an instruction and a context, or vm state.
        // Each execution of an instruction will have a new state.
        this.parent = parent;
    }

    void setContext(MethodContext ctx) {
        this.ctx = ctx;
    }
}
