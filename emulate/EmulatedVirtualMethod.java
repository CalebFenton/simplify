package simplify.emulate;

import simplify.exec.MethodExecutionContext;

public interface EmulatedVirtualMethod extends EmulatedMethod {
    public void execute(MethodExecutionContext ectx);
}
