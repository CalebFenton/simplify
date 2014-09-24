package org.cf.smalivm;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayDeque;
import java.util.Deque;

import org.cf.smalivm.context.ClassContextMap;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.context.ContextNode;
import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxNodeVisitsExceeded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodExecutor {

    private static Logger log = LoggerFactory.getLogger(MethodExecutor.class.getSimpleName());

    private final VirtualMachine vm;

    MethodExecutor(VirtualMachine vm) {
        this.vm = vm;
    }

    ContextGraph execute(String methodDescriptor, MethodContext mctx, ClassContextMap classContextMap)
                    throws MaxNodeVisitsExceeded, MaxCallDepthExceeded {
        ContextGraph graph = vm.getInstructionGraphClone(methodDescriptor);
        ContextNode rootNode = graph.getRootNode();
        rootNode.setMethodContext(mctx);
        rootNode.setClassContextMap(classContextMap);

        return execute(graph);
    }

    private ContextGraph execute(ContextGraph graph) throws MaxNodeVisitsExceeded, MaxCallDepthExceeded {
        // TODO: move call depth stuff to ContextNode
        String methodDescriptor = graph.getMethodDescriptor();
        ContextNode node = graph.getRootNode();
        MethodContext mctx = node.getMethodContext();
        log.info("Executing " + methodDescriptor + ", depth=" + mctx.getCallDepth());
        if (mctx.getCallDepth() > vm.getMaxCallDepth()) {
            throw new MaxCallDepthExceeded("Exceeded max call depth in " + methodDescriptor);
        }

        TIntIntMap addressToNodeVisitCounts = new TIntIntHashMap(graph.getNodeCount());
        Deque<ContextNode> executeStack = new ArrayDeque<ContextNode>();
        ContextNode rootNode = graph.getRootNode();
        if (mctx != null) {
            // If called with a context, invoked from local method.
            rootNode.setMethodContext(mctx);
        }
        executeStack.push(rootNode);

        do {
            ContextNode currentNode = executeStack.poll();
            int visitCount = addressToNodeVisitCounts.get(node.getAddress());
            if (visitCount > vm.getMaxNodeVisits()) {
                throw new MaxNodeVisitsExceeded("Exceeded max node visits for " + node + " in " + methodDescriptor);
            }

            int[] childAddresses = currentNode.execute();
            for (int address : childAddresses) {
                // Every node visit means a new clone on the pile. Piles can be examined by the optimizer for
                // stuff like consensus of register values.
                ContextNode child = new ContextNode(graph.getTemplateNode(address));
                child.setMethodContext(new MethodContext(currentNode.getMethodContext()));
                currentNode.addChild(child);
                graph.addNode(address, child);
            }

            executeStack.addAll(currentNode.getChildren());
        } while (executeStack.peek() != null);

        return graph;
    }

}
