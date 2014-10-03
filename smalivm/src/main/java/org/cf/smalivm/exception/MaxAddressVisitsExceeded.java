package org.cf.smalivm.exception;

import org.cf.smalivm.context.ExecutionNode;

public class MaxAddressVisitsExceeded extends Exception {

    private static final long serialVersionUID = 7115169814850980339L;

    public MaxAddressVisitsExceeded(ExecutionNode node, String methodDescriptor) {
        this("Exceeded max address visits @" + node.getAddress() + " " + node + " in " + methodDescriptor);
    }

    private MaxAddressVisitsExceeded(String message) {
        super(message);
    }

}
