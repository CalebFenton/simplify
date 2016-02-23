package org.cf.simplify;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.reference.LocalMethod;
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

    public static ExecutionGraphManipulator getGraphManipulator(String className, String methodDescriptor,
                    VMState initial) {
        // Force class reloading because implementations change (hopefully!)
        VirtualMachine vm = VMTester.spawnVM(true);
        ExecutionGraph graph = VMTester.execute(vm, className, methodDescriptor, initial);

        String methodSignature = className + "->" + methodDescriptor;
        LocalMethod localMethod = vm.getClassManager().getMethod(methodSignature);
        DexBuilder dexBuilder = VMTester.getDexBuilder();

        return new ExecutionGraphManipulator(graph, localMethod, vm, dexBuilder);
    }

}
