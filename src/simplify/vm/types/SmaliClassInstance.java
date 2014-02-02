package simplify.vm.types;

public class SmaliClassInstance implements LocalType {

    private final String type;

    public SmaliClassInstance(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

}
