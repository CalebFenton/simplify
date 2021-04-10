package org.cf.smalivm.exception;

public class MaxAddressVisitsExceededException extends VirtualMachineException {

    private static final long serialVersionUID = 7115169814850980339L;

    public MaxAddressVisitsExceededException(String methodSignature, int address) {
        super("Exceeded max address visits in " + methodSignature + " @" + address);
    }
}
