package org.cf.smalivm;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class MethodExecutor {

    private static Logger log = LoggerFactory.getLogger(MethodExecutor.class.getSimpleName());

    private final ClassManager classManager;
    private final int maxAddressVisits;
    private final int maxCallDepth;
    private final int maxExecutionTime;
    private final int maxMethodVisits;
    private int totalVisits;

    MethodExecutor(ClassManager classManager, int maxCallDepth, int maxAddressVisits, int maxMethodVisits, int maxExecutionTime) {
        this.classManager = classManager;
        this.maxCallDepth = maxCallDepth;
        this.maxAddressVisits = maxAddressVisits;
        this.maxMethodVisits = maxMethodVisits;
        this.maxExecutionTime = maxExecutionTime;
        totalVisits = 0;
    }

    ExecutionGraph execute(ExecutionGraph graph) throws VirtualMachineException {
        TIntIntMap addressToVisitCount = new TIntIntHashMap();
        VirtualMethod method = graph.getMethod();
        ExecutionNode node = graph.getRoot();
        int callDepth = node.getCallDepth();
        if (node.getCallDepth() > getMaxCallDepth()) {
            throw new MaxCallDepthExceededException(method.getSignature());
        }

        if (callDepth == 0) {
            // This is a new root method.
            resetTotalVisits();
        }

        log.info("Executing {}, depth={}", method, callDepth);

        NodeExecutor nodeExecutor = new NodeExecutor(graph, classManager);
        Deque<ExecutionNode> stack = new ArrayDeque<>();
        stack.push(node);
        long endTime = System.currentTimeMillis() + (maxExecutionTime * 1000);
        boolean warnedMultipleExecutionPaths = false;
        while ((node = stack.poll()) != null) {
            totalVisits += 1;
            checkMaxVisits(node, method, addressToVisitCount);

            nodeExecutor.execute(node);

            if (node.getChildren().size() > 1 && !warnedMultipleExecutionPaths) {
                warnedMultipleExecutionPaths = true;
                String children = node.getChildren()
                        .stream()
                        .map(ExecutionNode::toString)
                        .collect(Collectors.joining(", "));
                // This can lead to more ambiguity and it's not always obvious when this happens.
                // Let the user know if they're listening.
                log.debug("{} has multiple execution paths starting at {}: {}", method, node, children);
            }

            stack.addAll(node.getChildren());
            checkMaxExecutionTime(endTime, method);
        }

        return graph;
    }

    private void checkMaxExecutionTime(long endTime, VirtualMethod localMethod) throws MaxExecutionTimeExceededException {
        if (maxExecutionTime == 0) {
            return;
        }

        if (System.currentTimeMillis() >= endTime) {
            throw new MaxExecutionTimeExceededException(localMethod.getSignature());
        }
    }

    private void checkMaxVisits(ExecutionNode node, VirtualMethod localMethod, TIntIntMap addressToVisitCount) throws MaxAddressVisitsExceededException, MaxMethodVisitsExceededException {
        if (totalVisits > getMaxMethodVisits()) {
            throw new MaxMethodVisitsExceededException(node, localMethod.getSignature());
        }

        int address = node.getAddress();
        int visitCount = addressToVisitCount.get(address);
        if (visitCount > getMaxAddressVisits()) {
            throw new MaxAddressVisitsExceededException(node, localMethod.getSignature());
        }
        boolean adjusted = addressToVisitCount.adjustValue(address, 1);
        if (!adjusted) {
            addressToVisitCount.put(address, 1);
        }
    }

    private int getMaxAddressVisits() {
        return maxAddressVisits;
    }

    private int getMaxCallDepth() {
        return maxCallDepth;
    }

    private int getMaxMethodVisits() {
        return maxMethodVisits;
    }

    private void resetTotalVisits() {
        totalVisits = 0;
    }

}
