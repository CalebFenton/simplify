package simplify.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

public class Node {

    private List<Node> parents;
    private List<Node> children;

    private String instruction;
    private boolean visited;

    public Node(String instruction) {
        this.instruction = instruction;
        visited = false;
        parents = new ArrayList<Node>();
        children = new ArrayList<Node>();
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

    public String getLine() {
        return instruction;
    }

    public boolean wasVisited() {
        return visited;
    }

    @Override
    public String toString() {
        return instruction;
    }

    public String toGraph() {
        return getGraph(this, new ArrayList<String>()).toString();
    }

    private static StringBuilder getGraph(Node root, List<String> visitedEdges) {
        StringBuilder result = new StringBuilder();
        for (Node child : root.getChildren()) {
            StringBuilder edge = new StringBuilder();
            edge.append("\"").append(StringEscapeUtils.escapeJava(root.toString())).append("\" -> \"");
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
}
