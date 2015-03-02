package org.cf.smalivm;


public class VirtualException extends Exception {

    private static final long serialVersionUID = -3174287361309231608L;

    private Exception exception;

    VirtualException(Exception exception) {
        this.exception = exception;
    }

    Exception getException() {
        return exception;
    }

}
