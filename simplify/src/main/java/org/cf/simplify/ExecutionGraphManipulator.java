package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.FillArrayDataPayloadOp;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.NewInstanceOp;
import org.cf.smalivm.opcode.NopOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.OpCreator;
import org.cf.smalivm.opcode.ReturnOp;
import org.cf.smalivm.opcode.ReturnVoidOp;
import org.cf.smalivm.opcode.SwitchPayloadOp;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionGraphManipulator extends ExecutionGraph {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ExecutionGraphManipulator.class.getSimpleName());

    private final DexBuilder dexBuilder;
    private final MutableMethodImplementation implementation;
    private final BuilderMethod method;
    private final VirtualMachine vm;
    private final Set<MethodLocation> recreateLocations;
    private final List<MethodLocation> reexecuteLocations;
    private OpCreator opCreator;
    private boolean recreateOrReexecute;

    public ExecutionGraphManipulator(ExecutionGraph graph, BuilderMethod method, VirtualMachine vm,
                    DexBuilder dexBuilder) {
        super(graph, true);

        this.dexBuilder = dexBuilder;
        this.method = method;
        implementation = (MutableMethodImplementation) method.getImplementation();
        this.vm = vm;
        opCreator = getOpCreator(vm, addressToLocation);
        recreateLocations = new HashSet<MethodLocation>();

        // When many ops are added, such as when unreflecting, need to execute in order to ensure
        // correct contexts for each op. Executing out of order may read registers that haven't been assigned yet.
        reexecuteLocations = new LinkedList<MethodLocation>();
        recreateOrReexecute = true;
    }

    public void addInstruction(MethodLocation location, BuilderInstruction instruction) {
        int index = location.getIndex();
        implementation.addInstruction(index, instruction);
        MethodLocation newLocation = instruction.getLocation();
        MethodLocation oldLocation = implementation.getInstructions().get(index + 1).getLocation();
        try {
            Method m = MethodLocation.class.getDeclaredMethod("mergeInto", MethodLocation.class);
            m.setAccessible(true);
            m.invoke(oldLocation, newLocation);
        } catch (Exception e) {
            log.error("Error invoking MethodLocation#mergeInto(). Wrong dexlib version?", e);
        }

        rebuildGraph();
    }

    public void addInstruction(int address, BuilderInstruction newInstruction) {
        addInstruction(getLocation(address), newInstruction);
    }

    public TIntList getAvailableRegisters(int address) {
        Deque<ExecutionNode> stack = new ArrayDeque<ExecutionNode>(getChildren(address));
        ExecutionNode node = stack.peek();
        if (null == node) {
            // Edge case.
            assert getTemplateNode(address).getOp() instanceof ReturnOp || getTemplateNode(address).getOp() instanceof ReturnVoidOp;
            MethodState mState = getNodePile(address).get(0).getContext().getMethodState();
            TIntList available = new TIntLinkedList();
            // They're all available!
            for (int i = 0; i < mState.getRegisterCount(); i++) {
                available.add(i);
            }

            return available;
        }

        int[] registers = new int[node.getContext().getMethodState().getRegisterCount()];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = i;
        }
        TIntSet registersRead = new TIntHashSet();
        TIntSet registersAssigned = new TIntHashSet();
        while ((node = stack.poll()) != null) {
            // TODO: easy - determine if dalvik allows you to overwrite the "this" register for instance methods
            MethodState mState = node.getContext().getMethodState();
            for (int register : registers) {
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

        TIntList available = new TIntLinkedList();
        for (int register : registers) {
            if (registersRead.contains(register)) {
                continue;
            }
            available.add(register);
        }

        return available;
    }

    public List<ExecutionNode> getChildren(int address) {
        List<ExecutionNode> children = new ArrayList<ExecutionNode>();
        List<ExecutionNode> nodePile = getNodePile(address);
        for (ExecutionNode node : nodePile) {
            children.addAll(node.getChildren());
        }

        return children;
    }

    public DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    public @Nullable BuilderInstruction getInstruction(int address) {
        ExecutionNode node = getTemplateNode(address);

        return node.getOp().getInstruction();
    }

    public TIntSet getParentAddresses(int address) {
        TIntSet parentAddresses = new TIntHashSet();
        for (ExecutionNode node : getNodePile(address)) {
            ExecutionNode parent = node.getParent();
            if (null == parent) {
                continue;
            }
            parentAddresses.add(parent.getAddress());
        }

        return parentAddresses;
    }

    public List<BuilderTryBlock> getTryBlocks() {
        return implementation.getTryBlocks();
    }

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

    public void removeInstructions(TIntList addresses) {
        addresses.sort();
        addresses.reverse();
        log.debug("Removing instructions: " + addresses);
        for (int address : addresses.toArray()) {
            removeInstruction(address);
        }
    }

    public void replaceInstruction(int insertAddress, BuilderInstruction instruction) {
        List<BuilderInstruction> instructions = new LinkedList<BuilderInstruction>();
        instructions.add(instruction);
        replaceInstruction(insertAddress, instructions);
    }

    public void replaceInstruction(int insertAddress, List<BuilderInstruction> instructions) {
        recreateOrReexecute = false;
        int address = insertAddress;
        for (BuilderInstruction instruction : instructions) {
            addInstruction(address, instruction);
            address += instruction.getCodeUnits();
        }
        MethodLocation location = getLocation(address);
        recreateOrReexecute = true;
        removeInstruction(location);
    }

    public String toSmali() {
        int[] addresses = getAddresses();
        Arrays.sort(addresses);
        StringBuilder sb = new StringBuilder();
        for (int address : addresses) {
            Op op = getOp(address);
            sb.append(op.toString()).append('\n');
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
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
        List<ExecutionNode> newNodePile = new ArrayList<ExecutionNode>();
        locationToNodePile.put(newLocation, newNodePile);

        Op shiftedOp = shiftedNodePile.get(0).getOp();
        Op op = opCreator.create(newLocation);
        recreateLocations.add(newLocation);
        reexecuteLocations.add(newLocation);
        boolean autoAddedPadding = op instanceof NopOp && (shiftedOp instanceof FillArrayDataPayloadOp || shiftedOp instanceof SwitchPayloadOp);
        for (int i = 0; i < shiftedNodePile.size(); i++) {
            ExecutionNode newNode = new ExecutionNode(op);
            newNodePile.add(i, newNode);

            if (autoAddedPadding) {
                // Padding of this type is never reached
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
                String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
                newContext = vm.spawnRootExecutionContext(methodDescriptor);
                newNode.setContext(newContext);
            }
            reparentNode(shiftedNode, newNode);
        }
    }

    private void reparentNode(ExecutionNode child, ExecutionNode parent) {
        ExecutionContext newContext = parent.getContext().spawnChild();
        child.setContext(newContext);
        child.setParent(parent);
        reexecuteLocations.add(child.getOp().getLocation());

        for (ExecutionNode grandChild : child.getChildren()) {
            grandChild.getContext().setShallowParent(newContext);
        }
    }

    private void recreateAndExecute() {
        if (!recreateOrReexecute) {
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

            for (int i = 0; i < pile.size(); i++) {
                pile.get(i).setOp(op);
            }
        }

        // Locations with the same address may be added. One is probably being removed. If using a sorted set with an
        // address comparator, it prevents adding multiple locations. This prevents them from executing here.
        Collections.sort(reexecuteLocations, (e1, e2) -> Integer.compare(e1.getCodeAddress(), e2.getCodeAddress()));
        Set<MethodLocation> reexecute = new LinkedHashSet<MethodLocation>(reexecuteLocations);
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
        Set<MethodLocation> implementationLocations = new HashSet<MethodLocation>();
        for (BuilderInstruction instruction : implementation.getInstructions()) {
            implementationLocations.add(instruction.getLocation());
        }

        Set<MethodLocation> addedLocations = new HashSet<MethodLocation>(implementationLocations);
        addedLocations.removeAll(staleLocations);
        for (MethodLocation location : addedLocations) {
            addToNodePile(location);
        }
        Set<MethodLocation> removedLocations = new HashSet<MethodLocation>(staleLocations);
        removedLocations.removeAll(implementationLocations);
        for (MethodLocation location : removedLocations) {
            removeFromNodePile(location);
        }

        TIntObjectMap<MethodLocation> newAddressToLocation = buildAddressToLocation(implementation);
        addressToLocation.clear();
        addressToLocation.putAll(newAddressToLocation);

        recreateAndExecute();
    }

    public MethodLocation getLocation(int address) {
        return addressToLocation.get(address);
    }

    @SuppressWarnings("unchecked")
    private void removeEmptyTryCatchBlocks() {
        /*
         * MutableMethodImplementation#getTryBlocks() returns immutable collection. Maybe dexlib should be smart enough
         * to remove the try block if start, end and catch labels are removed, but it isn't. To be fair, this is a
         * strange event.
         */
        ArrayList<BuilderTryBlock> tryBlocks = null;
        try {
            java.lang.reflect.Field f = implementation.getClass().getDeclaredField("tryBlocks");
            f.setAccessible(true); // I DO WHAT I WANT.
            tryBlocks = (ArrayList<BuilderTryBlock>) f.get(implementation);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        TIntList indexes = new TIntArrayList();
        for (BuilderTryBlock tryBlock : implementation.getTryBlocks()) {
            MethodLocation startLocation = tryBlock.start.getLocation();
            MethodLocation endLocation = tryBlock.end.getLocation();
            if (startLocation.getCodeAddress() == endLocation.getCodeAddress()) {
                // Empty try block!

                // Use indexOf, because if you try and remove by object reference, it needs to call equals, which will
                // fail because we just wiped out the labels and dexlib is like "wtf".
                indexes.add(tryBlocks.indexOf(tryBlock));

                List<Label> remove = new ArrayList<Label>();
                remove.add(tryBlock.start);
                remove.add(tryBlock.end);
                remove.add(tryBlock.exceptionHandler.getHandler());
                startLocation.getLabels().removeAll(remove);
            }
        }

        // Remove from the end to avoid re-indexing invalidations
        indexes.sort();
        indexes.reverse();
        for (int index : indexes.toArray()) {
            tryBlocks.remove(index);
        }
    }

    private void removeFromNodePile(MethodLocation location) {
        List<ExecutionNode> nodePile = locationToNodePile.remove(location);
        Map<MethodLocation, ExecutionNode> locationToChildNodeToRemove = new HashMap<MethodLocation, ExecutionNode>();
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
