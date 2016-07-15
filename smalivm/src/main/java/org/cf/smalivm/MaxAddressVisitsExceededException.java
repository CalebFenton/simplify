package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionNode;

public class MaxAddressVisitsExceededException extends VirtualMachineException {

    private static final long serialVersionUID = 7115169814850980339L;

    public MaxAddressVisitsExceededException(ExecutionNode node, String methodDescriptor) {
        super("Exceeded max address visits @" + node.getAddress() + " " + node + " in " + methodDescriptor);
    }

}
