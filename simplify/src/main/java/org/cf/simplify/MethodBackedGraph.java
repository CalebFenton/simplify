package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.FillArrayDataPayloadOp;
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

public class MethodBackedGraph extends ExecutionGraph {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MethodBackedGraph.class.getSimpleName());

    private final DexBuilder dexBuilder;
    private final MutableMethodImplementation implementation;
    private final BuilderMethod method;
    private final String methodDescriptor;
    private OpCreator opCreator;
    private final VirtualMachine vm;

    public MethodBackedGraph(ExecutionGraph graph, BuilderMethod method, VirtualMachine vm, DexBuilder dexBuilder) {
        super(graph, true);

        this.dexBuilder = dexBuilder;
        this.method = method;
        this.methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        implementation = (MutableMethodImplementation) method.getImplementation();
        this.vm = vm;
        opCreator = getOpCreator(vm, addressToLocation);
    }

    public void addInstruction(MethodLocation location, BuilderInstruction newInstruction) {
        int index = location.getIndex();
        implementation.addInstruction(index, newInstruction);

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
            assert (getTemplateNode(address).getOp() instanceof ReturnOp) || (getTemplateNode(address).getOp() instanceof ReturnVoidOp);
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

    public TIntList getParentAddresses(int address) {
        TIntSet parentAddressSet = new TIntHashSet();
        for (ExecutionNode node : getNodePile(address)) {
            ExecutionNode parent = node.getParent();
            if (null == parent) {
                continue;
            }
            parentAddressSet.add(parent.getAddress());
        }
        TIntList parentAddresses = new TIntArrayList(parentAddressSet);

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
        tryMigrateLabels(index);
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

    public void replaceInstruction(int address, BuilderInstruction instruction) {
        removeInstruction(getLocation(address));
        addInstruction(getLocation(address), instruction);
    }

    public void replaceInstruction(int insertAddress, List<BuilderInstruction> instructions) {
        MethodLocation location = getLocation(insertAddress);
        int address = location.getCodeAddress();
        removeInstruction(location);
        for (BuilderInstruction instruction : instructions) {
            addInstruction(address, instruction);
            address += instruction.getCodeUnits();
        }
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
        int oldIndex = newLocation.getIndex() + 1;
        MethodLocation shiftedLocation = null;
        for (MethodLocation location : locationToNodePile.keySet()) {
            if (location.getIndex() == oldIndex) {
                shiftedLocation = location;
                break;
            }
        }
        List<ExecutionNode> shiftedNodePile = locationToNodePile.get(shiftedLocation);
        List<ExecutionNode> newNodePile = new ArrayList<ExecutionNode>();
        locationToNodePile.put(newLocation, newNodePile);

        Op shiftedOp = shiftedNodePile.get(0).getOp();
        Op op = opCreator.create(newLocation);
        boolean padding = (op instanceof NopOp && (shiftedOp instanceof FillArrayDataPayloadOp || shiftedOp instanceof SwitchPayloadOp));
        for (int i = 0; i < shiftedNodePile.size(); i++) {
            ExecutionNode newNode = new ExecutionNode(op);
            newNodePile.add(i, newNode);

            if (padding) {
                break;
            }
            if (i == TEMPLATE_NODE_INDEX) {
                continue;
            }

            ExecutionNode shiftedNode = shiftedNodePile.get(i);
            ExecutionNode parentNode = shiftedNode.getParent();
            ExecutionContext newContext;
            if (parentNode != null) {
                parentNode.replaceChild(shiftedNode, newNode);
                newContext = parentNode.getContext().spawnChild();
            } else {
                assert METHOD_ROOT_ADDRESS == newLocation.getCodeAddress();
                newContext = vm.spawnExecutionContext(methodDescriptor);
            }
            shiftedNode.setParentNode(newNode);
            newNode.setContext(newContext);

            // Execute to set children and context
            newNode.execute();
        }
    }

    private void rebuildGraph() {
        Set<MethodLocation> staleLocations = locationToNodePile.keySet();
        Set<MethodLocation> freshLocations = new HashSet<MethodLocation>();
        for (BuilderInstruction instruction : implementation.getInstructions()) {
            freshLocations.add(instruction.getLocation());
        }

        Set<MethodLocation> addedLocations = new HashSet<MethodLocation>(freshLocations);
        addedLocations.removeAll(staleLocations);
        for (MethodLocation location : addedLocations) {
            addToNodePile(location);
        }
        Set<MethodLocation> removedLocations = new HashSet<MethodLocation>(staleLocations);
        removedLocations.removeAll(freshLocations);
        for (MethodLocation location : removedLocations) {
            removeFromNodePile(location);
        }

        TIntObjectMap<MethodLocation> newAddressToLocation = buildAddressToLocation(implementation);
        addressToLocation.clear();
        addressToLocation.putAll(newAddressToLocation);
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
        List<ExecutionNode> nodePile = locationToNodePile.get(location);
        locationToNodePile.remove(location);
        Map<MethodLocation, ExecutionNode> locationToChildNodeToRemove = new HashMap<MethodLocation, ExecutionNode>();
        for (ExecutionNode removedNode : nodePile) {
            ExecutionNode parentNode = removedNode.getParent();
            if (parentNode != null) {
                parentNode.removeChild(removedNode);
            }
            for (ExecutionNode childNode : removedNode.getChildren()) {
                // parentNode could be null, and that's ok
                Op op = childNode.getOp();
                boolean pseudoChild = (op instanceof FillArrayDataPayloadOp || op instanceof SwitchPayloadOp);
                if (!pseudoChild) {
                    childNode.setParentNode(parentNode);
                } else {
                    for (ExecutionNode grandChildNode : childNode.getChildren()) {
                        grandChildNode.setParentNode(parentNode);
                    }
                    locationToChildNodeToRemove.put(childNode.getOp().getLocation(), childNode);
                }
            }
        }
        for (Entry<MethodLocation, ExecutionNode> entry : locationToChildNodeToRemove.entrySet()) {
            List<ExecutionNode> pile = locationToNodePile.get(entry.getKey());
            pile.remove(entry.getValue());
        }
    }

    private void tryMigrateLabels(int index) {
        // If it has a label, and it's not the last instruction, move it to the next instruction.
        List<BuilderInstruction> instructions = implementation.getInstructions();
        int instructionCount = instructions.size();
        if (index < (instructionCount - 1)) {
            // Not last instruction
            BuilderInstruction moveFrom = instructions.get(index);
            Set<Label> labelSet = moveFrom.getLocation().getLabels();
            if (0 < labelSet.size()) {
                BuilderInstruction moveTo = instructions.get(index + 1);
                List<Label> labels = new ArrayList<Label>(labelSet.size());
                for (Label label : labels) {
                    moveTo.getLocation().getLabels().add(label);
                }
                // moveFrom.getLocation().getLabels().removeAll(labelSet);
            }
        }
    }

    TIntList getReachedAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addressToLocation.keys()) {
            if (wasAddressReached(address)) {
                result.add(address);
            }
        }

        return result;
    }

}
