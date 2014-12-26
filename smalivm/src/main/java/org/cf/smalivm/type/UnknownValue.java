package org.cf.smalivm.type;

public class UnknownValue implements LocalType {

    public UnknownValue() {

    }

    @Override
    public boolean equals(Object other) {
        // i.e. NULL != NULL
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return "*unknown*";
    }

}
