package org.cf.smalivm;

import gnu.trove.set.TIntSet;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxExecutionTimeExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.type.LocalInstance;
import org.cf.util.ImmutableUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachine {

    private static String getClassNameFromMethodDescriptor(String methodDescriptor) {
        return methodDescriptor.split("->", 2)[0];
    }

    private static HeapItem getMutableParameterConsensus(int[] addresses, ExecutionGraph graph, int parameterRegister) {
        ExecutionNode firstNode = graph.getNodePile(addresses[0]).get(0);
        HeapItem item = firstNode.getContext().getMethodState().peekParameter(parameterRegister);
        for (int address : addresses) {
            List<ExecutionNode> nodes = graph.getNodePile(address);
            for (ExecutionNode node : nodes) {
                HeapItem otherItem = node.getContext().getMethodState().peekParameter(parameterRegister);
                if (item.getValue() != otherItem.getValue()) {
                    log.trace("No conensus value for r{}. Returning unknown.", parameterRegister);

                    return HeapItem.newUnknown(item.getType());
                }
            }

        }

        return item;
    }

    private static final Logger log = LoggerFactory.getLogger(VirtualMachine.class.getSimpleName());

    private final MethodExecutor methodExecutor;
    private final ClassManager classManager;
    private final Map<BuilderMethod, ExecutionGraph> methodToTemplateExecutionGraph;
    private final StaticFieldAccessor staticFieldAccessor;

    VirtualMachine(ClassManager manager, int maxAddressVisits, int maxCallDepth, int maxMethodVisits,
                    int maxExecutionTime) {
        this.classManager = manager;
        methodExecutor = new MethodExecutor(classManager, maxCallDepth, maxAddressVisits, maxMethodVisits,
                        maxExecutionTime);
        methodToTemplateExecutionGraph = new HashMap<BuilderMethod, ExecutionGraph>();
        staticFieldAccessor = new StaticFieldAccessor(this);
    }

    public ExecutionGraph execute(String methodDescriptor) throws MaxAddressVisitsExceeded, MaxCallDepthExceeded,
                    MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        if (!classManager.methodHasImplementation(methodDescriptor)) {
            return null;
        }
        ExecutionContext ectx = spawnRootExecutionContext(methodDescriptor);

        return execute(methodDescriptor, ectx);
    }

    public ExecutionGraph execute(String methodDescriptor, ExecutionContext ectx) throws MaxAddressVisitsExceeded,
                    MaxCallDepthExceeded, MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        return execute(methodDescriptor, ectx, null, null);
    }

    public ExecutionGraph execute(String methodDescriptor, ExecutionContext calleeContext,
                    ExecutionContext callerContext, int[] parameterRegisters) throws MaxAddressVisitsExceeded,
                    MaxCallDepthExceeded, MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        if (callerContext != null) {
            inheritClassStates(callerContext, calleeContext);
        }

        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        calleeContext.staticallyInitializeClassIfNecessary(className);

        ExecutionGraph graph = spawnInstructionGraph(methodDescriptor);
        ExecutionNode rootNode = new ExecutionNode(graph.getRoot());
        rootNode.setContext(calleeContext);
        graph.addNode(rootNode);

        ExecutionGraph execution = methodExecutor.execute(graph);
        if ((execution != null) && (callerContext != null)) {
            collapseMultiverse(methodDescriptor, graph, callerContext, parameterRegisters);
        }

        return execution;
    }

    public ClassManager getClassManager() {
        return classManager;
    }

    public StaticFieldAccessor getStaticFieldAccessor() {
        return staticFieldAccessor;
    }

    public ExecutionGraph spawnInstructionGraph(String methodDescriptor) {
        BuilderMethod method = classManager.getMethod(methodDescriptor);
        if (!methodToTemplateExecutionGraph.containsKey(method)) {
            updateInstructionGraph(methodDescriptor);
        }
        ExecutionGraph graph = methodToTemplateExecutionGraph.get(method);
        ExecutionGraph spawn = new ExecutionGraph(graph);

        return spawn;
    }

    public ExecutionContext spawnRootExecutionContext(String methodDescriptor) {
        return spawnRootExecutionContext(methodDescriptor, null, 0);
    }

    public ExecutionContext spawnRootExecutionContext(String methodDescriptor,
                    @Nullable ExecutionContext callerContext, int callerAddress) {
        if (!classManager.isLocalMethod(methodDescriptor)) {
            throw new IllegalArgumentException("Method does not exist: " + methodDescriptor);
        }

        if (!classManager.methodHasImplementation(methodDescriptor)) {
            // Native or abstract methods have no implementation. Shouldn't be executing them.
            throw new IllegalArgumentException("No implementation for " + methodDescriptor);
        }

        ExecutionContext spawnedContext = new ExecutionContext(this, methodDescriptor);
        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        ClassState templateClassState = getTemplateClassState(spawnedContext, className);
        spawnedContext.setClassState(className, templateClassState);

        MethodState templateMethodState = getTemplateMethodState(spawnedContext);
        spawnedContext.setMethodState(templateMethodState);

        if (callerContext != null) {
            spawnedContext.registerCaller(callerContext, callerAddress);
        }

        return spawnedContext;
    }

    private MethodState getTemplateMethodState(ExecutionContext ectx) {
        String methodDescriptor = ectx.getMethodDescriptor();
        BuilderMethod method = classManager.getMethod(methodDescriptor);
        MethodImplementation implementation = method.getImplementation();
        int registerCount = implementation.getRegisterCount();
        List<String> parameterTypes = classManager.getParameterTypes(methodDescriptor);
        int parameterSize = Utils.getRegisterSize(parameterTypes);
        MethodState mState = new MethodState(ectx, registerCount, parameterTypes.size(), parameterSize);
        int firstParameter = mState.getParameterStart();
        int parameterRegister = firstParameter;

        // Assume all input values are unknown.
        boolean isStatic = Modifier.isStatic(method.getAccessFlags());
        for (String type : parameterTypes) {
            HeapItem item;
            if (!isStatic && (parameterRegister == firstParameter)) {
                item = new HeapItem(new LocalInstance(type), type);
            } else {
                item = HeapItem.newUnknown(type);
            }
            mState.assignParameter(parameterRegister, item);
            parameterRegister += Utils.getRegisterSize(type);
        }

        return mState;
    }

    public boolean isLocalClass(String classDescriptor) {
        // Prefer to reflect methods, even if local. It's faster and less prone to error than emulating ourselves.
        return classManager.isLocalClass(classDescriptor) && !MethodReflector.isSafe(classDescriptor);
    }

    public void updateInstructionGraph(String methodDescriptor) {
        BuilderMethod method = classManager.getMethod(methodDescriptor);
        ExecutionGraph graph = new ExecutionGraph(this, method);
        methodToTemplateExecutionGraph.put(method, graph);
    }

    public ClassState getTemplateClassState(ExecutionContext ectx, String className) {
        List<String> fieldNameAndTypes = classManager.getFieldNameAndTypes(className);
        ClassState cState = new ClassState(ectx, className, fieldNameAndTypes.size());
        for (String fieldNameAndType : fieldNameAndTypes) {
            String type = fieldNameAndType.split(":")[1];
            cState.pokeField(fieldNameAndType, HeapItem.newUnknown(type));
        }

        return cState;
    }

    /*
     * Get consensus for method and class states and merge them into callerContext.
     */
    private void collapseMultiverse(String methodDescriptor, ExecutionGraph graph, ExecutionContext callerContext,
                    int[] parameterRegisters) {
        TIntSet terminatingAddressSet = graph.getConnectedTerminatingAddresses();
        int[] terminatingAddresses = terminatingAddressSet.toArray();
        if (parameterRegisters != null) {
            MethodState mState = callerContext.getMethodState();
            List<String> parameterTypes = classManager.getParameterTypes(methodDescriptor);
            int parameterRegister = graph.getNodePile(0).get(0).getContext().getMethodState().getParameterStart();
            for (int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++) {
                String type = parameterTypes.get(parameterIndex);
                if (ImmutableUtils.isImmutableClass(type)) {
                    continue;
                }

                HeapItem item = getMutableParameterConsensus(terminatingAddresses, graph, parameterRegister);
                int register = parameterRegisters[parameterIndex];
                mState.assignRegister(register, item);

                parameterRegister += Utils.getRegisterSize(type);
            }
        }

        // TODO: performance: this is expensive and happens frequently.
        // classManager has all classes, include reflib. would be nice to keep track of all initialized classes
        // for each method execution, and use that to iterate instead
        List<ExecutionContext> terminatingContexts = graph.getTerminatingContexts();
        for (String className : classManager.getClassNames()) {
            boolean isInitializedInCaller = callerContext.isClassInitialized(className);
            hasOneInitialization: if (!isInitializedInCaller) {
                // Not initialized in caller, but maybe initialized in callee multiverse.
                for (ExecutionContext ectx : terminatingContexts) {
                    if (ectx.isClassInitialized(className)) {
                        break hasOneInitialization;
                    }
                }

                // Class was never initialized. Nothing to merge.
                continue;
            }

            List<String> fieldNameAndTypes = classManager.getFieldNameAndTypes(className);
            ClassState cState;
            if (isInitializedInCaller) {
                cState = callerContext.peekClassState(className);
            } else {
                cState = new ClassState(callerContext, className, fieldNameAndTypes.size());
                SideEffect.Level level = graph.getHighestClassSideEffectLevel(className);
                callerContext.initializeClass(className, cState, level);
            }

            for (String fieldNameAndType : fieldNameAndTypes) {
                HeapItem item = graph.getFieldConsensus(terminatingAddressSet, className, fieldNameAndType);
                cState.pokeField(fieldNameAndType, item);
            }
        }
    }

    private void inheritClassStates(ExecutionContext parent, ExecutionContext child) {
        for (String className : classManager.getLoadedClassNames()) {
            if (!parent.isClassInitialized(className)) {
                continue;
            }

            ClassState fromClassState = parent.peekClassState(className);
            ClassState toClassState = new ClassState(fromClassState, child);
            for (String fieldNameAndType : classManager.getFieldNameAndTypes(className)) {
                HeapItem item = fromClassState.peekField(fieldNameAndType);
                toClassState.pokeField(fieldNameAndType, item);
            }
            SideEffect.Level level = parent.getClassSideEffectLevel(className);
            child.initializeClass(className, toClassState, level);
        }
    }

}
