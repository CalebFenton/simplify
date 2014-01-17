package simplify.exec;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.util.SparseArray;

import simplify.Main;
import simplify.graph.InstructionNode;

import com.google.common.collect.LinkedListMultimap;

public class MethodExecutor {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final int MAX_NODE_VISITS_DEFAULT = 10000;

    private final int maxNodeVisits;

    public MethodExecutor() {
        this(MAX_NODE_VISITS_DEFAULT);
    }

    public MethodExecutor(int maxNodeVisits) {
        this.maxNodeVisits = maxNodeVisits;
    }

    public LinkedListMultimap<Integer, InstructionNode> execute(List<BuilderClassDef> classes, BuilderMethod method,
                    int maxCallDepth) throws MaxNodeVisitsExceeded {

        MethodExecutionContext ectx = buildMethodContext(method, maxCallDepth);
        return execute(classes, method, ectx);
    }

    private MethodExecutionContext buildMethodContext(BuilderMethod method, int maxCallDepth) {
        int registerCount = method.getImplementation().getRegisterCount();
        int parameterCount = method.getParameters().size();
        MethodExecutionContext ectx = new MethodExecutionContext(registerCount, parameterCount, maxCallDepth);
        int paramIndexStop = method.getImplementation().getRegisterCount();
        int paramIndexStart = paramIndexStop - method.getParameters().size();

        // Non-static methods have p0 "this" reference
        // Just going to set it to unknown value, since put/get methods aren't ever going to work.
        boolean isStatic = (method.getAccessFlags() & AccessFlags.STATIC.getValue()) != 0;
        if (!isStatic) {
            paramIndexStart--;
        }

        System.out.println("method: " + method.getName() + " start: " + paramIndexStart + "  stop: " + paramIndexStop);
        for (int i = paramIndexStart; i < paramIndexStop; i++) {
            // TODO: we could get register type here by looking at method.getParameters()
            ectx.addRegister(i, "?", new UnknownValue(), -1);
        }

        return ectx;
    }

    public LinkedListMultimap<Integer, InstructionNode> execute(List<BuilderClassDef> classes, BuilderMethod method,
                    MethodExecutionContext ectx) throws MaxNodeVisitsExceeded {
        log.info("Executing method: " + method.getName());

        LinkedListMultimap<Integer, InstructionNode> nodes = LinkedListMultimap.create();
        MutableMethodImplementation methodImpl = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = methodImpl.getInstructions();

        if (instructions.size() == 0) {
            return nodes;
        }

        List<BuilderTryBlock> tryBlocks = methodImpl.getTryBlocks();
        SparseArray<BuilderInstruction> instructionOffsets = getInstructionOffsets(instructions);
        Deque<InstructionNode> executeStack = new ArrayDeque<InstructionNode>();
        executeStack.push(new InstructionNode(instructions.get(0), ectx));

        InstructionNode node;
        while ((node = executeStack.poll()) != null) {
            // Determining successors is kind of messy, so delegate to the instruction executor.
            List<Integer> childOffsets = InstructionExecutor.execute(classes, tryBlocks, node.getContext(),
                            node.getInstruction());

            int index = node.getInstruction().getLocation().getIndex();
            nodes.put(index, node);

            if (nodes.get(index).size() > maxNodeVisits) {
                throw new MaxNodeVisitsExceeded("Node: " + node);
            }

            for (int childOffset : childOffsets) {
                if (childOffset == -1) {
                    childOffset = instructions.get(index + 1).getLocation().getCodeAddress();
                }

                InstructionNode child = new InstructionNode(instructionOffsets.get(childOffset), node.getContext()
                                .clone());
                node.addChild(child);
                executeStack.push(child);
            }
        }

        // System.out.println(rootNode.toGraph());

        return nodes;
    }

    private static SparseArray<BuilderInstruction> getInstructionOffsets(List<BuilderInstruction> instructions) {
        SparseArray<BuilderInstruction> result = new SparseArray<BuilderInstruction>();

        int currentCodeOffset = 0;
        for (BuilderInstruction instruction : instructions) {
            result.append(currentCodeOffset, instruction);
            currentCodeOffset += instruction.getCodeUnits();
        }

        return result;
    }

}
