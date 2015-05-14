package org.cf.smalivm.emulate;

import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class org_cf_simplify_Utils_breakpoint implements MethodStateMethod {

    private static final Logger log = LoggerFactory.getLogger(org_cf_simplify_Utils_breakpoint.class.getSimpleName());

    private final Set<VirtualException> exceptions;

    org_cf_simplify_Utils_breakpoint() {
        exceptions = new HashSet<VirtualException>();
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    @Override
    public void execute(VirtualMachine vm, MethodState mState) {
        // To use, add invoke-static {}, Lorg/cf/simplify/Utils;->breakpoint()V, and set a breakpoint here.
        log.trace("For a good time, set a breakpoint here!");
    }

    @Override
    public SideEffect.Level getSideEffectLevel() {
        // No side effect. This will be optimized away.
        return SideEffect.Level.NONE;
    }
}
