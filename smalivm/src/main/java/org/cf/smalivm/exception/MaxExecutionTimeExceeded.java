package org.cf.smalivm.exception;

public class MaxExecutionTimeExceeded extends VirtualMachineException {

    private static final long serialVersionUID = -9148536615221065528L;

    public MaxExecutionTimeExceeded(String methodDescriptor) {
        super("Exceeded execution time in " + methodDescriptor);
    }

}
