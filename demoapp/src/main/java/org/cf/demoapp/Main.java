package org.cf.demoapp;

import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodEmulator;

public class Main {

    private static final String SmaliPath = "resources/";

    private static VirtualMachine vm;

    public static void main(String[] args) throws Exception {
        // The class manager is responsible for loading Smali files.
        SmaliClassManager classManager = new SmaliClassManager(SmaliPath);
        vm = new VirtualMachine(classManager);

        // Hook println with our own implementation
        MethodEmulator.addMethod("Ljava/io/PrintStream;->println(Ljava/lang/String;)V",
                        new java_io_PrintStream_println());

        // Execute particular method
        vm.execute("Lorg/cf/demosmali/Main;->main([Ljava/lang/String;)V");

        executePrintParameter(42);
        executeParameterLogic(10);
    }

    private static void executeParameterLogic(int parameterValue) {
        String methodDescriptor = "Lorg/cf/demosmali/Main;->parameterLogic(I)I";

        // Execute with ambiguous / unknown parameters.
        // You should see two prints, indicating it took both execution paths.
        ExecutionGraph graph = vm.execute(methodDescriptor);

        // Get the return value for all possible executions.
        Object value = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
        System.out.println("With no context, returns: " + value);

        // Now execute with parameter
        ExecutionContext ectx = vm.getRootExecutionContext(methodDescriptor);
        MethodState mState = ectx.getMethodState();

        // Since this method is not static, the first parameter is a reference to 'this'.
        // So the integer parameter goes in index 1.
        mState.assignParameter(1, parameterValue);
        graph = vm.execute(methodDescriptor, ectx);

        value = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
        System.out.println("With context, returns: " + value);
    }

    private static void executePrintParameter(int parameterValue) {
        String methodDescriptor = "Lorg/cf/demosmali/Main;->printParameter(I)V";
        ExecutionContext ectx = vm.getRootExecutionContext(methodDescriptor);
        MethodState mState = ectx.getMethodState();
        mState.assignParameter(0, parameterValue);

        // Execute method with some context
        vm.execute(methodDescriptor, ectx);
    }

}
