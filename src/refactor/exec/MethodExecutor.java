package refactor.exec;

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

        Deque<ContextNode> executeStack = new ArrayDeque<ContextNode>();
        executeStack.push(graph.getRootNode());

        do {
            ContextNode currentNode = executeStack.poll();
            log.fine("Handling " + currentNode);

            int[] childAddresses = currentNode.execute();

            addChildNodes(executeStack, graph, childAddresses, currentNode.getIndex());
        } while (executeStack.peek() != null);

        return graph;
    }

    private static void addChildNodes(Deque<ContextNode> executeStack, ContextGraph graph, int[] childAddresses,
                    int currentIndex) {
        for (int address : childAddresses) {
            ContextNode childNode;
            if (address == VirtualMachine.ContinueNextInstruction) {
                int index = currentIndex + 1;
                childNode = graph.getTemplateNodeByIndex(index);
            } else {
                childNode = graph.getTemplateNodeByAddress(address);
            }

            executeStack.push(childNode);
        }
    }
}
