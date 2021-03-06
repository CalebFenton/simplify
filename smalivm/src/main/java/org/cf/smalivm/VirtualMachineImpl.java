package org.cf.smalivm;

import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.*;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.dex.SmaliClassLoader;
import org.cf.smalivm.exception.VirtualMachineException;
import org.cf.smalivm.type.*;
import org.cf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualMachineImpl implements VirtualMachine {

    private static final Logger log = LoggerFactory.getLogger(VirtualMachineImpl.class.getSimpleName());

    private final ClassManager classManager;
    private final MethodExecutorFactory methodExecutorFactory;
    private final SmaliClassLoader classLoader;
    private final Map<VirtualMethod, ExecutionGraph> methodToTemplateExecutionGraph;
    private final StaticFieldAccessor staticFieldAccessor;
    private final Configuration configuration;
    private final ExceptionFactory exceptionFactory;

    VirtualMachineImpl(ClassManager manager, int maxAddressVisits, int maxCallDepth, int maxMethodVisits, int maxExecutionTime) {
        this.classManager = manager;
        this.methodExecutorFactory = new MethodExecutorFactory(this)
                .setMaxAddressVisits(maxAddressVisits)
                .setMaxCallDepth(maxCallDepth)
                .setMaxMethodVisits(maxMethodVisits)
                .setMaxExecutionTime(maxExecutionTime);
        classLoader = new SmaliClassLoader(classManager);
        methodToTemplateExecutionGraph = new HashMap<>();
        staticFieldAccessor = new StaticFieldAccessor(this);
        configuration = Configuration.instance();
        exceptionFactory = new ExceptionFactory(classLoader);
    }

    @Override
    public ExecutionGraph execute(String methodSignature) throws VirtualMachineException {
        VirtualMethod method = classManager.getMethod(methodSignature);
        if (method == null) {
            throw new RuntimeException("Method signature not found: " + methodSignature);
        }

        return execute(method);
    }

    @Override
    public ExecutionGraph execute(String className, String methodDescriptor) throws VirtualMachineException {
        return execute(className + "->" + methodDescriptor);
    }

    @Override
    public ExecutionGraphImpl execute(VirtualMethod virtualMethod) throws VirtualMachineException {
        ExecutionContext calleeContext = spawnRootContext(virtualMethod);

        return execute(calleeContext);
    }

    @Override
    public ExecutionGraphImpl execute(ExecutionContext context) throws VirtualMachineException {
        return execute(context, null, null);
    }

    @Override
    public ExecutionGraphImpl execute(ExecutionContext calleeContext, ExecutionContext callerContext, int[] parameterRegisters) throws VirtualMachineException {
        VirtualMethod virtualMethod = calleeContext.getMethod();
        if (virtualMethod == null) {
            throw new RuntimeException("Method not found: " + virtualMethod);
        } else if (!virtualMethod.hasImplementation()) {
            log.warn("Attempting to execute method without implementation: {}", virtualMethod);
            return null;
        }

        MethodExecutor methodExecutor = methodExecutorFactory.build(calleeContext, callerContext);
        methodExecutor.execute();

        return finishExecution(methodExecutor, callerContext, parameterRegisters);
    }

    private ExecutionGraphImpl finishExecution(MethodExecutor methodExecutor, ExecutionContext callerContext, int[] parameterRegisters) {
        if ((methodExecutor.getExecutionGraph() != null) && (callerContext != null)) {
            collapseMultiverse(methodExecutor.getVirtualMethod(), methodExecutor.getExecutionGraph(), callerContext, parameterRegisters);
        }

        return methodExecutor.getExecutionGraph();
    }

    @Override
    public MethodExecutor startDebug(ExecutionContext calleeContext, ExecutionContext callerContext) {
        VirtualMethod virtualMethod = calleeContext.getMethod();
        if (!virtualMethod.hasImplementation()) {
            log.warn("Attempting to execute method without implementation: {}", virtualMethod);
            return null;
        }

        return methodExecutorFactory.build(calleeContext, callerContext);
    }

    @Override
    public ExecutionGraph finishDebug(MethodExecutor methodExecutor, ExecutionContext callerContext, int[] parameterRegisters) {
        return finishExecution(methodExecutor, callerContext, parameterRegisters);
    }

    @Override
    public SmaliClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ClassManager getClassManager() {
        return classManager;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public StaticFieldAccessor getStaticFieldAccessor() {
        return staticFieldAccessor;
    }

    @Override
    public boolean isSafe(VirtualType virtualClass) {
        return getConfiguration().isSafe(virtualClass.toString());
    }

    @Override
    public ExecutionGraph spawnInstructionGraph(String className, String methodDescriptor) {
        VirtualMethod method = getClassManager().getVirtualClass(className).getMethod(methodDescriptor);

        return spawnInstructionGraph(method);
    }

    @Override
    public ExecutionGraphImpl spawnInstructionGraph(VirtualMethod method) {
        if (!methodToTemplateExecutionGraph.containsKey(method)) {
            updateInstructionGraph(method);
        }
        ExecutionGraphImpl graph = (ExecutionGraphImpl) methodToTemplateExecutionGraph.get(method);

        return new ExecutionGraphImpl(graph);
    }

    @Override
    public ExecutionContext spawnRootContext(String methodSignature) {
        String[] parts = methodSignature.split("->");
        String className = parts[0];
        String methodDescriptor = parts[1];

        return spawnRootContext(className, methodDescriptor);
    }

    @Override
    public ExecutionContext spawnRootContext(String className, String methodDescriptor) {
        VirtualMethod method = getClassManager().getVirtualClass(className).getMethod(methodDescriptor);
        if (method == null) {
            throw new RuntimeException("Method signature not found: " + className + "->" + methodDescriptor);
        }

        return spawnRootContext(method);
    }

    @Override
    public ExecutionContext spawnRootContext(VirtualMethod method) {
        return spawnRootContext(method, null, 0);
    }

    @Override
    public ExecutionContext spawnRootContext(VirtualMethod method, @Nullable ExecutionContext callerContext, int callerAddress) {
        if (!method.hasImplementation()) {
            // Native or abstract methods have no implementation. Shouldn't be executing them.
            throw new IllegalArgumentException("No implementation for " + method);
        }

        ExecutionContext spawnedContext = new ExecutionContext(this, method);
        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, method.getDefiningClass());
        spawnedContext.setClassState(templateClassState);

        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
        spawnedContext.setMethodState(templateMethodState);

        if (callerContext != null) {
            spawnedContext.registerCaller(callerContext, callerAddress);
        }

        return spawnedContext;
    }

    @Override
    public void updateInstructionGraph(VirtualMethod method) {
        ExecutionGraph graph = new ExecutionGraphImpl(this, method);
        methodToTemplateExecutionGraph.put(method, graph);
    }

    @Override
    public MethodExecutorFactory getMethodExecutorFactory() {
        return methodExecutorFactory;
    }

    @Override
    public ExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }

    private static String getClassNameFromMethodSignature(String methodSignature) {
        return methodSignature.split("->", 2)[0];
    }

    private static HeapItem getMutableParameterConsensus(int[] addresses, ExecutionGraph graph, int parameterRegister) {
        ExecutionNode firstNode = graph.getNodePile(addresses[0]).get(0);
        HeapItem item = firstNode.getContext().getMethodState().peekParameter(parameterRegister);
        for (int address : addresses) {
            List<ExecutionNode> nodes = graph.getNodePile(address);
            for (ExecutionNode node : nodes) {
                HeapItem otherItem = node.getContext().getMethodState().peekParameter(parameterRegister);
                if (item.getValue() != otherItem.getValue()) {
                    log.trace("No consensus value for r{}. Returning unknown.", parameterRegister);

                    return HeapItem.newUnknown(item.getType());
                }
            }

        }

        return item;
    }

    /*
     * Get the consensus of mutable objects of method and class states of called context and merge them into
     * the context of the caller. In other words, reflect changes to objects that happen in the called method back
     * into the caller method.
     */
    private void collapseMultiverse(VirtualMethod calledMethod, ExecutionGraph graph, ExecutionContext callerContext, int[] parameterRegisters) {
        int[] terminatingAddresses = graph.getConnectedTerminatingAddresses();
        if (parameterRegisters != null) {
            MethodState callerMethodState = callerContext.getMethodState();
            List<String> parameterTypes = calledMethod.getParameterTypeNames();
            int parameterRegister = graph.getNodePile(0).get(0).getContext().getMethodState().getParameterStart();
            for (int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++) {
                String type = parameterTypes.get(parameterIndex);
                if (configuration.isMutable(type)) {
                    HeapItem item = getMutableParameterConsensus(terminatingAddresses, graph, parameterRegister);
                    int register = parameterRegisters[parameterIndex];
                    callerMethodState.assignRegisterAndUpdateIdentities(register, item);
                }
                parameterRegister += Utils.getRegisterSize(type);
            }
        }

        // TODO: performance: this is expensive and happens frequently.
        // classManager has all local classes. would be nice to keep track of all initialized classes
        // for each method execution, and use that to iterate instead
        List<ExecutionContext> terminatingContexts = graph.getTerminatingContexts();
        for (VirtualClass virtualClass : classManager.getLoadedClasses()) {
            boolean isInitializedInCaller = callerContext.isClassInitialized(virtualClass);
            hasOneInitialization:
            if (!isInitializedInCaller) {
                // Not initialized in caller, but maybe initialized in callee multiverse.
                for (ExecutionContext context : terminatingContexts) {
                    if (context.isClassInitialized(virtualClass)) {
                        break hasOneInitialization;
                    }
                }

                // Class was never initialized in called method. Nothing to merge into caller method.
                continue;
            }


            ClassState cState;
            if (isInitializedInCaller) {
                cState = callerContext.peekClassState(virtualClass);
            } else {
                /*
                 * Class is initialized in the graph but not in callerContext.
                 * It must have been initialized during execution that produced the graph.
                 */
                cState = new ClassState(virtualClass, callerContext);
                SideEffect.Level level = graph.getHighestClassSideEffectLevel(virtualClass);
                callerContext.initializeClass(cState, level);
            }

            for (VirtualField field : virtualClass.getFields()) {
                HeapItem item = graph.getFieldConsensus(terminatingAddresses, field);
                if (item.isPrimitive()) {
                    cState.pokeField(field, item);
                } else {
                    cState.updateIdentities(field, item);
                }
            }
        }
    }

}
