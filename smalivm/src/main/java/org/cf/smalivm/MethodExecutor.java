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
import org.cf.smalivm.opcode.Op;
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

    private void resetTotalVisits() {
        totalVisits = 0;
    }

    private int getMaxCallDepth() {
        return maxCallDepth;
    }

    ExecutionGraph execute(ExecutionGraph graph) throws MaxAddressVisitsExceeded, MaxCallDepthExceeded,
                    MaxMethodVisitsExceeded, UnhandledVirtualException {
        TIntIntMap addressToVisitCount = new TIntIntHashMap();
        String methodDescriptor = graph.getMethodDescriptor();
        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager,
                        methodDescriptor);

        ExecutionNode currentNode = graph.getRoot();
        int callDepth = currentNode.getCallDepth();
        if (log.isInfoEnabled()) {
            log.info("Executing {}, depth={}", methodDescriptor, callDepth);
        }
        if (currentNode.getCallDepth() > getMaxCallDepth()) {
            throw new MaxCallDepthExceeded(methodDescriptor);
        }

        if (callDepth == 0) {
            // This is a new root method.
            resetTotalVisits();
        }

        Deque<ExecutionNode> executeStack = new ArrayDeque<ExecutionNode>();
        executeStack.push(currentNode);
        while ((currentNode = executeStack.poll()) != null) {
            totalVisits += 1;
            checkMaxVisits(currentNode, methodDescriptor, addressToVisitCount);

            try {
                currentNode.execute();
            } catch (Exception ex) {
                // TODO: this exception handler should be REMOVED when ops set exceptions properly
                // These exceptions could be from bugs in simplify, not real exceptions
                if (log.isWarnEnabled()) {
                    log.warn("{} generated a real exception: {}", currentNode, ex);
                }
                int childAddress = exceptionResolver.resolve(ex, currentNode.getAddress());
                spawnChild(graph, currentNode, childAddress);
            }

            spawnChildren(graph, currentNode);
            spawnExceptionChildren(graph, currentNode, exceptionResolver);

            executeStack.addAll(currentNode.getChildren());
        }

        return graph;
    }

    private static void spawnChild(ExecutionGraph graph, ExecutionNode parentNode, int childAddress) {
        Op childOp = graph.getTemplateNode(childAddress).getOp();
        ExecutionNode childNode = parentNode.spawnChild(childOp);
        graph.addNode(childNode);
    }

    private static void spawnChildren(ExecutionGraph graph, ExecutionNode parentNode) {
        // Each visit adds a new ExecutionNode to the pile. These piles can be inspected for register or field
        // consensus, or other optimizations.
        for (int childAddress : parentNode.getChildAddresses()) {
            spawnChild(graph, parentNode, childAddress);
        }
    }

    private static void spawnExceptionChildren(ExecutionGraph graph, ExecutionNode node,
                    ExceptionHandlerAddressResolver exceptionResolver) throws UnhandledVirtualException {
        if (node.mayThrowException()) {
            for (VirtualException exception : node.getExceptions()) {
                if (log.isTraceEnabled()) {
                    log.trace("{} may throw virtual exception: {}", node, exception);
                }

                int childAddress = exceptionResolver.resolve(exception, node.getAddress());
                if (childAddress == -1) {
                    if (node.getChildAddresses().length == 0) {
                        if (log.isErrorEnabled()) {
                            log.error("{} unhandled virtual exception: {}", node, exception);
                        }

                        throw new UnhandledVirtualException(exception);
                    } else {
                        /*
                         * Since there are children, it means the op *may* have an exception. If it's unhandled, assume
                         * there is no exception. In many cases, the verifier catches this stuff.
                         */
                        if (log.isTraceEnabled()) {
                            log.trace("{} possible unhandled virtual exception: {}", node, exception);
                        }
                    }
                } else {
                    spawnChild(graph, node, childAddress);
                }
            }
        }
    }

    private int getMaxMethodVisits() {
        return maxMethodVisits;
    }

    private int getMaxAddressVisits() {
        return maxAddressVisits;
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

}
