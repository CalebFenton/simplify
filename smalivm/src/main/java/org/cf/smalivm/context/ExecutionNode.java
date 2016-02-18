package org.cf.smalivm.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.cf.smalivm.VirtualException;
import org.cf.smalivm.opcode.ExecutionContextOp;
import org.cf.smalivm.opcode.MethodStateOp;
import org.cf.smalivm.opcode.Op;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionNode {

    private static Logger log = LoggerFactory.getLogger(ExecutionNode.class.getSimpleName());

    private final List<ExecutionNode> children;
    private Op op;
    private ExecutionContext ectx;
    private ExecutionNode parent;
    private Set<VirtualException> exceptions;
    private MethodLocation[] childLocations;

    public ExecutionNode(ExecutionNode other) {
        op = other.op;
        children = new ArrayList<ExecutionNode>(other.getChildren());
    }

    public void setOp(Op op) {
        this.op = op;
    }

    public ExecutionNode(Op op) {
        this.op = op;
        children = new ArrayList<ExecutionNode>(op.getChildren().length);
    }

    public void clearChildren() {
        setChildLocations();
    }

    public void clearExceptions() {
        exceptions = new HashSet<VirtualException>();
    }

    public void execute() {
        ExecutionContext ectx = getContext();
        if (op instanceof MethodStateOp) {
            MethodState mState = ectx.getMethodState();
            ((MethodStateOp) op).execute(this, mState);
        } else if (op instanceof ExecutionContextOp) {
            ((ExecutionContextOp) op).execute(this, ectx);
        }

        // Op didn't set children specifically. Pull in template values.
        if (childLocations == null) {
            setChildLocations(op.getChildren());
        }

        // Op didn't set exceptions specifically. Pull in template values.
        if (exceptions == null) {
            setExceptions(op.getExceptions());
        }
    }

    public void setChildLocations(MethodLocation... childLocations) {
        this.childLocations = childLocations;
    }

    public int getAddress() {
        return op.getAddress();
    }

    public int getCallDepth() {
        return ectx.getCallDepth();
    }

    public MethodLocation[] getChildLocations() {
        return childLocations;
    }

    public List<ExecutionNode> getChildren() {
        return children;
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

    public void removeChild(ExecutionNode child) {
        // http://stream1.gifsoup.com/view/773318/not-the-father-dance-o.gif
        children.remove(child);
        rebuildChildLocationsFromChildren();
    }

    private void rebuildChildLocationsFromChildren() {
        childLocations = new MethodLocation[children.size()];
        for (int i = 0; i < childLocations.length; i++) {
            childLocations[i] = children.get(i).getOp().getLocation();
        }
    }

    public void replaceChild(ExecutionNode oldChild, ExecutionNode newChild) {
        removeChild(oldChild);
        newChild.setParent(this);
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

    public void setParent(@Nonnull ExecutionNode parent) {
        // All nodes will have [0,1] parents since a node represents both an instruction and a context, or vm state.
        // Each execution of an instruction will have a new state.
        this.parent = parent;
        parent.addChild(this);
        getContext().setParent(parent.getContext());
    }

    public ExecutionNode spawnChild(Op childOp) {
        ExecutionNode child = new ExecutionNode(childOp);
        child.setContext(ectx.spawnChild());
        child.setParent(this);

        return child;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ExecutionNode{");
        if (this.ectx != null) {
            sb.append("signature=").append(ectx.getMethodSignature()).append(", ");
        }
        sb.append("op=").append(op.toString()).append('}');

        return sb.toString();
    }

    private void addChild(ExecutionNode child) {
        children.add(child);
        rebuildChildLocationsFromChildren();
    }
}
