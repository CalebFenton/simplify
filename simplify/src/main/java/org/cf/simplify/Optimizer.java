package org.cf.simplify;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class Optimizer {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    public static TIntObjectMap<BuilderInstruction> buildAddressToInstruction(List<BuilderInstruction> instructions) {
        TIntObjectMap<BuilderInstruction> result = new TIntObjectHashMap<BuilderInstruction>();
        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            result.put(address, instruction);
        }

        return result;
    }

    private final DexBuilder dexBuilder;
    private final BuilderMethod method;
    private final String methodDescriptor;
    private final ContextGraph graph;
    private final MutableMethodImplementation implementation;
    private final TIntObjectMap<BuilderInstruction> addressToInstruction;
    private final VirtualMachine vm;
    private final OpFactory opFactory;

    private int deadCount = 0;
    private int emitCount = 0;
    private int deadAssignmentCount = 0;
    private int deadResultCount = 0;
    private int deadBranchCount = 0;
    private int peeps = 0;

    public Optimizer(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph, VirtualMachine vm) {
        this.dexBuilder = dexBuilder;
        this.method = method;
        this.methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        this.graph = graph;
        implementation = (MutableMethodImplementation) method.getImplementation();
        addressToInstruction = buildAddressToInstruction(implementation.getInstructions());
        this.vm = vm;
        opFactory = new OpFactory(vm, methodDescriptor);
    }

    public boolean simplify(int maxSweeps) {
        System.out.println("Simplifying: " + methodDescriptor);

        propigateConstants();
        performPeepholeOptimizations();

        int sweep = 0;
        boolean madeChanges = false;
        do {
            madeChanges = removeDeadOps();
            sweep++;
        } while (((sweep < maxSweeps) && (maxSweeps > 0)) && madeChanges);

        String result = String
                        .format("Optimized instructions: consts=%d, dead(address=%d, results=%d, assigns=%d, branches=%d), peeps=%d",
                                        emitCount, deadCount, deadResultCount, deadAssignmentCount, deadBranchCount,
                                        peeps);
        System.out.println(result);

        return (emitCount > 0) || (deadResultCount > 0) || (deadAssignmentCount > 0) || (deadCount > 0);
    }

    PeepholeOptimizer getPeepholeOptimizer() {
        PeepholeOptimizer result = new PeepholeOptimizer(dexBuilder, method, graph, vm, implementation,
                        addressToInstruction, opFactory, this);

        return result;
    }

    private void performPeepholeOptimizations() {
        PeepholeOptimizer po = getPeepholeOptimizer();
        peeps = po.perform();
    }

    private void propigateConstants() {
        for (int address : addressToInstruction.keys()) {
            BuilderInstruction originalInstruction = addressToInstruction.get(address);
            BuilderInstruction constInstruction = ConstantBuilder.buildConstantForAddress(address, graph,
                            originalInstruction, dexBuilder);
            if (constInstruction != null) {
                replaceInstruction(address, originalInstruction, constInstruction);
                emitCount++;
            }
        }
    }

    private boolean removeDeadOps() {
        List<BuilderTryBlock> tryBlocks = implementation.getTryBlocks();
        DeadDetector deadDetector = new DeadDetector(graph, addressToInstruction, tryBlocks);
        TIntSet removeSet = new TIntHashSet();
        TIntList addresses;
        addresses = deadDetector.getDeadAddresses();
        deadCount += addresses.size();
        removeSet.addAll(addresses);

        addresses = deadDetector.getDeadAssignmentAddresses();
        deadAssignmentCount += addresses.size();
        removeSet.addAll(addresses);

        addresses = deadDetector.getDeadResultAddresses();
        deadResultCount += addresses.size();
        removeSet.addAll(addresses);

        addresses = deadDetector.getDeadBranchAddresses();
        deadBranchCount += addresses.size();
        removeSet.addAll(addresses);

        addresses = new TIntArrayList(removeSet.toArray());
        removeInstructions(addresses);

        return addresses.size() > 0;
    }

    void removeInstruction(int address) {
        TIntList addresses = new TIntArrayList(new int[] { address });
        removeInstructions(addresses);
    }

    void removeInstructions(TIntList addresses) {
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
            graph.removeInstruction(address, instruction.getCodeUnits());
        }

        // Need to remove in reverse order or indexes will get out of sync.
        indexes.sort();
        indexes.reverse();
        for (int index : indexes.toArray()) {
            implementation.removeInstruction(index);
        }
    }

    void replaceInstruction(int address, BuilderInstruction originalInstruction,
                    BuilderInstruction replacementInstruction) {
        int index = originalInstruction.getLocation().getIndex();
        implementation.replaceInstruction(index, replacementInstruction);
        addressToInstruction.put(address, replacementInstruction);

        int codeUnits = replacementInstruction.getCodeUnits();
        int shift = codeUnits - originalInstruction.getCodeUnits();
        Utils.shiftIntegerMapKeys(address, shift, addressToInstruction);

        Op handler = opFactory.create(replacementInstruction, address);
        graph.replaceInstruction(address, shift, handler, codeUnits);
    }
}
