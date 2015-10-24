package org.cf.demoapp;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodEmulator;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxExecutionTimeExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.exception.UnhandledVirtualException;

public class Main {

    private static final String SMALI_PATH = "resources/";

    private static VirtualMachine vm;

    public static void main(String[] args) throws Exception {
        VirtualMachineFactory vmFactory = new VirtualMachineFactory();
        vm = vmFactory.build(SMALI_PATH);

        // Hook println with our own implementation
        MethodEmulator.addMethod("Ljava/io/PrintStream;->println(Ljava/lang/String;)V",
                        java_io_PrintStream_println.class);

        // Execute particular method
        vm.execute("Lorg/cf/demosmali/Main;->main([Ljava/lang/String;)V");

        executePrintParameter(42);
        executeParameterLogicWithUnknownParameter();
        executeParameterLogicWithKnownParameter(10);
    }

    private static void executeParameterLogicWithKnownParameter(int parameterValue) throws MaxAddressVisitsExceeded,
                    MaxCallDepthExceeded, MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        String methodDescriptor = "Lorg/cf/demosmali/Main;->parameterLogic(I)I";

        ExecutionContext ectx = vm.spawnRootExecutionContext(methodDescriptor);
        MethodState mState = ectx.getMethodState();
        // This method has 4 locals (r0, r1, r2, r3). It's virtual so p0 (r4) is 'this' reference.
        // First method parameter starts at r5, or parameter start + 1.
        mState.assignParameter(mState.getParameterStart() + 1, parameterValue, "I");
        ExecutionGraph graph = vm.execute(methodDescriptor, ectx);

        HeapItem item = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
        System.out.println("With context, returns: " + item);
    }

    private static void executeParameterLogicWithUnknownParameter() throws MaxAddressVisitsExceeded,
                    MaxCallDepthExceeded, MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        String methodDescriptor = "Lorg/cf/demosmali/Main;->parameterLogic(I)I";

        // Execute with ambiguous / unknown parameters.
        // You should see two prints, indicating it took both execution paths.
        ExecutionGraph graph = vm.execute(methodDescriptor);

        // Get the return value consensus over all possible execution paths.
        // Because some values are not known, there are multiple execution paths.
        // These paths have different return values, so there's no consensus.
        HeapItem item = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
        System.out.println("With no context, returns: " + item);
    }

    private static void executePrintParameter(int parameterValue) throws MaxAddressVisitsExceeded,
                    MaxCallDepthExceeded, MaxMethodVisitsExceeded, UnhandledVirtualException, MaxExecutionTimeExceeded {
        String methodDescriptor = "Lorg/cf/demosmali/Main;->printParameter(I)V";

        // Execute method with some context
        ExecutionContext ectx = vm.spawnRootExecutionContext(methodDescriptor);
        MethodState mState = ectx.getMethodState();
        mState.assignParameter(3, parameterValue, "I");
        vm.execute(methodDescriptor, ectx);
    }

}
