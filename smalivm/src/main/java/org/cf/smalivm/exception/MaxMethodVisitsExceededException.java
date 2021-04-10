package org.cf.smalivm.exception;

public class MaxMethodVisitsExceededException extends VirtualMachineException {

    private static final long serialVersionUID = 2782400703961403297L;

    public MaxMethodVisitsExceededException(String methodSignature, int address) {
        super("Exceeded max method visits in " + methodSignature + " @" + address);
    }
}
