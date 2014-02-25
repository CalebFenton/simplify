package simplify.vm.handlers;

import simplify.vm.MethodContext;

public abstract class OpHandler {

    private final int address;
    private final String opName;
    private final int[] childAddresses;

    OpHandler(int address, String opName, int childAddress) {
        this(address, opName, new int[] { childAddress });
    }

    OpHandler(int address, String opName, int[] childAddresses) {
        this.address = address;
        this.opName = opName;
        this.childAddresses = childAddresses;
    }

    public abstract int[] execute(MethodContext mctx);

    @Override
    public abstract String toString();

    public final int getAddress() {
        return address;
    }

    public final String getOpName() {
        return opName;
    }

    public final int[] getPossibleChildren() {
        return childAddresses;
    }

}
