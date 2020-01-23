package org.cf.smalivm.exception;

public abstract class VirtualMachineException extends Exception {

    private static final long serialVersionUID = 367467528566284839L;

    protected VirtualMachineException(String message) {
        super(message);
    }

}
