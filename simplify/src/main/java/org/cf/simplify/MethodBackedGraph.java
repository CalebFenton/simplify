package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.OpFactory;
import org.cf.smalivm.opcode.ReturnOp;
import org.cf.smalivm.opcode.ReturnVoidOp;
import org.cf.util.Utils;
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

    private static TIntObjectMap<BuilderInstruction> buildAddressToInstruction(List<BuilderInstruction> instructions) {
        TIntObjectMap<BuilderInstruction> result = new TIntObjectHashMap<BuilderInstruction>();
        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            result.put(address, instruction);
        }

        return result;
    }

    private void shiftOpAddresses(int startAddress, int shift) {
        for (int nodeAddress : addressToNodePile.keys()) {
            if (nodeAddress <= startAddress) {
                continue;
            }

            // All nodes at an address have the same op. No need to modify the entire node pile.
            ExecutionNode node = getTemplateNode(nodeAddress);
            Op op = node.getOp();
            op.setAddress(op.getAddress() + shift);

            int[] childAddresses = op.getPossibleChildren();
            if (childAddresses.length > 0) {
                for (int i = 0; i < childAddresses.length; i++) {
                    childAddresses[i] += shift;
                }
                op.setPossibleChildren(childAddresses);
            }
        }
    }

    private final TIntObjectMap<BuilderInstruction> addressToInstruction;
    private final DexBuilder dexBuilder;
    private final MutableMethodImplementation implementation;
    private final BuilderMethod method;
    private final String methodDescriptor;
    private final OpFactory opFactory;
    private final VirtualMachine vm;

    public MethodBackedGraph(ExecutionGraph graph, BuilderMethod method, VirtualMachine vm, DexBuilder dexBuilder) {
        super(graph, true);

        this.dexBuilder = dexBuilder;
        this.method = method;
        this.methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        implementation = (MutableMethodImplementation) method.getImplementation();
        addressToInstruction = buildAddressToInstruction(implementation.getInstructions());
        this.vm = vm;
        opFactory = new OpFactory(vm);
    }

    public TIntObjectMap<BuilderInstruction> getAddressToInstruction() {
        return addressToInstruction;
    }

    public DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    public BuilderInstruction getInstruction(int address) {
        return addressToInstruction.get(address);
    }

    public VirtualMachine getVM() {
        return vm;
    }

    public List<BuilderTryBlock> getTryBlocks() {
        return implementation.getTryBlocks();
    }

    public void removeInstruction(int address) {
        removeInstructions(new TIntArrayList(new int[] { address }));
    }

    public void removeInstructions(TIntList addresses) {
        /*
         * Implementation removes by index, not address. Collect indexes and update addressToInstruction map. Also,
         * while looping, remove orphaned debug items, or you're gonna have a bad time.
         */
        TIntList indexes = new TIntArrayList();
        addresses.sort();
        addresses.reverse();
        if (log.isInfoEnabled()) {
            log.info("Remove addresses: " + addresses);
        }
        for (int address : addresses.toArray()) {
            BuilderInstruction instruction = addressToInstruction.get(address);
            MethodLocation location = instruction.getLocation();
            location.getDebugItems().clear();
            if (log.isDebugEnabled()) {
                log.debug("@" + address + " is index " + location.getIndex());
            }
            indexes.add(location.getIndex());

            // Shifting down also removes this address key
            int shift = -instruction.getCodeUnits();
            Utils.shiftIntegerMapKeys(address, shift, addressToInstruction);
            removeNodePile(address, shift);
        }

        // Go in reverse order because implementation reorders on removal
        indexes.sort();
        indexes.reverse();
        for (int index : indexes.toArray()) {
            tryMigrateLabels(index);

            implementation.removeInstruction(index);
        }

        removeEmptyTryCatchBlocks();
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

    private void tryMigrateLabels(int index) {
        // If it has a label, and it's not the last instruction, move it to the next instruction.
        int instructionCount = implementation.getInstructions().size();
        if (index < (instructionCount - 1)) {
            // Not last instruction
            BuilderInstruction moveFrom = implementation.getInstructions().get(index);
            Set<Label> labelSet = moveFrom.getLocation().getLabels();
            if (0 < labelSet.size()) {
                BuilderInstruction moveTo = implementation.getInstructions().get(index + 1);
                List<Label> labels = new ArrayList<Label>(labelSet.size());
                for (Label label : labels) {
                    moveTo.getLocation().getLabels().add(label);
                }
                // moveFrom.getLocation().getLabels().removeAll(labelSet);
            }
        }
    }

    public void replaceInstruction(int address, BuilderInstruction replacement) {
        BuilderInstruction original = getInstruction(address);
        int index = original.getLocation().getIndex();
        implementation.replaceInstruction(index, replacement);
        addressToInstruction.put(address, replacement);

        int codeUnits = replacement.getCodeUnits();
        int shift = codeUnits - original.getCodeUnits();
        Utils.shiftIntegerMapKeys(address, shift, addressToInstruction);

        Op op = opFactory.create(replacement, address);
        replaceInstruction(address, shift, op, codeUnits);
    }

    private void shiftNodePileAddresses(int startAddress, int shift) {
        shiftOpAddresses(startAddress, shift);
        Utils.shiftIntegerMapKeys(startAddress, shift, addressToNodePile);
    }

    public void insertInstruction(int address, BuilderInstruction instruction) {
        // Insert + shift instructions
        BuilderInstruction original = addressToInstruction.get(address);
        int index = original.getLocation().getIndex();
        implementation.addInstruction(index, instruction);
        int shift = instruction.getCodeUnits();
        Utils.shiftIntegerMapKeys(address - 1, shift, addressToInstruction);
        addressToInstruction.put(address, instruction);

        // Insert + shift nodes
        List<ExecutionNode> shiftedNodePile = addressToNodePile.get(address);
        shiftNodePileAddresses(address - 1, shift);
        List<ExecutionNode> addedNodePile = new ArrayList<ExecutionNode>();
        addressToNodePile.put(address, addedNodePile);

        // Create + execute new nodes, adjust parent-child relationships
        Op op = opFactory.create(instruction, address);
        for (index = 0; index < shiftedNodePile.size(); index++) {
            ExecutionNode newNode = new ExecutionNode(op);
            addedNodePile.add(index, newNode);

            ExecutionNode shiftedNode = shiftedNodePile.get(index);
            if (TEMPLATE_NODE_INDEX == index) {
                assert shiftedNode.getContext() == null;
                assert shiftedNode.getParent() == null;
                assert shiftedNode.getChildren().size() == 0;

                continue;
            }

            ExecutionNode parentNode = shiftedNode.getParent();
            ExecutionContext newContext;
            if (parentNode != null) {
                parentNode.replaceChild(shiftedNode, newNode);
                newContext = parentNode.getContext().getChild();
            } else {
                assert METHOD_ROOT_ADDRESS == address;
                newContext = vm.getRootExecutionContext(methodDescriptor);
            }
            shiftedNode.setParent(newNode);

            newNode.setContext(newContext);
            newNode.execute();
        }
    }

    private void removeNodePile(int address, int shift) {
        List<ExecutionNode> nodePile = addressToNodePile.get(address);
        for (ExecutionNode removedNode : nodePile) {
            ExecutionNode parentNode = removedNode.getParent();
            if (parentNode != null) {
                parentNode.removeChild(removedNode);
            }
            for (ExecutionNode childNode : removedNode.getChildren()) {
                // parentNode could be null, and that's ok
                childNode.setParent(parentNode);
            }
        }

        shiftNodePileAddresses(address, shift);
    }

    protected void replaceInstruction(int address, int shift, Op op, int codeUnits) {
        List<ExecutionNode> nodePile = addressToNodePile.get(address);
        Map<ExecutionNode, ExecutionNode> replacedToNew = new HashMap<ExecutionNode, ExecutionNode>();
        for (int index = 0; index < nodePile.size(); index++) {
            ExecutionNode replacedNode = nodePile.get(index);
            ExecutionNode newNode = new ExecutionNode(op);
            nodePile.set(index, newNode);

            if (TEMPLATE_NODE_INDEX == index) {
                assert replacedNode.getContext() == null;
                assert replacedNode.getParent() == null;
                assert replacedNode.getChildren().size() == 0;

                continue;
            }

            for (ExecutionNode child : replacedNode.getChildren()) {
                child.setParent(newNode);
            }

            ExecutionNode parentNode = replacedNode.getParent();
            ExecutionContext newContext;
            if (parentNode != null) {
                parentNode.replaceChild(replacedNode, newNode);
                newContext = parentNode.getContext().getChild();
            } else {
                assert METHOD_ROOT_ADDRESS == address;
                newContext = vm.getRootExecutionContext(methodDescriptor);
            }
            newNode.setContext(newContext);
            newNode.execute();

            replacedToNew.put(replacedNode, newNode);
        }

        // Update any children's parents to the new nodes we made.
        shiftNodePileAddresses(address, shift);
        // int childAddress = address + codeUnits;
        // nodePile = getNodePile(childAddress);
        // if (nodePile != null) {
        // for (ExecutionNode node : nodePile) {
        // ExecutionNode parent = node.getParent();
        // if (replacedToNew.containsKey(parent)) {
        // node.setParent(replacedToNew.get(parent));
        // }
        // }
        // }
    }

    TIntList getReachedAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addressToInstruction.keys()) {
            if (wasAddressReached(address)) {
                result.add(address);
            }
        }

        return result;
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

    public List<ExecutionNode> getChildren(int address) {
        List<ExecutionNode> children = new ArrayList<ExecutionNode>();
        List<ExecutionNode> nodePile = getNodePile(address);
        for (ExecutionNode node : nodePile) {
            children.addAll(node.getChildren());
        }

        return children;
    }

    public TIntList getAvailableRegisters(int address) {
        Deque<ExecutionNode> stack = new ArrayDeque<ExecutionNode>(getChildren(address));
        ExecutionNode node = stack.peek();
        if (null == node) {
            // Edge case.
            assert (getTemplateNode(address).getOp() instanceof ReturnOp)
                            || (getTemplateNode(address).getOp() instanceof ReturnVoidOp);
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
            MethodState mState = node.getContext().getMethodState();
            for (int register : registers) {
                if (registersRead.contains(register) || registersAssigned.contains(register)) {
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

}
