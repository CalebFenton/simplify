package org.cf.simplify;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.context.MethodContext;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class OptimizerTester {

    public static MethodBackedGraph getMethodBackedGraph(String className, String methodSignature) {
        TIntObjectMap<Object> initial = new TIntObjectHashMap<Object>();

        return getMethodBackedGraph(className, methodSignature, initial);
    }

    public static MethodBackedGraph getMethodBackedGraph(String className, String methodSignature,
                    TIntObjectMap<Object> initial) {
        BuilderClassDef classDef = VMTester.getBuilderClassDef(className);
        BuilderMethod method = VMTester.getBuilderMethod(className, methodSignature);
        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        DexBuilder dexBuilder = VMTester.getDexBuilder();
        String methodDescriptor = className + "->" + methodSignature;
        MethodContext ctx = MethodContext.build(initial);
        ContextGraph graph = vm.execute(methodDescriptor, ctx);

        return new MethodBackedGraph(graph, method, vm, dexBuilder);
    }

}
