package org.cf.smalivm;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.opcode.Op;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;
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
        List<? extends TryBlock<? extends ExceptionHandler>> tryBlocks = vm.getClassManager().getTryCatchList(
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
            // This is a start or root entry point.
            resetTotalVisits();
        }

        Deque<ExecutionNode> executeStack = new ArrayDeque<ExecutionNode>();
        executeStack.push(currentNode);
        while ((currentNode = executeStack.poll()) != null) {
            totalVisits += 1;
            checkMaxVisits(currentNode, methodDescriptor, addressToVisitCount);

            int[] childAddresses = new int[0];
            try {
                childAddresses = currentNode.execute();
            } catch (Exception ex) {
                if (log.isWarnEnabled()) {
                    log.warn(currentNode + " generated an exception:", ex);
                }
                childAddresses = currentNode.getOp().getPossibleChildren();
                int[] catchAddresses = getCatchAddresses(ex, currentNode.getAddress(), tryBlocks);
                addChildrenToGraph(graph, catchAddresses, currentNode);
            }
            addChildrenToGraph(graph, childAddresses, currentNode);

            executeStack.addAll(currentNode.getChildren());
        }

        return graph;
    }

    private static void addChildrenToGraph(ExecutionGraph graph, int[] childAddresses, ExecutionNode parentNode) {
        // Each visit adds a new ExecutionNode to the pile. These piles can be inspected for register or field
        // consensus, or other optimizations.
        for (int address : childAddresses) {
            Op childOp = graph.getTemplateNode(address).getOp();
            ExecutionNode childNode = parentNode.getChild(childOp);
            graph.addNode(childNode);
        }
    }

    private int[] getCatchAddresses(Exception exception, int address,
                    List<? extends TryBlock<? extends ExceptionHandler>> tryBlocks) {
        String exceptionType = exception.getClass().getName();
        exceptionType = SmaliClassUtils.javaClassToSmali(exceptionType);
        TIntList addresses = new TIntLinkedList();
        for (TryBlock<? extends ExceptionHandler> tryBlock : tryBlocks) {
            if ((address < tryBlock.getStartCodeAddress())
                            || (address > (tryBlock.getStartCodeAddress() + tryBlock.getCodeUnitCount()))) {
                // address is not inside of this try/catch
                continue;
            }

            for (ExceptionHandler handler : tryBlock.getExceptionHandlers()) {
                String handlerType = handler.getExceptionType();
                if (!exceptionType.equals(handlerType)) {
                    continue;
                }

                int handlerAddress = handler.getHandlerCodeAddress();
                addresses.add(handlerAddress);
            }
        }

        return addresses.toArray();
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
