package simplify.vm.handlers;

import simplify.vm.MethodContext;

public abstract class OpHandler {

    public abstract int[] execute(MethodContext mctx);

    @Override
    public abstract String toString();

    public abstract int[] getPossibleChildren();

    public abstract int getAddress();
}
