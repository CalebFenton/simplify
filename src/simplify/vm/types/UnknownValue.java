package simplify.vm.types;

public class UnknownValue implements Type {

    private final String smaliType;

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

    @Override
    public boolean equals(Object other) {
        return false;
    }

}
