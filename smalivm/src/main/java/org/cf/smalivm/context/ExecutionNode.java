package org.cf.smalivm.context;

import java.util.ArrayList;
import java.util.List;

import org.cf.smalivm.opcode.ExecutionContextOp;
import org.cf.smalivm.opcode.MethodStateOp;
import org.cf.smalivm.opcode.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionNode {

    private final static String DOT = "[^a-zA-Z\200-\377_0-9\\s\\p{Punct}]";

    private static Logger log = LoggerFactory.getLogger(ExecutionNode.class.getSimpleName());

    private final List<ExecutionNode> children;
    private ExecutionContext ectx;
    private final Op op;
    private ExecutionNode parent;

    public ExecutionNode(Op op) {
        this.op = op;
        children = new ArrayList<ExecutionNode>(op.getPossibleChildren().length);
    }

    ExecutionNode(ExecutionNode other) {
        op = other.op;
        children = new ArrayList<ExecutionNode>(other.getChildren());
    }

    public int[] execute() {
        ExecutionContext ectx = getContext();
        // TODO: print method and class states
        MethodState mState = ectx.getMethodState();
        log.debug("HANDLING @" + op.getAddress() + ": " + op + "\nState before: " + mState);

        int[] result = null;
        if (op instanceof MethodStateOp) {
            result = ((MethodStateOp) op).execute(mState);
        } else if (op instanceof ExecutionContextOp) {
            result = ((ExecutionContextOp) op).execute(ectx);
        }
        log.debug("State after: " + mState);

        return result;
    }

    public int getAddress() {
        return op.getAddress();
    }

    public int getCallDepth() {
        return ectx.getCallDepth();
    }

    public ExecutionNode getChild(Op childOp) {
        ExecutionNode child = new ExecutionNode(childOp);
        child.setContext(ectx.getChild());
        child.setParent(this);

        return child;
    }

    public List<ExecutionNode> getChildren() {
        return children;
    }

    public ClassState getClassState(String className) {
        return ectx.getClassState(className);
    }

    public ExecutionContext getContext() {
        return ectx;
    }

    public MethodState getMethodState() {
        return ectx.getMethodState();
    }

    public Op getOp() {
        return op;
    }

    public ExecutionNode getParent() {
        return parent;
    }

    public void removeChild(ExecutionNode child) {
        children.remove(child);
    }

    public void replaceChild(ExecutionNode oldChild, ExecutionNode newChild) {
        int index = children.indexOf(oldChild);
        // http://stream1.gifsoup.com/view/773318/not-the-father-dance-o.gif
        assert index >= 0;
        children.remove(index);
        children.add(index, newChild);
        newChild.setParent(this);
    }

    public void setClassState(String className, ClassState cState) {
        ectx.setClassState(className, cState);
    }

    public void setContext(ExecutionContext ectx) {
        this.ectx = ectx;
    }

    public void setMethodState(MethodState mState) {
        ectx.setMethodState(mState);
    }

    public void setParent(ExecutionNode parent) {
        // All nodes will have [0,1] parents since a node represents both an instruction and a context, or vm state.
        // Each execution of an instruction will have a new state.
        this.parent = parent;
        parent.addChild(this);
    }

    public String toGraph() {
        List<ExecutionNode> visitedNodes = new ArrayList<ExecutionNode>();
        StringBuilder sb = new StringBuilder("digraph {\n");
        getGraph(sb, visitedNodes);
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toString() {
        return op.toString();
    }

    private void addChild(ExecutionNode child) {
        children.add(child);
    }

    private void getGraph(StringBuilder sb, List<ExecutionNode> visitedNodes) {
        if (visitedNodes.contains(this)) {
            return;
        }
        visitedNodes.add(this);

        ExecutionContext parentExecutionContext = getContext();
        MethodState parentMethodState = parentExecutionContext.getMethodState();
        for (ExecutionNode child : getChildren()) {
            String op = toString().replaceAll(DOT, "?").replace("\"", "\\\"");
            String ctx = parentMethodState.toString().replaceAll(DOT, "?").replace("\"", "\\\"").trim();
            sb.append("\"").append(getAddress()).append("\n").append(op).append("\n").append(ctx).append("\"");

            sb.append(" -> ");

            ExecutionContext childExecutionContext = child.getContext();
            MethodState childMethodState = childExecutionContext.getMethodState();
            op = toString().replaceAll(DOT, "?").replace("\"", "\\\"");
            ctx = childMethodState.toString().replaceAll(DOT, "?").replace("\"", "\\\"").trim();
            sb.append("\"").append(getAddress()).append("\n").append(op).append("\n").append(ctx).append("\"");
            sb.append("\n");

            child.getGraph(sb, visitedNodes);
        }
    }

}
