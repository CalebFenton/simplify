package org.cf.smalivm.emulate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cf.smalivm.context.MethodContext;

public class org_cf_simplify_Utils_breakpoint implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(org_cf_simplify_Utils_breakpoint.class.getSimpleName());

    public void execute(MethodContext mctx) {
        // To use, add invoke-static {}, Lsimplify/Utils;->breakpoint()V, and set a breakpoint here.
        log.info("For a good time, set a breakpoint here!");
    }

}
