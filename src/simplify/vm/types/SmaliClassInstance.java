package simplify.vm.types;

import simplify.vm.InstanceContext;

public class SmaliClassInstance implements LocalType {

    private final String type;

    private final InstanceContext ctx;

    public SmaliClassInstance(String type) {
        this.type = type;
        this.ctx = new InstanceContext();
    }

    public String getType() {
        return type;
    }

    public InstanceContext getContext() {
        // isInitialized -> ctx != null ?
        return ctx;
    }

    @Override
    public String toString() {
        return type;
    }

}
