package org.cf.smalivm.exception;

public class UnknownAncestors extends Exception {

    private static final long serialVersionUID = -203186703320731132L;

    public UnknownAncestors(String className) {
        super("Unknown ancestory for class: " + className);
    }

}
