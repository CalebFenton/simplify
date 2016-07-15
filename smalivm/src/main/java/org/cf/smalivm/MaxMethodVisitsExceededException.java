package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionNode;

public class MaxMethodVisitsExceededException extends VirtualMachineException {

    private static final long serialVersionUID = 2782400703961403297L;

    public MaxMethodVisitsExceededException(ExecutionNode node, String methodDescriptor) {
        this("Exceeded max method visits @" + node.getAddress() + " " + node + " in " + methodDescriptor);
    }

    private MaxMethodVisitsExceededException(String message) {
        super(message);
    }

}
