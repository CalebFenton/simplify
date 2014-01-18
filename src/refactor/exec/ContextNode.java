package refactor.exec;

import java.util.ArrayList;
import java.util.List;

import refactor.op.OpHandler;

public class ContextNode {

    private final OpHandler handler;
    private final List<ContextNode> children;
    private ContextNode parent;
    private MethodExecutionContext mectx;

    ContextNode(OpHandler handler) {
        this.handler = handler;

        children = new ArrayList<ContextNode>(1);
    }

    ContextNode(ContextNode node) {
        this(node.handler);
        if (node.mectx != null) {
            mectx = new MethodExecutionContext(node.mectx);
        }
    }

    public int[] execute() {
        return handler.execute(mectx);
    }

    void setContext(MethodExecutionContext mectx) {
        this.mectx = mectx;
    }

    public MethodExecutionContext getContext() {
        return mectx;
    }

    public void addChild(ContextNode child) {
        child.setParent(this);
        children.add(child);
    }

    private void setParent(ContextNode parent) {
        this.parent = parent;
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
