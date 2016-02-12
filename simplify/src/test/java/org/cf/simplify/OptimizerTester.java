package org.cf.simplify;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class OptimizerTester {

    public static ExecutionGraphManipulator getGraphManipulator(String className, String methodSignature,
                    Object... args) {
        VMState initial = new VMState();
        if (args.length > 0) {
            initial.setRegisters(args);
        }

        return getGraphManipulator(className, methodSignature, initial);
    }

    public static ExecutionGraphManipulator getGraphManipulator(String className, String methodSignature,
                    VMState initial) {
        // Force class reloading because implementations change (hopefully!)
        VirtualMachine vm = VMTester.spawnVM(true);
        String methodDescriptor = className + "->" + methodSignature;
        ExecutionGraph graph = VMTester.execute(vm, className, methodSignature, initial);

        BuilderMethod method = vm.getClassManager().getMethod(methodDescriptor);
        DexBuilder dexBuilder = VMTester.getDexBuilder();

        return new ExecutionGraphManipulator(graph, method, vm, dexBuilder);
    }

}
