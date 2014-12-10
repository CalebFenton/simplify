package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.OpFactory;
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
        opFactory = new OpFactory(vm, methodDescriptor);
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

        // Remove from the end to avoid re-indexing
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

    public void insertInstruction(int address, BuilderInstruction instruction) {
        BuilderInstruction original = addressToInstruction.get(address);
        int index = original.getLocation().getIndex();

        // addInstruction(int, instruction) will insert with reindex
        implementation.addInstruction(index, instruction);
        int shift = instruction.getCodeUnits();
        Utils.shiftIntegerMapKeys(address - original.getCodeUnits(), shift, addressToInstruction);
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

        // addressToNodePile.remove(address);
        // Ops in node pile still have old addresses
        Utils.shiftIntegerMapKeys(address, shift, addressToNodePile);
    }

    /*
     * Need to be able to update a graph to pass around between optimization strategies. This does a shallow update, not
     * touching any handlers or individual nodes. It just updates addressToNodePile by shifting addresses up or down,
     * depending on delta between old and new instruction. It also executes the new instruction to build a realistic
     * context to help optimizer, i.e. assigned registers, etc.
     */
    protected void replaceInstruction(int address, int addressShift, Op op, int codeUnits) {
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
        Utils.shiftIntegerMapKeys(address, addressShift, addressToNodePile);
        int childAddress = address + codeUnits;
        nodePile = getNodePile(childAddress);
        if (nodePile != null) {
            for (ExecutionNode node : nodePile) {
                ExecutionNode parent = node.getParent();
                if (replacedToNew.containsKey(parent)) {
                    node.setParent(replacedToNew.get(parent));
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

    public List<ExecutionNode> getChildrenAtAddress(int address) {
        List<ExecutionNode> children = new ArrayList<ExecutionNode>();
        List<ExecutionNode> nodePile = getNodePile(address);
        for (ExecutionNode node : nodePile) {
            children.addAll(node.getChildren());
        }

        return children;
    }

}
