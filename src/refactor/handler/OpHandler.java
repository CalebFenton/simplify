package refactor.handler;

import refactor.vm.MethodContext;

public abstract class OpHandler {

    public abstract int[] execute(MethodContext mectx);

    @Override
    public abstract String toString();

    public abstract int[] getPossibleChildren();

    public abstract int getIndex();
}
