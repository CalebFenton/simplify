package org.cf.simplify;

import com.google.common.primitives.Ints;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraphImpl;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.*;
import org.cf.smalivm.type.VirtualMethod;
import org.jf.dexlib2.builder.*;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@SuppressWarnings("Convert2streamapi")
public class ExecutionGraphManipulator extends ExecutionGraphImpl {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ExecutionGraphManipulator.class.getSimpleName());

    private final DexBuilder dexBuilder;
    private final MutableMethodImplementation implementation;
    private final VirtualMethod method;
    private final VirtualMachine vm;
    private final Set<MethodLocation> recreateLocations;
    private final List<MethodLocation> reexecuteLocations;
    private final OpCreator opCreator;
    private boolean recreateOrExecuteAgain;

    public ExecutionGraphManipulator(ExecutionGraphImpl graph, VirtualMethod method, VirtualMachine vm, DexBuilder dexBuilder) {
        super(graph, true);

        this.dexBuilder = dexBuilder;
        this.method = method;
        implementation = method.getImplementation();
        this.vm = vm;
        opCreator = getOpCreator(vm, addressToLocation);
        recreateLocations = new HashSet<>();

        // When ops are added, such as when unreflecting, need to execute in order to ensure
        // correct contexts for each op. Executing out of order may read registers that haven't been assigned yet.
        reexecuteLocations = new LinkedList<>();
        recreateOrExecuteAgain = true;
    }

    public void addInstruction(MethodLocation location, BuilderInstruction instruction) {
        int index = location.getIndex();
        implementation.addInstruction(index, instruction);
        MethodLocation newLocation = instruction.getLocation();
        MethodLocation oldLocation = implementation.getInstructions().get(index + 1).getLocation();
        try {
            java.lang.reflect.Method m = MethodLocation.class.getDeclaredMethod("mergeInto", MethodLocation.class);
            m.setAccessible(true);
            m.invoke(oldLocation, newLocation);
        } catch (Exception e) {
            log.error("Error invoking MethodLocation.mergeInto(). Wrong dexlib version?", e);
        }

        rebuildGraph();
    }

    public void addInstruction(int address, BuilderInstruction newInstruction) {
        addInstruction(getLocation(address), newInstruction);
    }

    public int[] getAvailableRegisters(int address) {
        int[] registers = new int[getRegisterCount(address)];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = i;
        }

        Deque<ExecutionNode> stack = new ArrayDeque<>(getChildren(address));
        ExecutionNode node = stack.peek();
        if (null == node) {
            // No children. All registers available!
            assert getTemplateNode(address).getOp() instanceof ReturnOp ||
                    getTemplateNode(address).getOp() instanceof ReturnVoidOp;
            return registers;
        }

        Set<Integer> registersRead = new HashSet<>();
        Set<Integer> registersAssigned = new HashSet<>();
        while ((node = stack.poll()) != null) {
            MethodState mState = node.getContext().getMethodState();
            for (Integer register : registers) {
                if (registersRead.contains(register) || registersAssigned.contains(register)) {
                    continue;
                }

                if (node.getOp().getName().startsWith("move-result")) {
                    // The target and result registers will always be identical. This makes it seem as if the register
                    // has always been read since it was read when it was in the result register.
                    continue;
                }

                if (mState.wasRegisterRead(register)) {
                    registersRead.add(register);
                } else if (mState.wasRegisterAssigned(register)) {
                    registersAssigned.add(register);
                }
            }
            stack.addAll(node.getChildren());
        }

        return Arrays.stream(registers).filter(r -> !registersRead.contains(r)).toArray();
    }

    public List<ExecutionNode> getChildren(int address) {
        List<ExecutionNode> children = new ArrayList<>();
        List<ExecutionNode> nodePile = getNodePile(address);
        for (ExecutionNode node : nodePile) {
            children.addAll(node.getChildren());
        }

        return children;
    }

    public DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    public
    @Nullable
    BuilderInstruction getInstruction(int address) {
        ExecutionNode node = getTemplateNode(address);

        return node.getOp().getInstruction();
    }

    public int[] getParentAddresses(int address) {
        Set<Integer> parentAddresses = new HashSet<>();
        for (ExecutionNode node : getNodePile(address)) {
            ExecutionNode parent = node.getParent();
            if (null == parent) {
                continue;
            }
            parentAddresses.add(parent.getAddress());
        }

        return Ints.toArray(parentAddresses);
    }

    public List<BuilderTryBlock> getTryBlocks() {
        return implementation.getTryBlocks();
    }

    @Override
    public VirtualMachine getVM() {
        return vm;
    }

    public void removeInstruction(MethodLocation location) {
        int index = location.getIndex();
        implementation.removeInstruction(index);
        removeEmptyTryCatchBlocks();
        rebuildGraph();
    }

    public void removeInstruction(int address) {
        removeInstruction(getLocation(address));
    }

    public void removeInstructions(List<Integer> addresses) {
        Collections.sort(addresses);
        Collections.reverse(addresses);

        log.debug("Removing instructions: {}", addresses);
        addresses.forEach(this::removeInstruction);
    }

    public void replaceInstruction(int insertAddress, BuilderInstruction instruction) {
        List<BuilderInstruction> instructions = new LinkedList<>();
        instructions.add(instruction);
        replaceInstruction(insertAddress, instructions);
    }

    public void replaceInstruction(int insertAddress, List<BuilderInstruction> instructions) {
        recreateOrExecuteAgain = false;
        int address = insertAddress;
        for (BuilderInstruction instruction : instructions) {
            addInstruction(address, instruction);
            address += instruction.getCodeUnits();
        }
        MethodLocation location = getLocation(address);
        recreateOrExecuteAgain = true;
        removeInstruction(location);
    }

    public MethodLocation getLocation(int address) {
        return addressToLocation.get(address);
    }

    private int getRegisterCount(int address) {
        return getNodePile(address).get(0).getContext().getMethodState().getRegisterCount();
    }

    private void addToNodePile(MethodLocation newLocation) {
        // Returns node which need to be re-executed after graph / mappings are rebuilt
        // E.g. branch offset instructions can't be created without accurate mappings
        int oldIndex = newLocation.getIndex() + 1;
        MethodLocation shiftedLocation = null;
        for (MethodLocation location : locationToNodePile.keySet()) {
            if (location.getIndex() == oldIndex) {
                shiftedLocation = location;
                break;
            }
        }
        assert shiftedLocation != null;

        List<ExecutionNode> shiftedNodePile = locationToNodePile.get(shiftedLocation);
        List<ExecutionNode> newNodePile = new ArrayList<>();
        locationToNodePile.put(newLocation, newNodePile);

        Op shiftedOp = shiftedNodePile.get(0).getOp();
        Op op = opCreator.create(newLocation);
        recreateLocations.add(newLocation);
        reexecuteLocations.add(newLocation);
        boolean autoAddedPadding = op instanceof NopOp && (shiftedOp instanceof FillArrayDataPayloadOp ||
                shiftedOp instanceof SwitchPayloadOp);
        for (int i = 0; i < shiftedNodePile.size(); i++) {
            ExecutionNode newNode = new ExecutionNode(op);
            newNodePile.add(i, newNode);

            if (autoAddedPadding) {
                // Padding of this class is never reached
                break;
            }
            if (i == TEMPLATE_NODE_INDEX) {
                continue;
            }

            ExecutionNode shiftedNode = shiftedNodePile.get(i);
            ExecutionNode shiftedParent = shiftedNode.getParent();
            ExecutionContext newContext;
            if (shiftedParent != null) {
                shiftedParent.removeChild(shiftedNode);
                reparentNode(newNode, shiftedParent);

                // Recreate parent op because its children locations may be affected.
                recreateLocations.add(shiftedParent.getOp().getLocation());
            } else {
                assert METHOD_ROOT_ADDRESS == newLocation.getCodeAddress();
                newContext = vm.spawnRootContext(method);
                newNode.setContext(newContext);
            }
            reparentNode(shiftedNode, newNode);
        }
    }

    private void reparentNode(@Nonnull ExecutionNode child, @Nonnull ExecutionNode parent) {
        ExecutionContext newContext = parent.getContext().spawnChild();
        child.setContext(newContext);
        child.setParent(parent);
        reexecuteLocations.add(child.getOp().getLocation());

        for (ExecutionNode grandChild : child.getChildren()) {
            grandChild.getContext().setShallowParent(newContext);
        }
    }

    private void recreateAndExecute() {
        if (!recreateOrExecuteAgain) {
            return;
        }

        // Was removed from implementation before getting here
        recreateLocations.removeIf(p -> p.getInstruction() == null);
        reexecuteLocations.removeIf(p -> p.getInstruction() == null);

        for (MethodLocation location : recreateLocations) {
            Op op = opCreator.create(location);
            List<ExecutionNode> pile = locationToNodePile.get(location);

            // TODO: move side effects out of ops and into nodes or graph
            // This is a big ugly.
            if (op instanceof NewInstanceOp || op instanceof InvokeOp) {
                ExecutionNode node = pile.get(0);

                try {
                    SideEffect.Level originalLevel = node.getOp().getSideEffectLevel();
                    Class<? extends Op> klazz;
                    if (op instanceof NewInstanceOp) {
                        klazz = NewInstanceOp.class;
                    } else { // InvokeOp
                        klazz = InvokeOp.class;
                    }
                    Field f = klazz.getDeclaredField("sideEffectLevel");
                    f.setAccessible(true);
                    f.set(op, originalLevel);
                } catch (Exception e) {
                    // Ugly.
                    e.printStackTrace();
                }
            }

            for (ExecutionNode aPile : pile) {
                aPile.setOp(op);
            }
        }

        // Locations with the same address may be added. One is probably being removed. If using a sorted set with an
        // address comparator, it prevents adding multiple locations. This prevents them from executing here.
        Collections.sort(reexecuteLocations, (e1, e2) -> Integer.compare(e1.getCodeAddress(), e2.getCodeAddress()));
        Set<MethodLocation> reexecute = new LinkedHashSet<>(reexecuteLocations);
        for (MethodLocation location : reexecute) {
            List<ExecutionNode> pile = locationToNodePile.get(location);
            for (int i = 0; i < pile.size(); i++) {
                ExecutionNode node = pile.get(i);
                if (i == TEMPLATE_NODE_INDEX) {
                    continue;
                }

                node.execute();
            }
        }

        recreateLocations.clear();
        reexecuteLocations.clear();
    }

    private void rebuildGraph() {
        // This seems like overkill until you realize implementation may change from under us.
        // Multiple new instructions may be added from adding or removing a single instruction.
        Set<MethodLocation> staleLocations = locationToNodePile.keySet();
        Set<MethodLocation> implementationLocations =
                implementation.getInstructions().stream().map(BuilderInstruction::getLocation)
                        .collect(Collectors.toSet());

        Set<MethodLocation> addedLocations = new HashSet<>(implementationLocations);
        addedLocations.removeAll(staleLocations);
        for (MethodLocation location : addedLocations) {
            addToNodePile(location);
        }
        Set<MethodLocation> removedLocations = new HashSet<>(staleLocations);
        removedLocations.removeAll(implementationLocations);
        removedLocations.forEach(this::removeFromNodePile);

        TIntObjectMap<MethodLocation> newAddressToLocation = buildAddressToLocation(implementation);
        addressToLocation.clear();
        addressToLocation.putAll(newAddressToLocation);

        recreateAndExecute();
    }

    @SuppressWarnings("unchecked")
    private void removeEmptyTryCatchBlocks() {
        /*
         * If every op from a try block is removed, the dex file will fail to save. Maybe dexlib should be smart enough
         * to remove empty blocks itself, but this is an admittedly strange event.
         */

        ListIterator<BuilderTryBlock> iter = implementation.getTryBlocks().listIterator();
        TIntList removeIndexes = new TIntArrayList();
        while (iter.hasNext()) {
            int index = iter.nextIndex();
            BuilderTryBlock tryBlock = iter.next();
            // Get location using reflection to avoid null check.
            MethodLocation start = getLocation(tryBlock.start);
            MethodLocation end = getLocation(tryBlock.end);
            if (start == null || end == null || start.getCodeAddress() == end.getCodeAddress()) {
                // Empty try block!

                // Went through the trouble of getting indexes ahead of time because otherwise
                // calls to equals might need to be made, and that would inspect properties
                // of the try block, which could cause null pointer exceptions.
                removeIndexes.add(index);

                // I think dexlib correctly, gracefully handles removing orphaned labels
                // if (start != null) {
                // List<Label> remove = new LinkedList<Label>();
                // remove.add(tryBlock.start);
                // remove.add(tryBlock.end);
                // start.getLabels().removeAll(remove);
                // }
            }
        }

        // MutableMethodImplementation#getTryBlocks() returns an immutable collection, but we need to modify it.
        ArrayList<BuilderTryBlock> tryBlocks = null;
        try {
            java.lang.reflect.Field f = implementation.getClass().getDeclaredField("tryBlocks");
            f.setAccessible(true); // I DO WHAT I WANT.
            tryBlocks = (ArrayList<BuilderTryBlock>) f.get(implementation);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Remove from the end to avoid re-indexing invalidations
        removeIndexes.sort();
        removeIndexes.reverse();
        for (int index : removeIndexes.toArray()) {
            tryBlocks.remove(index);
        }
    }

    private
    @Nullable
    MethodLocation getLocation(Label label) {
        try {
            Field f = ItemWithLocation.class.getDeclaredField("location");
            f.setAccessible(true);
            return (MethodLocation) f.get(label);
        } catch (Exception e) {
            log.error("Couldn't get label location.", e);
        }
        return null;
    }

    private void removeFromNodePile(MethodLocation location) {
        List<ExecutionNode> nodePile = locationToNodePile.remove(location);
        Map<MethodLocation, ExecutionNode> locationToChildNodeToRemove = new HashMap<>();
        for (ExecutionNode removedNode : nodePile) {
            ExecutionNode parentNode = removedNode.getParent();
            if (parentNode == null) {
                continue;
            }

            parentNode.removeChild(removedNode);
            recreateLocations.add(parentNode.getOp().getLocation());
            // reexecuteLocations.add(parentNode.getOp().getLocation());

            for (ExecutionNode childNode : removedNode.getChildren()) {
                Op childOp = childNode.getOp();
                boolean pseudoChild = childOp instanceof FillArrayDataPayloadOp || childOp instanceof SwitchPayloadOp;
                if (!pseudoChild) {
                    reparentNode(childNode, parentNode);
                } else { // pseudo child
                    // Implementation was altered such that dexlib removed something, probably nop padding
                    for (ExecutionNode grandChildNode : childNode.getChildren()) {
                        reparentNode(grandChildNode, parentNode);
                    }
                    locationToChildNodeToRemove.put(childOp.getLocation(), childNode);
                }
            }
        }

        for (Entry<MethodLocation, ExecutionNode> entry : locationToChildNodeToRemove.entrySet()) {
            List<ExecutionNode> pile = locationToNodePile.get(entry.getKey());
            pile.remove(entry.getValue());
        }
    }

}
