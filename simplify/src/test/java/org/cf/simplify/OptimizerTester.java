package org.cf.simplify;

import org.cf.smalivm.ExecutionGraph;
import org.cf.smalivm.TestState;
import org.cf.smalivm.Tester;
import org.cf.smalivm.VirtualMachine;

public class OptimizerTester {

    public static ExecutionGraph getGraphManipulator(String className, String methodSignature, Object... args) {
        TestState initial = new TestState();
        if (args.length > 0) {
            initial.setRegisters(args);
        }

        return getGraphManipulator(className, methodSignature, initial);
    }

    public static ExecutionGraph getGraphManipulator(String className, String methodDescriptor, TestState initial) {
        // Force reloading of classes since implementations in class definitions may have changed
        VirtualMachine vm = Tester.spawnVM(true);

        return getGraphManipulator(vm, className, methodDescriptor, initial);
    }

    public static ExecutionGraph getGraphManipulator(VirtualMachine vm, String className, String methodDescriptor, TestState initial) {
        return Tester.execute(className, methodDescriptor, initial, vm);

//        String methodSignature = className + "->" + methodDescriptor;
//        VirtualMethod method = vm.getClassManager().getMethod(methodSignature);
//        DexBuilder dexBuilder = Tester.getDexBuilder();
//
//        return new ExecutionGraphManipulator(graph, method, vm, dexBuilder);
    }

}
