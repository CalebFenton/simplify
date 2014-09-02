package org.cf.smalivm.context;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.op_handler.Op;
import org.cf.smalivm.op_handler.OpFactory;
import org.cf.smalivm.type.TypeUtil;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextGraph implements Iterable<ContextNode> {

    private static TIntObjectMap<List<ContextNode>> buildAddressToNodePile(VirtualMachine vm, String methodDescriptor,
                    List<BuilderInstruction> instructions) {
        OpFactory handlerFactory = new OpFactory(vm, methodDescriptor);
        TIntObjectMap<List<ContextNode>> result = new TIntObjectHashMap<List<ContextNode>>();
        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            Op handler = handlerFactory.create(instruction, address);
            ContextNode node = new ContextNode(handler);

            // Most node piles will be a template node and one or more ContextNodes.
            List<ContextNode> nodePile = new ArrayList<ContextNode>(2);
            nodePile.add(node);
            result.put(address, nodePile);
        }

        return result;
    }

    private static TIntList buildTerminatingAddresses(List<BuilderInstruction> instructions) {
        TIntList result = new TIntArrayList(1);

        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            /*
             * Array payload is a weird pseudo instruction. We treat it like a normal one but perhaps a better way would
             * be to make it easier for operations to execute other operations, perhaps looking up by address. This
             * would eliminate the need for MethodContext.pseudoInstructionReturnAddress, and Context's getParent().
             */
            Opcode op = instruction.getOpcode();
            if (op.canContinue() || (op == Opcode.ARRAY_PAYLOAD) || op.name.startsWith("goto")) {
                continue;
            }
            result.add(address);
        }

        return result;
    }

    private static final Logger log = LoggerFactory.getLogger(ContextGraph.class.getSimpleName());

    protected final TIntObjectMap<List<ContextNode>> addressToNodePile;
    private final String methodDescriptor;
    private final TIntList terminatingAddresses;

    public ContextGraph(ContextGraph other, boolean wrap) {
        this.addressToNodePile = other.addressToNodePile;
        this.methodDescriptor = other.methodDescriptor;
        this.terminatingAddresses = other.terminatingAddresses;
    }

    public ContextGraph(ContextGraph other) {
        methodDescriptor = other.methodDescriptor;
        addressToNodePile = new TIntObjectHashMap<List<ContextNode>>();
        for (int address : other.addressToNodePile.keys()) {
            List<ContextNode> otherNodePile = other.addressToNodePile.get(address);
            List<ContextNode> nodePile = new ArrayList<ContextNode>(otherNodePile.size());
            for (ContextNode otherNode : otherNodePile) {
                nodePile.add(new ContextNode(otherNode));
            }
            addressToNodePile.put(address, nodePile);
        }
        terminatingAddresses = other.terminatingAddresses;
    }

    public ContextGraph(VirtualMachine vm, BuilderMethod method) {
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();
        addressToNodePile = buildAddressToNodePile(vm, methodDescriptor, instructions);
        terminatingAddresses = buildTerminatingAddresses(instructions);
    }

    public void addNode(int address, ContextNode child) {
        addressToNodePile.get(address).add(child);
    }

    public TIntSet getAddresses() {
        return addressToNodePile.keySet();
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

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    public int getNodeCount() {
        return addressToNodePile.size();
    }

    public List<ContextNode> getNodePile(int address) {
        List<ContextNode> result = addressToNodePile.get(address);
        if (address > 0) {
            result = result.subList(1, result.size()); // remove template node
        }

        return result;
    }

    public Op getOpHandler(int address) {
        List<ContextNode> pile = addressToNodePile.get(address);
        ContextNode bottomNode = pile.get(0);

        return bottomNode.getOpHandler();
    }

    public Object getRegisterConsensus(int address, int register) {
        TIntList addresses = new TIntArrayList(1);
        addresses.add(address);

        return getRegisterConsensus(addresses, register);
    }

    public Object getRegisterConsensus(TIntList addressList, int register) {
        Object value = null;
        for (int address : addressList.toArray()) {
            Set<Object> values = getRegisterValues(address, register);
            value = values.toArray()[0];
            if (values.size() != 1) {
                log.trace("No conensus value for register #" + register + ", returning unknown");
                String type = SmaliClassUtils.javaClassToSmali(TypeUtil.getValueType(value));
                UnknownValue consensus = new UnknownValue(type);

                return consensus;
            }
        }

        return value;
    }

    public Set<Object> getRegisterValues(int address, int register) {
        List<ContextNode> nodePile = getNodePile(address);
        Set<Object> result = new HashSet<Object>(nodePile.size());
        for (ContextNode node : nodePile) {
            Object value = node.getContext().peekRegister(register);
            result.add(value);
        }

        return result;
    }

    public MethodContext getRootContext() {
        return getRootNode().getContext();
    }

    public ContextNode getRootNode() {
        // There is only one entry point for a method.
        return addressToNodePile.get(0).get(0);
    }

    public SideEffect.Type getStrongestSideEffectType() {
        SideEffect.Type result = SideEffect.Type.NONE;
        for (ContextNode node : this) {
            SideEffect.Type type = node.getOpHandler().sideEffectType();
            switch (type) {
            case STRONG:
                return type;
            case WEAK:
                result = type;
                break;
            case NONE:
                break;
            }
        }

        return result;
    }

    public ContextNode getTemplateNode(int address) {
        return addressToNodePile.get(address).get(0);
    }

    public Object getTerminatingRegisterConsensus(int register) {
        return getTerminatingRegisterConsensus(new int[] { register })[0];
    }

    public Object[] getTerminatingRegisterConsensus(int[] registers) {
        TIntList addresses = getConnectedTerminatingAddresses();
        Object[] result = new Object[registers.length];
        for (int i = 0; i < registers.length; i++) {
            result[i] = getRegisterConsensus(addresses, registers[i]);
        }

        return result;
    }

    @Override
    public Iterator<ContextNode> iterator() {
        return new ContextGraphIterator(this);
    }

    public void setRootContext(MethodContext mctx) {
        getRootNode().setContext(mctx);
    }

    public String toGraph() {
        return getRootNode().toGraph();
    }

    public boolean wasAddressReached(int address) {
        if (address == 0) {
            // Root is always reachable
            return true;
        }

        // If this address was reached during execution there will be clones in the pile.
        List<ContextNode> nodePile = addressToNodePile.get(address);
        if (nodePile.size() < 1) {
            log.warn("Node pile @" + address + " has no template node.");
        }

        return nodePile.size() > 1;
    }
}
