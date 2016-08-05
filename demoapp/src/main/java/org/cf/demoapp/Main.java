package org.cf.demoapp;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineException;
import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodEmulator;

public class Main {

    private static final String SMALI_PATH = "resources/";

    private static VirtualMachine vm;

    public static void main(String[] args) throws Exception {
        // The SMALI_PATH directory is populated using dumpSmali.sh and contains converted code from org.cf.demosmali.
        // You can use a link to a DEX, APK, or directory with Smali files.
        VirtualMachineFactory vmFactory = new VirtualMachineFactory();
        vm = vmFactory.build(SMALI_PATH);

        // The smali code contains System.out.println()s used for debugging, but smalivm won't execute this method
        // because it's not white listed because it affects state outside of the VM. Also, smalivm doesn't have a
        // "screen" to send output. This hooks those method calls with our own implementation to actually print stuff.
        MethodEmulator
                .addMethod("Ljava/io/PrintStream;->println(Ljava/lang/String;)V", java_io_PrintStream_println.class);

        // Execute demo smali's main() method
        vm.execute("Lorg/cf/demosmali/Main;->main([Ljava/lang/String;)V");

        executePrintParameter(42);
        executeParameterLogicWithUnknownParameter();
        executeParameterLogicWithKnownParameter(10);
    }

    private static void executeParameterLogicWithKnownParameter(int parameterValue) throws VirtualMachineException {
        String methodSignature = "Lorg/cf/demosmali/Main;->parameterLogic(I)I";
        ExecutionContext context = vm.spawnRootContext(methodSignature);
        MethodState mState = context.getMethodState();

        // This method has 4 locals (r0, r1, r2, r3). Since it's virtual (not static) the first parameter (p0 or r4)
        // is the 'this' reference, or contains a reference to the object.
        // First method parameter starts at r5, or parameter start + 1.
        mState.assignParameter(mState.getParameterStart() + 1, parameterValue, "I");
        ExecutionGraph graph = vm.execute(methodSignature, context);

        HeapItem item = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
        System.out.println("With context, returns " + parameterValue + ": " + item);
    }

    private static void executeParameterLogicWithUnknownParameter() throws VirtualMachineException {
        String methodSignature = "Lorg/cf/demosmali/Main;->parameterLogic(I)I";

        // Execute with ambiguous / unknown parameters. This forces smalivm to take both execution paths.
        // You should see two prints, indicating it took both execution paths.
        ExecutionGraph graph = vm.execute(methodSignature);

        // Get the return value consensus over all possible execution paths.
        // These paths have different return values, so there's no consensus.
        HeapItem item = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
        System.out.println("With no context, returns an unknown integer: " + item);
    }

    private static void executePrintParameter(int parameterValue) throws VirtualMachineException {
        String className = "Lorg/cf/demosmali/Main;";
        String methodDescriptor = "printParameter(I)V";

        // Execute method with context which contains the argument values.
        ExecutionContext context = vm.spawnRootContext(className, methodDescriptor);
        MethodState mState = context.getMethodState();
        mState.assignParameter(3, parameterValue, "I");
        vm.execute(className, methodDescriptor, context);
    }

}
