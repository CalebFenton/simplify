package org.cf.smalivm.exception;

public class MaxExecutionTimeExceededException extends VirtualMachineException {

    private static final long serialVersionUID = -7500776521505568601L;

    public MaxExecutionTimeExceededException(String methodDescriptor) {
        super("Exceeded execution time in " + methodDescriptor);
    }

}
