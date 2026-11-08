package org.cf.sdbg.command

import org.cf.sdbg.DebugSession
import org.cf.smalivm.core.Debugger
import org.cf.smalivm.core.MethodView
import picocli.CommandLine.ParentCommand
import java.io.PrintWriter

/** A console subcommand; picocli injects [parent], which carries the session every command shares. */
internal abstract class DebuggerCommand : Runnable {
    @ParentCommand
    protected lateinit var parent: CliCommands

    protected val out: PrintWriter get() = parent.out
    protected val debugSession: DebugSession get() = parent.debugSession
    protected val debugger: Debugger get() = debugSession.debugger
    protected val currentView: MethodView
        get() = debugger.cursor?.view ?: debugger.lastNode?.view ?: debugSession.session.methodView

    protected fun printInvalid(reason: String) {
        out.println("invalid target; $reason")
    }

    protected fun printFinished() {
        out.println("execution finished: ${debugSession.session.completion()}")
        debugSession.session.outcomes.forEach { out.println(it) }
    }
}
