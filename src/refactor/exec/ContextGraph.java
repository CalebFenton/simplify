package refactor.exec;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.util.SparseArray;

import refactor.op.OpHandler;
import refactor.op.OpHandlerFactory;

public class ContextGraph {

    private final TIntIntHashMap indexToAddress;
    private final SparseArray<List<ContextNode>> indexToNodePile;
    private final String methodDescriptor;

    ContextGraph(VirtualMachine vm, BuilderMethod method) {
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);

        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();
        indexToAddress = buildIndexToAddress(instructions);

        indexToNodePile = buildIndexToNodes(vm, methodDescriptor, instructions);
    }

    ContextGraph(ContextGraph other) {
        methodDescriptor = other.methodDescriptor;
        indexToAddress = other.indexToAddress;

        indexToNodePile = new SparseArray<List<ContextNode>>(other.indexToNodePile.size());
        for (int i = 0; i < other.indexToNodePile.size(); i++) {
            int index = indexToNodePile.keyAt(i);
            List<ContextNode> otherNodePile = other.indexToNodePile.get(index);
            List<ContextNode> nodePile = new ArrayList<ContextNode>(otherNodePile.size());
            for (ContextNode otherNode : otherNodePile) {
                nodePile.add(new ContextNode(otherNode));
            }

            indexToNodePile.put(index, nodePile);
        }
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    private static TIntIntHashMap buildIndexToAddress(List<BuilderInstruction> instructions) {
        TIntIntHashMap indicies = new TIntIntHashMap(instructions.size());

        for (BuilderInstruction instruction : instructions) {
            MethodLocation location = instruction.getLocation();
            int index = location.getIndex();
            int address = location.getCodeAddress();
            indicies.put(index, address);
        }

        return indicies;
    }

    private static SparseArray<List<ContextNode>> buildIndexToNodes(VirtualMachine vm, String methodDescriptor,
                    List<BuilderInstruction> instructions) {
        OpHandlerFactory handlerFactory = new OpHandlerFactory(vm, methodDescriptor);

        SparseArray<List<ContextNode>> result = new SparseArray<List<ContextNode>>(instructions.size());
        for (BuilderInstruction instruction : instructions) {
            int index = instruction.getLocation().getIndex();
            OpHandler handler = handlerFactory.create(instruction, index);
            ContextNode node = new ContextNode(handler);

            List<ContextNode> nodePile = new ArrayList<ContextNode>();
            nodePile.add(node);

            result.put(index, nodePile);
        }

        return result;
    }

    public List<ContextNode> getNodePileByIndex(int index) {
        return indexToNodePile.get(index);
    }

    public List<ContextNode> getNodePileByAddress(int address) {
        int index = indexToAddress.get(address);

        return getNodePileByIndex(index);
    }

    ContextNode getBottomNode(int address) {
        return getNodePileByAddress(address).get(0);
    }

    ContextNode getRootNode() {
        // There is only one entry point for a method.
        return indexToNodePile.get(0).get(0);
    }

    void setRootContext(MethodExecutionContext mectx) {
        getRootNode().setContext(mectx);
    }

    public TIntList getTerminalIndicies() {
        TIntList result = new TIntArrayList();

        // TODO: implement

        return result;
    }

    public boolean isInstructionReachable(int index) {
        if (index == 0) {
            // Root is always reachable
            return true;
        }

        List<ContextNode> nodePile = indexToNodePile.get(index);

        // If this index was reached during execution, there would be clones in the pile.
        return nodePile.size() > 1;
    }
}
