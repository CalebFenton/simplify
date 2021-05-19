package org.cf.smalivm.emulate

import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.ExecutionState
import org.cf.smalivm.UnresolvedChild
import org.cf.smalivm.VirtualMachine
import org.slf4j.LoggerFactory

internal class org_cf_simplify_Utils_breakpoint : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine): Array<out UnresolvedChild> {
        // Add invoke-static {}, Lorg/cf/simplify/Utils;->breakpoint()V, and set an IDE breakpoint here.
        log.trace("Set a breakpoint here!")
        return finishMethod()
    }

    companion object {
        private val log = LoggerFactory.getLogger(org_cf_simplify_Utils_breakpoint::class.java.simpleName)
    }
}
