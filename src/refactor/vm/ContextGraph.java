package refactor.vm;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.util.SparseArray;

import refactor.handler.OpHandler;
import refactor.handler.OpHandlerFactory;

public class ContextGraph {

    private final SparseArray<List<ContextNode>> addressToNodePile;
    private final String methodDescriptor;

    ContextGraph(VirtualMachine vm, BuilderMethod method) {
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);

        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();

        addressToNodePile = buildAddressToNodePile(vm, methodDescriptor, instructions);
    }

    ContextGraph(ContextGraph other) {
        methodDescriptor = other.methodDescriptor;

        addressToNodePile = new SparseArray<List<ContextNode>>(other.addressToNodePile.size());
        for (int i = 0; i < other.addressToNodePile.size(); i++) {
            int address = other.addressToNodePile.keyAt(i);
            List<ContextNode> otherNodePile = other.addressToNodePile.get(address);
            List<ContextNode> nodePile = new ArrayList<ContextNode>(otherNodePile.size());
            for (ContextNode otherNode : otherNodePile) {
                nodePile.add(new ContextNode(otherNode));
            }

            addressToNodePile.put(address, nodePile);
        }
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    private static SparseArray<List<ContextNode>> buildAddressToNodePile(VirtualMachine vm, String methodDescriptor,
                    List<BuilderInstruction> instructions) {
        OpHandlerFactory handlerFactory = new OpHandlerFactory(vm, methodDescriptor);

        SparseArray<List<ContextNode>> result = new SparseArray<List<ContextNode>>(instructions.size());
        for (BuilderInstruction instruction : instructions) {
            int index = instruction.getLocation().getIndex();
            OpHandler handler = handlerFactory.create(instruction, index);
            ContextNode node = new ContextNode(handler);

            // Most node piles will be a template node and one node with context.
            List<ContextNode> nodePile = new ArrayList<ContextNode>(2);
            nodePile.add(node);

            int address = instruction.getLocation().getCodeAddress();
            result.put(address, nodePile);
        }

        return result;
    }

    public List<ContextNode> getNodePile(int address) {
        List<ContextNode> result = addressToNodePile.get(address);
        result = result.subList(1, result.size()); // remove template node

        return result;
    }

    ContextNode getTemplateNode(int address) {
        return addressToNodePile.get(address).get(0);
    }

    ContextNode getRootNode() {
        // There is only one entry point for a method.
        return addressToNodePile.get(0).get(0);
    }

    int getNodeCount() {
        return addressToNodePile.size();
    }

    void setRootContext(MethodContext mctx) {
        getRootNode().setContext(mctx);
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

        List<ContextNode> nodePile = addressToNodePile.get(index);

        // If this index was reached during execution, there would be clones in the pile.
        return nodePile.size() > 1;
    }
}
