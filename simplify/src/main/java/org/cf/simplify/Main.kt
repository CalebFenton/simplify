package org.cf.simplify

import org.apache.commons.cli.ParseException
import kotlin.system.exitProcess

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            Launcher().run(args)
        } catch (exception: ParseException) {
            System.err.println("Error: ${exception.message}")
            Launcher.usage(SimplifyOptionsParser.options)
            exitProcess(1)
        }
    }
}
