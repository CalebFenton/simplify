package org.cf.smalivm.emulate;

import java.util.logging.Logger;

import org.cf.smalivm.context.MethodContext;

public class org_cf_simplify_Utils_breakpoint implements EmulatedMethod {

    private static final Logger log = Logger.getLogger(org_cf_simplify_Utils_breakpoint.class.getSimpleName());

    public void execute(MethodContext mctx, int[] parameterRegisters) {
        // To use, add invoke-static {}, Lsimplify/Utils;->breakpoint()V, and set a breakpoint here.
        log.info("For a good time, set a breakpoint here!");
    }

}
