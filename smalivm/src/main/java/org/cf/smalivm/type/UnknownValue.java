package org.cf.smalivm.type;

public class UnknownValue extends Instance {

    public UnknownValue() {
        super(null);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object other) {
        // This is similar to NULL != NULL
        return false;
    }

    @Override
    public String toString() {
        return "*unknown*";
    }

}
