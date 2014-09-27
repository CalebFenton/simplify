package org.cf.smalivm.exception;

import org.cf.smalivm.context.ExecutionNode;

public class MaxNodeVisitsExceeded extends Exception {

    private static final long serialVersionUID = 7115169814850980339L;

    public MaxNodeVisitsExceeded(ExecutionNode currentNode, String methodDescriptor) {
        this("Exceeded max node visits for " + currentNode + " in " + methodDescriptor);
    }

    private MaxNodeVisitsExceeded(String message) {
        super(message);
    }

}
