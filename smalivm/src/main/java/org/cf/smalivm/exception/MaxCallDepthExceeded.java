package org.cf.smalivm.exception;

public class MaxCallDepthExceeded extends VirtualMachineException {

    private static final long serialVersionUID = -9148536615221065528L;

    public MaxCallDepthExceeded(String methodDescriptor) {
        super("Exceeded max call depth in " + methodDescriptor);
    }

}
