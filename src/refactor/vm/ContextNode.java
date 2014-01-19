package refactor.vm;

import java.util.ArrayList;
import java.util.List;

import refactor.handler.OpHandler;

public class ContextNode {

    private final OpHandler handler;
    private final List<ContextNode> children;
    private ContextNode parent;
    private MethodContext mectx;

    ContextNode(OpHandler handler) {
        this.handler = handler;

        // Most nodes will only have one child.
        children = new ArrayList<ContextNode>(1);
    }

    ContextNode(ContextNode node) {
        this(node.handler);
        if (node.mectx != null) {
            mectx = new MethodContext(node.mectx);
        }
    }

    public int[] execute() {
        return handler.execute(mectx);
    }

    void setContext(MethodContext mectx) {
        this.mectx = mectx;
    }

    public MethodContext getContext() {
        return mectx;
    }

    public void addChild(ContextNode child) {
        child.setParent(this);
        children.add(child);
    }

    private void setParent(ContextNode parent) {
        this.parent = parent;
    }

    public int getIndex() {
        return handler.getIndex();
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
