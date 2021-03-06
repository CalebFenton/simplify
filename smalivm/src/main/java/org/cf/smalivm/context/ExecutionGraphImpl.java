package org.cf.smalivm.context;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.OpCreator;
import org.cf.smalivm.type.*;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ExecutionGraphImpl implements ExecutionGraph {

    protected static final int TEMPLATE_NODE_INDEX = 0;
    protected static final int METHOD_ROOT_ADDRESS = 0;
    private static final Logger log = LoggerFactory.getLogger(ExecutionGraphImpl.class.getSimpleName());

    protected final Map<MethodLocation, List<ExecutionNode>> locationToNodePile;
    protected final TIntObjectMap<MethodLocation> addressToLocation;
    private final VirtualMachine vm;
    private final VirtualMethod method;
    private final int[] terminatingAddresses;

    public ExecutionGraphImpl(ExecutionGraphImpl other) {
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

    public ExecutionGraphImpl(ExecutionGraphImpl other, boolean wrap) {
        method = other.method;
        locationToNodePile = other.locationToNodePile;
        terminatingAddresses = other.terminatingAddresses;
        addressToLocation = other.addressToLocation;
        vm = other.vm;
    }

    public ExecutionGraphImpl(VirtualMachine vm, VirtualMethod method) {
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
        Map a2L = new HashMap<Integer, MethodLocation>();
        for (int key : addressToLocation.keys()) {
            a2L.put(key, addressToLocation.get(key));
        }
        return new OpCreator(a2L, vm.getClassManager());
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

    private static void collapseTypeHierarchies(Set<String> types, ClassManager classManager) {
        List<VirtualType> typeList = new LinkedList<>();
        for (String type : types) {
            typeList.add(classManager.getVirtualType(type));
        }
        for (int index = 0; index < typeList.size(); index++) {
            VirtualType currentType = typeList.get(index);
            for (int i = 0; i < typeList.size(); i++) {
                if (i == index) {
                    continue;
                }
                VirtualType compareType = typeList.get(i);
                if (currentType.isChildOf(compareType)) {
                    types.remove(currentType.getName());
                    break;
                }
            }
        }
    }

    private String getConsensusType(Set<String> types, Set<HeapItem> items) {
        if (types.size() == 1) {
            return types.toArray(new String[1])[0];
        }

        int newAncestors = 0;
        do {
            /*
             * Collapse type hierarchies to get the most common type. For example, if types includes ChildClass and ParentClass, then the consensus
             * type should be ParentClass.
             */
            ClassManager classManager = vm.getClassManager();
            collapseTypeHierarchies(types, classManager);

            if (types.size() == 1) {
                return types.toArray(new String[1])[0];
            }

            if (types.size() == 2 && types.contains("I")) {
                // Dalvik uses 0 constant to represent null value
                // https://calebfenton.github.io/2016/02/16/how-does-dalvik-handle-null-types/
                for (String currentType : types) {
                    if (currentType.startsWith("L") && items.contains(new HeapItem(0, "I"))) {
                        return currentType;
                    }
                }
            }

            // If there are still multiple possible types, check for common immediate ancestors.
            Set<String> newTypes = new HashSet<>();
            for (String type : types) {
                VirtualType virtualType = classManager.getVirtualType(type);
                for (VirtualType ancestor : virtualType.getImmediateAncestors()) {
                    newTypes.add(ancestor.getName());
                }
            }
            types.addAll(newTypes);
            newAncestors = newTypes.size();
            collapseTypeHierarchies(types, classManager);
        } while (newAncestors > 0);

        return CommonTypes.UNKNOWN;
    }

    @Override
    public String toString() {
        return "ExecutionGraph{" + method + "}";
    }

    @Override
    public void addNode(ExecutionNode node) {
        MethodLocation location = node.getOp().getInstruction().getLocation();
        locationToNodePile.get(location).add(node);
    }

    /**
     * @return Naturally sorted array of all unique addresses in the graph.
     */
    @Override
    public int[] getAddresses() {
        int[] addresses = addressToLocation.keys();
        Arrays.sort(addresses);

        return addresses;
    }

    @Override
    public Set<VirtualType> getAllPossiblyInitializedClasses(int[] addresses) {
        Set<VirtualType> allClasses = new HashSet<>();
        for (int address : addresses) {
            List<ExecutionNode> pile = getNodePile(address);
            for (ExecutionNode node : pile) {
                allClasses.addAll(node.getContext().getInitializedClasses());
            }
        }

        return allClasses;
    }

    @Override
    public int[] getConnectedTerminatingAddresses() {
        TIntList addresses = new TIntLinkedList();
        for (int address : terminatingAddresses) {
            if (wasAddressReached(address)) {
                addresses.add(address);
            }
        }

        return addresses.toArray();
    }

    @Override
    public HeapItem getFieldConsensus(int[] addresses, VirtualField field) {
        VirtualType virtualClass = field.getDefiningClass();
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

    @Override
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

    @Override
    public SideEffect.Level getHighestClassSideEffectLevel(VirtualType virtualClass) {
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

    @Override
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

    @Override
    public SideEffect.Level getHighestSideEffectLevel() {
        SideEffect.Level result = getHighestMethodSideEffectLevel();
        if (result == SideEffect.Level.STRONG) {
            return result;
        }

        int[] addresses = getConnectedTerminatingAddresses();
        Set<VirtualType> allClasses = getAllPossiblyInitializedClasses(addresses);
        for (VirtualType virtualClass : allClasses) {
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

    @Override
    public Collection<MethodLocation> getLocations() {
        return addressToLocation.valueCollection();
    }

    @Override
    public VirtualMethod getMethod() {
        return method;
    }

    @Override
    public int getNodeCount() {
        int totalSize = locationToNodePile.size();
        int templateCount = locationToNodePile.keySet().size();

        return totalSize - templateCount;
    }

    @Override
    public List<ExecutionNode> getNodePile(int address) {
        List<ExecutionNode> nodePile = getNodePileByAddress(address);
        nodePile = nodePile.subList(1, nodePile.size()); // exclude template

        return nodePile;
    }

    @Override
    public
    @Nullable
    Op getOp(int address) {
        return getTemplateNode(address).getOp();
    }

    /**
     * {@see #getRegisterConsensus(int[], int)}
     *
     * @return consensus value over all {@code addresses} in {@code register} or {@link
     * UnknownValue} if a consensus doesn't exist
     */
    @Override
    public HeapItem getRegisterConsensus(int address, int register) {
        return getRegisterConsensus(new int[] { address }, register);
    }

    /**
     * Look at the value in {@code register} at each of the {@code addresses}. If all values are
     * identical, that means there is a consensus. It means every exeuction path had the same value
     * at those particular {@code addresses} for that particular {@code register}. If there is more
     * than one value, it means it's not possible to know with certainty what the value is. For
     * example, consider the following method:
     *
     * <pre>{@code
     * foo()I
     *   v0 = readStringFromNetwork()Ljava/lang/String; // won't be executed because unsafe
     *   if v0 == "the spice must flow":
     *     v1 = 1
     *   else:
     *     v1 = 0
     *   return v1}</pre>
     * <p>
     * Since the result of {@code readStringFromNetwork()Ljava/lang/String;} won't be known, because
     * it's probably unsafe to virtually execute, the {@code if v0} will be ambiguous and every
     * possible execution path will be taken. This means {@code return v1} could either be {@code 1}
     * or {@code 0}. This means there is no consensus. In this case, an {@link UnknownValue} will
     * be returned.
     *
     * @return consensus value over all {@code addresses} in {@code register} or {@link
     * UnknownValue} if a consensus doesn't exist
     */
    @Override
    public
    @Nullable
    HeapItem getRegisterConsensus(int[] addresses, int register) {
        Set<HeapItem> items = new HashSet<>();
        for (int address : addresses) {
            items.addAll(getRegisterItems(address, register));
        }

        if (items.size() == 1) {
            return items.toArray(new HeapItem[1])[0];
        }

        log.trace("No consensus value for register {}; returning UnknownValue", register);

        // Determine correct type for UnknownValue
        Set<String> types = new HashSet<>(items.size());
        for (HeapItem item : items) {
            if (item == null) {
                // Register was not assigned in this execution path.
                // This can happen in short methods which terminate early.
                continue;
            }
            types.add(item.getType());
        }

        if (types.size() == 0) {
            // May not have any items if an exception was thrown and checking return register
            log.warn("No types for consensus; using *unknown* type! method={}, addresses={}, " + "register={}", getMethod().getSignature(), addresses,
                    register, types);
            return HeapItem.newUnknown(CommonTypes.UNKNOWN);
        } else {
            String type = getConsensusType(types, items);
            if (type.equals(CommonTypes.UNKNOWN)) {
                if (register == MethodState.ReturnRegister) {
                    log.warn("Strange: No consensus type for return register; using method return type, method={}, addresses={}, " + "register={}, "
                            + "types={}", getMethod().getSignature(), addresses, register, types);
                    type = method.getReturnType();
                } else {
                    log.warn("No consensus type; using *unknown* type! method={}, addresses={}, " + "register={}, types={}",
                            getMethod().getSignature(), addresses, register, types);
                }
            }

            return HeapItem.newUnknown(type);
        }
    }

    @Override
    public Object getRegisterConsensusValue(int address, int register) {
        HeapItem item = getRegisterConsensus(address, register);

        return item.getValue();
    }

    @Override
    public
    @Nullable
    Object getRegisterConsensusValue(int[] addresses, int register) {
        HeapItem item = getRegisterConsensus(addresses, register);

        return item.getValue();
    }

    /**
     * This gives you every possible value for a given {@code address} and {@code register}.
     *
     * @return returns items at {@code address} in {@code register} for every execution path
     */
    @Override
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

    @Override
    public ExecutionNode getRoot() {
        List<ExecutionNode> pile = getNodePileByAddress(METHOD_ROOT_ADDRESS);
        boolean nodePileInitialized = pile.size() > 1;
        if (nodePileInitialized) {
            return pile.get(1);
        } else {
            return pile.get(TEMPLATE_NODE_INDEX);
        }
    }

    @Override
    public
    @Nullable
    ExecutionNode getTemplateNode(int address) {
        List<ExecutionNode> nodePile = getNodePileByAddress(address);

        return nodePile.get(TEMPLATE_NODE_INDEX);
    }

    /**
     * @return all terminating addresses (return, throw)
     */
    @Override
    public int[] getTerminatingAddresses() {
        return terminatingAddresses;
    }

    @Override
    public List<ExecutionContext> getTerminatingContexts() {
        List<ExecutionContext> contexts = new LinkedList<>();
        int[] addresses = getConnectedTerminatingAddresses();
        for (int address : addresses) {
            contexts.addAll(getNodePile(address).stream().map(ExecutionNode::getContext).collect(Collectors.toList()));
        }

        return contexts;
    }

    @Override
    public HeapItem getTerminatingFieldConsensus(VirtualField field) {
        Map<VirtualField, HeapItem> items = getTerminatingFieldConsensus(new VirtualField[] { field });

        return items.get(field);
    }

    @Override
    public Map<VirtualField, HeapItem> getTerminatingFieldConsensus(VirtualField[] fields) {
        int[] addresses = getConnectedTerminatingAddresses();
        Map<VirtualField, HeapItem> result = new HashMap<>();
        for (VirtualField field : fields) {
            HeapItem item = getFieldConsensus(addresses, field);
            result.put(field, item);
        }

        return result;
    }

    @Override
    public
    @Nullable
    HeapItem getTerminatingRegisterConsensus(int register) {
        Map<Integer, HeapItem> items = getTerminatingRegisterConsensus(new int[] { register });

        return items.get(register);
    }

    @Override
    public
    @Nullable
    Map<Integer, HeapItem> getTerminatingRegisterConsensus(int[] registers) {
        int[] addresses = getConnectedTerminatingAddresses();
        Map<Integer, HeapItem> result = new HashMap<>(registers.length);
        for (int register : registers) {
            HeapItem item = getRegisterConsensus(addresses, register);
            result.put(register, item);
        }

        return result;
    }

    @Override
    public VirtualMachine getVM() {
        return vm;
    }

    @Override
    public Iterator<ExecutionNode> iterator() {
        return new ExecutionGraphIterator(this);
    }

    @Override
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

    @Override
    public String toSmali() {
        return toSmali(false);
    }

    @Override
    public String toSmali(boolean includeAddress) {
        int[] addresses = getAddresses();
        StringBuilder sb = new StringBuilder();
        for (int address : addresses) {
            Op op = getOp(address);
            sb.append(op.toString());
            if (includeAddress) {
                sb.append(" # @").append(address);
            }
            sb.append('\n');
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
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
