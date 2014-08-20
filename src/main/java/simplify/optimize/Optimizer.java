package simplify.optimize;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Main;
import simplify.util.Utils;
import simplify.vm.VirtualMachine;
import simplify.vm.context.ContextGraph;
import simplify.vm.op_handler.Op;
import simplify.vm.op_handler.OpFactory;

public class Optimizer {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static TIntObjectMap<BuilderInstruction> buildAddressToInstruction(List<BuilderInstruction> instructions) {
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
    private final OpFactory opFactory;

    private int deadCount = 0;
    private int emitCount = 0;
    private int unusedAssignmentCount = 0;
    private int unusedResultCount = 0;

    public Optimizer(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph, VirtualMachine vm) {
        this.dexBuilder = dexBuilder;
        this.method = method;
        this.methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        this.graph = graph;
        implementation = (MutableMethodImplementation) method.getImplementation();
        addressToInstruction = buildAddressToInstruction(implementation.getInstructions());
        opFactory = new OpFactory(vm, methodDescriptor);
    }

    public boolean simplify(int maxSweeps) {
        System.out.println("Simplifying: " + methodDescriptor);

        int sweep = 0;
        propigateConstants();
        boolean madeChanges = false;
        do {
            madeChanges = removeDeadOps();
            sweep++;
        } while (((sweep < maxSweeps) && (maxSweeps > 0)) && madeChanges);

        String result = String.format("Optimized instructions: consts=%d, dead=%d, unused(results=%d, assigns=%d)",
                        emitCount, deadCount, unusedResultCount, unusedAssignmentCount);
        System.out.println(result);

        return (emitCount > 0) || (unusedResultCount > 0) || (unusedAssignmentCount > 0) || (deadCount > 0);
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
        TIntList validAddresses = DeadDetector.getValidAddresses(addressToInstruction, tryBlocks, graph);
        TIntSet removeSet = new TIntHashSet();
        TIntList addresses;
        addresses = DeadDetector.getDeadAddresses(validAddresses, graph);
        deadCount += addresses.size();
        removeSet.addAll(addresses);

        addresses = DeadDetector.getUnusedAssignmentAddresses(validAddresses, graph);
        unusedAssignmentCount += addresses.size();
        removeSet.addAll(addresses);

        addresses = DeadDetector.getUnusedResultAddresses(validAddresses, graph, addressToInstruction);
        unusedResultCount += addresses.size();
        removeSet.addAll(addresses);

        addresses = new TIntArrayList(removeSet.toArray());
        removeInstructions(addresses);

        return addresses.size() > 0;
    }

    private void removeInstructions(TIntList removeAddresses) {
        /*
         * Implementation removes by index, not address. Collect indexes and update addressToInstruction map. Also,
         * while looping, remove orphaned debug items, or you're gonna have a bad time.
         */
        TIntList indexes = new TIntArrayList();
        removeAddresses.sort();
        removeAddresses.reverse();
        log.info("Remove addresses: " + removeAddresses);
        for (int address : removeAddresses.toArray()) {
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

    private void replaceInstruction(int address, BuilderInstruction originalInstruction,
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
