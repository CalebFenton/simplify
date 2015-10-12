package org.cf.smalivm;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayDeque;
import java.util.Deque;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodExecutor {

    private static Logger log = LoggerFactory.getLogger(MethodExecutor.class.getSimpleName());

    private final ClassManager classManager;
    private final int maxCallDepth;
    private final int maxAddressVisits;
    private final int maxMethodVisits;
    private int totalVisits;

    MethodExecutor(ClassManager classManager, int maxCallDepth, int maxAddressVisits, int maxMethodVisits) {
        this.classManager = classManager;
        this.maxCallDepth = maxCallDepth;
        this.maxAddressVisits = maxAddressVisits;
        this.maxMethodVisits = maxMethodVisits;
        totalVisits = 0;
    }

    private void checkMaxVisits(ExecutionNode node, String methodDescriptor, TIntIntMap addressToVisitCount)
                    throws MaxAddressVisitsExceeded, MaxMethodVisitsExceeded {
        if (totalVisits > getMaxMethodVisits()) {
            throw new MaxMethodVisitsExceeded(node, methodDescriptor);
        }

        int address = node.getAddress();
        int visitCount = addressToVisitCount.get(address);
        if (visitCount > getMaxAddressVisits()) {
            throw new MaxAddressVisitsExceeded(node, methodDescriptor);
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

    ExecutionGraph execute(ExecutionGraph graph) throws MaxAddressVisitsExceeded, MaxCallDepthExceeded,
                    MaxMethodVisitsExceeded, UnhandledVirtualException {
        TIntIntMap addressToVisitCount = new TIntIntHashMap();
        String methodDescriptor = graph.getMethodDescriptor();
        ExecutionNode node = graph.getRoot();
        int callDepth = node.getCallDepth();
        if (log.isInfoEnabled()) {
            log.info("Executing {}, depth={}", methodDescriptor, callDepth);
        }
        if (node.getCallDepth() > getMaxCallDepth()) {
            throw new MaxCallDepthExceeded(methodDescriptor);
        }

        if (callDepth == 0) {
            // This is a new root method.
            resetTotalVisits();
        }

        NodeExecutor nodeExecutor = new NodeExecutor(graph, classManager);
        Deque<ExecutionNode> executeStack = new ArrayDeque<ExecutionNode>();
        executeStack.push(node);
        while ((node = executeStack.poll()) != null) {
            totalVisits += 1;
            checkMaxVisits(node, methodDescriptor, addressToVisitCount);

            nodeExecutor.execute(node);

            executeStack.addAll(node.getChildren());
        }

        return graph;
    }

}
