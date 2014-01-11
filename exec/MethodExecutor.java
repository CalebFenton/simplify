package simplify.exec;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.util.SparseArray;

import simplify.Simplifier;
import simplify.graph.InstructionNode;

import com.google.common.collect.LinkedListMultimap;

public class MethodExecutor {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private final int maxNodeVisits;
    private final int maxCallDepth;

    public MethodExecutor(int maxNodeVisits, int maxCallDepth) {
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;
    }

    public LinkedListMultimap<Integer, InstructionNode> execute(BuilderMethod method) throws MaxNodeVisitsExceeded {
        ExecutionContext ectx = new ExecutionContext();
        // List<? extends BuilderMethodParameter> parameters = method.getParameters();
        int paramIndexStop = method.getImplementation().getRegisterCount();
        int paramIndexStart = paramIndexStop - method.getParameters().size();
        for (int i = paramIndexStart; i < paramIndexStop; i++) {
            // TODO: we could get register type here by looking at method.getParameters()
            ectx.addRegister(i, "?", new UnknownValue(), -1);
        }

        return execute(method, ectx);
    }

    protected LinkedListMultimap<Integer, InstructionNode> execute(BuilderMethod method, ExecutionContext ectx)
                    throws MaxNodeVisitsExceeded {
        log.fine("Executing method: " + method.getName());

        LinkedListMultimap<Integer, InstructionNode> nodes = LinkedListMultimap.create();
        List<BuilderInstruction> instructions = ((MutableMethodImplementation) method.getImplementation())
                        .getInstructions();
        if (instructions.size() == 0) {
            return nodes;
        }

        SparseArray<BuilderInstruction> instructionOffsets = getInstructionOffsets(instructions);
        InstructionNode rootNode = new InstructionNode(instructions.get(0), ectx);
        Deque<InstructionNode> executeStack = new ArrayDeque<InstructionNode>();
        executeStack.push(rootNode);

        while (executeStack.peek() != null) {
            InstructionNode currentNode = executeStack.poll();
            List<Integer> childOffsets = InstructionExecutor.execute(currentNode);

            int idx = currentNode.getInstruction().getLocation().getIndex();
            nodes.put(idx, currentNode);

            if (nodes.get(idx).size() > maxNodeVisits) {
                throw new MaxNodeVisitsExceeded("Node: " + currentNode);
            }

            for (int childOffset : childOffsets) {
                if (childOffset == -1) {
                    childOffset = instructions.get(idx + 1).getLocation().getCodeAddress();
                }

                InstructionNode child = new InstructionNode(instructionOffsets.get(childOffset), currentNode
                                .getContext().clone());
                currentNode.addChild(child);
                executeStack.push(child);
            }
        }

        System.out.println(rootNode.toGraph());

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
