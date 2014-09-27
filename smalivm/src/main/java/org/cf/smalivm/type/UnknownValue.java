package org.cf.smalivm.type;

public class UnknownValue implements Type {

    private String smaliType;

    public UnknownValue(String smaliType) {
        this.smaliType = smaliType;
    }

    @Override
    public String toString() {
        return "*unknown*, smaliType=" + smaliType;
    }

    public String getType() {
        return smaliType;
    }

    // TODO: After removing UnimplementedOp, remove this method and make smaliType final.
    // Level only need to change because sometimes they're "?" which can cause problems of parameter width calculation.
    public void setType(String smaliType) {
        this.smaliType = smaliType;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }

}
