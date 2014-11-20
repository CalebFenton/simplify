package org.cf.simplify;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.Map;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class OptimizerTester {

    public static MethodBackedGraph getMethodBackedGraph(String className, String methodSignature) {
        TIntObjectMap<Object> initial = new TIntObjectHashMap<Object>();

        return getMethodBackedGraph(className, methodSignature, initial);
    }

    public static MethodBackedGraph getMethodBackedGraph(String className, String methodSignature,
                    TIntObjectMap<Object> initial) {
        Map<String, Map<String, Object>> classNameToFieldValue = new HashMap<String, Map<String, Object>>();

        return getMethodBackedGraph(className, methodSignature, initial, classNameToFieldValue);
    }

    public static MethodBackedGraph getMethodBackedGraph(String className, String methodSignature,
                    TIntObjectMap<Object> initial, Map<String, Map<String, Object>> classNameToFieldValue) {
        VirtualMachine vm = VMTester.getTestVM();
        String methodDescriptor = className + "->" + methodSignature;
        BuilderMethod method = vm.getClassManager().getMethod(methodDescriptor);
        DexBuilder dexBuilder = VMTester.getDexBuilder();
        ExecutionGraph graph = VMTester.execute(vm, className, methodSignature, initial, classNameToFieldValue);

        return new MethodBackedGraph(graph, method, vm, dexBuilder);
    }

}
