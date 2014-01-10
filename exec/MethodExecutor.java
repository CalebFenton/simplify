package simplify.exec;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.SwitchPayload;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.util.SparseArray;

import simplify.Simplifier;
import simplify.graph.InstructionNode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class MethodExecutor {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private final int maxNodeVisits;
    private final int maxCallDepth;

    public MethodExecutor(int maxNodeVisits, int maxCallDepth) {
        this.maxNodeVisits = maxNodeVisits;
        this.maxCallDepth = maxCallDepth;
    }

    public Multimap<Integer, InstructionNode> execute(BuilderMethod method) throws MaxNodeVisitsExceeded {
        return execute(method, new ExecutionContext());
    }

    protected Multimap<Integer, InstructionNode> execute(BuilderMethod method, ExecutionContext ectx)
                    throws MaxNodeVisitsExceeded {
        Multimap<Integer, InstructionNode> nodes = ArrayListMultimap.create();
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
            List<Integer> childOffsets = executeInstruction(currentNode);

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

    private List<Integer> executeInstruction(InstructionNode node) {
        log.fine("Executing instruction: " + node);

        List<Integer> childOffsets = new ArrayList<Integer>();
        BuilderInstruction instruction = node.getInstruction();
        if (instruction instanceof OffsetInstruction) {
            int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
            int codeAddress = instruction.getLocation().getCodeAddress();
            childOffsets.add(codeAddress + branchOffset);
        } else if (instruction instanceof SwitchPayload) {
            List<? extends SwitchElement> elements = ((SwitchPayload) instruction).getSwitchElements();
            for (SwitchElement element : elements) {
                childOffsets.add(element.getOffset());
            }
        }

        if (instruction.getOpcode().canContinue()) {
            childOffsets.add(-1);
        }

        return childOffsets;
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
