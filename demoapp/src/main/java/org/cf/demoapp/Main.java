package org.cf.demoapp;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.emulate.MethodEmulator;

public class Main {

    private static final String SmaliPath = "resources/";

    private static VirtualMachine vm;

    public static void main(String[] args) throws Exception {
        // Load Smali files at SmaliPath into virtual machine
        vm = new VirtualMachine(SmaliPath);

        // Hook println with our own implementation
        MethodEmulator.addMethod("Ljava/io/PrintStream;->println(Ljava/lang/String;)V",
                        new java_io_PrintStream_println());

        // Execute particular method
        vm.execute("Lorg/cf/demosmali/Main;->main([Ljava/lang/String;)V");

        executePrintParameter(42);
        executeParameterLogic(10);
    }

    private static void executePrintParameter(int parameterValue) {
        String methodDescriptor = "Lorg/cf/demosmali/Main;->printParameter(I)V";
        ContextGraph graph = vm.getInstructionGraphClone(methodDescriptor);
        MethodContext mctx = graph.getRootContext();
        mctx.assignParameter(0, parameterValue);

        // Execute method with some context
        vm.execute("Lorg/cf/demosmali/Main;->printParameter(I)V", mctx);
    }

    private static void executeParameterLogic(int parameterValue) {
        String methodDescriptor = "Lorg/cf/demosmali/Main;->parameterLogic(I)I";

        // Execute with ambigious / uknown parameters.
        // You should see two prints, indicating it took both execution paths.
        ContextGraph graph = vm.execute(methodDescriptor);

        // Get the return value for all possible executions.
        Object value = graph.getTerminatingRegisterConsensus(MethodContext.ReturnRegister);
        System.out.println("With no context, returns: " + value);

        // Now execute with parameter
        graph = vm.getInstructionGraphClone(methodDescriptor);
        MethodContext mctx = graph.getRootContext();

        // Since this method is not static, the first parameter is a reference to 'this'.
        // So the integer parameter goes in index 1.
        mctx.assignParameter(1, parameterValue);
        graph = vm.execute(methodDescriptor, mctx);

        value = graph.getTerminatingRegisterConsensus(MethodContext.ReturnRegister);
        System.out.println("With context, returns: " + value);
    }

}
