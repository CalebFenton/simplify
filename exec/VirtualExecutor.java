package simplify.exec;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jf.dexlib2.writer.builder.BuilderMethod;

import simplify.Simplifier;
import simplify.graph.CallGraphBuilder;
import simplify.graph.Node;

public class VirtualExecutor {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private final List<BuilderMethod> methods;
    private final int maxNodeVisits;
    private final int maxCallDepth;

    private Map<BuilderMethod, Node> pristineCallGraphs;

    private class NodeWithContext {
        public final ExecutionContext ectx;
        public final Node node;

        NodeWithContext(ExecutionContext ectx, Node node) {
            this.ectx = ectx;
            this.node = node;
        }
    }

    public VirtualExecutor(List<BuilderMethod> methods, int maxNodeVisits, int maxCallDepth) {
        this.methods = methods;
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;
    }

    public void execute() {
        for (BuilderMethod method : methods) {
            log.fine("Executing method: " + method.getName());

            ExecutionContext ectx = new ExecutionContext();
            Node rootNode = getCallGraph(method);
            try {
                executeMethod(ectx, method);
            } catch (MaxNodeVisitsExceeded e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void executeMethod(ExecutionContext ectx, Node rootNode) throws MaxNodeVisitsExceeded {
        Map<Node, Integer> visitCounts = new HashMap<Node, Integer>();
        Map<Node, ExecutionContext> partialContexts = new HashMap<Node, ExecutionContext>();
        Deque<NodeWithContext> startNodes = new ArrayDeque<NodeWithContext>();
        startNodes.push(new NodeWithContext(ectx, rootNode));

        NodeWithContext nctx;
        outer: while ((nctx = startNodes.poll()) != null) {
            Node currentNode = nctx.node;
            log.fine("Visiting: " + currentNode);

            Integer visitCount = visitCounts.get(currentNode);
            visitCount = visitCount == null ? 1 : visitCount + 1;
            visitCounts.put(currentNode, visitCount);

            if (visitCount >= maxNodeVisits) {
                throw new MaxNodeVisitsExceeded("Node: " + currentNode);
            }

            ExecutionContext pectx = partialContexts.get(currentNode);
            if (pectx != null) {
                // Execution halted on this node previously with a partial context.
                // Merge the two so going forward we have all possibilities.
                ectx = ExecutionContext.merge(ectx, pectx);
                partialContexts.remove(pectx);
            }

            // If all parents have not been visited, don't continue.
            // We're not sure of every possibility a register may have going forward.
            for (Node parent : currentNode.getParents()) {
                if (visitCounts.get(parent) == null) {
                    log.fine("\tHave not visitied parent: " + parent);

                    // Edge case for circular loops.
                    if (currentNode.getChildren().contains(parent)) {
                        // Ignore restriction on visiting nodes with unvisited parents.
                        break;
                    }

                    continue outer;
                }
            }

            List<Node> children = executeNode(ectx, currentNode);

            for (Node child : children) {
                startNodes.push(new NodeWithContext(ectx.clone(), child));
            }
        }
    }

    private List<Node> executeNode(ExecutionContext ectx, Node node) {
        return node.getChildren();
    }

    private void visitInvalidBranch(Node node) {
        // Mark each node in this invalid branch as visited until we get to node with unvisited parentii
    }

    private Node getCallGraph(BuilderMethod method) {
        Node rootNode = pristineCallGraphs.get(method);

        if (rootNode == null) {
            rootNode = CallGraphBuilder.build(method);
            pristineCallGraphs.put(method, rootNode);
        }

        return rootNode.clone();
    }
}
