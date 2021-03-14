package org.cf.smalivm.context;

import org.cf.smalivm.opcode.ExecutionContextOp;
import org.cf.smalivm.opcode.MethodStateOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.VirtualMethod;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

public class ExecutionNode {

    private static Logger log = LoggerFactory.getLogger(ExecutionNode.class.getSimpleName());

    private final List<ExecutionNode> children;
    private Op op;
    private ExecutionContext context;
    private ExecutionNode parent;
    private Set<Throwable> exceptions;
    private MethodLocation[] childLocations;

    public ExecutionNode(ExecutionNode other) {
        op = other.op;
        List l = new ArrayList<? extends Op>()
        children = new ArrayList<>(other.getChildren());
    }

    public ExecutionNode(Op op) {
        this.op = op;
        children = new ArrayList<>(op.getChildren().length);
    }

    public void clearChildren() {
        setChildLocations();
    }

    public void clearExceptions() {
        exceptions = new HashSet<>();
    }

    public void execute() {
        ExecutionContext context = getContext();
        if (op instanceof MethodStateOp) {
            MethodState mState = context.getMethodState();
            ((MethodStateOp) op).execute(this, mState);
        } else {
            ((ExecutionContextOp) op).execute(this, context);
        }

        // Op didn't set children; pull in template values.
        if (childLocations == null) {
            setChildLocations(op.getChildren());
        }

        // Op didn't set exceptions; pull in template values.
        if (exceptions == null) {
            setExceptions(op.getExceptions());
        }
    }

    public int getAddress() {
        return op.getAddress();
    }

    public int getIndex() {
        return op.getIndex();
    }

    public VirtualMethod getMethod() {
        return context.getMethod();
    }

    public int getCallDepth() {
        return context.getCallDepth();
    }

    public MethodLocation[] getChildLocations() {
        return childLocations;
    }

    public void setChildLocations(MethodLocation... childLocations) {
        this.childLocations = childLocations;
    }

    public List<ExecutionNode> getChildren() {
        return children;
    }

    public ExecutionContext getContext() {
        return context;
    }

    public void setContext(ExecutionContext context) {
        this.context = context;
    }

    public Set<Throwable> getExceptions() {
        return exceptions;
    }

    public void setExceptions(Set<Throwable> exceptions) {
        this.exceptions = exceptions;
    }

    public Op getOp() {
        return op;
    }

    public void setOp(Op op) {
        this.op = op;
    }

    public ExecutionNode getParent() {
        return parent;
    }

    public void setParent(@Nonnull ExecutionNode parent) {
        // All nodes will have [0,1] parents since a node represents both an instruction and a context, or vm state.
        // Each execution of an instruction will have a new state.
        this.parent = parent;
        parent.addChild(this);
        getContext().setParent(parent.getContext());
    }

    public boolean mayThrowException() {
        return exceptions != null && exceptions.size() > 0;
    }

    public void removeChild(ExecutionNode child) {
        // http://stream1.gifsoup.com/view/773318/not-the-father-dance-o.gif
        children.remove(child);
        rebuildChildLocationsFromChildren();
    }

    public void replaceChild(ExecutionNode oldChild, ExecutionNode newChild) {
        removeChild(oldChild);
        newChild.setParent(this);
    }

    public void setException(Throwable exception) {
        exceptions = new HashSet<>();
        exceptions.add(exception);
    }

    public void setMethodState(MethodState mState) {
        context.setMethodState(mState);
    }

    public ExecutionNode spawnChild(Op childOp) {
        ExecutionNode child = new ExecutionNode(childOp);
        child.setContext(context.spawnChild());
        child.setParent(this);

        return child;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ExecutionNode{");
        if (this.context != null) {
            sb.append("signature=").append(context.getMethod()).append(", ");
        }
        sb.append("op=").append(op.toString()).append(", ");
        sb.append("@=").append(op.getAddress()).append('}');

        return sb.toString();
    }

    private void addChild(ExecutionNode child) {
        children.add(child);
        rebuildChildLocationsFromChildren();
    }

    private void rebuildChildLocationsFromChildren() {
        childLocations = new MethodLocation[children.size()];
        for (int i = 0; i < childLocations.length; i++) {
            childLocations[i] = children.get(i).getOp().getLocation();
        }
    }
}
