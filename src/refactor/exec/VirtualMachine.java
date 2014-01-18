package refactor.exec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;


public class VirtualMachine {

    private final Map<String, InstructionGraph> methodInstructionGraphs;
    private final MethodExecutor methodExecutor;

    public VirtualMachine(List<BuilderClassDef> classDefs) {
        methodInstructionGraphs = buildInstructionGraphs(classDefs);

        methodExecutor = new MethodExecutor(methodInstructionGraphs);
    }

    public Map<String, InstructionGraph> execute() {
        for (String methodDescriptor : methodInstructionGraphs.keySet()) {
            methodExecutor.execute(methodDescriptor);
        }

        return methodInstructionGraphs;
    }

    private Map<String, InstructionGraph> buildInstructionGraphs(final List<BuilderClassDef> classdDefs) {
        Map<String, InstructionGraph> result = new HashMap<String, InstructionGraph>();

        for (BuilderClassDef classDef : classdDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                InstructionGraph graph = new InstructionGraph(this, method);

                // TODO: set root context
                result.put(methodDescriptor, graph);
            }
        }
        // set context

        return result;
    }

    private MethodExecutionContext buildRootContext(BuilderMethod method) {
        method.getParameters();
        // static, etc.
        return null;
    }
}
