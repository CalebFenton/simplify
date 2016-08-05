package org.cf.smalivm.context;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.OpCreator;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualGeneric;
import org.cf.smalivm.type.VirtualMethod;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExecutionGraph implements Iterable<ExecutionNode> {

    protected static final int TEMPLATE_NODE_INDEX = 0;
    protected static final int METHOD_ROOT_ADDRESS = 0;
    private static final Logger log = LoggerFactory.getLogger(ExecutionGraph.class.getSimpleName());
    protected final Map<MethodLocation, List<ExecutionNode>> locationToNodePile;
    protected final TIntObjectMap<MethodLocation> addressToLocation;
    private final VirtualMachine vm;
    private final VirtualMethod method;
    private final int[] terminatingAddresses;

    public ExecutionGraph(ExecutionGraph other) {
        method = other.method;
        locationToNodePile = new HashMap<>();
        for (MethodLocation location : other.locationToNodePile.keySet()) {
            List<ExecutionNode> otherNodePile = other.locationToNodePile.get(location);
            List<ExecutionNode> nodePile = new ArrayList<>(otherNodePile.size());
            nodePile.addAll(otherNodePile.stream().map(ExecutionNode::new).collect(Collectors.toList()));
            locationToNodePile.put(location, nodePile);
        }
        terminatingAddresses = other.terminatingAddresses;
        addressToLocation = other.addressToLocation;
        vm = other.vm;
    }

    public ExecutionGraph(ExecutionGraph other, boolean wrap) {
        method = other.method;
        locationToNodePile = other.locationToNodePile;
        terminatingAddresses = other.terminatingAddresses;
        addressToLocation = other.addressToLocation;
        vm = other.vm;
    }

    public ExecutionGraph(VirtualMachine vm, VirtualMethod method) {
        this.method = method;
        MutableMethodImplementation implementation = method.getImplementation();
        addressToLocation = buildAddressToLocation(implementation);
        locationToNodePile = buildLocationToNodePile(vm, addressToLocation);
        List<BuilderInstruction> instructions = implementation.getInstructions();
        terminatingAddresses = buildTerminatingAddresses(instructions);
        this.vm = vm;
    }

    protected static TIntObjectMap<MethodLocation> buildAddressToLocation(MutableMethodImplementation implementation) {
        List<BuilderInstruction> instructions = implementation.getInstructions();
        TIntObjectMap<MethodLocation> addressToLocation = new TIntObjectHashMap<>(instructions.size());
        for (BuilderInstruction instruction : instructions) {
            MethodLocation location = instruction.getLocation();
            int address = location.getCodeAddress();
            addressToLocation.put(address, location);
        }

        return addressToLocation;
    }

    protected static OpCreator getOpCreator(VirtualMachine vm, TIntObjectMap<MethodLocation> addressToLocation) {
        return new OpCreator(vm, addressToLocation);
    }

    private static Map<MethodLocation, List<ExecutionNode>> buildLocationToNodePile(VirtualMachine vm,
                                                                                    TIntObjectMap<MethodLocation> addressToLocation) {
        OpCreator opCreator = getOpCreator(vm, addressToLocation);
        Map<MethodLocation, List<ExecutionNode>> locationToNodePile = new HashMap<>();
        for (MethodLocation location : addressToLocation.values(new MethodLocation[addressToLocation.size()])) {
            Op op = opCreator.create(location);
            ExecutionNode node = new ExecutionNode(op);

            // Most node piles will be a template node and 1+ ExecutionNodes.
            List<ExecutionNode> pile = new ArrayList<>(2);
            pile.add(node);
            locationToNodePile.put(location, pile);
        }

        return locationToNodePile;
    }

    private static int[] buildTerminatingAddresses(List<BuilderInstruction> instructions) {
        TIntList addresses = new TIntLinkedList();
        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            Opcode op = instruction.getOpcode();
            switch (op) {
                case RETURN_VOID:
                case RETURN:
                case RETURN_WIDE:
                case RETURN_OBJECT:
                case THROW:
                    break;
                default:
                    continue;
            }

            addresses.add(address);
        }

        return addresses.toArray();
    }

    public String toString() {
        return "ExecutionGraph{" + method + "}";
    }

    public void addNode(ExecutionNode node) {
        MethodLocation location = node.getOp().getInstruction().getLocation();
        locationToNodePile.get(location).add(node);
    }

    /**
     * @return Naturally sorted array of all unique addresses in the graph.
     */
    public int[] getAddresses() {
        int[] addresses = addressToLocation.keys();
        Arrays.sort(addresses);

        return addresses;
    }

    public Set<VirtualGeneric> getAllPossiblyInitializedClasses(int[] addresses) {
        Set<VirtualGeneric> allClasses = new HashSet<>();
        for (int address : addresses) {
            List<ExecutionNode> pile = getNodePile(address);
            for (ExecutionNode node : pile) {
                allClasses.addAll(node.getContext().getInitializedClasses());
            }
        }

        return allClasses;
    }

    public int[] getConnectedTerminatingAddresses() {
        TIntList addresses = new TIntLinkedList();
        for (int address : terminatingAddresses) {
            if (wasAddressReached(address)) {
                addresses.add(address);
            }
        }

        return addresses.toArray();
    }

    public HeapItem getFieldConsensus(int[] addresses, VirtualField field) {
        VirtualGeneric virtualClass = field.getDefiningClass();
        Set<HeapItem> items = new HashSet<>();
        for (int address : addresses) {
            // If the class wasn't initialized in one path, it's unknown
            for (ExecutionNode node : getNodePile(address)) {
                if (!node.getContext().isClassInitialized(virtualClass)) {
                    String type = field.getType();
                    return HeapItem.newUnknown(type);
                }
            }

            items.addAll(getFieldItems(address, field));
            if (1 != items.size()) {
                // Since items is a set, size == 1 implies consensus
                if (log.isTraceEnabled()) {
                    log.trace("No consensus for {}, returning Unknown.", field);
                }

                return HeapItem.newUnknown(field.getType());
            }
        }

        return items.toArray(new HeapItem[items.size()])[0];
    }

    public Set<HeapItem> getFieldItems(int address, VirtualField field) {
        List<ExecutionNode> nodePile = getNodePile(address);
        Set<HeapItem> items = new HashSet<>(nodePile.size());
        for (ExecutionNode node : nodePile) {
            ExecutionContext context = node.getContext();
            ClassState cState = context.peekClassState(field.getDefiningClass());
            HeapItem item = cState.peekField(field);
            items.add(item);
        }

        return items;
    }

    public SideEffect.Level getHighestClassSideEffectLevel(VirtualGeneric virtualClass) {
        int[] addresses = getConnectedTerminatingAddresses();
        SideEffect.Level result = SideEffect.Level.NONE;
        for (int address : addresses) {
            List<ExecutionNode> pile = getNodePile(address);
            for (ExecutionNode node : pile) {
                SideEffect.Level level = node.getContext().getClassSideEffectLevel(virtualClass);
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

    public SideEffect.Level getHighestMethodSideEffectLevel() {
        SideEffect.Level result = SideEffect.Level.NONE;
        for (ExecutionNode node : this) {
            Op op = node.getOp();
            SideEffect.Level level = op.getSideEffectLevel();
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

    public SideEffect.Level getHighestSideEffectLevel() {
        SideEffect.Level result = getHighestMethodSideEffectLevel();
        if (result == SideEffect.Level.STRONG) {
            return result;
        }

        int[] addresses = getConnectedTerminatingAddresses();
        Set<VirtualGeneric> allClasses = getAllPossiblyInitializedClasses(addresses);
        for (VirtualGeneric virtualClass : allClasses) {
            SideEffect.Level level = getHighestClassSideEffectLevel(virtualClass);
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

    public Collection<MethodLocation> getLocations() {
        return addressToLocation.valueCollection();
    }

    public VirtualMethod getMethod() {
        return method;
    }

    public int getNodeCount() {
        int totalSize = locationToNodePile.size();
        int templateCount = locationToNodePile.keySet().size();

        return totalSize - templateCount;
    }

    public List<ExecutionNode> getNodePile(int address) {
        List<ExecutionNode> nodePile = getNodePileByAddress(address);
        nodePile = nodePile.subList(1, nodePile.size()); // exclude template

        return nodePile;
    }

    public
    @Nullable
    Op getOp(int address) {
        // Node piles share an Op parse
        return getTemplateNode(address).getOp();
    }

    public HeapItem getRegisterConsensus(int address, int register) {
        return getRegisterConsensus(new int[] { address }, register);
    }

    public
    @Nonnull
    HeapItem getRegisterConsensus(int[] addresses, int register) {
        Set<HeapItem> items = new HashSet<>();
        for (int address : addresses) {
            items.addAll(getRegisterItems(address, register));
            // Size may be 0 if there was an exception
            if (items.size() != 1) {
                log.trace("No consensus for register #{}, returning Unknown.", register);
                Set<String> types = new HashSet<>();
                String type = null;
                for (HeapItem item : items) {
                    if (item == null) {
                        // Register was never assigned for this execution path
                        // This can happen in short methods with branching
                        continue;
                    }
                    type = item.getType();
                    types.add(type);
                }

                if (types.size() > 1) {
                    log.warn("Consensus has multiple types! Returning unknown type.");
                    type = "?";
                }

                return HeapItem.newUnknown(type);
            }
        }

        return items.toArray(new HeapItem[1])[0];
    }

    public Object getRegisterConsensusValue(int address, int register) {
        HeapItem item = getRegisterConsensus(address, register);

        return item.getValue();
    }

    public
    @Nullable
    Object getRegisterConsensusValue(int[] addresses, int register) {
        HeapItem item = getRegisterConsensus(addresses, register);

        return item.getValue();
    }

    public Set<HeapItem> getRegisterItems(int address, int register) {
        List<ExecutionNode> nodePile = getNodePile(address);
        Set<HeapItem> items = new HashSet<>(nodePile.size());
        for (ExecutionNode node : nodePile) {
            MethodState mState = node.getContext().getMethodState();
            HeapItem item = mState.peekRegister(register);
            items.add(item);
        }

        return items;
    }

    public ExecutionNode getRoot() {
        List<ExecutionNode> pile = getNodePileByAddress(METHOD_ROOT_ADDRESS);
        // Return node with initialized context if available.
        if (pile.size() > 1) {
            return pile.get(1);
        } else {
            return pile.get(TEMPLATE_NODE_INDEX);
        }
    }

    public
    @Nullable
    ExecutionNode getTemplateNode(int address) {
        List<ExecutionNode> nodePile = getNodePileByAddress(address);

        return nodePile.get(TEMPLATE_NODE_INDEX);
    }

    public int[] getTerminatingAddresses() {
        return terminatingAddresses;
    }

    public List<ExecutionContext> getTerminatingContexts() {
        List<ExecutionContext> contexts = new LinkedList<>();
        int[] addresses = getConnectedTerminatingAddresses();
        for (int address : addresses) {
            contexts.addAll(getNodePile(address).stream().map(ExecutionNode::getContext).collect(Collectors.toList()));
        }

        return contexts;
    }

    public HeapItem getTerminatingFieldConsensus(VirtualField field) {
        Map<VirtualField, HeapItem> items = getTerminatingFieldConsensus(new VirtualField[] { field });

        return items.get(field);
    }

    public Map<VirtualField, HeapItem> getTerminatingFieldConsensus(VirtualField[] fields) {
        int[] addresses = getConnectedTerminatingAddresses();
        Map<VirtualField, HeapItem> result = new HashMap<>();
        for (VirtualField field : fields) {
            HeapItem item = getFieldConsensus(addresses, field);
            result.put(field, item);
        }

        return result;
    }

    public
    @Nonnull
    HeapItem getTerminatingRegisterConsensus(int register) {
        Map<Integer, HeapItem> items = getTerminatingRegisterConsensus(new int[] { register });

        return items.get(register);
    }

    public Map<Integer, HeapItem> getTerminatingRegisterConsensus(int[] registers) {
        int[] addresses = getConnectedTerminatingAddresses();
        Map<Integer, HeapItem> result = new HashMap<>(registers.length);
        for (int register : registers) {
            HeapItem item = getRegisterConsensus(addresses, register);
            result.put(register, item);
        }

        return result;
    }

    public VirtualMachine getVM() {
        return vm;
    }

    @Override
    public Iterator<ExecutionNode> iterator() {
        return new ExecutionGraphIterator(this);
    }

    public boolean wasAddressReached(int address) {
        if (METHOD_ROOT_ADDRESS == address) {
            // Root is always reachable
            return true;
        }

        // If this address was reached during execution there will be clones in the pile.
        List<ExecutionNode> nodePile = getNodePileByAddress(address);
        if ((nodePile == null) || (1 > nodePile.size())) {
            log.warn("Node pile @{} has no template node.", address);
            return false;
        }

        return nodePile.size() > 1;
    }

    protected int getNodeIndex(ExecutionNode node) {
        return getNodePile(node.getAddress()).indexOf(node);
    }

    private
    @Nullable
    List<ExecutionNode> getNodePileByAddress(int address) {
        MethodLocation location = addressToLocation.get(address);

        return locationToNodePile.get(location);
    }

}
