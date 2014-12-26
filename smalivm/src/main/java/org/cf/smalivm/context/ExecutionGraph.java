package org.cf.smalivm.context;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.OpFactory;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionGraph implements Iterable<ExecutionNode> {

    private static final Logger log = LoggerFactory.getLogger(ExecutionGraph.class.getSimpleName());

    protected static final int TEMPLATE_NODE_INDEX = 0;
    protected static final int METHOD_ROOT_ADDRESS = 0;

    private static TIntObjectMap<List<ExecutionNode>> buildAddressToNodePile(VirtualMachine vm,
                    List<BuilderInstruction> instructions) {
        OpFactory opFactory = new OpFactory(vm);
        TIntObjectMap<List<ExecutionNode>> result = new TIntObjectHashMap<List<ExecutionNode>>();
        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            Op op = opFactory.create(instruction, address);
            ExecutionNode node = new ExecutionNode(op);

            // Most node piles will be a template node and one or more ContextNodes.
            List<ExecutionNode> nodePile = new ArrayList<ExecutionNode>(2);
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
             * would eliminate the need for MethodState.pseudoInstructionReturnAddress.
             */
            Opcode op = instruction.getOpcode();
            if (op.canContinue() || (op == Opcode.ARRAY_PAYLOAD) || op.name.startsWith("goto")) {
                continue;
            }
            result.add(address);
        }

        return result;
    }

    private final String methodDescriptor;
    private final TIntList terminatingAddresses;
    protected final TIntObjectMap<List<ExecutionNode>> addressToNodePile;

    public ExecutionGraph(ExecutionGraph other) {
        methodDescriptor = other.methodDescriptor;
        addressToNodePile = new TIntObjectHashMap<List<ExecutionNode>>();
        for (int address : other.addressToNodePile.keys()) {
            List<ExecutionNode> otherNodePile = other.addressToNodePile.get(address);
            List<ExecutionNode> nodePile = new ArrayList<ExecutionNode>(otherNodePile.size());
            for (ExecutionNode otherNode : otherNodePile) {
                nodePile.add(new ExecutionNode(otherNode));
            }
            addressToNodePile.put(address, nodePile);
        }
        terminatingAddresses = other.terminatingAddresses;
    }

    public ExecutionGraph(ExecutionGraph other, boolean wrap) {
        this.addressToNodePile = other.addressToNodePile;
        this.methodDescriptor = other.methodDescriptor;
        this.terminatingAddresses = other.terminatingAddresses;
    }

    public ExecutionGraph(VirtualMachine vm, BuilderMethod method) {
        methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();
        addressToNodePile = buildAddressToNodePile(vm, instructions);
        terminatingAddresses = buildTerminatingAddresses(instructions);
    }

    public void addNode(ExecutionNode node) {
        addressToNodePile.get(node.getAddress()).add(node);
    }

    public int[] getAddresses() {
        return addressToNodePile.keys();
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

    public HeapValue getFieldConsensus(TIntList addressList, String fieldDescriptor) {
        String[] parts = fieldDescriptor.split("->");
        String className = parts[0];
        String fieldNameAndType = parts[1];

        return getFieldConsensus(addressList, className, fieldNameAndType);
    }

    public HeapValue getFieldConsensus(TIntList addressList, String className, String fieldNameAndType) {
        String[] parts = fieldNameAndType.split(":");
        String type = parts[1];
        Set<HeapValue> values = new HashSet<HeapValue>();
        for (int address : addressList.toArray()) {
            // If the class wasn't initialized in one path, it's unknown
            for (ExecutionNode node : getNodePile(address)) {
                if (!node.getContext().isClassInitialized(className)) {
                    return new HeapValue(new UnknownValue(), type);
                }
            }

            values.addAll(getFieldValues(address, className, fieldNameAndType));
            if (1 != values.size()) {
                // since set, size == 1 -> consensus
                if (log.isTraceEnabled()) {
                    log.trace("No conensus for " + className + "->" + fieldNameAndType + ", returning unknown");
                }

                return new HeapValue(new UnknownValue(), type);
            }
        }

        return values.toArray(new HeapValue[values.size()])[0];
    }

    public Set<String> getAllPossiblyInitializedClasses(TIntList addressList) {
        Set<String> allClasses = new HashSet<String>();
        for (int address : addressList.toArray()) {
            List<ExecutionNode> pile = getNodePile(address);
            for (ExecutionNode node : pile) {
                allClasses.addAll(node.getContext().getInitializedClasses());
            }
        }

        return allClasses;
    }

    public Set<HeapValue> getFieldValues(int address, String className, String fieldNameAndType) {
        List<ExecutionNode> nodePile = getNodePile(address);
        Set<HeapValue> result = new HashSet<HeapValue>(nodePile.size());
        for (ExecutionNode node : nodePile) {
            ClassState cState = node.getContext().peekClassState(className);
            HeapValue value = cState.peekField(fieldNameAndType);
            result.add(value);
        }

        return result;
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    public int getNodeCount() {
        int totalSize = addressToNodePile.size();
        int templateCount = addressToNodePile.keys().length;

        return totalSize - templateCount;
    }

    public List<ExecutionNode> getNodePile(int address) {
        List<ExecutionNode> result = addressToNodePile.get(address);
        result = result.subList(1, result.size());

        return result;
    }

    public Op getOp(int address) {
        List<ExecutionNode> pile = addressToNodePile.get(address);
        // same pile implies same op
        ExecutionNode bottomNode = pile.get(TEMPLATE_NODE_INDEX);

        return bottomNode.getOp();
    }

    public Object getRegisterConsensus(int address, int register) {
        TIntList addresses = new TIntArrayList(new int[] { address });

        return getRegisterConsensus(addresses, register);
    }

    public SideEffect.Level getHighestClassSideEffectLevel(String className) {
        TIntList addressList = getConnectedTerminatingAddresses();
        SideEffect.Level result = SideEffect.Level.NONE;
        for (int address : addressList.toArray()) {
            List<ExecutionNode> pile = getNodePile(address);
            for (ExecutionNode node : pile) {
                SideEffect.Level level = node.getContext().getClassStateSideEffectLevel(className);
                if (level == null) {
                    // Maybe the class wasn't initialized.
                    continue;
                }
                switch (level) {
                case STRONG:
                    return level;
                case WEAK:
                    result = level;
                    break;
                case NONE:
                    break;
                }
            }
        }

        return result;
    }

    public HeapValue getRegisterConsensus(TIntList addressList, int register) {
        Set<HeapValue> values = new HashSet<HeapValue>();
        for (int address : addressList.toArray()) {
            values.addAll(getRegisterValues(address, register));
            if (values.size() != 1) {
                if (log.isTraceEnabled()) {
                    log.trace("No conensus for register #" + register + ", returning unknown");
                }

                HeapValue value = values.toArray(new HeapValue[values.size()])[0];
                return new HeapValue(new UnknownValue(), value.getType());
            }
        }

        return values.toArray(new HeapValue[values.size()])[0];
    }

    public Set<HeapValue> getRegisterValues(int address, int register) {
        List<ExecutionNode> nodePile = getNodePile(address);
        Set<HeapValue> result = new HashSet<HeapValue>(nodePile.size());
        for (ExecutionNode node : nodePile) {
            MethodState mState = node.getContext().getMethodState();
            HeapValue value = mState.peekRegister(register);
            result.add(value);
        }

        return result;
    }

    public ExecutionNode getRoot() {
        List<ExecutionNode> pile = addressToNodePile.get(METHOD_ROOT_ADDRESS);
        // Return node with initialized context if available.
        if (pile.size() > 1) {
            return pile.get(1);
        } else {
            return pile.get(TEMPLATE_NODE_INDEX);
        }
    }

    public SideEffect.Level getHighestSideEffectLevel() {
        SideEffect.Level result = getHighestMethodSideEffectLevel();
        if (result == SideEffect.Level.STRONG) {
            return result;
        }

        TIntList addressList = getConnectedTerminatingAddresses();
        Set<String> allClasses = getAllPossiblyInitializedClasses(addressList);
        for (String className : allClasses) {
            SideEffect.Level level = getHighestClassSideEffectLevel(className);
            switch (level) {
            case STRONG:
                return level;
            case WEAK:
                result = level;
                break;
            case NONE:
                break;
            }
        }

        return result;
    }

    public SideEffect.Level getHighestMethodSideEffectLevel() {
        SideEffect.Level result = SideEffect.Level.NONE;
        for (ExecutionNode node : this) {
            Op op = node.getOp();
            SideEffect.Level level = op.sideEffectLevel();
            switch (level) {
            case STRONG:
                return level;
            case WEAK:
                result = level;
                break;
            case NONE:
                break;
            }
        }

        return result;
    }

    public ExecutionNode getTemplateNode(int address) {
        return addressToNodePile.get(address).get(TEMPLATE_NODE_INDEX);
    }

    public Object getTerminatingFieldConsensus(String fieldDescriptor) {
        Map<String, Object> values = getTerminatingFieldConsensus(new String[] { fieldDescriptor });

        return values.get(fieldDescriptor);
    }

    public Map<String, Object> getTerminatingFieldConsensus(String[] fieldDescriptors) {
        TIntList addresses = getConnectedTerminatingAddresses();
        Map<String, Object> result = new HashMap<String, Object>(fieldDescriptors.length);
        for (String fieldDescriptor : fieldDescriptors) {
            Object value = getFieldConsensus(addresses, fieldDescriptor);
            result.put(fieldDescriptor, value);
        }

        return result;
    }

    public Object getTerminatingRegisterConsensus(int register) {
        Map<Integer, Object> values = getTerminatingRegisterConsensus(new int[] { register });

        return values.get(register);
    }

    public Map<Integer, Object> getTerminatingRegisterConsensus(int[] registers) {
        TIntList addresses = getConnectedTerminatingAddresses();
        Map<Integer, Object> result = new HashMap<Integer, Object>(registers.length);
        for (int register : registers) {
            Object value = getRegisterConsensus(addresses, register);
            result.put(register, value);
        }

        return result;
    }

    @Override
    public Iterator<ExecutionNode> iterator() {
        return new ExecutionGraphIterator(this);
    }

    public String toGraph() {
        return getRoot().toGraph();
    }

    public boolean wasAddressReached(int address) {
        if (METHOD_ROOT_ADDRESS == address) {
            // Root is always reachable
            return true;
        }

        // If this address was reached during execution there will be clones in the pile.
        List<ExecutionNode> nodePile = addressToNodePile.get(address);
        if (nodePile == null) {
            System.out.println("nullity!!");
        }
        if ((nodePile == null) || (1 > nodePile.size())) {
            log.warn("Node pile @" + address + " has no template node.");
            return false;
        }

        return nodePile.size() > 1;
    }
}
