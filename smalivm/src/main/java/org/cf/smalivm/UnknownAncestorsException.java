package org.cf.smalivm;

public class UnknownAncestorsException extends VirtualMachineException {

    private static final long serialVersionUID = -203186703320731132L;

    public UnknownAncestorsException(String className) {
        super("Unknown ancestry for class: " + className);
    }

}
