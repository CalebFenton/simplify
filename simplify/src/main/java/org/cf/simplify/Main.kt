package org.cf.simplify

import org.apache.commons.cli.ParseException
import java.io.PrintStream
import kotlin.system.exitProcess

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val status = run(args, Launcher(), System.err)
        if (status != 0) exitProcess(status)
    }

    internal fun run(args: Array<String>, launcher: Launcher, error: PrintStream): Int {
        val options = try {
            SimplifyOptionsParser.parse(args)
        } catch (exception: ParseException) {
            error.println("Error: ${exception.message}")
            Launcher.usage(SimplifyOptionsParser.options, error)
            return 1
        }
        val result = launcher.run(options)
        return if (result.failedMethodCount == 0 || options.ignoreErrors) 0 else 1
    }
}
