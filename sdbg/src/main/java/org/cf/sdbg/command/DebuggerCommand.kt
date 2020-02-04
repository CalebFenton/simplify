package org.cf.sdbg.command

import org.cf.sdbg.Main
import org.cf.smalivm.debug.Debugger

open class DebuggerCommand : Runnable {
    protected val debugger: Debugger by lazy { Main.debugger }

    override fun run() {
        TODO("not implemented; that's a 50 DKP MINUS!")
    }

}
