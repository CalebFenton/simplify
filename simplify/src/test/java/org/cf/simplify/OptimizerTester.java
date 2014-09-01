package org.cf.simplify;

import java.util.Arrays;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class OptimizerTester {

    public static MethodBackedGraph getMethodBackedGraph(String className, String methodSignature) {
        BuilderClassDef classDef = VMTester.getBuilderClassDef(className);
        BuilderMethod method = VMTester.getBuilderMethod(className, methodSignature);
        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        DexBuilder dexBuilder = VMTester.getDexBuilder();
        String methodDescriptor = className + "->" + methodSignature;
        ContextGraph graph = vm.execute(methodDescriptor);

        return new MethodBackedGraph(graph, method, vm, dexBuilder);
    }

}
