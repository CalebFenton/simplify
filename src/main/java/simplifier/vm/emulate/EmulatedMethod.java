package simplifier.vm.emulate;

import simplifier.vm.context.MethodContext;

public interface EmulatedMethod {

    public void execute(MethodContext mctx);

}
