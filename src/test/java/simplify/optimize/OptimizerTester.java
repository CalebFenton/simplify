package simplify.optimize;

import gnu.trove.map.TIntObjectMap;

import java.util.Arrays;
import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.vm.VMTester;
import simplify.vm.VirtualMachine;
import simplify.vm.context.ContextGraph;
import simplify.vm.op_handler.OpFactory;

public class OptimizerTester {

    static DeadDetector getDeadDetector(String className, String methodSignature) {
        BuilderMethod method = VMTester.getBuilderMethod(className, methodSignature);
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();
        TIntObjectMap<BuilderInstruction> addressToInstruction = Optimizer.buildAddressToInstruction(instructions);
        List<BuilderTryBlock> tryBlocks = implementation.getTryBlocks();

        ContextGraph graph = VMTester.execute(className, methodSignature);
        DeadDetector result = new DeadDetector(graph, addressToInstruction, tryBlocks);

        return result;
    }

    static PeepholeOptimizer getPeepholeOptimizer(String className, String methodSignature) {
        DexBuilder dexBuilder = VMTester.getDexBuilder();
        BuilderMethod method = VMTester.getBuilderMethod(className, methodSignature);
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();
        TIntObjectMap<BuilderInstruction> addressToInstruction = Optimizer.buildAddressToInstruction(instructions);

        BuilderClassDef classDef = VMTester.getBuilderClassDef(className);
        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        String methodDescriptor = className + "->" + methodSignature;
        OpFactory opFactory = new OpFactory(vm, methodDescriptor);
        ContextGraph graph = vm.execute(methodDescriptor);

        PeepholeOptimizer result = new PeepholeOptimizer(dexBuilder, method, graph, vm, implementation,
                        addressToInstruction, opFactory);

        return result;
    }

}
