package org.cf.smalivm.exception;

import org.cf.smalivm.VirtualException;

public class UnhandledVirtualException extends VirtualMachineException {

    private static final long serialVersionUID = 7055493085764845808L;

    private final VirtualException exception;

    public UnhandledVirtualException(VirtualException exception) {
        super(exception.toString());
        this.exception = exception;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Unhandled virtual exception: ").append(exception);

        return sb.toString();
    }

}
