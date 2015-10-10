package org.cf.smalivm.context;

import java.util.ArrayList;
import java.util.List;

public class ExecutionGraphPrinter {

    private final static String DOT = "[^a-zA-Z\200-\377_0-9\\s\\p{Punct}]";

    public static String print(ExecutionGraph graph) {
        ExecutionNode node = graph.getRoot();
        StringBuilder sb = new StringBuilder("digraph {\n");
        getGraph(node, sb, new ArrayList<ExecutionNode>());
        sb.append("}");

        return sb.toString();
    }

    private static void getGraph(ExecutionNode node, StringBuilder sb, List<ExecutionNode> visitedNodes) {
        if (visitedNodes.contains(node)) {
            return;
        }
        visitedNodes.add(node);

        StringBuilder nodeState = getNodeState(node);
        for (ExecutionNode child : node.getChildren()) {
            sb.append(nodeState).append(" -> ").append(getNodeState(child)).append('\n');

            getGraph(child, sb, visitedNodes);
        }
    }

    private static StringBuilder getNodeState(ExecutionNode node) {
        StringBuilder sb = new StringBuilder();
        MethodState state = node.getContext().getMethodState();
        String opString = node.toString().replaceAll(DOT, "?").replace("\"", "\\\"");
        String stateString = state.toString().replaceAll(DOT, "?").replace("\"", "\\\"").trim();
        sb.append("\"@").append(node.getAddress()).append(" - ").append(opString).append('\n').append(stateString)
                        .append('"');

        return sb;
    }
}
