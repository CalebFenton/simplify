package simplify.vm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import simplify.Main;
import simplify.handlers.OpHandler;

public class ContextNode {

    private static Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final OpHandler handler;
    private final List<ContextNode> children;
    private ContextNode parent;
    private MethodContext mctx;

    ContextNode(OpHandler handler) {
        this.handler = handler;

        // Most nodes will only have one child.
        children = new ArrayList<ContextNode>(1);
    }

    ContextNode(ContextNode node) {
        this(node.handler);

        if (node.mctx != null) {
            mctx = new MethodContext(node.mctx);
        }
    }

    public int[] execute() {
        log.fine("Handling : " + handler + ", context before: ");

        int[] result = handler.execute(mctx);

        log.fine("Context after: " + mctx);

        return result;
    }

    void setContext(MethodContext mctx) {
        this.mctx = mctx;
    }

    public MethodContext getContext() {
        return mctx;
    }

    public void addChild(ContextNode child) {
        child.setParent(this);
        children.add(child);
    }

    private void setParent(ContextNode parent) {
        // All nodes will have [0,1] parents since a node represents both an instruction and a context, or vm state.
        // Each execution of an instruction will have a new state.
        this.parent = parent;
    }

    public ContextNode getParent() {
        return parent;
    }

    public List<ContextNode> getChildren() {
        return children;
    }

    public int getAddress() {
        return handler.getAddress();
    }

    @Override
    public String toString() {
        return handler.toString();
    }

    public String toGraph() {
        // TODO Auto-generated method stub
        return null;
    }
}
