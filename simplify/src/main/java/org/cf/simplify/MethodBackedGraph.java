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
import java.util.Map.Entry;
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

    private static final int INSERT_SHIFT = 0xf;

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

            int[] childAddresses = op.getChildren();
            if (childAddresses.length > 0) {
                for (int i = 0; i < childAddresses.length; i++) {
                    childAddresses[i] += shift;
                }
                op.setChildren(childAddresses);
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

    private void reorderGraphAfterRemove() {
        TIntObjectMap<BuilderInstruction> newAddressToInstruction = buildAddressToInstruction(implementation
                        .getInstructions());
        int[] newAddresses = newAddressToInstruction.keys();
        Arrays.sort(newAddresses);

        int[] oldAddresses = addressToInstruction.keys();
        Arrays.sort(oldAddresses);

        Map<Integer, Integer> oldToNewAddresses = new HashMap<Integer, Integer>();
        for (int i = 0; i < oldAddresses.length; i++) {
            int oldAddress = oldAddresses[i];
            int newAddress = newAddresses[i];

            List<ExecutionNode> nodePile = addressToNodePile.get(oldAddress);
            addressToNodePile.remove(oldAddress);
            addressToNodePile.put(newAddress, nodePile);

            oldToNewAddresses.put(oldAddresses[i], newAddresses[i]);
        }

        reorderGraph(oldToNewAddresses, newAddressToInstruction);
    }

    private void reorderGraphAfterInsert(int insertAddress, BuilderInstruction instruction) {
        TIntObjectMap<BuilderInstruction> newAddressToInstruction = buildAddressToInstruction(implementation
                        .getInstructions());
        int[] newAddresses = newAddressToInstruction.keys();
        Arrays.sort(newAddresses);

        int[] shiftedAddresses = addressToInstruction.keys();
        Arrays.sort(shiftedAddresses);
        Map<Integer, Integer> oldToNewAddresses = new HashMap<Integer, Integer>();
        for (int i = 0; i < shiftedAddresses.length; i++) {
            int oldAddress = shiftedAddresses[i];
            if (oldAddress < insertAddress) {
                oldToNewAddresses.put(shiftedAddresses[i], newAddresses[i]);
            } else if (oldAddress == insertAddress) {
                continue;
            } else {
                oldToNewAddresses.put(shiftedAddresses[i] - INSERT_SHIFT, newAddresses[i]);
            }
        }

        Integer[] oldAddresses = oldToNewAddresses.keySet().toArray(new Integer[oldToNewAddresses.size()]);
        Arrays.sort(oldAddresses);
        for (int i = 0; i < oldAddresses.length; i++) {
            int oldAddress = oldAddresses[i];
            if (oldAddress < insertAddress) {
                continue;
            }
            int newAddress = oldToNewAddresses.get(oldAddress);
            int shiftedAddress = oldAddress + INSERT_SHIFT;
            List<ExecutionNode> nodePile = addressToNodePile.get(shiftedAddress);
            addressToNodePile.remove(shiftedAddress);
            addressToNodePile.put(newAddress, nodePile);
        }
        reorderGraph(oldToNewAddresses, newAddressToInstruction);
    }

    private void reorderGraph(Map<Integer, Integer> oldToNewAddresses,
                    TIntObjectMap<BuilderInstruction> newAddressToInstruction) {
        addressToInstruction.clear();
        addressToInstruction.putAll(newAddressToInstruction);

        for (Entry<Integer, Integer> entry : oldToNewAddresses.entrySet()) {
            int oldAddress = entry.getKey();
            int newAddress = entry.getValue();
            // TODO: continue until these are different

            // All nodes at an address have the same op. No need to modify the entire node pile.
            System.out.println("addy: " + newAddress);
            if (newAddress == 129) {
                System.out.println("got here");
            }
            Op op = getTemplateNode(newAddress).getOp();
            op.setAddress(newAddress);

            // Op name can change, e.g. GOTO -> GOTO/16
            // Can happen in implementation#fixInstructions if instructions are inserted
            BuilderInstruction instruction = addressToInstruction.get(newAddress);
            String opName = instruction.getOpcode().name;
            op.setOpName(opName);

            int[] childAddresses = op.getChildren();
            if (childAddresses.length > 0) {
                for (int j = 0; j < childAddresses.length; j++) {
                    int oldChildAddress = childAddresses[j];
                    if (!oldToNewAddresses.containsKey(oldChildAddress)) {
                        System.out.println("doesn't have it");
                    }
                    childAddresses[j] = oldToNewAddresses.get(oldChildAddress);
                }
                op.setChildren(childAddresses);
            }
        }
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
            addressToNodePile.remove(address);
            addressToInstruction.remove(address);
        }

        // Remove in reverse order because implementation reorders on removal
        indexes.sort();
        indexes.reverse();
        for (int index : indexes.toArray()) {
            tryMigrateLabels(index);

            implementation.removeInstruction(index);
        }

        removeEmptyTryCatchBlocks();
        reorderGraphAfterRemove();
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
        removeInstruction(address);
        insertInstruction(address, replacement);
    }

    public void replaceWithMultipleInstructions(int address, List<BuilderInstruction> replacements) {
        int insertAddress = address;
        for (BuilderInstruction replacement : replacements) {
            if (insertAddress == address) {
                replaceInstruction(insertAddress, replacement);
            } else {
                insertInstruction(insertAddress, replacement);
            }
            insertAddress += replacement.getCodeUnits();
        }
    }

    public void insertInstruction(int address, BuilderInstruction instruction) {
        if (addressToInstruction.size() == 128) {
            System.out.println("getting big");
        }
        int index = addressToInstruction.get(address).getLocation().getIndex();
        implementation.addInstruction(index, instruction);
        implementation.getInstructions(); // fixInstructions

        List<ExecutionNode> oldNodePile = addressToNodePile.get(address);
        Utils.shiftIntegerMapKeys(address - 1, INSERT_SHIFT, addressToInstruction);
        Utils.shiftIntegerMapKeys(address - 1, INSERT_SHIFT, addressToNodePile);

        // Address may have changed if other ops were "fixed"
        address = instruction.getLocation().getCodeAddress();

        // Shift by static amount and reconstruct actual addresses since implementation
        // may be changed by dexlib after insertion, e.g. goto -> goto/16
        addressToInstruction.put(address, instruction);

        List<ExecutionNode> newNodePile = new ArrayList<ExecutionNode>();
        addressToNodePile.put(address, newNodePile);

        Op op = opFactory.create(instruction, address);
        for (index = 0; index < oldNodePile.size(); index++) {
            ExecutionNode newNode = new ExecutionNode(op);
            newNodePile.add(index, newNode);

            ExecutionNode shiftedNode = oldNodePile.get(index);
            if (TEMPLATE_NODE_INDEX == index) {
                continue;
            }

            ExecutionNode parentNode = shiftedNode.getParent();
            ExecutionContext newContext;
            if (parentNode != null) {
                parentNode.replaceChild(shiftedNode, newNode);
                newContext = parentNode.getContext().spawnChild();
            } else {
                assert METHOD_ROOT_ADDRESS == address;
                newContext = vm.spawnExecutionContext(methodDescriptor);
            }
            shiftedNode.setParent(newNode);
            newNode.setContext(newContext);

            // Execute to properly set children and any other context
            newNode.execute();
        }

        reorderGraphAfterInsert(address, instruction);
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
