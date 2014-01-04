package simplify.exec;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jf.dexlib2.writer.builder.BuilderMethod;

import simplify.Simplifier;
import simplify.graph.Node;

public class VirtualExecutor {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private final Map<BuilderMethod, Node> callGraphs;
    private final int maxNodeVisits;
    private final int maxCallDepth;

    private class NodeWithContext {
        public final ExecutionContext ectx;
        public final Node node;

        NodeWithContext(ExecutionContext ectx, Node node) {
            this.ectx = ectx;
            this.node = node;
        }
    }

    public VirtualExecutor(Map<BuilderMethod, Node> callGraphs, int maxNodeVisits, int maxCallDepth) {
        this.callGraphs = callGraphs;
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;
    }

    public void execute() {
        for (BuilderMethod method : callGraphs.keySet()) {
            log.fine("Executing method: " + method.getName());

            ExecutionContext ectx = new ExecutionContext();
            try {
                executeMethod(ectx, method);
            } catch (MaxNodeVisitsExceeded e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void executeMethod(ExecutionContext ectx, BuilderMethod method) throws MaxNodeVisitsExceeded {
        Map<Node, Integer> visitCounts = new HashMap<Node, Integer>();
        Map<Node, ExecutionContext> partialContexts = new HashMap<Node, ExecutionContext>();
        Deque<NodeWithContext> startNodes = new ArrayDeque<NodeWithContext>();
        startNodes.push(new NodeWithContext(ectx, callGraphs.get(method)));

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
            }

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

            executeNode(ectx, currentNode);

            // All parents visited -> All possible contexts computed.
            // Can now correctly execute visited shared children.s
            for (Node child : currentNode.getChildren()) {
                startNodes.push(new NodeWithContext(ectx.clone(), child));
            }
        }
    }

    private void executeNode(ExecutionContext ectx, Node node) {

    }

}
