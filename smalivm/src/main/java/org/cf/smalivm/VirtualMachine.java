package org.cf.smalivm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.context.ClassContext;
import org.cf.smalivm.context.ClassContextMap;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.context.ContextNode;
import org.cf.smalivm.context.MethodContext;
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

    private static MethodContext buildRootMethodContext(String methodDescriptor, int accessFlags, int registerCount,
                    List<String> parameterTypes) {
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
        int parameterCount = getParameterSize(parameterTypes);
        MethodContext result = new MethodContext(registerCount, parameterCount, 0);
        for (int parameterIndex = 0; parameterIndex < parameterTypes.size(); parameterIndex++) {
            String type = parameterTypes.get(parameterIndex);
            Object value = (!isStatic && (parameterIndex == 0)) ? new LocalInstance(type) : new UnknownValue(type);
            result.assignParameter(parameterIndex, value);
        }

        return result;
    }

    private static String getClassNameFromMethodDescriptor(String methodDescriptor) {
        return methodDescriptor.split("->", 2)[0];
    }

    public static int getParameterSize(List<String> parameterTypes) {
        int result = 0;
        for (String type : parameterTypes) {
            result += type.equals("J") || type.equals("D") ? 2 : 1;
        }

        return result;
    }

    private final Map<String, ClassContext> classNameToClassContext;
    private final Set<String> localClasses;

    private final int maxNodeVisits;
    private final int maxCallDepth;
    private final Map<String, BuilderMethod> methodDescriptorToBuilderMethod;
    private final Map<String, ContextGraph> methodDescriptorToInstructionGraph;
    private final Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> methodDescriptorToTryCatchList;
    private final MethodExecutor methodExecutor;

    public VirtualMachine(String path) throws Exception {
        this(path, 2000, 20);
    }

    public VirtualMachine(String path, int maxNodeVisits, int maxCallDepth) throws Exception {
        this(Dexifier.dexifySmaliFiles(path), maxNodeVisits, maxCallDepth);
    }

    public VirtualMachine(List<BuilderClassDef> classDefs, int maxNodeVisits, int maxCallDepth) {
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;

        methodDescriptorToTryCatchList = buildMethodDescriptorToTryCatchList(classDefs);
        methodDescriptorToBuilderMethod = buildMethodDescriptorToBuilderMethod(classDefs);
        classNameToClassContext = buildClassNameToClassContext(classDefs);
        localClasses = new HashSet<String>(classNameToClassContext.keySet());
        methodExecutor = new MethodExecutor(this);

        // Build graphs last because that's when ops are created and they may access the VM.
        methodDescriptorToInstructionGraph = buildMethodDescriptorToInstructionGraph(classDefs);
    }

    // For testing
    public void setupClassContext(Map<String, Map<String, Object>> classNameToInitialFieldValue) {
        for (String contextClassName : classNameToInitialFieldValue.keySet()) {
            ClassContext cctx = getStaticClassContext(contextClassName);
            Map<String, Object> fieldNameToValue = classNameToInitialFieldValue.get(contextClassName);
            for (String fieldReference : fieldNameToValue.keySet()) {
                Object value = fieldNameToValue.get(fieldReference);
                cctx.pokeField(fieldReference, value);
            }
        }
    }

    public ContextGraph execute(String methodDescriptor) {
        MethodContext mctx = buildRootMethodContext(methodDescriptor);

        return execute(methodDescriptor, mctx);
    }

    public ContextGraph execute(String methodDescriptor, MethodContext mctx) {
        ClassContextMap classContextMap = new ClassContextMap();

        return execute(methodDescriptor, mctx, classContextMap);
    }

    public ContextGraph execute(String methodDescriptor, MethodContext mctx, ClassContextMap classContextMap) {
        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        staticallyInitializeClassIfNecessary(className, classContextMap);

        ContextGraph result = null;
        try {
            result = methodExecutor.execute(methodDescriptor, mctx, classContextMap);
        } catch (MaxCallDepthExceeded | MaxNodeVisitsExceeded e) {
            log.warn(e.toString());
        } catch (Exception e) {
            log.warn("Unhandled exception in " + methodDescriptor + ". Giving up on this method.");
            log.debug("Stack trace: ", e);
        }

        return result;
    }

    public void setClassContext(String className, ClassContext ctx) {
        classNameToClassContext.put(className, ctx);
    }

    ClassContext getStaticClassContext(String className) {
        if (!classNameToClassContext.containsKey(className)) {
            log.warn("Peeking context of non-local class: " + className + ". Returning empty context.");
            ClassContext ctx = new ClassContext(0);
            classNameToClassContext.put(className, ctx);

            return ctx;
        }

        return classNameToClassContext.get(className);
    }

    public ContextGraph getInstructionGraphClone(String methodDescriptor) {
        ContextGraph graph = methodDescriptorToInstructionGraph.get(methodDescriptor);
        ContextGraph result = new ContextGraph(graph);

        return result;
    }

    public int getMaxCallDepth() {
        return maxCallDepth;
    }

    public int getMaxNodeVisits() {
        return maxNodeVisits;
    }

    public Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> getMethodToTryCatchList() {
        return methodDescriptorToTryCatchList;
    }

    public boolean isClassDefinedLocally(String className) {
        return localClasses.contains(className);
    }

    public boolean isMethodDefined(String methodDescriptor) {
        return methodDescriptorToInstructionGraph.containsKey(methodDescriptor);
    }

    public void updateInstructionGraph(String methodDescriptor) {
        BuilderMethod method = methodDescriptorToBuilderMethod.get(methodDescriptor);
        ContextGraph graph = new ContextGraph(this, method);
        MethodContext mctx = buildRootMethodContext(methodDescriptor);
        graph.getRootNode().setMethodContext(mctx);
        methodDescriptorToInstructionGraph.put(methodDescriptor, graph);
    }

    private Map<String, ClassContext> buildClassNameToClassContext(List<BuilderClassDef> classDefs) {
        Map<String, ClassContext> result = new HashMap<String, ClassContext>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            // Build out context with all fields so they can be enumerated later.
            // This context should be cloned and never changed.
            ClassContext cctx = new ClassContext(classDef.getFields().size());
            for (BuilderField field : classDef.getFields()) {
                String fieldDescriptor = ReferenceUtil.getFieldDescriptor(field);
                String fieldNameAndType = fieldDescriptor.split("->")[1];
                String type = fieldNameAndType.split(":")[1];
                cctx.pokeField(fieldNameAndType, new UnknownValue(type));
            }

            String className = ReferenceUtil.getReferenceString(classDef);
            result.put(className, cctx);
        }

        return result;
    }

    private Map<String, BuilderMethod> buildMethodDescriptorToBuilderMethod(List<BuilderClassDef> classDefs) {
        Map<String, BuilderMethod> result = new HashMap<String, BuilderMethod>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                result.put(methodDescriptor, method);
            }
        }

        return result;
    }

    private Map<String, ContextGraph> buildMethodDescriptorToInstructionGraph(final List<BuilderClassDef> classDefs) {
        Map<String, ContextGraph> result = new HashMap<String, ContextGraph>(classDefs.size());
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                ContextGraph graph = new ContextGraph(this, method);
                ContextNode rootNode = graph.getRootNode();
                MethodContext mctx = buildRootMethodContext(methodDescriptor);
                rootNode.setMethodContext(mctx);
                result.put(methodDescriptor, graph);
            }
        }

        return result;
    }

    public MethodContext buildRootMethodContext(String methodDescriptor) {
        BuilderMethod method = getBuilderMethod(methodDescriptor);
        int accessFlags = method.getAccessFlags();
        int registerCount = method.getImplementation().getRegisterCount();
        List<String> parameterTypes = Utils.getParameterTypes(methodDescriptor);

        return buildRootMethodContext(methodDescriptor, accessFlags, registerCount, parameterTypes);
    }

    public Set<String> getMethodDescriptors() {
        return methodDescriptorToBuilderMethod.keySet();
    }

    private BuilderMethod getBuilderMethod(String methodDescriptor) {
        return methodDescriptorToBuilderMethod.get(methodDescriptor);
    }

    public void staticallyInitializeClassIfNecessary(String className, ClassContextMap classContextMap) {
        // This method should be called when a class is first used. A usage is:
        // 1.) The invocation of a method declared by the class (not inherited from a superclass)
        // 2.) The invocation of a constructor of the class (covered by #1)
        // 3.) The use or assignment of a field declared by a class (not inherited from a superclass), except for fields
        // that are both static and final, and are initialized by a compile-time constant expression.

        if (classContextMap.isClassInitialized(className)) {
            return;
        }
        ClassContext cctx = new ClassContext(classNameToClassContext.get(className));
        classContextMap.setClassContext(className, cctx);

        String clinitDescriptor = className + "-><clinit>()V";
        if (methodDescriptorToBuilderMethod.containsKey(clinitDescriptor)) {
            ContextGraph graph = execute(clinitDescriptor);
            if (graph == null) {
                // Error executing. Assume the worst.
                classContextMap.setSideEffectType(className, SideEffect.Type.STRONG);
            } else {
                // Need to collapse class context multiverse into consensus
                // build new cctx for every class defined at terminating addresses
                // if a class is not initialized at every terminating address, all unknown
                // else
                // for every field in class, set cctx to consensus
                // TODO: !!! graph.getFieldConsensus
                SideEffect.Type sideEffectType = graph.getStrongestSideEffectType();
                classContextMap.setSideEffectType(className, sideEffectType);
            }
        } else {
            // No clinit for this class.
        }
    }
}
