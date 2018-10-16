package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.ClassManager;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeExecutor {

    private static Logger log = LoggerFactory.getLogger(NodeExecutor.class.getSimpleName());

    private final ExecutionGraph graph;
    private final ExceptionHandlerAddressResolver exceptionResolver;

    public NodeExecutor(ExecutionGraph graph, ClassManager classManager) {
        this.graph = graph;
        exceptionResolver = new ExceptionHandlerAddressResolver(classManager, graph.getMethod());
    }

    private static ExecutionNode spawnChild(ExecutionGraph graph, ExecutionNode parentNode, int childAddress) {
        Op childOp = graph.getTemplateNode(childAddress).getOp();
        ExecutionNode childNode = parentNode.spawnChild(childOp);
        graph.addNode(childNode);

        return childNode;
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
            for (Throwable exception : node.getExceptions()) {
                if (log.isTraceEnabled()) {
                    log.trace("{} may throw virtual exception: ", node, exception);
                }

                int childAddress = exceptionResolver.resolve(exception, node.getAddress());
                if (childAddress >= 0) {
                    ExecutionNode childNode = spawnChild(graph, node, childAddress);
                    childNode.getContext().getMethodState().assignExceptionRegister(exception);
                } else {
                    if (node.getChildLocations().length == 0) {
                        if (log.isErrorEnabled()) {
                            // No children, probably a real exception
                            log.error("{} unhandled virtual exception: ", node, exception);
                        }

                        throw new UnhandledVirtualException(exception);
                    } else {
                        // Op has children, doesn't *always* throw, probably virtual exception
                        if (log.isTraceEnabled()) {
                            log.trace("{} possible unhandled virtual exception: ", node, exception);
                        }
                    }
                }
            }
        }
    }

    public void execute(ExecutionNode node) throws UnhandledVirtualException {
        if (log.isDebugEnabled()) {
            Op op = node.getOp();
            log.debug("Handling @{}: {}", op.getAddress(), op);
            if (log.isTraceEnabled()) {
                log.trace("Context before:\n{}", node.getContext().toString(false));
            }
        }

        try {
            node.execute();
        } catch (Exception e) {
            /*
             * Virtual exceptions should be set when appropriate by the ops. Getting an exception could mean there is
             * either a bug in smalivm (possible!) or the op just didn't take into account an exceptional situation,
             * such as a null pointer.
             * TODO: This should be re-examined when ops set exceptions properly.
             */
            //e.printStackTrace();
            int childAddress = exceptionResolver.resolve(e, node.getAddress());
            if (childAddress <= 0) {
                throw new RuntimeException("Real exception was thrown executing " + node +
                                           " and was not handled. This could be a bug in smalivm.", e);
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("{} threw a real exception but was caught by an exception handler. " +
                             "This may be a bug in smalivm or in the input code. Exception: ", node, e);
                }
            }
            spawnChild(graph, node, childAddress);
        }


        spawnChildren(graph, node);
        spawnExceptionChildren(graph, node, exceptionResolver);

        if (log.isDebugEnabled()) {
            log.debug("Context after:\n{}", node.getContext());
        }
    }
}
