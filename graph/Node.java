package simplify.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jf.dexlib2.builder.BuilderInstruction;

public class Node {

    private final List<Node> parents;
    private final List<Node> children;

    private BuilderInstruction instruction;
    private int visits;

    public Node(BuilderInstruction instruction) {
        parents = new ArrayList<Node>();
        children = new ArrayList<Node>();

        visits = 0;

        this.instruction = instruction;
    }

    public void addChild(Node child) {
        children.add(child);
        child.addParent(this);
    }

    private void addParent(Node parent) {
        parents.add(parent);
    }

    public List<Node> getParents() {
        return parents;
    }

    public List<Node> getChildren() {
        return children;
    }

    public boolean wasVisited() {
        return visited;
    }

    @Override
    public String toString() {
        return instruction.getOpcode().name + " @" + instruction.getLocation().getIndex();
    }

    public String toGraph() {
        // For GraphViz
        return "digraph {\n" + getGraph(this, new ArrayList<String>()) + "}";
    }

    private static StringBuilder getGraph(Node root, List<String> visitedEdges) {
        StringBuilder result = new StringBuilder();

        for (Node child : root.getChildren()) {
            StringBuilder edge = new StringBuilder();
            edge.append("\t\"").append(StringEscapeUtils.escapeJava(root.toString())).append("\" -> \"");
            edge.append(StringEscapeUtils.escapeJava(child.toString())).append("\"\n");

            if (visitedEdges.contains(edge.toString())) {
                continue;
            }

            visitedEdges.add(edge.toString());

            result.append(edge);
            result.append(getGraph(child, visitedEdges));
        }

        return result;
    }

    @Override
    public Node clone() {
        return cloneOf(this);
    }

    public static Node cloneOf(Node rootNode) {
        Node clone = new Node(rootNode.instruction);

        for (Node child : rootNode.getChildren()) {
            clone.addChild(cloneOf(child));
        }

        return clone;
    }

}
