package simplify.vm;

import java.util.ArrayList;
import java.util.List;

import simplify.handlers.OpHandler;

public class ContextNode {

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
        System.out.println("executing with: " + mctx);

        int[] result = handler.execute(mctx);

        System.out.println("after executing: " + mctx);
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
}
