package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.MethodContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class org_cf_simplify_Utils_breakpoint implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(org_cf_simplify_Utils_breakpoint.class.getSimpleName());

    public void execute(MethodContext mctx) {
        // To use, add invoke-static {}, Lsimplify/Utils;->breakpoint()V, and set a breakpoint here.
        log.info("For a good time, set a breakpoint here!");
    }

    public SideEffect.Type getSideEffectType() {
        // No side effect. This will be optimized away.
        return SideEffect.Type.NONE;
    }
}
