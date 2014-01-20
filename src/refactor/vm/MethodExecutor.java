package refactor.vm;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

import simplify.Main;
import simplify.exec.MaxNodeVisitsExceeded;

public class MethodExecutor {

    private static Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final VirtualMachine vm;

    MethodExecutor(VirtualMachine vm) {
        this.vm = vm;
    }

    ContextGraph execute(String methodDescriptor) throws MaxNodeVisitsExceeded {
        log.info("Executing " + methodDescriptor);

        ContextGraph graph = vm.getInstructionGraph(methodDescriptor);
        TIntIntMap indexToNodeVisitCounts = new TIntIntHashMap(graph.getNodeCount());
        Deque<ContextNode> executeStack = new ArrayDeque<ContextNode>();
        executeStack.push(graph.getRootNode());

        do {
            ContextNode currentNode = executeStack.poll();
            log.fine("Handling " + currentNode);

            recordNodeVisitation(indexToNodeVisitCounts, currentNode, vm.getMaxNodeVisits());

            int[] childAddresses = currentNode.execute();

            for (int address : childAddresses) {
                // Every node visit means a new clone on the pile. This way, piles can be examined by the optimizer for
                // stuff like consensus of certain register values.
                ContextNode childNode = new ContextNode(graph.getTemplateNode(address));
                childNode.setContext(new MethodContext(currentNode.getContext()));

                executeStack.push(childNode);
            }
        } while (executeStack.peek() != null);

        return graph;
    }

    private static void recordNodeVisitation(TIntIntMap addressToNodeVisitCount, ContextNode node, int maxNodeVisits)
                    throws MaxNodeVisitsExceeded {
        int address = node.getAddress();
        int visitCount = addressToNodeVisitCount.get(address);

        if (visitCount > maxNodeVisits) {
            throw new MaxNodeVisitsExceeded(node.toString());
        }

        addressToNodeVisitCount.put(address, visitCount + 1);
    }

}
