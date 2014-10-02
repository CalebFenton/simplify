package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.lang.reflect.Field;
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
            System.out.println("@" + address + " is index " + location.getIndex());
            if (log.isDebugEnabled()) {
                log.debug("@" + address + " is index " + location.getIndex());
            }
            indexes.add(location.getIndex());

            // Shifting down will remove this address
            int shift = -instruction.getCodeUnits();
            Utils.shiftIntegerMapKeys(address, shift, addressToInstruction);
            removeNodes(address, instruction.getCodeUnits());
        }

        // Need to remove in reverse order or indexes will get out of sync.
        indexes.sort();
        indexes.reverse();
        for (int index : indexes.toArray()) {
            migrateLabelsIfPossible(index);

            implementation.removeInstruction(index);

            removeEmptyTryCatchBlocks();
        }
    }

    private void removeEmptyTryCatchBlocks() {
        List<BuilderTryBlock> rb = new ArrayList<BuilderTryBlock>();
        for (BuilderTryBlock tryBlock : implementation.getTryBlocks()) {
            MethodLocation startLocation = tryBlock.start.getLocation();
            MethodLocation endLocation = tryBlock.end.getLocation();
            if (startLocation.getCodeAddress() == endLocation.getCodeAddress()) {
                // Empty!
                List<Label> remove = new ArrayList<Label>(3);
                remove.add(tryBlock.start);
                remove.add(tryBlock.end);
                remove.add(tryBlock.exceptionHandler.getHandler());
                startLocation.getLabels().retainAll(remove);

                rb.add(tryBlock);
            }
        }

        // tryBlocks is immutable, and the tryBlock isn't removed, dex fails to decompile
        // should probably submit a patch...
        try {
            Field f = implementation.getClass().getDeclaredField("tryBlocks");
            f.setAccessible(true);
            ArrayList<BuilderTryBlock> tryBlocks = (ArrayList<BuilderTryBlock>) f.get(implementation);
            tryBlocks.removeAll(rb);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void migrateLabelsIfPossible(int index) {
        /*
         * If it has a label, try and move it down. If it can't move down, just delete it. The only two instructions at
         * the bottom of a method are return and goto. Or else instructions will run past end of method.
         */
        int instructionCount = implementation.getInstructions().size();
        if (index < (instructionCount - 1)) {
            BuilderInstruction moveFrom = implementation.getInstructions().get(index);
            Set<Label> labelSet = moveFrom.getLocation().getLabels();
            if (1 < labelSet.size()) {
                BuilderInstruction moveTo = implementation.getInstructions().get(index + 1);
                List<Label> labels = new ArrayList<Label>(labelSet.size());
                moveFrom.getLocation().getLabels().removeAll(labelSet);
                for (Label label : labels) {
                    moveTo.getLocation().getLabels().add(label);
                }
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

    protected void removeNodes(int address, int codeUnits) {
        List<ExecutionNode> nodePile = addressToNodePile.get(address);
        for (ExecutionNode removedNode : nodePile) {
            ExecutionNode parentNode = removedNode.getParent();
            for (ExecutionNode childNode : removedNode.getChildren()) {
                // parentNode could be null, and that's ok
                childNode.setParent(parentNode);
            }

            if (parentNode != null) {
                parentNode.removeChild(removedNode);
            }
        }

        // addressToNodePile.remove(address);
        Utils.shiftIntegerMapKeys(address, -codeUnits, addressToNodePile);
    }

    /*
     * Need to be able to update a graph to pass around between optimization strategies. This does a shallow update, not
     * touching any handlers or individual nodes. It just updates addressToNodePile by shifting addresses up or down,
     * depending on delta between old and new instruction. It also executes the new instruction to build a realistic
     * context to help optimizer, i.e. assigned registers, etc.
     */
    protected void replaceInstruction(int address, int addressShift, Op op, int codeUnits) {
        Utils.shiftIntegerMapKeys(address, addressShift, addressToNodePile);

        List<ExecutionNode> nodePile = addressToNodePile.get(address);
        Map<ExecutionNode, ExecutionNode> oldToNew = new HashMap<ExecutionNode, ExecutionNode>();
        for (int index = 0; index < nodePile.size(); index++) {
            ExecutionNode replacedNode = nodePile.get(index);
            ExecutionNode newNode = new ExecutionNode(op);

            nodePile.remove(replacedNode);
            nodePile.add(index, newNode);

            if (index == 0) {
                // Template node shouldn't have children or context.
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
                newContext = vm.getRootExecutionContext(methodDescriptor);
            }
            newNode.setContext(newContext);
            newNode.execute();

            oldToNew.put(replacedNode, newNode);
        }

        // Update any children's parents to the new nodes we made.
        int childAddress = address + codeUnits;
        nodePile = getNodePile(childAddress);
        if (nodePile != null) {
            for (ExecutionNode node : nodePile) {
                ExecutionNode parent = node.getParent();
                if (oldToNew.containsKey(parent)) {
                    node.setParent(oldToNew.get(parent));
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
