package org.cf.simplify;

import gnu.trove.map.TIntObjectMap;

import java.util.Arrays;
import java.util.List;

import org.cf.simplify.DeadDetector;
import org.cf.simplify.Optimizer;
import org.cf.simplify.PeepholeOptimizer;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

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

    static Optimizer getOptimizer(String className, String methodSignature) {
        DexBuilder dexBuilder = VMTester.getDexBuilder();
        BuilderMethod method = VMTester.getBuilderMethod(className, methodSignature);
        BuilderClassDef classDef = VMTester.getBuilderClassDef(className);
        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        String methodDescriptor = className + "->" + methodSignature;
        ContextGraph graph = vm.execute(methodDescriptor);
        Optimizer result = new Optimizer(dexBuilder, method, graph, vm);

        return result;
    }

    static PeepholeOptimizer getPeepholeOptimizer(String className, String methodSignature) {
        Optimizer opt = getOptimizer(className, methodSignature);

        return opt.getPeepholeOptimizer();
    }

}
