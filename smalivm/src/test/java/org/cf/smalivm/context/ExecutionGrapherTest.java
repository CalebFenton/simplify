package org.cf.smalivm.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.jupiter.api.Test;

public class ExecutionGrapherTest {

    private static final int CHILD_ADDRESS = 2;

    private static final String CHILD_NODE_STR = "child node";
    private static final String CHILD_STATE_STR = "child state";
    private static final String METHOD_SIGNATURE = "Lclass;->method()V";

    private static final int ROOT_ADDRESS = 1;
    private static final String ROOT_NODE_STR = "root node";
    private static final String ROOT_STATE_STR = "root state";

    private static ExecutionNode buildNode(int address, String nodeString, String stateString) {
        ExecutionNode node = mock(ExecutionNode.class);
        when(node.getAddress()).thenReturn(address);
        when(node.toString()).thenReturn(nodeString);

        MethodState state = mock(MethodState.class);
        when(state.toString()).thenReturn(stateString);
        ExecutionContext context = mock(ExecutionContext.class);
        when(context.getMethodState()).thenReturn(state);

        when(node.getContext()).thenReturn(context);

        return node;
    }

    @Test
    public void testHasExpectedGraph() {
        ExecutionNode child = buildNode(CHILD_ADDRESS, CHILD_NODE_STR, CHILD_STATE_STR);
        when(child.getChildren()).thenReturn(new LinkedList<ExecutionNode>());

        ExecutionNode root = buildNode(ROOT_ADDRESS, ROOT_NODE_STR, ROOT_STATE_STR);
        List<ExecutionNode> children = new LinkedList<ExecutionNode>();
        children.add(child);
        when(root.getChildren()).thenReturn(children);

        VirtualMethod localMethod = mock(VirtualMethod.class);
        when(localMethod.toString()).thenReturn(METHOD_SIGNATURE);

        ExecutionGraphImpl graph = mock(ExecutionGraphImpl.class);
        when(graph.getRoot()).thenReturn(root);
        when(graph.getMethod()).thenReturn(localMethod);
        when(graph.getNodeIndex(root)).thenReturn(0);
        when(graph.getNodeIndex(child)).thenReturn(0);
        String digraph = ExecutionGrapher.graph(graph);

        StringBuilder sb = new StringBuilder("digraph {\n");
        sb.append("\"@").append(ROOT_ADDRESS).append(".0 :: ").append(ROOT_NODE_STR).append('\n');
        sb.append(ROOT_STATE_STR).append("\" -> ");
        sb.append("\"@").append(CHILD_ADDRESS).append(".0 :: ").append(CHILD_NODE_STR).append('\n');
        sb.append(CHILD_STATE_STR).append("\"\n");
        sb.append("labelloc=\"t\"\n");
        sb.append("label=\"").append(METHOD_SIGNATURE).append("\";");
        sb.append("\n}");

        String expected = sb.toString();
        assertEquals(expected, digraph);
    }

}
