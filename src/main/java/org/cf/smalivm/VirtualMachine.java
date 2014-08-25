package org.cf.smalivm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.cf.simplify.Main;
import org.cf.smalivm.context.ClassContext;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxNodeVisitsExceeded;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Utils;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;

public class VirtualMachine {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

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

    private static MethodContext buildRootContext(String methodDescriptor, int accessFlags, int registerCount,
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
            result += type.equals("J") ? 2 : 1;
        }

        return result;
    }

    private final Map<String, ClassContext> classNameToClassContext;
    private final Map<String, SideEffect.Type> classNameToSideEffectType;
    private final List<String> initializedClasses;

    private final int maxNodeVisits;
    private final int maxCallDepth;
    private final Map<String, BuilderMethod> methodDescriptorToBuilderMethod;
    private final Map<String, ContextGraph> methodDescriptorToInstructionGraph;
    private final Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> methodDescriptorToTryCatchList;
    private final MethodExecutor methodExecutor;

    public VirtualMachine(List<BuilderClassDef> classDefs, int maxNodeVisits, int maxCallDepth) {
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;

        methodDescriptorToTryCatchList = buildMethodDescriptorToTryCatchList(classDefs);
        methodDescriptorToBuilderMethod = buildMethodDescriptorToBuilderMethod(classDefs);
        classNameToClassContext = buildClassNameToClassContext(classDefs);
        classNameToSideEffectType = new HashMap<String, SideEffect.Type>(classNameToClassContext.size());
        for (String className : classNameToClassContext.keySet()) {
            classNameToSideEffectType.put(className, SideEffect.Type.NONE);
        }

        methodExecutor = new MethodExecutor(this);

        // No classes have been initialized yet.
        initializedClasses = new ArrayList<String>(classDefs.size());

        // Build graphs last because that's when handlers are assigned and some handlers access this VM instance.
        methodDescriptorToInstructionGraph = buildMethodDescriptorToInstructionGraph(classDefs);
    }

    // For testing
    public void setupClassContext(Map<String, Map<String, Object>> classNameToInitialFieldValue) {
        for (String contextClassName : classNameToInitialFieldValue.keySet()) {
            ClassContext cctx = peekClassContext(contextClassName);
            Map<String, Object> fieldNameToValue = classNameToInitialFieldValue.get(contextClassName);
            for (String fieldReference : fieldNameToValue.keySet()) {
                Object value = fieldNameToValue.get(fieldReference);
                cctx.pokeField(fieldReference, value);
            }
        }
    }

    public ContextGraph execute(String methodDescriptor) {
        ContextGraph graph = methodDescriptorToInstructionGraph.get(methodDescriptor);
        MethodContext ctx = graph.getRootContext();

        return execute(methodDescriptor, ctx);
    }

    public ContextGraph execute(String methodDescriptor, MethodContext ctx) {
        // Invoking a method (including <init>) is a reason to statically initialize a class.
        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        staticallyInitializeClassIfNecessary(className);

        ContextGraph result = null;
        try {
            result = methodExecutor.execute(methodDescriptor, ctx);
        } catch (MaxNodeVisitsExceeded | MaxCallDepthExceeded e) {
            log.warning("Exceeded max node visits for " + e.getMessage() + " in " + methodDescriptor + "\nContext: "
                            + ctx);
        } catch (Exception e) {
            log.warning("Unhandled exception in " + methodDescriptor + ". Skipping!\n" + e);
            e.printStackTrace();
        }

        return result;
    }

    public void setClassContext(String className, ClassContext ctx) {
        classNameToClassContext.put(className, ctx);
    }

    public ClassContext readClassContext(String className) {
        // Since this is called for the use or assignment of a class' field, clinit the class
        staticallyInitializeClassIfNecessary(className);

        return peekClassContext(className);
    }

    public ClassContext peekClassContext(String className) {
        return classNameToClassContext.get(className);
    }

    public ContextGraph getInstructionGraph(String methodDescriptor) {
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
        return classNameToClassContext.get(className) != null;
    }

    public boolean isMethodDefined(String methodDescriptor) {
        return methodDescriptorToInstructionGraph.containsKey(methodDescriptor);
    }

    public void updateInstructionGraph(String methodDescriptor) {
        BuilderMethod method = methodDescriptorToBuilderMethod.get(methodDescriptor);
        ContextGraph graph = new ContextGraph(this, method);
        graph.setRootContext(buildRootContext(methodDescriptor));
        methodDescriptorToInstructionGraph.put(methodDescriptor, graph);
    }

    private Map<String, ClassContext> buildClassNameToClassContext(List<BuilderClassDef> classDefs) {
        Map<String, ClassContext> result = new HashMap<String, ClassContext>(classDefs.size());

        for (BuilderClassDef classDef : classDefs) {
            String className = ReferenceUtil.getReferenceString(classDef);

            // Context is initialized when it's needed.
            result.put(className, new ClassContext(classDef.getFields().size()));
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

                graph.setRootContext(buildRootContext(methodDescriptor));

                result.put(methodDescriptor, graph);
            }
        }

        return result;
    }

    private MethodContext buildRootContext(String methodDescriptor) {
        BuilderMethod method = getBuilderMethod(methodDescriptor);
        int accessFlags = method.getAccessFlags();
        int registerCount = method.getImplementation().getRegisterCount();
        List<String> parameterTypes = Utils.getParameterTypes(methodDescriptor);

        return buildRootContext(methodDescriptor, accessFlags, registerCount, parameterTypes);
    }

    public Set<String> getMethodDescriptors() {
        return methodDescriptorToBuilderMethod.keySet();
    }

    private BuilderMethod getBuilderMethod(String methodDescriptor) {
        return methodDescriptorToBuilderMethod.get(methodDescriptor);
    }

    public SideEffect.Type getClassStaticInitializerSideEffectType(String className) {
        return classNameToSideEffectType.get(className);
    }

    public void staticallyInitializeClassIfNecessary(String className) {
        // This method should be called when a class is first used. A usage is:
        // 1.) The invocation of a method declared by the class (not inherited from a superclass)
        // 2.) The invocation of a constructor of the class (covered by #1)
        // 3.) The use or assignment of a field declared by a class (not inherited from a superclass), except for fields
        // that are both static and final, and are initialized by a compile-time constant expression.

        if (initializedClasses.contains(className)) {
            return;
        }
        initializedClasses.add(className);

        String clinitDescriptor = className + "-><clinit>()V";
        if (methodDescriptorToBuilderMethod.containsKey(clinitDescriptor)) {
            // Any sput's recorded by op handler in ClassContext. Empty contexts built at VM initialization.
            ContextGraph graph = execute(clinitDescriptor);
            if (graph == null) {
                // Error executing. Assume the worst.
                classNameToSideEffectType.put(className, SideEffect.Type.STRONG);
            } else {
                SideEffect.Type sideEffectType = graph.getStrongestSideEffectType();
                classNameToSideEffectType.put(className, sideEffectType);
            }
        } else {
            // No clinit for this class.
        }
    }
}
