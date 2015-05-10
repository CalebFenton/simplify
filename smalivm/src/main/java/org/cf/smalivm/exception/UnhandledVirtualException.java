package org.cf.smalivm.exception;

import org.cf.smalivm.VirtualException;

public final class UnhandledVirtualException extends Exception {

    private static final long serialVersionUID = 5225112348847465697L;

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
