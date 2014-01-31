package simplify.vm.types;

public class UnknownValue implements LocalType {

    private final String type;

    public UnknownValue(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "*unknown value*, type=" + type;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }
}
