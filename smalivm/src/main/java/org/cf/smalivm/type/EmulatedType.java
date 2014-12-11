package org.cf.smalivm.type;

public class EmulatedType implements LocalType {

    private String extra;
    private String smaliType;

    public EmulatedType(String smaliType) {
        this.smaliType = smaliType;
    }

    @Override
    public String getName() {
        return smaliType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(", extra=").append(extra);

        return sb.toString();
    }

}
