package refactor.exec;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.util.SparseArray;

import refactor.op.OpHandler;
import refactor.op.OpHandlerFactory;

public class InstructionGraph {

    private final TIntIntHashMap offsetToIndex;
    private final SparseArray<List<InstructionNode>> indexToNodes;

    public InstructionGraph(VirtualMachine vm, BuilderMethod method) {
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();

        offsetToIndex = buildOffsetToIndicies(instructions);

        OpHandlerFactory handlerFactory = new OpHandlerFactory(vm);
        indexToNodes = buildIndexToNodes(handlerFactory, instructions);
    }

    private static TIntIntHashMap buildOffsetToIndicies(List<BuilderInstruction> instructions) {
        TIntIntHashMap indicies = new TIntIntHashMap(instructions.size());

        int offset = 0;
        for (BuilderInstruction instruction : instructions) {
            int index = instruction.getLocation().getIndex();
            indicies.put(offset, index);
        }

        return indicies;
    }

    private static SparseArray<List<InstructionNode>> buildIndexToNodes(OpHandlerFactory handlerFactory,
                    List<BuilderInstruction> instructions) {
        SparseArray<List<InstructionNode>> nodes = new SparseArray<List<InstructionNode>>(instructions.size());

        for (BuilderInstruction instruction : instructions) {
            int index = instruction.getLocation().getIndex();
            OpHandler handler = handlerFactory.getHandler(instruction, index);
            InstructionNode node = new InstructionNode(handler);

            List<InstructionNode> nodePile = new ArrayList<InstructionNode>();
            nodePile.add(node);

            nodes.put(index, nodePile);
        }

        return nodes;
    }

    public List<InstructionNode> getNodePileByIndex(int index) {
        return indexToNodes.get(index);
    }

    public List<InstructionNode> getNodePileByOffset(int offset) {
        int index = offsetToIndex.get(offset);

        return getNodePileByIndex(index);
    }

    public InstructionNode getRootNode() {
        // There is only one entry point for a method.
        return indexToNodes.get(0).get(0);
    }

    public void setRootContext(MethodExecutionContext mectx) {
        getRootNode().setContext(mectx);
    }

    public TIntList getTerminalIndicies() {
        TIntList result = new TIntArrayList();

        // TODO: implement
        return result;
    }

    public boolean isInstructionReachable(int index) {
        List<InstructionNode> nodePile = indexToNodes.get(index);

        // TODO: implement
        // if (nodePile.getParents().length) // has parents?
        return false;
    }
}
