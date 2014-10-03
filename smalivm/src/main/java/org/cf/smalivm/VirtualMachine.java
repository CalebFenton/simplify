package org.cf.smalivm;

import gnu.trove.list.TIntList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.TypeUtil;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Dexifier;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderMethodParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachine {

    private static final Logger log = LoggerFactory.getLogger(VirtualMachine.class.getSimpleName());

    public static int getParameterSize(List<String> parameterTypes) {
        int result = 0;
        for (String type : parameterTypes) {
            result += type.equals("J") || type.equals("D") ? 2 : 1;
        }

        return result;
    }

    private static Set<String> buildLocalClasses(List<BuilderClassDef> classDefs) {
        Set<String> result = new HashSet<String>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            String className = ReferenceUtil.getReferenceString(classDef);
            result.add(className);
        }

        return result;
    }

    private static Map<String, BuilderMethod> buildMethodDescriptorToBuilderMethod(List<BuilderClassDef> classDefs) {
        Map<String, BuilderMethod> result = new HashMap<String, BuilderMethod>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                result.put(methodDescriptor, method);
            }
        }

        return result;
    }

    private static Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> buildMethodDescriptorToTryCatchList(
                    List<BuilderClassDef> classDefs) {
        Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> result = new HashMap<String, List<? extends TryBlock<? extends ExceptionHandler>>>();
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                result.put(methodDescriptor, method.getImplementation().getTryBlocks());
            }
        }

        return result;

    }

    private static String getClassNameFromMethodDescriptor(String methodDescriptor) {
        return methodDescriptor.split("->", 2)[0];
    }

    private final Set<String> localClasses;
    private final int maxCallDepth;
    private final int maxAddressVisits;
    private final int maxMethodVisits;
    private final Map<String, BuilderMethod> methodDescriptorToBuilderMethod;
    private final Map<String, List<String>> methodDescriptorToParameterTypes;
    private final Map<String, ExecutionGraph> methodDescriptorToTemplateContextGraph;
    private final Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> methodDescriptorToTryCatchList;
    private final Map<String, List<String>> classNameToFieldNameAndType;
    private final MethodExecutor methodExecutor;

    private final ExecutionContext templateExecutionContext;

    public VirtualMachine(List<BuilderClassDef> classDefs, int maxAddressVisits, int maxCallDepth, int maxMethodVisits) {
        this.maxAddressVisits = maxAddressVisits;
        this.maxMethodVisits = maxMethodVisits;
        this.maxCallDepth = maxCallDepth;

        localClasses = buildLocalClasses(classDefs);
        methodDescriptorToBuilderMethod = buildMethodDescriptorToBuilderMethod(classDefs);
        methodDescriptorToParameterTypes = buildMethodDescriptorToParameterTypes(classDefs);
        methodDescriptorToTryCatchList = buildMethodDescriptorToTryCatchList(classDefs);
        classNameToFieldNameAndType = new HashMap<String, List<String>>();
        templateExecutionContext = buildTemplateExecutionContext(classDefs);
        methodExecutor = new MethodExecutor(this);

        // Build graphs last because that's when ops are created and they may access the VM.
        methodDescriptorToTemplateContextGraph = new HashMap<String, ExecutionGraph>();
        buildMethodDescriptorToTemplateContextGraph(classDefs);
    }

    private static Map<String, List<String>> buildMethodDescriptorToParameterTypes(List<BuilderClassDef> classDefs) {
        Map<String, List<String>> result = new HashMap<String, List<String>>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                List<? extends BuilderMethodParameter> builderParameters = method.getParameters();
                List<String> parameterTypes = new ArrayList<String>(builderParameters.size());
                for (BuilderMethodParameter builderParameter : builderParameters) {
                    parameterTypes.add(builderParameter.getType());
                }

                int accessFlags = method.getAccessFlags();
                boolean isStatic = ((accessFlags & AccessFlags.STATIC.getValue()) != 0);
                if (!isStatic) {
                    // First "parameter" for non-static methods is instance ref
                    parameterTypes.add(0, method.getDefiningClass());
                }

                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                result.put(methodDescriptor, parameterTypes);
            }
        }

        return result;
    }

    public VirtualMachine(String path) throws Exception {
        this(path, 1000, 20, 1000 * 1000);
    }

    public VirtualMachine(String path, int maxAddressVisits, int maxCallDepth, int maxMethodVisits) throws Exception {
        this(Dexifier.dexifySmaliFiles(path), maxAddressVisits, maxCallDepth, maxMethodVisits);
    }

    public ExecutionGraph execute(String methodDescriptor) {
        ExecutionContext ectx = getRootExecutionContext(methodDescriptor);

        return execute(methodDescriptor, ectx);
    }

    public ExecutionGraph execute(String methodDescriptor, ExecutionContext ectx) {
        return execute(methodDescriptor, ectx, null, null);
    }

    private void mergeClassStates(ExecutionContext mergeFrom, ExecutionContext mergeTo) {
        for (String className : getLocalClasses()) {
            if (!mergeFrom.isClassInitialized(className)) {
                continue;
            }

            ClassState fromClassState = mergeFrom.peekClassState(className);
            ClassState toClassState = new ClassState(fromClassState, mergeTo);
            for (String fieldNameAndType : getFieldNameAndTypes(className)) {
                Object value = fromClassState.peekField(fieldNameAndType);
                toClassState.pokeField(fieldNameAndType, value);
            }
            SideEffect.Level level = mergeFrom.getClassStateSideEffectLevel(className);
            mergeTo.initializeClass(className, toClassState, level);
        }
    }

    public ExecutionGraph execute(String methodDescriptor, ExecutionContext calleeContext,
                    ExecutionContext callerContext, int[] parameterRegisters) {
        if (callerContext != null) {
            mergeClassStates(callerContext, calleeContext);
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
            log.warn(e.toString());
        } catch (Exception e) {
            log.warn("Unhandled exception in " + methodDescriptor + ". Giving up on this method.");
            if (log.isDebugEnabled()) {
                log.debug("Stack trace: ", e);
            }
        }

        if ((result != null) && (callerContext != null)) {
            collapseMultiverse(methodDescriptor, graph, calleeContext, callerContext, parameterRegisters);
        }

        return result;
    }

    /*
     * Get consensus for method and class states for all execution paths and merge them into callerContext.
     */
    private void collapseMultiverse(String methodDescriptor, ExecutionGraph graph, ExecutionContext calleeContext,
                    ExecutionContext callerContext, int[] parameterRegisters) {
        TIntList terminatingAddresses = graph.getConnectedTerminatingAddresses();
        if (parameterRegisters != null) {
            MethodState mState = callerContext.getMethodState();
            List<String> parameterTypes = getParameterTypes(methodDescriptor);
            for (int parameterIndex = 0; parameterIndex < parameterRegisters.length; parameterIndex++) {
                String type = parameterTypes.get(parameterIndex);
                boolean mutable = !SmaliClassUtils.isImmutableClass(type);
                if (!mutable) {
                    continue;
                }

                Object value = getMutableParameterConsensus(terminatingAddresses, graph, parameterIndex);
                int register = parameterRegisters[parameterIndex];
                mState.assignRegister(register, value);
            }
        }

        for (String currentClassName : getLocalClasses()) {
            List<String> fieldNameAndTypes = getFieldNameAndTypes(currentClassName);
            for (String fieldNameAndType : fieldNameAndTypes) {
                Object value = graph.getFieldConsensus(terminatingAddresses, currentClassName, fieldNameAndType);
                ClassState currentClassState;
                if (callerContext.isClassInitialized(currentClassName)) {
                    currentClassState = callerContext.peekClassState(currentClassName);
                } else {
                    currentClassState = new ClassState(callerContext, currentClassName, fieldNameAndTypes.size());
                    SideEffect.Level level = graph.getHighestClassSideEffectLevel(currentClassName);
                    callerContext.initializeClass(currentClassName, currentClassState, level);
                }
                currentClassState.pokeField(fieldNameAndType, value);
            }
        }
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

    public ExecutionGraph getInstructionGraphClone(String methodDescriptor) {
        ExecutionGraph graph = methodDescriptorToTemplateContextGraph.get(methodDescriptor);
        ExecutionGraph clone = new ExecutionGraph(graph);

        return clone;
    }

    public int getMaxCallDepth() {
        return maxCallDepth;
    }

    public int getMaxAddressVisits() {
        return maxAddressVisits;
    }

    public int getMaxMethodVisits() {
        return maxMethodVisits;
    }

    public Set<String> getMethodDescriptors() {
        return methodDescriptorToBuilderMethod.keySet();
    }

    public Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> getMethodToTryCatchList() {
        return methodDescriptorToTryCatchList;
    }

    public List<String> getParameterTypes(String methodDescriptor) {
        return methodDescriptorToParameterTypes.get(methodDescriptor);
    }

    public ExecutionContext getRootExecutionContext(String methodDescriptor) {
        BuilderMethod method = getBuilderMethod(methodDescriptor);
        int registerCount = method.getImplementation().getRegisterCount();
        List<String> parameterTypes = getParameterTypes(methodDescriptor);
        int parameterSize = getParameterSize(parameterTypes);

        int accessFlags = method.getAccessFlags();
        boolean isStatic = ((accessFlags & AccessFlags.STATIC.getValue()) != 0);

        // Assume all input values are unknown.
        ExecutionContext rootContext = new ExecutionContext(templateExecutionContext);
        MethodState mState = new MethodState(rootContext, registerCount, parameterTypes.size(), parameterSize);
        for (int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++) {
            String type = parameterTypes.get(parameterIndex);
            Object value = (!isStatic && (parameterIndex == 0)) ? new LocalInstance(type) : new UnknownValue(type);
            mState.assignParameter(parameterIndex, value);
        }
        rootContext.setMethodState(mState);

        return rootContext;
    }

    public boolean isLocalClass(String className) {
        return localClasses.contains(className);
    }

    public List<String> getLocalClasses() {
        String[] classNames = localClasses.toArray(new String[localClasses.size()]);

        return Arrays.asList(classNames);
    }

    public boolean isLocalMethod(String methodDescriptor) {
        return methodDescriptorToBuilderMethod.containsKey(methodDescriptor);
    }

    public void updateInstructionGraph(String methodDescriptor) {
        BuilderMethod method = methodDescriptorToBuilderMethod.get(methodDescriptor);
        ExecutionGraph graph = new ExecutionGraph(this, method);
        methodDescriptorToTemplateContextGraph.put(methodDescriptor, graph);
    }

    private void buildMethodDescriptorToTemplateContextGraph(final List<BuilderClassDef> classDefs) {
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                updateInstructionGraph(methodDescriptor);
            }
        }
    }

    private ExecutionContext buildTemplateExecutionContext(List<BuilderClassDef> classDefs) {
        ExecutionContext result = new ExecutionContext(this);
        for (BuilderClassDef classDef : classDefs) {
            // Build out context with all fields so they can be enumerated later.
            // This context should be cloned and never changed.
            String className = ReferenceUtil.getReferenceString(classDef);
            Collection<BuilderField> fields = classDef.getFields();
            List<String> fieldNameAndTypes = new LinkedList<String>();
            ClassState cState = new ClassState(result, className, fields.size());
            for (BuilderField field : classDef.getFields()) {
                String fieldDescriptor = ReferenceUtil.getFieldDescriptor(field);
                String fieldNameAndType = fieldDescriptor.split("->")[1];
                fieldNameAndTypes.add(fieldNameAndType);
                String type = fieldNameAndType.split(":")[1];
                cState.pokeField(fieldNameAndType, new UnknownValue(type));
            }
            classNameToFieldNameAndType.put(className, fieldNameAndTypes);
            result.setClassState(className, cState);
        }

        return result;
    }

    public List<String> getFieldNameAndTypes(String className) {
        return classNameToFieldNameAndType.get(className);
    }

    private BuilderMethod getBuilderMethod(String methodDescriptor) {
        return methodDescriptorToBuilderMethod.get(methodDescriptor);
    }

}
