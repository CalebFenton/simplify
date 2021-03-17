package org.cf.smalivm;

import org.apache.commons.collections.functors.ExceptionFactory;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionGraphImpl;
import org.cf.smalivm.dex.SmaliClassLoader;
import org.cf.smalivm.exception.VirtualMachineException;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.smalivm.type.VirtualType;

import javax.annotation.Nullable;

public interface VirtualMachine {
    ExecutionGraph execute(String methodSignature) throws VirtualMachineException;

    ExecutionGraph execute(String className, String methodDescriptor) throws VirtualMachineException;

    ExecutionGraphImpl execute(VirtualMethod virtualMethod) throws VirtualMachineException;

    ExecutionGraphImpl execute(ExecutionContext context) throws VirtualMachineException;

    ExecutionGraphImpl execute(ExecutionContext calleeContext, ExecutionContext callerContext, int[] parameterRegisters) throws VirtualMachineException;

    MethodExecutor startDebug(ExecutionContext calleeContext, ExecutionContext callerContext);

    ExecutionGraph finishDebug(MethodExecutor methodExecutor, ExecutionContext callerContext, int[] parameterRegisters);

    SmaliClassLoader getClassLoader();

    ClassManager getClassManager();

    Configuration getConfiguration();

    StaticFieldAccessor getStaticFieldAccessor();

    boolean isSafe(VirtualType virtualClass);

    ExecutionGraph spawnInstructionGraph(String className, String methodDescriptor);

    ExecutionGraphImpl spawnInstructionGraph(VirtualMethod method);

    ExecutionContext spawnRootContext(String methodSignature);

    ExecutionContext spawnRootContext(String className, String methodDescriptor);

    ExecutionContext spawnRootContext(VirtualMethod method);

    ExecutionContext spawnRootContext(VirtualMethod method, @Nullable ExecutionContext callerContext, int callerAddress);

    void updateInstructionGraph(VirtualMethod method);

    MethodExecutorFactory getMethodExecutorFactory();

    ExceptionFactory getExceptionFactory();
}
