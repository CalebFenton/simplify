package org.cf.smalivm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.cf.smalivm.configuration.Configuration;
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
import org.cf.smalivm.reference.LocalMethod;
import org.cf.smalivm.smali.AncestorEnumerator;
import org.cf.smalivm.smali.ClassManager;
import org.cf.smalivm.smali.SmaliClassLoader;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.ValueType;
import org.jf.dexlib2.iface.value.BooleanEncodedValue;
import org.jf.dexlib2.iface.value.ByteEncodedValue;
import org.jf.dexlib2.iface.value.CharEncodedValue;
import org.jf.dexlib2.iface.value.DoubleEncodedValue;
import org.jf.dexlib2.iface.value.FloatEncodedValue;
import org.jf.dexlib2.iface.value.IntEncodedValue;
import org.jf.dexlib2.iface.value.LongEncodedValue;
import org.jf.dexlib2.iface.value.ShortEncodedValue;
import org.jf.dexlib2.iface.value.StringEncodedValue;
import org.jf.dexlib2.writer.builder.BuilderEncodedValues;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachine {

    private static final Logger log = LoggerFactory.getLogger(VirtualMachine.class.getSimpleName());

    private final ClassManager classManager;
    private final SmaliClassLoader classLoader;
    private final MethodExecutor methodExecutor;
    private final Map<LocalMethod, ExecutionGraph> methodToTemplateExecutionGraph;
    private final StaticFieldAccessor staticFieldAccessor;
    private final Configuration configuration;
    private final AncestorEnumerator ancestorEnumerator;

    VirtualMachine(ClassManager manager, int maxAddressVisits, int maxCallDepth, int maxMethodVisits,
                    int maxExecutionTime) {
        this.classManager = manager;
        classLoader = new SmaliClassLoader(classManager);
        methodExecutor = new MethodExecutor(classManager, maxCallDepth, maxAddressVisits, maxMethodVisits,
                        maxExecutionTime);
        methodToTemplateExecutionGraph = new HashMap<LocalMethod, ExecutionGraph>();
        staticFieldAccessor = new StaticFieldAccessor(this);
        configuration = Configuration.instance();
        ancestorEnumerator = new AncestorEnumerator(classManager, classLoader);
    }

    public ExecutionGraph execute(String methodSignature) throws MaxAddressVisitsExceeded, MaxCallDepthExceeded,
                    MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        LocalMethod localMethod = classManager.getMethod(methodSignature);

        return execute(localMethod);
    }

    public ExecutionGraph execute(LocalMethod localMethod) throws MaxAddressVisitsExceeded, MaxCallDepthExceeded,
                    MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        ExecutionContext ectx = spawnRootExecutionContext(localMethod);

        return execute(localMethod, ectx, null, null);
    }

    public ExecutionGraph execute(String methodSignature, ExecutionContext ectx) throws MaxAddressVisitsExceeded,
                    MaxCallDepthExceeded, MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        return execute(methodSignature, ectx, null, null);
    }

    public ExecutionGraph execute(String methodSignature, ExecutionContext calleeContext,
                    ExecutionContext callerContext, int[] parameterRegisters) throws MaxAddressVisitsExceeded,
                    MaxCallDepthExceeded, MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        LocalMethod localMethod = classManager.getMethod(methodSignature);

        return execute(localMethod, calleeContext, callerContext, parameterRegisters);
    }

    public ExecutionGraph execute(LocalMethod localMethod, ExecutionContext calleeContext,
                    ExecutionContext callerContext, int[] parameterRegisters) throws MaxAddressVisitsExceeded,
                    MaxCallDepthExceeded, MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        if (!localMethod.hasImplementation()) {
            return null;
        }

        if (callerContext != null) {
            inheritClassStates(callerContext, calleeContext);
        }

        String className = localMethod.getClassName();
        calleeContext.staticallyInitializeClassIfNecessary(className);

        ExecutionGraph graph = spawnInstructionGraph(localMethod);
        ExecutionNode rootNode = new ExecutionNode(graph.getRoot());
        rootNode.setContext(calleeContext);
        graph.addNode(rootNode);

        ExecutionGraph execution = methodExecutor.execute(graph);
        if ((execution != null) && (callerContext != null)) {
            collapseMultiverse(localMethod, graph, callerContext, parameterRegisters);
        }

        return execution;
    }

    public AncestorEnumerator getAncestorEnumerator() {
        return ancestorEnumerator;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ClassManager getClassManager() {
        return classManager;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public StaticFieldAccessor getStaticFieldAccessor() {
        return staticFieldAccessor;
    }


    public boolean shouldTreatAsLocal(String classDescriptor) {
        // Prefer to reflect methods, even if local. It's faster and less prone to error than emulating ourselves.
        return classManager.isLocalClass(classDescriptor) && !getConfiguration().isSafe(classDescriptor);
    }

    public ExecutionGraph spawnInstructionGraph(String className, String methodDescriptor){
        return spawnInstructionGraph(classManager.getMethod(className, methodDescriptor));
    }

    public ExecutionGraph spawnInstructionGraph(String methodSignature){
        return spawnInstructionGraph(classManager.getMethod(methodSignature));
    }

    public ExecutionGraph spawnInstructionGraph(LocalMethod localMethod) {
        if (!methodToTemplateExecutionGraph.containsKey(localMethod)) {
            updateInstructionGraph(localMethod);
        }
        ExecutionGraph graph = methodToTemplateExecutionGraph.get(localMethod);
        ExecutionGraph spawn = new ExecutionGraph(graph);

        return spawn;
    }

    public ExecutionContext spawnRootExecutionContext(String methodSignature) {
        return spawnRootExecutionContext(classManager.getMethod(methodSignature));
    }

    public ExecutionContext spawnRootExecutionContext(LocalMethod localMethod) {
        return spawnRootExecutionContext(localMethod, null, 0);
    }

    public ExecutionContext spawnRootExecutionContext(LocalMethod localMethod,
                    @Nullable ExecutionContext callerContext, int callerAddress) {

        if (!localMethod.hasImplementation()) {
            // Native or abstract methods have no implementation. Shouldn't be executing them.
            throw new IllegalArgumentException("No implementation for " + localMethod);
        }

        ExecutionContext spawnedContext = new ExecutionContext(this, localMethod);
        String className = localMethod.getClassName();
        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, className, classManager);
        spawnedContext.setClassState(className, templateClassState);

        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
        spawnedContext.setMethodState(templateMethodState);

        if (callerContext != null) {
            spawnedContext.registerCaller(callerContext, callerAddress);
        }

        return spawnedContext;
    }

    public void updateInstructionGraph(LocalMethod localMethod) {
        ExecutionGraph graph = new ExecutionGraph(this, localMethod);
        methodToTemplateExecutionGraph.put(localMethod, graph);
    }

    /*
     * Get consensus for method and class states and merge them into callerContext.
     */
    private void collapseMultiverse(LocalMethod localMethod, ExecutionGraph graph, ExecutionContext callerContext,
                    int[] parameterRegisters) {
        int[] terminatingAddresses = graph.getConnectedTerminatingAddresses();
        if (parameterRegisters != null) {
            MethodState mState = callerContext.getMethodState();
            List<String> parameterTypes = localMethod.getParameterTypes();
            int parameterRegister = graph.getNodePile(0).get(0).getContext().getMethodState().getParameterStart();
            for (int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++) {
                String type = parameterTypes.get(parameterIndex);
                if (configuration.isImmutable(type)) {
                    continue;
                }

                HeapItem item = getMutableParameterConsensus(terminatingAddresses, graph, parameterRegister);
                int register = parameterRegisters[parameterIndex];
                mState.assignRegister(register, item);

                parameterRegister += Utils.getRegisterSize(type);
            }
        }

        // TODO: performance: this is expensive and happens frequently.
        // classManager has all local classes. would be nice to keep track of all initialized classes
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
                HeapItem item = graph.getFieldConsensus(terminatingAddresses, className, fieldNameAndType);
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
                    log.trace("No conensus value for r{}. Returning unknown.", parameterRegister);

                    return HeapItem.newUnknown(item.getType());
                }
            }

        }

        return item;
    }

}
