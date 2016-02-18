package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.smali.ClassManager;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeExecutor {

    private static Logger log = LoggerFactory.getLogger(NodeExecutor.class.getSimpleName());

    private final ExecutionGraph graph;
    private final ExceptionHandlerAddressResolver exceptionResolver;

    public NodeExecutor(ExecutionGraph graph, ClassManager classManager) {
        this.graph = graph;
        String methodDescriptor = graph.getMethodSignature();
        exceptionResolver = new ExceptionHandlerAddressResolver(classManager, methodDescriptor);
    }

    public void execute(ExecutionNode node) throws UnhandledVirtualException {
        if (log.isDebugEnabled()) {
            Op op = node.getOp();
            log.debug("Handling @{}: {}\nContext before:\n{}", op.getAddress(), op, node.getContext());
        }

        try {
            node.execute();
        } catch (Exception e) {
            // These exceptions are likely from simplify and not real.
            // TODO: this exception handler should be re-examined when ops set exceptions properly
            if (log.isWarnEnabled()) {
                log.warn("{} threw a real exception:", node, e);
            }
            int childAddress = exceptionResolver.resolve(e, node.getAddress());
            spawnChild(graph, node, childAddress);
        }

        spawnChildren(graph, node);
        spawnExceptionChildren(graph, node, exceptionResolver);

        if (log.isDebugEnabled()) {
            log.debug("Context after:\n{}", node.getContext());
        }
    }

    private static void spawnChild(ExecutionGraph graph, ExecutionNode parentNode, int childAddress) {
        Op childOp = graph.getTemplateNode(childAddress).getOp();
        ExecutionNode childNode = parentNode.spawnChild(childOp);
        graph.addNode(childNode);
    }

    private static void spawnChildren(ExecutionGraph graph, ExecutionNode parentNode) {
        // Each visit adds a new ExecutionNode to the pile. These piles can be inspected for register or field
        // consensus, or other optimizations.
        for (MethodLocation childLocation : parentNode.getChildLocations()) {
            spawnChild(graph, parentNode, childLocation.getCodeAddress());
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
                    if (node.getChildLocations().length == 0) {
                        if (log.isErrorEnabled()) {
                            // No children, probably a real exception
                            log.error("{} unhandled virtual exception: {}", node, exception);
                        }

                        throw new UnhandledVirtualException(exception);
                    } else {
                        // Op has children, doesn't *always* throw, probably virtual exception
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

}
