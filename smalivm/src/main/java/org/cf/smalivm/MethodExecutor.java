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
import org.cf.smalivm.opcode.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodExecutor {

    private static Logger log = LoggerFactory.getLogger(MethodExecutor.class.getSimpleName());

    private final VirtualMachine vm;
    private int totalVisits;

    MethodExecutor(VirtualMachine vm) {
        this.vm = vm;
        totalVisits = 0;
    }

    private void resetTotalVisits() {
        totalVisits = 0;
    }

    ExecutionGraph execute(ExecutionGraph graph) throws MaxAddressVisitsExceeded, MaxCallDepthExceeded,
                    MaxMethodVisitsExceeded {
        TIntIntMap addressToVisitCount = new TIntIntHashMap();
        String methodDescriptor = graph.getMethodDescriptor();
        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(vm.getClassManager(),
                        methodDescriptor);

        ExecutionNode currentNode = graph.getRoot();
        int callDepth = currentNode.getCallDepth();
        if (log.isInfoEnabled()) {
            log.info("Executing " + methodDescriptor + ", depth=" + callDepth);
        }
        if (currentNode.getCallDepth() > vm.getMaxCallDepth()) {
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
                    log.warn(currentNode + " generated a real exception:", ex);
                }
                int childAddress = exceptionResolver.resolve(ex, currentNode.getAddress());
                spawnChild(graph, currentNode, childAddress);
            }

            spawnChildren(graph, currentNode, currentNode.getOp().getChildren());

            // TODO: this is "proper" virtual exception handling, compared to above
            Op op = currentNode.getOp();
            if (op.mayThrowException()) {
                for (VirtualException exception : op.getExceptions()) {
                    if (log.isWarnEnabled()) {
                        log.warn("{} may throw virtual exception: {}", currentNode, exception);
                    }

                    int childAddress = exceptionResolver.resolve(exception, currentNode.getAddress());
                    if (childAddress == -1) {
                        if (op.getChildren().length == 0) {
                            if (log.isErrorEnabled()) {
                                log.error("{} unhandled virtual exception: {}", currentNode, exception);
                            }

                            // TODO: should set in the graph that this method threw an exception
                            // and that should get bubbled up to the calling graph
                            return null;
                        } else {
                            if (log.isTraceEnabled()) {
                                log.trace("{} possible unhandled virtual exception: {}", currentNode, exception);
                            }
                        }
                    } else {
                        spawnChild(graph, currentNode, childAddress);
                    }
                }
            }

            executeStack.addAll(currentNode.getChildren());
        }

        return graph;
    }

    private static void spawnChild(ExecutionGraph graph, ExecutionNode parentNode, int childAddress) {
        spawnChildren(graph, parentNode, new int[] { childAddress });
    }

    private static void spawnChildren(ExecutionGraph graph, ExecutionNode parentNode, int[] childAddresses) {
        // Each visit adds a new ExecutionNode to the pile. These piles can be inspected for register or field
        // consensus, or other optimizations.
        for (int address : childAddresses) {
            Op childOp = graph.getTemplateNode(address).getOp();
            ExecutionNode childNode = parentNode.spawnChild(childOp);
            graph.addNode(childNode);
        }
    }

    private void checkMaxVisits(ExecutionNode node, String methodDescriptor, TIntIntMap addressToVisitCount)
                    throws MaxAddressVisitsExceeded, MaxMethodVisitsExceeded {
        if (totalVisits > vm.getMaxMethodVisits()) {
            throw new MaxMethodVisitsExceeded(node, methodDescriptor);
        }

        int address = node.getAddress();
        int visitCount = addressToVisitCount.get(address);
        if (visitCount > vm.getMaxAddressVisits()) {
            throw new MaxAddressVisitsExceeded(node, methodDescriptor);
        }
        boolean adjusted = addressToVisitCount.adjustValue(address, 1);
        if (!adjusted) {
            addressToVisitCount.put(address, 1);
        }
    }

}
