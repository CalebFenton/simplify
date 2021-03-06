package org.cf.simplify;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraphImpl;
import org.cf.smalivm.type.VirtualMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class OptimizerTester {

    public static ExecutionGraphManipulator getGraphManipulator(String className, String methodSignature, Object... args) {
        VMState initial = new VMState();
        if (args.length > 0) {
            initial.setRegisters(args);
        }

        return getGraphManipulator(className, methodSignature, initial);
    }

    public static ExecutionGraphManipulator getGraphManipulator(String className, String methodDescriptor, VMState initial) {
        // Force reloading of classes since implementations in class definitions may have changed
        VirtualMachine vm = VMTester.spawnVM(true);

        return getGraphManipulator(vm, className, methodDescriptor, initial);
    }

    public static ExecutionGraphManipulator getGraphManipulator(VirtualMachine vm, String className,
                                                                String methodDescriptor, VMState initial) {
        ExecutionGraphImpl graph = VMTester.execute(vm, className, methodDescriptor, initial);

        String methodSignature = className + "->" + methodDescriptor;
        VirtualMethod method = vm.getClassManager().getMethod(methodSignature);
        DexBuilder dexBuilder = VMTester.getDexBuilder();

        return new ExecutionGraphManipulator(graph, method, vm, dexBuilder);
    }

}
