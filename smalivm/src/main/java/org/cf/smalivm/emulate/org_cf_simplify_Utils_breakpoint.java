package org.cf.smalivm.emulate;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class org_cf_simplify_Utils_breakpoint extends MethodStateMethod {

    private static final Logger log = LoggerFactory.getLogger(org_cf_simplify_Utils_breakpoint.class.getSimpleName());

    @Override
    protected void execute(VirtualMachine vm, Op op, MethodState mState) {
        // To use, add invoke-static {}, Lorg/cf/simplify/Utils;->breakpoint()V, and set a breakpoint here.
        log.trace("For a good time, set a breakpoint here!");
    }

}
