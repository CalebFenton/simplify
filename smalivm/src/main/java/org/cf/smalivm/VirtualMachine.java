package org.cf.smalivm;

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
import org.cf.smalivm.exception.MaxNodeVisitsExceeded;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Dexifier;
import org.cf.util.Utils;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.jf.dexlib2.writer.builder.BuilderMethod;
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
    private final int maxNodeVisits;
    private final Map<String, BuilderMethod> methodDescriptorToBuilderMethod;
    private final Map<String, ExecutionGraph> methodDescriptorToTemplateContextGraph;
    private final Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> methodDescriptorToTryCatchList;
    private final Map<String, List<String>> classNameToFieldNameAndType;
    private final MethodExecutor methodExecutor;

    private final ExecutionContext templateExecutionContext;

    public VirtualMachine(List<BuilderClassDef> classDefs, int maxNodeVisits, int maxCallDepth) {
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;

        localClasses = buildLocalClasses(classDefs);
        methodDescriptorToBuilderMethod = buildMethodDescriptorToBuilderMethod(classDefs);
        methodDescriptorToTryCatchList = buildMethodDescriptorToTryCatchList(classDefs);
        classNameToFieldNameAndType = new HashMap<String, List<String>>();
        templateExecutionContext = buildTemplateExecutionContext(classDefs);
        methodExecutor = new MethodExecutor(this);

        // Build graphs last because that's when ops are created and they may access the VM.
        methodDescriptorToTemplateContextGraph = new HashMap<String, ExecutionGraph>();
        buildMethodDescriptorToTemplateContextGraph(classDefs);
    }

    public VirtualMachine(String path) throws Exception {
        this(path, 2000, 20);
    }

    public VirtualMachine(String path, int maxNodeVisits, int maxCallDepth) throws Exception {
        this(Dexifier.dexifySmaliFiles(path), maxNodeVisits, maxCallDepth);
    }

    public ExecutionGraph execute(String methodDescriptor) {
        ExecutionContext ectx = getRootExecutionContext(methodDescriptor);

        return execute(methodDescriptor, ectx);
    }

    public ExecutionGraph execute(String methodDescriptor, ExecutionContext ectx) {
        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        ectx.staticallyInitializeClassIfNecessary(className);

        ExecutionGraph graph = getInstructionGraphClone(methodDescriptor);
        ExecutionNode rootNode = new ExecutionNode(graph.getRoot());
        rootNode.setContext(ectx);
        graph.addNode(rootNode);

        ExecutionGraph result = null;
        try {
            result = methodExecutor.execute(graph);
        } catch (MaxCallDepthExceeded | MaxNodeVisitsExceeded e) {
            log.warn(e.toString());
        } catch (Exception e) {
            log.warn("Unhandled exception in " + methodDescriptor + ". Giving up on this method.");
            log.debug("Stack trace: ", e);
        }

        return result;
    }

    public ExecutionGraph getInstructionGraphClone(String methodDescriptor) {
        ExecutionGraph graph = methodDescriptorToTemplateContextGraph.get(methodDescriptor);
        ExecutionGraph clone = new ExecutionGraph(graph);

        return clone;
    }

    public int getMaxCallDepth() {
        return maxCallDepth;
    }

    public int getMaxNodeVisits() {
        return maxNodeVisits;
    }

    public Set<String> getMethodDescriptors() {
        return methodDescriptorToBuilderMethod.keySet();
    }

    public Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> getMethodToTryCatchList() {
        return methodDescriptorToTryCatchList;
    }

    public ExecutionContext getRootExecutionContext(String methodDescriptor) {
        BuilderMethod method = getBuilderMethod(methodDescriptor);
        int accessFlags = method.getAccessFlags();
        int registerCount = method.getImplementation().getRegisterCount();
        List<String> parameterTypes = Utils.getParameterTypes(methodDescriptor);

        boolean isStatic = ((accessFlags & AccessFlags.STATIC.getValue()) != 0);
        if (!isStatic) {
            // Append instance reference to p0
            List<String> lst = new ArrayList<String>();
            String classDescriptor = methodDescriptor.split("->")[0];
            lst.add(classDescriptor);
            lst.addAll(parameterTypes);
            parameterTypes = lst;
        }

        // TODO: store parameter types for they can be looked up by invokeop instead of carried around

        // Assume all input values are unknown.
        ExecutionContext result = new ExecutionContext(templateExecutionContext);
        int parameterCount = getParameterSize(parameterTypes);
        MethodState mState = new MethodState(result, registerCount, parameterCount);
        for (int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++) {
            String type = parameterTypes.get(parameterIndex);
            Object value = (!isStatic && (parameterIndex == 0)) ? new LocalInstance(type) : new UnknownValue(type);
            mState.assignParameter(parameterIndex, value);
        }
        result.setMethodState(mState);

        return result;
    }

    public boolean isLocalClass(String className) {
        return localClasses.contains(className);
    }

    public List<String> getLocalClasses() {
        String[] classNames = localClasses.toArray(new String[localClasses.size()]);

        return Arrays.asList(classNames);
    }

    public boolean isLocalMethod(String methodDescriptor) {
        return methodDescriptorToTemplateContextGraph.containsKey(methodDescriptor);
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
