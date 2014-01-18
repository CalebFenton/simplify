package refactor.exec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;

import simplify.Main;
import simplify.exec.MaxNodeVisitsExceeded;

public class VirtualMachine {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final Map<String, ContextGraph> methodInstructionGraphs;
    private final Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> methodToTryCatchList;
    private final List<ClassExecutionContext> clinitContexts;
    private final MethodExecutor methodExecutor;
    private final List<String> enteredClasses;
    private final int maxNodeVisits;
    private final int maxCallDepth;

    public VirtualMachine(List<BuilderClassDef> classDefs, int maxNodeVisits, int maxCallDepth) {
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;

        methodInstructionGraphs = buildInstructionGraphs(classDefs);

        methodToTryCatchList = buildTryCatchList(classDefs);

        methodExecutor = new MethodExecutor(this);

        clinitContexts = new ArrayList<ClassExecutionContext>(classDefs.size());
        enteredClasses = new ArrayList<String>(classDefs.size());
    }

    public ContextGraph execute(String methodDescriptor) throws MaxNodeVisitsExceeded {
        clinitMethodClassIfNecessary(methodDescriptor);

        ContextGraph result = methodExecutor.execute(methodDescriptor);

        return result;
    }

    ContextGraph getInstructionGraph(String methodDescriptor) {
        ContextGraph result = new ContextGraph(methodInstructionGraphs.get(methodDescriptor));

        return result;
    }

    public Map<String, List<? extends TryBlock<? extends ExceptionHandler>>> getMethodToTryCatchList() {
        return methodToTryCatchList;
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

    private void clinitMethodClassIfNecessary(String methodDescriptor) {
        String className = getClassNameFromMethodDescriptor(methodDescriptor);
        if (enteredClasses.contains(className)) {
            return;
        }
        enteredClasses.add(className);

        String clinitDescriptor = className + "-><clinit>()V";
        if (!methodInstructionGraphs.containsKey(clinitDescriptor)) {
            // No clinit for this class
            return;
        }

        ContextGraph clinitGraph = null;
        try {
            clinitGraph = execute(clinitDescriptor);
        } catch (MaxNodeVisitsExceeded ex) {
            log.warning("Node visits exceeded  " + clinitDescriptor + ", skipping.\n" + ex.getMessage());
            ex.printStackTrace();
        }

        ClassExecutionContext ctx = buildClassContext(clinitGraph);
        clinitContexts.add(ctx);
    }

    private ClassExecutionContext buildClassContext(ContextGraph graph) {
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

    private static MethodExecutionContext buildRootContext(BuilderMethod method) {
        method.getParameters();
        // TODO: implement
        return null;
    }
}
