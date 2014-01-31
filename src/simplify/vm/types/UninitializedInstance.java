package simplify.vm.types;

public class UninitializedInstance implements LocalType {

    private final String type;

    public UninitializedInstance(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
