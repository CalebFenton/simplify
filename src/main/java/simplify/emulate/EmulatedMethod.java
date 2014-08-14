package simplify.emulate;

import simplify.vm.MethodContext;

public interface EmulatedMethod {

    public void execute(MethodContext mctx);

}
