package org.cf.smalivm.exception;

import org.cf.smalivm.context.ExecutionNode;

public class MaxMethodVisitsExceeded extends VirtualMachineException {

    private static final long serialVersionUID = 7115169814850980339L;

    public MaxMethodVisitsExceeded(ExecutionNode node, String methodDescriptor) {
        this("Exceeded max method visits @" + node.getAddress() + " " + node + " in " + methodDescriptor);
    }

    private MaxMethodVisitsExceeded(String message) {
        super(message);
    }

}
