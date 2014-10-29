package org.cf.smalivm.type;

public class EmulatedType implements Type {

    private String extra;
    private String smaliType;

    public EmulatedType(String smaliType) {
        this.smaliType = smaliType;
    }

    @Override
    public String getType() {
        return smaliType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

}
