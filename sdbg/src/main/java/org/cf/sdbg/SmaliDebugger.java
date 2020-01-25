package org.cf.sdbg;

import org.cf.smalivm.MethodExecutor;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualMethod;

import java.io.IOException;

public class Debugger {
    private static VirtualMachine vm;
    private static MethodExecutor executor;

    Debugger(String methodSignature) throws IOException {
        VirtualMachineFactory vmFactory = new VirtualMachineFactory();
        vm = vmFactory.build("resources/");
        vm.getMethodExecutorFactory().setInteractive();
    }

    public void start(String methodSignature) {
        ClassManager classManager = vm.getClassManager();
        VirtualMethod virtualMethod = classManager.getMethod(methodSignature);
        executor = vm.getMethodExecutorFactory().build(virtualMethod);
    }

    public void stop() {
        executor = null;
    }

    public ExecutionNode step() throws UnhandledVirtualException {
        // TODO: check finished! node could be null
        ExecutionNode node = executor.step();
    }

    public boolean isRunning() {
        return executor != null;
    }
}
