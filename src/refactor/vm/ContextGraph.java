package refactor.vm;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.util.SparseArray;

import refactor.handler.OpHandler;
import refactor.handler.OpHandlerFactory;
import simplify.Main;
import simplify.exec.UnknownValue;

public class ContextGraph {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final SparseArray<List<ContextNode>> addressToNodePile;
    private final String methodDescriptor;
    private final TIntList terminatingAddresses;

    ContextGraph(VirtualMachine vm, BuilderMethod method) {
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);

        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();

        addressToNodePile = buildAddressToNodePile(vm, methodDescriptor, instructions);

        terminatingAddresses = buildTerminatingAddresses(instructions);
    }

    private static TIntList buildTerminatingAddresses(List<BuilderInstruction> instructions) {
        TIntList result = new TIntArrayList(1);

        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            if (!instruction.getOpcode().canContinue()) {
                result.add(address);
            }
        }

        return result;
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

        terminatingAddresses = other.terminatingAddresses;
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    private static SparseArray<List<ContextNode>> buildAddressToNodePile(VirtualMachine vm, String methodDescriptor,
                    List<BuilderInstruction> instructions) {
        OpHandlerFactory handlerFactory = new OpHandlerFactory(vm, methodDescriptor);

        SparseArray<List<ContextNode>> result = new SparseArray<List<ContextNode>>(instructions.size());
        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            OpHandler handler = handlerFactory.create(instruction, address);
            ContextNode node = new ContextNode(handler);

            // Most node piles will be a template node and one node with context.
            List<ContextNode> nodePile = new ArrayList<ContextNode>(2);
            nodePile.add(node);

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

    public TIntList getConnectedTerminatingAddresses() {
        TIntList result = new TIntArrayList(1);
        for (int i = 0; i < terminatingAddresses.size(); i++) {
            int address = terminatingAddresses.get(i);
            if (wasAddressReached(address)) {
                result.add(address);
            }
        }

        return result;
    }

    public boolean wasAddressReached(int address) {
        if (address == 0) {
            // Root is always reachable
            return true;
        }

        List<ContextNode> nodePile = addressToNodePile.get(address);

        // If this address was reached during execution there will be clones in the pile.
        return nodePile.size() > 0;
    }

    public RegisterStore getConsensusRegister(int register) {
        TIntList terminating = getConnectedTerminatingAddresses();
        ContextNode fistNode = getNodePile(terminating.get(0)).get(0);
        RegisterStore registerStore = fistNode.getContext().peekRegister(register);
        String type = registerStore.getType();
        Object value = registerStore.getValue();
        for (int i = 1; i < terminating.size(); i++) {
            int address = terminating.get(i);
            for (ContextNode node : getNodePile(address)) {
                RegisterStore rs = node.getContext().peekRegister(register);
                if (!rs.getType().equals(type) || !rs.getValue().equals(value)) {
                    log.finer("No conensus value for register #" + register + ", returning unknown");
                    return new RegisterStore("?", new UnknownValue());
                }
            }
        }

        return registerStore;
    }

}
