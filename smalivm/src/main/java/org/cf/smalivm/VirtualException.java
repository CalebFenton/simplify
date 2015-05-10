package org.cf.smalivm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VirtualException {

    private final String exceptionClass;
    private StackTraceElement[] stackTrace;

    public VirtualException(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    String getExceptionClass() {
        return exceptionClass;
    }

    void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    StackTraceElement[] getStackTrace() {
        return stackTrace;
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
                        .append(getStackTrace(), rhs.getStackTrace()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(327, 53).append(getExceptionClass()).append(getStackTrace()).hashCode();
    }

    @Override
    public String toString() {
        return exceptionClass;
    }

}
