package org.cf.sdbg

import org.cf.smalivm.core.Debugger
import org.cf.smalivm.core.ExecutionSession
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.VmServices

/** One debugged execution of [method]: its session, the debugger driving it, and a listing of the same program. */
internal class DebugSession(val services: VmServices, method: MethodView) {
    val session: ExecutionSession = services.newSession(method)
    val debugger: Debugger = Debugger(session)
    val listing: SmaliListing = SmaliListing(services)
}
