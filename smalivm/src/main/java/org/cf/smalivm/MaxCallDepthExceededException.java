package org.cf.smalivm;

public class MaxCallDepthExceededException extends VirtualMachineException {

    private static final long serialVersionUID = -9148536615221065528L;

    public MaxCallDepthExceededException(String methodDescriptor) {
        super("Exceeded max call depth in " + methodDescriptor);
    }

}
