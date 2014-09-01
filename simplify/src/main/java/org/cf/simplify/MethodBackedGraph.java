package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.op_handler.Op;
import org.cf.smalivm.op_handler.OpFactory;
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

public class MethodBackedGraph extends ContextGraph {

    private static TIntObjectMap<BuilderInstruction> buildAddressToInstruction(List<BuilderInstruction> instructions) {
        TIntObjectMap<BuilderInstruction> result = new TIntObjectHashMap<BuilderInstruction>();
        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            result.put(address, instruction);
        }

        return result;
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MethodBackedGraph.class.getSimpleName());

    private final DexBuilder dexBuilder;
    private final BuilderMethod method;
    private final String methodDescriptor;
    private final MutableMethodImplementation implementation;
    private final TIntObjectMap<BuilderInstruction> addressToInstruction;
    private final VirtualMachine vm;
    private final OpFactory opFactory;

    public MethodBackedGraph(ContextGraph graph, BuilderMethod method, VirtualMachine vm, DexBuilder dexBuilder) {
        super(graph, true);

        this.dexBuilder = dexBuilder;
        this.method = method;
        this.methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        implementation = (MutableMethodImplementation) method.getImplementation();
        addressToInstruction = buildAddressToInstruction(implementation.getInstructions());
        this.vm = vm;
        opFactory = new OpFactory(vm, methodDescriptor);
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

    public void removeInstruction(int address) {
        TIntList addresses = new TIntArrayList(new int[] { address });
        removeInstructions(addresses);
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

    public BuilderInstruction getInstruction(int address) {
        return addressToInstruction.get(address);
    }

    public DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    public List<BuilderTryBlock> getTryBlocks() {
        return implementation.getTryBlocks();
    }

    public TIntObjectMap<BuilderInstruction> getAddressToInstruction() {
        return addressToInstruction;
    }

}
