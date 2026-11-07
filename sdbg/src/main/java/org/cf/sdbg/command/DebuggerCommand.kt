package org.cf.sdbg.command

import org.cf.sdbg.Main
import org.cf.smalivm.core.Debugger
import org.cf.smalivm.core.MethodView

abstract class DebuggerCommand : Runnable {
    protected val debugger: Debugger get() = Main.debugger
    protected val currentView: MethodView
        get() = debugger.cursor?.view ?: debugger.lastNode?.view ?: Main.session.methodView

    protected fun printFinished(parent: CliCommands) {
        parent.out.println("execution finished: ${Main.session.completion()}")
        Main.session.outcomes.forEach { parent.out.println(it) }
    }
}
