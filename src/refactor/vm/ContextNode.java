package refactor.vm;

import java.util.ArrayList;
import java.util.List;

import refactor.handler.OpHandler;

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
        int[] result = handler.execute(mctx);

        return result;
    }

    void setContext(MethodContext mectx) {
        this.mctx = mectx;
    }

    public MethodContext getContext() {
        return mctx;
    }

    public void addChild(ContextNode child) {
        child.setParent(this);
        children.add(child);
    }

    private void setParent(ContextNode parent) {
        this.parent = parent;
    }

    public int getIndex() {
        return handler.getAddress();
    }

    public ContextNode getParent() {
        return parent;
    }

    public List<ContextNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return handler.toString();
    }
}
