package simplify.vm.emulate;

import simplify.vm.context.MethodContext;

public interface EmulatedMethod {

    public void execute(MethodContext mctx);

}
