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
            log.info("Handling " + currentNode);

            int[] childOffsets = currentNode.execute();
            for (int offset : childOffsets) {
                // ContextNode childNode = graph.g
            }
        } while (executeStack.peek() != null);

        // for every method that's called, check if that class has been static init'ed
        // for every static field ref, also static init' the class (sget / sput need vm)

        return graph;
    }

}
