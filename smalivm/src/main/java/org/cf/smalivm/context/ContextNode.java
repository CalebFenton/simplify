package org.cf.smalivm.context;

import java.util.ArrayList;
import java.util.List;

import org.cf.smalivm.op_handler.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextNode {

    private static Logger log = LoggerFactory.getLogger(ContextNode.class.getSimpleName());

    private final static String DOT = "[^a-zA-Z\200-\377_0-9\\s\\p{Punct}]";

    private final List<ContextNode> children;
    private final Op handler;
    private MethodContext ctx;
    private ContextNode parent;

    public ContextNode(ContextNode node) {
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

    void removeChild(ContextNode child) {
        children.remove(child);
    }

    void replaceChild(ContextNode oldChild, ContextNode newChild) {
        int index = children.indexOf(oldChild);
        children.remove(index);
        children.add(index, newChild);
    }

    public int[] execute() {
        log.debug("HANDLING @" + handler.getAddress() + ": " + handler + "\nContext before: " + ctx);
        int[] result = handler.execute(ctx);
        log.debug("Context after: " + ctx);

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

    public Op getOpHandler() {
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

    void setParent(ContextNode parent) {
        // All nodes will have [0,1] parents since a node represents both an instruction and a context, or vm state.
        // Each execution of an instruction will have a new state.
        this.parent = parent;
    }

    public void setContext(MethodContext ctx) {
        this.ctx = ctx;
    }
}
