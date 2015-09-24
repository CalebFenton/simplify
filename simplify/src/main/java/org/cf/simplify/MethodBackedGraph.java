package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.OpCreator;
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

    private final TIntObjectMap<BuilderInstruction> addressToInstruction;
    private final DexBuilder dexBuilder;
    private final MutableMethodImplementation implementation;
    private final BuilderMethod method;
    private final String methodDescriptor;
    private final OpCreator opCreator;
    private final VirtualMachine vm;

    public MethodBackedGraph(ExecutionGraph graph, BuilderMethod method, VirtualMachine vm, DexBuilder dexBuilder) {
        super(graph, true);

        this.dexBuilder = dexBuilder;
        this.method = method;
        this.methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        implementation = (MutableMethodImplementation) method.getImplementation();
        addressToInstruction = ExecutionGraph.buildAddressToInstruction(implementation.getInstructions());
        this.vm = vm;
        opCreator = new OpCreator(vm, addressToInstruction);
    }

    public TIntObjectMap<BuilderInstruction> getAddressToInstruction() {
        return addressToInstruction;
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

    public BuilderInstruction getInstruction(int address) {
        return addressToInstruction.get(address);
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

    public void insertInstruction(int address, BuilderInstruction instruction) {
        int index = addressToInstruction.get(address).getLocation().getIndex();
        implementation.addInstruction(index, instruction);

        List<ExecutionNode> oldNodePile = addressToNodePile.get(address);

        // Address may have changed if other ops were "fixed"
        int newAddress = instruction.getLocation().getCodeAddress();
        // Make room in address mappings
        int shift = instruction.getCodeUnits() + (newAddress - address);
        Utils.shiftIntegerMapKeys(address - 1, shift, addressToInstruction);
        Utils.shiftIntegerMapKeys(address - 1, shift, addressToNodePile);
        addressToInstruction.put(address, instruction);
        List<ExecutionNode> newNodePile = new ArrayList<ExecutionNode>();
        addressToNodePile.put(address, newNodePile);

        if (address != newAddress || fixInstructions()) {
            implementation.getInstructions(); // force fixInstructions
            rebuildGraphFromImplementation();
            address = newAddress;
        }

        Op op = opCreator.create(instruction);
        for (index = 0; index < oldNodePile.size(); index++) {
            ExecutionNode newNode = new ExecutionNode(op);
            newNodePile.add(index, newNode);

            if (index == TEMPLATE_NODE_INDEX) {
                continue;
            }

            ExecutionNode shiftedNode = oldNodePile.get(index);
            ExecutionNode parentNode = shiftedNode.getParent();
            ExecutionContext newContext;
            if (parentNode != null) {
                parentNode.replaceChildNode(shiftedNode, newNode);
                newContext = parentNode.getContext().spawnChild();
            } else {
                assert METHOD_ROOT_ADDRESS == address;
                newContext = vm.spawnExecutionContext(methodDescriptor);
            }
            shiftedNode.setParentNode(newNode);
            newNode.setContext(newContext);

            // Execute to properly set children and any other context
            newNode.execute();
        }
    }

    public void removeInstruction(int address) {
        removeInstruction_private(address);
        removeEmptyTryCatchBlocks();
        rebuildGraphFromImplementation();
    }

    public void removeInstructions(TIntList addresses) {
        /*
         * Implementation removes by index, not address. Collect indexes and update addressToInstruction map. Also,
         * while looping, remove orphaned debug items, or you're gonna have a bad time.
         */
        addresses.sort();
        addresses.reverse();
        if (log.isInfoEnabled()) {
            log.info("Remove addresses: " + addresses);
        }

        for (int address : addresses.toArray()) {
            removeInstruction(address);
        }

        removeEmptyTryCatchBlocks();
        rebuildGraphFromImplementation();
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

    private boolean fixInstructions() {
        boolean fixInstructions = false;
        try {
            Field f = implementation.getClass().getDeclaredField("fixInstructions");
            f.setAccessible(true);
            fixInstructions = (boolean) f.get(implementation);
        } catch (Exception ex) {
            fixInstructions = true;
        }

        return fixInstructions;
    }

    private void rebuildGraphFromImplementation() {
        // Expects address maps to at least be in same order as implementation
        TIntObjectMap<BuilderInstruction> newAddressToInstruction = ExecutionGraph
                        .buildAddressToInstruction(implementation.getInstructions());
        TIntObjectMap<List<ExecutionNode>> newAddressToNodePile = new TIntObjectHashMap<List<ExecutionNode>>(
                        addressToNodePile.size());

        int[] newAddresses = newAddressToInstruction.keys();
        Arrays.sort(newAddresses);

        int[] oldAddresses = addressToInstruction.keys();
        Arrays.sort(oldAddresses);

        for (int i = 0; i < newAddresses.length; i++) {
            int newAddress = newAddresses[i];
            int oldAddress = oldAddresses[i];

            BuilderInstruction newInstruction = newAddressToInstruction.get(newAddress);
            BuilderInstruction oldInstruction = addressToInstruction.get(oldAddress);

            if (newInstruction != oldInstruction) {
                // It's been "fixed"
                updateInstructionsInAllOps(oldInstruction, newInstruction);
            }

            List<ExecutionNode> nodePile = addressToNodePile.get(oldAddress);
            newAddressToNodePile.put(newAddress, nodePile);
        }

        addressToInstruction.clear();
        addressToInstruction.putAll(newAddressToInstruction);

        addressToNodePile.clear();
        addressToNodePile.putAll(newAddressToNodePile);
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

    private void removeInstruction_private(int address) {
        BuilderInstruction instruction = addressToInstruction.get(address);
        MethodLocation location = instruction.getLocation();
        location.getDebugItems().clear();
        int index = location.getIndex();
        if (log.isDebugEnabled()) {
            log.debug("@" + address + " is index " + index);
        }

        List<ExecutionNode> nodePile = addressToNodePile.get(address);
        for (ExecutionNode removedNode : nodePile) {
            ExecutionNode parentNode = removedNode.getParent();
            if (parentNode != null) {
                parentNode.removeChildNode(removedNode);
            }
            for (ExecutionNode childNode : removedNode.getChildren()) {
                // parentNode could be null, and that's ok
                childNode.setParentNode(parentNode);
            }
        }

        int shift = -instruction.getCodeUnits();

        tryMigrateLabels(index);
        implementation.removeInstruction(index);
        Utils.shiftIntegerMapKeys(address, shift, addressToInstruction);
        Utils.shiftIntegerMapKeys(address, shift, addressToNodePile);
    }

    private void tryMigrateLabels(int index) {
        // If it has a label, and it's not the last instruction, move it to the next instruction.
        // TODO: *easy* replace getInstructions with one call
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

    private void updateInstructionsInAllOps(BuilderInstruction oldInstruction, BuilderInstruction newInstruction) {
        for (int address : addressToNodePile.keys()) {
            List<ExecutionNode> nodePile = addressToNodePile.get(address);
            for (ExecutionNode node : nodePile) {
                Op op = node.getOp();
                if (oldInstruction == op.getInstruction()) {
                    op.setInstruction(newInstruction);
                }

                boolean madeChanges = false;
                BuilderInstruction[] children = op.getChildren();
                for (int i = 0; i < children.length; i++) {
                    BuilderInstruction instr = children[i];
                    if (oldInstruction == instr) {
                        children[i] = newInstruction;
                        madeChanges = true;
                    }
                }
                if (madeChanges) {
                    op.setChildren(children);
                }
            }
        }
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

}
