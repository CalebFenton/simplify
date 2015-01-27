package org.cf.smalivm;

import gnu.trove.list.TIntList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.type.LocalInstance;
import org.cf.util.ImmutableUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachine {

    private static String getClassNameFromMethodDescriptor(String methodDescriptor) {
        return methodDescriptor.split("->", 2)[0];
    }

    private static HeapItem getMutableParameterConsensus(TIntList addressList, ExecutionGraph graph,
                    int parameterRegister) {
        ExecutionNode firstNode = graph.getNodePile(addressList.get(0)).get(0);
        HeapItem item = firstNode.getContext().getMethodState().peekParameter(parameterRegister);
        int[] addresses = addressList.toArray();
        for (int address : addresses) {
            List<ExecutionNode> nodes = graph.getNodePile(address);
            for (ExecutionNode node : nodes) {
                HeapItem otherItem = node.getContext().getMethodState().peekParameter(parameterRegister);
                if (item.getValue() != otherItem.getValue()) {
                    if (log.isTraceEnabled()) {
                        log.trace("No conensus value for r" + parameterRegister + ". Returning unknown.");
                    }

                    return HeapItem.newUnknown(item.getType());
                }
            }

        }

        return item;
    }

    private static final Logger log = LoggerFactory.getLogger(VirtualMachine.class.getSimpleName());

    private static final int DEFAULT_MAX_ADDRESS_VISITS = 500;
    private static final int DEFAULT_MAX_CALL_DEPTH = 20;
    private static final int DEFAULT_MAX_METHOD_VISITS = DEFAULT_MAX_ADDRESS_VISITS * 500;

    private final int maxCallDepth;
    private final int maxAddressVisits;
    private final int maxMethodVisits;
    private final MethodExecutor methodExecutor;
    private final SmaliClassManager classManager;
    private final Map<BuilderMethod, ExecutionGraph> methodToTemplateContextGraph;
    private final StaticFieldAccessor staticFieldAccessor;

    public VirtualMachine(SmaliClassManager manager) {
        this(manager, DEFAULT_MAX_ADDRESS_VISITS, DEFAULT_MAX_CALL_DEPTH, DEFAULT_MAX_METHOD_VISITS);
    }

    public VirtualMachine(SmaliClassManager manager, int maxAddressVisits, int maxCallDepth, int maxMethodVisits) {
        this.classManager = manager;
        this.maxAddressVisits = maxAddressVisits;
        this.maxMethodVisits = maxMethodVisits;
        this.maxCallDepth = maxCallDepth;
        methodExecutor = new MethodExecutor(this);
        methodToTemplateContextGraph = new HashMap<BuilderMethod, ExecutionGraph>();
        staticFieldAccessor = new StaticFieldAccessor(this);
    }

    public ExecutionGraph execute(String methodDescriptor) {
        if (!classManager.methodHasImplementation(methodDescriptor)) {
            return null;
        }
        ExecutionContext ectx = getRootExecutionContext(methodDescriptor);

        return execute(methodDescriptor, ectx);
    }

    public ExecutionGraph execute(String methodDescriptor, ExecutionContext ectx) {
        return execute(methodDescriptor, ectx, null, null);
    }

    public ExecutionGraph execute(String methodDescriptor, ExecutionContext calleeContext,
                    ExecutionContext callerContext, int[] parameterRegisters) {
        if (callerContext != null) {
            inheritClassStates(callerContext, calleeContext);
        }

        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        calleeContext.staticallyInitializeClassIfNecessary(className);

        ExecutionGraph graph = getInstructionGraphClone(methodDescriptor);
        ExecutionNode rootNode = new ExecutionNode(graph.getRoot());
        rootNode.setContext(calleeContext);
        graph.addNode(rootNode);

        ExecutionGraph result = null;
        try {
            result = methodExecutor.execute(graph);
        } catch (MaxCallDepthExceeded | MaxAddressVisitsExceeded | MaxMethodVisitsExceeded e) {
            if (log.isWarnEnabled()) {
                log.warn(e.toString());
            }
        }
        // catch (Exception e) {
        // if (log.isWarnEnabled()) {
        // log.warn("Unhandled exception in " + methodDescriptor + ". Giving up on this method.");
        // }
        // if (log.isDebugEnabled()) {
        // log.debug("Stack trace: ", e);
        // }
        // }

        if ((result != null) && (callerContext != null)) {
            collapseMultiverse(methodDescriptor, graph, calleeContext, callerContext, parameterRegisters);
        }

        return result;
    }

    public SmaliClassManager getClassManager() {
        return classManager;
    }

    public StaticFieldAccessor getStaticFieldAccessor() {
        return staticFieldAccessor;
    }

    public ExecutionGraph getInstructionGraphClone(String methodDescriptor) {
        BuilderMethod method = classManager.getMethod(methodDescriptor);
        if (!methodToTemplateContextGraph.containsKey(method)) {
            updateInstructionGraph(methodDescriptor);
        }
        ExecutionGraph graph = methodToTemplateContextGraph.get(method);
        ExecutionGraph clone = new ExecutionGraph(graph);

        return clone;
    }

    public int getMaxAddressVisits() {
        return maxAddressVisits;
    }

    public int getMaxCallDepth() {
        return maxCallDepth;
    }

    public int getMaxMethodVisits() {
        return maxMethodVisits;
    }

    public ExecutionContext getRootExecutionContext(String methodDescriptor) {
        if (!classManager.isLocalMethod(methodDescriptor)) {
            throw new IllegalArgumentException("Method does not exist: " + methodDescriptor);
        }

        if (!classManager.methodHasImplementation(methodDescriptor)) {
            // Native or abstract methods have no implementation. Shouldn't be executing them.
            throw new IllegalArgumentException("No implementation for " + methodDescriptor);
        }

        BuilderMethod method = classManager.getMethod(methodDescriptor);
        MethodImplementation impl = method.getImplementation();
        int registerCount = impl.getRegisterCount();
        List<String> parameterTypes = classManager.getParameterTypes(methodDescriptor);
        int parameterSize = Utils.getRegisterSize(parameterTypes);
        int accessFlags = method.getAccessFlags();
        boolean isStatic = ((accessFlags & AccessFlags.STATIC.getValue()) != 0);

        ExecutionContext rootContext = new ExecutionContext(this, methodDescriptor);
        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        addTemplateClassState(rootContext, className);

        // Assume all input values are unknown.
        MethodState mState = new MethodState(rootContext, registerCount, parameterTypes.size(), parameterSize);
        int firstParameter = mState.getParameterStart();
        int parameterRegister = firstParameter;
        for (String type : parameterTypes) {
            HeapItem item;
            if (!isStatic && (parameterRegister == firstParameter)) {
                item = new HeapItem(new LocalInstance(type), type);
            } else {
                item = HeapItem.newUnknown(type);
            }
            mState.assignParameter(parameterRegister, item);
            parameterRegister += "J".equals(type) || "D".equals(type) ? 2 : 1;
        }
        rootContext.setMethodState(mState);

        return rootContext;
    }

    public boolean isLocalClass(String classDescriptor) {
        // If it's local but reflected, should be treated as non-local.
        return classManager.isLocalClass(classDescriptor) && !MethodReflector.isSafe(classDescriptor);
    }

    public void updateInstructionGraph(String methodDescriptor) {
        BuilderMethod method = classManager.getMethod(methodDescriptor);
        ExecutionGraph graph = new ExecutionGraph(this, method);
        methodToTemplateContextGraph.put(method, graph);
    }

    public void addTemplateClassState(ExecutionContext ectx, String className) {
        List<String> fieldNameAndTypes = classManager.getFieldNameAndTypes(className);
        ClassState cState = new ClassState(ectx, className, fieldNameAndTypes.size());
        ectx.setClassState(className, cState, SideEffect.Level.NONE);
        for (String fieldNameAndType : fieldNameAndTypes) {
            String type = fieldNameAndType.split(":")[1];
            cState.pokeField(fieldNameAndType, HeapItem.newUnknown(type));
        }
    }

    /*
     * Get consensus for method and class states for all execution paths and merge them into callerContext.
     */
    private void collapseMultiverse(String methodDescriptor, ExecutionGraph graph, ExecutionContext calleeContext,
                    ExecutionContext callerContext, int[] parameterRegisters) {
        TIntList terminatingAddresses = graph.getConnectedTerminatingAddresses();
        if (parameterRegisters != null) {
            MethodState mState = callerContext.getMethodState();
            List<String> parameterTypes = classManager.getParameterTypes(methodDescriptor);
            int parameterRegister = calleeContext.getMethodState().getParameterStart();
            for (int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++) {
                String type = parameterTypes.get(parameterIndex);
                if (ImmutableUtils.isImmutableClass(type)) {
                    continue;
                }

                HeapItem item = getMutableParameterConsensus(terminatingAddresses, graph, parameterRegister);
                int register = parameterRegisters[parameterIndex];
                mState.assignRegister(register, item);

                parameterRegister += "J".equals(type) || "D".equals(type) ? 2 : 1;
            }
        }

        for (String currentClassName : classManager.getClassNames()) {
            if (!callerContext.isClassInitialized(currentClassName)
                            && !calleeContext.isClassInitialized(currentClassName)) {
                continue;
            }

            List<String> fieldNameAndTypes = classManager.getFieldNameAndTypes(currentClassName);
            ClassState currentClassState;
            if (callerContext.isClassInitialized(currentClassName)) {
                currentClassState = callerContext.peekClassState(currentClassName);
            } else {
                currentClassState = new ClassState(callerContext, currentClassName, fieldNameAndTypes.size());
                SideEffect.Level level = graph.getHighestClassSideEffectLevel(currentClassName);
                callerContext.initializeClass(currentClassName, currentClassState, level);
            }

            for (String fieldNameAndType : fieldNameAndTypes) {
                HeapItem item = graph.getFieldConsensus(terminatingAddresses, currentClassName, fieldNameAndType);
                currentClassState.pokeField(fieldNameAndType, item);
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
            SideEffect.Level level = parent.getClassStateSideEffectLevel(className);
            child.initializeClass(className, toClassState, level);
        }
    }

}
