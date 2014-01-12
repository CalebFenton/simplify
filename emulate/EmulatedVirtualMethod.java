package simplify.emulate;

import simplify.exec.ExecutionContext;

public interface EmulatedVirtualMethod extends EmulatedMethod {
    public void execute(ExecutionContext ectx);
}
