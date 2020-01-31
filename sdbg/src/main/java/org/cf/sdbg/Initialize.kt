package org.cf.sdbg

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.cf.smalivm.Debugger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.File
import java.util.concurrent.Callable

@CommandLine.Command(mixinStandardHelpOptions = true, version = [Main.version],
        description = ["Smali DeBuGger (SDBG)"])
internal class Initialize : Callable<Int> {
    @CommandLine.Parameters(index = "0", paramLabel = "input-path", arity = "1",
            description = ["Input directory containing Smali"])
    private lateinit var inputFile: File
    @CommandLine.Parameters(index = "1", paramLabel = "method-signature", arity = "1",
            description = ["Method signature to execute"])
    private lateinit var methodSignature: String

    @CommandLine.Option(names = ["-v", "--verbose"],
            description = ["Specify multiple -v options to increase verbosity.", "For example, `-v -v -v` or `-vvv`"])
    private var verbosity = BooleanArray(0)

    private fun setLogLevel(verbosity: Int) {
        val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        when (verbosity) {
            0 -> { rootLogger.level = Level.OFF }
            1 -> { rootLogger.level = Level.INFO }
            2 -> { rootLogger.level = Level.DEBUG }
            3 -> { rootLogger.level = Level.TRACE }
        }
    }

    override fun call(): Int {
        setLogLevel(verbosity.size)
        println("Starting debugger for $methodSignature from $inputFile")
        Main.debugger = Debugger(inputFile, methodSignature)
        return 1
    }
}
