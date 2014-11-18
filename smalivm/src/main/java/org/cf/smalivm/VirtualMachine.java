package org.cf.smalivm;

import gnu.trove.list.TIntList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.TypeUtil;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Dexifier;
import org.cf.util.ImmutableUtils;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachine {

    public static int getParameterSize(List<String> parameterTypes) {
        int result = 0;
        for (String type : parameterTypes) {
            result += type.equals("J") || type.equals("D") ? 2 : 1;
        }

        return result;
    }

    private static String getClassNameFromMethodDescriptor(String methodDescriptor) {
        return methodDescriptor.split("->", 2)[0];
    }

    private static Object getMutableParameterConsensus(TIntList addressList, ExecutionGraph graph, int parameterIndex) {
        ExecutionNode firstNode = graph.getNodePile(addressList.get(0)).get(0);
        Object value = firstNode.getContext().getMethodState().peekParameter(parameterIndex);
        int[] addresses = addressList.toArray();
        for (int address : addresses) {
            List<ExecutionNode> nodes = graph.getNodePile(address);
            for (ExecutionNode node : nodes) {
                Object otherValue = node.getContext().getMethodState().peekParameter(parameterIndex);

                if (value != otherValue) {
                    if (log.isTraceEnabled()) {
                        log.trace("No conensus value for parameterIndex #" + parameterIndex + ", returning unknown");
                    }

                    return new UnknownValue(TypeUtil.getValueType(value));
                }
            }

        }

        return value;
    }

    private static final Logger log = LoggerFactory.getLogger(VirtualMachine.class.getSimpleName());

    private static final int DEFAULT_MAX_ADDRESS_VISITS = 500;
    private static final int DEFAULT_MAX_METHOD_VISITS = DEFAULT_MAX_ADDRESS_VISITS * 500;
    private static final int DEFAULT_MAX_CALL_DEPTH = 20;

    private final int maxCallDepth;
    private final int maxAddressVisits;
    private final int maxMethodVisits;
    private final MethodExecutor methodExecutor;
    private final ClassManager classManager;
    private final Map<String, ExecutionGraph> methodDescriptorToTemplateContextGraph;

    public VirtualMachine(ClassManager manager, int maxAddressVisits, int maxCallDepth, int maxMethodVisits) {
        this.classManager = manager;
        this.maxAddressVisits = maxAddressVisits;
        this.maxMethodVisits = maxMethodVisits;
        this.maxCallDepth = maxCallDepth;

        methodExecutor = new MethodExecutor(this);

        methodDescriptorToTemplateContextGraph = new HashMap<String, ExecutionGraph>();
        buildMethodDescriptorToTemplateContextGraph();
    }

    public VirtualMachine(List<BuilderClassDef> classDefs) {
        this(new ClassManager(classDefs), DEFAULT_MAX_ADDRESS_VISITS, DEFAULT_MAX_CALL_DEPTH, DEFAULT_MAX_METHOD_VISITS);
    }

    public VirtualMachine(String path) throws Exception {
        this(path, DEFAULT_MAX_ADDRESS_VISITS, DEFAULT_MAX_CALL_DEPTH, DEFAULT_MAX_METHOD_VISITS);
    }

    public VirtualMachine(String path, int maxAddressVisits, int maxCallDepth, int maxMethodVisits) throws Exception {
        this(new ClassManager(Dexifier.dexifySmaliFiles(path)), maxAddressVisits, maxCallDepth, maxMethodVisits);
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
        } catch (MaxCallDepthExceeded | MaxAddressVisitsExceeded e) {
            if (log.isWarnEnabled()) {
                log.warn(e.toString());
            }
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Unhandled exception in " + methodDescriptor + ". Giving up on this method.");
            }
            if (log.isDebugEnabled()) {
                log.debug("Stack trace: ", e);
            }
        }

        if ((result != null) && (callerContext != null)) {
            collapseMultiverse(methodDescriptor, graph, calleeContext, callerContext, parameterRegisters);
        }

        return result;
    }

    public ClassManager getClassManager() {
        return classManager;
    }

    public ExecutionGraph getInstructionGraphClone(String methodDescriptor) {
        ExecutionGraph graph = methodDescriptorToTemplateContextGraph.get(methodDescriptor);
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
        if (!classManager.methodHasImplementation(methodDescriptor)) {
            // Native or abstract methods have no implementation. Shouldn't be executing them.
            throw new IllegalArgumentException("No implementation for " + methodDescriptor);
        }

        BuilderMethod method = classManager.getBuilderMethod(methodDescriptor);
        MethodImplementation impl = method.getImplementation();
        int registerCount = impl.getRegisterCount();
        List<String> parameterTypes = classManager.getParameterTypes(methodDescriptor);
        int parameterSize = getParameterSize(parameterTypes);
        int accessFlags = method.getAccessFlags();
        boolean isStatic = ((accessFlags & AccessFlags.STATIC.getValue()) != 0);

        ExecutionContext rootContext = new ExecutionContext(this);
        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        addTemplateClassState(rootContext, className);

        // Assume all input values are unknown.
        MethodState mState = new MethodState(rootContext, registerCount, parameterTypes.size(), parameterSize);
        for (int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++) {
            String type = parameterTypes.get(parameterIndex);
            Object value = (!isStatic && (parameterIndex == 0)) ? new LocalInstance(type) : new UnknownValue(type);
            mState.assignParameter(parameterIndex, value);
        }
        rootContext.setMethodState(mState);

        return rootContext;
    }

    public boolean isLocalClass(String classDescriptor) {
        return classManager.isLocalClass(classDescriptor);
    }

    public void updateInstructionGraph(String methodDescriptor) {
        BuilderMethod method = classManager.getBuilderMethod(methodDescriptor);
        ExecutionGraph graph = new ExecutionGraph(this, method);
        methodDescriptorToTemplateContextGraph.put(methodDescriptor, graph);
    }

    private void addTemplateClassState(ExecutionContext ectx, String className) {
        List<String> fieldNameAndTypes = classManager.getFieldNameAndTypes(className);
        ClassState cState = new ClassState(ectx, className, fieldNameAndTypes.size());
        ectx.setClassState(className, cState, SideEffect.Level.NONE);
        for (String fieldNameAndType : fieldNameAndTypes) {
            String type = fieldNameAndType.split(":")[1];
            cState.pokeField(fieldNameAndType, new UnknownValue(type));
        }
    }

    private void buildMethodDescriptorToTemplateContextGraph() {
        for (String classDescriptor : classManager.getLocalClasses()) {
            BuilderClassDef classDef = classManager.getBuilderClass(classDescriptor);
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                if (!classManager.methodHasImplementation(methodDescriptor)) {
                    continue;
                }
                updateInstructionGraph(methodDescriptor);
            }
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
            for (int parameterIndex = 0; parameterIndex < parameterRegisters.length; parameterIndex++) {
                String type = parameterTypes.get(parameterIndex);
                boolean mutable = !ImmutableUtils.isImmutableClass(type);
                if (!mutable) {
                    continue;
                }

                Object value = getMutableParameterConsensus(terminatingAddresses, graph, parameterIndex);
                int register = parameterRegisters[parameterIndex];
                mState.assignRegister(register, value);
            }
        }

        for (String currentClassName : classManager.getLocalClasses()) {
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
                Object value = graph.getFieldConsensus(terminatingAddresses, currentClassName, fieldNameAndType);
                currentClassState.pokeField(fieldNameAndType, value);
            }
        }
    }

    private void inheritClassStates(ExecutionContext parent, ExecutionContext child) {
        for (String className : classManager.getLocalClasses()) {
            if (!parent.isClassInitialized(className)) {
                continue;
            }

            ClassState fromClassState = parent.peekClassState(className);
            ClassState toClassState = new ClassState(fromClassState, child);
            for (String fieldNameAndType : classManager.getFieldNameAndTypes(className)) {
                Object value = fromClassState.peekField(fieldNameAndType);
                toClassState.pokeField(fieldNameAndType, value);
            }
            SideEffect.Level level = parent.getClassStateSideEffectLevel(className);
            child.initializeClass(className, toClassState, level);
        }
    }

}
