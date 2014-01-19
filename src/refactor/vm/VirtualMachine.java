package refactor.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.jf.dexlib2.writer.builder.BuilderMethod;

import simplify.Main;
import simplify.exec.MaxNodeVisitsExceeded;

public class VirtualMachine {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final Map<String, ContextGraph> methodInstructionGraphs;
    private final Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> methodToTryCatchList;
    private final Map<String, ClassContext> classNameToClassContext;
    private final MethodExecutor methodExecutor;
    private final int maxNodeVisits;
    private final int maxCallDepth;
    private final List<String> initializedClasses;

    public static final int ContinueNextInstruction = -1;

    public VirtualMachine(List<BuilderClassDef> classDefs, int maxNodeVisits, int maxCallDepth) {
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;

        methodInstructionGraphs = buildInstructionGraphs(classDefs);

        classNameToClassContext = buildNameToClassContext(classDefs);
        initializedClasses = new ArrayList<String>(classDefs.size());

        methodToTryCatchList = buildTryCatchList(classDefs);

        methodExecutor = new MethodExecutor(this);
    }

    public ContextGraph execute(String methodDescriptor) throws MaxNodeVisitsExceeded {
        // Invoking a method (including <init>) is a reason to statually initialize a class.
        staticallyInitializeMethodClassIfNecessary(methodDescriptor);

        ContextGraph result = methodExecutor.execute(methodDescriptor);

        return result;
    }

    private Map<String, ClassContext> buildNameToClassContext(List<BuilderClassDef> classDefs) {
        Map<String, ClassContext> result = new HashMap<String, ClassContext>(classDefs.size());

        for (BuilderClassDef classDef : classDefs) {
            for (BuilderField field : classDef.getFields()) {
                // TODO: implement this and class contexts, make sure array is totally populated
            }
        }

        return result;
    }

    ContextGraph getInstructionGraph(String methodDescriptor) {
        ContextGraph result = new ContextGraph(methodInstructionGraphs.get(methodDescriptor));

        return result;
    }

    public Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> getMethodToTryCatchList() {
        return methodToTryCatchList;
    }

    public ClassContext getClassExecutionContext(String methodDescriptor) {
        // Since this is called for the use or assignment of a class' field, clinit the class
        staticallyInitializeMethodClassIfNecessary(methodDescriptor);

        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        return classNameToClassContext.get(className);
    }

    private static Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> buildTryCatchList(
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

    void staticallyInitializeMethodClassIfNecessary(String methodDescriptor) {
        // This method should be called when a class is first used. A usage is:
        // 1.) The invocation of a method declared by the class (not inherited from a superclass)
        // 2.) The invocation of a constructor of the class (covered by #1)
        // 3.) The use or assignment of a field declared by a class (not inherited from a superclass), except for fields
        // that are both static and final, and are initialized by a compile-time constant expression.

        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        if (initializedClasses.contains(className)) {
            return;
        }
        initializedClasses.add(className);

        String clinitDescriptor = className + "-><clinit>()V";
        if (!methodInstructionGraphs.containsKey(clinitDescriptor)) {
            // No clinit for this class
            return;
        }

        try {
            // Don't need the graph. get/put methods will be setting up the class contexts.
            execute(clinitDescriptor);
        } catch (MaxNodeVisitsExceeded ex) {
            log.warning("Node visits exceeded  " + clinitDescriptor + ", skipping.\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private ClassContext buildClassContext(ContextGraph graph) {
        // graph may be null!
        // TODO Auto-generated method stub
        return null;
    }

    private static String getClassNameFromMethodDescriptor(String methodDescriptor) {
        return methodDescriptor.split("->", 2)[0];
    }

    private Map<String, ContextGraph> buildInstructionGraphs(final List<BuilderClassDef> classDefs) {
        Map<String, ContextGraph> result = new HashMap<String, ContextGraph>();

        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                ContextGraph graph = new ContextGraph(this, method);

                graph.setRootContext(buildRootContext(method));

                result.put(methodDescriptor, graph);
            }
        }

        return result;
    }

    private static MethodContext buildRootContext(BuilderMethod method) {
        method.getParameters();
        // TODO: implement
        return null;
    }
}
