package org.cf.smalivm.exception;

import org.cf.smalivm.context.ExecutionNode;

public class MaxNodeVisitsExceeded extends Exception {

    private static final long serialVersionUID = 7115169814850980339L;

    public MaxNodeVisitsExceeded(ExecutionNode node, String methodDescriptor) {
        this("Exceeded max node visits @" + node.getAddress() + " " + node + " in " + methodDescriptor);
    }

    private MaxNodeVisitsExceeded(String message) {
        super(message);
    }

}
