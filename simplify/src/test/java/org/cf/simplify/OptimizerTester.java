package org.cf.simplify;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.Map;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class OptimizerTester {

    public static ExecutionGraphManipulator getGraphManipulator(String className, String methodSignature) {
        TIntObjectMap<HeapItem> initial = new TIntObjectHashMap<HeapItem>();

        return getGraphManipulator(className, methodSignature, initial);
    }

    public static ExecutionGraphManipulator getGraphManipulator(String className, String methodSignature,
                    TIntObjectMap<HeapItem> initial) {
        Map<String, Map<String, HeapItem>> classNameToFieldValue = new HashMap<String, Map<String, HeapItem>>();

        return getGraphManipulator(className, methodSignature, initial, classNameToFieldValue);
    }

    public static ExecutionGraphManipulator getGraphManipulator(String className, String methodSignature,
                    TIntObjectMap<HeapItem> initial, Map<String, Map<String, HeapItem>> classNameToFieldValue) {
        // Force class reloading because implementations will have changed
        VirtualMachine vm = VMTester.getTestVM(true);
        String methodDescriptor = className + "->" + methodSignature;
        ExecutionGraph graph = VMTester.execute(vm, className, methodSignature, initial, classNameToFieldValue);

        BuilderMethod method = vm.getClassManager().getMethod(methodDescriptor);
        DexBuilder dexBuilder = VMTester.getDexBuilder();

        return new ExecutionGraphManipulator(graph, method, vm, dexBuilder);
    }

}
