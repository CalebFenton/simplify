package org.cf.smalivm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cf.util.SmaliClassUtils;

public class VirtualException {

    private final String exceptionClass;
    private StackTraceElement[] stackTrace;
    private String message;

    public VirtualException(String exceptionClass) {
        this(exceptionClass, "");
    }

    public VirtualException(Class<?> exceptionClass) {
        this(SmaliClassUtils.javaClassToSmali(exceptionClass), "");
    }

    public VirtualException(Class<?> exceptionClass, String message) {
        this(SmaliClassUtils.javaClassToSmali(exceptionClass), message);
    }

    public VirtualException(String exceptionClass, String message) {
        this.exceptionClass = exceptionClass;
        this.message = message;
    }

    public <T extends Exception> VirtualException(T exception) {
        this(exception.getClass(), exception.getMessage());
    }

    String getExceptionClass() {
        return exceptionClass;
    }

    void setMessage(String message) {
        this.message = message;
    }

    void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        VirtualException rhs = (VirtualException) obj;

        return new EqualsBuilder().append(getExceptionClass(), rhs.getExceptionClass())
                        .append(getStackTrace(), rhs.getStackTrace()).append(getMessage(), rhs.getMessage()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(327, 53).append(getExceptionClass()).append(getStackTrace()).append(getMessage())
                        .hashCode();
    }

    @Override
    public String toString() {
        return exceptionClass;
    }

}
