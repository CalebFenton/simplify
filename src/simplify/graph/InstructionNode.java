package simplify.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jf.dexlib2.builder.BuilderInstruction;

import simplify.exec.MethodExecutionContext;

public class InstructionNode {

    private final List<InstructionNode> parents;
    private final List<InstructionNode> children;
    private final MethodExecutionContext ectx;

    private BuilderInstruction instruction;

    public InstructionNode(BuilderInstruction instruction, MethodExecutionContext ectx) {
        parents = new ArrayList<InstructionNode>();
        children = new ArrayList<InstructionNode>();

        this.ectx = ectx;
        this.instruction = instruction;
    }

    public MethodExecutionContext getContext() {
        return ectx;
    }

    public BuilderInstruction getInstruction() {
        return instruction;
    }

    public void addChild(InstructionNode child) {
        children.add(child);
        child.addParent(this);
    }

    private void addParent(InstructionNode parent) {
        parents.add(parent);
    }

    public List<InstructionNode> getParents() {
        return parents;
    }

    public List<InstructionNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return instruction.getOpcode().name + " @" + instruction.getLocation().getIndex();
    }

    public String toGraph() {
        // For GraphViz
        return "digraph {\n" + getGraph(this, new ArrayList<String>()) + "}";
    }

    private static StringBuilder getGraph(InstructionNode root, List<String> visitedEdges) {
        StringBuilder result = new StringBuilder();

        for (InstructionNode child : root.getChildren()) {
            StringBuilder edge = new StringBuilder();
            edge.append("\t\"").append(StringEscapeUtils.escapeJava(root.toString())).append("\n")
                            .append(root.getContext().toString()).append("\" -> \"");
            edge.append(StringEscapeUtils.escapeJava(child.toString())).append("\n")
                            .append(child.getContext().toString()).append("\"\n");

            if (visitedEdges.contains(edge.toString())) {
                continue;
            }

            visitedEdges.add(edge.toString());

            result.append(edge);
            result.append(getGraph(child, visitedEdges));
        }

        return result;
    }

}
