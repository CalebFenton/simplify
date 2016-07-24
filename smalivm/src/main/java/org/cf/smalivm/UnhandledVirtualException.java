package org.cf.smalivm;

public class UnhandledVirtualException extends VirtualMachineException {

    private static final long serialVersionUID = 7055493085764845808L;

    private final Throwable exception;

    public UnhandledVirtualException(Throwable exception) {
        super(exception.getMessage());
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "Unhandled virtual exception: " + exception;
    }

}
