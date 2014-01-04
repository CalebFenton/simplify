package simplify.exec;

import java.util.logging.Logger;

import simplify.Simplifier;

public class ExecutionContext {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    public ExecutionContext() {
    }

    @Override
    public ExecutionContext clone() {
        return new ExecutionContext();
    }

    public static boolean equals(ExecutionContext ectx1, ExecutionContext ectx2) {
        log.warning("No ectx equals implemented!");
        return false;
    }

    public static ExecutionContext merge(ExecutionContext ectx1, ExecutionContext ectx2) {
        if (equals(ectx1, ectx2)) {
            return ectx1;
        }

        log.warning("Not actually merging contexts!!!");

        return ectx1;
    }
}
