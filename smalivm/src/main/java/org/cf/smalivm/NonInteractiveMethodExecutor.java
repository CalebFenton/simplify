package org.cf.smalivm;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionGraphImpl;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.exception.*;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class NonInteractiveMethodExecutor extends MethodExecutor {

    private static Logger log = LoggerFactory.getLogger(NonInteractiveMethodExecutor.class.getSimpleName());

    private final int maxCallDepth;
    private final int maxAddressVisits;
    private final int maxMethodVisits;
    private final int maxExecutionTime;
    private int totalVisits;

    NonInteractiveMethodExecutor(ClassManager classManager, ExecutionGraphImpl graph, int maxCallDepth, int maxAddressVisits, int maxMethodVisits, int maxExecutionTime) {
        super(classManager, graph);
        this.maxCallDepth = maxCallDepth;
        this.maxAddressVisits = maxAddressVisits;
        this.maxMethodVisits = maxMethodVisits;
        this.maxExecutionTime = maxExecutionTime;
        totalVisits = 0;
    }

    public ExecutionGraph execute() throws VirtualMachineException {
        ExecutionNode rootNode = getExecutionGraph().getRoot();
        VirtualMethod method = getExecutionGraph().getMethod();
        int callDepth = rootNode.getCallDepth();
        log.info("Executing {}, depth={}", method, callDepth);

        boolean isRootMethod = callDepth == 0;
        if (isRootMethod) {
            resetTotalVisits();
        } else if (callDepth > getMaxCallDepth()) {
            throw new MaxCallDepthExceededException(method.getSignature());
        }

        TIntIntMap addressToVisitCount = new TIntIntHashMap();
        long endTime = System.currentTimeMillis() + (maxExecutionTime * 1000);
        boolean warnedMultipleExecutionPaths = false;
        while (!isFinished()) {
            totalVisits += 1;
            checkMaxVisits(rootNode, method, addressToVisitCount);

            ExecutionNode node = step();

            if (!warnedMultipleExecutionPaths && node.getChildren().size() > 1) {
                warnedMultipleExecutionPaths = true;
                String children = node.getChildren()
                        .stream()
                        .map(ExecutionNode::toString)
                        .collect(Collectors.joining(", "));
                // Multiple execution paths often leads to more unknowns and execution ambiguity so draw some attention to it.
                log.debug("{} has multiple execution paths starting at {}: {}", method, node, children);
            }

            checkMaxExecutionTime(endTime, method);
        }

        return getExecutionGraph();
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
