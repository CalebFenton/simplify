package org.cf.smalivm.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.VirtualException;
import org.cf.smalivm.opcode.ExecutionContextOp;
import org.cf.smalivm.opcode.MethodStateOp;
import org.cf.smalivm.opcode.Op;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionNode {

    private final static String DOT = "[^a-zA-Z\200-\377_0-9\\s\\p{Punct}]";

    private static Logger log = LoggerFactory.getLogger(ExecutionNode.class.getSimpleName());

    private final List<ExecutionNode> childNodes;
    private final Op op;

    private ExecutionContext ectx;
    private ExecutionNode parent;
    private BuilderInstruction[] children;
    private Set<VirtualException> exceptions;

    public ExecutionNode(ExecutionNode other) {
        op = other.op;
        childNodes = new ArrayList<ExecutionNode>(other.getChildren());
    }

    public ExecutionNode(Op op) {
        this.op = op;
        childNodes = new ArrayList<ExecutionNode>(op.getChildren().length);
    }

    public void clearChildren() {
        setChildren();
    }

    public void clearExceptions() {
        exceptions = new HashSet<VirtualException>();
    }

    public void execute() {
        ExecutionContext ectx = getContext();
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("HANDLING @").append(op.getAddress()).append(": ").append(op);
            sb.append("\nContext before:\n").append(ectx);
            log.debug(sb.toString());
        }

        if (op instanceof MethodStateOp) {
            MethodState mState = ectx.getMethodState();
            ((MethodStateOp) op).execute(this, mState);
        } else if (op instanceof ExecutionContextOp) {
            ((ExecutionContextOp) op).execute(this, ectx);
        }

        if (log.isDebugEnabled()) {
            log.debug("Context after:\n" + ectx);
        }

        // Op didn't set children specifically. Pull in template values.
        if (children == null) {
            setChildren(op.getChildren());
        }

        // Op didn't set exceptions specifically. Pull in template values.
        if (exceptions == null) {
            setExceptions(op.getExceptions());
        }
    }

    public int getAddress() {
        return op.getAddress();
    }

    public int getCallDepth() {
        return ectx.getCallDepth();
    }

    public int[] getChildAddresses() {
        int[] childAddresses = new int[children.length];
        for (int i = 0; i < children.length; i++) {
            childAddresses[i] = children[i].getLocation().getCodeAddress();
        }

        return childAddresses;
    }

    public List<ExecutionNode> getChildren() {
        return childNodes;
    }

    public ExecutionContext getContext() {
        return ectx;
    }

    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    public Op getOp() {
        return op;
    }

    public ExecutionNode getParent() {
        return parent;
    }

    public boolean mayThrowException() {
        return exceptions != null && exceptions.size() > 0;
    }

    public void removeChildNode(ExecutionNode child) {
        // http://stream1.gifsoup.com/view/773318/not-the-father-dance-o.gif
        childNodes.remove(child);
    }

    public void replaceChildNode(ExecutionNode oldChild, ExecutionNode newChild) {
        removeChildNode(oldChild);
        newChild.setParentNode(this);
    }

    public void setChildren(BuilderInstruction... children) {
        this.children = children;
    }

    public void setContext(ExecutionContext ectx) {
        this.ectx = ectx;
    }

    public void setException(VirtualException exception) {
        exceptions = new HashSet<VirtualException>();
        exceptions.add(exception);
    }

    public void setExceptions(Set<VirtualException> exceptions) {
        this.exceptions = exceptions;
    }

    public void setMethodState(MethodState mState) {
        ectx.setMethodState(mState);
    }

    public void setParentNode(ExecutionNode parent) {
        // All nodes will have [0,1] parents since a node represents both an instruction and a context, or vm state.
        // Each execution of an instruction will have a new state.
        this.parent = parent;
        if (parent != null) {
            parent.addChildNode(this);
        }
    }

    public ExecutionNode spawnChild(Op childOp) {
        ExecutionNode child = new ExecutionNode(childOp);
        child.setContext(ectx.spawnChild());
        child.setParentNode(this);

        return child;
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

    private void addChildNode(ExecutionNode child) {
        childNodes.add(child);
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
            sb.append('"').append(getAddress()).append('\n').append(op).append('\n').append(ctx).append('"');

            sb.append(" -> ");

            ExecutionContext childExecutionContext = child.getContext();
            MethodState childMethodState = childExecutionContext.getMethodState();
            op = toString().replaceAll(DOT, "?").replace("\"", "\\\"");
            ctx = childMethodState.toString().replaceAll(DOT, "?").replace("\"", "\\\"").trim();
            sb.append('"').append(getAddress()).append('\n').append(op).append('\n').append(ctx).append('"');
            sb.append('\n');

            child.getGraph(sb, visitedNodes);
        }
    }

}
