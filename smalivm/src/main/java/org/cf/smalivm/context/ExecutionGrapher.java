package org.cf.smalivm.context;

import java.util.ArrayList;
import java.util.List;

public class ExecutionGrapher {

    private final static String DOT = "[^a-zA-Z\200-\377_0-9\\s\\p{Punct}]";

    public static String graph(ExecutionGraphImpl graph) {
        ExecutionNode node = graph.getRoot();
        StringBuilder sb = new StringBuilder("digraph {\n");
        getGraph(node, graph, sb, new ArrayList<>());
        sb.append("labelloc=\"t\"\n");
        sb.append("label=\"").append(graph.getMethod()).append("\";\n");
        sb.append('}');

        return sb.toString();
    }

    private static void getGraph(ExecutionNode node, ExecutionGraphImpl graph, StringBuilder sb,
                                 List<ExecutionNode> visitedNodes) {
        if (visitedNodes.contains(node)) {
            return;
        }
        visitedNodes.add(node);

        StringBuilder nodeState = getNodeState(node, graph);
        for (ExecutionNode child : node.getChildren()) {
            sb.append(nodeState).append(" -> ").append(getNodeState(child, graph)).append('\n');

            getGraph(child, graph, sb, visitedNodes);
        }
    }

    private static StringBuilder getNodeState(ExecutionNode node, ExecutionGraphImpl graph) {
        MethodState state = node.getContext().getMethodState();
        String op = node.toString().replaceAll(DOT, "?").replace("\"", "\\\"");
        String stateStr = state.toString().replaceAll(DOT, "?").replace("\"", "\\\"").trim();
        int nodeIndex = graph.getNodeIndex(node);

        StringBuilder sb = new StringBuilder();
        sb.append("\"@").append(node.getAddress()).append('.').append(nodeIndex).append(" :: ");
        sb.append(op).append('\n').append(stateStr).append('"');

        return sb;
    }
}
