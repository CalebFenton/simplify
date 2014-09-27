package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.OpFactory;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
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
        log.info("Remove addresses: " + addresses);
        for (int address : addresses.toArray()) {
            BuilderInstruction instruction = addressToInstruction.get(address);
            MethodLocation location = instruction.getLocation();
            location.getDebugItems().clear();
            log.debug("@" + addresses + " is index " + location.getIndex());
            indexes.add(location.getIndex());

            // Shifting down will remove this address
            int shift = -instruction.getCodeUnits();
            Utils.shiftIntegerMapKeys(address, shift, addressToInstruction);
            removeInstruction(address, instruction.getCodeUnits());
        }

        // Need to remove in reverse order or indexes will get out of sync.
        indexes.sort();
        indexes.reverse();
        for (int index : indexes.toArray()) {
            implementation.removeInstruction(index);
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

        Op handler = opFactory.create(replacement, address);
        replaceInstruction(address, shift, handler, codeUnits);
    }

    protected void removeInstruction(int address, int codeUnits) {
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
    protected void replaceInstruction(int address, int addressShift, Op handler, int codeUnits) {
        Utils.shiftIntegerMapKeys(address, addressShift, addressToNodePile);

        List<ExecutionNode> nodePile = addressToNodePile.get(address);
        Map<ExecutionNode, ExecutionNode> oldToNew = new HashMap<ExecutionNode, ExecutionNode>();
        for (int index = 0; index < nodePile.size(); index++) {
            ExecutionNode replacedNode = nodePile.get(index);
            ExecutionNode newNode = new ExecutionNode(handler);

            nodePile.remove(replacedNode);
            nodePile.add(index, newNode);
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
        nodePile = addressToNodePile.get(childAddress);
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
