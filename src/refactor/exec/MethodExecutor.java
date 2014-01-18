package refactor.exec;

import java.util.ArrayDeque;
import java.util.Deque;

public class MethodExecutor {

    private final VirtualMachine vm;

    MethodExecutor(VirtualMachine vm) {
        this.vm = vm;
    }

    ContextGraph execute(String methodDescriptor) {
        ContextGraph graph = vm.getInstructionGraph(methodDescriptor);

        Deque<ContextNode> executeStack = new ArrayDeque<ContextNode>();
        executeStack.push(graph.getRootNode());

        ContextNode currentNode;
        do {
            currentNode = executeStack.poll();

            int[] childOffsets = currentNode.execute();
            for (int offset : childOffsets) {
                // ContextNode childNode = graph.g
            }
        } while (currentNode != null);

        // for every method that's called, check if that class has been static init'ed
        // for every static field ref, also static init' the class (sget / sput need vm)

        return graph;
    }

}
